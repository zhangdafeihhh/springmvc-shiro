package com.zhuanche.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author lixl
 * @ClassName: BeasUtil
 * @Description: 基础工具(这里用一句话描述这个类的作用)
 * @date 2018/8/27 20:02
 */
public class Md5Util {
	
	static Logger logger = LoggerFactory.getLogger(Md5Util.class);
	
	/**
	 * 接口签名
	 * @param paramMap
	 * @param signValue
	 * @return
	 */
	public static String createMD5Sign(Map<String, Object> paramMap, String signValue) {
		List<String> sortList = new ArrayList<String>();
		StringBuffer strBuff = new StringBuffer();
		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			if ("sign".equals(entry.getKey())) {
				continue;
			}
			sortList.add(entry.getKey());
		}
		if (sortList.size() <= 0) {
			return strBuff.toString();
		}
		Collections.sort(sortList);
		
		for (String key : sortList) {
			strBuff.append(key).append("=").append(paramMap.get(key)).append("&");
		}
		strBuff.append("signKey=").append(signValue);
		logger.info("加密拼接字符串="+strBuff.toString());
		try {
			return DigestUtils.md5Hex(strBuff.toString()).toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException("签名异常.");
		}
	}
	
	/**
	 * 客服系统加密方法
	 * @param params
	 * @param signKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String createSignByBase64(TreeMap<String , Object> params , String signKey) throws NoSuchAlgorithmException {
		try {
			StringBuilder str = new StringBuilder();
			for (String mapKey : params.keySet()){
				if(params.get(mapKey) != null){
					str.append(mapKey);
					str.append("=");
					str.append(params.get(mapKey));
					str.append("&");
				}
			}
			str.append("ak="+signKey);
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(str.toString().getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
			String punishExpireDate="3";
			String orderNo="P201809059720536230";
			Integer sysFlag=1;
			String orderId="139829629";
			String punishTypeName="乘客投诉";
			String businessId="KF2213";
			String punishIntegral="0";
			String punishReason="乘客投诉";
			String punishFlow="0.0";
			String stopDay="1";
			Integer driverId=19887;
			String punishPrice="1.0";
			Integer punishType=1;
			Integer relieveId=2213;
			Map<String, Object> signMap = new HashMap<>();
			signMap.put("businessId", businessId);
			signMap.put("orderId", orderId);
			signMap.put("punishType", punishType);
			signMap.put("punishTypeName", punishTypeName);
			signMap.put("punishReason", punishReason);
			signMap.put("stopDay", stopDay);
			signMap.put("orderNo", orderNo);
			signMap.put("relieveId", relieveId);
			signMap.put("punishPrice", punishPrice);
			signMap.put("punishIntegral", punishIntegral);
			signMap.put("punishFlow", punishFlow);
			signMap.put("driverId", driverId);
			signMap.put("punishExpireDate", punishExpireDate);
			signMap.put("sysFlag", sysFlag);
			String md5Hex = Md5Util.createMD5Sign(signMap, "w3b1zh4nr1yqzy2nch5dws1b");
			
			System.out.println("58B97865F21BF8AA3F4606C703A7E4D6");
			System.out.println(md5Hex);
	}

	public static String md5(String str) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] bytes = str.getBytes();
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(bytes);
			byte[] updateBytes = messageDigest.digest();
			int len = updateBytes.length;
			char myChar[] = new char[len * 2];
			int k = 0;
			for (int i = 0; i < len; i++) {
				byte byte0 = updateBytes[i];
				myChar[k++] = hexDigits[byte0 >>> 4 & 0x0f];
				myChar[k++] = hexDigits[byte0 & 0x0f];
			}
			return new String(myChar);
		} catch (Exception e) {
			return null;
		}

	}
}