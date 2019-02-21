package com.zhuanche.common.enums;

public enum FeedBackManageStatusEnum {

    TO_ACCEPT(1,"待受理"),
    ALREADY_ACCEPT(2, "已受理")
    ;

    private int code;

    private String msg;

    FeedBackManageStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
