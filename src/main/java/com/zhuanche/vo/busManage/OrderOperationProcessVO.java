package com.zhuanche.vo.busManage;

import java.util.Date;

/**
 * @program: mp-manage
 * @description: 订单操作过程
 * @author: niuzilian
 * @create: 2018-12-22 19:45
 **/
public class OrderOperationProcessVO {
    /**事件名称*/
    private String eventName;
    /**操作事件*/
    private Date operationTime;
    /**事件描述*/
    private String desc;

    public OrderOperationProcessVO() {
    }

    public OrderOperationProcessVO(String eventName) {
        this.eventName = eventName;
    }

    public OrderOperationProcessVO(String eventName, Date operationTime, String desc) {
        this.eventName = eventName;
        this.operationTime = operationTime;
        this.desc = desc;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
