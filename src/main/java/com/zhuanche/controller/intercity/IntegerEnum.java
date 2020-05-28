package com.zhuanche.controller.intercity;

/**
 * @Author fanht
 * @Description
 * @Date 2020/5/28 下午3:26
 * @Version 1.0
 */
public enum  IntegerEnum {

    DISCOUNT_TYPE_ZERO(0,"固定金额"),
    DISCOUNT_TYPE_ONE(1,"优惠折扣"),
    ALL_DISCOUNT_TYPE_ZERO(0,"包车固定金额"),
    ALL_DISCOUNT_TYPE_ONE(1,"包车优惠折扣");

    private Integer value;

    private String msg;

    IntegerEnum(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
