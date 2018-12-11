package com.zhuanche.util;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.zhuanche.util.encrypt.MD5Utils;
import com.zhuanche.util.encrypt.SHAUtils;

public class ServerUtil {
	private static Properties allenv;
	private static Properties props;
	static {
		try {
			allenv = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application-allenv.properties"));
			String runMode = getRunMode();
			Resource resource = new ClassPathResource(runMode + "/global.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Title: getRunMode
	 * @Description: 获取运行环境
	 * @return String
	 * @throws
	 */
	public static String getRunMode() {
		return allenv.getProperty("runMode", "");
	}

	/**
	 * 取cookie加密key
	 * 
	 * @return
	 */
	public static String getCookieKey() {
		return props.getProperty("cookie.key", "");
	}

	/**
	 * session存储位置
	 * 
	 * @return
	 */
	public static String getSessionStore() {
		return props.getProperty("session.store", "");
	}
	public static String get(String key){
		return props.getProperty(key, "");
		
	}

	public static String getDomain() {

		return props.getProperty("domain", "");
	}
	
	public static String getSecurityKey(){
		return props.getProperty("security.key", "");
	}
	/**
	 * 登录时候用
	 * 
	 * @param name
	 * @param sha1pass
	 * @return
	 */
	public static String createPasswd(String name, String sha1pass) {

		try {
			return SHAUtils.getSHADigestHex(name + MD5Utils.getMD5DigestHex(sha1pass) + get("security.key"));
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	/**
	 * 注册时候用
	 * 
	 * @param name
	 * @param pass
	 * @return
	 */
	public static String createPasswdBySha1(String name, String pass) {

		try {
			return createPasswd(name, SHAUtils.getSHADigestHex(pass));
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

}
