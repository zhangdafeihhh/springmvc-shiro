package com.zhuanche.entity.bigdata;

import lombok.Data;

@Data
public class DriverOperAnalyIndex {

    private String demenItemName; // 分组字段
    private String onlineDriverAmount; // 上线司机数
    private String onlineRate; // 上线率
    private String serviceDriverAmount; // 运营司机数
    private String serviceRate; // 运营率
    private String distributeOrderAmount; // 派单量
    private String completeOrderAmount; // 完单率
    private String bindOrderAmountPerVehicle; // 车均绑单量
    private String completeOrderPerVehicle; // 车均完单量
    private String incomePerVehicle; // 车均流水
    private String pricePerOIrder; // 单均价
    private String totalDriverNum; // 司机总数
    private String disinfectDriverCnt;// 已消毒司机数
    private String noDisinfectDriverCnt;// 未消毒司机数
    private String disinfectDriverCntRate;// 消毒率
}
