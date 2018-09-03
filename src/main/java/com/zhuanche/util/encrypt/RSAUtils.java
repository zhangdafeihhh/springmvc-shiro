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
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密算法实现类
 * <pre>
 *   //使用准备，如果已有数字证书，此步可以忽略
 *   KeyPair keyPair = RSAUtils.generateKeyPair();
 *   PublicKey publicKey = keyPair.getPublic();
 *   PrivateKey privateKey = keyPair.getPrivate()；
 *
 *   //明文
 *   String plainTextString = ... ;
 *   byte[] plainTextBytes = ... ;
 *   //Byte格式加解密
 *   //加密
 *   byte[] cryperTextBytes = RSAUtils.encrypt(plainTextString, publicKey);
 *   或者
 *   byte[] cryperTextBytes = RSAUtils.encrypt(plainTextBytes,publicKey)
 *   //解密
 *   byte[] decryptTextBytes = RSAUtils.decrypt(cryperTextBytes, privateKey));
 *
 *   //Hex格式加解密
 *   //加密
 *   String cryperTextHexString = RSAUtils.encryptHex(plainTextString, publicKey);
 *   或者
 *   String cryperTextHexString = RSAUtils.encryptHex(plainTextBytes,publicKey)
 *   //解密
 *   String decryptTextString = RSAUtils.decryptHex(cryperTextHexString, privateKey));
 *
 *   //Base64格式加解密
 *   //加密
 *   String cryperTextBase64String = RSAUtils.encryptBase64(plainTextString, publicKey);
 *   或者
 *   String cryperTextBase64String = RSAUtils.encryptBase64(plainTextBytes,publicKey)
 *   //解密
 *   String decryptTextBase64String = RSAUtils.decryptBase64(cryperTextBase64String, privateKey));
 * </pre>
 *
 * @author zane
 * @author todd
 * @version 1.2 , 2008/5/7
 * @since JDK1.5
 */
public final class RSAUtils {
	private RSAUtils() {
	}
	private static final String RSA_ALGORITHM = "RSA";

	private static Cipher getRSACipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		return Cipher.getInstance(RSA_ALGORITHM);
	}

	/**
	 * 生成RSA密钥对
	 *
	 * @return 密钥对
	 * @throws NoSuchAlgorithmException
	 * @since 1.0
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		keyGen.initialize(1024);
		return keyGen.generateKeyPair();
	}

	/**
	 * 用公钥加密明文byte数组，返回byte数组密文
	 *
	 * @param plainText
	 *            明文byte数组
	 * @param publicKey
	 *            公钥
	 * @return 密文byte数组
	 * @throws Exception
	 * @since 1.0
	 */
	public static byte[] encrypt(byte[] plainText, PublicKey publicKey) throws Exception {
		Cipher cipher = getRSACipher();
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(plainText);
	}

	/**
	 * 用公钥加密明文字符串，返回byte数组密文
	 *
	 * @param plainText
	 *            明文字符串
	 * @param publicKey
	 *            公钥
	 * @return 密文byte数组
	 * @throws Exception
	 * @since 1.0
	 */
	public static byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
		return encrypt(plainText.getBytes(), publicKey);
	}
	
	 /** 
     * 得到密钥字符串（经过base64编码） 
     *  
     * @return 
     */  
    public static String getKeyString(Key key) throws Exception {  
        byte[] keyBytes = key.getEncoded();  
        String s = new String(Base64.encodeBase64(keyBytes));  
        return s;  
    }
    
    /** 
     * 得到公钥 
     *  
     * @param key 
     *            密钥字符串（经过base64编码） 
     * @throws Exception 
     */  
    public static PublicKey getPublicKey(String key) throws Exception {  
        byte[] keyBytes;  
        keyBytes = Base64.decodeBase64(key);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
        return publicKey;  
    }  
      
    /** 
     * 得到私钥 
     *  
     * @param key 
     *            密钥字符串（经过base64编码） 
     * @throws Exception 
     */  
    public static PrivateKey getPrivateKey(String key) throws Exception {  
        byte[] keyBytes;  
        keyBytes = Base64.decodeBase64(key);  
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
        return privateKey;  
    } 

	/**
	 * 用公钥加密明文byte数组，返回Hex字符串密文
	 *
	 * @param plainText
	 *            明文byte数组
	 * @param publicKey
	 *            公钥
	 * @return Hex字符串
	 * @throws Exception
	 * @since 1.0
	 */
	public static String encryptHex(byte[] plainText, PublicKey publicKey) throws Exception {
		return new String(Hex.encodeHex(encrypt(plainText, publicKey)));
	}

	/**
	 * 用公钥加密明文字符串，返回Hex字符串密文
	 *
	 * @param plainText
	 *            明文字符串
	 * @param publicKey
	 *            公钥
	 * @return Hex字符串
	 * @throws Exception
	 * @since 1.0
	 */
	public static String encryptHex(String plainText, PublicKey publicKey) throws Exception {
		return new String(Hex.encodeHex(encrypt(plainText, publicKey)));
	}

	/**
	 * 用公钥加密明文byte数组，返回Base64字符串密文
	 *
	 * @param plainText
	 *            明文byte数组
	 * @param publicKey
	 *            公钥
	 * @return Base64字符串
	 * @throws Exception
	 * @since 1.0
	 */
	public static String encryptBase64(byte[] plainText, PublicKey publicKey) throws Exception {
		return new String(Base64.encodeBase64(encrypt(plainText, publicKey)));
	}

	/**
	 * 用公钥加密明文字符串，返回Base64字符串密文
	 *
	 * @param plainText
	 *            明文字符串
	 * @param publicKey
	 *            公钥
	 * @return Base64字符串密文
	 * @throws Exception
	 * @since 1.0
	 */
	public static String encryptBase64(String plainText, PublicKey publicKey) throws Exception {
		return new String(Base64.encodeBase64(encrypt(plainText, publicKey)));
	}

	/**
	 * 用私钥解密byte密文，返回byte数组明文
	 *
	 * @param cipherText
	 *            byte密文
	 * @param privateKey
	 *            私钥
	 * @return byte数组明文
	 * @throws Exception
	 * @since 1.0
	 */
	public static byte[] decrypt(byte[] cipherText, PrivateKey privateKey) throws Exception {
		Cipher cipher = getRSACipher();
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(cipherText);
	}

	/**
	 * 用私钥解密Hex密文，返回Hex字符串明文
	 *
	 * @param cipherTextHex
	 *            Hex密文
	 * @param privateKey
	 *            私钥
	 * @return Hex字符串明文
	 * @throws Exception
	 * @since 1.0
	 */
	public static String decryptHex(String cipherTextHex, PrivateKey privateKey) throws Exception {
		return new String(Hex.encodeHex(decrypt(Hex.decodeHex(cipherTextHex.toCharArray()), privateKey)));
	}

	/**
	 * 用私钥解密Base64格式密文，返回Base64格式字符串明文
	 *
	 * @param cipherTextBase64
	 *            Base64格式密文
	 * @param privateKey
	 *            私钥
	 * @return Base64格式字符串明文
	 * @throws Exception
	 * @since 1.0
	 */
	public static String decryptBase64(String cipherTextBase64, PrivateKey privateKey) throws Exception {
		return new String(Base64.encodeBase64(decrypt(Base64.decodeBase64(cipherTextBase64.getBytes()), privateKey)));
	}
}
