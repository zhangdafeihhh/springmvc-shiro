package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.zhuanche.common.enums.OrderStateEnum;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.exception.ServiceException;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.util.LbsSignUtil;
import com.zhuanche.common.util.TransportUtils;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.controller.driver.YueAoTongPhoneConfig;
import com.zhuanche.dto.mdbcarmanage.InterCityOrderDTO;
import com.zhuanche.dto.mdbcarmanage.MainOrderDetailDTO;
import com.zhuanche.entity.driver.SupplierExtDto;
import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;
import com.zhuanche.entity.mdbcarmanage.InterDriverLineRel;
import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;
import com.zhuanche.entity.mdbcarmanage.SupplierDistributor;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.common.CitySupplierTeamCommonService;
import com.zhuanche.serv.intercity.MainOrderInterService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.serv.supplier.SupplierDistributorService;
import com.zhuanche.serv.supplier.SupplierRecordService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.*;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.driver.ex.YueAoTongPhoneConfigExMapper;
import mapper.mdbcarmanage.ex.DriverInfoInterCityExMapper;
import mapper.mdbcarmanage.ex.InterDriverLineRelExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Author fanht
 * @Description ?????????????????????
 * @Date 2019/10/14 ??????1:55
 * @Version 1.0
 */
@Controller
@RequestMapping("/integerCity")
public class IntegerCityController {

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
    private MainOrderInterService interService;

    @Autowired
    private CarFactOrderInfoService carFactOrderInfoService;
    @Autowired
    private CitySupplierTeamCommonService citySupplierTeamCommonService;
    @Autowired
    private CarBizSupplierService carBizSupplierService;


    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

    @Autowired
    private SupplierDistributorService distributorService;

    @Autowired
    private SupplierRecordService recordService;

    @Value("${ordercost.server.api.base.url}")
    private String orderCostUrl;

    @Value("${query.driver.url}")
    private String queryDriverUrl;

    @Autowired
    private YueAoTongPhoneConfigExMapper yueAoTongPhoneConfigExMapper;

    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;

    @Autowired
    private InterDriverLineRelExMapper lineRelExMapper;

    private static final String SYSMOL = "&";

    private static final String SPLIT = ",";


    private static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(4,
            new BasicThreadFactory.Builder().namingPattern("bing-master-order-%d").daemon(true).build());

    /**
     * ???????????? wiki???http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=31813392
     *
     * @param pageNum
     * @param pageSize
     * @param cityId
     * @param supplierId
     * @param orderState       ?????????????????????????????????????????????????????????????????????????????????????????????
     * @param pushDriverType   ??????????????????  ??????????????????
     * @param serviceType      ????????????: ?????????????????????????????????
     * @param orderType        ???????????????????????????
     * @param airportId        ???????????????
     * @param orderSource      ???????????? ??????????????????????????????????????????  ??????????????????????????????
     * @param driverName       ????????????
     * @param driverPhone      ???????????????
     * @param licensePlates    ???????????????
     * @param reserveName      ???????????????
     * @param reservePhone     ??????????????????
     * @param riderName        ???????????????
     * @param orderNo          ????????????
     * @param mainOrderNo      ????????????
     * @param beginCreateDate  ??????????????????
     * @param endCreateDate    ??????????????????
     * @param endCostEndDate   ????????????????????????
     * @param beginCostEndDate ????????????????????????
     * @param riderPhone       ??????????????????
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
                                   String riderPhone,
                                   String distributorId,
                                   String lineBeforeName,
                                   String lineAfterName,
                                   String bookingDateSort,
                                   String isCrossDiscountReduction,
                                   Integer payFlag,
                                   Integer offlineIntercityServiceType,
                                   Integer isCarpoolPayChannel) {
        logger.info(MessageFormat.format("??????????????????:pageNum:{0},pageSize:{1},cityId:{2},supplierId:{3},orderState:" +
                        "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                        "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                        "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostEndDate{20},endCostEndDate{21},riderPhone:{22},distributorId:{23}," +
                        "bookingDateSort:{24},payFlag:{25},offlineIntercityServiceType:{26},isCarpoolPayChannel{27}", pageNum,
                pageSize, cityId, supplierId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate,
                endCreateDate, beginCostEndDate, endCostEndDate, riderPhone, distributorId, lineBeforeName, bookingDateSort, payFlag, offlineIntercityServiceType,isCarpoolPayChannel));
        Map<String, Object> map = Maps.newHashMap();
        if (StringUtils.isNotEmpty(lineBeforeName) || StringUtils.isNotEmpty(lineAfterName)) {
            String ruleBatch = this.ruleBatch(lineBeforeName, lineAfterName);
            if (StringUtils.isEmpty(ruleBatch)) {
                logger.info("=====???????????????????????????====");
                return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
            }
            map.put("ruleIdBatch", ruleBatch);
        }
        map = this.getQueryParam(pageNum, pageSize, cityId, supplierId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource, driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo,
                mainOrderNo, beginCreateDate, endCreateDate, beginCostEndDate, endCostEndDate, riderPhone, distributorId, lineBeforeName, bookingDateSort, isCrossDiscountReduction, payFlag, offlineIntercityServiceType, map);
        String supplierIdBatch = this.supplierBatch(cityId, supplierId);
        if (StringUtils.isNotEmpty(supplierIdBatch)) {
            map.put("supplierIdBatch", supplierIdBatch);
        }
        String teamIdBatch = this.teamIdBatch();
        if(StringUtils.isNotEmpty(teamIdBatch())){
            map.put("teamIdBatch",teamIdBatch);
        }
        //????????????????????????
        if(StringUtils.isNotEmpty(isCarpoolPayChannel+"")){
            map.put("isCarpoolPayChannel",isCarpoolPayChannel);
        }
        /**??????????????????*/
        JSONArray arraySort = this.arraySort(bookingDateSort);
        map.put("sort", arraySort.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String transId = sdf.format(new Date());
        map.put("transId", transId);
        String url = esOrderDataSaasUrl + "/order/v2/search";
        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue(Constants.CODE);
            /**0?????????????????????*/
            if (code == 0) {
                JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);
                if (jsonData != null && jsonData.get(Constants.DATA) != null) {
                    /**??????????????????????????????????????????*/
                    return this.response(jsonData);
                }
            }
        }
        /**???????????????????????????*/
        return AjaxResponse.success(null);
    }


    /***
     * ??????????????????????????????????????????????????????????????????id
     * @param lineBeforeName
     * @param lineAfterName
     * @return
     */
    private String ruleBatch(String lineBeforeName, String lineAfterName) {
        List<Integer> ruleBeforeList = null;
        if (StringUtils.isNotEmpty(lineBeforeName)) {
            ruleBeforeList = this.getRuleIdBatch(Constants.BEFORE, lineBeforeName);
            if (CollectionUtils.isEmpty(ruleBeforeList)) {
                logger.info("=======??????????????????");
                return "";
            }
        }
        List<Integer> ruleAfterList = null;
        if (StringUtils.isNotEmpty(lineAfterName)) {
            ruleAfterList = this.getRuleIdBatch(Constants.AFTER, lineAfterName);
            if (CollectionUtils.isEmpty(ruleAfterList)) {
                logger.info("======??????????????????=");
                return "";
            }
        }

        String ruleBatch = "";
        if (StringUtils.isNotEmpty(lineBeforeName) && StringUtils.isNotEmpty(lineAfterName)) {
            ruleBeforeList.retainAll(ruleAfterList);
            ruleBatch = StringUtils.join(ruleBeforeList.toArray(), Constants.SEPERATER);
        } else if (StringUtils.isNotEmpty(lineBeforeName)) {
            ruleBatch = StringUtils.join(ruleBeforeList.toArray(), Constants.SEPERATER);
        } else if (StringUtils.isNotEmpty(lineAfterName)) {
            ruleBatch = StringUtils.join(ruleAfterList.toArray(), Constants.SEPERATER);
        }
        return ruleBatch;
    }

    /**
     * ???????????????
     *
     * @param cityId
     * @param supplierId
     * @return
     */
    private String supplierBatch(Integer cityId, Integer supplierId) {

        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
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
        if (citiesSet != null && citiesSet.size() > 0) {
            List<Integer> listCity = new ArrayList<>(citiesSet);
            listCity.forEach(listCityId -> {
                cityBuilder.append(listCityId).append(SPLIT);
            });
        }
        StringBuilder supplierBuilder = new StringBuilder();
        if (suppliersSet != null && suppliersSet.size() > 0) {
            List<Integer> listSupplier = new ArrayList<>(suppliersSet);
            listSupplier.forEach(listSupplierId -> {
                supplierBuilder.append(listSupplierId).append(SPLIT);
            });
        } else {
            if (!WebSessionUtil.isSupperAdmin() && citiesSet.size() > 0) {
                List<CarBizSupplier> carBizSuppliers = citySupplierTeamCommonService.querySupplierAllList(citiesSet);
                List<Integer> supplierIds = carBizSuppliers.stream().map(f -> f.getSupplierId()).collect(Collectors.toList());
                supplierIds.forEach(supplierEveryId -> {
                    supplierBuilder.append(supplierEveryId).append(SPLIT);
                });
            }
        }

        if (StringUtils.isNotEmpty(supplierBuilder.toString())) {
            supplierIdBatch = supplierBuilder.toString().substring(0, supplierBuilder.toString().length() - 1);
        }
        return supplierIdBatch;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private String teamIdBatch() {
        if (WebSessionUtil.isSupperAdmin()) {
            return null;
        }
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        if (loginUser == null) {
            throw new ServiceException(RestErrorCode.HTTP_INVALID_SESSION);
        }
        String teamIdBatch = null;
        if (loginUser.getLevel() != null && loginUser.getLevel().equals(PermissionLevelEnum.TEAM)) {
            StringBuilder teamBuilder = new StringBuilder();
            Optional.ofNullable(loginUser.getTeamIds()).ifPresent(teamIds -> teamIds.forEach(t-> teamBuilder.append(t).append(SPLIT)));
            if (teamBuilder != null && teamBuilder.length() > 0) {
                teamIdBatch = teamBuilder.toString().substring(0, teamBuilder.toString().length() - 1);
            }
        }
        return teamIdBatch;
    }

    private Map<String, Object> getQueryParam(Integer pageNum,
                                              Integer pageSize,
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
                                              String riderPhone,
                                              String distributorId,
                                              String lineName,
                                              String bookingDateSort,
                                              String isCrossDiscountReduction,
                                              Integer payFlag,
                                              Integer offlineIntercityServiceType,
                                              Map<String, Object> map) {
        map.put("pageNo", pageNum);
        map.put("pageSize", pageSize);
        map.put("status", orderState);
        map.put("pushDriverType", pushDriverType);
        map.put("serviceTypeIdBatch", Constants.INTEGER_SERVICE_TYPE);
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
        map.put("beginCostEndDate", beginCostEndDate);
        map.put("endCostEndDate", endCostEndDate);
        map.put("riderPhone", riderPhone);
        map.put("distributorId", distributorId);
        map.put("offlineIntercityServiceType", offlineIntercityServiceType);

        if (StringUtils.isNotEmpty(isCrossDiscountReduction)) {
            map.put("isCrossDiscountReduction", isCrossDiscountReduction);
        }
        /**?????????????????????  payFlag 1 ??? ??? ?????? ?????? 13  ??????????????????   ????????????*/
        if (payFlag != null && payFlag > 0) {
            map.put("payFlag", payFlag);
        }
        return map;
    }


    private JSONArray arraySort(String bookingDateSort) {
        JSONObject jsonSort = new JSONObject();
        if (StringUtils.isNotEmpty(bookingDateSort) && Constants.BOOKING_DATE_SORT_DESC.equals(bookingDateSort)) {
            jsonSort.put("field", "bookingDate");
            jsonSort.put("operator", "desc");
        } else if (StringUtils.isNotEmpty(bookingDateSort) && Constants.BOOKING_DATE_SORT_ASC.equals(bookingDateSort)) {
            jsonSort.put("field", "bookingDate");
            jsonSort.put("operator", "asc");
        } else {
            jsonSort.put("field", "createDate");
            jsonSort.put("operator", "desc");
        }
        JSONArray arraySort = new JSONArray();
        arraySort.add(jsonSort);
        return arraySort;
    }

    @RequestMapping(value = "/orderWrestQuery", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse orderWrestQuery(@Verify(param = "pageNum", rule = "required") Integer pageNum,
                                        @Verify(param = "pageSize", rule = "required") Integer pageSize,
                                        Integer cityId,
                                        @RequestParam(value = "orderState", defaultValue = "13") Integer orderState,
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
                                        String distributorId,
                                        String bookingDateSort,
                                        Integer offlineIntercityServiceType,
                                        String ruleIdBatch) {
        logger.info(MessageFormat.format("??????????????????:pageNum:{0},pageSize:{1},cityId:{2},orderState:" +
                        "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                        "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                        "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostStartDate{20},beginCostEndDate{21}," +
                        "riderPhone:{22},distributorId:{23},bookingDateSort:{24},offlineIntercityServiceType:{25}", pageNum,
                pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate,
                endCreateDate, beginCostStartDate, beginCostEndDate, riderPhone, distributorId, bookingDateSort, offlineIntercityServiceType));


        Map<String, Object> map = Maps.newHashMap();
        map = this.wrestQueryMap(map, pageNum, pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate, endCreateDate,
                beginCostStartDate, beginCostEndDate, riderPhone, distributorId, bookingDateSort, offlineIntercityServiceType);

        if (StringUtils.isNotEmpty(ruleIdBatch)) {
            map.put("ruleIdBatch", ruleIdBatch);
        } else {
            map = this.wrestQueryParam(map);
        }

        if (map == null) {
            return AjaxResponse.success(null);
        }

        String url = esOrderDataSaasUrl + "/order/v2/search";
        /**???????????????????????????*/
        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            int code = jsonObject.getIntValue(Constants.CODE);
            /**0?????????????????????*/
            if (code == 0) {
                JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);
                if (jsonData != null && jsonData.get(Constants.DATA) != null) {
                    return this.response(jsonData);
                }
            }
        }
        return AjaxResponse.success(null);
    }


    private Map<String, Object> wrestQueryMap(Map<String, Object> map, Integer pageNum,
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
                                              String distributorId,
                                              String bookingDateSort,
                                              Integer offlineIntercityServiceType) {
        map.put("pageNo", pageNum);
        map.put("pageSize", pageSize);
        map.put("status", orderState);
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
        map.put("distributorId", distributorId);
        map.put("offlineIntercityServiceType", offlineIntercityServiceType);
        map.put("supplierIdBatch", "");
        /**??????????????????*/
        JSONObject jsonSort = new JSONObject();
        if (StringUtils.isNotEmpty(bookingDateSort) && Constants.BOOKING_DATE_SORT_DESC.equals(bookingDateSort)) {
            jsonSort.put("field", "bookingDate");
            jsonSort.put("operator", "desc");
        } else if (StringUtils.isNotEmpty(bookingDateSort) && Constants.BOOKING_DATE_SORT_ASC.equals(bookingDateSort)) {
            jsonSort.put("field", "bookingDate");
            jsonSort.put("operator", "asc");
        } else {
            jsonSort.put("field", "createDate");
            jsonSort.put("operator", "desc");
        }
        JSONArray arraySort = new JSONArray();
        arraySort.add(jsonSort);
        map.put("sort", arraySort.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        String transId = sdf.format(new Date());
        map.put("transId", transId);


        //????????????????????????????????????
        map.put("serviceTypeIdBatch", String.valueOf(Constants.INTEGER_SERVICE_TYPE));

        return map;
    }


    /**
     * ??????????????????
     *
     * @param map
     * @return
     */
    private Map<String, Object> wrestQueryParam(Map<String, Object> map) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        String supplierIdBatch = "";
        //??????????????????????????????
        InterDriverLineRel lineRel = lineRelExMapper.queryDriverLineRelByUserId(loginUser.getId());
        if (lineRel != null && StringUtils.isNotEmpty(lineRel.getLineIds())) {
            map.put("ruleIdBatch", lineRel.getLineIds());
        } else {
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

                    if (StringUtils.isEmpty(lineIds)) {
                        logger.info("=========????????????????????????????????????============");
                        return null;
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
                        logger.info("????????????????????????id:" + allSupplier);
                        String lineIds = this.getLineIdBySupplierIds(allSupplier.substring(0, allSupplier.length() - 1));
                        if (StringUtils.isEmpty(lineIds)) {
                            logger.info("=========????????????????????????============");
                            return null;
                        }

                        if (StringUtils.isNotBlank(lineIds)) {
                            map.put("ruleIdBatch", lineIds);
                        } else {
                            map.put("ruleIdBatch", "-1");
                        }
                    } else {
                        logger.info("=========????????????????????????============");
                        return null;
                    }

                }
            }
        }

        return map;
    }

    /**
     * ?????????????????????
     *
     * @param jsonData
     * @return
     */
    private AjaxResponse response(JSONObject jsonData) {
        try {
            JSONArray jsonArray = jsonData.getJSONArray("data");
            if (jsonArray != null && jsonArray.size() > 0) {
                Set<Integer> distributorIds = new HashSet<>();

                Set<Integer> supplierIds = new HashSet<>();

                jsonArray.forEach(array -> {
                    JSONObject disObject = (JSONObject) array;
                    if (disObject.get(Constants.DISTRIBUTORID) != null && disObject.getInteger(Constants.DISTRIBUTORID) > 0) {
                        distributorIds.add(disObject.getInteger(Constants.DISTRIBUTORID));
                    }

                    if (disObject.get(Constants.SUPPLIER_ID) != null && disObject.getInteger(Constants.SUPPLIER_ID) > 0) {
                        supplierIds.add(disObject.getInteger(Constants.SUPPLIER_ID));
                    }
                });

                Map<Integer, String> mapDis = Maps.newHashMap();

                if (CollectionUtils.isNotEmpty(distributorIds)) {
                    List<SupplierDistributor> distributorList = distributorService.distributorList(distributorIds);
                    if (CollectionUtils.isNotEmpty(distributorList)) {
                        distributorList.forEach(list -> {
                            mapDis.put(list.getId(), list.getDistributorName());
                        });
                    }
                }

                Map<Integer, String> supplierMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(supplierIds)) {
                    List<CarBizSupplier> queryNameBySupplierIds = carBizSupplierService.queryNamesByIds(supplierIds);
                    if (CollectionUtils.isNotEmpty(queryNameBySupplierIds)) {
                        queryNameBySupplierIds.forEach(supplierList -> {
                            supplierMap.put(supplierList.getSupplierId(), supplierList.getSupplierFullName());
                        });
                    }
                }


                JSONObject jsonResult = new JSONObject();
                JSONArray jsonDisArray = new JSONArray();

                jsonArray.forEach(array -> {
                    JSONObject disObject = (JSONObject) array;
                    if (disObject.get(Constants.DISTRIBUTORID) != null && disObject.getInteger(Constants.DISTRIBUTORID) > 0) {
                        disObject.put(Constants.DISTRIBUTORNAME, mapDis.get(disObject.getInteger(Constants.DISTRIBUTORID)));
                    } else {
                        disObject.put("distributorName", "");
                    }

                    if (disObject.get(Constants.SUPPLIER_ID) != null && disObject.getInteger(Constants.SUPPLIER_ID) > 0) {
                        disObject.put("supplierFullName", supplierMap.get(disObject.getInteger(Constants.SUPPLIER_ID)));
                    }

                    jsonDisArray.add(disObject);
                });

                jsonResult.put("data", jsonDisArray);
                jsonResult.put("total", jsonData.get("total"));
                jsonResult.put("pageNo", jsonData.get("pageNo"));
                jsonResult.put("totalPage", jsonData.get("totalPage"));
                jsonResult.put("pageSize", jsonData.get("pageSize"));

                return AjaxResponse.success(jsonResult);
            }

        } catch (Exception e) {
            logger.error("??????id?????????????????????" + e);
        }

        return AjaxResponse.success(jsonData);
    }

    /**
     * ??????1
     *
     * @param offlineIntercityServiceType ??????????????????  1:????????????(??????) 2:????????????
     * @param reserveName
     * @param reservePhone
     * @param isSameRider
     * @param riderName
     * @param riderPhone
     * @param riderCount                  ?????????
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
    public AjaxResponse handOperateAddOrderSetp1(@Verify(param = "offlineIntercityServiceType", rule = "required") Integer offlineIntercityServiceType,
                                                 @Verify(param = "reserveName", rule = "required") String reserveName,
                                                 @Verify(param = "reservePhone", rule = "required") String reservePhone,
                                                 @Verify(param = "isSameRider", rule = "required") Integer isSameRider,
                                                 String riderName,
                                                 String riderPhone,
                                                 Integer riderCount,
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
                                                 @Verify(param = "bookingEndShortAddr", rule = "required") String bookingEndShortAddr,
                                                 @Verify(param = "specialRequirement", rule = "maxLength(30)") String specialRequirement) {
        logger.info(MessageFormat.format("handOperateAddOrderSetp1????????????????????????1??????,{0},{1},{2},{3},{4},{5},{6},{7},{8},{9}," +
                        "{10},{11},{12},{13},{14},{15}", reserveName, reservePhone,
                isSameRider, riderName, riderPhone, riderCount, boardingTime, boardingCityId, boardingGetOnX, boardingGetOnY, boardingGetOffCityId,
                boardingGetOffX, boardingGetOffY, offlineIntercityServiceType, specialRequirement));
        try {
            /**???????????????????????????????????????30??????*/
            if (!verifyBoardingTime(boardingTime)) {
                logger.info("???????????????????????????????????????30?????????");
                String beforeHalf = DateUtil.beforeHalfHour(new Date(), 30, DateUtil.TIME_FORMAT);
                return AjaxResponse.fail(RestErrorCode.VERIFY_BOARDING_TIME, beforeHalf);
            }
            /**1.?????????????????????????????????????????????????????????*/
            AjaxResponse res = this.verifyLine(boardingCityId, boardingGetOnX, boardingGetOnY,
                    boardingGetOffCityId, boardingGetOffX, boardingGetOffY);
            if (res.getCode() != RestErrorCode.SUCCESS) {
                return res;
            }
            Map<String, Object> mapRes = (Map<String, Object>) res.getData();
            String ruleId = (String) mapRes.get("ruleId");
            String supplierId = (String) mapRes.get("supplierId");

            /**2.??????????????????????????????userId*/
            Integer customerId = this.getCustomerId(reservePhone);

            if (customerId == null || customerId == 0) {
                logger.info("??????????????????customer??????");
                return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
            }

            /**3.?????????????????????,???????????????????????????????????????*/
            if (Constants.INTER_CITY_CHARTER_TYPE.equals(offlineIntercityServiceType)) {
                riderCount = seatCount(carGroup);
            } else {
                if (riderCount == null) {
                    logger.info("?????????????????????????????????");
                    return AjaxResponse.fail(RestErrorCode.UN_RIDER_COUNT);
                }
            }

            /**---------------???????????????start-----------------------------*/

            Date bookDate = DateUtils.getDate(boardingTime, "yyyy-MM-dd HH:mm:ss");

            long bookingDate = bookDate.getTime();

            String[] ruleStr = ruleId.split(",");
            String[] supplierStr = supplierId.split(",");
            AjaxResponse elsRes = null;
            for (int i = 0; i < ruleStr.length; i++) {
                elsRes = this.getOrderEstimatedAmount620(bookingDate, boardingCityId, Constants.INTEGER_SERVICE_TYPE, riderPhone,
                        ruleStr[i], customerId, boardingGetOffX, boardingGetOffY, riderCount, String.valueOf(carGroup), boardingGetOnX, boardingGetOffY,
                        isSameRider, offlineIntercityServiceType);
                if (elsRes.getCode() == RestErrorCode.SUCCESS) {
                    JSONObject jsonEst = (JSONObject) elsRes.getData();
                    /***??????????????????*/
                    String estimatedAmount = jsonEst.getString("estimatedAmount");
                    /**???????????????????????????0*/
                    BigDecimal bigDecimal = new BigDecimal(estimatedAmount);
                    if (bigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                        logger.info("=========??????????????????????????????==========");
                        /**?????????????????? todo ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????*/
                        ruleId = ruleStr[i];
                        supplierId = supplierStr[i];
                        break;
                    }
                }
            }

            if (elsRes.getCode() != RestErrorCode.SUCCESS) {
                logger.info("?????????????????????");
                return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
            }

            JSONObject jsonEst = (JSONObject) elsRes.getData();
            String estimatedAmount = jsonEst.getString("estimatedAmount");
            String estimatedKey = jsonEst.getString("estimatedKey");
            String pingSettleType = jsonEst.get("pingSettleType") == null ? null : jsonEst.getString("pingSettleType");
            String carpoolPayChannelId = jsonEst.get("carpoolPayChannelId") == null ? null : jsonEst.getString("carpoolPayChannelId");
            logger.info("?????????????????????:" + estimatedAmount);
            logger.info("????????????id:" + carpoolPayChannelId);
            /**???????????????????????????0*/
            BigDecimal bigDecimal = new BigDecimal(estimatedAmount);
            if (bigDecimal.compareTo(BigDecimal.ZERO) != 1) {
                logger.info("???????????????????????????0");
                return AjaxResponse.fail(RestErrorCode.UN_SUPPORT_CAR);
            }
            /**--------------------???????????????end------------------*/

            /**6.-----------------------??????map start--------------------*/
            Map<String, Object> map = Maps.newHashMap();

            StringBuffer sb = new StringBuffer();

            logger.info("=================pingSettleType=========" + pingSettleType);

            if (pingSettleType != null && Constants.PING_SETTLE_TYPE.equals(pingSettleType)) {
                map.put("payFlag", String.valueOf(Constants.PAYFLAG));
                sb.append("payFlag=13").append(SYSMOL);
            } else {
                /**??????????????? 0-??????????????????1-??????????????????2-?????????????????????????????????????????????????????????-1-????????????*/
                map.put("payFlag", "1");
                sb.append("payFlag=1").append(SYSMOL);
            }

            /**??????id*/
            if(StringUtils.isNotEmpty(carpoolPayChannelId)){
                map.put("carpoolPayChannelId", carpoolPayChannelId);
                sb.append("carpoolPayChannelId="+carpoolPayChannelId).append(SYSMOL);
            }
            /**?????????ID*/
            map.put("businessId", Common.BUSSINESSID);
            sb.append("businessId=" + Common.BUSSINESSID).append(SYSMOL);

            map.put("estimatedKey", estimatedKey);
            sb.append("estimatedKey=" + estimatedKey).append(SYSMOL);

            /**??????????????????*/
            map.put("type", "1");
            sb.append("type=1").append(SYSMOL);
            map.put("bookingUserName", reserveName);
            sb.append("bookingUserName=" + reserveName).append(SYSMOL);
            /**???????????? 28 ???????????? http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=37118198*/
            map.put("clientType", 28);
            sb.append("clientType=28").append(SYSMOL);
            /**???????????????????????????*/
            map.put("bookingDate", bookingDate);
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
            /**???????????????*/
            map.put("serviceTypeId", Constants.INTEGER_SERVICE_TYPE);
            sb.append("serviceTypeId=68").append(SYSMOL);

            /**?????????????????? ???1???-????????????2???-?????????*/
            map.put("receiveSMS", "2");
            sb.append("receiveSMS=2").append(SYSMOL);
            map.put("bookingDriverId", "0");
            sb.append("bookingDriverId=0").append(SYSMOL);
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
            /**?????????*/
            map.put("buyoutPrice", estimatedAmount);
            sb.append("buyoutPrice=" + estimatedAmount).append(SYSMOL);
            /**????????????(0:????????????1:??????)*/
            map.put("carpoolMark", 1);
            sb.append("carpoolMark=1").append(SYSMOL);
            map.put("seats", riderCount);
            sb.append("seats=" + riderCount).append(SYSMOL);
            map.put("ruleId", ruleId);
            sb.append("ruleId=" + ruleId).append(SYSMOL);
            map.put("supplierId", supplierId);
            sb.append("supplierId=" + supplierId).append(SYSMOL);
            /**??????????????????*/
            map.put("offlineIntercityServiceType", offlineIntercityServiceType);
            sb.append("offlineIntercityServiceType=" + offlineIntercityServiceType).append(SYSMOL);


            if (StringUtils.isNotBlank(supplierId)) {
                CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(Integer.parseInt(supplierId));
                if (carBizSupplier != null) {
                    map.put("supplierFullName", carBizSupplier.getSupplierFullName());
                    sb.append("supplierFullName=" + carBizSupplier.getSupplierFullName()).append(SYSMOL);
                }
            }
            /**?????????id*/
            map.put("couponId", "111");
            sb.append("couponId=111").append(SYSMOL);
            /**????????????*/
            map.put("estimatedAmount", estimatedAmount);
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
            /**????????????*/
            if (StringUtils.isNotEmpty(specialRequirement)) {
                map.put("specialRequirement", specialRequirement);
                sb.append("specialRequirement=" + specialRequirement).append(SYSMOL);
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
            /**-----------------------??????map end--------------------*/


            String orderUrl = "/order/carpool/create";


            logger.info("??????????????????:" + JSONObject.toJSONString(map));
            String result = carRestTemplate.postForObject(orderUrl, JSONObject.class, map);

            JSONObject orderResult = JSONObject.parseObject(result);
            if (orderResult.get(Constants.CODE) != null && orderResult.getInteger(Constants.CODE) == 0) {
                JSONObject jsonData = orderResult.getJSONObject(Constants.DATA);
                logger.info("========????????????????????????========" + jsonData.toString());
                /**??????data???????????????*/
                Integer returnCode = jsonData.get("returnCode") == null ? null : jsonData.getInteger("returnCode");
                if (returnCode != null && returnCode == 0) {
                    return AjaxResponse.success(jsonData);
                } else if (returnCode != null && Constants.ORDER_RETURN_CODE.equals(returnCode)) {
                    logger.info("???????????????????????????");
                    return AjaxResponse.fail(RestErrorCode.HAS_SUB_ORDER);
                } else if (returnCode != null && Constants.ORDER_INTER_CODE.equals(returnCode)) {
                    logger.info("?????????????????????????????????????????????");
                    return AjaxResponse.fail(RestErrorCode.HAS_PAY_NOT_ORDER);
                }
            }
        } catch (Exception e) {
            logger.error("?????????????????????", e);
            return AjaxResponse.fail(RestErrorCode.FAILED_CREATE_SUB_ORDER);
        }

        return AjaxResponse.success(null);
    }


    /**
     * ??????????????????????????????????????????30????????? ???????????????false
     *
     * @param boardingTime
     * @return
     */
    private boolean verifyBoardingTime(String boardingTime) {
        String beforeHalf = DateUtil.beforeHalfHour(new Date(), 30, DateUtil.TIME_FORMAT);
        double res = DateUtil.getResBetweenTime(beforeHalf, boardingTime, DateUtil.TIME_FORMAT);

        if (res < 0) {
            return false;
        }
        return true;
    }

    /**
     * ????????????
     *
     * @param boardingCityId
     * @param boardingGetOnX
     * @param boardingGetOnY
     * @param boardingGetOffCityId
     * @param boardingGetOffX
     * @param boardingGetOffY
     * @return
     */
    private AjaxResponse verifyLine(Integer boardingCityId, String boardingGetOnX, String boardingGetOnY,
                                    Integer boardingGetOffCityId, String boardingGetOffX, String boardingGetOffY) {
        /**?????????????????????????????????????????????????????????*/

        AjaxResponse boardResponse = hasBoardRoutRights(boardingCityId, boardingGetOnX, boardingGetOnY);

        if (boardResponse.getCode() != RestErrorCode.SUCCESS) {
            logger.info("=============???????????????????????????,??????:boardingGetOnX" + boardingGetOnX + ",boardingGetOnY:" + boardingGetOnY);
            return boardResponse;
        }


        AjaxResponse boardOffResponse = hasBoardOffRoutRights(boardingGetOffCityId, boardingGetOffX, boardingGetOffY);

        if (boardOffResponse.getCode() != RestErrorCode.SUCCESS) {
            logger.info("=============???????????????????????????,??????:boardingGetOffX" + boardingGetOffX + ",boardingGetOffY:" + boardingGetOffY);

            return boardOffResponse;
        }

        String getOnId = boardResponse.getData().toString();

        String getOffId = boardOffResponse.getData().toString();

        AjaxResponse configRouteRes = this.anyRoute(getOnId, getOffId);

        String ruleId = "";
        String supplierId = "";
        if (configRouteRes.getCode() != RestErrorCode.SUCCESS) {
            logger.info("=========????????????id????????????????????????========");
            return configRouteRes;
        }

        JSONObject jsonRoute = (JSONObject) configRouteRes.getData();
        if (jsonRoute != null && jsonRoute.get(Constants.LINEID) != null) {
            ruleId = jsonRoute.get(Constants.LINEID).toString();
            supplierId = jsonRoute.get(Constants.SUPPLIER_ID).toString();
        }

        if (StringUtils.isEmpty(ruleId)) {
            logger.info("==========???????????????????????????=======");
            return AjaxResponse.fail(RestErrorCode.UNFINDED_LINE);
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("ruleId", ruleId);
        map.put("supplierId", supplierId);
        AjaxResponse retRes = AjaxResponse.success(map);
        return retRes;
    }


    /**
     * ?????????????????????customerId
     *
     * @param reservePhone
     * @return
     */
    private Integer getCustomerId(String reservePhone) {
        Integer customerId = null;
        if (StringUtils.isNotEmpty(reservePhone)) {
            String url = "/api/customer/regist";
            Map<String, Object> map = Maps.newHashMap();
            map.put("phone", reservePhone);
            map.put("registerSource", 2);
            map.put("channelNum", "?????????????????????");
            /**????????????id*/
            String registerResult = MpOkHttpUtil.okHttpPost(centerUrl + url, map, 0, null);
            if (StringUtils.isNotEmpty(registerResult)) {
                JSONObject jsonResult = JSONObject.parseObject(registerResult);
                if (jsonResult.get(Constants.CODE) == null || jsonResult.getInteger(Constants.CODE) != 0) {
                    logger.info("??????????????????customer??????", jsonResult.toJSONString());
                    return customerId;
                }
                JSONObject data = jsonResult.getJSONObject("data");
                customerId = data.getInteger("customerId");
            }
        }
        return customerId;
    }

    /**
     * ??????????????????????????????
     *
     * @param orderNo
     */
    private void noticeAssign(String orderNo) {
        Map<String, Object> orderMap = Maps.newHashMap();
        orderMap.put("orderNo", orderNo);
        orderMap.put("businessId", "BusinessPlatform");
        orderMap.put("sign", SignatureUtils.sign(orderMap, driverBusinessId));
        MpOkHttpUtil.okHttpPostAsync(assignUrl + "/v2/carpooling/acrossCityNotify", orderMap, 0, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.info("========??????????????????????????????" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.info("========??????????????????????????????");
            }
        });
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping("/subOrderByQuery")
    public AjaxResponse subOrderByQuery(@Verify(param = "orderNo", rule = "required") String orderNo) {

        logger.info("==================????????????????????????orderNo:" + orderNo);

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
            logger.info("==================???????????????????????????" + JSONObject.toJSONString(map));
            /**wiki?????? http://inside-yapi.01zhuanche.com/project/19/interface/api/3561*/
            JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderByOrderNo", map, 0, "??????????????????");

            if (jsonObject != null && jsonObject.get(Constants.CODE) != null) {
                Integer code = jsonObject.getIntValue(Constants.CODE);
                if (0 == code) {
                    JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);
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
                    String jsonPoint = jsonData.get("bookingStartPoint") == null ? "" : jsonData.getString("bookingStartPoint");
                    if (StringUtils.isNotEmpty(jsonPoint)) {
                        if (jsonPoint.contains(Constants.SPLIT)) {
                            String[] arr = jsonPoint.split(Constants.SPLIT);
                            if (arr.length > 0) {
                                dto.setBookingStartPoint(arr[0]);
                            }
                        } else {
                            dto.setBookingStartPoint(jsonPoint);
                        }
                    }

                    String jsonEnd = jsonData.get("bookingEndPoint") == null ? "" : jsonData.getString("bookingEndPoint");
                    if (StringUtils.isNotEmpty(jsonEnd)) {
                        if (jsonEnd.contains(Constants.SPLIT)) {
                            String[] arr = jsonEnd.split(Constants.SPLIT);
                            if (arr.length > 0) {
                                dto.setBookingEndPoint(arr[0]);
                            }
                        } else {
                            dto.setBookingEndPoint(jsonEnd);
                        }
                    }
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

                    if (jsonData != null && jsonData.get(Constants.MEMO) != null) {
                        JSONObject jsonMemo = jsonData.getJSONObject("memo");
                        dto.setBoardingCityId(jsonMemo.get("startCityId") == null ? "" : jsonMemo.getString("startCityId"));
                        dto.setBoardingGetOffCityId(jsonMemo.get("endCityId") == null ? "" : jsonMemo.getString("endCityId"));
                        dto.setBoardingCityName(jsonMemo.get("startCityName") == null ? "" : jsonMemo.getString("startCityName"));
                        dto.setBoardingGetOffCityName(jsonMemo.get("endCityName") == null ? "" : jsonMemo.getString("endCityName"));
                        dto.setRuleId(jsonMemo.get("ruleId") == null ? "0" : jsonMemo.getString("ruleId"));
                        Integer newCrossServiceType = jsonMemo.get(Constants.NEW_CROSS_SERVICE_TYPE) != null ? jsonMemo.getInteger(Constants.NEW_CROSS_SERVICE_TYPE) : 0;
                        dto.setOfflineIntercityServiceType(newCrossServiceType);

                    }
                    if (StringUtils.isNotEmpty(bookingUserPhone) && StringUtils.isNotEmpty(riderPhone)) {
                        if (bookingUserPhone.equals(riderPhone)) {
                            /**1?????? 0 ??????*/
                            dto.setIsSameRider(1);
                        } else {
                            dto.setIsSameRider(0);
                        }
                    } else {
                        dto.setIsSameRider(0);
                    }
                    dto.setOrderNo(orderNo);
                    if (StringUtils.isNotEmpty(dto.getRuleId())) {
                        Map<String, Object> jsonRouteMap = Maps.newHashMap();
                        jsonRouteMap.put("lineIds", dto.getRuleId());
                        String routeResult = MpOkHttpUtil.okHttpGet(configUrl + "/intercityCarUse/getLineNameByIds", jsonRouteMap, 0, null);
                        logger.info("==============????????????????????????????????????=========" + JSONObject.toJSONString(routeResult));
                        if (StringUtils.isNotEmpty(routeResult)) {
                            JSONObject jsonRoute = JSONObject.parseObject(routeResult);
                            if (jsonRoute.get(Constants.CODE) != null && jsonRoute.getInteger(Constants.CODE) == 0) {
                                JSONArray routeArray = jsonRoute.getJSONArray(Constants.DATA);
                                if (routeArray != null && routeArray.size() > 0) {
                                    JSONObject jsonObjRoute = (JSONObject) routeArray.get(0);
                                    String routeName = jsonObjRoute.get("lineName") == null ? "" : jsonObjRoute.getString("lineName");
                                    dto.setRouteName(routeName);
                                }
                            }
                        }
                    }
                    /**???????????????*/
                    Map<String, Object> bookMap = Maps.newHashMap();
                    bookMap.put("orderNo", orderNo);
                    bookMap.put("name", "bookingUserName");
                    logger.info("====================??????????????????????????????" + JSONObject.toJSONString(bookMap));
                    String bookingResult = MpOkHttpUtil.okHttpGet(orderServiceUrl + "/order/byFields/find", bookMap, 0, null);
                    if (StringUtils.isNotEmpty(bookingResult)) {
                        JSONObject jsonBook = JSONObject.parseObject(bookingResult);
                        if (jsonBook.get(Constants.CODE) != null && jsonBook.getInteger(Constants.CODE) == 0) {
                            JSONObject jsonBookData = jsonBook.getJSONObject(Constants.DATA);
                            if (jsonBookData != null) {
                                String bookingUserName = jsonBookData.get(Constants.BOOKING_USER_NAME) != null ? jsonBookData.getString(Constants.BOOKING_USER_NAME) : "";
                                dto.setReserveName(bookingUserName);
                            }

                        }
                    }

                    try {
                        String mainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(orderNo);
                        if (StringUtils.isNotEmpty(mainOrderNo)) {
                            dto.setMainOrderNo(mainOrderNo);
                            /**??????????????????????????????????????????????????????????????????????????????*/
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

                    /**??????????????????????????? wiki http://inside-yapi.01zhuanche.com/project/19/interface/api/9519*/
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

                    /**wiki?????? http://inside-yapi.01zhuanche.com/project/19/interface/api/9519*/
                    JSONObject jsonShort = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderSpecifiedByOrderNo", shortMap, 0, "?????????????????????");
                    logger.info("=============???????????????????????????=============" + jsonShort.toString());
                    if (jsonShort != null && jsonShort.get(Constants.CODE) != null) {
                        if (jsonShort.getInteger(Constants.CODE) == 0 && jsonShort.get(Constants.DATA) != null) {
                            JSONObject jsonAddress = jsonShort.getJSONObject(Constants.DATA);
                            if (jsonAddress != null && jsonAddress.get(Constants.ORDER_ADDRESS) != null) {
                                JSONObject jsonDetailAdd = jsonAddress.getJSONObject("orderAddress");
                                dto.setBookingStartShortAddr(jsonDetailAdd.get("bookingStartShortAddr") == null ? "" : jsonDetailAdd.getString("bookingStartShortAddr"));
                                dto.setBookingEndShortAddr(jsonDetailAdd.get("bookingEndShortAddr") == null ? "" : jsonDetailAdd.getString("bookingEndShortAddr"));
                            }
                        }
                    }

                    if (dto.getCarGroup() != null) {
                        String carName = carBizCarGroupExMapper.getGroupNameByGroupId(Integer.valueOf(dto.getCarGroup()));
                        dto.setCarGroupName(carName);
                    }
                    return AjaxResponse.success(dto);
                }
            }
        } catch (Exception e) {
            logger.error("??????????????????", e);
        }

        return AjaxResponse.fail(RestErrorCode.ORDER_DETAIL_UNDEFINED);

    }

    /**
     * ????????????
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/editOrder", method = RequestMethod.POST)
    public AjaxResponse editOrder(@Verify(param = "offlineIntercityServiceType", rule = "required") Integer offlineIntercityServiceType,
                                  @Verify(param = "orderNo", rule = "required") String orderNo,
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
                                  String bookingEndShortAddr,
                                  @Verify(param = "specialRequirement", rule = "maxLength(30)") String specialRequirement) {

        logger.info(MessageFormat.format("??????????????????:orderNo{0},reserveName{1},reservePhone{2},isSameRider{3},riderName{4},riderPhone{5}," +
                        "riderCount:{6},boardingTime{7},boardingCityId{8},boardingGetOnX{9},boardingGetOnY{10},boardingGetOffCityId{11}," +
                        "boardingGetOffX:{12},boardingGetOffY:{13},offlineIntercityServiceType:{14}", orderNo, reserveName, reservePhone, isSameRider, riderName, riderPhone, riderCount,
                boardingTime, boardingCityId, boardingGetOnX, boardingGetOnY, boardingGetOffCityId, boardingGetOffX, boardingGetOffY, mainOrderNo,
                status, startCityName, endCityName, bookingStartAddr, bookingEndAddr, carGroup, bookingStartShortAddr, bookingEndShortAddr, offlineIntercityServiceType));

        /**???????????????????????????????????????30??????*/
        if (!verifyBoardingTime(boardingTime)) {
            logger.info("???????????????????????????????????????30?????????");
            String beforeHalf = DateUtil.beforeHalfHour(new Date(), 30, DateUtil.TIME_FORMAT);
            return AjaxResponse.fail(RestErrorCode.VERIFY_BOARDING_TIME, beforeHalf);
        }


        if (Constants.INTER_CITY_CHARTER_TYPE.equals(offlineIntercityServiceType)) {
            riderCount = seatCount(Integer.valueOf(carGroup));
        } else {
            if (riderCount == null) {
                logger.info("?????????????????????????????????");
                return AjaxResponse.fail(RestErrorCode.UN_RIDER_COUNT);
            }
        }

        /**??????????????????????????????????????????????????????????????????*/
        /**?????????????????????????????????????????????????????????*/
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
            AjaxResponse configRouteRes = this.anyRoute(getOnId, getOffId);

            if (configRouteRes.getCode() != RestErrorCode.SUCCESS) {
                logger.info("????????????id????????????????????????");
                return configRouteRes;
            }
        }


        String getOn = boardingGetOnX + "," + boardingGetOnY;
        String getOff = boardingGetOffX + "," + boardingGetOffY;


        AjaxResponse configRouteRes = this.anyRoute(getOnId, getOffId);

        String ruleId = "";
        String supplierId = "";
        if (configRouteRes.getCode() != RestErrorCode.SUCCESS) {
            logger.info("????????????id????????????????????????");
            return configRouteRes;
        }

        JSONObject jsonRoute = (JSONObject) configRouteRes.getData();


        if (jsonRoute != null && jsonRoute.get(Constants.LINEID) != null) {
            ruleId = jsonRoute.get("lineId").toString();
            supplierId = jsonRoute.get("supplierId").toString();
        }

        if (StringUtils.isEmpty(ruleId)) {
            logger.info("???????????????????????????");
            return AjaxResponse.fail(RestErrorCode.UNFINDED_LINE);
        }
        /**???????????????????????????userId*/
        Integer customerId = 0;
        if (StringUtils.isNotEmpty(reservePhone)) {
            String url = "/api/customer/regist";
            Map<String, Object> map = Maps.newHashMap();
            map.put("phone", reservePhone);
            map.put("registerSource", 2);
            map.put("channelNum", "?????????????????????");
            String registerResult = MpOkHttpUtil.okHttpPost(centerUrl + url, map, 0, null);
            if (StringUtils.isNotEmpty(registerResult)) {
                JSONObject jsonResult = JSONObject.parseObject(registerResult);
                if (jsonResult.get(Constants.CODE) == null || jsonResult.getInteger(Constants.CODE) != 0) {
                    logger.info("??????????????????customer??????");
                    return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
                }
                JSONObject data = jsonResult.getJSONObject(Constants.DATA);
                customerId = data.getInteger("customerId");
            }
        }

        if (customerId == null || customerId == 0) {
            logger.info("??????????????????customer??????");
            return AjaxResponse.fail(RestErrorCode.REGISTER_BY_PHONE);
        }

        Date longTimeDate = new Date();
        if (StringUtils.isNotEmpty(boardingTime)) {
            longTimeDate = DateUtils.getDate(boardingTime, "yyyy-MM-dd HH:mm:ss");
        }


        /**???????????????*/
        AjaxResponse elsRes = null;

        String[] ruleStr = ruleId.split(Constants.SEPERATER);
        String[] supplierStr = supplierId.split(Constants.SEPERATER);

        /**?????????????????????*/
        for (int i = 0; i < ruleStr.length; i++) {
            try {
                elsRes = this.getOrderEstimatedAmount620(Long.valueOf(longTimeDate.getTime()), boardingCityId, Constants.INTEGER_SERVICE_TYPE, riderPhone,
                        ruleStr[i], customerId, boardingGetOffX, boardingGetOffY, riderCount, String.valueOf(carGroup), boardingGetOnX,
                        boardingGetOnY, isSameRider, offlineIntercityServiceType);
                if (elsRes.getCode() == RestErrorCode.SUCCESS) {
                    JSONObject jsonEst = (JSONObject) elsRes.getData();
                    /***??????????????????*/
                    String estimatedAmount = jsonEst.getString("estimatedAmount");
                    /**???????????????????????????0*/
                    BigDecimal bigDecimal = new BigDecimal(estimatedAmount);
                    if (bigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                        logger.info("=========??????????????????????????????==========");
                        ruleId = ruleStr[i];
                        supplierId = supplierStr[i];
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("?????????????????????", e);
                return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
            }

        }


        if (elsRes.getCode() != RestErrorCode.SUCCESS) {
            logger.info("?????????????????????");
            return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
        }


        JSONObject jsonEst = (JSONObject) elsRes.getData();
        /**??????????????????*/
        String estimatedAmount = jsonEst.getString("estimatedAmount");
        String estimatedKey = jsonEst.getString("estimatedKey");
        String carpoolPayChannelId = jsonEst.get("carpoolPayChannelId") == null ? null : jsonEst.getString("carpoolPayChannelId");

        logger.info("?????????????????????:" + estimatedAmount);
        /**???????????????????????????0*/
        BigDecimal bigDecimal = new BigDecimal(estimatedAmount);
        if (bigDecimal.compareTo(BigDecimal.ZERO) != 1) {
            logger.info("???????????????????????????0");
            return AjaxResponse.fail(RestErrorCode.UN_SUPPORT_CAR);
        }

        Map<String, Object> map = Maps.newHashMap();
        List<String> list = new ArrayList<>();

        //??????id
        if(StringUtils.isNotEmpty(carpoolPayChannelId)){
            map.put("carpoolPayChannelId", carpoolPayChannelId);
            list.add("carpoolPayChannelId="+carpoolPayChannelId);
        }

        /**?????????????????????*/
        map.put("offlineIntercityServiceType", offlineIntercityServiceType);
        list.add("offlineIntercityServiceType=" + offlineIntercityServiceType);
        /**????????????*/
        map.put("estimatedAmount", estimatedAmount);
        list.add("estimatedAmount=" + estimatedAmount);
        /**?????????*/
        map.put("buyoutPrice", estimatedAmount);
        list.add("buyoutPrice=" + estimatedAmount);
        map.put("estimatedKey", estimatedKey);
        list.add("estimatedKey=" + estimatedKey);

        map.put("businessId", Common.BUSSINESSID);
        list.add("businessId=" + Common.BUSSINESSID);
        map.put("orderNo", orderNo);
        list.add("orderNo=" + orderNo);
        if (StringUtils.isNotEmpty(getOn)) {
            /**???????????????????????????id*/
            map.put("factStartPoint", getOn);
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
        /**????????????????????????(1 ????????????| 2 ????????????| 3 ??????????????????)*/
        map.put("pushDriverType", OrderStateEnum.MANUAL_ASSIGNMENT.getCode());
        list.add("pushDriverType=" + OrderStateEnum.MANUAL_ASSIGNMENT.getCode());

        if (boardingCityId != null) {
            map.put("factStartAddr", boardingCityId);
            list.add("factStartAddr=" + boardingCityId);
        }


        if (boardingGetOffCityId != null) {
            map.put("factEndAddr", boardingGetOffCityId);
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

        if (StringUtils.isNotEmpty(specialRequirement)) {
            map.put("specialRequirement", specialRequirement);
            list.add("specialRequirement=" + specialRequirement);
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
        logger.info("???????????????" + JSONObject.toJSONString(map));
        String editResult = carRestTemplate.postForObject(url, JSONObject.class, map);
        logger.info("???????????????" + editResult);
        if (StringUtils.isNotBlank(editResult)) {
            JSONObject updateResult = JSONObject.parseObject(editResult);
            if (updateResult.get(Constants.CODE) != null && updateResult.getInteger(Constants.CODE) == 0) {
                logger.info("????????????");
                JSONObject editJson = new JSONObject();
                editJson.put("orderNo", orderNo);
                /**?????????????????????????????????????????????????????????????????????????????????*/
                try {
                    Map<String, Object> chargeMap = Maps.newHashMap();
                    chargeMap.put("orderNo", orderNo);
                    chargeMap.put("bookingUserId", customerId);
                    chargeMap.put("cityId", boardingCityId);
                    chargeMap.put("bookingDate", boardingTime);
                    chargeMap.put("bookingStartPoint", getOn);
                    chargeMap.put("bookingEndPoint", getOff);
                    chargeMap.put("serviceTypeId", Constants.INTEGER_SERVICE_TYPE);
                    chargeMap.put("estimatedKey", estimatedKey);
                    chargeMap.put("bookingGroupId", carGroup);
                    chargeMap.put("riderPhone", riderPhone);
                    /**saas??????*/
                    chargeMap.put("channelsNum", "saas");
                    chargeMap.put("bid", 30);

                    String chargeResult = MpOkHttpUtil.okHttpPost(driverFeeServiceApiBaseUrl + "/scfl/updateEstimateInfo", chargeMap, 0, null);
                    logger.info("????????????????????????:" + chargeResult);
                } catch (Exception e) {
                    logger.info("????????????????????????:" + e);
                }


                return AjaxResponse.success(editJson);
            }
        }

        return AjaxResponse.fail(RestErrorCode.UPDATE_SUB_ORDER_FAILED);
    }


    /**
     * ????????????
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
        logger.info("=============??????????????????:orderNo???" + orderNo + ",status:" + status + "==============");

        logger.info(MessageFormat.format("??????????????????:orderNo:{0},status:{1},cityId:{2},serviceTypeId:{3}," +
                        "carGroupId:{4},bookingUserPhone:{5},ruleId:{6}", orderNo, status, cityId, serviceTypeId, carGroupId,
                bookingUserPhone, ruleId));


        try {
            Map<String, Object> map = Maps.newHashMap();
            List<String> listStr = new ArrayList<>();
            map.put("businessId", Common.BUSSINESSID);
            listStr.add("businessId=" + Common.BUSSINESSID);
            map.put("orderNo", orderNo);
            listStr.add("orderNo=" + orderNo);
            map.put("cancelReasonId", 24);
            listStr.add("cancelReasonId=" + 24);
            map.put("memo", "??????????????????????????????");
            listStr.add("memo=??????????????????????????????");
            /**1-?????????2-?????????3-?????????4-??????*/
            map.put("cancelType", 853);
            listStr.add("cancelType=853");
            /**?????????????????????*/
            map.put("cancelStatus", 11);
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
            if (status > Constants.ORDER_STATUS) {
                url = "/order/carpool/syncCancelSubOrder";
            }
            String result = carRestTemplate.postForObject(url, JSONObject.class, map);
            logger.info("============????????????????????????:" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject jsonResult = JSONObject.parseObject(result);
                if (jsonResult != null && jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    logger.info("??????????????????");
                } else {
                    logger.info("????????????????????????????????????????????????10105678");
                    return AjaxResponse.fail(RestErrorCode.ORDER_CANCEL_FAILED);
                }
            }
        } catch (Exception e) {
            logger.info("?????????????????????????????????" + e);
            return AjaxResponse.fail(RestErrorCode.CANCEL_FAILED);

        }


        /**???????????????????????????http://inside-yapi.01zhuanche.com/project/88/interface/api/16255*/
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
            logger.info("=======????????????????????????==========" + JSONObject.toJSONString(chargeMap));
            String chargeResult = MpOkHttpUtil.okHttpPost(orderCostUrl + "/cancel/carpool/payCancelDamage", chargeMap,
                    0, null);
            logger.info("========????????????????????????????????????========" + chargeResult);
            if (StringUtils.isNotEmpty(chargeResult)) {
                JSONObject chargeJson = JSONObject.parseObject(chargeResult);
                if (chargeJson.get(Constants.CODE) != null && chargeJson.getInteger(Constants.CODE) == 0) {
                    logger.info("??????????????????????????????");
                    try {
                        logger.info("=========????????????????????????????????????========" + orderNo);
                        this.noticeAssign(orderNo);
                    } catch (Exception e) {
                        logger.error("????????????????????????" + e);
                    }
                    return AjaxResponse.success(null);
                }
            }
        } catch (Exception e) {
            return AjaxResponse.fail(RestErrorCode.CHARGE_CANCEL_FAILED);
        }

        return AjaxResponse.fail(RestErrorCode.CANCEL_FAILED);
    }


    /**
     * ????????????
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
                                    Integer teamId,
                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        logger.info(MessageFormat.format("?????????????????????????????????supplierId:{0},driverName:{1},driverPhpne:{2},license:{3}", supplierId,
                driverName, driverPhone, license));


        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Set<Integer> cityIds = new HashSet<>();
        Set<Integer> supplierIds = new HashSet<>();
        Set<Integer> teamIds = new HashSet<>();

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
        if (teamId != null) {
            teamIds.add(teamId);
        } else {
            teamIds = loginUser.getTeamIds();
        }
        Page page = PageHelper.startPage(pageNum, pageSize);
        List<MainOrderDetailDTO> interCityList = infoInterCityExMapper.queryDriver(cityId, supplierId, driverName, driverPhone, license, cityIds, supplierIds, teamId, teamIds);

        if (CollectionUtils.isNotEmpty(interCityList)) {
            for (MainOrderDetailDTO detailDTO : interCityList) {
                if (StringUtils.isNotEmpty(detailDTO.getMainOrder())) {
                    //???????????????
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
                            /**????????????driverId????????????????????????????????????*/
                            Integer groupId = carBizCarInfoExMapper.groupIdByDriverId(detailDTO.getDriverId());
                            if (groupId == 0) {
                                /**????????????groupId???0?????????*/
                                groupId = jsonData.getInteger("groupId");
                            }
                            Integer maxSeat = seatCount(groupId);
                            /**???????????????*/
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
                        /**????????????groupId???0?????????*/
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
     * ????????????
     *
     * @param mainTime
     * @return
     */
    @RequestMapping(value = "/addMainOrder", method = RequestMethod.POST)
    public AjaxResponse addMainOrder(String mainOrderNo,
                                     String mainTime) {

        logger.info("??????????????????");
        int code = interService.updateMainTime(mainOrderNo, mainTime);
        if (code > 0) {
            logger.info("???????????????????????????");
            return AjaxResponse.success(null);
        }

        return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
    }


    /**
     * ??????
     *
     * @param mainOrderNo
     * @param orderNo
     * @param driverId
     * @param driverName
     * @param driverPhone
     * @param licensePlates
     * @param groupId
     * @param crossCityStartTime ?????????????????????????????????????????????????????????????????????
     * @param routeName
     * @return
     */
    @RequestMapping("/handOperateAddOrderSetp2")
    @ResponseBody
    public AjaxResponse handOperateAddOrderSetp2(String mainOrderNo,
                                                 @Verify(param = "orderNo", rule = "required") String orderNo,
                                                 @Verify(param = "driverId", rule = "required") Integer driverId,
                                                 @Verify(param = "driverName", rule = "required") String driverName,
                                                 @Verify(param = "driverPhone", rule = "required") String driverPhone,
                                                 @Verify(param = "licensePlates", rule = "required") String licensePlates,
                                                 @Verify(param = "groupId", rule = "required") String groupId,
                                                 String crossCityStartTime,
                                                 String routeName) {
        logger.info("??????????????????:mainOrderNo=" + mainOrderNo + ",orderNo:" + orderNo
                + ",driverId:" + driverId + ",driverName:" + driverName + ",driverPhone:" + driverPhone + ",licensePlates:" + licensePlates
                + ",groupId:" + groupId + ",crossCityStartTime:" + crossCityStartTime + ",routeName:" + routeName);
        boolean bl = this.queryOrderByDriverId(orderNo, driverId);

        if (!bl) {
            logger.info("====??????????????????????????????????????????=====");
            return AjaxResponse.fail(RestErrorCode.HAS_ORDER_DRIVER_ID);
        }

        if (!verifyLine(orderNo, driverId)) {
            logger.info("???????????????????????????????????????,driverId:" + driverId);
            return AjaxResponse.fail(RestErrorCode.HAS_DRIVER_PERMISSION);
        }

        if (!verifyHasPermission(driverId)) {
            logger.info("???????????????????????????????????????,driverId:" + driverId);
            return AjaxResponse.fail(RestErrorCode.HAS_DRIVER_PERMISSION);
        }


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
        //todo ????????????????????????????????????????????? ??????????????????id???????????????

        /**??????driverId??????groupId*/
        Integer newGroupId = carBizCarInfoExMapper.groupIdByDriverId(driverId);

        if (newGroupId != null && newGroupId > 0) {
            map.put("carGroupId", newGroupId);
            listParam.add("carGroupId=" + newGroupId);
            int carSeatNums = seatCount(Integer.valueOf(newGroupId));
            map.put("carSeatNums", carSeatNums);
            listParam.add("carSeatNums=" + carSeatNums);
        } else {
            logger.info("==========???????????????????????????========= ???????????????????????????");
            map.put("carGroupId", groupId);
            listParam.add("carGroupId=" + groupId);
            int carSeatNums = seatCount(Integer.valueOf(groupId));
            map.put("carSeatNums", carSeatNums);
            listParam.add("carSeatNums=" + carSeatNums);
        }

        if (StringUtils.isNotEmpty(crossCityStartTime)) {
            Date startTime = DateUtils.getDate(crossCityStartTime, "yyyy-MM-dd HH:mm:ss");
            map.put("crossCityStartTime", startTime.getTime());
            listParam.add("crossCityStartTime=" + startTime.getTime());
        }

        if (StringUtils.isNotEmpty(routeName)) {
            map.put("routeName", routeName);
            listParam.add("routeName=" + routeName);
        }

        String dispatcherPhone = getOpePhone(driverId);

        /**????????????????????????*/
        if (WebSessionUtil.getCurrentLoginUser().getMobile() != null) {
            map.put("dispatcherPhone", dispatcherPhone);
            listParam.add("dispatcherPhone=" + dispatcherPhone);
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
        logger.info("??????????????????:" + result);
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonResult = JSONObject.parseObject(result);
            if (jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                logger.info("????????????????????????");
                this.noticeAssign(orderNo);
                Future<String> future = executorService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        logger.info("=================??????????????????============" + jsonResult.getJSONObject(Constants.DATA));
                        JSONObject jsonData = jsonResult.getJSONObject(Constants.DATA);
                        try {
                            if (jsonData != null && jsonData.get(Constants.MAIN_ORDER_NO) != null) {
                                String newMainOrderNo = jsonData.getString("mainOrderNo");
                                MainOrderInterCity queryMain = interService.queryMainOrder(newMainOrderNo);
                                logger.info("========????????????=======" + JSONObject.toJSONString(queryMain));
                                int code = 0;
                                if (queryMain != null && queryMain.getId() > 0) {
                                    code = interService.updateMainOrderState(newMainOrderNo, 1, dispatcherPhone);
                                } else {
                                    MainOrderInterCity main = new MainOrderInterCity();
                                    main.setDriverId(driverId);
                                    main.setCreateTime(new Date());
                                    main.setUpdateTime(new Date());
                                    main.setMainName(routeName);
                                    main.setStatus(MainOrderInterCity.orderState.NOTSETOUT.getCode());
                                    main.setMainOrderNo(newMainOrderNo);
                                    main.setOpePhone(dispatcherPhone);
                                    main.setMainTime(crossCityStartTime);
                                    code = interService.addMainOrderNo(main);
                                }
                                if (code > 0) {
                                    logger.info("=========????????????????????????=======");
                                    return String.valueOf(code);
                                }
                                return String.valueOf(code);
                            }
                        } catch (Exception e) {
                            logger.error("??????????????????=======" + e);
                            String newMainOrderNo = jsonData.getString("mainOrderNo");
                            MainOrderInterCity queryMain = interService.queryMainOrder(newMainOrderNo);
                            logger.info("????????????????????????====" + JSONObject.toJSONString(queryMain));
                            if (queryMain != null && queryMain.getId() > 0) {
                                int code = interService.updateMainOrderState(newMainOrderNo, 1, dispatcherPhone);
                                if (code > 0) {
                                    logger.info("=====??????????????????=====");
                                }
                            }
                        }
                        return "=============??????????????????==========";
                    }
                });

                return AjaxResponse.success(null);


            } else {
                logger.info("??????????????????????????????========" + jsonResult.toString());
                return AjaxResponse.failMsg(jsonResult.getIntValue("code"), jsonResult.getString("msg"));
            }

        } else {
            return AjaxResponse.fail(RestErrorCode.FAILED_GET_MAIN_ORDER);
        }

    }


    /**
     * ?????????????????????
     *
     * @param orderNo
     * @param driverId
     * @return
     */
    private boolean verifyLine(String orderNo, Integer driverId) {

        Integer ruleId = 0;
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("bId", Common.BUSSINESSID);
            map.put("orderNo", orderNo);
            String sign = null;
            try {
                sign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(map, Common.MAIN_ORDER_KEY));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            map.put("sign", sign);
            logger.info("==================???????????????????????????" + JSONObject.toJSONString(map));
            /**wiki?????? http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=37123085*/
            JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderByOrderNo", map, 0, "??????????????????");

            if (jsonObject != null && jsonObject.get(Constants.CODE) != null) {
                Integer code = jsonObject.getIntValue(Constants.CODE);
                if (0 == code) {
                    JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);

                    if (jsonData != null && jsonData.get(Constants.MEMO) != null) {
                        JSONObject jsonMemo = jsonData.getJSONObject("memo");
                        logger.info("=======??????memo??????=====" + JSONObject.toJSONString(jsonMemo));
                        ruleId = jsonMemo.get(Constants.RULEID) != null ? jsonMemo.getInteger(Constants.RULEID) : 0;
                    }
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        final Integer lineId = ruleId;

        logger.info("============???????????????????????????????????????==========");
        final boolean[] bl = {false};

        DriverInfoInterCity driverInfo = infoInterCityExMapper.getByDriverId(driverId);
        if (driverInfo == null) {
            logger.info("===??????????????????===");
            return bl[0];
        }
        Integer supplierId = driverInfo.getSupplierId();
        if (supplierId == null || supplierId == 0) {
            logger.info("===??????????????????===");
            return bl[0];
        }


        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("supplierId", supplierId);
            map.put("status", 1);

            logger.info("==============???????????????id????????????========" + JSONObject.toJSONString(map));
            JSONObject jsonResult = MpOkHttpUtil.okHttpGetBackJson(configUrl + "/intercityCarUse/getLineListBySupplierId", map, 0, "???????????????id????????????");
            logger.info("=============????????????======" + jsonResult.toString());

            if (jsonResult != null && jsonResult.get(Constants.CODE) != null) {
                Integer code = jsonResult.getIntValue(Constants.CODE);
                if (code == 0) {
                    JSONArray jsonData = jsonResult.getJSONArray(Constants.DATA);
                    if (jsonData != null && jsonData.size() > 0) {
                        jsonData.forEach(json -> {
                            JSONObject jsonEach = (JSONObject) json;
                            if (jsonEach != null && jsonEach.get("lineId") != null) {
                                Integer userLineId = jsonEach.get("lineId") == null ? 0 : jsonEach.getInteger("lineId");
                                if (userLineId.equals(lineId)) {
                                    logger.info("===??????????????????=====" + jsonEach.get("lineName").toString());
                                    bl[0] = true;
                                }
                            }
                        });
                    }

                }
            }
        } catch (Exception e) {
            logger.info("????????????", e);
        }
        return bl[0];
    }

    /**
     * ???????????????????????????????????????
     *
     * @param driverId
     * @return
     */
    private boolean verifyHasMainOrder(Integer driverId) {
        final boolean[] bl = {true};

        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("driverId", driverId);
            map.put("businessId", Common.BUSSINESSID);
            Date beforeDate = DateUtils.afterNHoursDate(new Date(), -Constants.VERIFY_HOUR);
            /**TODO ?????? ???????????????????????????????????????????????? ???????????????*/
            map.put("bookingStartTime", beforeDate.getTime());
            map.put("bookingEndTime", System.currentTimeMillis());

            String sign = null;
            try {
                sign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(map, Common.MAIN_ORDER_KEY));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            map.put("sign", sign);
            logger.info("==============????????????????????????========" + JSONObject.toJSONString(map));
            JSONObject jsonResult = MpOkHttpUtil.okHttpGetBackJson(carRestUrl + "/order/carpool/driver/getDriverCrossServiceMainOrderList", map, 0, "????????????????????????");

            logger.info("=============????????????======" + jsonResult.toString());

            if (jsonResult != null && jsonResult.get(Constants.CODE) != null) {
                Integer code = jsonResult.getIntValue(Constants.CODE);
                if (code == 0) {
                    JSONArray jsonData = jsonResult.getJSONArray(Constants.DATA);
                    jsonData.forEach(json -> {
                        JSONObject jsonEach = (JSONObject) json;
                        if (jsonEach != null && jsonEach.get("mainOrderNo") != null) {
                            Integer status = jsonEach.get("status") == null ? 60 : jsonEach.getInteger("status");
                            if (status < 44) {
                                logger.info("===???????????????=====" + jsonEach.get("mainOrderNo").toString() + ",?????????" + status);
                                bl[0] = false;
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            logger.info("????????????", e);
        }
        return bl[0];
    }

    /**
     * ?????????????????????????????????????????????-??????????????????
     *
     * @param orderNo
     * @param driverId
     * @return
     */
    private boolean queryOrderByDriverId(String orderNo, Integer driverId) {
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("bId", Common.BUSSINESSID);
            map.put("orderNo", orderNo);
            String sign = null;
            try {
                sign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(map, Common.MAIN_ORDER_KEY));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            map.put("sign", sign);
            logger.info("==================???????????????????????????" + JSONObject.toJSONString(map));
            /**wiki?????? http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=37123085*/
            JSONObject jsonObject = MpOkHttpUtil.okHttpGetBackJson(orderServiceUrl + "/orderMain/getOrderByOrderNo", map, 0, "??????????????????");

            if (jsonObject != null && jsonObject.get(Constants.CODE) != null) {
                Integer code = jsonObject.getIntValue(Constants.CODE);
                if (0 == code) {
                    JSONObject jsonData = jsonObject.getJSONObject(Constants.DATA);
                    String bookingDate = jsonData.get("bookingDate") == null ? "" : jsonData.getString("bookingDate");

                    Integer newCrossServiceType = 0;
                    if (jsonData != null && jsonData.get(Constants.MEMO) != null) {
                        JSONObject jsonMemo = jsonData.getJSONObject("memo");
                        logger.info("=======??????memo??????=====" + JSONObject.toJSONString(jsonMemo));
                        newCrossServiceType = jsonMemo.get(Constants.NEW_CROSS_SERVICE_TYPE) != null ? jsonMemo.getInteger(Constants.NEW_CROSS_SERVICE_TYPE) : 0;
                    }

                    if (StringUtils.isNotEmpty(bookingDate) && Constants.INTER_CITY_CHARTER_TYPE.equals(newCrossServiceType)) {
                        String longToStr = DateUtils.convertLongToString(Long.valueOf(bookingDate), DateUtils.dateTimeFormat_parttern);
                        Date bookingTime = DateUtils.parseDateStr(longToStr, DateUtils.dateTimeFormat_parttern);
                        Date bookingStartTime = DateUtils.afterNHoursDate(bookingTime, -Constants.VERIFY_HOUR);
                        Date bookingEndTime = DateUtils.afterNHoursDate(bookingTime, Constants.VERIFY_HOUR);
                        Map<String, Object> orderMap = Maps.newHashMap();
                        orderMap.put("bId", Common.BUSSINESSID);
                        orderMap.put("driverId", driverId);
                        orderMap.put("bookingDateStart", DateUtils.formatDate(bookingStartTime, DateUtils.dateTimeFormat_parttern));
                        orderMap.put("bookingDateEnd", DateUtils.formatDate(bookingEndTime, DateUtils.dateTimeFormat_parttern));
                        orderMap.put("status", Constants.VERIFY_STATUS);
                        String orderSign = null;
                        try {
                            orderSign = MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(orderMap, Common.MAIN_ORDER_KEY));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        orderMap.put("sign", orderSign);
                        /**wiki?????? http://inside-yapi.01zhuanche.com/project/22/interface/api/69102*/
                        logger.info("========?????????????????????????????????????????????========" + JSONObject.toJSONString(orderMap));
                        JSONObject orderObject = MpOkHttpUtil.okHttpGetBackJson(queryDriverUrl + "/driverWait/selectOrderDriverWaitByDriverId", orderMap, 0, "?????????????????????????????????????????????????????????");
                        logger.info("========???????????????????????????????????????result========" + JSONObject.toJSONString(orderObject));
                        if (orderObject != null && orderObject.get(Constants.CODE) != null) {
                            if (0 == orderObject.getInteger(Constants.CODE)) {
                                JSONArray arrayData = orderObject.getJSONArray(Constants.DATA);
                                if (arrayData != null && arrayData.size() > 0) {
                                    logger.info("=======??????????????????????????????====" + JSONObject.toJSONString(arrayData));
                                    return false;
                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error("=====????????????===" + e);
        }

        return true;
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param driverId
     * @return
     */
    private boolean verifyHasPermission(Integer driverId) {
        boolean bl = false;
        DriverInfoInterCity driverInfo = infoInterCityExMapper.getByDriverId(driverId);
        if (driverInfo == null) {
            logger.info("??????????????????");
            return bl;
        }


        if (WebSessionUtil.isSupperAdmin()) {
            bl = true;
        } else {
            SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
            Set<Integer> cities = loginUser.getCityIds();
            Set<Integer> suppliers = loginUser.getSupplierIds();

            if (CollectionUtils.isNotEmpty(suppliers)) {
                if (suppliers.contains(driverInfo.getSupplierId())) {
                    bl = true;
                }
            } else if (CollectionUtils.isNotEmpty(cities)) {
                if (cities.contains(driverInfo.getCityId())) {
                    bl = true;
                }
            } else {
                bl = true;
            }
        }
        return bl;

    }

    /**
     * ??????
     *
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
    public AjaxResponse updateOtherMainOrder(String mainOrderNo,
                                             @Verify(param = "orderNo", rule = "required") String orderNo,
                                             @Verify(param = "driverId", rule = "required") Integer driverId,
                                             @Verify(param = "driverPhone", rule = "required") String driverPhone,
                                             @Verify(param = "licensePlates", rule = "required") String licensePlates,
                                             @Verify(param = "cityId", rule = "required") Integer cityId,
                                             @Verify(param = "groupId", rule = "required") String groupId,
                                             String crossCityStartTime,
                                             String routeName) {
        logger.info(MessageFormat.format("???????????????mainOrderNo:{0},orderNo:{1},driverId:{2},driverPhone:{3},licensePlates{4},cityId{5}" +
                        ",groupId:{6},crossCityStartTime:{7},routeName:{8}", mainOrderNo, orderNo, driverId, driverPhone, licensePlates, cityId, groupId, crossCityStartTime,
                routeName));

        /**todo ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????**/
        try {
            if (StringUtils.isNotEmpty(mainOrderNo)) {
                /**????????????????????????*/
                String curMainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(orderNo);
                if (mainOrderNo.equals(curMainOrderNo)) {
                    curMainOrderNo = null;
                }
            }

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
            map.put("licensePlates", licensePlates);
            listParam.add("licensePlates=" + licensePlates);


            //??????driverId??????groupId
            Integer newGroupId = carBizCarInfoExMapper.groupIdByDriverId(driverId);
            if (newGroupId != null && newGroupId > 0) {
                map.put("groupId", newGroupId);
                listParam.add("groupId=" + newGroupId);
                int carSeatNums = seatCount(Integer.valueOf(newGroupId));
                map.put("carSeatNums", carSeatNums);
                listParam.add("carSeatNums=" + carSeatNums);
            } else {
                logger.info("==========???????????????????????????========= ???????????????????????????");
                map.put("groupId", groupId);
                listParam.add("groupId=" + groupId);
                int carSeatNums = seatCount(Integer.valueOf(groupId));
                map.put("carSeatNums", carSeatNums);
                listParam.add("carSeatNums=" + carSeatNums);
            }

            map.put("cityId", cityId);
            listParam.add("cityId=" + cityId);
            if (StringUtils.isNotEmpty(routeName)) {
                map.put("routeName", routeName);
                listParam.add("routeName=" + routeName);

            }

            if (StringUtils.isNotEmpty(driverPhone)) {
                map.put("driverPhone", driverPhone);
                listParam.add("driverPhone=" + driverPhone);
            }

            String dispatcherPhone = getOpePhone(driverId);

            //????????????????????????
            if (dispatcherPhone != null) {
                map.put("dispatcherPhone", dispatcherPhone);
                listParam.add("dispatcherPhone=" + dispatcherPhone);
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


            String url = "/order/carpool/crossCityOrderReassign";


            String result = carRestTemplate.postForObject(url, JSONObject.class, map);
            logger.info("????????????result:" + result);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonResult = JSONObject.parseObject(result);
                if (jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    logger.info("=====????????????====== ??????????????????????????????===??????:orderNo:" + orderNo + ",driverId:" + driverId);
                    try {
                        Map<String, Object> mapParam = Maps.newHashMap();
                        mapParam.put("driverId", driverId);
                        mapParam.put("orderNo", orderNo);
                        mapParam.put("businessId", "BusinessPlatform");
                        mapParam.put("sign", SignatureUtils.sign(mapParam, driverBusinessId));
                        String reassignResult = MpOkHttpUtil.okHttpPost(assignUrl + "/v2/carpooling/reassignResult", mapParam, 0, null);
                        logger.info("==========??????????????????????????????=======" + JSONObject.toJSONString(reassignResult));
                    } catch (Exception e) {
                        logger.error("====??????????????????????????????==" + e);
                    }

                    Future<String> future = executorService.submit(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            //???????????????????????????????????????
                            logger.info("==============??????????????????===========" + jsonResult.getJSONObject("data"));
                            try {
                                JSONObject jsonData = jsonResult.getJSONObject("data");
                                String newMainOrderNo = jsonData.getString("mainOrderNo");

                                MainOrderInterCity queryMainOrder = interService.queryMainOrder(newMainOrderNo);

                                if (queryMainOrder == null || queryMainOrder.getId() == 0) {
                                    int code = 0;
                                    MainOrderInterCity main = new MainOrderInterCity();
                                    main.setDriverId(driverId);
                                    main.setCreateTime(new Date());
                                    main.setUpdateTime(new Date());
                                    main.setMainName(routeName);
                                    main.setStatus(MainOrderInterCity.orderState.NOTSETOUT.getCode());
                                    main.setMainOrderNo(newMainOrderNo);
                                    main.setOpePhone(dispatcherPhone);
                                    main.setMainTime(crossCityStartTime);
                                    logger.info("=====?????????????????????" + JSONObject.toJSONString(main));
                                    code = interService.addMainOrderNo(main);
                                    logger.info("=====???????????????code:" + code);
                                    if (code > 0) {
                                        logger.info("=============????????????????????????=========");
                                        return String.valueOf(code);
                                    }
                                } else {
                                    logger.info("=========????????????=======" + JSONObject.toJSONString(queryMainOrder));
                                    if (queryMainOrder != null && queryMainOrder.getId() > 0) {
                                        int code = interService.updateMainOrderState(newMainOrderNo, 1, dispatcherPhone);
                                        if (code > 0) {
                                            logger.info("=============????????????????????????=========");
                                            return String.valueOf(code);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("???????????????????????????????????????");

                                JSONObject jsonData = jsonResult.getJSONObject("data");
                                String newMainOrderNo = jsonData.getString("mainOrderNo");

                                MainOrderInterCity queryMainOrder = interService.queryMainOrder(newMainOrderNo);

                                if (queryMainOrder != null && queryMainOrder.getId() > 0) {
                                    int code = interService.updateMainOrderState(newMainOrderNo, 1, dispatcherPhone);
                                    if (code > 0) {
                                        logger.info("=============????????????????????????=========");
                                        return String.valueOf(code);
                                    }
                                }

                                return "=============??????????????????==========" + e;
                            }
                            return "=============????????????==========";
                        }
                    });


                    return AjaxResponse.success(null);
                } else {
                    logger.info("???????????????");
                    return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
                }
            }
        } catch (Exception e) {
            logger.error("????????????" + e);
            return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
        }

        return AjaxResponse.fail(RestErrorCode.CHANGE_MAIN_FAILED);
    }


    /**
     * ?????????????????????????????????
     */
    private int seatCount(Integer groupId) {
        int seatNum = 0;
        try {
            seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(groupId);
        } catch (NumberFormatException e) {
            logger.error("?????????????????????" + e);
        }
        return seatNum;
    }

    private List<String> list(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<String> strList = new ArrayList<>();
        String[] strArr = str.split(Constants.AND);
        for (int i = 0; i < strArr.length; i++) {
            strList.add(strArr[i]);
        }
        return strList;
    }


    /**
     * ????????????
     *
     * @param type     0(????????????) 2(???????????????
     * @param platform H5 3
     * @param cityName ????????????
     * @param cityId   ??????id
     * @return
     */
    @ResponseBody
    @RequestMapping("/inputtips")
    public AjaxResponse inputtips(@Verify(param = "type", rule = "required") String type,
                                  @Verify(param = "platform", rule = "required") String platform,
                                  @Verify(param = "cityName", rule = "required") String cityName,
                                  @Verify(param = "cityId", rule = "required") String cityId,
                                  @Verify(param = "channel", rule = "required") String channel,
                                  String inCoordType,
                                  String cityLimit,
                                  String userId,
                                  String mobile,
                                  Integer soType,
                                  Integer serviceType,
                                  String lang,
                                  String version,
                                  String redPacket,
                                  String keywords) {
        logger.info("????????????");
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Map<String, Object> map = Maps.newHashMap();
        map.put("type", type);
        map.put("platform", platform);
        map.put("channel", channel);
        map.put("sessionId", loginUser.getId() + "_" + System.currentTimeMillis());
        map.put("cityName", cityName);
        map.put("cityId", cityId);
        if (StringUtils.isNotEmpty(inCoordType)) {
            map.put("inCoordType", inCoordType);
        }
        if (StringUtils.isNotEmpty(cityLimit)) {
            map.put("cityLimit", cityLimit);
        }
        if (StringUtils.isNotEmpty(userId)) {
            map.put("userId", userId);
        }
        if (StringUtils.isNotEmpty(mobile)) {
            map.put("mobile", mobile);
        }
        if (soType != null) {
            map.put("soType", soType);
        }
        if (serviceType != null) {
            map.put("serviceType", serviceType);
        }
        if (StringUtils.isNotEmpty(lang)) {
            map.put("lang", lang);
        }
        if (StringUtils.isNotEmpty(version)) {
            map.put("version", version);
        }
        if (StringUtils.isNotEmpty(redPacket)) {
            map.put("redPacket", redPacket);
        }
        if (StringUtils.isNotEmpty(keywords)) {
            map.put("keywords", keywords);
        }

        boolean typeBl = type != null && ("1".equals(type) || "2".equals(type));
        if (typeBl) {
            if (StringUtils.isEmpty(keywords)) {
                return AjaxResponse.fail(RestErrorCode.KEYWORDS_IS_NOT_NULL);
            }
        }
        String url = searchUrl + "/assistant/inputtips";

        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonResult = JSONObject.parseObject(result);
            if (jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                return AjaxResponse.success(jsonResult.get(Constants.DATA));
            } else {
                return AjaxResponse.failMsg(jsonResult.getIntValue(Constants.CODE), jsonResult.getString("msg"));
            }
        }
        return AjaxResponse.success(null);
    }


    /**
     * ??????lbs???????????? wiki http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=328062
     *
     * @param lng
     * @param lat
     * @param platform
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/regeo", method = RequestMethod.GET)
    public AjaxResponse regeo(String lng,
                              String lat,
                              String platform) {
        logger.info("?????????????????????");
        Map<String, Object> map = Maps.newHashMap();
        map.put("lng", lng);
        map.put("lat", lat);
        map.put("platform", platform);
        String url = searchUrl + "/geocode/regeo";
        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        if (StringUtils.isNotEmpty(result)) {
            JSONObject jsonResult = JSONObject.parseObject(result);
            if (jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                return AjaxResponse.success(jsonResult.get(Constants.DATA));
            } else {
                return AjaxResponse.failMsg(jsonResult.getIntValue(Constants.CODE), jsonResult.getString("msg"));
            }
        }
        return AjaxResponse.success(null);
    }


    /**
     * ??????????????? ????????????????????? wiki:http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=11178396
     *
     * @param boardingCityId
     * @param boardingGetOnX
     * @param boardingGetOnY
     * @return
     */
    private AjaxResponse hasBoardRoutRights(Integer boardingCityId, String boardingGetOnX, String boardingGetOnY) {
        //?????????????????????????????????????????????????????????
        Map<String, Object> mapX = Maps.newHashMap();
        mapX.put("token", lbsToken);
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cityId", boardingCityId);
        jsonObject.put("x", boardingGetOnX);
        jsonObject.put("y", boardingGetOnY);
        array.add(jsonObject);
        mapX.put("coordinateList", array.toString());
        String lbsSign = LbsSignUtil.sign(mapX, lbsToken);
        mapX.put("sign", lbsSign);
        String lbsResult = MpOkHttpUtil.okHttpPost(lbsUrl + "/area/getAreaByCoordinate", mapX, 0, null);
        String getOnId = "";
        if (StringUtils.isNotEmpty(lbsResult)) {
            JSONObject jsonResult = JSONObject.parseObject(lbsResult);
            if (jsonResult.get(Constants.CODE) == null || jsonResult.getInteger(Constants.CODE) != 0 ||
                    jsonResult.get(Constants.DATA) == null) {
                logger.info("?????????????????????");
                return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
            }

            JSONArray arrayData = jsonResult.getJSONArray(Constants.DATA);
            if (arrayData == null) {
                logger.info("????????????????????????");
                return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
            }

            for (int i = 0; i < arrayData.size(); i++) {
                JSONObject lbsRes = arrayData.getJSONObject(i);
                if (lbsRes.get("areaId") != null) {
                    getOnId += lbsRes.getString("areaId") + SPLIT;
                }

            }
        }

        if (StringUtils.isEmpty(getOnId)) {
            logger.info("??????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
        }

        return AjaxResponse.success(getOnId.substring(0, getOnId.length() - 1));
    }


    /**
     * ???????????????????????????????????? wiki:http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=11178396
     *
     * @param boardingGetOffCityId
     * @param boardingGetOffX
     * @param boardingGetOffY
     * @return
     */
    private AjaxResponse hasBoardOffRoutRights(Integer boardingGetOffCityId, String boardingGetOffX, String boardingGetOffY) {
        Map<String, Object> mapY = Maps.newHashMap();

        mapY.put("token", lbsToken);
        JSONArray arrayY = new JSONArray();
        JSONObject jsonY = new JSONObject();
        jsonY.put("cityId", boardingGetOffCityId);
        jsonY.put("x", boardingGetOffX);
        jsonY.put("y", boardingGetOffY);
        arrayY.add(jsonY);
        mapY.put("coordinateList", arrayY.toString());
        String lbsSignY = LbsSignUtil.sign(mapY, lbsToken);
        mapY.put("sign", lbsSignY);

        String resultY = MpOkHttpUtil.okHttpGet(lbsUrl + "/area/getAreaByCoordinate", mapY, 0, null);

        String getOffId = "";

        if (resultY != null) {
            JSONObject jsonResultY = JSONObject.parseObject(resultY);
            if (jsonResultY.get(Constants.CODE) == null || jsonResultY.getInteger(Constants.CODE) != 0 ||
                    jsonResultY.get(Constants.DATA) == null) {
                logger.info("?????????????????????");
                return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
            }

            JSONArray jsonArray = jsonResultY.getJSONArray(Constants.DATA);
            if (jsonArray.size() == 0) {
                logger.info("?????????????????????");
                return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
            }


            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject lbsRes = jsonArray.getJSONObject(i);
                if (lbsRes.get("areaId") != null) {
                    getOffId += lbsRes.getString("areaId") + SPLIT;
                }
            }
        }

        if (StringUtils.isEmpty(getOffId)) {
            logger.info("??????????????????????????????");
            return AjaxResponse.fail(RestErrorCode.ADD_NOT_RIGHT);
        }
        return AjaxResponse.success(getOffId.substring(0, getOffId.length() - 1));
    }


    /**
     * ??????????????????????????????
     *
     * @param getOnIds
     * @param getOffIds
     * @return
     */
    private AjaxResponse anyRoute(String getOnIds, String getOffIds) {
        StringBuilder builderLines = new StringBuilder();
        StringBuilder builderSupplierId = new StringBuilder();
        int count = 0;
        String[] getOnId = getOnIds.split(SPLIT);
        String[] getOffId = getOffIds.split(SPLIT);
        for (int i = 0; i < getOnId.length; i++) {
            for (int j = 0; j < getOffId.length; j++) {
                count++;
                AjaxResponse response = hasRoute(getOnId[i], getOffId[j]);
                if (response.getCode() == 0) {
                    logger.info("???????????????????????????????????????id:" + getOnId[i] + ",????????????id???" + getOffId[j] + ",???????????????" + count
                            + ",????????????:" + JSONObject.toJSONString(response.getData()));
                    JSONObject jsonRes = (JSONObject) response.getData();
                    builderLines.append(jsonRes.getString("lineId")).append(",");
                    builderSupplierId.append(jsonRes.getString("supplierId")).append(",");
                    //return response;
                }
            }
        }
        if (StringUtils.isEmpty(builderLines.toString())) {
            logger.info("???????????????????????????,????????????:" + count);
            return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
        } else {
            JSONObject jsonRoute = new JSONObject();
            jsonRoute.put("lineId", builderLines.toString().substring(0, builderLines.toString().length() - 1));
            jsonRoute.put("supplierId", builderSupplierId.toString().substring(0, builderSupplierId.toString().length() - 1));
            return AjaxResponse.success(jsonRoute);
        }

    }

    /**
     * ???????????????????????? http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=40172935
     *
     * @param getOnId
     * @param getOffId
     * @return
     */
    private AjaxResponse hasRoute(String getOnId, String getOffId) {
        JSONObject jsonRoute = new JSONObject();
        try {
            Integer areaStartRangId = Integer.valueOf(getOnId);
            Integer areaEndRangId = Integer.valueOf(getOffId);
            String areaUrl = configUrl + "/intercityCarUse/getLineSupplier?areaStartRangeId=" + areaStartRangId + "&areaEndRangeId=" + areaEndRangId;

            String areaResult = MpOkHttpUtil.okHttpGet(areaUrl, null, 0, null);
            if (StringUtils.isNotEmpty(areaResult)) {
                JSONObject jsonResult = JSONObject.parseObject(areaResult);
                if (jsonResult.get(Constants.DATA) != null && jsonResult.get(Constants.DATA) != "") {
                    //?????????????????? ?????????????????????
                    SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
                    Set<Integer> cities = loginUser.getCityIds();
                    Set<Integer> suppliers = loginUser.getSupplierIds();
                    JSONObject jsonData = jsonResult.getJSONObject(Constants.DATA);
                    if (jsonData.getString(Constants.SUPPLIER_ID) == null) {
                        logger.info("????????????????????????");
                        return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                    }
                    String supp = jsonData.getString("supplierId");
                    String[] inteSupp = supp.split(",");

                    LinkedList<Integer> linkedList = new LinkedList<>();
                    for (int i = 0; i < inteSupp.length; i++) {
                        linkedList.add(Integer.valueOf(inteSupp[i]));
                    }
                    boolean bl = false;
                    if (suppliers.size() > 0) {
                        HashSet<Integer> hashSet = new HashSet<>(suppliers);
                        if (jsonData.get(Constants.SUPPLIER_ID) != null) {
                            Iterator<Integer> iterator = linkedList.iterator();
                            while (iterator.hasNext()) {
                                Integer supplierId = iterator.next();
                                if (hashSet.contains(supplierId)) {
                                    logger.info("???????????????");
                                    jsonRoute.put("lineId", jsonData.getIntValue("lineId"));
                                    jsonRoute.put("lineName", jsonData.getString("lineName"));
                                    jsonRoute.put("supplierId", supplierId);
                                    bl = true;
                                    break;
                                }
                            }
                        }

                        if (!bl) {
                            return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                        }
                    } else if (cities.size() > 0) {
                        /**????????????????????????*/
                        List<CarBizSupplier> carBizSuppliers = citySupplierTeamCommonService.querySupplierAllList(cities);
                        List<Integer> supplierIds = carBizSuppliers.stream().map(f -> f.getSupplierId()).collect(Collectors.toList());
                        supplierIds.retainAll(linkedList);
                        if (supplierIds.size() > 0) {
                            jsonRoute.put("lineId", jsonData.getIntValue("lineId"));
                            jsonRoute.put("lineName", jsonData.getString("lineName"));
                            jsonRoute.put("supplierId", supplierIds.get(0));
                            bl = true;
                        }

                        if (!bl) {
                            return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                        }
                    } else {
                        jsonRoute.put("lineId", jsonData.getIntValue("lineId"));
                        jsonRoute.put("lineName", jsonData.getString("lineName"));
                        jsonRoute.put("supplierId", linkedList.get(0));
                    }

                    logger.info("?????????????????????");
                } else {
                    logger.info("????????????????????????");
                    return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
                }
            }
        } catch (NumberFormatException e) {
            logger.error("??????????????????" + e.getMessage());
            return AjaxResponse.fail(RestErrorCode.UNDEFINED_LINE);
        }
        return AjaxResponse.success(jsonRoute);
    }


    /**
     * ??????????????? wiki: http://inside-yapi.01zhuanche.com/project/88/interface/api/3957
     *
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
                                                    Integer isRemainder,
                                                    Integer offlineIntercityServiceType) throws Exception {
        try {
            Map<String, Object> map = Maps.newHashMap();
            /**????????????*/
            map.put("duration", 0);
            /**????????????*/
            map.put("distance", 0);
            /**?????????*/
            map.put("tolls", 0);
            /**????????????  ????????????*/
            map.put("bookingDate", bookingDate / 1000);
            /**cityId?????????*/
            map.put("cityId", cityId);
            map.put("serviceType", serviceType);
            map.put("riderPhone", riderPhone);
            /**????????????id*/
            map.put("ruleId", ruleId);
            /**???????????????0-????????? 1-????????????*/
            map.put("payFlag", 1);
            /**????????????id*/
            map.put("customerId", customerId);
            /**??????????????????0-???  1-??????*/
            map.put("isPing", 1);
            /**??????????????????*/
            map.put("endPointLo", endPointLo);
            /**??????????????????*/
            map.put("endPointLa", endPointLa);
            map.put("channel", "zhuanche");
            /**?????????????????????0-??? 1-??????*/
            map.put("isDesign", "0");
            map.put("startPoint", startPoint);
            map.put("endPoint", endPoint);
            /**?????????????????????0-??? 1-??????*/
            map.put("isShort", 0);
            /**?????????????????????0-??? 1-??????*/
            map.put("chargeType", 0);
            /**?????????????????????0-???  1-??????????????????*/
            map.put("isProxy", isRemainder == 0 ? 0 : 1);
            /**???????????????0-?????? 1-????????? ???????????????*/
            map.put("lineType", 1);
            /**??????id ??????????????? ruleId????????????????????????*/
            map.put("lineId", null);
            map.put("versionId", "7.0.1");
            map.put("carType", 1);
            map.put("ridePhone", riderPhone);
            /**????????????????????????id?????????????????????-1*/
            map.put("oldCouponId", -1);

            /**?????????????????? = 1????????????????????????2??????????????????*/
            map.put("autoCouponFlag", 1);
            /**?????????????????? = 1????????????????????????2??????????????????*/
            map.put("choiceGroupId", -1);
            /**??????????????? ??????0 ????????????0 1????????????*/
            map.put("pickUpCost", 0);
            /**????????????:????????????*/
            map.put("numberOfDays", 0);
            /**?????? ?????????????????????*/
            if (Constants.INTER_CITY_CHARTER_TYPE.equals(offlineIntercityServiceType)) {
                map.put("numberOfCarpoolSeats", Constants.BAO_CHE_RIDER_COUNT);
                /**??????id??????????????????34:1,35:1???  ??????*/
                map.put("groups", group + ":" + Constants.BAO_CHE_RIDER_COUNT);
            } else {
                /**??????id??????????????????34:1,35:1???  ??????*/
                map.put("groups", group + ":" + riderCount);
                map.put("numberOfCarpoolSeats", riderCount);
            }


            /**??????????????????  true  false*/
            map.put("isUseAmountSign", true);
            /**???????????????????????? ???h5*/
            map.put("source", Constants.SAAS_PRECE);
            /**?????????????????????????????????????????? ??????false*/
            map.put("useExpandFee", false);
            JSONObject jsoParam = new JSONObject();
            jsoParam.put("=============?????????????????????=============", JSON.toJSON(map));
            logger.info(jsoParam.toJSONString());
            String result = MpOkHttpUtil.okHttpPost(orderCostUrl + "/passenger/orderEstimatedAmount620", map, 0, null);
            logger.info("=============????????????????????????=============" + result);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject jsonResult = JSONObject.parseObject(result);
                if (jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    JSONObject jsonData = jsonResult.getJSONObject(Constants.DATA);
                    if (jsonData != null && jsonData.get(Constants.ESTIMATED) != null && jsonData.get(Constants.ESTIMATED_KEY) != null) {
                        /***??????estimatedKey ?????????*/
                        JSONObject chargeJson = new JSONObject();
                        String estimatedKey = jsonData.getString("estimatedKey");
                        chargeJson.put("estimatedKey", estimatedKey);
                        JSONArray arrayEst = jsonData.getJSONArray("estimated");

                        if (arrayEst != null && arrayEst.size() > 0) {
                            JSONObject jsonEsti = arrayEst.getJSONObject(0);
                            if (jsonEsti != null && jsonEsti.get(Constants.PING_SIGN) != null) {
                                String carpoolingKey = jsonEsti.getString("pingSign");
                                String pingSettleType = jsonEsti.get("pingSettleType") == null ? null : jsonEsti.getString("pingSettleType");
                                if (StringUtils.isNotEmpty(pingSettleType)) {
                                    chargeJson.put("pingSettleType", pingSettleType);
                                }
                                String carpoolingKeyStr = ChargeDecrypt.decrypt(carpoolingKey);
                                if (StringUtils.isNotEmpty(carpoolingKeyStr)) {
                                    logger.info(carpoolingKeyStr);
                                    if (carpoolingKeyStr.indexOf(Constants.SHORT_STOKE) > 0) {
                                        String[] poolingArr = carpoolingKeyStr.split("-");
                                        if(poolingArr.length>=7){
                                            chargeJson.put("estimatedAmount", poolingArr[0]);
                                            if(!Objects.isNull(poolingArr[6])){
                                                chargeJson.put("carpoolPayChannelId", poolingArr[6]);
                                            }
                                            logger.info("=============?????????????????????????????????==========" + chargeJson);
                                            return AjaxResponse.success(chargeJson);
                                        }else{
                                            chargeJson.put("estimatedAmount", poolingArr[0]);
                                            logger.info("=============?????????????????????==========" + chargeJson);
                                            return AjaxResponse.success(chargeJson);
                                        }
                                    }
                                }
                            }

                        }

                    }

                }
            }
        } catch (Exception e) {
            logger.error("?????????????????????,????????????" + e);
            return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
        }


        return AjaxResponse.fail(RestErrorCode.UNGET_PRICE);
    }


    private String getLineIdBySupplierIds(String supplierIdBatch) {
        if (StringUtils.isBlank(supplierIdBatch)) {
            return "";
        }
        InterDriverLineRel lineRel = lineRelExMapper.queryDriverLineRelByUserId(WebSessionUtil.getCurrentLoginUser().getId());
        if (lineRel != null && StringUtils.isNotEmpty(lineRel.getLineIds())) {
            return lineRel.getLineIds();
        }
        try {
            String linesUrl = configUrl + "/intercityCarUse/getLineIdBySupplierIds";
            Map<String, Object> params = Maps.newHashMap();
            params.put("supplierIds", supplierIdBatch);
            String lineResult = MpOkHttpUtil.okHttpPost(linesUrl, params, 1, null);
            logger.info("?????????????????????--{}????????????ID?????????????????????--{}", supplierIdBatch, lineResult);
            if (StringUtils.isNotEmpty(lineResult)) {
                JSONObject jsonResult = JSONObject.parseObject(lineResult);
                if (jsonResult != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    if (jsonResult.get(Constants.DATA) != null) {
                        /**??????????????????500 ??? -1 ,???????????????????????????????????????*/
                        String ruleBatchResult = jsonResult.get(Constants.DATA).toString();

                        if (ruleBatchResult.split(Constants.SEPERATER).length > 500) {
                            return Constants.AllRULE;
                        }
                        return jsonResult.get(Constants.DATA).toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("??????????????????ID??????" + e);
            return "";
        }
        return "";
    }


    /**
     * ??????????????????30?????????
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
                                       String beginCostEndDate, String riderPhone) {
        logger.info(MessageFormat.format("??????????????????????????????:pageNum:{0},pageSize:{1},cityId:{2},orderState:" +
                        "{4},orderPushDriverType:{5},serviceType:{6},orderType:{7},airportId:{8},orderSource:{9},driverName:" +
                        "{10},driverPhone:{11},licensePlates:{12},reserveName:{13},reservePhone:{14},riderName:{15},orderNo:{16}," +
                        "mainOrderNo:{17},beginCreateDate:{18},endCreateDate{19},beginCostStartDate{20},beginCostEndDate{21},riderPhone:{22}", pageNum,
                pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource,
                driverName, driverPhone, licensePlates, reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate,
                endCreateDate, beginCostStartDate, beginCostEndDate, riderPhone));
        Long currentTime = System.currentTimeMillis();
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();

        Map<String, Object> map = Maps.newHashMap();
        map = this.getNoticeMap(pageNum, pageSize, cityId, orderState, pushDriverType, serviceType, orderType, airportId, orderSource, driverName, driverPhone, licensePlates,
                reserveName, reservePhone, riderName, orderNo, mainOrderNo, beginCreateDate, endCreateDate, beginCostStartDate, beginCostEndDate, riderPhone, map);
        String supplierIdBatch = "";
        if (!WebSessionUtil.isSupperAdmin()) {
            Set<Integer> suppliersSet = loginUser.getSupplierIds();
            /**????????????????????????*/
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
                    logger.info("=========???????????????????????????============");
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
                    logger.info("????????????????????????id:" + allSupplier);
                    String lineIds = this.getLineIdBySupplierIds(allSupplier.substring(0, allSupplier.length() - 1));
                    if (StringUtils.isEmpty(lineIds)) {
                        logger.info("=========????????????????????????============");
                        return AjaxResponse.success(null);
                    }

                    if (StringUtils.isNotBlank(lineIds)) {
                        map.put("ruleIdBatch", lineIds);
                    } else {
                        map.put("ruleIdBatch", "-1");
                    }
                } else {
                    logger.info("=========????????????????????????============");
                    return AjaxResponse.success(null);
                }
            }
        }
        JSONArray arrayList = this.getJsonArray(map, currentTime);
        if (arrayList != null && arrayList.size() > 0) {
            return AjaxResponse.success(arrayList);
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
                                             Map<String, Object> map) {
        map.put("pageNo", 1);
        map.put("pageSize", 30);
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
        /**????????????????????????*/
        map.put("supplierIdBatch", "");
        /**??????????????????*/
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


    private JSONArray getJsonArray(Map<String, Object> map, Long currentTime) {
        JSONArray arrayList = new JSONArray();
        String url = esOrderDataSaasUrl + "/order/v2/search";
        String result = MpOkHttpUtil.okHttpGet(url, map, 0, null);
        try {
            if (StringUtils.isNotEmpty(result)) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                int code = jsonObject.getIntValue(Constants.CODE);
                /**0?????????????????????*/
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
     * ????????????????????????
     */
    private String getOpePhone(Integer driverId) {
        logger.info("==========????????????????????????????????????driverId=====" + driverId);
        String opePhone = null;
        /**????????????id???????????????id*/
        DriverInfoInterCity city = infoInterCityExMapper.getByDriverId(driverId);
        if (city != null && city.getSupplierId() > 0) {
            SupplierExtDto dto = recordService.extDtoDetail(city.getSupplierId());
            if (dto != null && StringUtils.isNotEmpty(dto.getCustomerPhone())) {
                opePhone = StringUtils.isNotEmpty(dto.getCustomerLineNumber()) ? dto.getCustomerPhone() + "/" + dto.getCustomerLineNumber() : dto.getCustomerPhone();
            } else {

                YueAoTongPhoneConfig config = this.queryOpePhone(city.getSupplierId().toString());
                if (config == null || StringUtils.isEmpty(config.getPhone())) {
                    /**??????????????????????????????????????????????????????????????????*/
                    opePhone = city.getDriverPhone();
                } else {
                    opePhone = config.getPhone();
                }

            }

        }
        if (StringUtils.isEmpty(opePhone)) {
            opePhone = WebSessionUtil.getCurrentLoginUser().getMobile();
        }
        logger.info("==========??????????????????????????????=====" + opePhone);
        return opePhone;
    }


    /**
     * ???????????????id???????????????
     *
     * @param suppliers
     * @return
     */
    private YueAoTongPhoneConfig queryOpePhone(String suppliers) {
        List<YueAoTongPhoneConfig> list = yueAoTongPhoneConfigExMapper.findBySupplierId(suppliers);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * lineType 1 ???????????? 2 ????????????
     * ??????????????????????????????id
     */
    private List<Integer> getRuleIdBatch(Integer lineType, String lineName) {
        logger.info("====???????????????????????????=====start");
        List<Integer> listLineIds = new ArrayList<>();
        try {
            Map<String, Object> requestMap = new HashMap<>(4);
            requestMap.put("lineModel", 2);
            if (StringUtils.isNotEmpty(lineName)) {
                requestMap.put("lineName", lineName);
            }

            String configResult = MpOkHttpUtil.okHttpPost(configUrl + "/intercityCarUse/getIntercityCarSharingList", requestMap, 0, null);

            logger.info("=====????????????===" + configResult);
            if (!org.springframework.util.StringUtils.isEmpty(configResult)) {
                JSONObject jsonResult = JSONObject.parseObject(configResult);
                if (jsonResult.get(Constants.CODE) != null && jsonResult.getInteger(Constants.CODE) == 0) {
                    String jsonData = jsonResult.get(Constants.DATA).toString();
                    JSONArray jsonArray = JSONArray.parseArray(jsonData);
                    jsonArray.forEach(json -> {
                        JSONObject jsonObject = (JSONObject) json;
                        if (jsonObject.get(Constants.LINEID) != null) {
                            String allLineName = jsonObject.get("lineName").toString();
                            if (beforeOrAfter(lineType, lineName, allLineName)) {
                                listLineIds.add(jsonObject.getInteger("lineId"));

                            }
                        }
                    });
                }
            }
            logger.info("==========????????????????????????end==============" + JSONObject.toJSONString(listLineIds));
        } catch (Exception e) {
            logger.info("??????????????????????????????" + e);
        }

        return listLineIds;
    }

    /**
     * 1 ???????????? 2 ????????????
     *
     * @param lineType
     * @return
     */
    private boolean beforeOrAfter(Integer lineType, String param, String lineName) {
        boolean bl = false;
        switch (lineType) {
            case 1:
                String before = lineName.substring(0, lineName.indexOf(Constants.SHORT_STOKE) > 0 ? lineName.indexOf(Constants.SHORT_STOKE) : lineName.length());

                if (before.contains(param)) {
                    bl = true;
                }
                break;
            case 2:

                Integer index = lineName.indexOf(Constants.SHORT_STOKE) > 0 ? (lineName.indexOf(Constants.SHORT_STOKE) + 1) : 0;
                String after = lineName.substring(index, lineName.length());

                if (after.contains(param)) {
                    bl = true;
                }
                break;

            default:
                return bl;
        }

        return bl;
    }

}
