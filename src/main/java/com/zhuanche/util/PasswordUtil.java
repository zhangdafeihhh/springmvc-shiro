package com.zhuanche.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

public final class PasswordUtil {
//	/**生成MD5摘要**/
//	public static String md5( String text ){
//		try {
//			MessageDigest md = MessageDigest.getInstance("MD5");
//			return new String(Hex.encodeHex(md.digest(text.getBytes()))).toLowerCase() ;
//		}catch(Exception ex) {
//			return null;
//		}
//	}
	
	/**生成MD5摘要**/
	public static String md5( String password , String salt ){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return new String(Hex.encodeHex(md.digest( (password+"{"+salt+"}").getBytes()))) .toLowerCase() ;
		}catch(Exception ex) {
			return null;
		}
	}

	public static void main(String[] args) {
//		System.out.println( PasswordUtil.md5("123456") );
		System.out.println( PasswordUtil.md5("111111","admin") );
		System.out.println( "15101067308".substring(7) );
	}
}