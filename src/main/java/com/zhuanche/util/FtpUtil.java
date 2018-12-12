package com.zhuanche.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * FTP文件上传、下载操作
 *
 * Created on 2017年12月19日17:49:02
 * @author lilongsheng
 * @since 1.0
 */
@Component
public final class FtpUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

//    public static final Properties CONFIG = getConfigProperties();
    public static final String PROTOCAL= "ftp://";

	@Value("${ftp.sqyc.host}")
	private String REMOTE_ADDRESS;
//    public static final String REMOTE_ADDRESS = CONFIG.getProperty("ftp.sqyc.host");

	@Value("${ftp.sqyc.remotePath}")
	private String REMOTE_PATH;
//    public static final String REMOTE_PATH = CONFIG.getProperty("ftp.sqyc.remotePath");

	@Value("${ftp.sqyc.port}")
	private String REMOTE_PORT;
//    public static final String REMOTE_PORT = CONFIG.getProperty("ftp.sqyc.port");

	@Value("${ftp.sqyc.password}")
	private String PASSWORD;
//    private static final String PASSWORD = CONFIG.getProperty("ftp.sqyc.password");

	@Value("${ftp.sqyc.name}")
	private String USER_NAME;
//    private static final String USER_NAME = CONFIG.getProperty("ftp.sqyc.name");

	private static Properties ftpPathConfig;

    private FTPClient ftpClient;

	private FtpUtil() { }

	public static FtpUtil getInstance() {
		return new FtpUtil();
	}

//	public static Properties getConfigProperties() {
//		if(CONFIG == null){
//			Properties p = new Properties();
//			try {
//				p.load(FtpUtil.class.getResourceAsStream("/ftp.properties"));
//				return p ;
//			} catch (IOException e) {
//				e.printStackTrace();
//                logger.error("风控-上传下载异常-加载配置文件 error:{}",e.getMessage());
//                return null;
//			}
//
//		}else{
//			return CONFIG;
//		}
//
//	}

	/**
	 * 连接FTP服务器
	 * @param remotePath 远程访问路径
	 */
	public void connectServer(String remotePath) {
        String path=remotePath;
		try {
			if (path == null) {
				path = this.REMOTE_PATH;
			}

			ftpClient = new FTPClient();

			if (REMOTE_PORT == null || 0 == Integer.parseInt(REMOTE_PORT)) {
				ftpClient.connect(REMOTE_ADDRESS);
			} else {
				ftpClient.connect(REMOTE_ADDRESS, Integer.parseInt(REMOTE_PORT));
			}

			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				// FTP server refused connection.
				closeConnect();
				System.exit(1);
			}

			ftpClient.login(USER_NAME, PASSWORD);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// login success !!!

			if (path.length() != 0) {
//				boolean flag =
                createDirecroty(path, ftpClient);

				/*if (flag) {
					// set working directory successful !!!
				}*/
			}
		} catch (IOException e) {
			// not login !!!
			e.printStackTrace();
		}
	}

	public void connectServer() {
		connectServer(null);
	}

    /**
     * 关闭与FTP服务器之间的连接
     */
	public void closeConnect() {
		try {
			ftpClient.disconnect();
			// disconnect success !!!
		} catch (IOException e) {
			// not disconnect !!!
			e.printStackTrace();
		}
	}

    /**
     * 创建工作目录，并将当前工作目录定位到新创建的目录
     * 采用一次命令创建一个文件夹的方式，已兼容 IBM FTP
     * @param path 要创建的目录路径
     * @return 是否操作成功
     * @throws IOException
     */
	public boolean alertWorkingDirectory(String path) throws IOException {
		boolean flag = ftpClient.changeWorkingDirectory(path);
		if (!flag) {
			String[] ps = path.split("/");
            for (String p : ps) {
                if (!ftpClient.changeWorkingDirectory(p)) {
                    if (ftpClient.makeDirectory(p)) {
                        flag = ftpClient.changeWorkingDirectory(p);
                    } else {
                        flag = false;
                        break;
                    }
                }
            }
		}
		return flag;
	}

	/**
	 * Description: 递归创建远程服务器目录
	 *
	 * @param remote
	 *            远程服务器文件绝对路径
	 * @param ftpClient
	 *            FTPClient 对象
	 * @return boolean 目录创建是否成功
	 * @throws IOException
	 */
	public boolean createDirecroty(String remote, FTPClient ftpClient)
			throws IOException {
		boolean status = false;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!directory.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(new String(directory
						.getBytes("utf-8"), "iso-8859-1"))) {
			// 如果远程目录不存在，则递归创建远程服务器目录
			int start;
			int end;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				String subDirectory = new String(remote.substring(start, end)
						.getBytes("utf-8"), "iso-8859-1");
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						// 创建目录失败
						return status;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}

		status = true;
		return status;
	}

	/**
	 * 文件上传
	 * @param path 文件保存路径
	 * @param fileName 文件名称
	 * @param inputStream 文件流
	 * @return 操作是否成功
	 */
	public boolean upload(String path, String fileName, InputStream inputStream) {

		boolean flag = false;
		try {
			createDirecroty(path, ftpClient);
			flag = ftpClient.storeFile(fileName, inputStream);
			/*if (flag) {
				// upload success !!!
			}*/
		} catch (IOException e) {
			// not upload !!!
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 根据传入的路径建立输出流
	 * @Version1.0 2014-7-9 上午11:55:14 by 张延伟（yw.zhang02@zuche.com）创建
	 * @param path 文件保存路径
	 * @param fileName 文件名称
	 * @return
	 */
	public OutputStream storeFileStream(String path, String fileName) {
		OutputStream os = null;
		try {
			createDirecroty(path, ftpClient);
			os = ftpClient.storeFileStream(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os;
	}

	/**
	 * 判断文件是否存在
	 * @param path 文件路径
	 * @param fileName 文件名称
	 * @return ture：存在 false：不存在
	 */
	public boolean isFileExist(String path, String fileName){
		try {
			boolean cdStatus = ftpClient.changeWorkingDirectory(new String(path.getBytes("utf-8"), "iso-8859-1"));
			if(!cdStatus){
				return false;
			}else {
				String[] fileNames = ftpClient.listNames();
				return Arrays.binarySearch(fileNames, fileName) > -1;
			}
		} catch (Exception e) {
            e.printStackTrace();
            logger.error("风控-上传下载异常-文件是否存在 error:{}",e.getMessage());
            return false;
		}

	}

	/**
	 * 文件下载
	 * @param fileName 文件的完整路径
	 * @return 下载到的文件流
	 */
	public InputStream download(String fileName) {
		InputStream inputStream = null;
		try {
			inputStream = ftpClient.retrieveFileStream(fileName);
		} catch (IOException e) {
			// not download !!!
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * Description: 获取FTP服务器上文件存储目录
	 *
	 * @param type
	 *            文件所属模块类型
	 * @return 文件服务器端路径
	 */
	public static String getRemoteFileDir(int type){

		StringBuilder remoteFileDir = new StringBuilder();

		remoteFileDir.append(getFtpPathConfig().getProperty(String.valueOf(type)));
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");

		String datePath = sf.format(new Date());

		remoteFileDir.append(datePath).append("/");

		return remoteFileDir.toString();
	}

    private static Properties getFtpPathConfig(){
        synchronized (FtpUtil.class) {
            if (ftpPathConfig == null) {
                Properties p = new Properties();
                InputStream in = null;
                try {
                    in = FtpUtil.class.getResourceAsStream("/ftpUploadPathConfig.properties");
                    p.load(in);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("风控-上传下载异常-文件路径配置 error:{}",e.getMessage());
                }finally{
                    if(in != null){
                        try {
                            in.close();
                        } catch (IOException e) {
                            in = null;
                        }
                    }
                }
                ftpPathConfig = p;
            }
        }

        return ftpPathConfig;
    }

    public boolean completePendingCommand(){
        try {
            return ftpClient.completePendingCommand();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("风控-上传下载异常-执行命令 error:{}",e.getMessage());
            return false;
        }
    }

	/**初始化配置    文件位置configLocation **/
	public static synchronized void init( String configLocation) {
		try{
			logger.info("***************initializing ftp"+configLocation+"]");
			ftpPathConfig = PropertiesLoaderUtils.loadAllProperties(configLocation);
			logger.info( ftpPathConfig.toString()  );
			logger.info("***************initialized ftp["+configLocation+"]\n");
		} catch (Exception e) {
			logger.error("ftp初始化配置文件："+ configLocation, e);
			System.err.println("ftp初始化配置文件："+ configLocation );
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
