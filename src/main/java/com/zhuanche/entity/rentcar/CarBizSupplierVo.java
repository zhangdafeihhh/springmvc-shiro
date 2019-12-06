package com.zhuanche.entity.rentcar;

import com.zhuanche.dto.driver.supplier.SupplierCooperationAgreementDTO;
import com.zhuanche.entity.driver.SupplierAccountApply;
import com.zhuanche.entity.driver.SupplierCooperationAgreement;
import com.zhuanche.entity.driver.SupplierExperience;
import lombok.Data;

import java.util.Date;
import java.util.List;

public class CarBizSupplierVo extends CarBizSupplier {
    private String supplierCityName;
    private String dispatcherPhone;
    private String cooperationName;
    private Integer isTwoShifts;//是否支持双班
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

    private List<SupplierCooperationAgreement> cooperationAgreementList;

    private List<SupplierExperience> experienceList;

    private String agreementListStr;

    private String experienceListStr;

    private String mainCityName;

    /**
     *合作模式 1 自主、2 非自主；
     */
    private Integer cooperationMode;

    /**
     * 花园计划等级 1 LV1、2 LV2、3 LV3、4 LV4、5 LV5、6 金牌；
     */
    private String gardenPlanLevel;

    /**
     * 状态 1启用 0禁用
     */
    private Integer status;


    private SupplierAccountApply applyList;


    public String getSupplierCityName() {
        return supplierCityName;
    }

    public void setSupplierCityName(String supplierCityName) {
        this.supplierCityName = supplierCityName;
    }

    public String getDispatcherPhone() {
        return dispatcherPhone;
    }

    public void setDispatcherPhone(String dispatcherPhone) {
        this.dispatcherPhone = dispatcherPhone;
    }

    public String getCooperationName() {
        return cooperationName;
    }

    public void setCooperationName(String cooperationName) {
        this.cooperationName = cooperationName;
    }

    public Integer getIsTwoShifts() {
        return isTwoShifts;
    }

    public void setIsTwoShifts(Integer isTwoShifts) {
        this.isTwoShifts = isTwoShifts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSupplierShortName() {
        return supplierShortName;
    }

    public void setSupplierShortName(String supplierShortName) {
        this.supplierShortName = supplierShortName;
    }

    public List<GroupInfo> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupInfo> groupList) {
        this.groupList = groupList;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public Integer getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }

    public Integer getSettlementCycle() {
        return settlementCycle;
    }

    public void setSettlementCycle(Integer settlementCycle) {
        this.settlementCycle = settlementCycle;
    }

    public Integer getSettlementDay() {
        return settlementDay;
    }

    public void setSettlementDay(Integer settlementDay) {
        this.settlementDay = settlementDay;
    }

    public String getSettlementAccount() {
        return settlementAccount;
    }

    public void setSettlementAccount(String settlementAccount) {
        this.settlementAccount = settlementAccount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankIdentify() {
        return bankIdentify;
    }

    public void setBankIdentify(String bankIdentify) {
        this.bankIdentify = bankIdentify;
    }

    public String getSettlementFullName() {
        return settlementFullName;
    }

    public void setSettlementFullName(String settlementFullName) {
        this.settlementFullName = settlementFullName;
    }

    public String getFirstSignTime() {
        return firstSignTime;
    }

    public void setFirstSignTime(String firstSignTime) {
        this.firstSignTime = firstSignTime;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getAgreementStartTime() {
        return agreementStartTime;
    }

    public void setAgreementStartTime(String agreementStartTime) {
        this.agreementStartTime = agreementStartTime;
    }

    public String getAgreementEndTime() {
        return agreementEndTime;
    }

    public void setAgreementEndTime(String agreementEndTime) {
        this.agreementEndTime = agreementEndTime;
    }

    public Double getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(Double marginAmount) {
        this.marginAmount = marginAmount;
    }

    public String getApplyCreateDate() {
        return applyCreateDate;
    }

    public void setApplyCreateDate(String applyCreateDate) {
        this.applyCreateDate = applyCreateDate;
    }

    public Byte getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Byte applyStatus) {
        this.applyStatus = applyStatus;
    }

    public List<SupplierCooperationAgreementDTO> getAgreementList() {
        return agreementList;
    }

    public void setAgreementList(List<SupplierCooperationAgreementDTO> agreementList) {
        this.agreementList = agreementList;
    }

    public List<SupplierExperience> getExperienceList() {
        return experienceList;
    }

    public void setExperienceList(List<SupplierExperience> experienceList) {
        this.experienceList = experienceList;
    }

    public String getAgreementListStr() {
        return agreementListStr;
    }

    public void setAgreementListStr(String agreementListStr) {
        this.agreementListStr = agreementListStr;
    }

    public String getExperienceListStr() {
        return experienceListStr;
    }

    public void setExperienceListStr(String experienceListStr) {
        this.experienceListStr = experienceListStr;
    }

    public String getMainCityName() {
        return mainCityName;
    }

    public void setMainCityName(String mainCityName) {
        this.mainCityName = mainCityName;
    }

    public Integer getCooperationMode() {
        return cooperationMode;
    }

    public void setCooperationMode(Integer cooperationMode) {
        this.cooperationMode = cooperationMode;
    }

    public String getGardenPlanLevel() {
        return gardenPlanLevel;
    }

    public void setGardenPlanLevel(String gardenPlanLevel) {
        this.gardenPlanLevel = gardenPlanLevel;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
    }

    public SupplierAccountApply getApplyList() {
        return applyList;
    }

    public void setApplyList(SupplierAccountApply applyList) {
        this.applyList = applyList;
    }

    public List<SupplierCooperationAgreement> getCooperationAgreementList() {
        return cooperationAgreementList;
    }

    public void setCooperationAgreementList(List<SupplierCooperationAgreement> cooperationAgreementList) {
        this.cooperationAgreementList = cooperationAgreementList;
    }
}
