//package com.zhuanche.shiro.credentials;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.shiro.crypto.hash.SimpleHash;
//import org.apache.shiro.util.ByteSource;
//
///**
// * Created by wuhui on 2016/2/16.
// */
//public class PasswordHelper {
//
//
//	private static String algorithmName = "md5";
//    private static int hashIterations = 2;
//
//    public void setAlgorithmName(String algorithmName) {
//        PasswordHelper.algorithmName = algorithmName;
//    }
//
//    public  void setHashIterations(int hashIterations) {
//        PasswordHelper.hashIterations = hashIterations;
//    }
//
//    public static String encryptPassword(String userName,String password){
//        return new SimpleHash(
//                algorithmName,
//                password,
//                ByteSource.Util.bytes(userName),
//                hashIterations).toHex();
//    }
//
//    public static void main(String[] args) {
//        System.out.println(encryptPassword("admin","admin"));
//    }
//}
