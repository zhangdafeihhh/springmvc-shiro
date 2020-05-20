package com.zhuanche.util;

import com.sq.common.okhttp.builder.OkHttpRequest;
import com.sq.common.okhttp.intercepter.RetryIntercepter;
import com.sq.common.okhttp.util.PropertiesUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sq.common.okhttp.constants.OkHttpConfig.*;
import static com.sq.common.okhttp.result.ResponseMsg.GET_ERROR_MSG;

/**
 * 代理转发http接口工具类
 * 此工具因需要使用okhttp 原生response 借用OkHttpUtil中的方式 实现doGet方法
 *
 * @author answer
 * @Date 2019-12-09
 */
public class ProxyHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(ProxyHttpUtil.class);

    private static OkHttpClient okHttpClient;
    private static boolean okHttpLogSwitch;


    static {

        int maxIdleConnections = PropertiesUtil.getProperty(OKHTTP_POOL_MAXIDLECONNECTIONS) != null ?
                Integer.parseInt(PropertiesUtil.getProperty(OKHTTP_POOL_MAXIDLECONNECTIONS)) : DEFAULT_CLIENT_MAXIDLECONNECTIONS;
        long keepAliveDuration = PropertiesUtil.getProperty(OKHTTP_POOL_KEEPALIVEDURATION) != null ?
                Long.parseLong(PropertiesUtil.getProperty(OKHTTP_POOL_KEEPALIVEDURATION)) : DEFAULT_CLIENT_KEEPALIVEDURATION;
        long connectTimeout = PropertiesUtil.getProperty(OKHTTP_CLIENT_CONNECTTIMEOUT) != null ?
                Long.parseLong(PropertiesUtil.getProperty(OKHTTP_CLIENT_CONNECTTIMEOUT)) : DEFAULT_CLIENT_CONNECTTIMEOUT;
        long writeTimeout = PropertiesUtil.getProperty(OKHTTP_CLIENT_WRITETIMEOUT) != null ?
                Long.parseLong(PropertiesUtil.getProperty(OKHTTP_CLIENT_WRITETIMEOUT)) : DEFAULT_CLIENT_WRITETIMEOUT;
        long readTimeout = PropertiesUtil.getProperty(OKHTTP_CLIENT_READTIMEOUT) != null ?
                Long.parseLong(PropertiesUtil.getProperty(OKHTTP_CLIENT_READTIMEOUT)) : DEFAULT_CLIENT_READTIMEOUT;
        int maxRetry = PropertiesUtil.getProperty(OKHTTP_CLIENT_MAXRETRY) != null ?
                Integer.parseInt(PropertiesUtil.getProperty(OKHTTP_CLIENT_MAXRETRY)) : DEFAULT_CLIENT_MAXRETRY;
        okHttpLogSwitch = PropertiesUtil.getProperty(OKHTTP_CLIENT_LOGSWITCH) != null ?
                Boolean.parseBoolean(PropertiesUtil.getProperty(OKHTTP_CLIENT_LOGSWITCH)) : DEFAULT_CLIENT_LOGSWITCH;
        boolean logParamsSwitch = PropertiesUtil.getProperty(OKHTTP_CLIENT_LOGPARAMSSWITCH) != null ?
                Boolean.parseBoolean(PropertiesUtil.getProperty(OKHTTP_CLIENT_LOGPARAMSSWITCH)) : DEFAULT_CLIENT_LOGSWITCH;

        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES);
        okHttpClient = new OkHttpClient.Builder().connectionPool(connectionPool)
                .retryOnConnectionFailure(true)
                .addInterceptor(new RetryIntercepter(maxRetry, okHttpLogSwitch, logParamsSwitch))
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS).build();
    }


    public static Response doGet(OkHttpRequest okHttpRequest) {
        Headers.Builder headerBuilder = buildHeaders(okHttpRequest.headerParams(), okHttpRequest.retryTimes());
        String tag = swapTag(okHttpRequest.tag());
        Request request = new Request.Builder().headers(headerBuilder.build()).url(buildGetUrl(okHttpRequest.url(), okHttpRequest.getParams())).tag(tag).build();
        Call call = okHttpClient.newCall(request);
        Response execute = null;
        try {
            execute = call.execute();
        } catch (Exception e) {
            logger.error(GET_ERROR_MSG, e);
            if (execute != null && execute.body() != null) {
                execute.body().close();
            }
        }
        return execute;
    }


    public static Headers.Builder buildHeaders(Map<String, ?> headerParams, Integer retryTimes) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (retryTimes != null) {
            headerBuilder.add(DEFAULT_HEADER_RETRYTIMES, String.valueOf(retryTimes));
        }
        headerBuilder.add(DEFAULT_HEADER_ACCEPT_ENCODING, DEFAULT_HEADER_ACCEPT_ENCODING_VALUE);
        headerBuilder.add(OKHTTP_LOGGER_NG_TRACE_ID, (MDC.get(OKHTTP_LOGGER_NG_TRACE_ID) == null) ? "" : MDC.get(OKHTTP_LOGGER_NG_TRACE_ID));
        if (headerParams != null) {
            for (Map.Entry<String, ?> headerEntry : headerParams.entrySet()) {
                if (headerEntry.getValue() != null) {
                    headerBuilder.add(headerEntry.getKey(), getValueEncoded(headerEntry.getValue().toString()));
                } else {
                    headerBuilder.add(headerEntry.getKey(), "");
                }
            }
        }
        return headerBuilder;
    }

    public static String getValueEncoded(String value) {
        if (value == null) {
            return "";
        }
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                try {
                    return URLEncoder.encode(newValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("getValueEncoded error", e);
                }
            }
        }
        return newValue;
    }

    public static String buildGetUrl(String url, Map<String, ?> params) {
        StringBuilder builder = null;
        if (params != null) {
            for (String key : params.keySet()) {
                if (key != null && params.get(key) != null) {
                    if (builder == null) {
                        builder = new StringBuilder(url);
                        builder.append((url.indexOf("?") == -1) ? "?" : "&");
                    } else {
                        builder.append("&");
                    }
                    builder.append(key).append("=");
                    if (params.get(key) != null) {
                        builder.append(encodeParams(params.get(key).toString()));
                    } else {
                        builder.append("");
                    }
                }
            }
        }
        builder = (builder != null) ? builder : new StringBuilder(url);

        return builder.toString();
    }

    public static String encodeParams(String params) {
        try {
            params = URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encodeParams异常", e);
        }
        return params;
    }

    private static String swapTag(String tag) {
        tag = tag == null ? "" : tag;
        return tag;
    }
}
