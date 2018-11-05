package com.zhuanche.serv.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.DriverDailyReportExService;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.MyRestTemplate;
import mapper.mdbcarmanage.ex.DriverDailyReportExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Service
public class DriverDailyReportExServiceImpl implements DriverDailyReportExService {

    private static final Logger logger = LoggerFactory.getLogger(DriverDailyReportExServiceImpl.class);

    @Autowired
    private DriverDailyReportExMapper driverDailyReportExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    @Qualifier("busOrderCostTemplate")
    private MyRestTemplate busOrderCostTemplate;


    @Override
    public PageInfo<DriverDailyReport> findDayDriverDailyReportByparam(DriverDailyReportParams params) {
        logger.info("查询工作报告，日报，参数为："+(params==null?"null": JSON.toJSONString(params)));
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        List<DriverDailyReport> list  = this.driverDailyReportExMapper.queryForListObject(params);
        PageInfo<DriverDailyReport> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public PageInfo<DriverDailyReport> findWeekDriverDailyReportByparam(DriverDailyReportParams params,String statDateStart,  String statDateEnd  ) {
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        logger.info("查询工作报告，周报，参数为："+(params==null?"null": JSON.toJSONString(params)));
        List<DriverDailyReport> list  = this.driverDailyReportExMapper.queryWeekForListObject(params);
        PageInfo<DriverDailyReport> pageInfo = new PageInfo<>(list);
        if(list!=null && list.size()>0){
            for (DriverDailyReport report: list) {
                report.setStatDateStart(statDateStart);
                report.setStatDateEnd(statDateEnd);
            }
        }
        return pageInfo;
    }

    /**
     * <p>Title: selectSuppierNameAndCityNameDays</p>
     * <p>Description: 转换</p>
     * @param rows
     * @return
     * return: List<DriverDailyReportDTO>
     */
    @Override
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<DriverDailyReportDTO> selectSuppierNameAndCityNameDays(List<DriverDailyReport> rows, Integer reportType) throws ParseException {
        List<DriverDailyReportDTO> list = null;
        //不为空进行转换并查询城市名称和供应商名称
        if(rows!=null&&rows.size()>0){
            list = BeanUtil.copyList(rows, DriverDailyReportDTO.class);
            Set<Integer> s = new HashSet();
            for(DriverDailyReportDTO driverDailyReport : list){
                s.add(driverDailyReport.getSupplierId());
            }
            //查询供应商名称，一次查询出来避免多次读库
            List<CarBizSupplier>  names = this.carBizSupplierExMapper.queryNamesByIds(s);
            Map<String,CarBizSupplier> cacheCarBizSupplier = new HashMap<>();

            for (CarBizSupplier item: names) {
                cacheCarBizSupplier.put("c_"+item.getSupplierId(),item);
            }
            CarBizSupplier tempCarBizSupplier = null;
            for (DriverDailyReportDTO dto: list) {
                tempCarBizSupplier = cacheCarBizSupplier.get("c_"+dto.getSupplierId());
                if(tempCarBizSupplier != null){
                    dto.setSupplierName(tempCarBizSupplier.getSupplierFullName());
                }
                //司机营业信息查询
                if (reportType==0){
                    this.modifyDriverVolume(dto, dto.getStatDate());
                }else{
                    this.modifyMonthDriverVolume(dto,dto.getStatDateStart(),dto.getStatDateEnd());
                    dto.setStatDate("("+dto.getStatDateStart()+")-("+dto.getStatDateEnd()+")");
                }
            }
        }
        return list;
    }

    private void modifyDriverVolume(DriverDailyReportDTO ddre, String statDateStart) {
        if(StringUtils.isNotEmpty(statDateStart) && statDateStart.compareTo("2018-01-01") >0 ){
            String url = "/driverIncome/getDriverIncome?driverId="+ddre.getDriverId()+"&incomeDate=" + statDateStart;
            String result = busOrderCostTemplate.getForObject(url, String.class);

            Map<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
            if (null == resultMap || !String.valueOf(resultMap.get("code")).equals("0")) {
                logger.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回异常,code:"+String.valueOf(resultMap.get("code")));
                return;
            }

            String reData = String.valueOf(resultMap.get("data"));
            if (StringUtils.isBlank(reData)) {
                logger.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回data为空.");
                return;
            }

            Map dataMap = JSONObject.parseObject(String.valueOf(resultMap.get("data")), Map.class);
            if (dataMap!=null){
                String driverIncome = String.valueOf(dataMap.get("driverIncome"));
                if (StringUtils.isBlank(driverIncome)) {
                    logger.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回driverIncome为空.");
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(driverIncome);
//				log.info("modifyDriverVolume查询接口【/driverIncome/getDriverIncome】返回jsonObject成功."+jsonObject);
                // 当日完成订单量
                Integer orderCounts= Integer.valueOf(String.valueOf(jsonObject.get("orderCounts")));
                ddre.setOperationNum(orderCounts);
                // 当日营业额
                BigDecimal todayIncomeAmount = new BigDecimal(String.valueOf(jsonObject.get("todayIncomeAmount")));
                ddre.setActualPay(todayIncomeAmount.doubleValue());
                // 当日载客里程
                BigDecimal todayTravelMileage = new BigDecimal(String.valueOf(jsonObject.get("todayTravelMileage")));
                ddre.setServiceMileage(todayTravelMileage.doubleValue());
                // 当日司机代付价外费
                BigDecimal todayOtherFee = new BigDecimal(String.valueOf(jsonObject.get("todayOtherFee")));
                ddre.setDriverOutPay(todayOtherFee.doubleValue());
                // 当日司机代收
                BigDecimal todayDriverPay = new BigDecimal(String.valueOf(jsonObject.get("todayDriverPay")));
            }
        }
    }

    private void modifyMonthDriverVolume(DriverDailyReportDTO ddre, String statDateStart,String endDateStart) throws ParseException {
        if (StringUtils.isNotEmpty(statDateStart) && statDateStart.compareTo("2018-01-01") > 0) {
            long statTime = DateUtil.DATE_SIMPLE_FORMAT.parse(statDateStart).getTime();
            long endTime = DateUtil.DATE_SIMPLE_FORMAT.parse(endDateStart).getTime();
            String url = "/driverIncome/getDriverDateIncome?driverId=" + ddre.getDriverId() + "&startDate=" + statTime + "&endDate=" + endTime;
            String result = busOrderCostTemplate.getForObject(url, String.class);

            Map<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
            if (null == resultMap || !String.valueOf(resultMap.get("code")).equals("0")) {
                logger.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回异常,code:" + String.valueOf(resultMap.get("code")));
                return;
            }

            String reData = String.valueOf(resultMap.get("data"));
            if (StringUtils.isBlank(reData)) {
                logger.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回data为空.");
                return;
            }

            Map dataMap = JSONObject.parseObject(String.valueOf(resultMap.get("data")), Map.class);
            if (dataMap != null) {
                String driverIncome = String.valueOf(dataMap.get("driverIncome"));
                if (StringUtils.isBlank(driverIncome)) {
                    logger.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回driverIncome为空.");
                    return;
                }

                JSONObject jsonObject = JSONObject.parseObject(driverIncome);
//				log.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回jsonObject成功."+jsonObject);
                // 当段日期完成订单量
                Integer orderCounts = Integer.valueOf(String.valueOf(jsonObject.get("orderCounts")));
                ddre.setOperationNum(orderCounts);
                // 当段日期营业额
                BigDecimal incomeAmount = new BigDecimal(String.valueOf(jsonObject.get("incomeAmount")));
                ddre.setActualPay(incomeAmount.doubleValue());
//					// 当段日期载客里程
//					BigDecimal todayTravelMileage = new BigDecimal(String.valueOf(jsonObject.get("todayTravelMileage")));
//					ddre.setServiceMileage(todayTravelMileage.doubleValue());
//					// 当段日期司机代付价外费
//					BigDecimal todayOtherFee = new BigDecimal(String.valueOf(jsonObject.get("todayOtherFee")));
//					ddre.setDriverOutPay(todayOtherFee.doubleValue());
//					// 当段日期司机代收
//					BigDecimal todayDriverPay = new BigDecimal(String.valueOf(jsonObject.get("todayDriverPay")));
            }
        }
    }


}
