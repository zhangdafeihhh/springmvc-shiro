package com.zhuanche.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author fanht
 * @Description
 * @Date 2019/11/1 上午2:10
 * @Version 1.0
 */
@Component
public class StaticRpcUrl {


    public static String  esOrderDataSaasUrl;


    public static String configUrl;


    public static String lbsUrl;


    public static String carRestUrl;



    public static String orderServiceUrl;



    public static String lbsToken;


    public static String searchUrl;


    public static String centerUrl;


    public static String orderCostUrl;


    @Value("${order.saas.es.url}")
    public void setEsOrderDataSaasUrl(String esOrderDataSaasUrl) {
        this.esOrderDataSaasUrl = esOrderDataSaasUrl;
    }

    @Value("${config.url}")
    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }

    @Value("${lbs.url}")
    public void setLbsUrl(String lbsUrl) {
        this.lbsUrl = lbsUrl;
    }

    @Value("${car.rest.url}")
    public void setCarRestUrl(String carRestUrl) {
        this.carRestUrl = carRestUrl;
    }

    @Value("${order.server.api.base.url}")
    public void setOrderServiceUrl(String orderServiceUrl) {
        this.orderServiceUrl = orderServiceUrl;
    }

    @Value("${lbs.token}")
    public void setLbsToken(String lbsToken) {
        this.lbsToken = lbsToken;
    }

    @Value("${search.url}")
    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    @Value("${center.url}")
    public void setCenterUrl(String centerUrl) {
        this.centerUrl = centerUrl;
    }

    @Value("${ordercost.server.api.base.url}")
    public void setOrderCostUrl(String orderCostUrl) {
        this.orderCostUrl = orderCostUrl;
    }
}
