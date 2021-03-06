package com.zhuanche.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;

/**
 * ftp 工具类
 *
 * @author dudu
 *
 */
@Component
public class BigDataFtpUtil {

	private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);

	//ftp服务器地址
	@Value("${ftpBigdataServerUrl}")
	public String hostname;
	//ftp服务器端口号默认为21
	@Value("${ftpBigdataServerPort}")
	public Integer port = 21 ;
	//ftp登录账号
	@Value("${ftpBigdataServerUserName}")
	public String username;
	//ftp登录密码
	@Value("${ftpBigdataServerPassword}")
	public String password;

	public FTPClient ftpClient = null;

	/**
	 * 初始化ftp服务器
	 */
	public void initFtpClient() {
		ftpClient = new FTPClient();
		ftpClient.setControlEncoding("utf-8");
		try {
			logger.info("connecting...ftp服务器:"+this.hostname+":"+this.port);
			ftpClient.connect(hostname, port); //连接ftp服务器
			ftpClient.login(username, password); //登录ftp服务器
			ftpClient.setBufferSize(1024);//可以控制上传或下载的速度
			int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
			if(!FTPReply.isPositiveCompletion(replyCode)){
				logger.info("connect failed...ftp服务器:"+this.hostname+":"+this.port);
			}
			logger.info("connect successfu...ftp服务器:"+this.hostname+":"+this.port);
		}catch (MalformedURLException e) {
			logger.error("ftp上传异常",e);
 		}catch (IOException e) {
			logger.error("ftp上传异常",e);
		}
	}

	/**
	 * 上传文件
	 * @param pathname ftp服务保存地址
	 * @param fileName 上传到ftp的文件名
	 *  @param originfilename 待上传文件的名称（绝对地址） *
	 * @return
	 */
	public boolean uploadFile( String pathname, String fileName,String originfilename){
		boolean flag = false;
		InputStream inputStream = null;
		try{
			logger.info("开始上传文件");
			inputStream = new FileInputStream(new File(originfilename));
			initFtpClient();
			ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			ftpClient.makeDirectory(pathname);
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			ftpClient.logout();
			flag = true;
			logger.info("上传文件成功");
		}catch (Exception e) {
			logger.info("上传文件失败",e);
		}finally{
			if(ftpClient.isConnected()){
				try{
					ftpClient.disconnect();
				}catch(IOException e){
					logger.info("上传文件错误",e);
				}
			}
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.info("上传文件错误",e);
				}
			}
		}
		return true;
	}
	/**
	 * 上传文件
	 * @param pathname ftp服务保存地址
	 * @param fileName 上传到ftp的文件名
	 * @param inputStream 输入文件流
	 * @return
	 */
	public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
		boolean flag = false;
		try{
 			initFtpClient();
			ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
			CreateDirecroty(pathname);
			ftpClient.makeDirectory(pathname);
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.storeFile(fileName, inputStream);
			inputStream.close();
			ftpClient.logout();
			flag = true;
 		}catch (Exception e) {
			logger.info("",e);
 		}finally{
			if(ftpClient.isConnected()){
				try{
					ftpClient.disconnect();
				}catch(IOException e){
					logger.info("",e);
				}
			}
			if(null != inputStream){
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.info("",e);
				}
			}
		}
		return true;
	}
	//改变目录路径
	public boolean changeWorkingDirectory(String directory) {
		boolean flag = true;
		try {
			flag = ftpClient.changeWorkingDirectory(directory);
			if (flag) {
				logger.info("进入文件夹" + directory + " 成功！");

			} else {
				logger.info("进入文件夹" + directory + " 失败！开始创建文件夹");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return flag;
	}

	//创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
	public boolean CreateDirecroty(String remote) throws IOException {
		boolean success = true;
		String directory = remote + "/";
		// 如果远程目录不存在，则递归创建远程服务器目录
		if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			String path = "";
			String paths = "";
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
				path = path + "/" + subDirectory;
				if (!existFile(path)) {
					if (makeDirectory(subDirectory)) {
						changeWorkingDirectory(subDirectory);
					} else {
 						changeWorkingDirectory(subDirectory);
					}
				} else {
					changeWorkingDirectory(subDirectory);
				}

				paths = paths + "/" + subDirectory;
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return success;
	}

	//判断ftp服务器文件是否存在
	public boolean existFile(String path) throws IOException {
		boolean flag = false;
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (ftpFileArr.length > 0) {
			flag = true;
		}
		return flag;
	}
	//创建目录
	public boolean makeDirectory(String dir) {
		boolean flag = true;
		try {
			flag = ftpClient.makeDirectory(dir);
			if (flag) {
 				logger.info("创建文件夹" + dir + " 成功！");
			} else {
				logger.info("创建文件夹" + dir + " 失败！");
 			}
		} catch (Exception e) {
			logger.error("创建文件夹异常",e);
		}
		return flag;
	}

	/** * 下载文件 *
	 * @param pathname FTP服务器文件目录 *
	 * @param filename 文件名称 *
	 * @param localpath 下载后的文件路径 *
	 * @return */
	public  boolean downloadFile(String pathname, String filename, String localpath){
		boolean flag = false;
		OutputStream os=null;
		try {
			logger.info("开始下载文件");
			initFtpClient();
			//切换FTP目录
			ftpClient.changeWorkingDirectory(pathname);
			FTPFile[] ftpFiles = ftpClient.listFiles();
			for(FTPFile file : ftpFiles){
				if(filename.equalsIgnoreCase(file.getName())){
					File localFile = new File(localpath + "/" + file.getName());
					os = new FileOutputStream(localFile);
					ftpClient.retrieveFile(file.getName(), os);
					os.close();
				}
			}
			ftpClient.logout();
			flag = true;
			logger.info("下载文件成功");
		} catch (Exception e) {
			logger.error("下载文件失败",e);
		} finally{
			if(ftpClient.isConnected()){
				try{
					ftpClient.disconnect();
				}catch(IOException e){
					logger.info("文件下载异常",e);
				}
			}
			if(null != os){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/** * 删除文件 *
	 * @param pathname FTP服务器保存目录 *
	 * @param filename 要删除的文件名称 *
	 * @return */
	public boolean deleteFile(String pathname, String filename){
		boolean flag = false;
		try {
			logger.info("开始删除文件");
 			initFtpClient();
			//切换FTP目录
			ftpClient.changeWorkingDirectory(pathname);
			ftpClient.dele(filename);
			ftpClient.logout();
			flag = true;
			logger.info("删除文件成功");
		} catch (Exception e) {
			logger.info("删除文件失败",e);
		} finally {
			if(ftpClient.isConnected()){
				try{
					ftpClient.disconnect();
				}catch(IOException e){
					logger.info("删除文件异常",e);
				}
			}
		}
		return flag;
	}

	/** * 下载文件 *
	 * @param pathname FTP服务器文件目录 *
	 * @param filename 文件名称 *
	 * @return */
	public InputStream downloadFile(String pathname, String filename){
		InputStream in = null;
		try {
			logger.info("开始下载文件");
			initFtpClient();
			//切换FTP目录
			ftpClient.setBufferSize(1024);//可以控制上传或下载的速度
			ftpClient.changeWorkingDirectory(pathname);
			String path = pathname+"/"+filename;
			in = ftpClient.retrieveFileStream(path);
			return in;
//			FTPFile[] ftpFiles = ftpClient.listFiles();
//			for(FTPFile file : ftpFiles){
//				if(filename.equalsIgnoreCase(file.getName())){
//					String path = pathname+"/"+filename;
//					in = ftpClient.retrieveFileStream(path);
//					return in;
//				}
//			}
//			ftpClient.logout();
//			System.out.println("下载文件成功");
		} catch (Exception e) {
			logger.info("下载文件失败",e);
		} finally{
			if(ftpClient.isConnected()){
				try{
					ftpClient.disconnect();
				}catch(IOException e){
					logger.error("连接异常",e);
				}
			}
//			if(null != in){
//				try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
		}
		return in;
	}

	public static void main(String[] args) {
////        FtpUtils ftp =new FtpUtils();
//        //ftp.uploadFile("ftpFile/data", "123.docx", "E://123.docx");
////        ftp.downloadFile("ftpFile/data", "123.docx", "F://");
////        ftp.deleteFile("ftpFile/data", "123.docx");
//        System.out.println("ok");


//		BigDataFtpUtil ftp =new BigDataFtpUtil();
//		boolean b = ftp.downloadFile("/testuser/updir", "16_月.csv", "D://");
//		System.out.println(b);


		BigDataFtpUtil ftp =new BigDataFtpUtil();
		InputStream inputStream = ftp.downloadFile("/testuser/updir", "16_月.csv");
		System.out.println(inputStream);
	}
}
