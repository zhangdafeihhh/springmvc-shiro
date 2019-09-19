package com.zhuanche.controller.supplierFee;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/16 上午10:18
 * @Version 1.0
 */
public enum SupplierFeeStatusEnum {

    NORMAL(1,"金额正常"),
    UNNORMAL(2,"金额异议");

    private int code;

    private String msg;

    SupplierFeeStatusEnum(int code, String msg) {
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
