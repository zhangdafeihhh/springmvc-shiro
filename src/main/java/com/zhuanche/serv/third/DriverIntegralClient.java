package com.zhuanche.serv.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kjeakiry
 */
@Slf4j
@Component
public class DriverIntegralClient {


    public static final String INCOME_COMPLAIN_NOTICE = "/income/complainNotice";
    public static final String GUARANTEE_PLAN_ORDER = "/guarantee/plan/order";
    /**
     * 请求头
     */
    private static Map<String, Object> headerParams = new HashMap<String, Object>() {{
        put("Content-Type", "application/json;charset=UTF-8");
    }};

    @Value("${driver.integral.url}")
    private String driverIntegralUrl;


    /**
     * 保障计划订单状态接口调用urlC
     * @param driverId
     * @param orderNo
     * @param orderStatusType
     */
    public void guaranteePlanOrderUrl(Integer driverId,String orderNo,Integer orderStatusType){

        Map<String,Object> paramMap = Maps.newHashMapWithExpectedSize(5);
        paramMap.put("driverId",driverId);
        paramMap.put("orderNo",orderNo);
        paramMap.put("orderStatusType",orderStatusType);
        paramMap.put("orderStatus",1);
        paramMap.put("orderChangeTime",System.currentTimeMillis());
        log.info("调用保障计划订单状态接口 paramMap=" + paramMap.toString());
        try {
            JSONObject result = MpOkHttpUtil.okHttpPostBackJson(driverIntegralUrl + GUARANTEE_PLAN_ORDER, paramMap, 3, "调用保障计划订单状态", headerParams);
            log.info("调用保障计划订单状态接口执行结果,result=" + JSON.toJSONString(result));
            int code = result.getIntValue("code");
            String msg = result.getString("msg");
            if (code == 1) {
                log.info("调用保障计划订单状态接口出错,错误码:" + code + ",错误原因:" + msg);
            }
        } catch (Exception e) {
            log.info("调用保障计划订单状态接口出错  error:" + e);
        }
    }

    /**
     * 司机积分策略  处罚后置司机有责时调用策略工具接口
     *
     * @param driverId
     * @param orderNo
     * @param punishReason
     */
    public void driverIntegralStrategyUrl(Integer driverId, String orderNo, Date createDate, String punishReason) {
        String url = driverIntegralUrl + INCOME_COMPLAIN_NOTICE;
        Map<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("driverId", driverId);
        paramMap.put("orderNo", orderNo);
        if (createDate != null) {
            paramMap.put("complainTime", DateUtils.formatDate(createDate, "yyyy-MM-dd HH:mm:ss"));
        }
        paramMap.put("finalTime", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paramMap.put("complainReason", punishReason);
        log.info("调用司机积分策略工具接口 paramMap=" + paramMap.toString());
        try {
            String resultData = HttpClientUtil.buildPostRequest(url).addParams(paramMap)
                    .setConnectTimeOut(5000).setReadTimeOut(3000).execute();
            JSONObject result = JSON.parseObject(resultData);
            int code = result.getIntValue("code");
            String msg = result.getString("msg");
            if (code == 1) {
                log.info("调用司机积分策略工具接口出错,错误码:" + code + ",错误原因:" + msg);
            }
        } catch (Exception e) {
            log.info("调用司机积分策略工具接口出错  error:" + e);
        }

    }
}
