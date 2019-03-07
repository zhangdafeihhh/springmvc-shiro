package com.zhuanche.entity.rentcar;

import lombok.Data;

import java.util.List;

@Data
public class CarBizSupplierVo extends CarBizSupplier {
    private String supplierCityName;
    private String dispatcherPhone;
    private String cooperationName;
    private Integer isTwoShifts;
    private String email;
    private String supplierShortName;
    //分佣协议
    private List<GroupInfo> groupList;
    private String createName;
    private String updateName;
    //二级加盟类型字段 二级加盟类型id 二级加盟类型名称
    private Integer twoLevelCooperation;
    private String twoLevelCooperationName;
    //分佣结算化字段
    private Integer settlementType;
    private Integer settlementCycle;
    private Integer settlementDay;
    private String settlementAccount;
    private String bankAccount;
    private String bankName;
    private String bankIdentify;
    private String settlementFullName;
}
