package com.zhuanche.common.enums;

/**
 * @Author fanht
 * @Description 订单状态枚举类
 * @Date 2019/10/14 下午3:39
 * @Version 1.0
 */
public enum OrderStateEnum {


    SYSTEM_STRONG_FACTION(1,"系统强派"),
    DRIVER_ROBBING(2,"司机抢单"),
    MANUAL_ASSIGNMENT(3,"后台人工指派"),



    //车型类型
    COMFORTABLE(34,"舒适型"),
    BUSINESS7(35,"商务7座"),
    BUSINESSWELL(40, "商务福祉车"),
    LUXURY(41, "豪华型"),
    ENJOY(43, "畅享型"),
    TESLA(54, "特斯拉");



    private int code;

    private String msg;

    OrderStateEnum(int code, String msg) {
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
