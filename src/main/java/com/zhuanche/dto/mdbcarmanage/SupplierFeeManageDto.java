package com.zhuanche.dto.mdbcarmanage;

import java.util.Date;

public class SupplierFeeManageDto {

    private String feeOrderNo;

    private Integer cityId;

    private String supplierName;

    private Integer supplierId;

    private String settleStartDate;

    private String settleEndDate;

    private String paymentTime;

    private Integer status;

    private Integer amountStatus;

    public String getFeeOrderNo() {
        return feeOrderNo;
    }

    public void setFeeOrderNo(String feeOrderNo) {
        this.feeOrderNo = feeOrderNo;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSettleStartDate() {
        return settleStartDate;
    }

    public void setSettleStartDate(String settleStartDate) {
        this.settleStartDate = settleStartDate;
    }

    public String getSettleEndDate() {
        return settleEndDate;
    }

    public void setSettleEndDate(String settleEndDate) {
        this.settleEndDate = settleEndDate;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAmountStatus() {
        return amountStatus;
    }

    public void setAmountStatus(Integer amountStatus) {
        this.amountStatus = amountStatus;
    }
}