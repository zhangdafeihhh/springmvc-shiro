package com.zhuanche.exception;

/**
 * @Author fanht
 * @Description
 * @Date 2018/11/22 下午5:11
 * @Version 1.0
 */
public class MessageAbstractException extends RuntimeException{

    //返回的code码
    protected int code;
    //返回值信息
    protected String Message;


    public MessageAbstractException(int code, String message) {
        this.code = code;
        Message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
