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
 * @description: 司机分佣
 * @author: niuzilian
 * @create: 2018-12-01 15:34
 **/
@RestController
@RequestMapping("/bus/driverMaid")
public class DriverMaidController {

    private static final Logger logger = LoggerFactory.getLogger(DriverMaidController.class);


    /**
     * 本类的日志前缀
     */
    private static String LOG_PRE = "【巴士司机分佣】";
    /**
     * httpclient 链接超时时间
     **/
    private static final Integer CONNECT_TIMEOUT = 30000;
    /**
     * httpclient 读取超时时间
     **/
    private static final Integer READ_TIMEOUT = 30000;

    /**
     * 代表巴士司机业务类型(巴士司机分佣)
     */
    private static int ACCOUNT_TYPE = 3;


    /**
     * 分佣明细url
     */
    @Value("${bus.driver.maid.list.url}")
    private String MAID_LIST_URL;
    /**
     * 提现记录url
     */
    @Value("${bus.driver.withdrawals.record.url}")
    private String WITHDRAWALS_LIST_ULR;
    /**
     * 账户余额url
     */
    @Value("${bus.driver.account.balance.url}")
    private String ACCOUNT_BALANCE_URL;
    /**
     * 查询订单ID by orderNo
     */
    @Value("${bus.query.orderId.url}")
    private String QUERY_ORDER_ID_URL;

    /**
     * 订单标识巴士业务businessId
     */
    @Value("${bus.order.businessId}")
    private int ORDER_BUSINESS_ID;
    /**
     * 订单定义的关于巴士的KEY
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
     * @Description: 查询司机分佣明细,
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
        logger.info(LOG_PRE + "明细接口查询参数=" + JSON.toJSONString(param));
        JSONObject data = parseResult(MAID_LIST_URL, param);
        if (data == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        logger.info(LOG_PRE + "明细接口查询参数=" + JSON.toJSONString(param) + "查询结果=" + data);
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
        logger.info(LOG_PRE + "明细接口查询参数=" + JSON.toJSONString(param) + " 耗时=" + (System.currentTimeMillis() - start));
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
     * @Description: 查询提现记录
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
        logger.info(LOG_PRE + "提现记录查询参数=" + JSON.toJSONString(param));
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
        logger.info(LOG_PRE + "提现记录查询参数=" + JSON.toJSONString(param) + "结果=" + data + " 耗时=" + (System.currentTimeMillis() - start));
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
        logger.info(LOG_PRE + "账户余额查询参数=" + JSON.toJSONString(param));
        JSONObject data = parseResult(ACCOUNT_BALANCE_URL, param);
        if (data == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        logger.info(LOG_PRE + "明细接口查询参数=" + JSON.toJSONString(param) + "查询结果=" + data);
        JSONArray array = data.getJSONArray("listData");
        //封装城市名称
        JSONArray resultArray = addCityName2jsonArray(array);
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), resultArray);
        logger.info(LOG_PRE + "提现记录查询参数=" + JSON.toJSONString(param) + "结果=" + data + " 耗时=" + (System.currentTimeMillis() - start));
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

    //=============================================导出=========================================

    /**
     * 导出分佣明细
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
        logger.info(LOG_PRE + "导出分佣明细参数=" + JSON.toJSONString(dto));
        //构建文件名称
        String fileName = BusConstant.buidFileName(request, DriverMaidConstant.MAID_FILE_NAME);
        //构建文件标题
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
                csvDataList.add("没有符合条件的数据");
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
            // isList=true时表示时之后一页停止循环
        } while (!isList);
        logger.info(LOG_PRE + "导出分佣明细参数=" + JSON.toJSONString(dto) + " 消耗时间=" + (System.currentTimeMillis() - start));
    }


    /**
     * 导出导出提现记录
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
        logger.info(LOG_PRE + "导出提现记录参数=" + JSON.toJSONString(dto));
        Map<String, Object> param = new HashedMap();
        CsvUtils utilEntity = new CsvUtils();
        //构建文件名称
        String fileName = BusConstant.buidFileName(request, DriverMaidConstant.DRAW_FILE_NAME);
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        headerList.add(DriverMaidConstant.DRAW_EXPORT_HEAD);
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("没有符合条件的数据");
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
                csvDataList.add("没有符合条件的数据");
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
            // isList=true时表示时之后一页停止循环
        } while (!isList);
        logger.info(LOG_PRE + "导出提现记录参数=" + JSON.toJSONString(dto) + " 消耗时间=" + (System.currentTimeMillis() - start));
    }

    /**
     * 导出账户余额
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
        logger.info(LOG_PRE + "导出账户余额=" + JSON.toJSONString(dto));
        Map<String, Object> param = new HashedMap();
        CsvUtils utilEntity = new CsvUtils();
        //构建文件名称
        String fileName = BusConstant.buidFileName(request, DriverMaidConstant.ACCOUNT_FILE_NAME);
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        headerList.add(DriverMaidConstant.ACCOUNT_EXPORT_HEAD);
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("没有符合条件的数据");
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
                csvDataList.add("没有符合条件的数据");
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
            // isList=true时表示时之后一页停止循环
        } while (!isList);
        logger.info(LOG_PRE + "导出提现记录参数=" + JSON.toJSONString(dto) + " 消耗时间=" + (System.currentTimeMillis() - start));
    }




    /**
     * 根据分佣接口返回的cityCode查询城市
     *
     * @param array
     * @return
     */
    private Map<Integer, CarBizCity> queryCity(JSONArray array) {
        if (array == null || array.size() == 0) {
            return new HashMap<Integer, CarBizCity>(0);
        }
        //取出所有的城市id
        Set<Integer> cityIds = array.stream().map(o -> (JSONObject) o).map(o -> o.getInteger("cityCode")).collect(Collectors.toSet());
        //查询城市
        Map<Integer, CarBizCity> cityMap = cityService.queryCity(cityIds);
        return cityMap;
    }

    /**
     * 数据集合中的每个元素添加一个城市名称参数
     *
     * @Param: [array]
     * @return: com.alibaba.fastjson.JSONArray
     * @Date: 2018/12/7
     */
    private JSONArray addCityName2jsonArray(JSONArray array) {
        Map<Integer, CarBizCity> cityMap = queryCity(array);
        //将城市名称放到array中
        array.stream().map(o -> (JSONObject) o).forEach(o -> {
            Integer cityCode = o.getInteger("cityCode");
            CarBizCity city = cityMap.get(cityCode);
            o.put("cityName", city != null ? city.getCityName() : StringUtils.EMPTY);
        });
        return array;
    }

    /**
     * 构建查询的必要参数 pageNo,pageSize,accountType（业务类型,巴士司机等于3）
     *
     * @Param: [param, pageNum, pageSize]
     * @return: void
     * @Date: 2018/12/7
     */
    private void buidNecessaryParam(Map<String, Object> param, Integer pageNum, Integer pageSize) {
        //accountType 计费定义的参数 3代表巴士司机
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
     * @Description: 统一调用计费接口的方法
     * @Param: [url, param]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/1
     */
    private JSONObject parseResult(String url, Map<String, Object> param) {
        //调用计费接口查询分佣列表
        try {
            String body = HttpClientUtil.buildPostRequest(url)
                    .setConnectTimeOut(CONNECT_TIMEOUT).setReadTimeOut(READ_TIMEOUT)
                    .addParams(param).execute();
            JSONObject result = JSON.parseObject(body);
            if (result == null) {
                logger.error(LOG_PRE + "调用计费接口" + url + "返回结果为null");
                return null;
            }
            String code = result.getString("code");
            if (code == null || !result.getString("code").equals("0")) {
                logger.error(LOG_PRE + "调用计费接口" + url + "返回错误 msg=" + result.getString("msg"));
                return null;
            }
            JSONObject data = result.getJSONObject("data");
            return data;
        } catch (HttpException e) {
            logger.error(LOG_PRE + "调用计费接口" + url + "异常", JSON.toJSONString(e));
            return null;
        }
    }

    /**
     * 根据订单号，查询订单ID
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
                logger.error(LOG_PRE + "查询订单id 返回错误 msg=" + result.getString("msg"));
                //订单接口有异常返回空的map,不能影响其他的功能
                return new HashMap<>(0);
            }
            JSONArray data = result.getJSONArray("data");
            Map<String, Integer> noAndId = new HashedMap();
            data.stream().map(o -> (JSONObject) o).forEach(o -> {
                noAndId.put(o.getString("orderNo"), o.getIntValue("orderId"));
            });
            return noAndId;
        } catch (Exception e) {
            logger.error(LOG_PRE + "查询订单id 异常 e=" + JSON.toJSONString(e));
            return new HashMap(0);
        }
    }
}
