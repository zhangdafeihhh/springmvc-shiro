package com.zhuanche.util;

import com.zhuanche.oss.McOssClient;
import com.zhuanche.oss.McOssClientBuilder;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.util.Date;

/**
 * @Author fanht
 * @Description
 * @Date 2021/3/9 上午11:19
 * @Version 1.0
 */
public class AliyunImgUtils {

    static McOssClient mcOssClient = new McOssClientBuilder().build();


    public static String transUrl(String url) {

        if (StringUtils.isEmpty(url)) {
            return null;
        }

        try {
            url = mcOssClient.getSignedUrlByFileType(url, new Date(System.currentTimeMillis() + 100000));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }

}
