package com.zhuanche.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class SignatureUtils {

    public static String getMD5Sign(Map<String, Object> paramMap, String signKey) {
        List<String> sortedKeys = new ArrayList<>();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if ("sign".equals(entry.getKey())) {
                continue;
            }

            sortedKeys.add(entry.getKey());
        }

        Collections.sort(sortedKeys);

        StringBuilder buff = new StringBuilder();
        for (String key : sortedKeys) {
            String val = paramMap.get(key).toString();
            if (StringUtils.isBlank(val)) {
                continue;
            }

            buff.append(key).append("=").append(val).append("&");
        }

        buff.append("key=").append(signKey);
        return buff.toString();
    }



    /**
     *
     * @param param 请求参数
     * @param key   业务线id对应的密钥
     * @return  调用派单组加密
     */
    public static final String sign(final Map<String, Object> param, final String key) {
        return doSign(param, key);
    }

    private static final String doSign(final Map<String, Object> param, final String key) {
        Map<String, Object> dict = new TreeMap<>(param);
        dict.remove("sign");
        StringBuilder builder = new StringBuilder(128);
        dict.forEach((k, v) -> {
            if (StringUtils.isBlank(k)) {
                return;
            }
            if (v == null) {
                return;
            }
            if ((v instanceof String) && StringUtils.isBlank(v.toString())) {
                return;
            }

            builder.append(k).append("=").append(v).append("&");
        });
        builder.append("key=").append(key);
        String sign = DigestUtils.md5Hex(builder.toString());
        return sign;
    }
}
