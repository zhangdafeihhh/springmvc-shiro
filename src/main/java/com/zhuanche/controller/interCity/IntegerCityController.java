package com.zhuanche.controller.interCity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.enums.OrderStateEnum;
import com.zhuanche.common.util.LbsSignUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.InterCityOrderDTO;
import com.zhuanche.dto.mdbcarmanage.MainOrderDetailDTO;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.interCity.MainOrderInterService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.*;
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



    @Value("${lbs.token}")
    private String lbsToken;


    @Value("${search.url}")
    private String searchUrl;

    @Value("${center.url}")
    private String centerUrl;

    @Autowired
    private DriverInfoInterCityExMapper infoInterCityExMapper;


    @Autowired
    private MainOrderInterService interService;

    private static final String SYSMOL = "&";

    /**
     * 订单查询
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState 待指派、待服务、已出发、已上车、服务中、已送达、已完成、已取消
     * @param pushDriverType 订单指派方式  绑单、抢单；
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
                                   Integer pushDriverType,
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
                pageSize,cityId,supplierId,orderState,pushDriverType,serviceType,orderType,airportId,orderSource,
                driverName,driverPhone,licensePlates,reserveName,reservePhone,riderName,orderNo,mainOrderNo,beginCreateDate,
                endCreateDate,beginCostStartDate,beginCostEndDate,riderPhone));

        Map<String,Object> map = Maps.newHashMap();
        map.put("pageNo",pageNum);
        map.put("pageSize",pageSize);
        map.put("cityId",cityId);
        map.put("supplierId",supplierId);
        map.put("orderState",orderState);
        map.put("orderPushDriverType",pushDriverType);
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

        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue("code");
            //0表示有结果返回
            if(code == 0){
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if(jsonData != null && jsonData.get("data") != null) {
                    return AjaxResponse.success(jsonData);
                }
            }
        }
        //调用订单组接口查询
        return AjaxResponse.success(null);
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
                                                 @Verify(param = "reservePhone",rule = "required") String reservePhone,
                                                 @Verify(param = "isSameRider",rule = "required")Integer isSameRider,
                                                 String riderName,
                                                 String riderPhone,
                                                 @Verify(param = "riderCount",rule = "required")Integer riderCount,
                                                 @Verify(param = "boardingTime",rule = "required")String boardingTime,
                                                 @Verify(param = "boardingCityId",rule = "required")Integer boardingCityId,
                                                 @Verify(param = "boardingGetOnX",rule = "required")String boardingGetOnX,
                                                 @Verify(param = "boardingGetOnY",rule = "required")String boardingGetOnY,
                                                 @Verify(param = "boardingGetOffCityId",rule = "required")String boardingGetOffCityId,
                                                 @Verify(param = "boardingGetOffX",rule = "required")String boardingGetOffX,
                                                 @Verify(param = "boardingGetOffY",rule = "required")String boardingGetOffY,
                                                 @Verify(param = "startCityName",rule = "required")String startCityName,
                                                 @Verify(param = "endCityName",rule = "required")String endCityName
                                            ){
        logger.info(MessageFormat.format("手动录入订单步骤1入参,{0},{1},{2},{3},{4},{5},{6},{7}",reserveName,reservePhone,
                isSameRider,riderName,riderPhone,riderCount,boardingTime,boardingCityId,boardingGetOnX,boardingGetOnY,boardingGetOffCityId,
                boardingGetOffX,boardingGetOffY));


        //根据横纵坐标获取围栏，根据围栏获取路线
        Map<String,Object> mapX = Maps.newHashMap();

        mapX.put("token",lbsToken);

        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cityId",boardingCityId);
        jsonObject.put("x",boardingGetOnX);
        jsonObject.put("y",boardingGetOnY);
        array.add(jsonObject);
        mapX.put("coordinateList",array.toString());
        String lbsSign = LbsSignUtil.sign(mapX,lbsToken);
        mapX.put("sign",lbsSign);
        String lbsResult = MpOkHttpUtil.okHttpPost(lbsUrl + "/area/getAreaByCoordinate",mapX,0,null);
        String getOnId = "";
        if(StringUtils.isNotEmpty(lbsResult)){
            JSONObject jsonResult = JSONObject.parseObject(lbsResult);
            if(jsonResult.get("code") == null || jsonResult.getInteger("code") != 0 ||
                    jsonResult.get("data") == null) {
                logger.info("获取上车点失败");
                return AjaxResponse.fail(RestErrorCode.GET_ON_ADDRESS_FAILED);
            }

            JSONArray arrayData = jsonResult.getJSONArray("data");
            if(arrayData == null){
                logger.info("获取上车区域失败");
                return AjaxResponse.fail(RestErrorCode.GET_ON_ADDRESS_FAILED);
            }

            for(int i  =0;i<arrayData.size();i++){
                JSONObject lbsRes = arrayData.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOnId = lbsRes.getString("areaId");
                    break;
                }

            }
        }

        Map<String,Object> mapY = Maps.newHashMap();

        mapY.put("token",lbsToken);
        JSONArray arrayY = new JSONArray();
        JSONObject jsonY = new JSONObject();
        jsonY.put("cityId",boardingGetOffCityId);
        jsonY.put("x",boardingGetOffX);
        jsonY.put("y",boardingGetOffY);
        arrayY.add(jsonY);
        mapY.put("coordinateList",arrayY.toString());
        String lbsSignY = LbsSignUtil.sign(mapY,lbsToken);
        mapY.put("sign",lbsSignY);

        String resultY = MpOkHttpUtil.okHttpGet(lbsUrl+"/area/getAreaByCoordinate",mapY,0,null);

        String getOffId = "";

        if(resultY != null ){
            JSONObject jsonResultY = JSONObject.parseObject(resultY);
            if(jsonResultY.get("code") == null || jsonResultY.getInteger("code") != 0 ||
                    jsonResultY.get("data") == null){
                 logger.info("获取下车点失败");
                return AjaxResponse.fail(RestErrorCode.GET_OFF_ADDRESS_FAILED);
            }

            JSONArray jsonArray = jsonResultY.getJSONArray("data");
            if(jsonArray.size() == 0){
                logger.info("获取下车点失败");
                return AjaxResponse.fail(RestErrorCode.GET_OFF_ADDRESS_FAILED);
            }


            for(int i  =0;i<jsonArray.size();i++){
                JSONObject lbsRes = jsonArray.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOffId = lbsRes.getString("areaId");
                    break;
                }
            }
        }

        if(StringUtils.isEmpty(getOnId) || StringUtils.isEmpty(getOffId)){
            logger.info("上下车点不再围栏区域");
            return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
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

       //根据乘客人获取乘客userId
        Integer customerId = 0;
        if(StringUtils.isNotEmpty(reservePhone)){
            String url =  "/api/customer/regist";
            Map<String,Object> map = Maps.newHashMap();
            map.put("phone",reservePhone);
            map.put("registerSource",2);
            map.put("channelNum","新城际订单渠道");
            //获取乘客id
            String registerResult = MpOkHttpUtil.okHttpPost(centerUrl+url,map,0,null);
            if(StringUtils.isNotEmpty(registerResult)){
                JSONObject jsonResult = JSONObject.parseObject(registerResult);
                if(jsonResult.get("code") == null || jsonResult.getInteger("code") != 0){
                    logger.info("根据乘客获取customer失败");
                    return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
                }
                JSONObject data = jsonResult.getJSONObject("data");
                customerId = data.getInteger("customerId");
            }
        }

        if(customerId == null || customerId  == 0 ){
            logger.info("根据乘客获取customer失败");
            return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
        }

        Map<String,Object> map = Maps.newHashMap();
        Date bookDate = DateUtils.getDate(boardingTime,"yyyy-MM-dd HH:mm:ss");

        long bookingDate = bookDate.getTime();
        StringBuffer sb = new StringBuffer();
        map.put("businessId",Common.BUSSINESSID);//业务线ID
        sb.append("businessId=" + Common.BUSSINESSID).append(SYSMOL);
        map.put("type","1");//普通用户订单
        sb.append("type=1").append(SYSMOL);
        map.put("bookingUserName",reserveName);
        sb.append("bookingUserName="+reserveName).append(SYSMOL);
        map.put("clientType",28);//订单类型
        sb.append("clientType=28").append(SYSMOL);
        map.put("bookingDate", bookingDate);//预定日期（时间戳）
        sb.append("bookingDate=" + bookingDate).append(SYSMOL);
        if("1".equals(isSameRider)){
            map.put("riderName",reserveName);
            sb.append("riderName=" + reserveName).append(SYSMOL);
            map.put("riderPhone",reservePhone);
            sb.append("riderPhone=" + reservePhone).append(SYSMOL);
        }else {
            map.put("riderName",reserveName);
            sb.append("riderName=" + reserveName).append(SYSMOL);
            map.put("riderPhone",reservePhone);
            sb.append("riderPhone=" + reservePhone).append(SYSMOL);
        }

        map.put("cityId",boardingCityId);
        sb.append("cityId=" +boardingCityId).append(SYSMOL);
        map.put("serviceTypeId",68);
        sb.append("serviceTypeId=68").append(SYSMOL);
        map.put("payFlag","1");//付款人标识 0-预订人付款，1-乘车人付款，2-门童代人叫车乘车人是自己且乘车人付款，-1-机构付款
        sb.append("payFlag=1").append(SYSMOL);
        map.put("receiveSMS","2");//是否接收短信 “1”-接收，“2”-不接收
        sb.append("receiveSMS=2").append(SYSMOL);
        map.put("bookingDriverId","0");//
        sb.append("bookingDriverId=0").append(SYSMOL);//
        map.put("bookingCurrentAddr","1");
        sb.append("bookingCurrentAddr=1").append(SYSMOL);
        map.put("bookingCurrentPoint","1");
        sb.append("bookingCurrentPoint=1").append(SYSMOL);
        map.put("channelsNum","1");
        sb.append("channelsNum=1").append(SYSMOL);
        map.put("version","1");
        sb.append("version=1").append(SYSMOL);
        map.put("imei","1");
        sb.append("imei=1").append(SYSMOL);
        map.put("coordinate","GD");
        sb.append("coordinate=GD").append(SYSMOL);
        map.put("bookingUserId",customerId);
        sb.append("bookingUserId=" + customerId).append(SYSMOL);
        map.put("bookingUserPhone",reservePhone);
        sb.append("bookingUserPhone=" + reservePhone).append(SYSMOL);
        map.put("buyoutFlag","0");
        sb.append("buyoutFlag=0").append(SYSMOL);
        map.put("buyoutPrice","1");
        sb.append("buyoutPrice=1").append(SYSMOL);
        map.put("carpoolMark",1);//拼车标识(0:不拼车，1:拼车)
        sb.append("carpoolMark=1").append(SYSMOL);
        map.put("seats",riderCount);
        sb.append("seats=" + riderCount).append(SYSMOL);
        map.put("ruleId",1);
        sb.append("ruleId=1").append(SYSMOL);
        map.put("couponId","111");
        sb.append("couponId=111").append(SYSMOL);
        map.put("estimatedAmount","111");//预估金额
        sb.append("estimatedAmount=111").append(SYSMOL);

        map.put("startCityId",boardingCityId);
        sb.append("startCityId="+boardingCityId).append(SYSMOL);
        map.put("startCityName",startCityName);
        sb.append("startCityName="+startCityName).append(SYSMOL);
        map.put("endCityId",boardingGetOffCityId);
        sb.append("endCityId="+boardingGetOffCityId).append(SYSMOL);
        map.put("endCityName",endCityName);
        sb.append("endCityName="+endCityName).append(SYSMOL);

        String getOn = boardingGetOnX + ";" + boardingGetOnY;
        String getOff = boardingGetOffX + ";" + boardingGetOffY;
        map.put("bookingStartPoint",getOn);
        sb.append("bookingStartPoint="+getOn).append(SYSMOL);
        map.put("bookingEndPoint",getOff);
        sb.append("bookingEndPoint="+getOff).append(SYSMOL);


        List<String> list = this.list(sb.toString());
        Collections.sort(list);
        list.add("key="+Common.MAIN_ORDER_KEY);
        StringBuilder sbSort = new StringBuilder();
        for(String str : list){
            sbSort.append(str).append(SYSMOL);
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
                String bookingUserPhone = jsonData.get("bookingUserPhone")==null?"":jsonData.getString("bookingUserPhone");
                String riderPhone = jsonData.get("riderPhone") == null ? "" : jsonData.getString("riderPhone");
                dto.setReserveName(jsonData.get("bookingUserName") == null ? null : jsonData.getString("bookingUserName"));
                dto.setReservePhone(bookingUserPhone);
                dto.setRiderName(jsonData.get("riderName") == null ?"":jsonData.getString("riderName"));
                dto.setRiderPhone(riderPhone);
                dto.setRiderCount(jsonData.get("factDriverId") == null ? 0 : jsonData.getInteger("factDriverId"));
                dto.setBoardingTime(jsonData.get("bookingDate") == null ? "" : jsonData.getString("bookingDate"));
                dto.setBoardingGetOffCityId(jsonData.get("bookingEndAddr") == null ? "" : jsonData.getString("bookingEndAddr"));
                dto.setBookingStartPoint(jsonData.get("bookingStartPoint") == null ? "" : jsonData.getString("bookingStartPoint"));
                dto.setBookingEndPoint(jsonData.get("bookingEndPoint") == null ? "" : jsonData.getString("bookingEndPoint"));
                dto.setStatus(jsonData.get("status") == null ? null : jsonData.getInteger("status"));
                dto.setOrderTime(jsonData.get("createDate") == null ? null : jsonData.getString("createDate"));
                dto.setType(jsonData.get("type") == null ? null : jsonData.getInteger("type"));
                dto.setBoardOnAddr(jsonData.get("bookingStartAddr") == null ? null : jsonData.getString("bookingStartAddr"));
                dto.setBoardOffAddr(jsonData.get("bookingEndAddr") == null ? null : jsonData.getString("bookingEndAddr"));
                dto.setCityId(jsonData.get("cityId") == null ? null : jsonData.getInteger("cityId"));

                if(jsonData != null && jsonData.get("memo") != null){
                    JSONObject jsonMemo = jsonData.getJSONObject("memo");
                    dto.setBoardingCityId(jsonMemo.get("startCityId") == null ? "":jsonMemo.getString("startCityId"));
                    dto.setBoardingGetOffCityId(jsonMemo.get("endCityId") == null ?"":jsonMemo.getString("endCityId"));
                    dto.setBoardingCityName(jsonMemo.get("startCityName") == null ? "":jsonMemo.getString("startCityName"));
                    dto.setBoardingGetOffCityName(jsonMemo.get("endCityName") == null ? "":jsonMemo.getString("endCityName"));
                }
                if(StringUtils.isNotEmpty(bookingUserPhone) && StringUtils.isNotEmpty(riderPhone)){
                    if(bookingUserPhone.equals(riderPhone)){
                        dto.setIsSameRider(1);//1相同 0 不同
                    }else {
                        dto.setIsSameRider(0);
                    }
                }else {
                    dto.setIsSameRider(0);
                }
                //获取预订人
                Map<String,Object> bookMap = Maps.newHashMap();
                bookMap.put("orderNo",orderNo);
                bookMap.put("name","bookingUserName");
                String bookingResult = MpOkHttpUtil.okHttpGet(orderServiceUrl+"/order/byFields/find",bookMap,0,null);
                if(StringUtils.isNotEmpty(bookingResult)){
                    JSONObject jsonBook = JSONObject.parseObject(bookingResult);
                    if(jsonBook.get("code") != null && jsonBook.getInteger("code") == 0){
                        JSONObject jsonBookData = jsonBook.getJSONObject("data");
                        if(jsonBookData != null && jsonBookData.get("bookingUserName") != null){
                            String bookingUserName = jsonBookData.getString("bookingUserName");
                            dto.setReserveName(bookingUserName);
                        }

                    }
                }

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
                                  String reserveName,
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

        //根据横纵坐标获取围栏，根据围栏获取路线
        Map<String,Object> mapX = Maps.newHashMap();

        mapX.put("token",lbsToken);

        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cityId",boardingCityId);
        jsonObject.put("x",boardingGetOnX);
        jsonObject.put("y",boardingGetOnY);
        array.add(jsonObject);
        mapX.put("coordinateList",array.toString());
        String lbsSign = LbsSignUtil.sign(mapX,lbsToken);
        mapX.put("sign",lbsSign);
        String lbsResult = MpOkHttpUtil.okHttpPost(lbsUrl + "/area/getAreaByCoordinate",mapX,0,null);
        String getOnId = "";
        if(StringUtils.isNotEmpty(lbsResult)){
            JSONObject jsonResult = JSONObject.parseObject(lbsResult);
            if(jsonResult.get("code") == null || jsonResult.getInteger("code") != 0 ||
                    jsonResult.get("data") == null) {
                logger.info("获取上车点失败");
                return AjaxResponse.fail(RestErrorCode.GET_ON_ADDRESS_FAILED);
            }

            JSONArray arrayData = jsonResult.getJSONArray("data");
            if(arrayData == null){
                logger.info("获取上车区域失败");
                return AjaxResponse.fail(RestErrorCode.GET_ON_ADDRESS_FAILED);
            }

            for(int i  =0;i<arrayData.size();i++){
                JSONObject lbsRes = arrayData.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOnId = lbsRes.getString("areaId");
                    break;
                }

            }
        }

        Map<String,Object> mapY = Maps.newHashMap();

        mapY.put("token",lbsToken);
        JSONArray arrayY = new JSONArray();
        JSONObject jsonY = new JSONObject();
        jsonY.put("cityId",boardingGetOffCityId);
        jsonY.put("x",boardingGetOffX);
        jsonY.put("y",boardingGetOffY);
        arrayY.add(jsonY);
        mapY.put("coordinateList",arrayY.toString());
        String lbsSignY = LbsSignUtil.sign(mapY,lbsToken);
        mapY.put("sign",lbsSignY);

        String resultY = MpOkHttpUtil.okHttpGet(lbsUrl+"/area/getAreaByCoordinate",mapY,0,null);

        String getOffId = "";

        if(resultY != null ){
            JSONObject jsonResultY = JSONObject.parseObject(resultY);
            if(jsonResultY.get("code") == null || jsonResultY.getInteger("code") != 0 ||
                    jsonResultY.get("data") == null){
                logger.info("获取下车点失败");
                return AjaxResponse.fail(RestErrorCode.GET_OFF_ADDRESS_FAILED);
            }

            JSONArray jsonArray = jsonResultY.getJSONArray("data");
            if(jsonArray.size() == 0){
                logger.info("获取下车点失败");
                return AjaxResponse.fail(RestErrorCode.GET_OFF_ADDRESS_FAILED);
            }


            for(int i  =0;i<jsonArray.size();i++){
                JSONObject lbsRes = jsonArray.getJSONObject(0);
                if(lbsRes.get("areaId") != null){
                    getOffId = lbsRes.getString("areaId");
                    break;
                }
            }
        }

        if(StringUtils.isNotEmpty(getOnId) && StringUtils.isNotEmpty(getOffId)){
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
        }



        String getOn = boardingGetOnX + ";" + boardingGetOnY;
        String getOff = boardingGetOffX + ";" + boardingGetOffY;

        Map<String,Object> map = Maps.newHashMap();
        List<String> list = new ArrayList<>();
        map.put("businessId",Common.BUSSINESSID);
        list.add("businessId="+Common.BUSSINESSID);
        map.put("orderNo",orderNo);
        list.add("orderNo="+orderNo);
        if(StringUtils.isNotEmpty(getOn)){
            map.put("factStartPoint",getOn);//根据坐标轴获取围栏id
            list.add("factStartPoint="+getOn);
        }

        if(StringUtils.isNotEmpty(getOff)){
            map.put("factEndPoint",getOff);
            list.add("factEndPoint="+getOff);
        }


        if(StringUtils.isNotEmpty(mainOrderNo)){
            map.put("mainOrderNo",mainOrderNo);
            list.add("mainOrderNo="+mainOrderNo);
        }

        map.put("pushDriverType", OrderStateEnum.MANUAL_ASSIGNMENT.getCode());//订单指派司机类型(1 系统强派| 2 司机抢单| 3 后台人工指派)
        list.add("pushDriverType="+OrderStateEnum.MANUAL_ASSIGNMENT.getCode());

        if(boardingCityId != null){
            map.put("factStartAddr",boardingCityId);
            list.add("factStartAddr="+boardingCityId);
        }


        if(StringUtils.isNotEmpty(boardingGetOffCityId)){
            map.put("factEndAddr",boardingGetOffCityId);//
            list.add("factEndAddr="+boardingGetOffCityId);
        }


        if(riderCount != null){
            map.put("factDriverId",riderCount);
            list.add("factDriverId="+riderCount);
        }


        if(StringUtils.isNotEmpty(reservePhone)){
            map.put("bookingUserPhone",reservePhone);
            list.add("bookingUserPhone="+reservePhone);
        }


        if("1".equals(isSameRider)){
            map.put("riderName",reserveName);
            list.add("riderName=" + reserveName);
            map.put("riderPhone",reservePhone);
            list.add("riderPhone=" + reservePhone);
        }else {
            map.put("riderName",riderName);
            list.add("riderName=" + riderName);
            map.put("riderPhone",riderPhone);
            list.add("riderPhone=" + riderPhone);
        }

        if(StringUtils.isNotEmpty(riderName)){
            map.put("riderName",riderName);
            list.add("riderName="+riderName);
        }


        if(StringUtils.isNotEmpty(riderPhone)){
            map.put("riderPhone",riderPhone);
            list.add("riderPhone="+riderPhone);
        }


        if(StringUtils.isNotEmpty(boardingTime)){
            Date crossCityStartTime = DateUtils.getDate(boardingTime,"yyyy-MM-dd HH:mm:ss");
            map.put("crossCityStartTime",crossCityStartTime.getTime());
            list.add("crossCityStartTime="+crossCityStartTime.getTime());
        }



        if(boardingCityId != null){
            map.put("startCityId",boardingCityId);
            list.add("startCityId="+boardingCityId);
        }
        if(boardingGetOffCityId  != null){
            map.put("endCityId", boardingGetOffCityId);
            list.add("endCityId="+boardingGetOffCityId);
        }
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
        String url =  "/order/carpool/updateCrossCityOrder";
        String editResult = carRestTemplate.postForObject(url,JSONObject.class,map);
        logger.info("编辑结果：" + editResult);
        if(StringUtils.isNotBlank(editResult)){
            JSONObject updateResult = JSONObject.parseObject(editResult);
            if(updateResult.get("code") != null && updateResult.getInteger("code") == 0){
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
     * 查询司机
     * @param supplierId
     * @param driverName
     * @param driverPhone
     * @param license
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryDriver")
    public AjaxResponse queryDriver(Integer cityId,
                                    Integer supplierId,
                                    String driverName,
                                    String driverPhone,
                                    String license){

        logger.info(MessageFormat.format("查询城际拼车司机入参：supplierId:{0},driverName:{1},driverPhpne:{2},license:{3}",supplierId,
                driverName,driverPhone,license));
        List<MainOrderDetailDTO> interCityList = infoInterCityExMapper.queryDriver(supplierId,driverName,driverPhone,license);
        //剩余车位数
        String url = "/order/carpool/getCrossCityMainOrder";

        for(MainOrderDetailDTO detailDTO : interCityList){
            if(StringUtils.isNotEmpty(detailDTO.getMainOrder())){
                StringBuilder sb = new StringBuilder();
                sb.append("businessId="+Common.BUSSINESSID+"&mainOrderNo="+detailDTO.getMainOrder()).append("&key="+Common.MAIN_ORDER_KEY);
                String sign = Base64.encodeBase64String(DigestUtils.md5(sb.toString()));
                url += "?businessId="+Common.BUSSINESSID+"&mainOrderNo="+detailDTO.getMainOrder()+"&sign="+sign;
                JSONObject jsonResult = carRestTemplate.getForObject(url,JSONObject.class);
                if(StringUtils.isNotBlank(jsonResult.toString())){
                    if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                        JSONObject jsonData = jsonResult.getJSONObject("data");
                        Integer passengerNums = jsonData.getInteger("passengerNums");
                        Integer groupId = jsonData.getInteger("groupId");
                        //如果是商务6座，默认是6个位置否则是4个
                        Integer maxSeat = this.seatCount(groupId.toString());
                        //剩余座位数
                        Integer remainSeat = maxSeat-passengerNums;
                        if(remainSeat<=0){
                            remainSeat = 0;
                        }
                        detailDTO.setRemainSeats(remainSeat);
                    }
                }
            } else {
                detailDTO.setRemainSeats(seatCount(detailDTO.getGroupId().toString()));
            }
        }

        return AjaxResponse.success(interCityList);
    }



    /**
     * 修改主单
     * @param mainTime
     * @return
     */
    @RequestMapping(value = "/addMainOrder",method = RequestMethod.POST)
    public AjaxResponse addMainOrder(String mainOrderNo,
                                     String mainTime){

        logger.info("添加主单接口");
        //修改订单接口
        int code = interService.updateMainTime(mainOrderNo,mainTime);
        if(code>0){
            logger.info("修改主订单接口成功");
            return AjaxResponse.success(null);
        }

        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    /**
     * 指派
     * @param mainOrderNo
     * @param orderNo
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param groupId
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
                                                 String groupId,
                                                 String crossCityStartTime,
                                                 String routeName){
        //派单
        logger.info("派单接口入参:mainOrderNo="+ mainOrderNo + ",orderNo:"+orderNo
        +",driverId:"+driverId + ",driverName:"+ driverName + ",driverPhone:"+driverPhone+",licensePlates:"+licensePlates
        +",groupId:"+groupId+",crossCityStartTime:"+crossCityStartTime+",routeName:"+routeName);
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
        map.put("driverName",driverName);
        listParam.add("driverName="+driverName);
        map.put("driverPhone",driverPhone);
        listParam.add("driverPhone="+driverPhone);
        map.put("licensePlates",licensePlates);
        listParam.add("licensePlates="+licensePlates);
        if(StringUtils.isNotEmpty(groupId)){
            map.put("carGroupId",groupId);
            listParam.add("carGroupId="+groupId);
            int carSeatNums = seatCount(groupId);
            map.put("carSeatNums",carSeatNums);
            listParam.add("carSeatNums="+carSeatNums);
        }

        if(StringUtils.isNotEmpty(crossCityStartTime)){
            map.put("crossCityStartTime",crossCityStartTime);
            listParam.add("crossCityStartTime="+crossCityStartTime);
        }

        if(StringUtils.isNotEmpty(routeName)){
            map.put("routeName",crossCityStartTime);
            listParam.add("routeName="+routeName);
        }

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
                JSONObject jsonData = jsonResult.getJSONObject("data");
                if(jsonData != null && jsonData.get("mainOrderNo") != null){
                    MainOrderInterCity main = new MainOrderInterCity();
                    main.setDriverId(driverId);
                    //main.setMainName(driverName);
                    main.setMainOrderNo(jsonData.getString("mainOrderNo"));
                    SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
                    main.setOpePhone(user.getMobile());
                    int code = interService.addMainOrderNo(main);
                    if(code > 0){
                        return AjaxResponse.success(null);
                    }

                    return AjaxResponse.fail(RestErrorCode.FAILED_GET_MAIN_ORDER);
                }

                return AjaxResponse.fail(RestErrorCode.FAILED_GET_MAIN_ORDER);
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
        logger.info("改派结果result:" + result);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                logger.info("改派成功");
                //如果是新增或者改派，需要将
                if(StringUtils.isNotEmpty(mainOrderNo)){
                    JSONObject jsonData = jsonResult.getJSONObject("data");
                    MainOrderInterCity main = new MainOrderInterCity();
                    main.setDriverId(driverId);
                    //main.setMainName(driverName);
                    main.setMainOrderNo(jsonData.getString("mainOrderNo"));
                    SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
                    main.setOpePhone(user.getMobile());
                    main.setMainTime(crossCityStartTime);
                    int code = interService.addMainOrderNo(main);
                    if(code > 0){
                        return AjaxResponse.success(null);
                    }
                    return AjaxResponse.fail(RestErrorCode.FAILED_GET_MAIN_ORDER);
                }
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


    /**
     * 搜索接口
     * @param type 0(周边搜索) 2(精确查找）
     * @param platform H5 3
     * @param cityName 城市简称
     * @param cityId 城市id
     * @return
     */
    @ResponseBody
    @RequestMapping("/inputtips")
    public AjaxResponse inputtips(@Verify(param = "type",rule = "required")String type,
                                  @Verify(param = "platform",rule = "required")String platform,
                                  @Verify(param = "cityName",rule = "required")String cityName,
                                  @Verify(param = "cityId",rule = "required")String cityId,
                                  @Verify(param = "channel",rule = "required")String channel,
                                  String inCoordType,
                                  String cityLimit,
                                  String userId,
                                  String mobile,
                                  Integer soType,
                                  Integer serviceType,
                                  String lang,
                                  String version,
                                  String redPacket,
                                  String keywords){
        logger.info("精确查询");
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String,Object> map = Maps.newHashMap();
        map.put("type",type);
        map.put("platform",platform);
        map.put("channel",channel);
        map.put("sessionId",loginUser.getId()+"_" +System.currentTimeMillis());
        map.put("cityName",cityName);
        map.put("cityId",cityId);
        if(StringUtils.isNotEmpty(inCoordType)){
            map.put("inCoordType",inCoordType);
        }
        if(StringUtils.isNotEmpty(cityLimit)){
            map.put("cityLimit",cityLimit);
        }
        if(StringUtils.isNotEmpty(userId)){
            map.put("userId",userId);
        }
        if(StringUtils.isNotEmpty(mobile)){
            map.put("mobile",mobile);
        }
        if(soType != null){
            map.put("soType",soType);
        }
        if(serviceType != null){
            map.put("serviceType",serviceType);
        }
        if(StringUtils.isNotEmpty(lang)){
            map.put("lang",lang);
        }
        if(StringUtils.isNotEmpty(version)){
            map.put("version",version);
        }
        if(StringUtils.isNotEmpty(redPacket)){
            map.put("redPacket",redPacket);
        }
        if(StringUtils.isNotEmpty(keywords)){
            map.put("keywords",keywords);
        }

        if(type!= null && ("1".equals(type) || "2".equals(type))){
            if(StringUtils.isEmpty(keywords)){
                return AjaxResponse.fail(RestErrorCode.KEYWORDS_IS_NOT_NULL);
            }
        }
        String url = searchUrl+"/assistant/inputtips";

        String result = MpOkHttpUtil.okHttpGet(url,map,0,null);
        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get("code") != null && jsonResult.getInteger("code")==0){
                return AjaxResponse.success(jsonResult.get("data"));
            }else {
                return AjaxResponse.failMsg(jsonResult.getIntValue("code"),jsonResult.getString("msg"));
            }
        }
        return AjaxResponse.success(null);
    }



    @ResponseBody
    @RequestMapping(value = "/regeo",method = RequestMethod.GET)
    public AjaxResponse regeo(String lng,
                              String lat,
                              String platform){
        logger.info("查询逆地理编码");
        Map<String,Object> map = Maps.newHashMap();
        map.put("lng",lng);
        map.put("lat",lat);
        map.put("platform",platform);
        String url = searchUrl + "/geocode/regeo";
        String result = MpOkHttpUtil.okHttpGet(url,map,0,null);
        if(StringUtils.isNotEmpty(result)){
            JSONObject jsonResult = JSONObject.parseObject(result);
            if(jsonResult.get("code") != null && jsonResult.getInteger("code")==0){
                return AjaxResponse.success(jsonResult.get("data"));
            }else {
                return AjaxResponse.failMsg(jsonResult.getIntValue("code"),jsonResult.getString("msg"));
            }
        }
        return AjaxResponse.success(null);
    }
}
