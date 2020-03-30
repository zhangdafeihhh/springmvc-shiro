package com.zhuanche.entity.bigdata;

import lombok.Data;

import java.util.List;

@Data
public class DriverOperAnalyIndexList<T> {

    private List<T> onlineDriverAmount; // 上线司机数
    private List<T> onlineRate; // 上线率
    private List<T> serviceDriverAmount; // 运营司机数
    private List<T> serviceRate; // 运营率
    private List<T> distributeOrderAmount; // 派单量
    private List<T> completeOrderAmount; // 完单率
    private List<T> bindOrderAmountPerVehicle; // 车均绑单量
    private List<T> completeOrderPerVehicle; // 车均完单量
    private List<T> incomePerVehicle; // 车均流水
    private List<T> pricePerOIrder; // 单均价
    private List<T> totalDriverNum; // 司机总数
    private List<T> disinfectDriverCnt;// 已消毒司机数
    private List<T> noDisinfectDriverCnt;// 未消毒司机数
    private List<T> disinfectDriverCntRate;// 消毒率
}
