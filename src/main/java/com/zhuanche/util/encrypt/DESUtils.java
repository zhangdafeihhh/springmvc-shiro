/*
 *
 * Copyright (c) 2006- CE, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * CE Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with CE.
 */
package com.zhuanche.util.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * DES算法工具类
 * <pre>
 *    //DES 加密解密算法以
 *    String sourceString = ...;
 *    String key = ...;
 *
 *    //计算SHA散列值，返回二进制数组
 *    String result1 = DESUtils.encrypt(sourceString,key);
 *    
 *    String result2 = SHAUtils.getSHADigest(result1,key);
 *	  
 *	  result2  == sourceString;
 * </pre>
 * @author will
 * @author todd
 * @since JDK1.5
 */
public final class DESUtils {
	
	/**
	 * 不创建实例
	 */
	private DESUtils() {
	}

	private static final String KEY_ALGORITHM = "DES";

	/**
	 * 生成加密key
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) throws Exception {
		DESKeySpec des = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		SecretKey secretKey = keyFactory.generateSecret(des);
		return secretKey;
	}
	/**
	 * base64 网络传输规则特殊字符
	 * @param secStr
	 * @return
	 */
	private static String enreplace(String secStr){
		secStr=secStr.replaceAll("\\+", "\\-");
		secStr=secStr.replaceAll("\\/", "\\*");
		secStr=secStr.replaceAll("\\=", "\\.");
		return secStr;
	}
	/**
	 * base64 网络传输规则特殊字符
	 * @param secStr
	 * @return
	 */
	private static String dereplace(String secStr){
		secStr=secStr.replaceAll("\\-","\\+");
		secStr=secStr.replaceAll("\\*","\\/");
		secStr=secStr.replaceAll("\\.","\\=");
		return secStr;
	}
	/**
	 * 加密
	 * @param data
	 * @param key
	 * @return
	 */
	public static String encrypt(String data, String key)  {
		String keyDes = key ;
		keyDes = StringUtils.center(keyDes, 8,"*");
		try {
			return enreplace(new String(Base64.encodeBase64(encrypt(data.getBytes(), keyDes.getBytes()))));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 加密
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom random = new SecureRandom(); 
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k,random);
		return cipher.doFinal(data);
	}

	/**
	 * 解密
	 * @param data
	 * @param key
	 * @return
	 */
	public static String decrypt(String data, String key)  {
		String keyDes = key ;
		keyDes =StringUtils.center(keyDes, 8,"*");
		data = dereplace(data);
		try {
			return new String(decrypt(Base64.decodeBase64(data.getBytes()), keyDes.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 解密
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom random = new SecureRandom(); 
		Key k = toKey(key);
		Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k ,random);
		return cipher.doFinal(data);
	}
	
    /**
     * 生成加密字符串
     *
     * @return String 加密后的字符串
     */
    public static String encrypter(String oriStr,String key) {
        Blowfish cipher=new Blowfish(key);
        return cipher.encryptString(oriStr);
    }


    /**
     * 解密并将得到的用户名和密码保存到一个字符串数组
     *
     * @param value String
     * @return String
     */
    public static String decrypter(String value,String key) {
        Blowfish cipher=new Blowfish(key);
        value = cipher.decryptString(value);
        return value;
    }
}
