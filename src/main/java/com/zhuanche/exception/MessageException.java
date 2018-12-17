package com.zhuanche.exception;

/**
 * @Author fanht
 * @Description
 * @Date 2018/11/22 下午5:12
 * @Version 1.0
 */
public class MessageException extends MessageAbstractException{

    public MessageException(int code, String message) {
        super(code, message);
    }
}
