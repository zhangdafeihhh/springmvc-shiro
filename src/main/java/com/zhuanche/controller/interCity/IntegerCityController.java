package com.zhuanche.controller.interCity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sq.common.okhttp.OkHttpUtil;
import com.zhuanche.common.enums.OrderStateEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author fanht
 * @Description 新城际拼车后端
 * @Date 2019/10/14 下午1:55
 * @Version 1.0
 */
@Controller
@RequestMapping("/integerCity")
public class IntegerCityController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${order.saas.es.url}")
    private String  esOrderDataSaasUrl;

    @Autowired
    private DriverInfoInterCityExMapper infoInterCityExMapper;

    @Value("${config.url}")
    private String configUrl;

    @Value("${lbs.url}")
    private String lbsUrl;


    /**
     * 订单查询
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState 待指派、待服务、已出发、已上车、服务中、已送达、已完成、已取消
     * @param orderPushDriverType 订单指派方式  绑单、抢单；
     * @param serviceType 服务类别: 新城际拼车、新城际包车
     * @param orderType 普通订单、机构用车
     * @param airportId 是否拼车单
     * @param orderSource 订单来源 线上订单、手动录单、扫码订单
     * @param driverName 司机姓名
     * @param driverPhone 司机手机号
     * @param licensePlates 司机车牌号
     * @param reserveName 预订人名称
     * @param reservePhone 预订人手机号
     * @param riderName 乘车人名称
     * @param orderNo 子订单号
     * @param mainOrderNo 主订单号
     * @param beginCreateDate 下单开始时间
     * @param endCreateDate 下单结束时间
     * @param beginCostStartDate 订单完成开始时间
     * @param beginCostEndDate 订单完成结束时间
     * @param riderPhone 乘车人手机号
     * @return
     */
    @RequestMapping(value = "/orderQuery",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse orderQuery(@Verify(param = "pageNum",rule = "required") Integer pageNum,
                                   @Verify(param = "pageSize",rule = "required") Integer pageSize,
                                   Integer cityId,
                                   Integer supplierId,
                                   Integer orderState,
                                   Integer orderPushDriverType,
                                   Integer serviceType,
                                   Integer orderType,
                                   Integer airportId,
                                   Integer orderSource,
                                   String driverName,
                                   String driverPhone,
                                   String licensePlates,
                                   String reserveName,
                                   String reservePhone,
                                   String riderName,
                                   String orderNo,
                                   String mainOrderNo,
                                   String beginCreateDate,
                                   String endCreateDate,
                                   String beginCostStartDate,
                                   String beginCostEndDate,
                                   String riderPhone){
        logger.info(MessageFormat.format("订单查询入参:pageNum:{0},pageSize:{1},cityId:{2},supplierId:{3},orderState:" +
                "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostStartDate{20},beginCostEndDate{21},riderPhone:{22}",pageNum,
                pageSize,cityId,supplierId,orderState,orderPushDriverType,serviceType,orderType,airportId,orderSource,
                driverName,driverPhone,licensePlates,reserveName,reservePhone,riderName,orderNo,mainOrderNo,beginCreateDate,
                endCreateDate,beginCostStartDate,beginCostEndDate,riderPhone));

        Map<String,Object> map = Maps.newHashMap();
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        map.put("cityId",cityId);
        map.put("supplierId",supplierId);
        map.put("orderState",orderState);
        map.put("orderPushDriverType",orderPushDriverType);
        map.put("serviceType",serviceType);
        map.put("orderType",orderType);
        map.put("airportId",airportId);
        map.put("orderSource",orderSource);
        map.put("driverName",driverName);
        map.put("driverPhone",driverPhone);
        map.put("licensePlates",licensePlates);
        map.put("reserveName",reserveName);
        map.put("reservePhone",reservePhone);
        map.put("riderName",riderName);
        map.put("orderNo",orderNo);
        map.put("mainOrderNo",mainOrderNo);
        map.put("beginCreateDate",beginCreateDate);
        map.put("endCreateDate",endCreateDate);
        map.put("beginCostStartDate",beginCostStartDate);
        map.put("beginCostEndDate",beginCostEndDate);
        map.put("riderPhone",riderPhone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String  transId =sdf.format(new Date());
        map.put("transId",transId);

        String url = esOrderDataSaasUrl +"/order/v1/search";


        //功能是给运营开放的，不需要权限处理
        String result =  MpOkHttpUtil.okHttpGet(url,map,0,null);
        JSONArray resultArray = new JSONArray();

        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue("code");
            //0表示有结果返回
            if(code == 0){
                //JSONArray resultData = jsonObject.getJSONArray("data");
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.get("data") != null) {
                    JSONArray array = jsonData.getJSONArray("data");
                    JSONObject jsonResult = new JSONObject();
                    for (int i = 0; i < array.size(); i++) {
                        jsonResult = array.getJSONObject(i);
                        /*JSONObject jsonReturn = new JSONObject();
                        jsonReturn.put("orderId", jsonResult.get("orderId"));*/
                        resultArray.add(jsonResult);
                    }
                }
            }
        }

        //调用订单组接口查询
        return AjaxResponse.success(resultArray);
    }


    /**
     * 步骤1
     * @param reserveName
     * @param reservePhone
     * @param isSameRider
     * @param riderName
     * @param riderPhone
     * @param riderCount 乘客数
     * @param boardingTime
     * @param boardingCityId
     * @param boardingGetOnX
     * @param boardingGetOnY
     * @param boardingGetOffCityId
     * @param boardingGetOffX
     * @param boardingGetOffY
     * @return
     */
    @RequestMapping(value = "handOperateAddOrderSetp1",method = RequestMethod.POST)
    public AjaxResponse handOperateAddOrderSetp1(@Verify(param = "reserveName",rule = "required") String reserveName,
                                            String reservePhone,
                                            Integer isSameRider,
                                            String riderName,
                                            String riderPhone,
                                            Integer riderCount,
                                            String boardingTime,
                                            Integer boardingCityId,
                                            String boardingGetOnX,
                                            String boardingGetOnY,
                                            String boardingGetOffCityId,
                                            String boardingGetOffX,
                                            String boardingGetOffY
                                            ){
        logger.info(MessageFormat.format("手动录入订单步骤1入参,{0},{1},{2},{3},{4},{5},{6},{7}",reserveName,reservePhone,
                isSameRider,riderName,riderPhone,riderCount,boardingTime,boardingCityId,boardingGetOnX,boardingGetOnY,boardingGetOffCityId,
                boardingGetOffX,boardingGetOffY));

        //
        //根据横纵坐标获取围栏，根据围栏获取路线
        String mapUrl = "";
        Map<String,Object> mapRequest = Maps.newHashMap();
        mapRequest.put("boardingGetOnX",boardingGetOnX);
        mapRequest.put("boardingGetOnY",boardingGetOnY);
        mapRequest.put("boardingGetOffX",boardingGetOffX);
        mapRequest.put("boardingGetOffY",boardingGetOffY);

        String mapResult = MpOkHttpUtil.okHttpGet(mapUrl,mapRequest,0,null);
        if(StringUtils.isNotEmpty(mapResult)){
            JSONObject jsonObject = JSONObject.parseObject(mapResult);
            if(jsonObject.get("code")  != null){
                Integer code = jsonObject.getIntValue("code");
                if(code == 0){
                    JSONObject  areaJson = jsonObject.getJSONObject("data");
                    Integer areaStartRangId = areaJson.getInteger("areaStartRengId");
                    Integer areaEndRangId = areaJson.getInteger("areaEndRangId");
                    Map<String,Object> mapArea = Maps.newHashMap();
                    mapArea.put("areaStartRangId",areaStartRangId);
                    mapArea.put("areaEndRangId",areaEndRangId);
                    String areaUrl = "";
                    String areaResult = MpOkHttpUtil.okHttpPost(areaUrl,mapArea,0,null);
                    if(StringUtils.isNotEmpty(areaResult)){
                        JSONObject jsonResult = JSONObject.parseObject(areaResult);
                        if(jsonResult.get("data") != null && jsonResult.get("data") != ""){
                            logger.info("该坐标含有线路");
                        }else {
                            logger.info("对应坐标没有线路");
                            return  AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                        }
                    }

                }
            }

        }

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String,Object> map = Maps.newHashMap();
        map.put("sign","");//验签
        map.put("businessId","");//业务线ID
        map.put("type","1");//普通用户订单
        map.put("clientType","");//订单类型
        map.put("bookingDate",boardingTime);//预定日期（时间戳）
        map.put("riderName",riderName);
        map.put("riderPhone",riderPhone);
        /*map.put("bookingStartPoint",);
        map.put("bookingStartAddr",);
        map.put("bookingStartShortAddr",);
        map.put("bookingEndPoint",);
        map.put("bookingEndShortAddr",);*/
        map.put("cityId",boardingCityId);
        map.put("serviceTypeId",1);
        map.put("payFlag","1");//付款人标识 0-预订人付款，1-乘车人付款，2-门童代人叫车乘车人是自己且乘车人付款，-1-机构付款
        map.put("receiveSMS","2");//是否接收短信 “1”-接收，“2”-不接收
        map.put("bookingDriverId","0");//
        map.put("bookingCurrentAddr","预订人下单地址");
        map.put("bookingCurrentPoint","预订人下单位置坐标");
        map.put("channelsNum","saas手动创建订单");
        map.put("version","1.0.2");
        map.put("imei","imei");
        map.put("coordinate","GD");
        map.put("bookingUserId",loginUser.getId());
        map.put("bookingUserPhone",loginUser.getMobile());
        map.put("buyoutFlag","0");
        map.put("buyoutPrice","1");
        map.put("carpoolMark",1);//拼车标识(0:不拼车，1:拼车)

        map.put("seats",riderCount);
        map.put("ruleId",1);

        map.put("couponId","111");
        map.put("estimatedAmount","111");//预估金额



        String orderUrl = esOrderDataSaasUrl + "/order/carpool/create";
        String result = MpOkHttpUtil.okHttpPost(orderUrl,map,0,mapUrl);
        if(StringUtils.isNotEmpty(result)){
            JSONObject orderResult = JSONObject.parseObject(result);
            if(orderResult.get("code") != null && orderResult.getInteger("code") == 0){
                JSONObject jsonData = orderResult.getJSONObject("data");
                if(jsonData.get("subOrderNo")  != null){
                    String subOrderNo = jsonData.getString("subOrderNo");
                    return AjaxResponse.success(subOrderNo);
                }
            }
        }
        return AjaxResponse.success(null);
    }




    /**
     * 编辑订单
     * @return
     */
    @RequestMapping(value = "/editOrder",method = RequestMethod.POST)
    public AjaxResponse editOrder(@Verify(param = "reserveName",rule = "required") String reserveName,
                                  String orderNo,
                                  String reservePhone,
                                  Integer isSameRider,
                                  String riderName,
                                  String riderPhone,
                                  Integer riderCount,
                                  String boardingTime,
                                  Integer boardingCityId,
                                  String boardingGetOnX,
                                  String boardingGetOnY,
                                  String boardingGetOffCityId,
                                  String boardingGetOffX,
                                  String boardingGetOffY){

        //根据上下车地址判断是否在城际列车配置的范围内
        String queryLbsUrl = lbsUrl + "/division/location";
        Map<String,Object> queryLbsMap = Maps.newHashMap();
        queryLbsMap.put("lng",boardingGetOnX);
        queryLbsMap.put("lat",boardingGetOnY);
        queryLbsMap.put("inCoordType","mars");//高德
        String weilanIdX = "";

        String result = MpOkHttpUtil.okHttpGet(queryLbsUrl,queryLbsMap,0,null);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);
            if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.get("id") != null){
                    weilanIdX = jsonData.getString("id");
                }
            }
        }

        String weilanIdY = "";

        Map<String,Object> queryLbsMapY = Maps.newHashMap();
        queryLbsMapY.put("lng",boardingGetOffX);
        queryLbsMapY.put("lat",boardingGetOffY);
        queryLbsMapY.put("inCoordType","mars");//高德
        String resultY = MpOkHttpUtil.okHttpGet(queryLbsUrl,queryLbsMapY,0,null);
        if(StringUtils.isNotBlank(resultY)){
            JSONObject jsonObject =JSON.parseObject(resultY);
            if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.get("id") != null){
                    weilanIdY = jsonData.getString("id");
                }
            }
        }

        Map<String,Object> map = Maps.newHashMap();
        map.put("sign","sign");
        map.put("businessId","businessId");
        map.put("orderNo",orderNo);
        map.put("pushDriverType", OrderStateEnum.MANUAL_ASSIGNMENT.getCode());//订单指派司机类型(1 系统强派| 2 司机抢单| 3 后台人工指派)
        map.put("factStartAddr",boardingCityId);
        map.put("factStartPoint",weilanIdX);//根据坐标轴获取围栏id
        map.put("factEndAddr",weilanIdY);//
        map.put("carGroupId","carGroupId");
        map.put("driverId",null);
        map.put("licensePlates",null);
        map.put("receiveSMS",1);//预订人是否接收短信(1 接收 2 不接受)

        String url = esOrderDataSaasUrl + "/order/carpool/updateSubOrder/";
        String editResult = MpOkHttpUtil.okHttpPost(url,map,0,null);
        if(StringUtils.isNotBlank(editResult)){
            JSONObject jsonObject = JSONObject.parseObject(editResult);
            if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 1){

            }
        }


        return null;
    }

    /**
     * 取消订单
     * @return
     */
    @RequestMapping(value = "cancelOrder",method = RequestMethod.POST)
    public AjaxResponse cancelOrder(String orderNo){
        Map<String,Object> map = Maps.newHashMap();
        map.put("sign","sign");
        map.put("businessId","businessId");
        map.put("orderNo",orderNo);
        map.put("cancelReasonId",24);//固定取消原有
        map.put("memo","加盟商调度员手动取消");
        map.put("cancelType",2); //1-乘客；2-平台；3-客服；4-司机
        map.put("cancelStatus",11);//平台取消任意值

        return null;
    }





    /**
     * 查询主单
     * @param supplierId
     * @param driverName
     * @param driverPhone
     * @param license
     * @return
     */
    public AjaxResponse queryDriver(Integer supplierId,
                                    String driverName,
                                    String driverPhone,
                                    String license){

        logger.info(MessageFormat.format("查询城际拼车司机入参：supplierId:{0},driverName:{1},driverPhpne:{2},license:{3}",supplierId,
                driverName,driverPhone,license));
        List<DriverInfoInterCity> interCityList = infoInterCityExMapper.queryDriver(supplierId,driverName,driverPhone,license);
        //剩余车位数
        for(DriverInfoInterCity infoInterCity : interCityList){
            String url = esOrderDataSaasUrl + "/order/carpool/getCrossCityMainOrder";
            Map<String,Object> map = Maps.newHashMap();
            map.put("mainOrderNo","mainOrder");
            map.put("sign","sign");
            map.put("businessId","bussinessId");
            String result = MpOkHttpUtil.okHttpGet(url,map,0,null);
            if(StringUtils.isNotBlank(result)){
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    Integer userSeat = jsonData.getInteger("seat");
                    //如果是商务6座，默认是6个位置否则是4个
                    /*if(infoInterCity.get){

                    }*/
                }
            }
        }

        return null;
    }


    /**
     * 添加主单
     * @param orderNo
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param carGroupId
     * @param startTime
     * @return
     */
    @RequestMapping(value = "/addMainOrder",method = RequestMethod.POST)
    public AjaxResponse addMainOrder(String orderNo,
                                     Integer driverId,
                                     String driverName,
                                     String driverPhone,
                                     String licensePlates,
                                     String carGroupId,
                                     String startTime){

        logger.info("");
        //调用订单组接口
        Map<String,Object> map = Maps.newHashMap();
        map.put("sign","sign");
        map.put("businessId","businessId");
        map.put("mainOrderNo",null);
        map.put("orderNo",orderNo);
        map.put("driverId",driverId);
        map.put("driverName",driverName);
        map.put("driverPhone",driverPhone);
        map.put("licensePlates",licensePlates);
        map.put("carGroupId",carGroupId);
        String url = esOrderDataSaasUrl + "";
        String result = MpOkHttpUtil.okHttpPost(url,map,0,null);
        if(StringUtils.isNotBlank(result)){

        }

        return null;
    }

    //获取行程接口
    public AjaxResponse getTrips(String boardingGetOnX,
                                 String boardingGetOnY){
        //获取行程  调用配置中心的接口

        String result = configUrl+"/intercityCarUse/getLineSupplier";

        Map<String,Object> map = Maps.newHashMap();
        map.put("areaStartRangeId",boardingGetOnX);

        return AjaxResponse.success(null);
    }


    /**
     * 派单
     * @param mainOrderNo
     * @param orderNo
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param carGroupId
     * @return
     */
    public AjaxResponse handOperateAddOrderSetp2(String mainOrderNo,
                                                 String orderNo,
                                                 Integer driverId,
                                                 String driverName,
                                                 String driverPhone,
                                                 String licensePlates,
                                                 String carGroupId){
        //派单
        logger.info("派单接口入参:");
        Map<String,Object> map = Maps.newHashMap();
        map.put("sign","sign");
        map.put("businessId","businessId");
        map.put("mainOrderNo",mainOrderNo);
        map.put("orderNo",orderNo);
        map.put("driverId",driverId);
        map.put("driverName",driverName);
        map.put("driverPhone",driverPhone);
        map.put("licensePlates",licensePlates);
        map.put("carGroupId",carGroupId);
        String url = esOrderDataSaasUrl + "/order/carpool/bingCrossCityMainOrder/";
        String result = MpOkHttpUtil.okHttpPost(url,map,0,null);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                logger.info("子单绑定主单成功");
            }
        }

        return null;
    }



    //根据车辆类型获取座位数
    private int seatCount(String groupId){
        if(StringUtils.isEmpty(groupId)){
            return 4;
        }
        if(groupId.equals(OrderStateEnum.BUSINESSWELL.getCode()) ||
                groupId.equals(OrderStateEnum.BUSINESS7.getCode())){
            return 6;
        }else {
            return 4;
        }
    }
}
