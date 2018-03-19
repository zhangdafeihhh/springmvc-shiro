package com.zhuanche.exception;

/**
 * Created by wuhui on 2017/7/17.
 */
public enum  InfoCode {

    SUCCESS(0,"成功"),
    INVALID_PARAM(1000,"请求参数缺失或不正确"),
    URL_NOT_FOUND(4000,"请求服务不存在"),
    INVALID_REQUEST(4001,"不支持的请求方式"),
    SERVICE_UNAVAILABLE(5000,"服务异常，请稍后再试");

    private int status;

    private String msg;

    InfoCode(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
