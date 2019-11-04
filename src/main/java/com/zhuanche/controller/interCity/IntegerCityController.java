package com.zhuanche.controller.interCity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.enums.OrderStateEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.util.LbsSignUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.mdbcarmanage.InterCityOrderDTO;
import com.zhuanche.dto.mdbcarmanage.MainOrderDetailDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.interCity.MainOrderInterService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.*;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
    private String esOrderDataSaasUrl;

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
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    @Autowired
    private MainOrderInterService interService;

    @Autowired
    private CarFactOrderInfoService carFactOrderInfoService;
    @Autowired
    private CitySupplierTeamCommonService citySupplierTeamCommonService;
    @Autowired
    private CarBizSupplierService carBizSupplierService;


    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Value("${ordercost.server.api.base.url}")
    private String orderCostUrl;

    private static final String SYSMOL = "&";

    private static final String SPLIT = ",";

    /**
     * 订单查询 wiki：http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31813392
     *
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState         待指派、待服务、已出发、已上车、服务中、已送达、已完成、已取消
     * @param pushDriverType     订单指派方式  绑单、抢单；
     * @param serviceType        服务类别: 新城际拼车、新城际包车
     * @param orderType          普通订单、机构用车
     * @param airportId          是否拼车单
     * @param orderSource        订单来源 线上订单、手动录单、扫码订单  订单查询时候无此字段
     * @param driverName         司机姓名
     * @param driverPhone        司机手机号
     * @param licensePlates      司机车牌号
     * @param reserveName        预订人名称
     * @param reservePhone       预订人手机号
     * @param riderName          乘车人名称
     * @param orderNo            子订单号
     * @param mainOrderNo        主订单号
     * @param beginCreateDate    下单开始时间
     * @param endCreateDate      下单结束时间
     * @param endCostEndDate 订单完成开始时间
     * @param beginCostEndDate   订单完成结束时间
     * @param riderPhone         乘车人手机号
     * @return
     */
    @RequestMapping(value = "/orderQuery", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse orderQuery(@Verify(param = "pageNum", rule = "required") Integer pageNum,
                                   @Verify(param = "pageSize", rule = "required") Integer pageSize,
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
                                   String beginCostEndDate,
                                   String endCostEndDate,
                                   String riderPhone) {
        logger.info(MessageFormat.format("订单查询入参:pageNum:{0},pageSize:{1},cityId:{2},supplierId:{3},orderState:" +
                        "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                        "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                        "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostEndDate{20},endCostEndDate{21},riderPhone:{22}", pageNum,
                pageSize, cityId, supplierId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate,
                endCreateDate, beginCostEndDate, endCostEndDate, riderPhone));


        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        String serviceCityBatch = "";
        String supplierIdBatch = "";

        Set<Integer> citiesSet = new HashSet<>();
        Set<Integer> suppliersSet = new HashSet<>();
        if (cityId != null) {
            citiesSet.add(cityId);
        } else {
            citiesSet = loginUser.getCityIds();
        }
        if (supplierId != null) {
            suppliersSet.add(supplierId);
        } else {
            suppliersSet = loginUser.getSupplierIds();
        }

        StringBuilder cityBuilder = new StringBuilder();
        if (citiesSet != null) {
            List<Integer> listCity = new ArrayList<>(citiesSet);
            for (int i = 0; i < listCity.size(); i++) {
                cityBuilder.append(listCity.get(i)).append(SPLIT);
            }
        }
        StringBuilder supplierBuilder = new StringBuilder();
        if (suppliersSet != null) {
            List<Integer> listSupplier = new ArrayList<>(suppliersSet);
            for (int i = 0; i < listSupplier.size(); i++) {
                supplierBuilder.append(listSupplier.get(i)).append(SPLIT);
            }
        }

        if (StringUtils.isNotEmpty(cityBuilder.toString())) {
            serviceCityBatch = cityBuilder.toString().substring(0, cityBuilder.toString().length() - 1);
        }

        if (StringUtils.isNotEmpty(supplierBuilder.toString())) {
            supplierIdBatch = supplierBuilder.toString().substring(0, supplierBuilder.toString().length() - 1);
        }


        Map<String, Object> map = Maps.newHashMap();
        map.put("pageNo", pageNum);
        map.put("pageSize", pageSize);
        /*map.put("cityId",cityId);
        map.put("supplierId",supplierId);*/
        map.put("status", orderState);
        map.put("pushDriverType", pushDriverType);
        map.put("serviceTypeIdBatch", 68);
        map.put("orderType", orderSource);
/*
        map.put("type",serviceType);
*/
        map.put("airportId", airportId);
        //map.put("orderSource",orderSource);
        map.put("driverName", driverName);
        map.put("driverPhone", driverPhone);
        map.put("licensePlates", licensePlates);
        map.put("bookingUserName", reserveName);
        map.put("bookingUserPhone", reservePhone);
        map.put("riderName", riderName);
        map.put("orderNo", orderNo);
        map.put("mainOrderNo", mainOrderNo);
        map.put("beginCreateDate", beginCreateDate);
        map.put("endCreateDate", endCreateDate);
        map.put("beginCostEndDate", beginCostEndDate);
        map.put("endCostEndDate", endCostEndDate);
        map.put("riderPhone", riderPhone);

        //根据不同权限添加过滤条件
        if (StringUtils.isNotEmpty(serviceCityBatch)) {
            map.put("cityIdBatch", serviceCityBatch);
        }

        if (StringUtils.isNotEmpty(supplierIdBatch)) {
            map.put("supplierIdBatch", supplierIdBatch);
        }

        /*if (serviceType == null) {
            map.put("serviceTypeIdBatch", "68");
        }*/
        //添加排序字段
        JSONObject jsonSort = new JSONObject();
        jsonSort.put("field", "createDate");
        jsonSort.put("operator", "desc");
        JSONArray arraySort = new JSONArray();
        arraySort.add(jsonSort);
        map.put("sort", arraySort.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String transId = sdf.format(new Date());
        map.put("transId", transId);
        String url = esOrderDataSaasUrl + "/order/v2/search";

        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue("code");
            //0表示有结果返回
            if (code == 0) {
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if (jsonData != null && jsonData.get("data") != null) {
                    return AjaxResponse.success(jsonData);
                }
            }
        }
        //调用订单组接口查询
        return AjaxResponse.success(null);
    }

    @RequestMapping(value = "/orderWrestQuery", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse orderWrestQuery(@Verify(param = "pageNum", rule = "required") Integer pageNum,
                                        @Verify(param = "pageSize", rule = "required") Integer pageSize,
                                        Integer cityId,
                                        //Integer supplierId,
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
                                        String riderPhone) {
        logger.info(MessageFormat.format("订单查询入参:pageNum:{0},pageSize:{1},cityId:{2},orderState:" +
                        "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                        "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                        "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostStartDate{20},beginCostEndDate{21},riderPhone:{22}", pageNum,
                pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate,
                endCreateDate, beginCostStartDate, beginCostEndDate, riderPhone));


        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        String serviceCityBatch = "";

        Set<Integer> citiesSet = new HashSet<>();

        if (cityId != null) {
            citiesSet.add(cityId);
        } else {
            citiesSet = loginUser.getCityIds();
        }
        StringBuilder cityBuilder = new StringBuilder();
        if (citiesSet != null) {
            List<Integer> listCity = new ArrayList<>(citiesSet);
            for (int i = 0; i < listCity.size(); i++) {
                cityBuilder.append(listCity.get(i)).append(SPLIT);
            }
        }

        if (StringUtils.isNotEmpty(cityBuilder.toString())) {
            serviceCityBatch = cityBuilder.toString().substring(0, cityBuilder.toString().length() - 1);
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("pageNo", pageNum);
        map.put("pageSize", pageSize);
        map.put("status", 13);
        map.put("pushDriverType", pushDriverType);
        map.put("orderType", orderSource);
        map.put("airportId", airportId);
        map.put("driverName", driverName);
        map.put("driverPhone", driverPhone);
        map.put("licensePlates", licensePlates);
        map.put("bookingUserName", reserveName);
        map.put("bookingUserPhone", reservePhone);
        map.put("riderName", riderName);
        map.put("orderNo", orderNo);
        map.put("mainOrderNo", mainOrderNo);
        map.put("beginCreateDate", beginCreateDate);
        map.put("endCreateDate", endCreateDate);
        map.put("beginCostEndDate", beginCostStartDate);
        map.put("endCostEndDate", beginCostEndDate);
        map.put("riderPhone", riderPhone);

        //根据不同权限添加过滤条件
        if (StringUtils.isNotEmpty(serviceCityBatch)) {
            map.put("cityIdBatch", serviceCityBatch);
        }


        map.put("serviceTypeIdBatch", "68");

        String supplierIdBatch = "";
        if (!WebSessionUtil.isSupperAdmin()) {
            Set<Integer> suppliersSet = loginUser.getSupplierIds();
            if (suppliersSet != null && suppliersSet.size() > 0) {
                StringBuilder supplierBuilder = new StringBuilder();
                for (Integer supplierId : suppliersSet) {
                    supplierBuilder.append(supplierId).append(SPLIT);
                }
                if (StringUtils.isNotEmpty(supplierBuilder.toString())) {
                    supplierIdBatch = supplierBuilder.toString().substring(0, supplierBuilder.toString().length() - 1);
                }

                String lineIds = this.getLineIdBySupplierIds(supplierIdBatch);
                if (StringUtils.isNotBlank(lineIds)) {
                    map.put("ruleIdBatch", lineIds);
                } else {
                    map.put("ruleIdBatch", "-1");
                }
            }
        }

        map.put("supplierIdBatch", "0");

        //添加排序字段
        JSONObject jsonSort = new JSONObject();
        jsonSort.put("field", "createDate");
        jsonSort.put("operator", "desc");
        JSONArray arraySort = new JSONArray();
        arraySort.add(jsonSort);
        map.put("sort", arraySort.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String transId = sdf.format(new Date());
        map.put("transId", transId);
        String url = esOrderDataSaasUrl + "/order/v2/search";

        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue("code");
            //0表示有结果返回
            if (code == 0) {
                JSONObject jsonData = jsonObject.getJSONObject("data");
                if (jsonData != null && jsonData.get("data") != null) {
                    return AjaxResponse.success(jsonData);
                }
            }
        }
        //调用订单组接口查询
        return AjaxResponse.success(null);
    }

    /**
     * 步骤1
     *
     * @param reserveName
     * @param reservePhone
     * @param isSameRider
     * @param riderName
     * @param riderPhone
     * @param riderCount           乘客数
     * @param boardingTime
     * @param boardingCityId
     * @param boardingGetOnX
     * @param boardingGetOnY
     * @param boardingGetOffCityId
     * @param boardingGetOffX
     * @param boardingGetOffY
     * @return
     */
    @RequestMapping(value = "/handOperateAddOrderSetp1", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse handOperateAddOrderSetp1(@Verify(param = "reserveName", rule = "required") String reserveName,
                                                 @Verify(param = "reservePhone", rule = "required") String reservePhone,
                                                 @Verify(param = "isSameRider", rule = "required") Integer isSameRider,
                                                 String riderName,
                                                 String riderPhone,
                                                 @Verify(param = "riderCount", rule = "required") Integer riderCount,
                                                 @Verify(param = "boardingTime", rule = "required") String boardingTime,
                                                 @Verify(param = "boardingCityId", rule = "required") Integer boardingCityId,
                                                 @Verify(param = "boardingGetOnX", rule = "required") String boardingGetOnX,
                                                 @Verify(param = "boardingGetOnY", rule = "required") String boardingGetOnY,
                                                 @Verify(param = "boardingGetOffCityId", rule = "required") Integer boardingGetOffCityId,
                                                 @Verify(param = "boardingGetOffX", rule = "required") String boardingGetOffX,
                                                 @Verify(param = "boardingGetOffY", rule = "required") String boardingGetOffY,
                                                 @Verify(param = "startCityName", rule = "required") String startCityName,
                                                 @Verify(param = "endCityName", rule = "required") String endCityName,
                                                 @Verify(param = "bookingStartAddr", rule = "required") String bookingStartAddr,
                                                 @Verify(param = "bookingEndAddr", rule = "required") String bookingEndAddr,
                                                 @Verify(param = "carGroup", rule = "required") Integer carGroup,
                                                 @Verify(param = "bookingStartShortAddr", rule = "required") String bookingStartShortAddr,
                                                 @Verify(param = "bookingEndShortAddr", rule = "required") String bookingEndShortAddr) {
        logger.info(MessageFormat.format("手动录入订单步骤1入参,{0},{1},{2},{3},{4},{5},{6},{7},{8},{9}," +
                        "{10},{11},{12},{13}", reserveName, reservePhone,
                isSameRider, riderName, riderPhone, riderCount, boardingTime, boardingCityId, boardingGetOnX, boardingGetOnY, boardingGetOffCityId,
                boardingGetOffX, boardingGetOffY));


        try {
            //根据横纵坐标获取围栏，根据围栏获取路线

            AjaxResponse boardResponse = hasBoardRoutRights(boardingCityId, boardingGetOnX, boardingGetOnY);

            if (boardResponse.getCode() != RestErrorCode.SUCCESS) {
                logger.info("=============获取上车点围栏为空,入参:boardingGetOnX" + boardingGetOnX + ",boardingGetOnY:" + boardingGetOnY);
                return boardResponse;
            }


            AjaxResponse boardOffResponse = hasBoardOffRoutRights(boardingGetOffCityId, boardingGetOffX, boardingGetOffY);

            if (boardOffResponse.getCode() != RestErrorCode.SUCCESS) {
                logger.info("=============获取下车点围栏为空,入参:boardingGetOffX" + boardingGetOffX + ",boardingGetOffY:" + boardingGetOffY);

                return boardOffResponse;
            }

            String getOnId = boardResponse.getData().toString();

            String getOffId = boardOffResponse.getData().toString();

            AjaxResponse configRouteRes = this.hasRoute(getOnId, getOffId);

            String ruleId = "";
            String supplierId = "";
            if (configRouteRes.getCode() != RestErrorCode.SUCCESS) {
                logger.info("=========根据围栏id未获取到配置路线========");
                return configRouteRes;
            }

            JSONObject jsonRoute = (JSONObject) configRouteRes.getData();
            if (jsonRoute != null && jsonRoute.get("lineId") != null) {
                //JSONObject righRoute = jsonRoute.getJSONObject("data");
                ruleId = jsonRoute.get("lineId").toString();
                supplierId = jsonRoute.get("supplierId").toString();
            }

            if (StringUtils.isEmpty(ruleId)) {
                logger.info("==========未获取到可配置线路=======");
                return AjaxResponse.fail(RestErrorCode.UNFINDED_LINE);
            }

            //根据乘客人获取乘客userId
            Integer customerId = 0;
            if (StringUtils.isNotEmpty(reservePhone)) {
                String url = "/api/customer/regist";
                Map<String, Object> map = Maps.newHashMap();
                map.put("phone", reservePhone);
                map.put("registerSource", 2);
                map.put("channelNum", "新城际订单渠道");
                //获取乘客id
                String registerResult = MpOkHttpUtil.okHttpPost(centerUrl + url, map, 0, null);
                if (StringUtils.isNotEmpty(registerResult)) {
                    JSONObject jsonResult = JSONObject.parseObject(registerResult);
                    if (jsonResult.get("code") == null || jsonResult.getInteger("code") != 0) {
                        logger.info("根据乘客获取customer失败");
                        return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
                    }
                    JSONObject data = jsonResult.getJSONObject("data");
                    customerId = data.getInteger("customerId");
                }
            }

            if (customerId == null || customerId == 0) {
                logger.info("根据乘客获取customer失败");
                return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
            }


            Map<String, Object> map = Maps.newHashMap();
            Date bookDate = DateUtils.getDate(boardingTime, "yyyy-MM-dd HH:mm:ss");

            long bookingDate = bookDate.getTime();

            //获取预估价
            AjaxResponse elsRes = this.getOrderEstimatedAmount620(bookingDate, boardingCityId, 68, riderPhone,
                    ruleId, customerId, boardingGetOffX, boardingGetOffY, riderCount, String.valueOf(carGroup), boardingGetOnX, boardingGetOffY,
                    isSameRider);
            if (elsRes.getCode() != RestErrorCode.SUCCESS) {
                logger.info("未获取到预估价");
                return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
            }

            JSONObject jsonEst = (JSONObject) elsRes.getData();
            //获取预估金额
            String estimatedAmount = jsonEst.getString("estimatedAmount");
            String estimatedKey = jsonEst.getString("estimatedKey");


            StringBuffer sb = new StringBuffer();
            map.put("businessId", Common.BUSSINESSID);//业务线ID
            sb.append("businessId=" + Common.BUSSINESSID).append(SYSMOL);

            map.put("estimatedKey", estimatedKey);
            sb.append("estimatedKey=" + estimatedKey).append(SYSMOL);

            map.put("type", "1");//普通用户订单
            sb.append("type=1").append(SYSMOL);
            map.put("bookingUserName", reserveName);
            sb.append("bookingUserName=" + reserveName).append(SYSMOL);
            map.put("clientType", 28);//订单类型
            sb.append("clientType=28").append(SYSMOL);
            map.put("bookingDate", bookingDate);//预定日期（时间戳）
            sb.append("bookingDate=" + bookingDate).append(SYSMOL);
            if (isSameRider == 1) {
                map.put("riderName", reserveName);
                sb.append("riderName=" + reserveName).append(SYSMOL);
                map.put("riderPhone", reservePhone);
                sb.append("riderPhone=" + reservePhone).append(SYSMOL);
            } else {
                map.put("riderName", riderName);
                sb.append("riderName=" + riderName).append(SYSMOL);
                map.put("riderPhone", riderPhone);
                sb.append("riderPhone=" + riderPhone).append(SYSMOL);
            }

            map.put("cityId", boardingCityId);
            sb.append("cityId=" + boardingCityId).append(SYSMOL);
            map.put("serviceTypeId", 68);
            sb.append("serviceTypeId=68").append(SYSMOL);
            map.put("payFlag", "1");//付款人标识 0-预订人付款，1-乘车人付款，2-门童代人叫车乘车人是自己且乘车人付款，-1-机构付款
            sb.append("payFlag=1").append(SYSMOL);
            map.put("receiveSMS", "2");//是否接收短信 “1”-接收，“2”-不接收
            sb.append("receiveSMS=2").append(SYSMOL);
            map.put("bookingDriverId", "0");//
            sb.append("bookingDriverId=0").append(SYSMOL);//
            map.put("bookingCurrentAddr", "1");
            sb.append("bookingCurrentAddr=1").append(SYSMOL);
            map.put("bookingCurrentPoint", "1");
            sb.append("bookingCurrentPoint=1").append(SYSMOL);
            map.put("channelsNum", "saas");
            sb.append("channelsNum=saas").append(SYSMOL);
            map.put("version", "7.0.1");
            sb.append("version=7.0.1").append(SYSMOL);
            map.put("imei", "1");
            sb.append("imei=1").append(SYSMOL);
            map.put("coordinate", "GD");
            sb.append("coordinate=GD").append(SYSMOL);
            map.put("bookingUserId", customerId);
            sb.append("bookingUserId=" + customerId).append(SYSMOL);
            map.put("bookingUserPhone", reservePhone);
            sb.append("bookingUserPhone=" + reservePhone).append(SYSMOL);
            map.put("buyoutFlag", "1");
            sb.append("buyoutFlag=1").append(SYSMOL);
            map.put("buyoutPrice", estimatedAmount); //预估价
            sb.append("buyoutPrice=" + estimatedAmount).append(SYSMOL);
            map.put("carpoolMark", 1);//拼车标识(0:不拼车，1:拼车)
            sb.append("carpoolMark=1").append(SYSMOL);
            map.put("seats", riderCount);
            sb.append("seats=" + riderCount).append(SYSMOL);
            map.put("ruleId", ruleId);
            sb.append("ruleId=" + ruleId).append(SYSMOL);
            map.put("supplierId", supplierId);
            sb.append("supplierId=" + supplierId).append(SYSMOL);

            if (StringUtils.isNotBlank(supplierId)) {
                CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(Integer.parseInt(supplierId));
                if (carBizSupplier != null) {
                    map.put("supplierFullName", carBizSupplier.getSupplierFullName());
                    sb.append("supplierFullName=" + carBizSupplier.getSupplierFullName()).append(SYSMOL);
                }
            }

            map.put("couponId", "111");
            sb.append("couponId=111").append(SYSMOL);
            map.put("estimatedAmount", estimatedAmount);//预估金额
            sb.append("estimatedAmount=" + estimatedAmount).append(SYSMOL);


            map.put("startCityId", boardingCityId);
            sb.append("startCityId=" + boardingCityId).append(SYSMOL);
            map.put("startCityName", startCityName);
            sb.append("startCityName=" + startCityName).append(SYSMOL);

            map.put("bookingStartAddr", bookingStartAddr);
            sb.append("bookingStartAddr=" + bookingStartAddr).append(SYSMOL);

            map.put("bookingEndAddr", bookingEndAddr);
            sb.append("bookingEndAddr=" + bookingEndAddr).append(SYSMOL);

            map.put("bookingStartShortAddr", bookingStartShortAddr);
            sb.append("bookingStartShortAddr=" + bookingStartShortAddr).append(SYSMOL);

            map.put("bookingEndShortAddr", bookingEndShortAddr);
            sb.append("bookingEndShortAddr=" + bookingEndShortAddr).append(SYSMOL);


            map.put("endCityId", boardingGetOffCityId);
            sb.append("endCityId=" + boardingGetOffCityId).append(SYSMOL);
            map.put("endCityName", endCityName);
            sb.append("endCityName=" + endCityName).append(SYSMOL);


            String getOn = boardingGetOnX + "," + boardingGetOnY;
            String getOff = boardingGetOffX + "," + boardingGetOffY;
            map.put("bookingStartPoint", getOn);
            sb.append("bookingStartPoint=" + getOn).append(SYSMOL);
            map.put("bookingEndPoint", getOff);
            sb.append("bookingEndPoint=" + getOff).append(SYSMOL);
            if (carGroup != null) {
                map.put("groupIds", carGroup);
                sb.append("groupIds=" + carGroup).append(SYSMOL);
            }


            List<String> list = this.list(sb.toString());
            Collections.sort(list);
            list.add("key=" + Common.MAIN_ORDER_KEY);
            StringBuilder sbSort = new StringBuilder();
            for (String str : list) {
                sbSort.append(str).append(SYSMOL);
            }

            String md5Before = sbSort.toString().substring(0, sbSort.toString().length() - 1);
            String sign = Base64.encodeBase64String(DigestUtils.md5(md5Before));
            map.put("sign", sign);


            String orderUrl = "/order/carpool/create";

            String result = carRestTemplate.postForObject(orderUrl, JSONObject.class, map);

            JSONObject orderResult = JSONObject.parseObject(result);
            if (orderResult.get("code") != null && orderResult.getInteger("code") == 0) {
                JSONObject jsonData = orderResult.getJSONObject("data");
                return AjaxResponse.success(jsonData);
            }
        } catch (Exception e) {
            logger.error("创建子订单失败", e);
            return AjaxResponse.fail(RestErrorCode.FAILED_CREATE_SUB_ORDER);
        }

        return AjaxResponse.success(null);
    }


    /**
     * 根据子订单号获取订单数据进行编辑
     *
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping("/subOrderByQuery")
    public AjaxResponse subOrderByQuery(@Verify(param = "orderNo", rule = "required") String orderNo) {

        logger.info("==================获取订单详情入参orderNo:" + orderNo);

        try {
            Map<String, Object> map = Maps.newHashMap();
            List<String> strList = new ArrayList<>();
            map.put("bId", Common.BUSSINESSID);
            strList.add("bId=" + Common.BUSSINESSID);
            map.put("orderNo", orderNo);
            strList.add("orderNo=" + orderNo);

            Collections.sort(strList);
            strList.add("key=" + Common.MAIN_ORDER_KEY);

            String sign = null;
            try {
                sign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(map, Common.MAIN_ORDER_KEY));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            map.put("sign", sign);
            logger.info("==================获取订单详情入参：" + JSONObject.toJSONString(map));
            //wiki地址 http://inside-yapi.01zhuanche.com/project/19/interface/api/3561
            JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderByOrderNo", map, 0, "查询订单详情");

            if (jsonObject != null && jsonObject.get("code") != null) {
                Integer code = jsonObject.getIntValue("code");
                if (0 == code) {
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    InterCityOrderDTO dto = new InterCityOrderDTO();
                    String bookingUserPhone = jsonData.get("bookingUserPhone") == null ? "" : jsonData.getString("bookingUserPhone");
                    String riderPhone = jsonData.get("riderPhone") == null ? "" : jsonData.getString("riderPhone");
                    dto.setReserveName(jsonData.get("bookingUserName") == null ? null : jsonData.getString("bookingUserName"));
                    dto.setReservePhone(bookingUserPhone);
                    dto.setRiderName(jsonData.get("riderName") == null ? "" : jsonData.getString("riderName"));
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
                    dto.setCarGroup(jsonData.get("bookingGroupIds") == null ? null : jsonData.getString("bookingGroupIds"));
                    dto.setServiceTypeId(jsonData.get("serviceTypeId") == null ? 0 : jsonData.getInteger("serviceTypeId"));
                    if (dto.getCarGroup() != null) {
                        Integer seatNum = this.seatCount(Integer.valueOf(dto.getCarGroup()));
                        dto.setSeatNum(seatNum);
                    }

                    if (jsonData != null && jsonData.get("memo") != null) {
                        JSONObject jsonMemo = jsonData.getJSONObject("memo");
                        dto.setBoardingCityId(jsonMemo.get("startCityId") == null ? "" : jsonMemo.getString("startCityId"));
                        dto.setBoardingGetOffCityId(jsonMemo.get("endCityId") == null ? "" : jsonMemo.getString("endCityId"));
                        dto.setBoardingCityName(jsonMemo.get("startCityName") == null ? "" : jsonMemo.getString("startCityName"));
                        dto.setBoardingGetOffCityName(jsonMemo.get("endCityName") == null ? "" : jsonMemo.getString("endCityName"));
                        dto.setRuleId(jsonMemo.get("ruleId") == null ? "0" : jsonMemo.getString("ruleId"));
                    }
                    if (StringUtils.isNotEmpty(bookingUserPhone) && StringUtils.isNotEmpty(riderPhone)) {
                        if (bookingUserPhone.equals(riderPhone)) {
                            dto.setIsSameRider(1);//1相同 0 不同
                        } else {
                            dto.setIsSameRider(0);
                        }
                    } else {
                        dto.setIsSameRider(0);
                    }
                    //线路名称
                    dto.setOrderNo(orderNo);
                    dto.setRouteName(dto.getBoardingCityName() + "-" + dto.getBoardingGetOffCityName());
                    //获取预订人
                    Map<String, Object> bookMap = Maps.newHashMap();
                    bookMap.put("orderNo", orderNo);
                    bookMap.put("name", "bookingUserName");
                    logger.info("====================获取预订人名称入参：" + JSONObject.toJSONString(bookMap));
                    String bookingResult = MpOkHttpUtil.okHttpGet(orderServiceUrl + "/order/byFields/find", bookMap, 0, null);
                    if (StringUtils.isNotEmpty(bookingResult)) {
                        JSONObject jsonBook = JSONObject.parseObject(bookingResult);
                        if (jsonBook.get("code") != null && jsonBook.getInteger("code") == 0) {
                            JSONObject jsonBookData = jsonBook.getJSONObject("data");
                            if (jsonBookData != null && jsonBookData.get("bookingUserName") != null) {
                                String bookingUserName = jsonBookData.getString("bookingUserName");
                                dto.setReserveName(bookingUserName);
                            }

                        }
                    }

                    try {
                        String mainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(orderNo);
                        if (StringUtils.isNotEmpty(mainOrderNo)) {
                            dto.setMainOrderNo(mainOrderNo);
                            //如果主单号不为空，根据主单号获取主单信息以及司机信息
                            MainOrderInterCity mainOrderInterCity = interService.queryMainOrder(mainOrderNo);
                            if (mainOrderInterCity != null && mainOrderInterCity.getDriverId() != null) {
                                dto.setRouteName(mainOrderInterCity.getMainName());
                                dto.setMainTime(mainOrderInterCity.getMainTime());
                                DriverInfoInterCity inter = infoInterCityExMapper.getByDriverId(mainOrderInterCity.getDriverId());
                                if (inter != null) {
                                    dto.setDriverId(mainOrderInterCity.getDriverId());
                                    dto.setSupplierId(inter.getSupplierId());
                                    dto.setSupplierName(inter.getSupplierName());
                                    dto.setDriverName(inter.getDriverName());
                                    dto.setSupplierName(inter.getSupplierName());
                                    dto.setDriverPhone(inter.getDriverPhone());
                                    dto.setLicensePlates(inter.getLicensePlates());
                                    dto.setCityName(inter.getCityName());

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //获取上下车的短地址 wiki http://inside-yapi.01zhuanche.com/project/19/interface/api/9519


                    Map<String, Object> shortMap = Maps.newHashMap();
                    List<String> shortList = new ArrayList<>();
                    shortMap.put("bId", Common.BUSSINESSID);
                    shortList.add("bId=" + Common.BUSSINESSID);
                    shortMap.put("orderNo", orderNo);
                    shortList.add("orderNo=" + orderNo);
                    shortMap.put("columns", "order_address");
                    shortList.add("columns=order_address");

                    Collections.sort(shortList);
                    shortList.add("key=" + Common.MAIN_ORDER_KEY);

                    String shortSign = null;
                    try {
                        shortSign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(shortMap, Common.MAIN_ORDER_KEY));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    shortMap.put("sign", shortSign);

                    //wiki地址 http://inside-yapi.01zhuanche.com/project/19/interface/api/9519
                    JSONObject jsonShort = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderSpecifiedByOrderNo", shortMap, 0, "获取上下车地址");
                    logger.info("=============获取订单短地址结果=============" + jsonShort.toString());
                    if (jsonShort != null && jsonShort.get("code") != null) {
                        if (jsonShort.getInteger("code") == 0 && jsonShort.get("data") != null) {
                            JSONObject jsonAddress = jsonShort.getJSONObject("data");
                            if (jsonAddress != null && jsonAddress.get("orderAddress") != null) {
                                JSONObject jsonDetailAdd = jsonAddress.getJSONObject("orderAddress");
                                dto.setBookingStartShortAddr(jsonDetailAdd.get("bookingStartShortAddr") == null ? "" : jsonDetailAdd.getString("bookingStartShortAddr"));
                                dto.setBookingEndShortAddr(jsonDetailAdd.get("bookingEndShortAddr") == null ? "" : jsonDetailAdd.getString("bookingEndShortAddr"));
                            }
                        }
                    }

                    //获取行程名称
                    if (dto.getCarGroup() != null) {
                        String carName = carBizCarGroupExMapper.getGroupNameByGroupId(Integer.valueOf(dto.getCarGroup()));
                        dto.setCarGroupName(carName);
                    }
                    return AjaxResponse.success(dto);
                }
            }
        } catch (Exception e) {
            logger.error("获取订单失败", e);
        }

        return AjaxResponse.fail(RestErrorCode.ORDER_DETAIL_UNDEFINED);

    }

    /**
     * 编辑订单
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editOrder", method = RequestMethod.POST)
    public AjaxResponse editOrder(@Verify(param = "orderNo", rule = "required") String orderNo,
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
                                  Integer boardingGetOffCityId,
                                  String boardingGetOffX,
                                  String boardingGetOffY,
                                  String mainOrderNo,
                                  Integer status,
                                  String startCityName,
                                  String endCityName,
                                  String bookingStartAddr,
                                  String bookingEndAddr,
                                  String carGroup,
                                  String bookingStartShortAddr,
                                  String bookingEndShortAddr) {

        //根据上下车地址判断是否在城际列车配置的范围内
        //根据横纵坐标获取围栏，根据围栏获取路线
        String getOnId = "";
        if (StringUtils.isNotEmpty(boardingGetOnX) && StringUtils.isNotEmpty(boardingGetOnY)) {
            AjaxResponse boardResponse = hasBoardRoutRights(boardingCityId, boardingGetOnX, boardingGetOnY);

            if (boardResponse.getCode() != RestErrorCode.SUCCESS) {
                return boardResponse;
            }
            getOnId = boardResponse.getData().toString();
        }


        String getOffId = "";

        if (StringUtils.isNotEmpty(boardingGetOffX) && StringUtils.isNotEmpty(boardingGetOffY)) {
            AjaxResponse boardOffResponse = hasBoardOffRoutRights(boardingGetOffCityId, boardingGetOffX, boardingGetOffY);

            if (boardOffResponse.getCode() != RestErrorCode.SUCCESS) {
                return boardOffResponse;
            }
            getOffId = boardOffResponse.getData().toString();
        }

        if (StringUtils.isNotEmpty(getOnId) && StringUtils.isNotEmpty(getOffId)) {
            AjaxResponse configRouteRes = this.hasRoute(getOnId, getOffId);

            if (configRouteRes.getCode() != RestErrorCode.SUCCESS) {
                logger.info("根据围栏id未获取到配置路线");
                return configRouteRes;
            }
        }


        String getOn = boardingGetOnX + "," + boardingGetOnY;
        String getOff = boardingGetOffX + "," + boardingGetOffY;


        AjaxResponse configRouteRes = this.hasRoute(getOnId, getOffId);

        String ruleId = "";
        String supplierId = "";
        if (configRouteRes.getCode() != RestErrorCode.SUCCESS) {
            logger.info("根据围栏id未获取到配置路线");
            return configRouteRes;
        }

        JSONObject jsonRoute = (JSONObject) configRouteRes.getData();
        if (jsonRoute != null && jsonRoute.get("lineId") != null) {
            ruleId = jsonRoute.get("lineId").toString();
            supplierId = jsonRoute.get("supplierId").toString();
        }

        if (StringUtils.isEmpty(ruleId)) {
            logger.info("未获取到可配置线路");
            return AjaxResponse.fail(RestErrorCode.UNFINDED_LINE);
        }
        //根据乘客人获取乘客userId
        Integer customerId = 0;
        if (StringUtils.isNotEmpty(reservePhone)) {
            String url = "/api/customer/regist";
            Map<String, Object> map = Maps.newHashMap();
            map.put("phone", reservePhone);
            map.put("registerSource", 2);
            map.put("channelNum", "新城际订单渠道");
            //获取乘客id
            String registerResult = MpOkHttpUtil.okHttpPost(centerUrl + url, map, 0, null);
            if (StringUtils.isNotEmpty(registerResult)) {
                JSONObject jsonResult = JSONObject.parseObject(registerResult);
                if (jsonResult.get("code") == null || jsonResult.getInteger("code") != 0) {
                    logger.info("根据乘客获取customer失败");
                    return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
                }
                JSONObject data = jsonResult.getJSONObject("data");
                customerId = data.getInteger("customerId");
            }
        }

        if (customerId == null || customerId == 0) {
            logger.info("根据乘客获取customer失败");
            return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
        }

        Date longTimeDate = new Date();
        if (StringUtils.isNotEmpty(boardingTime)) {
            longTimeDate = DateUtils.getDate(boardingTime, "yyyy-MM-dd HH:mm:ss");
         }


        //获取预估价
        AjaxResponse elsRes = null;
        try {
            elsRes = this.getOrderEstimatedAmount620(Long.valueOf(longTimeDate.getTime()), boardingCityId, 68, riderPhone,
                    ruleId, customerId, boardingGetOffX, boardingGetOffY, riderCount, String.valueOf(carGroup), boardingGetOnX,
                    boardingGetOnY, isSameRider);
        } catch (Exception e) {
            logger.error("获取预估价异常",e);
            return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
        }
        if (elsRes.getCode() != RestErrorCode.SUCCESS) {
            logger.info("未获取到预估价");
            return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
        }

        JSONObject jsonEst = (JSONObject) elsRes.getData();
        //获取预估金额
        String estimatedAmount = jsonEst.getString("estimatedAmount");
        String estimatedKey = jsonEst.getString("estimatedKey");


        Map<String, Object> map = Maps.newHashMap();
        List<String> list = new ArrayList<>();

        map.put("estimatedAmount", estimatedAmount);//预估金额
        list.add("estimatedAmount=" + estimatedAmount);
        map.put("buyoutPrice", estimatedAmount); //预估价
        list.add("buyoutPrice=" + estimatedAmount);
        map.put("estimatedKey", estimatedKey);
        list.add("estimatedKey=" + estimatedKey);


        map.put("businessId", Common.BUSSINESSID);
        list.add("businessId=" + Common.BUSSINESSID);
        map.put("orderNo", orderNo);
        list.add("orderNo=" + orderNo);
        if (StringUtils.isNotEmpty(getOn)) {
            map.put("factStartPoint", getOn);//根据坐标轴获取围栏id
            list.add("factStartPoint=" + getOn);
        }

        if (StringUtils.isNotEmpty(getOff)) {
            map.put("factEndPoint", getOff);
            list.add("factEndPoint=" + getOff);
        }


        if (StringUtils.isNotEmpty(mainOrderNo)) {
            map.put("mainOrderNo", mainOrderNo);
            list.add("mainOrderNo=" + mainOrderNo);
        }

        map.put("pushDriverType", OrderStateEnum.MANUAL_ASSIGNMENT.getCode());//订单指派司机类型(1 系统强派| 2 司机抢单| 3 后台人工指派)
        list.add("pushDriverType=" + OrderStateEnum.MANUAL_ASSIGNMENT.getCode());

        if (boardingCityId != null) {
            map.put("factStartAddr", boardingCityId);
            list.add("factStartAddr=" + boardingCityId);
        }


        if (boardingGetOffCityId != null) {
            map.put("factEndAddr", boardingGetOffCityId);//
            list.add("factEndAddr=" + boardingGetOffCityId);
        }


        if (riderCount != null) {
            map.put("factDriverId", riderCount);
            list.add("factDriverId=" + riderCount);
        }

        if (StringUtils.isNotEmpty(getOn)) {
            map.put("bookingStartPoint", getOn);
            list.add("bookingStartPoint=" + getOn);
        }

        if (StringUtils.isNotEmpty(getOff)) {
            map.put("bookingEndPoint", getOff);
            list.add("bookingEndPoint=" + getOff);
        }

        if (StringUtils.isNotEmpty(reservePhone)) {
            map.put("bookingUserPhone", reservePhone);
            list.add("bookingUserPhone=" + reservePhone);
        }


        if (1 == isSameRider) {
            if (StringUtils.isNotEmpty(reserveName)) {
                map.put("riderName", reserveName);
                list.add("riderName=" + reserveName);
            }
            if (StringUtils.isNotEmpty(reservePhone)) {
                map.put("riderPhone", reservePhone);
                list.add("riderPhone=" + reservePhone);
            }


        } else {
            if (StringUtils.isNotEmpty(riderName)) {
                map.put("riderName", riderName);
                list.add("riderName=" + riderName);
            }

            if (StringUtils.isNotEmpty(riderPhone)) {
                map.put("riderPhone", riderPhone);
                list.add("riderPhone=" + riderPhone);
            }


        }

        if (StringUtils.isNotEmpty(startCityName)) {
            map.put("startCityName", startCityName);
            list.add("startCityName=" + startCityName);
        }

        if (StringUtils.isNotEmpty(endCityName)) {
            map.put("endCityName", endCityName);
            list.add("endCityName=" + endCityName);
        }


        if (StringUtils.isNotEmpty(bookingStartAddr)) {
            map.put("bookingStartAddr", bookingStartAddr);
            list.add("bookingStartAddr=" + bookingStartAddr);

        }

        if (StringUtils.isNotEmpty(bookingEndAddr)) {
            map.put("bookingEndAddr", bookingEndAddr);
            list.add("bookingEndAddr=" + bookingEndAddr);
        }

        if (StringUtils.isNotEmpty(boardingTime)) {
            Date crossCityStartTime = DateUtils.getDate(boardingTime, "yyyy-MM-dd HH:mm:ss");
            map.put("crossCityStartTime", crossCityStartTime.getTime());
            list.add("crossCityStartTime=" + crossCityStartTime.getTime());
        }


        if (boardingCityId != null) {
            map.put("startCityId", boardingCityId);
            list.add("startCityId=" + boardingCityId);
        }
        if (boardingGetOffCityId != null) {
            map.put("endCityId", boardingGetOffCityId);
            list.add("endCityId=" + boardingGetOffCityId);
        }

        if (StringUtils.isNotEmpty(carGroup)) {
            map.put("groupIds", carGroup);
            list.add("groupIds=" + carGroup);
        }

        if (StringUtils.isNotEmpty(bookingStartShortAddr)) {
            map.put("bookingStartShortAddr", bookingStartShortAddr);
            list.add("bookingStartShortAddr=" + bookingStartShortAddr);
        }

        if (StringUtils.isNotEmpty(bookingEndShortAddr)) {
            map.put("bookingEndShortAddr", bookingEndShortAddr);
            list.add("bookingEndShortAddr=" + bookingEndShortAddr);
        }

        map.put("supplierId", supplierId);
        list.add("supplierId=" + supplierId);

        if (StringUtils.isNotBlank(supplierId)) {
            CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(Integer.parseInt(supplierId));
            if (carBizSupplier != null) {
                map.put("supplierFullName", carBizSupplier.getSupplierFullName());
                list.add("supplierFullName=" + carBizSupplier.getSupplierFullName());
            }
        }


        map.put("status", status);
        list.add("status=" + status);


        Collections.sort(list);
        list.add("key=" + Common.MAIN_ORDER_KEY);
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str).append("&");
        }

        String param = sb.toString().substring(0, sb.toString().length() - 1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(param));
        map.put("sign", sign);
        String url = "/order/carpool/updateCrossCityOrder";
        String editResult = carRestTemplate.postForObject(url, JSONObject.class, map);
        logger.info("编辑结果：" + editResult);
        if (StringUtils.isNotBlank(editResult)) {
            JSONObject updateResult = JSONObject.parseObject(editResult);
            if (updateResult.get("code") != null && updateResult.getInteger("code") == 0) {
                logger.info("更新成功");
                JSONObject editJson = new JSONObject();
                editJson.put("orderNo", orderNo);
                return AjaxResponse.success(editJson);
            }
        }

        return AjaxResponse.fail(RestErrorCode.UPDATE_SUB_ORDER_FAILED);
    }

    /**
     * 取消订单
     *
     * @return
     */
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse cancelOrder(@Verify(param = "orderNo", rule = "required") String orderNo,
                                    @Verify(param = "status", rule = "required") Integer status,
                                    @Verify(param = "cityId", rule = "required") Integer cityId,
                                    @Verify(param = "serviceTypeId", rule = "required") Integer serviceTypeId,
                                    @Verify(param = "carGroupId", rule = "required") Integer carGroupId,
                                    @Verify(param = "bookingUserPhone", rule = "required") String bookingUserPhone,
                                    @Verify(param = "ruleId", rule = "required") Integer ruleId) {
        logger.info("=============取消订单入参:orderNo：" + orderNo + ",status:" + status + "==============");

        logger.info(MessageFormat.format("取消接口入参:orderNo:{0},status:{1},cityId:{2},serviceTypeId:{3}," +
                        "carGroupId:{4},bookingUserPhone:{5},ruleId:{6}", orderNo, status, cityId, serviceTypeId, carGroupId,
                bookingUserPhone, ruleId));

        //调用计费取消费接口http://inside-yapi.01zhuanche.com/project/88/interface/api/16255
        try {
            Map<String, Object> chargeMap = Maps.newHashMap();
            chargeMap.put("orderNo", orderNo);
            chargeMap.put("cityId", cityId);
            chargeMap.put("serviceTypeId", serviceTypeId);
            chargeMap.put("carGroupId", carGroupId);
            chargeMap.put("orderStatus", status);
            chargeMap.put("bookingUserPhone", bookingUserPhone);
            chargeMap.put("ruleId", ruleId);
            chargeMap.put("pinSuccess", "1");
            logger.info("=======调用计费接口入参==========" + JSONObject.toJSONString(chargeMap));
            String chargeResult = MpOkHttpUtil.okHttpPost(orderCostUrl + "/cancel/carpool/payCancelDamage", chargeMap,
                    0, null);
            logger.info("========调用计费取消接口返回结果========" + chargeResult);
            if (StringUtils.isNotEmpty(chargeResult)) {
                JSONObject chargeJson = JSONObject.parseObject(chargeResult);
                if (chargeJson.get("code") != null && chargeJson.getInteger("code") == 0) {
                    logger.info("调用计费取消接口成功");
                    //return AjaxResponse.success(null);
                }
            }
        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.CHARGE_CANCEL_FAILED);
        }


        try {
            Map<String, Object> map = Maps.newHashMap();
            List<String> listStr = new ArrayList<>();
            map.put("businessId", Common.BUSSINESSID);
            listStr.add("businessId=" + Common.BUSSINESSID);
            map.put("orderNo", orderNo);
            listStr.add("orderNo=" + orderNo);
            map.put("cancelReasonId", 24);//固定取消原因
            listStr.add("cancelReasonId=" + 24);
            map.put("memo", "加盟商调度员手动取消");
            listStr.add("memo=加盟商调度员手动取消");
            map.put("cancelType", 853); //1-乘客；2-平台；3-客服；4-司机
            listStr.add("cancelType=853");
            map.put("cancelStatus", 11);//平台取消任意值
            listStr.add("cancelStatus=11");


            Collections.sort(listStr);
            listStr.add("key=" + Common.MAIN_ORDER_KEY);

            StringBuilder sb = new StringBuilder();
            for (String str : listStr) {
                sb.append(str).append(SYSMOL);
            }
            String param = sb.toString().substring(0, sb.toString().length() - 1);
            String sign = Base64.encodeBase64String(DigestUtils.md5(param));
            map.put("sign", sign);

            String url = "/order/carpool/inDispatcherCancelSubOrder";
            if (status > 13) {
                url = "/order/carpool/syncCancelSubOrder";
            }
            String result = carRestTemplate.postForObject(url, JSONObject.class, map);
            logger.info("============取消订单返回结果:" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject jsonResult = JSONObject.parseObject(result);
                if (jsonResult != null && jsonResult.get("code") != null && jsonResult.getInteger("code") == 0) {
                    logger.info("取消订单成功");
                    return  AjaxResponse.success(null);
                }
            }
        } catch (Exception e) {
            logger.info("调用订单获取取消费异常" + e);
            return AjaxResponse.fail(RestErrorCode.CANCEL_FAILED);

        }



        return AjaxResponse.fail(RestErrorCode.CANCEL_FAILED);
    }


    /**
     * 查询司机
     *
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
                                    String license,
                                    @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize) {

        logger.info(MessageFormat.format("查询城际拼车司机入参：supplierId:{0},driverName:{1},driverPhpne:{2},license:{3}", supplierId,
                driverName, driverPhone, license));


        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Set<Integer> cityIds = new HashSet<>();
        Set<Integer> supplierIds = new HashSet<>();

        if (cityId != null) {
            cityIds.add(cityId);
        } else {
            cityIds = loginUser.getCityIds();
        }
        if (supplierId != null) {
            supplierIds.add(supplierId);
        } else {
            supplierIds = loginUser.getSupplierIds();
        }

        Page page = PageHelper.startPage(pageNum,pageSize);
        List<MainOrderDetailDTO> interCityList = infoInterCityExMapper.queryDriver(cityId, supplierId, driverName, driverPhone, license, cityIds, supplierIds);

        for (MainOrderDetailDTO detailDTO : interCityList) {
            if (StringUtils.isNotEmpty(detailDTO.getMainOrder())) {
                //剩余车位数
                String url = "/order/carpool/getCrossCityMainOrder";
                StringBuilder sb = new StringBuilder();
                sb.append("businessId=" + Common.BUSSINESSID + "&mainOrderNo=" + detailDTO.getMainOrder()).append("&key=" + Common.MAIN_ORDER_KEY);
                String sign = Base64.encodeBase64String(DigestUtils.md5(sb.toString()));
                url += "?businessId=" + Common.BUSSINESSID + "&mainOrderNo=" + detailDTO.getMainOrder() + "&sign=" + sign;
                JSONObject jsonResult = carRestTemplate.getForObject(url, JSONObject.class);
                if (StringUtils.isNotBlank(jsonResult.toString())) {
                    if (jsonResult.get("code") != null && jsonResult.getInteger("code") == 0) {
                        JSONObject jsonData = jsonResult.getJSONObject("data");
                        Integer passengerNums = jsonData.getInteger("passengerNums");
                        //Integer groupId = jsonData.getInteger("groupId");
                        //根据司机driverId获取最新的车型以及座位数
                        Integer groupId =  carBizCarInfoExMapper.groupIdByDriverId(detailDTO.getDriverId());
                        if(groupId == 0){
                            groupId = jsonData.getInteger("groupId");//防止车管groupId为0的情况
                        }
                        Integer maxSeat = seatCount(groupId);
                        //剩余座位数
                        Integer remainSeat = maxSeat - passengerNums;
                        if (remainSeat <= 0) {
                            remainSeat = 0;
                        }
                        detailDTO.setRemainSeats(remainSeat);
                    }
                }
            } else {
                Integer groupId =  carBizCarInfoExMapper.groupIdByDriverId(detailDTO.getDriverId());
                if(groupId == 0){
                    groupId = 41;//防止车管groupId为0的情况
                }
                detailDTO.setRemainSeats(seatCount(groupId));
            }
        }
        int total = (int) page.getTotal();
        PageDTO pageDTO = new PageDTO(pageNum,pageSize,total,interCityList);

        return AjaxResponse.success(pageDTO);
    }


    /**
     * 修改主单
     *
     * @param mainTime
     * @return
     */
    @RequestMapping(value = "/addMainOrder", method = RequestMethod.POST)
    public AjaxResponse addMainOrder(String mainOrderNo,
                                     String mainTime) {

        logger.info("添加主单接口");
        //修改订单接口
        int code = interService.updateMainTime(mainOrderNo, mainTime);
        if (code > 0) {
            logger.info("修改主订单接口成功");
            return AjaxResponse.success(null);
        }

        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    /**
     * 指派
     *
     * @param mainOrderNo
     * @param orderNo
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param groupId
     * @param orderTime     如果司机主单为空，主单的行程时间和路线不能为空
     * @param routeName
     * @return
     */
    @RequestMapping("/handOperateAddOrderSetp2")
    @ResponseBody
    public AjaxResponse handOperateAddOrderSetp2(String mainOrderNo,
                                                 @Verify(param = "orderNo", rule = "required") String orderNo,
                                                 @Verify(param = "orderNo", rule = "required") Integer driverId,
                                                 @Verify(param = "orderNo", rule = "required") String driverName,
                                                 @Verify(param = "orderNo", rule = "required") String driverPhone,
                                                 @Verify(param = "orderNo", rule = "required") String licensePlates,
                                                 @Verify(param = "orderNo", rule = "required") String groupId,
                                                 String orderTime,
                                                 String routeName) {
        //派单
        logger.info("指派接口入参:mainOrderNo=" + mainOrderNo + ",orderNo:" + orderNo
                + ",driverId:" + driverId + ",driverName:" + driverName + ",driverPhone:" + driverPhone + ",licensePlates:" + licensePlates
                + ",groupId:" + groupId + ",orderTime:" + orderTime + ",routeName:" + routeName);
        Map<String, Object> map = Maps.newHashMap();
        List<String> listParam = new ArrayList<>();
        map.put("businessId", Common.BUSSINESSID);
        listParam.add("businessId=" + Common.BUSSINESSID);
        if (StringUtils.isNotEmpty(mainOrderNo)) {
            map.put("mainOrderNo", mainOrderNo);
            listParam.add("mainOrderNo=" + mainOrderNo);
        }
        map.put("orderNo", orderNo);
        listParam.add("orderNo=" + orderNo);
        map.put("driverId", driverId);
        listParam.add("driverId=" + driverId);
        map.put("driverName", driverName);
        listParam.add("driverName=" + driverName);
        map.put("driverPhone", driverPhone);
        listParam.add("driverPhone=" + driverPhone);
        map.put("licensePlates", licensePlates);
        listParam.add("licensePlates=" + licensePlates);
        //todo 获取座位数不是根据第一次的车型 而是根据司机id获取最新的
        /*if (StringUtils.isNotEmpty(groupId)) {
            map.put("carGroupId", groupId);
            listParam.add("carGroupId=" + groupId);
            int carSeatNums = seatCount(Integer.valueOf(groupId));
            map.put("carSeatNums", carSeatNums);
            listParam.add("carSeatNums=" + carSeatNums);
        }*/

        //根据driverId获取groupId
        Integer newGroupId = carBizCarInfoExMapper.groupIdByDriverId(driverId);
        if(newGroupId != null && newGroupId>0){
            map.put("carGroupId", newGroupId);
            listParam.add("carGroupId=" + newGroupId);
            int carSeatNums = seatCount(Integer.valueOf(newGroupId));
            map.put("carSeatNums", carSeatNums);
            listParam.add("carSeatNums=" + carSeatNums);
        }else {
            logger.info("==========获取司机座位数为空========= 使用用户选择的车型");
            map.put("carGroupId", groupId);
            listParam.add("carGroupId=" + groupId);
            int carSeatNums = seatCount(Integer.valueOf(groupId));
            map.put("carSeatNums", carSeatNums);
            listParam.add("carSeatNums=" + carSeatNums);
        }

        if (StringUtils.isNotEmpty(orderTime)) {
            Date startTime = DateUtils.getDate(orderTime, "yyyy-MM-dd HH:mm:ss");
            map.put("crossCityStartTime", startTime.getTime());
            listParam.add("crossCityStartTime=" + startTime.getTime());
        }

        if (StringUtils.isNotEmpty(routeName)) {
            map.put("routeName", routeName);
            listParam.add("routeName=" + routeName);
        }
        //添加调度员手机号
        if (WebSessionUtil.getCurrentLoginUser().getMobile() != null) {
            map.put("dispatcherPhone", WebSessionUtil.getCurrentLoginUser().getMobile());
            listParam.add("dispatcherPhone=" + WebSessionUtil.getCurrentLoginUser().getMobile());
        }

        Collections.sort(listParam);
        listParam.add("key=" + Common.MAIN_ORDER_KEY);


        StringBuilder sbSort = new StringBuilder();
        for (String str : listParam) {
            sbSort.append(str).append("&");
        }

        String md5Before = sbSort.toString().substring(0, sbSort.toString().length() - 1);
        String sign = Base64.encodeBase64String(DigestUtils.md5(md5Before));
        map.put("sign", sign);


        String url = "/order/carpool/bingCrossCityMainOrder";
        String result = carRestTemplate.postForObject(url, JSONObject.class, map);
        logger.info("指派返回结果:" + result);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonResult = JSONObject.parseObject(result);
            if (jsonResult.get("code") != null && jsonResult.getInteger("code") == 0) {
                logger.info("子单绑定主单成功");
                String mobile = WebSessionUtil.getCurrentLoginUser().getMobile();
                ExecutorService executor = Executors.newCachedThreadPool();
                Future<String> future = executor.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        logger.info("=================异步处理开始============" + jsonResult.getJSONObject("data"));
                        JSONObject jsonData = jsonResult.getJSONObject("data");
                        try {
                            if (jsonData != null && jsonData.get("mainOrderNo") != null) {
                                String newMainOrderNo = jsonData.getString("mainOrderNo");
                                MainOrderInterCity queryMain = interService.queryMainOrder(newMainOrderNo);
                                logger.info("========查询结果=======" + JSONObject.toJSONString(queryMain));
                                int code = 0;
                                if (queryMain != null && queryMain.getId() > 0) {
                                    code = interService.updateMainOrderState(newMainOrderNo, 1,mobile);
                                } else {
                                    MainOrderInterCity main = new MainOrderInterCity();
                                    main.setDriverId(driverId);
                                    main.setCreateTime(new Date());
                                    main.setUpdateTime(new Date());
                                    main.setMainName(routeName);
                                    main.setStatus(MainOrderInterCity.orderState.NOTSETOUT.getCode());
                                    main.setMainOrderNo(newMainOrderNo);
                                    main.setOpePhone(mobile);
                                    main.setMainTime(orderTime);
                                    code = interService.addMainOrderNo(main);
                                }
                                if (code > 0) {
                                    logger.info("=========子单绑定主单成功=======");
                                    return String.valueOf(code);
                                }
                                return String.valueOf(code);
                            }
                        } catch (Exception e) {
                            logger.error("子单绑定异常=======" + e);
                        }
                        return "=============子单绑定异常==========";
                    }
                });

                return AjaxResponse.success(null);


            }else {
                logger.info("子单指派主单返回信息========" + jsonResult.toString());
                return AjaxResponse.failMsg(jsonResult.getIntValue("code"),jsonResult.getString("msg"));
            }

        } else {
            return AjaxResponse.fail(RestErrorCode.FAILED_GET_MAIN_ORDER);
        }
        
    }






    /**
     * 改派
     * @param mainOrderNo
     * @param orderNo
     * @param driverId
     * @param driverPhone
     * @param licensePlates
     * @param groupId
     * @return
     */
    @RequestMapping("/updateOtherMainOrder")
    @ResponseBody
    public AjaxResponse updateOtherMainOrder( String mainOrderNo,
                                              @Verify(param = "orderNo",rule = "required")  String orderNo,
                                              @Verify(param = "driverId",rule = "required")   Integer driverId,
                                              @Verify(param = "driverPhone",rule = "required") String driverPhone,
                                              @Verify(param = "licensePlates",rule = "required")  String licensePlates,
                                              @Verify(param = "cityId",rule = "required") Integer cityId,
                                              @Verify(param = "groupId",rule = "required") String groupId,
                                              String orderTime,
                                              String routeName){
        //派单
        logger.info(MessageFormat.format("改派入参：mainOrderNo:{0},orderNo:{1},driverId:{2},driverPhone:{3},licensePlates{4},cityId{5}" +
                ",groupId:{6},orderTime:{7},routeName:{8}",mainOrderNo,orderNo,driverId,driverPhone,licensePlates,cityId,groupId,orderTime,
                routeName));

        /**todo 如果乘客指派成功后，又去给这个司机创建主单，需要给这个司机重新创建主单并记录起来**/
        try {
            if(StringUtils.isNotEmpty(mainOrderNo)){
                //当前子单的主单号
                String curMainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(orderNo);
                if(mainOrderNo.equals(curMainOrderNo)){
                    curMainOrderNo = null;
                }
            }

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


            //根据driverId获取groupId
            Integer newGroupId = carBizCarInfoExMapper.groupIdByDriverId(driverId);
            if(newGroupId != null && newGroupId>0){
                map.put("groupId", newGroupId);
                listParam.add("groupId=" + newGroupId);
                int carSeatNums = seatCount(Integer.valueOf(newGroupId));
                map.put("carSeatNums", carSeatNums);
                listParam.add("carSeatNums=" + carSeatNums);
            }else {
                logger.info("==========获取司机座位数为空========= 使用用户选择的车型");
                map.put("groupId", groupId);
                listParam.add("groupId=" + groupId);
                int carSeatNums = seatCount(Integer.valueOf(groupId));
                map.put("carSeatNums", carSeatNums);
                listParam.add("carSeatNums=" + carSeatNums);
            }

            map.put("cityId",cityId);
            listParam.add("cityId="+cityId);
            if(StringUtils.isNotEmpty(routeName)){
                map.put("routeName",routeName);
                listParam.add("routeName="+routeName);

            }

            if(StringUtils.isNotEmpty(driverPhone)){
                map.put("driverPhone",driverPhone);
                listParam.add("driverPhone="+driverPhone);
            }



            //添加调度员手机号
            if(WebSessionUtil.getCurrentLoginUser().getMobile() != null){
                map.put("dispatcherPhone",WebSessionUtil.getCurrentLoginUser().getMobile());
                listParam.add("dispatcherPhone="+WebSessionUtil.getCurrentLoginUser().getMobile());
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


            String url = "/order/carpool/crossCityOrderReassign";
            System.out.println(url);


            String result = carRestTemplate.postForObject(url,JSONObject.class,map);
            logger.info("改派结果result:" + result);
            if(StringUtils.isNotBlank(result)){
                JSONObject jsonResult = JSONObject.parseObject(result);
                if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                    logger.info("改派成功");
                    String mobile = WebSessionUtil.getCurrentLoginUser().getMobile();
                    ExecutorService executor = Executors.newCachedThreadPool();
                    Future<String> future = executor.submit(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            //如果是新增或者改派，需要将
                            logger.info("==============异步改派开始===========" + jsonResult.getJSONObject("data"));
                            if(StringUtils.isEmpty(mainOrderNo)){
                                JSONObject jsonData = jsonResult.getJSONObject("data");
                                int code =0;
                                MainOrderInterCity main = new MainOrderInterCity();
                                main.setDriverId(driverId);
                                main.setCreateTime(new Date());
                                main.setUpdateTime(new Date());
                                main.setMainName(routeName);
                                main.setStatus(MainOrderInterCity.orderState.NOTSETOUT.getCode());
                                main.setMainOrderNo(jsonData.getString("mainOrderNo"));
                                main.setOpePhone(mobile);
                                main.setMainTime(orderTime);
                                code = interService.addMainOrderNo(main);

                                if(code > 0){
                                    logger.info("=============异步插入数据成功=========");
                                    return String.valueOf(code);
                                }
                            }else {
                                MainOrderInterCity queryMainOrder  = interService.queryMainOrder(mainOrderNo);
                                logger.info("=========更新数据=======" + JSONObject.toJSONString(queryMainOrder));
                                if(queryMainOrder != null && queryMainOrder.getId()>0){
                                    int code = interService.updateMainOrderState(mainOrderNo,1,mobile);
                                    if(code > 0){
                                        logger.info("=============异步更新数据成功=========");
                                        return String.valueOf(code);
                                    }
                                }
                            }
                            return "=============子单绑定异常==========";
                        }
                    });


                    return AjaxResponse.success(null);
                }else {
                    logger.info("改派未成功");
                    return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
                }
            }
        } catch (Exception e) {
            logger.error("派单异常"+e);
            return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
        }

        return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
    }




    //根据车辆类型获取座位数
    private int seatCount(Integer groupId){
        int seatNum = 0;
        try {
            seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(groupId);
        } catch (NumberFormatException e) {
            logger.error("获取座位号失败"+e);
        }
        return seatNum;
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


    /**
     * 调用lbs地图接口 wiki http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=328062
     * @param lng
     * @param lat
     * @param platform
     * @return
     */
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


    /**
     * 判断上车点 是否在围栏区域 wiki:http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=11178396
     * @param boardingCityId
     * @param boardingGetOnX
     * @param boardingGetOnY
     * @return
     */
    private AjaxResponse hasBoardRoutRights(Integer boardingCityId,String boardingGetOnX,String boardingGetOnY){
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

        if(StringUtils.isEmpty(getOnId)){
            logger.info("上下车点不再围栏区域");
            return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
        }

        return AjaxResponse.success(getOnId);
    }


    /**
     * 判断下车点是否在围栏区域 wiki:http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=11178396
     * @param boardingGetOffCityId
     * @param boardingGetOffX
     * @param boardingGetOffY
     * @return
     */
    private AjaxResponse hasBoardOffRoutRights(Integer boardingGetOffCityId,String boardingGetOffX,String boardingGetOffY){
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

        if(StringUtils.isEmpty(getOffId)){
            logger.info("上下车点不再围栏区域");
            return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
        }
        return AjaxResponse.success(getOffId);
    }


    /**
     * 是否有配置的线路 http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=40172935
     * @param getOnId
     * @param getOffId
     * @return
     */
    private AjaxResponse hasRoute(String getOnId,String getOffId){
        JSONObject jsonRoute = new JSONObject();
        try {
            Integer areaStartRangId = Integer.valueOf(getOnId);
            Integer areaEndRangId = Integer.valueOf(getOffId);
            String areaUrl = configUrl + "/intercityCarUse/getLineSupplier?areaStartRangeId="+areaStartRangId+"&areaEndRangeId="+areaEndRangId;

            String areaResult = MpOkHttpUtil.okHttpGet(areaUrl,null,0,null);
            if(StringUtils.isNotEmpty(areaResult)){
                JSONObject jsonResult = JSONObject.parseObject(areaResult);
                if(jsonResult.get("data") != null && jsonResult.get("data") != ""){
                    //添加权限验证 管理员直接过去
                    SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
                    Set<Integer> cities = loginUser.getCityIds();
                    Set<Integer> suppliers = loginUser.getSupplierIds();
                    JSONObject jsonData = jsonResult.getJSONObject("data");
                    if (jsonData.getString("supplierId")==null) {
                    	 logger.info("对应坐标没有线路");
                         return  AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
					}
                    String supp = jsonData.getString("supplierId");
                    String[] inteSupp = supp.split(",");

                    LinkedList<Integer> linkedList = new LinkedList<>();
                    for(int i = 0;i<inteSupp.length;i++){
                        linkedList.add(Integer.valueOf(inteSupp[i]));
                    }
                    boolean bl = false;
                    if(suppliers.size()>0){
                        HashSet<Integer> hashSet = new HashSet<>(suppliers);
                        if(jsonData.get("supplierId") != null ){
                            Iterator<Integer> iterator = linkedList.iterator();
                            while (iterator.hasNext()){
                            	Integer supplierId = iterator.next();
                                if(hashSet.contains(supplierId)){
                                    logger.info("包含有路线");
                                    jsonRoute.put("lineId",jsonData.getIntValue("lineId"));
                                    jsonRoute.put("lineName",jsonData.getString("lineName"));
                                    jsonRoute.put("supplierId",supplierId);
                                    bl = true;
                                    break;
                                }
                            }
                        }

                        if(!bl){
                            return  AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                        }
                    }else if(cities.size()>0){//如果是城市级别的
                    	
                    	List<CarBizSupplier> carBizSuppliers=citySupplierTeamCommonService.querySupplierAllList(cities);
                    	List<Integer> supplierIds= carBizSuppliers.stream().map(f -> f.getSupplierId()).collect(Collectors.toList());
                    	supplierIds.retainAll(linkedList);
                    	if (supplierIds.size()>0) {
                    		jsonRoute.put("lineId",jsonData.getIntValue("lineId"));
                            jsonRoute.put("lineName",jsonData.getString("lineName"));
                            jsonRoute.put("supplierId",supplierIds.get(0));
                            bl = true;
						}
                        /*HashSet<Integer> hashSet = new HashSet<>(cities);
                        boolean bl = false;
                        if(jsonData.get("cityId") != null ){
                            String citys = jsonData.getString("cityId");
                            String[] interCity = citys.split(",");

                            LinkedList<Integer> linkedCityList = new LinkedList<>();
                            for(int i = 0;i<interCity.length;i++){
                            	linkedCityList.add(Integer.valueOf(interCity[i]));
                            }
                            Iterator<Integer> iterator = linkedCityList.iterator();
                            while (iterator.hasNext()){
                                if(hashSet.contains(iterator.next())){
                                    logger.info("包含有路线");
                                    jsonRoute.put("lineId",jsonData.getIntValue("lineId"));
                                    jsonRoute.put("lineName",jsonData.getString("lineName"));
                                    bl = true;
                                    break;
                                }
                            }
                        }*/

                        if(!bl){
                            return  AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                        }
                    }else {
                        jsonRoute.put("lineId",jsonData.getIntValue("lineId"));
                        jsonRoute.put("lineName",jsonData.getString("lineName"));
                        jsonRoute.put("supplierId",linkedList.get(0));
                    }

                    logger.info("该坐标含有线路");
                }else {
                    logger.info("对应坐标没有线路");
                    return  AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                }
            }
        } catch (NumberFormatException e) {
            logger.error("获取线路异常" + e.getMessage());
            return AjaxResponse.success(RestErrorCode.UNDEFINED_LINE);
        }
        return AjaxResponse.success(jsonRoute);
    }


    /**
     * 调用预估价 wiki: http://inside-yapi.01zhuanche.com/project/88/interface/api/3957
     * @param cityId
     * @param serviceType
     * @param riderPhone
     * @param ruleId
     * @param customerId
     * @param endPointLo
     * @param endPointLa
     * @param riderCount
     * @param group
     * @param startPoint
     * @param endPoint
     * @param isRemainder
     * @return
     * @throws Exception
     */
    private AjaxResponse getOrderEstimatedAmount620(long bookingDate,
                                                    Integer cityId,
                                                    Integer serviceType,
                                                    String riderPhone,
                                                    String ruleId,
                                                    Integer customerId,
                                                    String endPointLo,
                                                    String endPointLa,
                                                    Integer riderCount,
                                                    String group,
                                                    String startPoint,
                                                    String endPoint,
                                                    Integer isRemainder
    ) throws Exception {
        try {
            Map<String,Object> map = Maps.newHashMap();
            map.put("duration",0);//预估时长
            map.put("distance",0);//预估里程
            map.put("tolls",0);//高速费
            map.put("bookingDate",bookingDate/1000);//预定时间  单位是秒
            map.put("cityId",cityId); //cityId是哪个
            map.put("serviceType",serviceType);
            map.put("riderPhone",riderPhone);
            map.put("ruleId",ruleId);//拼车规则id
            map.put("payFlag",1); //支付类型（0-预定人 1-乘车人）
            map.put("customerId",customerId); //预订人的id
            map.put("isPing",1);//是否是拼车（0-否  1-是）
            map.put("endPointLo",endPointLo);//下车地点经度
            map.put("endPointLa",endPointLa);//下车地点纬度
            map.put("channel","zhuanche");
            map.put("groups",group+":"+riderCount); //车型id：乘车人数（34:1,35:1）  车型


            map.put("isDesign","0");//是否指定司机（0-否 1-是）
            map.put("startPoint",startPoint);
            map.put("endPoint",endPoint);

            map.put("isShort",0); //是否简洁版本（0-否 1-是）
            map.put("chargeType",0);//是否是公务卡（0-否 1-是）
            map.put("isProxy",isRemainder==0?0:1);//是否代人叫车（0-否  1-是）根据选择

            map.put("lineType",1);//线路类型（0-往返 1-单程） 周边游使用
            map.put("lineId",null); //线路id 周边游使用 ruleId那个是线路？？？
            map.put("versionId","7.0.1");
            map.put("carType",1); //carType
            map.put("ridePhone",riderPhone);
            map.put("oldCouponId",-1);//之前选择的优惠券id，没有选择过传-1


            map.put("autoCouponFlag",1);//是否手动选券 = 1：没有手动选券，2：手动选过券

            map.put("choiceGroupId",-1);//是否手动选券 = 1：没有手动选券，2：手动选过券
            map.put("pickUpCost",0);//接机员费用 默认0 非接机员0 1是接机员
            map.put("numberOfDays",0);//多日接送:新增天数
            map.put("numberOfCarpoolSeats",riderCount);//疑问 是总座位不是？
            map.put("isUseAmountSign",true);//是否是一口价  true  false
            map.put("source",null);//如果是费用预估页 为h5
            map.put("useExpandFee",false);//是否启用价格策略用户感知功能 默认false
            //map.put("areaId",);//重庆万州需求。如果有这个ID城市就用areaId
            JSONObject jsoParam = new JSONObject();
            jsoParam.put("=============调用预估价入参=============", JSON.toJSON(map));
            logger.info(jsoParam.toJSONString());
            String result = MpOkHttpUtil.okHttpPost(orderCostUrl+"/passenger/orderEstimatedAmount620",map,0,null);
            logger.info("=============调用预估返回结果=============" + result);
            if(StringUtils.isNotEmpty(result)){
                JSONObject jsonResult = JSONObject.parseObject(result);
                if(jsonResult.get("code") != null && jsonResult.getInteger("code") == 0){
                    JSONObject jsonData = jsonResult.getJSONObject("data");
                    if(jsonData != null && jsonData.get("estimated") != null && jsonData.get("estimatedKey") != null){
                        //获取estimatedKey 给计费
                        JSONObject chargeJSON = new JSONObject();
                        String estimatedKey = jsonData.getString("estimatedKey");
                        chargeJSON.put("estimatedKey",estimatedKey);
                        JSONArray arrayEst = jsonData.getJSONArray("estimated");
                        if(arrayEst != null && arrayEst.size() > 0){
                            JSONObject jsonEsti = arrayEst.getJSONObject(0);
                            if(jsonEsti!= null && jsonEsti.get("pingSign")!=null){
                                String  carpoolingKey = jsonEsti.getString("pingSign");
                                String carpoolingKeyStr = ChargeDecrypt.decrypt(carpoolingKey);
                                if(StringUtils.isNotEmpty(carpoolingKeyStr)){
                                    logger.info(carpoolingKeyStr);
                                    if(carpoolingKeyStr.indexOf("-")>0){
                                        String poolingArr[] =  carpoolingKeyStr.split("-");
                                        if(poolingArr.length>0){
                                            chargeJSON.put("estimatedAmount",poolingArr[0]);
                                            return AjaxResponse.success(chargeJSON);
                                        }
                                    }
                                }
                            }

                        }

                    }


                }
            }
        } catch (Exception e) {
            logger.error("未获取到预估价,异常信息" + e);
            return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
        }


        return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
    }


    private String getLineIdBySupplierIds(String supplierIdBatch) {
    	if (StringUtils.isBlank(supplierIdBatch)) {
    		return "";
		}
    	try {
			String linesUrl = configUrl + "/intercityCarUse/getLineIdBySupplierIds";
			Map<String,Object> params = Maps.newHashMap();
			params.put("supplierIds", supplierIdBatch);
			String lineResult = MpOkHttpUtil.okHttpGet(linesUrl,params,1,null);
			logger.info("配置中心供应商--{}查询路线ID集合返回结果集--{}",supplierIdBatch,lineResult);
			if(StringUtils.isNotEmpty(lineResult)){
			    JSONObject jsonResult = JSONObject.parseObject(lineResult);
			    if (jsonResult!=null && jsonResult.getInteger("code")==0) {
					if (jsonResult.get("data")!=null) {
						return jsonResult.get("data").toString();
						
					}
				}
			}
		} catch (Exception e) {
			 logger.error("查询配置路线ID异常" + e);
			return "";
		}
		return "";
	}



}
