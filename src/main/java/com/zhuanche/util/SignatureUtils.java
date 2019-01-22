package com.zhuanche.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
}
