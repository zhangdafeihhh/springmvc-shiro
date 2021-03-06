package com.zhuanche.controller.intercity;

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
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.dto.rentcar.CityDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.entity.orderPlatform.CarFactOrderInfoEntity;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.util.Common;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.MyRestTemplate;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.mdbcarmanage.ex.MainOrderInterCityExMapper;
import mapper.orderPlatform.ex.PoolMainOrderExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/14 ??????4:19
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

    @Autowired
    private CarBizCityExMapper carBizCityExMapper;
    @Autowired
    private CarFactOrderInfoService carFactOrderInfoService;
    @Autowired
    private CarBizCityService carBizCityService;

    @Autowired
    private MainOrderInterCityExMapper mainOrderInterCityExMapper;

    private static final String SPLIT = ",";
    /**
     * ????????????
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState ?????????????????????????????????????????????????????????????????????????????????????????????
     * @param serviceType ????????????: ?????????????????????????????????
     * @param driverName ????????????
     * @param driverPhone ???????????????
     * @param licensePlates ???????????????
     * @param mainOrderNo ????????????
     * @param beginCreateDate ??????????????????
     * @param endCreateDate ??????????????????
     * @param beginCostStartDate ????????????????????????
     * @param beginCostEndDate ????????????????????????
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
        CarPoolMainOrderDTO dto = this.dto(cityId,supplierId,orderState,serviceType,driverName,driverPhone,licensePlates,mainOrderNo,
                beginCreateDate,endCreateDate,beginCostStartDate,beginCostEndDate,sb);
        Page pageQuery = null;
        pageQuery = PageHelper.startPage(pageNum,pageSize);
        List<CarPoolMainOrderDTO> dtoList = exMapper.queryCarpoolMainList(dto);
        /**????????????????????????????????????????????????????????????*/
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
                    mainOrder = this.mainOrder(driver,mainOrder);
                }
            }
        }else {
            for(CarPoolMainOrderDTO mainOrder : dtoList){
                DriverInfoInterCity driver =   map.get(mainOrder.getDriverId());
                if(driver != null){
                    mainOrder = this.mainOrder(driver,mainOrder);
                }
            }
        }
        int total = 0;
        total = (int)pageQuery.getTotal();
        PageDTO pageDTO =  new PageDTO(pageNum,pageSize,total,dtoList);
        return AjaxResponse.success(pageDTO);
    }


            private CarPoolMainOrderDTO dto(Integer cityId,
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
                                       String beginCostEndDate,
                                        StringBuffer sb){
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
                return dto;
            }

            private CarPoolMainOrderDTO mainOrder(DriverInfoInterCity driver,CarPoolMainOrderDTO mainOrder){
                mainOrder.setSupplierName(driver.getDriverName());
                mainOrder.setSupplierId(driver.getSupplierId());
                mainOrder.setDriverName(driver.getDriverName());
                mainOrder.setDriverPhone(driver.getDriverPhone());
                mainOrder.setLicensePlates(driver.getLicensePlates());
                mainOrder.setCityName(driver.getCityName());
                if(StringUtils.isNotEmpty(mainOrder.getMainOrderNo())){
                    MainOrderInterCity orderInterCity = mainOrderInterCityExMapper.queryMainOrder(mainOrder.getMainOrderNo());
                    mainOrder.setRouteName(orderInterCity != null ? orderInterCity.getMainName() : "");
                }
                return mainOrder;
            }
    /**
     * ?????????????????????
     * @param mainOrderNo
     * @return
     */
    @RequestMapping(value = "/mainOrderDetail",method = RequestMethod.GET)
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse mainOrderDetail(String mainOrderNo){
        logger.info("?????????????????????????????????:mainOrderNo" + mainOrderNo);
        try {
            String url = Common.GET_MAIN_ORDER + "?businessId=" + Common.BUSSINESSID + "&isShowSubOrderList=0&mainOrderNo=" + mainOrderNo;
            // ?????????????????? ????????????id
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
                logger.info("???????????????????????????????????????,?????????:" + code + ",????????????:" + msg);
                return  AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
            }
            if (code == 0) {
                JSONObject data = result.getJSONObject("data");
                if (data==null || data.isEmpty()) {
                    AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
                }
                fillAttr(data);
                return AjaxResponse.success(data);
            }
        } catch (Exception e) {
            logger.error("??????????????????????????????????????????" ,e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
        return AjaxResponse.success(null);
    }

    private void fillAttr(JSONObject data) {
//        ??????????????? orderId
       Long createDate = data.getLongValue("createDate");
        Long driverEndDate = data.getLongValue("driverEndDate");
        Long driverStartDate = data.getLongValue("driverStartDate");
        Long updateDate = data.getLongValue("updateDate");
        //        ????????????  createDateStr
       data.put("createDateStr",createDate<=0?null: DateUtil.getSdf("yyyy-MM-dd HH:mm:ss").format(new Date(createDate)));
       data.put("updateDateStr",updateDate<=0?null: DateUtil.getSdf("yyyy-MM-dd HH:mm:ss").format(new Date(updateDate)));
       data.put("driverEndDateStr", driverEndDate<=0?null:DateUtil.getSdf("yyyy-MM-dd HH:mm:ss").format(new Date(driverEndDate)));
        //        ?????????????????? driverStartDateStr
       data.put("driverStartDateStr", driverStartDate<=0?null:DateUtil.getSdf("yyyy-MM-dd HH:mm:ss").format(new Date(driverStartDate)));
        //        ???????????? serviceTypeName
       data.put("serviceTypeName",carFactOrderInfoService.serviceTypeName(data.getInteger("serviceTypeId")));
        //        ?????? cityName
       data.put("cityName", carBizCityService.selectByPrimaryKey(data.getInteger("cityId")).getCityName());
        //        ???????????? driverName
        CarBizDriverInfoDTO driverInfoDTO = carFactOrderInfoService.querySupplierIdAndNameByDriverId(data.getInteger("driverId"));
       data.put("driverName",driverInfoDTO.getName());
        //        ???????????? driverPhone
       data.put("driverPhone",driverInfoDTO.getPhone());
        //        ?????? modelDetail
       data.put("modelDetail", carFactOrderInfoService.selectModelNameByLicensePlates(data.getString("licensePlates")));
        String tableName="car_biz_driver_record_"+data.getString("createDateStr").split(" ")[0];
        Map<String,String> paraMap=new HashMap<String, String>(4);
        paraMap.put("orderNo", data.getString("mainOrderNo"));
        paraMap.put("tableName", tableName.replace("-", "_") );

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
        logger.info("????????????????????????");
        List<CarBizCarGroup> listAll = carBizCarGroupExMapper.queryAllGroup();
        return AjaxResponse.success(listAll);
    }


    /**
     * ???????????????????????? ????????????
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/getAllCity")
    @ResponseBody
    public AjaxResponse getAllCity(){
        logger.info("????????????????????????");
        List<CityDto> queryAllCity = carBizCityExMapper.selectAllCity();
        return AjaxResponse.success(queryAllCity);
    }
}
