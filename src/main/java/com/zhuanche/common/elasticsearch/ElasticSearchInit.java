package com.zhuanche.common.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author fanht
 * @Description
 * @Date 2018/12/26 下午4:04
 * @Version 1.0
 */
@Component
public class ElasticSearchInit {

    public static String index;

    public static String hostName;

    public static String port;


    @Value("${index}")
    public  void setIndex(String index) {
        ElasticSearchInit.index = index;
    }

    @Value("${hostName}")
    public  void setHostName(String hostName) {
        ElasticSearchInit.hostName = hostName;
    }

    @Value("${port}")
    public  void setPort(String port) {
        ElasticSearchInit.port = port;
    }
}
