package com.zhuanche.controller.interCity;

import co.elastic.apm.shaded.bytebuddy.asm.Advice;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.common.web.datavalidate.custom.IdCard;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.entity.orderPlatform.CarFactOrderInfoEntity;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.DriverEntity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.orderPlatform.ex.PoolMainOrderExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/14 下午4:19
 * @Version 1.0
 */
@Controller
@RequestMapping("/interCityMainOrder")
public class InterCityMainOrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private PoolMainOrderExMapper exMapper;

    @Autowired
    @Qualifier("carRestTemplate")
    private MyRestTemplate carRestTemplate;

    @Autowired
    private DriverInfoInterCityExMapper infoInterCityExMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    private static final String SPLIT = ",";
    /**
     * 订单查询
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState 待指派、待服务、已出发、已上车、服务中、已送达、已完成、已取消
     * @param serviceType 服务类别: 新城际拼车、新城际包车
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param licensePlates 司机车牌号
     * @param mainOrderNo 主订单号
     * @param beginCreateDate 下单开始时间
     * @param endCreateDate 下单结束时间
     * @param beginCostStartDate 订单完成开始时间
     * @param beginCostEndDate 订单完成结束时间
     * @return
     */
    @RequestMapping("/mainOrderQuery")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse mainOrderQuery(@Verify(param = "pageNum",rule = "required") Integer pageNum,
                                       @Verify(param = "pageSize",rule = "required") Integer pageSize,
                                       Integer cityId,
                                       Integer supplierId,
                                       Integer orderState,
                                       Integer serviceType,
                                       String driverName,
                                       String driverPhone,
                                       String licensePlates,
                                       String mainOrderNo,
                                       String beginCreateDate,
                                       String endCreateDate,
                                       String beginCostStartDate,
                                       String beginCostEndDate){

        //Page page = PageHelper.startPage(pageNum,pageSize);
        StringBuffer sb =new StringBuffer();
        Map<Integer,DriverInfoInterCity> map = Maps.newHashMap();
        boolean hasQuery = false;
        if(supplierId  != null || StringUtils.isNotEmpty(driverName) || StringUtils.isNotEmpty(driverPhone) || StringUtils.isNotEmpty(licensePlates)){
            List<DriverInfoInterCity> listDriver = infoInterCityExMapper.queryMainOrderDrivers(cityId,supplierId,driverName,
                    driverPhone,licensePlates,null);
            hasQuery = true;
            if(CollectionUtils.isNotEmpty(listDriver)){
               for(DriverInfoInterCity driver : listDriver){
                   sb.append(driver.getDriverId()).append(SPLIT);
                   map.put(driver.getDriverId(),driver);
               }
            }else {
                return AjaxResponse.success(new PageDTO(pageNum,pageSize,0,null));
            }
        }

        CarPoolMainOrderDTO dto = new CarPoolMainOrderDTO();

        if(StringUtils.isNotEmpty(sb.toString())){
            String driverIds = sb.toString().substring(0,sb.toString().length()-1);
            dto.setDriverIds(driverIds);
        }
        dto.setCityId(cityId);
        dto.setStatus(orderState);
        dto.setServiceTypeId(serviceType);
        dto.setMainOrderNo(mainOrderNo);
        dto.setCreateDateBegin(beginCreateDate);
        dto.setCreateDateEnd(endCreateDate);
        dto.setDriverStartDateStr(beginCostStartDate);
        dto.setDriverEndDateStr(beginCostEndDate);
        Page pageQuery = null;
        /*if(StringUtils.isNotEmpty(sb.toString())){
        }*/

        pageQuery = PageHelper.startPage(pageNum,pageSize);

        List<CarPoolMainOrderDTO> dtoList = exMapper.queryCarpoolMainList(dto);
        //如果查询条件是直接查询订单的库，需要回显
        if(!hasQuery && CollectionUtils.isNotEmpty(dtoList)){
            StringBuilder orderSb = new StringBuilder();
            for(CarPoolMainOrderDTO orderDTO : dtoList){
                orderSb.append(orderDTO.getDriverId()).append(SPLIT);
            }
            String driverIds = null;
            if(StringUtils.isNotEmpty(orderSb.toString())){
                 driverIds = orderSb.toString().substring(0,orderSb.toString().length()-1);
            }

            List<DriverInfoInterCity> listDriver = infoInterCityExMapper.queryMainOrderDrivers(null,null,null,
                    null,null,driverIds);
            if(CollectionUtils.isNotEmpty(listDriver)){
                for(DriverInfoInterCity driver : listDriver){
                    map.put(driver.getDriverId(),driver);
                }
            }

            for(CarPoolMainOrderDTO mainOrder : dtoList){
                DriverInfoInterCity driver =   map.get(mainOrder.getDriverId());
                if(driver != null){
                    mainOrder.setSupplierName(driver.getDriverName());
                    mainOrder.setSupplierId(driver.getSupplierId());
                    mainOrder.setDriverName(driver.getDriverName());
                    mainOrder.setDriverPhone(driver.getDriverPhone());
                    mainOrder.setLicensePlates(driver.getLicensePlates());
                    mainOrder.setCityName(driver.getCityName());
                }
            }


        }else {
            for(CarPoolMainOrderDTO mainOrder : dtoList){
                DriverInfoInterCity driver =   map.get(mainOrder.getDriverId());
                if(driver != null){
                    mainOrder.setSupplierName(driver.getDriverName());
                    mainOrder.setSupplierId(driver.getSupplierId());
                    mainOrder.setDriverName(driver.getDriverName());
                    mainOrder.setDriverPhone(driver.getDriverPhone());
                    mainOrder.setLicensePlates(driver.getLicensePlates());
                    mainOrder.setCityName(driver.getCityName());
                }
            }
        }

        int total = 0;
        total = (int)pageQuery.getTotal();

        /*if(StringUtils.isNotEmpty(sb.toString())){
        }else {
            total = (int)page.getTotal();
        }
    */

        PageDTO pageDTO =  new PageDTO(pageNum,pageSize,total,dtoList);
        return AjaxResponse.success(pageDTO);
    }


    /**
     * 拼车主订单详情
     * @param mainOrderNo
     * @return
     */
    @RequestMapping(value = "/mainOrderDetail",method = RequestMethod.GET)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse mainOrderDetail(String mainOrderNo){
        logger.info("获取拼车单订单详情入参:mainOrderNo" + mainOrderNo);
        try {
            String url = Common.GET_MAIN_ORDER + "?businessId=" + Common.BUSSINESSID + "&isShowSubOrderList=0&mainOrderNo=" + mainOrderNo;
            // 参数：订单号 、业务线id
            StringBuffer param = new StringBuffer("");
            param.append("businessId=" + Common.BUSSINESSID + "&isShowSubOrderList=0&");
            param.append("mainOrderNo=" + mainOrderNo  + '&');
            param.append("key=" + Common.MAIN_ORDER_KEY);
            String sign = Base64.encodeBase64String(DigestUtils.md5(param.toString()));
            url += "&sign="+sign;


            JSONObject result = carRestTemplate.getForObject(url,JSONObject.class);
            int code = result.getIntValue("code");
            String msg = result.getString("msg");
            if (code == 1) {
                logger.info("根据主订单号查询子订单出错,错误码:" + code + ",错误原因:" + msg);
                return  AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
            if (code == 0) {
                JSONObject data = result.getJSONObject("data");
                if (data==null || data.isEmpty()) {
                    AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
                }
               return AjaxResponse.success(data);
            }
        } catch (Exception e) {
            logger.error("根据主订单查询子订单信息异常" ,e);
           return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        return AjaxResponse.success(null);
    }

    private List<CarFactOrderInfoEntity> convent(JSONArray jsonArrayData) {
        List<CarFactOrderInfoEntity> list = Lists.newArrayList();
        for(int i=0;i<jsonArrayData.size();i++) {
            JSONObject data = jsonArrayData.getJSONObject(i);
            CarFactOrderInfoEntity entity = new CarFactOrderInfoEntity();
            entity.setOrderId(Integer.parseInt(data.getString("orderId")));
            entity.setOrderno(data.getString("orderNo"));
            list.add(entity);
        }
        return list;
    }



    @RequestMapping(method = RequestMethod.GET,value = "/getAllSeats")
    @ResponseBody
    public AjaxResponse getAllSeats(){
        logger.info("获取所有的座位数");
        List<CarBizCarGroup> listAll = carBizCarGroupExMapper.queryAllGroup();
        return AjaxResponse.success(listAll);
    }
}
