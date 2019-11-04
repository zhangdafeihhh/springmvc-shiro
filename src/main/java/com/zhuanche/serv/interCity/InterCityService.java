package com.zhuanche.serv.interCity;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/14 下午4:12
 * @Version 1.0
 */
public interface InterCityService {

    /**
     * 手动录入订单
     * @return
     */
    int handOperateOrder();

    int editOrder();

    int cancelOrder(String orderNo);

    int assignmentOrder(String orderNo);

    int reassignmentOrder(String orderNo);
}
