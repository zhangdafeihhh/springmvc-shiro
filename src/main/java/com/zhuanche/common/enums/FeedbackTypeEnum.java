package com.zhuanche.common.enums;

public enum FeedbackTypeEnum {

    BACK_GROUND_USE(1,"后台使用"),
    ACTIVITY_USE(2,"活动政策"),
    BILL_COUNT(3,"账单结算"),
    ELSE_USE(4,"其他")
    ;

    private int code;

    private String msg;

    FeedbackTypeEnum(int code, String msg) {
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
