package com.zhuanche.serv.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.DriverDailyReportDTO;
import com.zhuanche.dto.driver.DriverIncome;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReport;
import com.zhuanche.entity.mdbcarmanage.DriverDailyReportParams;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.DriverDailyReportExService;
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
import org.springframework.util.CollectionUtils;

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
        List<Integer> listDriverIds = this.driverDailyReportExMapper.queryDriverIds(params);
        PageInfo<Integer> pageInfoDriver = new PageInfo<>(listDriverIds);
        String driverIds = "";
        if (listDriverIds != null && listDriverIds.size()>0){
            for(Integer driverId : listDriverIds){
                driverIds += driverId + Constants.SEPERATER;
            }
            if (StringUtils.isNotEmpty(driverIds)){
                params.setDriverIds(driverIds.substring(0,driverIds.length()-1));
            }
        }

        PageInfo<DriverDailyReport> pageInfo = null;
        List<DriverDailyReport> list  = new ArrayList<DriverDailyReport>();
        if (StringUtils.isNotEmpty(driverIds)){
            list  = this.driverDailyReportExMapper.queryForListObject(params);
        }
        pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(pageInfoDriver.getTotal());
        pageInfo.setPages(pageInfoDriver.getPages());
        return pageInfo;
    }

    @Override
    public PageInfo<DriverDailyReport> findWeekDriverDailyReportByparam(DriverDailyReportParams params,String statDateStart,  String statDateEnd  ) {
        try{
        PageHelper.startPage(params.getPage(), params.getPageSize(), true);
        logger.info("查询工作报告，周报，参数为："+(params==null?"null": JSON.toJSONString(params)));
        List<Integer> listDriverIds = this.driverDailyReportExMapper.queryDriverIds(params);
        PageInfo<Integer> pageInfoDriver = new PageInfo<>(listDriverIds);
        String driverIds = "";
        if (listDriverIds != null && listDriverIds.size()>0){
            for(Integer driverId : listDriverIds){
                driverIds += driverId + Constants.SEPERATER;
            }
            if (StringUtils.isNotEmpty(driverIds)){
                params.setDriverIds(driverIds.substring(0,driverIds.length()-1));
            }
        }

        PageInfo<DriverDailyReport> pageInfo = null;
        List<DriverDailyReport> list  = new ArrayList<DriverDailyReport>();
        if (StringUtils.isNotEmpty(driverIds)){
            list  = this.driverDailyReportExMapper.queryWeekForListObject(params);
        }
        pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(pageInfoDriver.getTotal());
        pageInfo.setPages(pageInfoDriver.getPages());
        if(list!=null && list.size()>0){
            for (DriverDailyReport report: list) {
                report.setStatDateStart(statDateStart);
                report.setStatDateEnd(statDateEnd);
            }
        }

        return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
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
    public List<DriverDailyReportDTO> selectSuppierNameAndCityNameDays(List<DriverDailyReport> rows,
                                                                       Integer reportType,
                                                                       String statDateStart,
                                                                       String statDateEnd) throws ParseException {
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
            StringBuffer stringBuffer = new StringBuffer();
            for (DriverDailyReportDTO dto: list) {
                tempCarBizSupplier = cacheCarBizSupplier.get("c_"+dto.getSupplierId());
                if(tempCarBizSupplier != null){
                    dto.setSupplierName(tempCarBizSupplier.getSupplierFullName());
                }
                stringBuffer.append(dto.getDriverId()).append(Constants.SEPERATER);
                //司机营业信息查询
                if (reportType!=0)
                    dto.setStatDate("("+dto.getStatDateStart()+")-("+dto.getStatDateEnd()+")");
            }
            if (reportType==0){
                long startTime = System.currentTimeMillis();
                for(DriverDailyReportDTO ddre : list) {
                    this.modifyDriverVolume(ddre, ddre.getStatDate());
                }
               /*
                if (StringUtils.isNotEmpty(stringBuffer.toString())){
                    String driverIds = stringBuffer.substring(0,stringBuffer.length()-1).toString();
                    Map<String,DriverIncomeForEveryDay> map = this.modifyDriverVolume(driverIds,statDateStart);
                    for(DriverDailyReportDTO ddre : list){
                        DriverIncomeForEveryDay driver = map.get(ddre.getDriverId().toString());
                        if(driver != null){
                            ddre.setOperationNum(driver.getOperationNum());
                            ddre.setActualPay(driver.getActualPay());
                            ddre.setServiceMileage(driver.getServiceMileage());
                            ddre.setDriverOutPay(driver.getDriverOutPay());
                        }
                    }
                }*/
                long endTime = System.currentTimeMillis();

                long totalTime = (endTime-startTime);
                System.out.println("远程时长：" +(totalTime));
            }else{
                long startTime = System.currentTimeMillis();
                if (!CollectionUtils.isEmpty(list) && StringUtils.isNotEmpty(stringBuffer.toString())){
                    String drivers = stringBuffer.substring(0,stringBuffer.length()-1).toString();
                    Map<String,DriverIncome> maps = this.modifyMonthDriverVolumes(drivers,statDateStart,statDateEnd,reportType);
                    if (maps != null && maps.size() >0){
                        for (DriverDailyReportDTO dto : list){
                            DriverIncome driverIncome = maps.get(dto.getDriverId().toString());
                            if (driverIncome != null){
                                dto.setOperationNum(driverIncome.getOrderCounts());
                                dto.setActualPay(driverIncome.getIncomeAmount().doubleValue());
                            }

                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                long lastTime = endTime - startTime;
                logger.info("调用远程接口时长：" + lastTime);
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
//                ddre.setServiceMileage(todayTravelMileage.doubleValue());
                // 当日司机代付价外费
                BigDecimal todayOtherFee = new BigDecimal(String.valueOf(jsonObject.get("todayOtherFee")));
//                ddre.setDriverOutPay(todayOtherFee.doubleValue());
                // 当日司机代收
                BigDecimal todayDriverPay = new BigDecimal(String.valueOf(jsonObject.get("todayDriverPay")));
            }
        }
    }







    /**
     * 批量从计费组获取信息
     * @param drivers
     * @param startDate
     * @param endDate
     * @return
     */

    private Map<String, DriverIncome> modifyMonthDriverVolumes(String drivers, String startDate, String endDate,Integer type) {
        Map<String, DriverIncome> maps = new HashMap<>();
        if (StringUtils.isNotEmpty(startDate) && startDate.compareTo(Constants.QUERY_DATE) > 0) {
            long statTime = 0;
            long endTime = 0;
            try {
                statTime = DateUtil.DATE_SIMPLE_FORMAT.parse(startDate).getTime();
                endTime = DateUtil.DATE_SIMPLE_FORMAT.parse(endDate).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long time = System.currentTimeMillis();
            String url = "";
//            if (Constants.WEEK.equals(type)){
//                url = "/driverIncome/findDriverDateIncomes?driverIds=" + drivers + "&startDate=" + statTime + "&endDate=" + endTime;
//            }else if(Constants.MONTH.equals(type)){
//                url = "/driverIncome/findDriverDateIncomes?driverIds=" + drivers + "&startDate=" + statTime + "&endDate=" + endTime;
//                //url = "/driverIncome/findDriverIncomes?driverIds="+drivers+"&inselectSuppierNameAndCityNameDayscomeDate="+startDate ;
//            }
            url = "/driverIncome/findDriverDateIncomes";
//            String result = busOrderCostTemplate.getForObject(url, String.class);
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("driverIds", drivers);
            paramMap.put("startDate", statTime);
            paramMap.put("endDate", endTime);
            String result = busOrderCostTemplate.postForObject(url, JSONObject.class, paramMap);
            long invokeEndTime = System.currentTimeMillis();
            logger.info("调用计费时长：" + (invokeEndTime-time));
            Map<String, Object> resultMap = JSONObject.parseObject(result, HashMap.class);
            if (null == resultMap || !String.valueOf(resultMap.get("code")).equals("0")) {
                logger.info("modifyMonthDriverVolumes查询接口【/driverIncome/getDriverIncomes】返回异常,code:" + String.valueOf(resultMap.get("code")));
                return null;
            }
            String reData = String.valueOf(resultMap.get("data"));
            if (StringUtils.isEmpty(reData)) {
                logger.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回data为空.");
                return null;
            }
            Map dataMap = JSONObject.parseObject(String.valueOf(resultMap.get("data")), Map.class);
            if (dataMap != null) {
                String driverIncome = String.valueOf(dataMap.get("driverIncomes"));
                if (StringUtils.isEmpty(driverIncome)) {
                    logger.info("modifyMonthDriverVolume查询接口【/driverIncome/getDriverIncome】返回driverIncomes为空.");
                    return null;
                }
                JSONArray jsonArray = JSONObject.parseArray(driverIncome);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 当段日期完成订单量
                    DriverIncome driverObject = null;
                    try {
                        Integer orderCounts = jsonObject.getInteger("orderCounts");
                        // 当段日期营业额
                        BigDecimal incomeAmount = new BigDecimal(jsonObject.getString("incomeAmount"));

                        driverObject = new DriverIncome(orderCounts, incomeAmount);

                        maps.put(jsonObject.getString("driverId"), driverObject);

                    } catch (Exception e) {
                        logger.info(e.getMessage());
                        continue;
                    }
                }
            }
        }
        return maps;
    }

    @Override
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public List<DriverDailyReport> queryDriverReportData(DriverDailyReportParams params){
        return  this.driverDailyReportExMapper.queryDriverReportData(params);
    }
}
