package com.zhuanche.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ftp 工具类
 *
 * @author dudu
 *
 */
@Component
public class BigDataFtpUtil {

	private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);

	private static FTPClient ftpClient = new FTPClient();

	@Value("${ftpBigdataServerUrl}")
	private static String ftpServerUrl;
	@Value("${ftpBigdataServerPath}")
	private static String ftpServerPath;
	@Value("${ftpBigdataServerPort}")
	private static int ftpServerPort = 21;
	@Value("${ftpBigdataServerUserName}")
	private static String ftpServerUserName;
	@Value("${ftpBigdataServerPassword}")
	private static String ftpServerPassword;

	/**
	 * 上传
	 *
	 * @param path
	 *            上传路径
	 * @param fileName
	 *            文件名
	 * @param fis
	 *            上传文件字节流
	 * @throws Exception
	 */
	public void upload(String path, String fileName, InputStream fis) throws Exception {
		InputStream in = null;
		try {
			ftpClient.setDefaultTimeout(20 * 1000);
			ftpClient.setConnectTimeout(20 * 1000);
			ftpClient.setDataTimeout(20 * 1000);
			ftpClient.enterLocalPassiveMode();
			ftpClient.connect(ftpServerUrl, ftpServerPort);
			ftpClient.login(ftpServerUserName, ftpServerPassword);
			boolean isSuccess = createDir(ftpServerPath + path);
			logger.info("ftp上传地址"+ftpServerPath + path+"切换目录"+isSuccess);
			ftpClient.setBufferSize(1024);
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
			ftpClient.storeFile(fileName, fis);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			try {
				if (fis != null) {
					IOUtils.closeQuietly(fis);
				}
				if (in != null) {
					IOUtils.closeQuietly(in);
				}
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 删除ftp服务器上文件
	 *
	 * @param path
	 * @param fileName
	 * @throws Exception
	 */
	public void delete(String path, String fileName) throws Exception {
		InputStream in = null;
		try {
			ftpClient.connect(ftpServerUrl, ftpServerPort);
			ftpClient.login(ftpServerUserName, ftpServerPassword);
			ftpClient.changeWorkingDirectory(ftpServerPath + path);
			ftpClient.dele(fileName);
		} catch (Exception e) {
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			try {
				if (in != null) {
					IOUtils.closeQuietly(in);
				}
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 删除文件并移除文件夹
	 */
	public void removeDirAndDeleteFile(String path, String fileName) throws Exception {
		try {
			ftpClient.connect(ftpServerUrl, ftpServerPort);
			ftpClient.login(ftpServerUserName, ftpServerPassword);
			ftpClient.changeWorkingDirectory(ftpServerPath + path);
			fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
			logger.info("移除目录"+ftpServerPath + path+"和文件"+fileName);
			ftpClient.deleteFile(fileName);
			ftpClient.removeDirectory(ftpServerPath + path);
		} catch (Exception e) {
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 下载
	 *
	 * @param path
	 *            远程目录
	 * @param webPath
	 *            本地web目录
	 * @param fileName
	 *            文件名
	 */
	public void download(String path, String webPath, String fileName) {
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			ftpClient.connect(ftpServerUrl, ftpServerPort);
			ftpClient.login(ftpServerUserName, ftpServerPassword);
			ftpClient.changeWorkingDirectory(ftpServerPath + path);
			fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
			fos = new FileOutputStream(webPath + fileName);
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.retrieveFile(fileName, fos);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			try {
				if (fos != null) {
					IOUtils.closeQuietly(fos);
				}
				if (in != null) {
					IOUtils.closeQuietly(in);
				}
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 下载
	 *
	 * @param path
	 *            远程目录
	 * @param fileName
	 *            文件名
	 */
	public InputStream download(String path, String fileName) {
		try {
			ftpClient.connect(ftpServerUrl, ftpServerPort);
			ftpClient.login(ftpServerUserName, ftpServerPassword);
			ftpClient.changeWorkingDirectory(ftpServerPath + path);
			ftpClient.setBufferSize(1024);
			// 设置文件类型（二进制）
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
			logger.info("下载目录"+ftpServerPath + path+"和文件"+fileName);
			return ftpClient.retrieveFileStream(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}

	/**
	 * 创建目录(有则切换目录，没有则创建目录)
	 *
	 * @param path
	 * @return
	 */
	public boolean createDir(String path) {
		try {
			// 尝试切入目录
			if (ftpClient.changeWorkingDirectory(path))
				return true;
			String[] arr = path.split("/");
			StringBuffer sbfDir = new StringBuffer();
			// 循环生成子目录
			for (String s : arr) {
				sbfDir.append("/");
				sbfDir.append(s);
				// 尝试切入目录
				if (ftpClient.changeWorkingDirectory(sbfDir.toString()))
					continue;
				if (!ftpClient.makeDirectory(sbfDir.toString())) {
					return false;
				}
			}
			// 将目录切换至指定路径
			return ftpClient.changeWorkingDirectory(sbfDir.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getFtpServerUrl() {
		return ftpServerUrl;
	}

	public static void setFtpServerUrl(String ftpServerUrl) {
		BigDataFtpUtil.ftpServerUrl = ftpServerUrl;
	}

	public static String getFtpServerPath() {
		return ftpServerPath;
	}

	public static void setFtpServerPath(String ftpServerPath) {
		BigDataFtpUtil.ftpServerPath = ftpServerPath;
	}

	public static int getFtpServerPort() {
		return ftpServerPort;
	}

	public static void setFtpServerPort(int ftpServerPort) {
		BigDataFtpUtil.ftpServerPort = ftpServerPort;
	}

	public static String getFtpServerUserName() {
		return ftpServerUserName;
	}

	public static void setFtpServerUserName(String ftpServerUserName) {
		BigDataFtpUtil.ftpServerUserName = ftpServerUserName;
	}

	public static String getFtpServerPassword() {
		return ftpServerPassword;
	}

	public static void setFtpServerPassword(String ftpServerPassword) {
		BigDataFtpUtil.ftpServerPassword = ftpServerPassword;
	}

//	public static void main(String args[]) throws FileNotFoundException, Exception {
///*		FtpUtils ftp = new FtpUtils();
//		ftp.setFtpServerUrl("10.0.5.11");
//		ftp.setFtpServerPort(20);
//		ftp.setFtpServerUserName("charge");
//		ftp.setFtpServerPassword("devcharge");
//		ftp.setFtpServerPath("/dev/");
//		ftp.upload("/12", "text.xlsx", new FileInputStream(new File("C:/Users/dudu/Desktop/abc.xlsx")));*/
//	}

	public static void main(String[] args) {
		String url = "/testuser/updir/2018-11-26/16_月.csv";
		String file = url.split("/")[4];
		System.out.println(file);
		String gg = file.split("\\.")[0];
		System.out.println(gg);
	}
}
