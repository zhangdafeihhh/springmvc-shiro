package com.zhuanche.common.util;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * @Author fanht
 * @Description lbs加密验签
 * @Date 2019/10/23 下午2:45
 * @Version 1.0
 */
public class LbsSignUtil {

    /**
     *
     * @param param 请求参数
     * @param key   业务线id对应的密钥
     * @return
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
