package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.busManage.BusConstant.DriverMaidConstant;
import com.zhuanche.dto.busManage.BusDriverMaidDTO;
import com.zhuanche.dto.busManage.withdrawalsRecordDTO;
import com.zhuanche.entity.busManage.MaidListEntity;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.CarBizCityService;
import com.zhuanche.serv.mongo.DriverMongoService;
import com.zhuanche.util.excel.CsvUtils;
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

    @Autowired
    private CarBizCityService cityService;
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
     * 导出订单时每页查询的条数
     */
    private static int EXPORT_PAGE_SIZE = 500;
    /**
     * 代表巴士司机业务类型
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
    @Autowired
    private DriverMongoService driverMongoService;

    /**
     * @Description: 查询司机分佣明细
     * @Param: [dto]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/4
     */
    @RequestMapping("/queryMaidData")
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
        //封装城市名称
        JSONArray resultArray = addCityName2jsonArray(array);
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), resultArray);
        logger.info(LOG_PRE + "明细接口查询参数=" + JSON.toJSONString(param) + " 耗时=" + (System.currentTimeMillis() - start));
        return AjaxResponse.success(page);
    }


    /**
     * @Description: 查询提现记录
     * @Param: [dto]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/5
     */
    @RequestMapping("/withdrawalsRecord")
    public AjaxResponse withdrawalsRecord(withdrawalsRecordDTO dto) {
        long start = System.currentTimeMillis();
        Map<String, Object> param = new HashedMap();
        if (StringUtils.isNotBlank(dto.getPhone())) {
            param.put("phone", dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getStartDate())) {
            param.put("startDate", dto.getStartDate());
        }
        if (StringUtils.isNotBlank(dto.getName())) {
            String driverids = getDriverIdsByName(dto.getName());
            if (StringUtils.isEmpty(driverids)) {
                return AjaxResponse.success(new PageDTO(dto.getPageNum(), dto.getPageSize(), 0, new ArrayList()));
            }
            param.put("accountIds", driverids);
        }
        if (StringUtils.isNotBlank(dto.getEndDate())) {
            param.put("endDate", dto.getEndDate());
        }
        buidNecessaryParam(param, dto.getPageNum(), dto.getPageSize());
        logger.info(LOG_PRE + "提现记录查询参数=" + JSON.toJSONString(param));
        JSONObject data = parseResult(WITHDRAWALS_LIST_ULR, param);
        if (data == null) {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        JSONArray array = data.getJSONArray("listData");
        JSONArray resultArray = addCityName2jsonArray(array);
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), resultArray);
        logger.info(LOG_PRE + "提现记录查询参数=" + JSON.toJSONString(param) + "结果=" + data + " 耗时=" + (System.currentTimeMillis() - start));
        return AjaxResponse.success(page);
    }

    @RequestMapping("/queryAccountBalance")
    public AjaxResponse queryAccountBalance(withdrawalsRecordDTO dto) {
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

    private Map<String,Object> buidMaidParam(BusDriverMaidDTO dto){
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

    //=============================================导出=========================================

    @RequestMapping("/exportMaidData")
    public void exportMaidData(HttpServletRequest request, BusDriverMaidDTO dto, HttpServletResponse response) throws Exception{
        Map<String, Object> param = buidMaidParam(dto);
        logger.info(LOG_PRE+"导出分佣明细参数="+JSON.toJSONString(param));
        dto.setPageSize(EXPORT_PAGE_SIZE);
        buidNecessaryParam(param,dto.getPageNum(),dto.getPageSize());
        JSONObject dataFirst = parseResult(MAID_LIST_URL, param);
        Long total = dataFirst.getLong("total");
        logger.info(LOG_PRE+"导出分佣明细总条数="+total);
        //构建文件名称
        String fileName = BusCSVDataUtil.buidFileName(request, DriverMaidConstant.MAID_FILE_NAME);
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        headerList.add(DriverMaidConstant.MAID_EXPORT_HEAD);
        CsvUtils utilEntity = new CsvUtils();
        if (total == 0) {
            List<String> csvDataList = new ArrayList<>();
            csvDataList.add("没有符合条件的数据");
            utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
            return;
        }
        JSONArray arrayFirst = dataFirst.getJSONArray("listData");
        JSONArray resultArrayFirst = addCityName2jsonArray(arrayFirst);
        List<String> csvDataFirst = resultArrayFirst.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, MaidListEntity.class)).map(o ->o.toString()).collect(Collectors.toList());
        PageDTO page = new PageDTO(dataFirst.getInteger("pageNo"), dataFirst.getInteger("pageSize"), dataFirst.getLong("total"), resultArrayFirst);
        int pages = page.getPages();
        if(pages==1){
            utilEntity.exportCsvV2(response, csvDataFirst, headerList, fileName, true, true);
            return;
        }
        utilEntity.exportCsvV2(response, csvDataFirst, headerList, fileName, true, false);
        for (int i = 2; i <= pages; i++) {
            dto.setPageNum(i);
            Map<String, Object> paramNext = buidMaidParam(dto);
            buidNecessaryParam(paramNext,dto.getPageNum(),dto.getPageSize());
            JSONObject data = parseResult(MAID_LIST_URL, paramNext);
            JSONArray array = data.getJSONArray("listData");
            JSONArray resultArray = addCityName2jsonArray(array);
            List<String> csvData = resultArray.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, MaidListEntity.class)).map(o ->o.toString()).collect(Collectors.toList());
            if (i == pages) {
                utilEntity.exportCsvV2(response, csvData, headerList, fileName, false, true);
            } else {
                utilEntity.exportCsvV2(response, csvData, headerList, fileName, false, false);
            }
        }

    }

    /**
     * 根据分佣接口返回的cityCode查询城市
     * @param array
     * @return
     */
    private Map<Integer,CarBizCity> queryCity(JSONArray array){
        if (array == null || array.size() == 0) {
            return new HashMap<Integer,CarBizCity>(0);
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
            String cityName = cityMap.get(cityCode).getCityName();
            o.put("cityName", cityName != null ? cityName : "");
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
        param.put("pageNum", pageNum == null ? 1 : pageNum);
        param.put("pageSize", pageSize == null ? 30 : pageSize);
    }


    private String getDriverIdsByName(String name) {
        List<DriverMongo> drivers = driverMongoService.queryDriverByName(name);
        if (drivers == null || drivers.size() == 0) {
            return StringUtils.EMPTY;
        }
        String ids = drivers.stream().map(o -> String.valueOf(o.getDriverId())).collect(Collectors.joining(","));
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

}
