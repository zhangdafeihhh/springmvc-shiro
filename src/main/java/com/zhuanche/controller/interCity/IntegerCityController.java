package com.zhuanche.controller.interCity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.enums.OrderStateEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.InterCityOrderDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import com.zhuanche.util.SignatureUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
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

import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Value("${config.url}")
    private String configUrl;

    @Value("${lbs.url}")
    private String lbsUrl;

    @Value("${car.rest.url}")
    private String carRestUrl;

    @Autowired
    @Qualifier("carRestTemplate")
    private MyRestTemplate carRestTemplate;

    @Value("${order.server.api.base.url}")
    private String orderServiceUrl;

    @Autowired
    private DriverInfoInterCityExMapper infoInterCityExMapper;



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
    @RequestMapping(value = "/handOperateAddOrderSetp1",method = RequestMethod.POST)
    @ResponseBody
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
        Map<String,Object> mapX = Maps.newHashMap();
        mapX.put("lng",boardingGetOnX);
        mapX.put("lat",boardingGetOnY);

        String getOnId = "";
        String resultX = MpOkHttpUtil.okHttpGet(lbsUrl + "/division/entrance",mapX,0,null);
        if(resultX != null){
            JSONObject jsonObject = JSONObject.parseObject(resultX);
            if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.get("id") != null){
                    getOnId = jsonData.getString("id");
                }
            }
        }

        if(StringUtils.isEmpty(getOnId)){
            logger.info("获取上车点失败");
            return AjaxResponse.fail(RestErrorCode.GET_ON_ADDRESS_FAILED);
        }


        Map<String,Object> mapY = Maps.newHashMap();

        mapY.put("lng",boardingGetOffX);
        mapY.put("lat",boardingGetOffY);

        String resultY = MpOkHttpUtil.okHttpGet(lbsUrl+"/division/entrance",mapY,0,null);

        String getOffId = "";
        if(resultX  != null){
            JSONObject jsonObject = JSONObject.parseObject(resultY);
            if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.get("id") != null){
                    getOffId = jsonData.getString("id");
                }
            }
        }

        if(StringUtils.isEmpty(getOffId)){
            logger.info("获取下车点失败");
            return AjaxResponse.fail(RestErrorCode.GET_OFF_ADDRESS_FAILED);
        }

        Integer areaStartRangId = Integer.valueOf(getOnId);
        Integer areaEndRangId = Integer.valueOf(getOffId);
        String areaUrl = configUrl + "/intercityCarUse/getLineSupplier?areaStartRangeId="+areaStartRangId+"&areaEndRangeId="+areaEndRangId;
        String areaResult = MpOkHttpUtil.okHttpGet(areaUrl,null,0,null);
        if(StringUtils.isNotEmpty(areaResult)){
            JSONObject jsonResult = JSONObject.parseObject(areaResult);
            if(jsonResult.get("data") != null && jsonResult.get("data") != ""){
                logger.info("该坐标含有线路");
            }else {
                logger.info("对应坐标没有线路");
                return  AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
            }
        }


        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String,Object> map = Maps.newHashMap();
        long currentTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        map.put("businessId",Common.BUSSINESSID);//业务线ID
        sb.append("businessId=" + Common.BUSSINESSID+ '&');
        map.put("type","1");//普通用户订单
        sb.append("type=1"+ '&');
        map.put("clientType",10);//订单类型
        sb.append("clientType=10"  + '&');
        map.put("bookingDate", currentTime);//预定日期（时间戳）
        sb.append("bookingDate=" + currentTime  + '&');
        map.put("riderName",riderName);
        sb.append("riderName=" + riderName  + '&');
        map.put("riderPhone",riderPhone);
        sb.append("riderPhone=" + riderPhone  + '&');
        map.put("cityId",boardingCityId);
        sb.append("cityId=" +boardingCityId  + '&');
        map.put("serviceTypeId",68);
        sb.append("serviceTypeId=68"   + '&');
        map.put("payFlag","1");//付款人标识 0-预订人付款，1-乘车人付款，2-门童代人叫车乘车人是自己且乘车人付款，-1-机构付款
        sb.append("payFlag=1"   + '&');
        map.put("receiveSMS","2");//是否接收短信 “1”-接收，“2”-不接收
        sb.append("receiveSMS=2"   + '&');
        map.put("bookingDriverId","0");//
        sb.append("bookingDriverId=0" + '&');//
        map.put("bookingCurrentAddr","1");
        sb.append("bookingCurrentAddr=1" + '&');
        map.put("bookingCurrentPoint","1");
        sb.append("bookingCurrentPoint=1"   + '&');
        map.put("channelsNum","1");
        sb.append("channelsNum=1"   + '&');
        map.put("version","1");
        sb.append("version=1"   + '&');
        map.put("imei","1");
        sb.append("imei=1"  + '&');
        map.put("coordinate","GD");
        sb.append("coordinate=GD"   + '&');
        map.put("bookingUserId",loginUser.getId());
        sb.append("bookingUserId=" + loginUser.getId()  + '&');
        map.put("bookingUserPhone",loginUser.getMobile());
        sb.append("bookingUserPhone=" + loginUser.getMobile()  + '&');
        map.put("buyoutFlag","0");
        sb.append("buyoutFlag=0" + '&');
        map.put("buyoutPrice","1");
        sb.append("buyoutPrice=1" + '&');
        map.put("carpoolMark",1);//拼车标识(0:不拼车，1:拼车)
        sb.append("carpoolMark=1" + '&');
        map.put("seats",riderCount);
        sb.append("seats=" + riderCount+ '&');
        map.put("ruleId",1);
        sb.append("ruleId=1" + '&');
        map.put("couponId","111");
        sb.append("couponId=111" + '&');
        map.put("estimatedAmount","111");//预估金额
        sb.append("estimatedAmount=111" + '&');


        List<String> list = this.list(sb.toString());
        Collections.sort(list);
        list.add("key="+Common.MAIN_ORDER_KEY);
        StringBuilder sbSort = new StringBuilder();
        for(String str : list){
            sbSort.append(str).append("&");
        }

        String md5Before = sbSort.toString().substring(0,sbSort.toString().length()-1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(md5Before));
        map.put("sign",sign);


       String orderUrl =  "/order/carpool/create";

        String result= carRestTemplate.postForObject(orderUrl,JSONObject.class,map);

            JSONObject orderResult = JSONObject.parseObject(result);
            if(orderResult.get("code") != null && orderResult.getInteger("code") == 0){
                JSONObject jsonData = orderResult.getJSONObject("data");
                return AjaxResponse.success(jsonData);
            }

        return AjaxResponse.success(null);
    }


    /**
     * 根据子订单号获取订单数据进行编辑
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping("/subOrderByQuery")
    public AjaxResponse subOrderByQuery(@Verify(param = "orderNo",rule = "required") String orderNo){

        logger.info("获取订单详情入参orderNo:" + orderNo);

        Map<String,Object> map = Maps.newHashMap();
        List<String> strList = new ArrayList<>();
        map.put("bId",Common.BUSSINESSID);
        strList.add("bId="+Common.BUSSINESSID);
        map.put("orderNo",orderNo);
        strList.add("orderNo="+orderNo);

        Collections.sort(strList);
        strList.add("key="+Common.MAIN_ORDER_KEY);

        String sign = null;
        try {
            sign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(map, Common.MAIN_ORDER_KEY));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        map.put("sign",sign);


        JSONObject jsonObject =MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderByOrderNo", map, 0, "查询订单列表");

        if(jsonObject != null && jsonObject.get("code") !=null){
            Integer code = jsonObject.getIntValue("code");
            if(0 == code){
             JSONObject jsonData =  jsonObject.getJSONObject("data");
                InterCityOrderDTO dto = new InterCityOrderDTO();
                dto.setReserveName(jsonData.getString(""));
                dto.setReservePhone(jsonData.get("bookingUserPhone")==null?"":jsonData.getString("bookingUserPhone"));
                dto.setRiderName(jsonData.get("riderName") == null ?"":jsonData.getString("riderName"));
                dto.setRiderPhone(jsonData.get("riderPhone") == null ? "" : jsonData.getString("riderPhone"));
                dto.setRiderCount(jsonData.get("factDriverId") == null ? 0 : jsonData.getInteger("factDriverId"));
                dto.setBoardingTime(jsonData.get("bookingDate") == null ? "" : jsonData.getString("bookingDate"));
                dto.setBoardingCityId(jsonData.get("cityId") == null ? "" : jsonData.getString("cityId"));
                dto.setBoardingGetOffCityId(jsonData.get("bookingStartAddr") == null ? "" : jsonData.getString("bookingStartAddr"));
                dto.setBoardingGetOffCityId(jsonData.get("bookingEndAddr") == null ? "" : jsonData.getString("bookingEndAddr"));
                dto.setBookingStartPoint(jsonData.get("bookingStartPoint") == null ? "" : jsonData.getString("bookingStartPoint"));
                dto.setBookingEndPoint(jsonData.get("bookingEndPoint") == null ? "" : jsonData.getString("bookingEndPoint"));
                dto.setStatus(jsonData.get("status") == null ? null : jsonData.getInteger("status"));
                return AjaxResponse.success(dto);
            }
        }


        return AjaxResponse.fail(RestErrorCode.ORDER_DETAIL_UNDEFINED);

    }
    /**
     * 编辑订单
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editOrder",method = RequestMethod.POST)
    public AjaxResponse editOrder(@Verify(param = "orderNo",rule = "required")String orderNo,
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
                                  String boardingGetOffY,
                                  String mainOrderNo,
                                  Integer status){

        //根据上下车地址判断是否在城际列车配置的范围内
        String queryLbsUrl = lbsUrl + "/division/entrance";
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
        List<String> list = new ArrayList<>();
        map.put("businessId",Common.BUSSINESSID);
        list.add("businessId="+Common.BUSSINESSID);
        map.put("orderNo",orderNo);
        list.add("orderNo="+orderNo);
       map.put("factStartPoint",weilanIdX);//根据坐标轴获取围栏id
        list.add("factStartPoint="+weilanIdX);
        map.put("factEndPoint",weilanIdY);
        list.add("factEndPoint="+weilanIdY);

        if(StringUtils.isNotEmpty(mainOrderNo)){
            map.put("mainOrderNo",mainOrderNo);
            list.add("mainOrderNo="+mainOrderNo);
        }

        map.put("pushDriverType", OrderStateEnum.MANUAL_ASSIGNMENT.getCode());//订单指派司机类型(1 系统强派| 2 司机抢单| 3 后台人工指派)
        list.add("pushDriverType="+OrderStateEnum.MANUAL_ASSIGNMENT.getCode());
        map.put("factStartAddr",boardingCityId);
        list.add("factStartAddr="+boardingCityId);

        map.put("factEndAddr",boardingGetOffCityId);//
        list.add("factEndAddr="+boardingGetOffCityId);

        map.put("factDriverId",riderCount);
        list.add("factDriverId="+riderCount);

        map.put("bookingUserPhone",reservePhone);
        list.add("bookingUserPhone="+reservePhone);

        map.put("riderName",riderName);
        list.add("riderName="+riderName);

        map.put("riderPhone",riderPhone);
        list.add("riderPhone="+riderPhone);

        map.put("crossCityStartTime",boardingTime);
        list.add("crossCityStartTime="+boardingTime);

        map.put("status",status);
        list.add("status="+status);


        Collections.sort(list);
        list.add("key="+Common.MAIN_ORDER_KEY);
        StringBuilder sb = new StringBuilder();
        for(String str : list){
            sb.append(str).append("&");
        }

        String param = sb.toString().substring(0,sb.toString().length()-1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(param));
        map.put("sign",sign);
        String url =  "/order/carpool/updateSubOrder";
        String editResult = carRestTemplate.postForObject(url,JSONObject.class,map);
        if(StringUtils.isNotBlank(editResult)){
            JSONObject jsonObject = JSONObject.parseObject(editResult);
            if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                logger.info("更新成功");
                return AjaxResponse.success(null);
            }
        }


        return AjaxResponse.fail(RestErrorCode.UPDATE_SUB_ORDER_FAILED);
    }

    /**
     * 取消订单
     * @return
     */
    @RequestMapping(value = "/cancelOrder",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse cancelOrder(String orderNo){
        Map<String,Object> map = Maps.newHashMap();
        List<String> listStr = new ArrayList<>();
        map.put("businessId",Common.BUSSINESSID);
        listStr.add("businessId="+Common.BUSSINESSID);
        map.put("orderNo",orderNo);
        listStr.add("orderNo="+orderNo);
        map.put("cancelReasonId",24);//固定取消原因
        listStr.add("cancelReasonId="+24);
        map.put("memo","加盟商调度员手动取消");
        listStr.add("memo=加盟商调度员手动取消");
        map.put("cancelType",15); //1-乘客；2-平台；3-客服；4-司机
        listStr.add("cancelType=15");
        map.put("cancelStatus",11);//平台取消任意值
        listStr.add("cancelStatus=11");


        Collections.sort(listStr);
        listStr.add("key="+Common.MAIN_ORDER_KEY);

        StringBuilder sb = new StringBuilder();
        for(String str : listStr){
            sb.append(str).append("&");
        }
        String param = sb.toString().substring(0,sb.toString().length()-1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(param));
        map.put("sign",sign);

        String url = "/order/carpool/inDispatcherCancelSubOrder";
        String result = carRestTemplate.postForObject(url,JSONObject.class,map);
        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult != null && jsonResult.get("code") != null && jsonResult.getInteger("code") == 1){
                logger.info("取消订单成功");
                return  AjaxResponse.success(null);
            }
        }
        return AjaxResponse.fail(RestErrorCode.CANCEL_FAILED);
    }





    /**
     * 查询主单
     * @param supplierId
     * @param driverName
     * @param driverPhone
     * @param license
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryDriver")
    public AjaxResponse queryDriver(Integer supplierId,
                                    String driverName,
                                    String driverPhone,
                                    String license){

        logger.info(MessageFormat.format("查询城际拼车司机入参：supplierId:{0},driverName:{1},driverPhpne:{2},license:{3}",supplierId,
                driverName,driverPhone,license));
        List<DriverInfoInterCity> interCityList = infoInterCityExMapper.queryDriver(supplierId,driverName,driverPhone,license);
        //剩余车位数
        /*for(DriverInfoInterCity infoInterCity : interCityList){
            String url = esOrderDataSaasUrl + "/order/carpool/getCrossCityMainOrder";
            Map<String,Object> map = Maps.newHashMap();
            map.put("mainOrderNo","");
            map.put("sign","sign");
            map.put("businessId","bussinessId");
            String result = MpOkHttpUtil.okHttpGet(url,map,0,null);
            if(StringUtils.isNotBlank(result)){
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject.get("code") != null && jsonObject.getInteger("code") == 0){
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    Integer userSeat = jsonData.getInteger("seat");
                    //如果是商务6座，默认是6个位置否则是4个
                    *//*if(infoInterCity.get){

                    }*//*
                }
            }
        }*/

        return AjaxResponse.success(interCityList);
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
   /* @RequestMapping(value = "/addMainOrder",method = RequestMethod.POST)
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
    }*/

    //获取行程接口
    public AjaxResponse getTrips(String boardingGetOnX,
                                 String boardingGetOnY){
        //获取行程  调用配置中心的接口

        String reqUrl = configUrl+"/intercityCarUse/getLineSupplier";

        Map<String,Object> map = Maps.newHashMap();
        map.put("areaStartRangeId",boardingGetOnX);
        map.put("areaEndRangeId",boardingGetOnY);


        String result = MpOkHttpUtil.okHttpGet(reqUrl,map,0,null);
        if(StringUtils.isNotEmpty(result)){

        }

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
    @RequestMapping("/handOperateAddOrderSetp2")
    @ResponseBody
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
        List<String> listParam = new ArrayList<>();
         map.put("businessId",Common.BUSSINESSID);
        listParam.add("businessId="+ Common.BUSSINESSID);
       /*map.put("mainOrderNo",mainOrderNo);
        listParam.add("mainOrderNo="+mainOrderNo);*/
        map.put("orderNo",orderNo);
        listParam.add("orderNo="+orderNo);
        map.put("driverId",driverId);
        listParam.add("driverId="+driverId);
        map.put("driverName",driverName);
        listParam.add("driverName="+driverName);
        map.put("driverPhone",driverPhone);
        listParam.add("driverPhone="+driverPhone);
        map.put("licensePlates",licensePlates);
        listParam.add("licensePlates="+licensePlates);
        map.put("carGroupId",carGroupId);
        listParam.add("carGroupId="+carGroupId);
        int carSeatNums = seatCount(carGroupId);
        map.put("carSeatNums",carSeatNums);
        listParam.add("carSeatNums="+carSeatNums);


        Collections.sort(listParam);
        listParam.add("key="+Common.MAIN_ORDER_KEY);


        StringBuilder sbSort = new StringBuilder();
        for(String str : listParam){
            sbSort.append(str).append("&");
        }

        String md5Before = sbSort.toString().substring(0,sbSort.toString().length()-1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(md5Before));
        map.put("sign",sign);


        String url = "/order/carpool/bingCrossCityMainOrder";
        String result = carRestTemplate.postForObject(url,JSONObject.class,map);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                logger.info("子单绑定主单成功");
                return AjaxResponse.success(null);
            }
        }

        return AjaxResponse.fail(RestErrorCode.BIND_MAIN_FAILED);
    }





    /**
     * 改派
     * @param mainOrderNo
     * @param orderNo
     * @param driverId
     * @param driverPhone
     * @param licensePlates
     * @param carGroupId
     * @return
     */
    @RequestMapping("/updateOtherMainOrder")
    @ResponseBody
    public AjaxResponse updateOtherMainOrder(String mainOrderNo,
                                                 String orderNo,
                                                 Integer driverId,
                                                 String driverPhone,
                                                 String licensePlates,
                                                 Integer cityId,
                                                 String carGroupId,
                                                 String crossCityStartTime,
                                                 String routeName){
        //派单
        logger.info("派单接口入参:");
        Map<String,Object> map = Maps.newHashMap();
        List<String> listParam = new ArrayList<>();
        map.put("businessId",Common.BUSSINESSID);
        listParam.add("businessId="+ Common.BUSSINESSID);
        if(StringUtils.isNotEmpty(mainOrderNo)){
            map.put("mainOrderNo",mainOrderNo);
            listParam.add("mainOrderNo="+mainOrderNo);
        }
        map.put("orderNo",orderNo);
        listParam.add("orderNo="+orderNo);
        map.put("driverId",driverId);
        listParam.add("driverId="+driverId);
         map.put("licensePlates",licensePlates);
        listParam.add("licensePlates="+licensePlates);
       map.put("groupId",carGroupId);
        listParam.add("groupId="+carGroupId);
        map.put("cityId",cityId);
        listParam.add("cityId="+cityId);
        /*if(StringUtils.isNotEmpty(crossCityStartTime)){

            Date date = DateUtils.parseDateStr(crossCityStartTime,"yyyy-MM-dd HH:mm:ss");
            map.put("crossCityStartTime",date);
            listParam.add("crossCityStartTime="+date);
        }*/
        map.put("routeName",routeName);
        listParam.add("routeName="+routeName);
        map.put("driverPhone",driverPhone);
        listParam.add("driverPhone="+driverPhone);

        Collections.sort(listParam);
        listParam.add("key="+Common.MAIN_ORDER_KEY);


        StringBuilder sbSort = new StringBuilder();
        for(String str : listParam){
            sbSort.append(str).append("&");
        }

        String md5Before = sbSort.toString().substring(0,sbSort.toString().length()-1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(md5Before));
        map.put("sign",sign);


        String url = "/order/carpool/crossCityOrderReassign";
        System.out.println(url);

        String result = carRestTemplate.postForObject(url,JSONObject.class,map);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                logger.info("改派成功");
                return AjaxResponse.success(null);
            }
        }

        return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
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

    private List<String> list(String str){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        List<String> strList = new ArrayList<>();
        String[] strArr = str.split("&");
        for(int i = 0;i<strArr.length;i++){
            strList.add(strArr[i]);
        }
       return strList;
    }
}
