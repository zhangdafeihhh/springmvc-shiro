package com.zhuanche.entity.rentcar;

import com.zhuanche.dto.driver.supplier.SupplierCooperationAgreementDTO;
import com.zhuanche.entity.driver.SupplierExperience;
import lombok.Data;

import java.util.Date;
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
    //分佣结算化字段
    private Integer settlementType;
    private Integer settlementCycle;
    private Integer settlementDay;
    private String settlementAccount;
    private String bankAccount;
    private String bankName;
    private String bankIdentify;
    private String settlementFullName;
    /**
     * 首次签约时间
     */
    private String firstSignTime;

    /**
     * 税率 3%、6%、9%、10%、13%、16%
     */
    private String taxRate;

    /**
     * 发票类型 0.无效发票类型 1.专票、2.普票、3.电子发票（普票）
     */
    private Integer invoiceType;

    /**
     * 协议开始时间
     */
    private String agreementStartTime;

    /**
     * 协议到期时间
     */
    private String agreementEndTime;

    /**
     * 保证金金额
     */
    private Double marginAmount;

    /**
     * 申请时间
     */
    private String applyCreateDate;

    /**
     * 申请状态 1申请中 2已更新
     */
    private Byte applyStatus;

    private List<SupplierCooperationAgreementDTO> agreementList;

    private List<SupplierExperience> experienceList;

    private String agreementListStr;

    private String experienceListStr;

}
