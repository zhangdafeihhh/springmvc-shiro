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

    public static final String ORDER_STATISTICS_CI = "order_statistics_ci_"; //订单数量统计

    public static final String SERVICE_RATE_STATISTIS = "service_evaluation_rate_statistics_"; //服务差评率

    public static final String CORE_STATISTICS = "core_indicators_statistics"; //核心指标统计

    public static final String RESPONSIBLE_RATE_STATISTICS = "responsible_complaint_rate_statistics"; //有责指标统计

    public static final String CORE_STATISTICS_CI = "core_indicators_statistics_ci_"; //核心指标统计

    public static final String SAAS_DAILY_REPORT = "saas_daily_report_"; //saas月报查询

    public static final String SAAS_MONTH_REPORT = "saas_month_report_"; //saas月报查询

    public static final String SAAS_SUMMARY_REPORT = "saas_summary_report_"; //saas汇总查询

    public static final String MP_ORDER_APPRAISAL = "mp_order_appraisal_"; //司机评价查询

    public static final String SERVICE_RATE_STATISTIS_CI = "service_evaluation_rate_statistics_ci_"; //服务差评率

}
