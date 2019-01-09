package com.zhuanche.constants.busManage;

public enum EnumOrderType {
    ANDROID(0, "安卓"),
    IOS(1, "IOS"),
    CUS_SVR_BACK(2, "客服后台创建"),
    ORG(7,"机构"),
    H5(12,"h5"),
    WE_CHAT(13,"微信");

    private Integer code;
    private String description;

    EnumOrderType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

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
        for (EnumOrderType type : EnumOrderType.values()) {
            if (type.getCode() == code) {
                return type.description;
            }
        }
        return null;
    }
}
