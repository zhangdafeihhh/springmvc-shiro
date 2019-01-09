package com.zhuanche.constants.busManage;

public enum EnumServiceType {
    TYPE_SJSD(1, "即时用车"),
    TYPE_YYYC(2, "预约用车"),
    TYPE_JIEJI(3, "接机"),
    TYPE_DRJC(4,"代人叫车"),// 统计营业额用  add by xiao
    TYPE_SONGJI(5, "送机"),
    TYPE_RIZU(6, "日租"),
    TYPE_BANRIZU(7, "半日租"),
    TYPE_MULTYORDER(10, "多日预约订单"),
    TYPE_SZHK(15,"深港通"),
    TYPE_HKSZ(16,"港深通"),
    TYPE_ZHAM(17,"珠澳通"),
    TYPE_AMZH(18,"澳珠通"),
    TYPE_ZIJIA(20,"短租自驾"),
    TYPE_INTERNATIONAL_JIEJI(25, "国际接机"),
    TYPE_INTERNATIONAL_SONGJI(26, "国际送机"),
    TYPE_INTERNATIONAL_RIZU(27, "国际包车游"),
    TYPE_BUS_MEETING_FLIGHT(30, "巴士接机"),
    TYPE_BUS_PICKUP_FLIGHT(31, "巴士送机"),
    TYPE_BUS_RIZU(32, "巴士日租"),
    TYPE_BUS_BANRIZU(33, "巴士半日租"),
    TYPE_BUS_SPECIAL(34, "巴士特色路线"),
    TYPE_TAXI_JISHI(35, "出租车即时"),
    TYPE_TAXI_YUYUE(36, "出租车预约"),
    TYPE_BARRIER_FREE(37, "无障碍"),
    TYPE_COMMON_DAIJIA(40, "日常代驾"),
    TYPE_YUYUE_DAIJIA(41, "预约代驾"),
    TYPE_ENTERPRISE_RIZU(42, "企业日租"),
    TYPE_ENTERPRISE_BANRIZU(43, "企业半日租"),
    TYPE_ENTERPRISE_JIEJI(44, "企业接机"),
    TYPE_ENTERPRISE_SONGJI(45, "企业送机"),
    TYPE_ZHOUBIAN_DANCHENG(46, "周边游-单程"),
    TYPE_ZHOUBIAN_WANGFAN(47, "周边游-往返"),
    TYPE_BUS_DUORIZU(50, "巴士多日租");

    /**
     * 根据value获取内容
     * @param value
     * @return
     */
    public static String getI18nByValue(int value) {
        for (EnumServiceType enumServiceType : EnumServiceType.values()) {
            if (value == enumServiceType.getValue()) {
                return enumServiceType.getI18n();
            }
        }
        return null;
    }

    public int value;
    private String i18n;

    EnumServiceType(int value, String i18n) {
        this.value = value;
        this.i18n = i18n;
    }

    public String getI18n() {
        return this.i18n;
    }
    public int getValue() {
        return this.value;
    }

}
