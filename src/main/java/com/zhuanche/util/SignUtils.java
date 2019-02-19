package com.zhuanche.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.zhuanche.util.encrypt.MD5Utils;

public class SignUtils {

	public static final String SIGN = "sign";

	public static boolean checkMD5Sign(Map<String, Object> paramMap, String sign, String signKey) {
		String md5Sign = createMD5Sign(paramMap, signKey);
		System.out.println(md5Sign);
		boolean result = md5Sign.equals(sign);

		return result;
	}

	public static String createMD5Sign(Map<String, Object> paramMap, String signKey) {
		List<String> sortedKeys = new ArrayList<String>();
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			if (SIGN.equals(entry.getKey())) {
				continue;
			}
			sortedKeys.add(entry.getKey());
		}
		// 没有参数
		if (sortedKeys.size() == 0) {
			return "";
		}

		Collections.sort(sortedKeys);
		StringBuffer buff = new StringBuffer("");
		for (String key : sortedKeys) {
			Object val = paramMap.get(key);
            if (val == null || StringUtils.isBlank(val.toString())) {
				continue;
			}
			buff.append(key).append("=").append(val).append("&");
		}
		buff.append("key=").append(signKey);

		try {
			return MD5Utils.getMD5DigestBase64(buff.toString());
		} catch (Exception e) {
			throw new RuntimeException("签名错误");
		}
	}

}
