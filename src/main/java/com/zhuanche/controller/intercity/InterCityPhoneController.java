package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.mdbcarmanage.MainOrderDetailDTO;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.intercity.MainOrderInterService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.MyRestTemplate;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author fanht
 * @Description
 * @Date 2020/10/19 下午6:39
 * @Version 1.0
 */
@Controller
@RequestMapping("/interCItyPhoneController")
public class InterCityPhoneController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Value("${driver.fee.server.api.base.url}")
    private String driverFeeServiceApiBaseUrl;


    @Value("${assign.url}")
    private String assignUrl;

    @Value("${driver.businessId}")
    private String driverBusinessId;


    @Autowired
    private DriverInfoInterCityExMapper infoInterCityExMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;

    @Autowired
    private MainOrderInterService mainOrderInterService;

    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Value("${ordercost.server.api.base.url}")
    private String orderCostUrl;

    @Value("${query.driver.url}")
    private String queryDriverUrl;


    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    private static final String SPLIT = ",";

    private static final Integer MAX_LENGTH = 500;

    /**
     * 语音播报最近30条数据
     *
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param orderState
     * @param pushDriverType
     * @param serviceType
     * @param orderType
     * @param airportId
     * @param orderSource
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param reserveName
     * @param reservePhone
     * @param riderName
     * @param orderNo
     * @param mainOrderNo
     * @param beginCreateDate
     * @param endCreateDate
     * @param beginCostStartDate
     * @param beginCostEndDate
     * @param riderPhone
     * @return
     */
    @RequestMapping(value = "/newOrderNotice", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse newOrderNotice(Integer pageNum, Integer pageSize, Integer cityId, Integer orderState,
                                       Integer pushDriverType, Integer serviceType, Integer orderType, Integer airportId,
                                       Integer orderSource, String driverName, String driverPhone, String licensePlates,
                                       String reserveName, String reservePhone, String riderName, String orderNo,
                                       String mainOrderNo, String beginCreateDate, String endCreateDate, String beginCostStartDate,
                                       String beginCostEndDate, String riderPhone,@RequestParam(value = "status",defaultValue = "13") Integer status) {
        logger.info(MessageFormat.format("手机端语音播报订单查询入参:pageNum:{0},pageSize:{1},cityId:{2},orderState:" +
                        "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                        "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                        "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostStartDate{20},beginCostEndDate{21},riderPhone:{22},status:{23}", pageNum,
                pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate,
                endCreateDate, beginCostStartDate, beginCostEndDate, riderPhone,status));
        Long currentTime = System.currentTimeMillis();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Map<String, Object> map = Maps.newHashMap();
        map = this.getNoticeMap(pageNum, pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource, driverName, driverPhone, licensePlates,
                reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate, endCreateDate, beginCostStartDate, beginCostEndDate, riderPhone, status,map);
        String supplierIdBatch = "";
        if (!WebSessionUtil.isSupperAdmin()) {
            Set<Integer> suppliersSet = loginUser.getSupplierIds();
            /**如果是供应商级别*/
            if (suppliersSet != null && suppliersSet.size() > 0) {
                StringBuilder supplierBuilder = new StringBuilder();
                for (Integer supplierId : suppliersSet) {
                    supplierBuilder.append(supplierId).append(SPLIT);
                }
                if (StringUtils.isNotEmpty(supplierBuilder.toString())) {
                    supplierIdBatch = supplierBuilder.toString().substring(0, supplierBuilder.toString().length() - 1);
                }
                String lineIds = this.getLineIdBySupplierIds(supplierIdBatch);
                if (StringUtils.isEmpty(lineIds)) {
                    logger.info("=========该供应商未配置线路============");
                    return AjaxResponse.success(null);
                }
                if (StringUtils.isNotBlank(lineIds)) {
                    map.put("ruleIdBatch", lineIds);
                } else {
                    map.put("ruleIdBatch", "-1");
                }
            } else if (CollectionUtils.isNotEmpty(loginUser.getCityIds())) {
                List<CarBizSupplier> querySupplierAllList = carBizSupplierExMapper.querySupplierAllList(loginUser.getCityIds(), null);
                StringBuilder supplierBuilder = new StringBuilder();
                querySupplierAllList.forEach(list -> {
                    supplierBuilder.append(list.getSupplierId()).append(SPLIT);
                });
                if (supplierBuilder.toString().length() > 0) {
                    String allSupplier = supplierBuilder.toString();
                    logger.info("获取所有的合作商id:" + allSupplier);
                    String lineIds = this.getLineIdBySupplierIds(allSupplier.substring(0, allSupplier.length() - 1));
                    if (StringUtils.isEmpty(lineIds)) {
                        logger.info("=========该城市未配置线路============");
                        return AjaxResponse.success(null);
                    }

                    if (StringUtils.isNotBlank(lineIds)) {
                        map.put("ruleIdBatch", lineIds);
                    } else {
                        map.put("ruleIdBatch", "-1");
                    }
                } else {
                    logger.info("=========该城市未配置线路============");
                    return AjaxResponse.success(null);
                }
            }
        }
        JSONArray arrayList = this.getJsonArray(map, currentTime);
        if (arrayList != null && arrayList.size() > 0) {
            return AjaxResponse.success(arrayList.size());
        }
        return AjaxResponse.success(null);
    }



    /***
     *
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param orderState
     * @param pushDriverType
     * @param serviceType
     * @param orderType
     * @param airportId
     * @param orderSource
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param reserveName
     * @param reservePhone
     * @param riderName
     * @param orderNo
     * @param mainOrderNo
     * @param beginCreateDate
     * @param endCreateDate
     * @param beginCostStartDate
     * @param beginCostEndDate
     * @param riderPhone
     * @param map
     * @return
     */
    private Map<String, Object> getNoticeMap(Integer pageNum,
                                             Integer pageSize,
                                             Integer cityId,
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
                                             String riderPhone,
                                             Integer status,
                                             Map<String, Object> map) {
        map.put("pageNo", 1);
        map.put("pageSize", 30);
        map.put("status", status);
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
        /**抢单和供应商无关*/
        map.put("supplierIdBatch", "");
        /**添加排序字段*/
        JSONObject jsonSort = new JSONObject();
        jsonSort.put("field", "createDate");
        jsonSort.put("operator", "desc");
        JSONArray arraySort = new JSONArray();
        arraySort.add(jsonSort);
        map.put("sort", arraySort.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String transId = sdf.format(new Date());
        map.put("transId", transId);
        map.put("serviceTypeIdBatch", String.valueOf(Constants.INTEGER_SERVICE_TYPE));
        return map;
    }


    private String getLineIdBySupplierIds(String supplierIdBatch) {
        if (StringUtils.isBlank(supplierIdBatch)) {
            return "";
        }
        try {
            String linesUrl = configUrl + "/intercityCarUse/getLineIdBySupplierIds";
            Map<String, Object> params = Maps.newHashMap();
            params.put("supplierIds", supplierIdBatch);
            String lineResult = MpOkHttpUtil.okHttpPost(linesUrl, params, 1, null);
            logger.info("配置中心供应商--{}查询路线ID集合返回结果集--{}", supplierIdBatch, lineResult);
            if (StringUtils.isNotEmpty(lineResult)) {
                JSONObject jsonResult = JSONObject.parseObject(lineResult);
                if (jsonResult != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    if (jsonResult.get(Constants.DATA) != null) {
                        /**如果长度超过500 传 -1 ,因为订单那边有参数长度限制*/
                        String ruleBatchResult = jsonResult.get(Constants.DATA).toString();

                        if (ruleBatchResult.split(Constants.SEPERATER).length > MAX_LENGTH) {
                            return Constants.AllRULE;
                        }
                        return jsonResult.get(Constants.DATA).toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("查询配置路线ID异常" + e);
            return "";
        }
        return "";
    }


    private JSONArray getJsonArray(Map<String, Object> map, Long currentTime) {
        JSONArray arrayList = new JSONArray();
        String url = esOrderDataSaasUrl + "/order/v2/search";
        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        try {
            if (StringUtils.isNotEmpty(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                int code = jsonObject.getIntValue(Constants.CODE);
                /**0表示有结果返回*/
                if (code == 0) {
                    JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);
                    if (jsonData != null && jsonData.get(Constants.DATA) != null) {
                        JSONArray arrayData = JSONArray.parseArray(jsonData.get(Constants.DATA).toString());
                        for (int i = 0; i < arrayData.size(); i++) {
                            JSONObject resultObject = arrayData.getJSONObject(i);
                            if (resultObject.get("createDate") != null) {
                                String createDate = resultObject.get("createDate").toString();
                                Long createLong = DateUtils.getDate(createDate, "yyyy-MM-dd HH:mm:ss").getTime();
                                if (currentTime - createLong <= 10000) {
                                    String lineName = resultObject.get("lineName") == null ? "" : resultObject.get("lineName").toString();
                                    String resultOrderNo = resultObject.get("orderNo") == null ? "" : resultObject.get("orderNo").toString();
                                    String bookingDate = resultObject.get("bookingDate") == null ? "" : resultObject.get("bookingDate").toString();
                                    String customerNumber = resultObject.get("factDriverId") == null ? "" : resultObject.get("factDriverId").toString();
                                    Integer offlineIntercityServiceType = resultObject.get("offlineIntercityServiceType") == null ? 0 : resultObject.getInteger("offlineIntercityServiceType");

                                    JSONObject objectNotice = new JSONObject();
                                    objectNotice.put("lineName", lineName);
                                    objectNotice.put("orderNo", resultOrderNo);
                                    objectNotice.put("bookingDate", bookingDate);
                                    objectNotice.put("customerNumber", customerNumber);
                                    objectNotice.put("offlineIntercityServiceType", offlineIntercityServiceType);
                                    arrayList.add(objectNotice);
                                }
                            }
                        }
                        return arrayList;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }


    /**
     * 查询司机列表
     * @param queryParam
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryDriverList")
    public AjaxResponse queryDriverList(String queryParam,
                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        logger.info(MessageFormat.format("手机端查询城际拼车司机入参：queryParam:{0},pageNum:{1},pageSize:{2}", queryParam,
                pageNum, pageSize));


        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Set<Integer> cityIds = new HashSet<>(4);
        Set<Integer> supplierIds = new HashSet<>(4);
        Set<Integer> teamIds = new HashSet<>(4);

        cityIds = loginUser.getCityIds();
        supplierIds = loginUser.getSupplierIds();
        teamIds = loginUser.getTeamIds();


        Page page = PageHelper.startPage(pageNum, pageSize);
        List<DriverInfoInterCity> interCityList = infoInterCityExMapper.phoneQueryMainOrderDrivers(queryParam, cityIds, supplierIds, teamIds);

        int total = (int) page.getTotal();
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, interCityList);

        return AjaxResponse.success(pageDTO);
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
    public AjaxResponse queryDriver(Integer driverId,@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {

        logger.info(MessageFormat.format("手机端查询城际拼车司机详情入参：driverId:{0}",driverId));
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Set<Integer> cityIds = new HashSet<>();
        Set<Integer> supplierIds = new HashSet<>();
        Set<Integer> teamIds = new HashSet<>();

        cityIds = loginUser.getCityIds();
        supplierIds = loginUser.getSupplierIds();
        teamIds = loginUser.getTeamIds();


        Page page = PageHelper.startPage(pageNum, pageSize);
        List<MainOrderInterCity> orderInterCityList = mainOrderInterService.phoneQueryByDriverId(driverId);


        List<MainOrderDetailDTO> interCityList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(orderInterCityList)){
            orderInterCityList.forEach(i ->{
                MainOrderDetailDTO dto = new MainOrderDetailDTO();
                dto.setMainOrder(i.getMainOrderNo());
                dto.setDriverId(driverId);
                dto.setMainOrderName(i.getMainName());
                dto.setMainOrderTime(i.getMainTime());
                interCityList.add(dto);
            });
        }

        if(CollectionUtils.isNotEmpty(interCityList)){
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
                            /**根据司机driverId获取最新的车型以及座位数*/
                            Integer groupId = carBizCarInfoExMapper.groupIdByDriverId(detailDTO.getDriverId());
                            if (groupId == 0) {
                                /**防止车管groupId为0的情况*/
                                groupId = jsonData.getInteger("groupId");
                            }
                            Integer maxSeat = seatCount(groupId);
                            /**剩余座位数*/
                            Integer remainSeat = maxSeat - passengerNums;
                            if (remainSeat <= 0) {
                                remainSeat = 0;
                            }
                            detailDTO.setRemainSeats(remainSeat);
                        }
                    }
                } else {
                    Integer groupId = carBizCarInfoExMapper.groupIdByDriverId(detailDTO.getDriverId());
                    if (groupId == 0) {
                        /**防止车管groupId为0的情况*/
                        groupId = 41;
                    }
                    detailDTO.setRemainSeats(seatCount(groupId));
                }
            }
        }

        int total = (int) page.getTotal();
        PageDTO pageDTO = new PageDTO(pageNum, pageSize, total, interCityList);

        return AjaxResponse.success(pageDTO);
    }

    /**
     * 根据车辆类型获取座位数
     */
    private int seatCount(Integer groupId) {
        int seatNum = 0;
        try {
            seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(groupId);
        } catch (NumberFormatException e) {
            logger.error("获取座位号失败" + e);
        }
        return seatNum;
    }



}
