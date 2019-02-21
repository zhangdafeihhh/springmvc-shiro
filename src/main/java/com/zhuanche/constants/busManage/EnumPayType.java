package com.zhuanche.constants.busManage;

//结算类型。0-帐户支付   1-信用卡支付   2-司机代收   1000：微信扫码；2000：支付宝扫码；3000：微信APP；4000：支付宝APP
public enum EnumPayType {

    ACCOUNT_PAY(0, "账户支付"),
    CREDIT_CARD_PAY(1, "信用卡支付"),
    DRIVER_COLL(2, "司机代收"),
    WE_CHAT_SCAN(1000, "微信扫码"),
    ALIPAY_SCAN(2000, "支付宝"),
    WE_CHAT(3000, "微信app"),
    ALIPAY(4000, "支付宝app");

    EnumPayType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 普通方法
    public static String getDis(int code) {
        for (EnumPayType pay : EnumPayType.values()) {
            if (pay.getCode() == code) {
                return pay.description;
            }
        }
        return null;
    }
}