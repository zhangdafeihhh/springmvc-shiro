package com.zhuanche.util;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;


public class Servlets {

    private static final String prefix = "search_";

    private static final Logger logger = LoggerFactory.getLogger(Servlets.class);

    public static Map<String,Object> getSearchParams(HttpServletRequest request){
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, batchConvertMessyCode(values));
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }

    public static String convertMessyCode(String value){
        if(value!=null){
            if(!(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value))){
                try {
                    value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("编码转换出错误："+e.getMessage(), e);
                }
            }
        }
        return value;
    }

    public static String[]  batchConvertMessyCode(String[] values){
        if(values == null || values.length < 1){
            return values;
        }
        for (int i = 0 ; i < values.length ; i++){
            values[i] = convertMessyCode(values[i]);
        }
        return values;
    }
    /**
     * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
     *
     * 返回的结果的Parameter名已去除前缀.
     */
    @SuppressWarnings("rawtypes")
    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Validate.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, StringUtil.batchConvertMessyCode(values));
                } else {
                    params.put(unprefixed, StringUtil.convertMessyCode(values[0]));
                }
            }
        }
        return params;
    }
}
