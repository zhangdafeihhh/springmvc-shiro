package com.zhuanche.controller.authsupplier;

/**
 * @Author fanht
 * @Description
 * @Date 2020/6/16 下午6:21
 * @Version 1.0
 */
public enum SendPhoneEnum {

    RESET_PASSWORD(1,"修改密码"),

    UPDATE_PHONE(2,"修改手机号");

    SendPhoneEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
