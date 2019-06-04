package com.zhuanche.common.exception;

/**
 * @Author: nysspring@163.com
 * @Description: 服务层自定义异常
 * @Date: 14:24 2019/5/14
 */
public class ServiceException extends RuntimeException {

    private Integer errorCode;

    public ServiceException(Integer errorCode){
        this.errorCode = errorCode;
    }

    public ServiceException(Integer errorCode,Throwable e){
        super(e);
        this.errorCode = errorCode;
    }

    public ServiceException(Integer errorCode,String msg){
        super(msg);
        this.errorCode = errorCode;
    }
    public ServiceException(Integer errorCode,String msg,Throwable e){
        super(msg,e);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
