package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rpc.HttpParamSignGenerator;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.BusConstant.DriverMaidConstant;
import com.zhuanche.dto.busManage.BusDriverMaidDTO;
import com.zhuanche.dto.busManage.WithdrawalsRecordDTO;
import com.zhuanche.entity.busManage.MaidListEntity;
import com.zhuanche.entity.busManage.MaidOrderEntity;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.serv.rentcar.IDriverService;
import com.zhuanche.serv.rentcar.impl.DriverServiceImpl;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.AccountBalanceVO;
import com.zhuanche.vo.busManage.MaidVO;
import com.zhuanche.vo.busManage.WithDrawDetailRecordVO;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: mp-manage
 * @description: ????????????
 * @author: niuzilian
 * @create: 2018-12-01 15:34
 **/
@RestController
@RequestMapping("/bus/driverMaid")
public class DriverMaidController {

    private static final Logger logger = LoggerFactory.getLogger(DriverMaidController.class);


    /**
     * ?????????????????????
     */
    private static String LOG_PRE = "????????????????????????";
    /**
     * httpclient ??????????????????
     **/
    private static final Integer CONNECT_TIMEOUT = 30000;
    /**
     * httpclient ??????????????????
     **/
    private static final Integer READ_TIMEOUT = 30000;

    /**
     * ??????????????????????????????(??????????????????)
     */
    private static int ACCOUNT_TYPE = 3;


    /**
     * ????????????url
     */
    @Value("${bus.driver.maid.list.url}")
    private String MAID_LIST_URL;
    /**
     * ????????????url
     */
    @Value("${bus.driver.withdrawals.record.url}")
    private String WITHDRAWALS_LIST_ULR;
    /**
     * ????????????url
     */
    @Value("${bus.driver.account.balance.url}")
    private String ACCOUNT_BALANCE_URL;
    /**
     * ????????????ID by orderNo
     */
    @Value("${bus.query.orderId.url}")
    private String QUERY_ORDER_ID_URL;

    /**
     * ????????????????????????businessId
     */
    @Value("${bus.order.businessId}")
    private int ORDER_BUSINESS_ID;
    /**
     * ??????????????????????????????KEY
     */
    @Value("${bus.order.key}")
    private String ORDER_KEY;

    @Autowired
    private CarBizCityService cityService;
    @Autowired
    private DriverMongoService driverMongoService;

    @Autowired
    private IDriverService driverService;
    /**
     * @Description: ????????????????????????,
     * @Param: [dto]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/4
     */
    @RequestMapping("/queryMaidData")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse queryMaidData(BusDriverMaidDTO dto) {
        long start = System.currentTimeMillis();
        Map<String, Object> param = buidMaidParam(dto);
        buidNecessaryParam(param, dto.getPageNum(), dto.getPageSize());
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param));
        JSONObject data = parseResult(MAID_LIST_URL, param);
        if (data == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param) + "????????????=" + data);
        JSONArray array = data.getJSONArray("listData");
        if(array==null||array.isEmpty()){
            return AjaxResponse.success(new PageDTO(dto.getPageNum(),dto.getPageSize(),0,array));
        }
        Set<Integer> cityIds = new HashSet<>();
        List<String> orderNos = array.stream().map(o -> (JSONObject) o).map(o -> {
            cityIds.add(o.getInteger("cityCode"));
            return o;
        }).map(o -> o.getString("orderNo")).collect(Collectors.toList());
        Map<Integer, CarBizCity> cityMap = cityService.queryCity(cityIds);
        Map<String, Integer> orderIdMap = queryOrderIds(JSON.toJSONString(orderNos));
        List<MaidVO> collect = array.stream().map(o -> (JSONObject) o).map(this::transformType).map(o -> {
            Integer cityCode = o.getCityCode();
            CarBizCity carBizCity = cityMap.get(cityCode);
            o.setCityName(carBizCity == null ? "" : carBizCity.getCityName());
            o.setOrderId(orderIdMap.get(o.getOrderNo()));
            return o;
        }).collect(Collectors.toList());
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), collect);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param) + " ??????=" + (System.currentTimeMillis() - start));
        return AjaxResponse.success(page);
    }

    private MaidVO transformType(JSONObject o) {
        MaidVO maid = new MaidVO();
        MaidListEntity entity = JSONObject.toJavaObject(o, MaidListEntity.class);
        maid.setOrderNo(entity.getOrderNo());
        maid.setSettleDate(entity.getSettleDate());
        maid.setPhone(entity.getPhone());
        maid.setCityCode(entity.getCityCode());
        maid.setSettleAmount(entity.getSettleAmount());
        if (StringUtils.isNotBlank(entity.getOrderDetail())) {
            MaidOrderEntity orderEntity = JSONObject.parseObject(entity.getOrderDetail(), MaidOrderEntity.class);
            BigDecimal orderAmount = orderEntity.getOrderAmount();
            BigDecimal prePayAmount = orderEntity.getPrePayAmount();
            maid.setOrderId(orderEntity.getOrderId());
            maid.setOrderAmount(orderAmount);
            maid.setPrePayAmount(prePayAmount);
            if (orderAmount != null && prePayAmount != null) {
                maid.setProxyAmount(orderAmount.subtract(prePayAmount));
            }
            maid.setHighWayFee(orderEntity.getHighWayFee());
            maid.setParkFee(orderEntity.getParkFee());
            maid.setHotelFee(orderEntity.getHotelFee());
            maid.setFoodFee(orderEntity.getFoodFee());
            maid.setSettleRatio(orderEntity.getSettleRatio());
        }
        return maid;
    }

    /**
     * @Description: ??????????????????
     * @Param: [dto]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/5
     */
    @RequestMapping("/withdrawalsRecord")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse withdrawalsRecord(WithdrawalsRecordDTO dto) {
        long start = System.currentTimeMillis();
        Map<String, Object> param = new HashedMap();
        buidDrawalsParam(param, dto);
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                return AjaxResponse.success(new PageDTO(dto.getPageNum(), dto.getPageSize(), 0, new ArrayList()));
            }
            param.put("accountIds", driverids);
        }
        buidNecessaryParam(param, dto.getPageNum(), dto.getPageSize());
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param));
        JSONObject data = parseResult(WITHDRAWALS_LIST_ULR, param);
        if (data == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        JSONArray array = data.getJSONArray("listData");
        if(array==null||array.isEmpty()){
           return AjaxResponse.success( new PageDTO(dto.getPageNum(),dto.getPageSize(), 0, new ArrayList()));
        }
        JSONArray resultArray = addCityName2jsonArray(array);
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), resultArray);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param) + "??????=" + data + " ??????=" + (System.currentTimeMillis() - start));
        return AjaxResponse.success(page);
    }

    @RequestMapping("/queryAccountBalance")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public AjaxResponse queryAccountBalance(WithdrawalsRecordDTO dto) {
        long start = System.currentTimeMillis();
        Map<String, Object> param = new HashedMap();
        if (StringUtils.isNotBlank(dto.getPhone())) {
            param.put("phone", dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                return AjaxResponse.success(new PageDTO(dto.getPageNum(), dto.getPageSize(), 0, new ArrayList()));
            }
            param.put("accountIds", driverids);
        }
        if (dto.getCityId() != null) {
            param.put("cityCode", dto.getCityId());
        }
        buidNecessaryParam(param, dto.getPageNum(), dto.getPageSize());
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param));
        JSONObject data = parseResult(ACCOUNT_BALANCE_URL, param);
        if (data == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param) + "????????????=" + data);
        JSONArray array = data.getJSONArray("listData");
        //??????????????????
        JSONArray resultArray = addCityName2jsonArray(array);
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), resultArray);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(param) + "??????=" + data + " ??????=" + (System.currentTimeMillis() - start));
        return AjaxResponse.success(page);
    }

    private Map<String, Object> buidMaidParam(BusDriverMaidDTO dto) {
        Map<String, Object> param = new HashMap<>(16);
        if (dto.getCityId() != null) {
            param.put("cityCode", dto.getCityId());
        }
        if (StringUtils.isNotBlank(dto.getPhone())) {
            param.put("phone", dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getOrderNo())) {
            param.put("orderNo", dto.getOrderNo());
        }
        if (StringUtils.isNotBlank(dto.getStartDate())) {
            param.put("startDate", dto.getStartDate());
        }
        if (StringUtils.isNotBlank(dto.getEndDate())) {
            param.put("endDate", dto.getEndDate());
        }
        return param;
    }

    private void buidDrawalsParam(Map<String, Object> param, WithdrawalsRecordDTO dto) {
        if (StringUtils.isNotBlank(dto.getPhone())) {
            param.put("phone", dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getStartDate())) {
            param.put("startDate", dto.getStartDate());
        }
        if (StringUtils.isNotBlank(dto.getEndDate())) {
            param.put("endDate", dto.getEndDate());
        }
    }

    //=============================================??????=========================================

    /**
     * ??????????????????
     *
     * @Param: [request, dto, response]
     * @return: void
     * @Date: 2018/12/10
     */
    @RequestMapping("/exportMaidData")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public void exportMaidData(HttpServletRequest request, BusDriverMaidDTO dto, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto));
        //??????????????????
        String fileName = BusConstant.buidFileName(request, DriverMaidConstant.MAID_FILE_NAME);
        //??????????????????
        List<String> headerList = new ArrayList<>();
        headerList.add(DriverMaidConstant.MAID_EXPORT_HEAD);
        CsvUtils utilEntity = new CsvUtils();
        Integer pageNum = 0;
        boolean isFirst = true;
        boolean isList = false;
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            Map<String, Object> param = buidMaidParam(dto);
            dto.setPageSize(CsvUtils.downPerSize);
            buidNecessaryParam(param, dto.getPageNum(), dto.getPageSize());
            JSONObject data = parseResult(MAID_LIST_URL, param);
            if (data == null || data.getLong("total") == null || data.getLong("total") == 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("???????????????????????????");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            Integer pages = data.getInteger("pages");
            JSONArray array = data.getJSONArray("listData");
            if (pageNum != 1) {
                isFirst = false;
            }
            if (pageNum.equals(pages)) {
                isList = true;
            }
            Map<Integer, CarBizCity> cityMap = queryCity(array);
            List<String> collect = array.stream().map(o -> (JSONObject) o).map(this::transformType)
                    .map(o -> {
                        Integer cityCode = o.getCityCode();
                        CarBizCity carBizCity = cityMap.get(cityCode);
                        o.setCityName(carBizCity == null ? "" : carBizCity.getCityName());
                        return o;
                    }).map(o -> o.toString()).collect(Collectors.toList());
            utilEntity.exportCsvV2(response, collect, headerList, fileName, isFirst, isList);
            // isList=true????????????????????????????????????
        } while (!isList);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto) + " ????????????=" + (System.currentTimeMillis() - start));
    }


    /**
     * ????????????????????????
     *
     * @Param: [request, dto, response]
     * @return: void
     * @Date: 2018/12/10
     */
    @RequestMapping("/exportWithdrawalsData")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public void exportWithdrawalsData(HttpServletRequest request, WithdrawalsRecordDTO dto, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto));
        Map<String, Object> param = new HashedMap();
        CsvUtils utilEntity = new CsvUtils();
        //??????????????????
        String fileName = BusConstant.buidFileName(request, DriverMaidConstant.DRAW_FILE_NAME);
        //??????????????????
        List<String> headerList = new ArrayList<>();
        headerList.add(DriverMaidConstant.DRAW_EXPORT_HEAD);
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("???????????????????????????");
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
                return;
            }
            param.put("accountIds", driverids);
        }
        buidDrawalsParam(param, dto);
        Integer pageNum = 0;
        boolean isFirst = true;
        boolean isList = false;
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            buidNecessaryParam(param, pageNum, CsvUtils.downPerSize);
            JSONObject data = parseResult(WITHDRAWALS_LIST_ULR, param);
            if (data == null || data.getLong("total") == null || data.getLong("total") == 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("???????????????????????????");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            Integer pages = data.getInteger("pages");
            JSONArray array = data.getJSONArray("listData");
            if (pageNum != 1) {
                isFirst = false;
            }
            if (pageNum.equals(pages)) {
                isList = true;
            }
            Map<Integer, CarBizCity> cityMap = queryCity(array);
            List<String> csvData = array.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, WithDrawDetailRecordVO.class))
                    .map(o -> {
                        Integer cityCode = o.getCityCode();
                        CarBizCity carBizCity = cityMap.get(cityCode);
                        o.setCityName(carBizCity == null ? "" : carBizCity.getCityName());
                        return o;
                    }).map(o -> o.toString()).collect(Collectors.toList());
            utilEntity.exportCsvV2(response, csvData, headerList, fileName, isFirst, isList);
            // isList=true????????????????????????????????????
        } while (!isList);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto) + " ????????????=" + (System.currentTimeMillis() - start));
    }

    /**
     * ??????????????????
     *
     * @Param: [request, dto, response]
     * @return: void
     * @Date: 2018/12/10
     */
    @RequestMapping("/exportAccBalnce")
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DynamicRoutingDataSource.DataSourceMode.SLAVE)
    })
    public void exportAccBalnce(HttpServletRequest request, WithdrawalsRecordDTO dto, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(LOG_PRE + "??????????????????=" + JSON.toJSONString(dto));
        Map<String, Object> param = new HashedMap();
        CsvUtils utilEntity = new CsvUtils();
        //??????????????????
        String fileName = BusConstant.buidFileName(request, DriverMaidConstant.ACCOUNT_FILE_NAME);
        //??????????????????
        List<String> headerList = new ArrayList<>();
        headerList.add(DriverMaidConstant.ACCOUNT_EXPORT_HEAD);
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("???????????????????????????");
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
                return;
            }
            param.put("accountIds", driverids);
        }
        if (StringUtils.isNotBlank(dto.getPhone())) {
            param.put("phone", dto.getPhone());
        }
        if (dto.getCityId() != null) {
            param.put("cityCode", dto.getCityId());
        }
        Integer pageNum = 0;
        boolean isFirst = true;
        boolean isList = false;
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            buidNecessaryParam(param, pageNum, CsvUtils.downPerSize);
            JSONObject data = parseResult(ACCOUNT_BALANCE_URL, param);
            if (data == null || data.getLong("total") == null || data.getLong("total") == 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("???????????????????????????");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            Integer pages = data.getInteger("pages");
            JSONArray array = data.getJSONArray("listData");
            if (pageNum != 1) {
                isFirst = false;
            }
            if (pageNum.equals(pages)) {
                isList = true;
            }
            Map<Integer, CarBizCity> cityMap = queryCity(array);
            List<String> csvData = array.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, AccountBalanceVO.class))
                    .map(o -> {
                        Integer cityCode = o.getCityCode();
                        CarBizCity carBizCity = cityMap.get(cityCode);
                        o.setCityName(carBizCity == null ? "" : carBizCity.getCityName());
                        return o;
                    }).map(o -> o.toString()).collect(Collectors.toList());
            utilEntity.exportCsvV2(response, csvData, headerList, fileName, isFirst, isList);
            // isList=true????????????????????????????????????
        } while (!isList);
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto) + " ????????????=" + (System.currentTimeMillis() - start));
    }




    /**
     * ???????????????????????????cityCode????????????
     *
     * @param array
     * @return
     */
    private Map<Integer, CarBizCity> queryCity(JSONArray array) {
        if (array == null || array.size() == 0) {
            return new HashMap<Integer, CarBizCity>(0);
        }
        //?????????????????????id
        Set<Integer> cityIds = array.stream().map(o -> (JSONObject) o).map(o -> o.getInteger("cityCode")).collect(Collectors.toSet());
        //????????????
        Map<Integer, CarBizCity> cityMap = cityService.queryCity(cityIds);
        return cityMap;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @Param: [array]
     * @return: com.alibaba.fastjson.JSONArray
     * @Date: 2018/12/7
     */
    private JSONArray addCityName2jsonArray(JSONArray array) {
        Map<Integer, CarBizCity> cityMap = queryCity(array);
        //?????????????????????array???
        array.stream().map(o -> (JSONObject) o).forEach(o -> {
            Integer cityCode = o.getInteger("cityCode");
            CarBizCity city = cityMap.get(cityCode);
            o.put("cityName", city != null ? city.getCityName() : StringUtils.EMPTY);
        });
        return array;
    }

    /**
     * ??????????????????????????? pageNo,pageSize,accountType???????????????,??????????????????3???
     *
     * @Param: [param, pageNum, pageSize]
     * @return: void
     * @Date: 2018/12/7
     */
    private void buidNecessaryParam(Map<String, Object> param, Integer pageNum, Integer pageSize) {
        //accountType ????????????????????? 3??????????????????
        param.put("accountType", ACCOUNT_TYPE);
        param.put("pageNo", pageNum == null ? 1 : pageNum);
        param.put("pageSize", pageSize == null ? 30 : pageSize);
    }


    private String getDriverIdsByName(String name) {

        List<Integer> drivers = driverService.queryDriverIdsByName(name);
        if (drivers == null || drivers.size() == 0) {
            return StringUtils.EMPTY;
        }
        String ids = drivers.stream().map(o -> String.valueOf(o)).collect(Collectors.joining(","));
        return ids;
    }


    /**
     * @Description: ?????????????????????????????????
     * @Param: [url, param]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/1
     */
    private JSONObject parseResult(String url, Map<String, Object> param) {
        //????????????????????????????????????
        try {
            String body = HttpClientUtil.buildPostRequest(url)
                    .setConnectTimeOut(CONNECT_TIMEOUT).setReadTimeOut(READ_TIMEOUT)
                    .addParams(param).execute();
            JSONObject result = JSON.parseObject(body);
            if (result == null) {
                logger.error(LOG_PRE + "??????????????????" + url + "???????????????null");
                return null;
            }
            String code = result.getString("code");
            if (code == null || !result.getString("code").equals("0")) {
                logger.error(LOG_PRE + "??????????????????" + url + "???????????? msg=" + result.getString("msg"));
                return null;
            }
            JSONObject data = result.getJSONObject("data");
            return data;
        } catch (HttpException e) {
            logger.error(LOG_PRE + "??????????????????" + url + "??????", JSON.toJSONString(e));
            return null;
        }
    }

    /**
     * ??????????????????????????????ID
     *
     * @Param: [orderNos]
     * @return: java.util.Map<java.lang.String,java.lang.Integer>
     * @Date: 2018/12/10
     */
    private Map<String, Integer> queryOrderIds(String orderNos) {
        Map<String, Object> httpParams = new HashMap<String, Object>(4);
        httpParams.put("orderNo", orderNos);
        httpParams.put("businessId", ORDER_BUSINESS_ID);
        String sign = HttpParamSignGenerator.genSignForOrderAPI(httpParams, ORDER_KEY);
        httpParams.put("sign", sign);
        try {
            String body = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, QUERY_ORDER_ID_URL, httpParams, null, "UTF-8");
            JSONObject result = JSON.parseObject(body);
            int code = result.getIntValue("code");
            if (code != 0) {
                logger.error(LOG_PRE + "????????????id ???????????? msg=" + result.getString("msg"));
                //?????????????????????????????????map,???????????????????????????
                return new HashMap<>(0);
            }
            JSONArray data = result.getJSONArray("data");
            Map<String, Integer> noAndId = new HashedMap();
            data.stream().map(o -> (JSONObject) o).forEach(o -> {
                noAndId.put(o.getString("orderNo"), o.getIntValue("orderId"));
            });
            return noAndId;
        } catch (Exception e) {
            logger.error(LOG_PRE + "????????????id ?????? e=" + JSON.toJSONString(e));
            return new HashMap(0);
        }
    }
}
