package com.zhuanche.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: MapUrlParamUtils
 * @Description: map 与 url参数转换
 * @author: yanyunpeng
 * @date: 2018年10月30日 下午3:59:03
 * 
 */
public class MapUrlParamUtils {
	
	/**
	 * @Title: getMapParamsByUrl
	 * @Description: 将url参数转换成map
	 * @param param
	 * @return Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> getMapParamsByUrl(String param) {
		Map<String, Object> map = new HashMap<String, Object>(0);
		if (StringUtils.isBlank(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (int i = 0; i < params.length; i++) {
			String[] p = params[i].split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

	/**
	 * @Title: getUrlParamsByMap
	 * @Description: 将map转换成url
	 * @param map
	 * @return String
	 * @throws
	 */
	public static String getUrlParamsByMap(Map<String, Object> map) {
		if (map == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append("&");
		}
		String s = sb.toString();
		if (s.endsWith("&")) {
			s = StringUtils.substringBeforeLast(s, "&");
		}
		return s;
	}
}
