package com.zhuanche.controller.supplierFee;

/**
 * @Author fanht
 * @Description
 * @Date 2019/11/14 上午11:24
 * @Version 1.0
 */
public enum  SupplierFeeManageEnum {


    BECONFIRMED(0,"待确认"),
    WAITINGTICKET(1,"待验票"),
    FAILEDTICKET(2,"验票失败"),
    SUBSTITUTETICKET(3,"代交发票");


    private int code;

    private String msg;

    SupplierFeeManageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static SupplierFeeManageEnum getFeeStatus(int feeStatus){
        switch (feeStatus){
            case 0:
                return BECONFIRMED;
            case 1:
                return WAITINGTICKET;
            case 2:
                return FAILEDTICKET;
            case 3:
                return SUBSTITUTETICKET;
            default:
                return BECONFIRMED;
        }
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
