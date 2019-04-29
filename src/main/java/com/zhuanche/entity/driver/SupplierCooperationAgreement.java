package com.zhuanche.entity.driver;

import java.util.Date;

public class SupplierCooperationAgreement {
    private Long id;

    private Integer supplierId;

    private String agreementNumber;

    private Date agreementStartTime;

    private Date agreementEndTime;

    private Double marginAmount;

    private String marginAccount;

    private Integer createBy;

    private String createName;

    private Date createDate;

    private Date updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber == null ? null : agreementNumber.trim();
    }

    public Date getAgreementStartTime() {
        return agreementStartTime;
    }

    public void setAgreementStartTime(Date agreementStartTime) {
        this.agreementStartTime = agreementStartTime;
    }

    public Date getAgreementEndTime() {
        return agreementEndTime;
    }

    public void setAgreementEndTime(Date agreementEndTime) {
        this.agreementEndTime = agreementEndTime;
    }

    public Double getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(Double marginAmount) {
        this.marginAmount = marginAmount;
    }

    public String getMarginAccount() {
        return marginAccount;
    }

    public void setMarginAccount(String marginAccount) {
        this.marginAccount = marginAccount == null ? null : marginAccount.trim();
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}