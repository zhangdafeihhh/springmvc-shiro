package com.zhuanche.util.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAWithBase64Utils {
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 使用私钥加密
     * @param plainTextData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String encrypt(byte[] plainTextData, RSAPrivateKey privateKey)
            throws Exception {
        if (privateKey == null) {
            throw new Exception("加密私钥为空, 请设置");
        }
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(plainTextData);
        return Base64.encodeBase64String(output);
    }

    /**
     * 使用私钥解密
     * @param cipherData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] cipherData, RSAPrivateKey privateKey)
            throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(cipherData);
        return new String(output);
    }

    /**
     * 公钥加密
     * @param plainTextData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encrypt(byte[] plainTextData, RSAPublicKey publicKey)
            throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(plainTextData);
        return Base64.encodeBase64String(output);
    }

    /**
     * 使用公钥解密
     * @param cipherData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] cipherData, RSAPublicKey publicKey)
            throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(cipherData);
        return new String(output);
    }

    /**
     * 通过公钥Base64字符串获取公钥
     * @param publicKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64.decodeBase64(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 通过私钥Base64字符串获取私钥
     * @param privateKeyStr
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] buffer = Base64.decodeBase64(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

}
