package com.zhuanche.exception;

/**
 * Created by wuhui on 2017/7/17.
 */
public class ApiRuntimeException extends RuntimeException{

    private static final long serialVersionUID = -3197616652643414121L;

    private InfoCode infoCode;

    public ApiRuntimeException(InfoCode infoCode){
        super(infoCode.getMsg());
        this.infoCode = infoCode;
    }

    public InfoCode getInfoCode() {
        return infoCode;
    }
}
