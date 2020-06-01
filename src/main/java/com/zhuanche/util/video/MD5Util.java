/**
 * <p>文件: MD5Util.java</p>
 * <p>版权: Copyright © 2016 ZHOULINGFEI., Beijing. All Rights Reserved.</p>
 * <p>作者: 周凌飞(zhlf2001@163.com , 64426359@qq.com)</p>
 * <p>时间: 2016-04-01 18:58:34</p>
*/
package com.zhuanche.util.video;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * MD5工具
 * </p>
 * 
 * @author 周凌飞(zhlf2001@163.com , 64426359@qq.com)
 * @date 2016年3月2日 上午10:30:45
 * @copyright (c)2016 ZHOULINGFEI. Beijing. All Rights Reserved.
 */
public enum MD5Util {


	instance;

	private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

	private static MessageDigest messagedigest = null;

	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsaex) {
			System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5");
			nsaex.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * 获取字符串的md5值（推荐使用） 
	 * 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
	 * </pre>
	 *
	 * @param str
	 * @return
	 */
	public String getMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(str.getBytes(Charset.forName("utf-8")));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5Util.getMD5 error", e);
		}
		return null;
	}

	/**
	 * 获取字符串的md5值 <br>
	 * 
	 * @param str
	 * @return
	 */
	public byte[] getMD5ForByte(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(str.getBytes(Charset.forName("utf-8")));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return array;
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5Util.getMD5ForByte error", e);
		}
		return null;
	}

	/**
	 * 对一个文件获取md5值（支持过大文件）
	 * 
	 * @return md5串
	 */
	public String getMD5(File file) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				messagedigest.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(messagedigest.digest()));
		} catch (FileNotFoundException e) {
			logger.error("MD5Util.getMD5 error", e);
		} catch (IOException e) {
			logger.error("MD5Util.getMD5 error", e);
		} finally {
			try {
				if (null != fileInputStream) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				logger.error("MD5Util.getMD5 error", e);
			}
		}
		return null;
	}

	/**
	 * 测试入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "adsfadsfasdasdfasfd";
		String md5 = MD5Util.instance.getMD5(str);
		System.out.println(md5);
		System.out.println(MD5Util.instance.getMD5ForByte(str));

		// String fileName = "D:\\useful-scripts-master.zip";
		// File file = new File(fileName);
		// String md5 = getMD5(file);
		// System.out.println("MD5: " + md5);
	}
}
