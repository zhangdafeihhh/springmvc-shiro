package com.zhuanche.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author fanht
 * @Description 加载ftp参数
 * @Date 2018/11/24 下午8:13
 * @Version 1.0
 */
@Component
public class FtpConstants {


    /**服务url地址**/
    public static String URL;

    /**ftp服务器地址**/
    public static String FTPURL;

    /**ftp端口**/
    public static String FTPPORT;

    public static final String FTP = "ftp://";

    @Value("${ftp.sqyc.host}")
    public  void setFTPURL(String FTPURL) {
        FtpConstants.FTPURL = FTPURL;
    }

    @Value("${ftp.sqyc.port}")
    public  void setFTPPORT(String FTPPORT) {
        FtpConstants.FTPPORT = FTPPORT;
    }

    @Value("${url}")
    public  void setURL(String URL) {
        FtpConstants.URL = URL;
    }
}
