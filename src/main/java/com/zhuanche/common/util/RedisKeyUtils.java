package com.zhuanche.common.util;

/**
 * @Author fanht
 * @Description  缓存优化:各种查询key值
 * @Date 2019/8/5 上午11:34
 * @Version 1.0
 */
public class RedisKeyUtils {

    public static final String DAILY_REPORT_KEY = "query_driver_report_"; //日报月报查询key

    public static final String VEHICLE_STATISTICS = "vehicle_statistics_"; //日均运营车辆统计查询key

    public static final String ORDER_STATISTICS = "order_statistics_"; //订单数量统计

    public static final String SERVICE_RATE_STATISTIS = "service_evaluation_rate_statistics_"; //服务差评率

    public static final String CORE_STATISTICS = "core_indicators_statistics"; //核心指标统计
}
