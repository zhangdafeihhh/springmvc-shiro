package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class SupplierFeeManage {
    private Integer id;

    private String feeOrderNo;

    private Integer cityId;

    private String supplierName;

    private Integer supplierId;

    private Date settleStartDate;

    private Date settleEndDate;

    private String totalFlow;

    private String flowAmount;

    private String windControlAmount;

    private String extraCharge;

    private String cancelCharge;

    private String totalAmountWater;

    private String scaleEfficient;

    private String totalFlowLastMonth;

    private String flowIncrease;

    private String growthFactor;

    private String badRatings;

    private String monthCommission;

    private String excludeCommission;

    private String deductionAmountLastMonth;

    private String total;

    private String complianceDriverAward;

    private String others;

    private String badRatingsAward;

    private String amountAssessmentSum;

    private String inspectionFines;

    private String totalManageFees;

    private Integer status;

    private Integer amountStatus;

    private Date paymentTime;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String cityName;

    private Date amountStatusTime; //金额异议处理时间

    private String settleStartDateStr;

    private String settleEndDateStr;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeeOrderNo() {
        return feeOrderNo;
    }

    public void setFeeOrderNo(String feeOrderNo) {
        this.feeOrderNo = feeOrderNo == null ? null : feeOrderNo.trim();
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
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Date getSettleStartDate() {
        return settleStartDate;
    }

    public void setSettleStartDate(Date settleStartDate) {
        this.settleStartDate = settleStartDate;
    }

    public Date getSettleEndDate() {
        return settleEndDate;
    }

    public void setSettleEndDate(Date settleEndDate) {
        this.settleEndDate = settleEndDate;
    }

    public String getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(String totalFlow) {
        this.totalFlow = totalFlow == null ? null : totalFlow.trim();
    }

    public String getFlowAmount() {
        return flowAmount;
    }

    public void setFlowAmount(String flowAmount) {
        this.flowAmount = flowAmount == null ? null : flowAmount.trim();
    }

    public String getWindControlAmount() {
        return windControlAmount;
    }

    public void setWindControlAmount(String windControlAmount) {
        this.windControlAmount = windControlAmount == null ? null : windControlAmount.trim();
    }

    public String getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(String extraCharge) {
        this.extraCharge = extraCharge == null ? null : extraCharge.trim();
    }

    public String getCancelCharge() {
        return cancelCharge;
    }

    public void setCancelCharge(String cancelCharge) {
        this.cancelCharge = cancelCharge == null ? null : cancelCharge.trim();
    }

    public String getTotalAmountWater() {
        return totalAmountWater;
    }

    public void setTotalAmountWater(String totalAmountWater) {
        this.totalAmountWater = totalAmountWater == null ? null : totalAmountWater.trim();
    }

    public String getScaleEfficient() {
        return scaleEfficient;
    }

    public void setScaleEfficient(String scaleEfficient) {
        this.scaleEfficient = scaleEfficient == null ? null : scaleEfficient.trim();
    }

    public String getTotalFlowLastMonth() {
        return totalFlowLastMonth;
    }

    public void setTotalFlowLastMonth(String totalFlowLastMonth) {
        this.totalFlowLastMonth = totalFlowLastMonth == null ? null : totalFlowLastMonth.trim();
    }

    public String getFlowIncrease() {
        return flowIncrease;
    }

    public void setFlowIncrease(String flowIncrease) {
        this.flowIncrease = flowIncrease == null ? null : flowIncrease.trim();
    }

    public String getGrowthFactor() {
        return growthFactor;
    }

    public void setGrowthFactor(String growthFactor) {
        this.growthFactor = growthFactor == null ? null : growthFactor.trim();
    }

    public String getBadRatings() {
        return badRatings;
    }

    public void setBadRatings(String badRatings) {
        this.badRatings = badRatings == null ? null : badRatings.trim();
    }

    public String getMonthCommission() {
        return monthCommission;
    }

    public void setMonthCommission(String monthCommission) {
        this.monthCommission = monthCommission == null ? null : monthCommission.trim();
    }

    public String getExcludeCommission() {
        return excludeCommission;
    }

    public void setExcludeCommission(String excludeCommission) {
        this.excludeCommission = excludeCommission == null ? null : excludeCommission.trim();
    }

    public String getDeductionAmountLastMonth() {
        return deductionAmountLastMonth;
    }

    public void setDeductionAmountLastMonth(String deductionAmountLastMonth) {
        this.deductionAmountLastMonth = deductionAmountLastMonth == null ? null : deductionAmountLastMonth.trim();
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total == null ? null : total.trim();
    }

    public String getComplianceDriverAward() {
        return complianceDriverAward;
    }

    public void setComplianceDriverAward(String complianceDriverAward) {
        this.complianceDriverAward = complianceDriverAward == null ? null : complianceDriverAward.trim();
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others == null ? null : others.trim();
    }

    public String getBadRatingsAward() {
        return badRatingsAward;
    }

    public void setBadRatingsAward(String badRatingsAward) {
        this.badRatingsAward = badRatingsAward == null ? null : badRatingsAward.trim();
    }

    public String getAmountAssessmentSum() {
        return amountAssessmentSum;
    }

    public void setAmountAssessmentSum(String amountAssessmentSum) {
        this.amountAssessmentSum = amountAssessmentSum == null ? null : amountAssessmentSum.trim();
    }

    public String getInspectionFines() {
        return inspectionFines;
    }

    public void setInspectionFines(String inspectionFines) {
        this.inspectionFines = inspectionFines == null ? null : inspectionFines.trim();
    }

    public String getTotalManageFees() {
        return totalManageFees;
    }

    public void setTotalManageFees(String totalManageFees) {
        this.totalManageFees = totalManageFees == null ? null : totalManageFees.trim();
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

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Date getAmountStatusTime() {
        return amountStatusTime;
    }

    public void setAmountStatusTime(Date amountStatusTime) {
        this.amountStatusTime = amountStatusTime;
    }

    public String getSettleStartDateStr() {
        return settleStartDateStr;
    }

    public void setSettleStartDateStr(String settleStartDateStr) {
        this.settleStartDateStr = settleStartDateStr;
    }

    public String getSettleEndDateStr() {
        return settleEndDateStr;
    }

    public void setSettleEndDateStr(String settleEndDateStr) {
        this.settleEndDateStr = settleEndDateStr;
    }
}