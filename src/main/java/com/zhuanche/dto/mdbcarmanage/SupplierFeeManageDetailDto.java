package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.SupplierFeeRecord;

import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/9/16 上午10:42
 * @Version 1.0
 */
public class SupplierFeeManageDetailDto {

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

    private List<SupplierFeeRecord> supplierFeeRecordList;

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
        this.totalFlow = totalFlow;
    }

    public String getFlowAmount() {
        return flowAmount;
    }

    public void setFlowAmount(String flowAmount) {
        this.flowAmount = flowAmount;
    }

    public String getWindControlAmount() {
        return windControlAmount;
    }

    public void setWindControlAmount(String windControlAmount) {
        this.windControlAmount = windControlAmount;
    }

    public String getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(String extraCharge) {
        this.extraCharge = extraCharge;
    }

    public String getCancelCharge() {
        return cancelCharge;
    }

    public void setCancelCharge(String cancelCharge) {
        this.cancelCharge = cancelCharge;
    }

    public String getTotalAmountWater() {
        return totalAmountWater;
    }

    public void setTotalAmountWater(String totalAmountWater) {
        this.totalAmountWater = totalAmountWater;
    }

    public String getScaleEfficient() {
        return scaleEfficient;
    }

    public void setScaleEfficient(String scaleEfficient) {
        this.scaleEfficient = scaleEfficient;
    }

    public String getTotalFlowLastMonth() {
        return totalFlowLastMonth;
    }

    public void setTotalFlowLastMonth(String totalFlowLastMonth) {
        this.totalFlowLastMonth = totalFlowLastMonth;
    }

    public String getFlowIncrease() {
        return flowIncrease;
    }

    public void setFlowIncrease(String flowIncrease) {
        this.flowIncrease = flowIncrease;
    }

    public String getGrowthFactor() {
        return growthFactor;
    }

    public void setGrowthFactor(String growthFactor) {
        this.growthFactor = growthFactor;
    }

    public String getBadRatings() {
        return badRatings;
    }

    public void setBadRatings(String badRatings) {
        this.badRatings = badRatings;
    }

    public String getMonthCommission() {
        return monthCommission;
    }

    public void setMonthCommission(String monthCommission) {
        this.monthCommission = monthCommission;
    }

    public String getExcludeCommission() {
        return excludeCommission;
    }

    public void setExcludeCommission(String excludeCommission) {
        this.excludeCommission = excludeCommission;
    }

    public String getDeductionAmountLastMonth() {
        return deductionAmountLastMonth;
    }

    public void setDeductionAmountLastMonth(String deductionAmountLastMonth) {
        this.deductionAmountLastMonth = deductionAmountLastMonth;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getComplianceDriverAward() {
        return complianceDriverAward;
    }

    public void setComplianceDriverAward(String complianceDriverAward) {
        this.complianceDriverAward = complianceDriverAward;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getBadRatingsAward() {
        return badRatingsAward;
    }

    public void setBadRatingsAward(String badRatingsAward) {
        this.badRatingsAward = badRatingsAward;
    }

    public String getAmountAssessmentSum() {
        return amountAssessmentSum;
    }

    public void setAmountAssessmentSum(String amountAssessmentSum) {
        this.amountAssessmentSum = amountAssessmentSum;
    }

    public String getInspectionFines() {
        return inspectionFines;
    }

    public void setInspectionFines(String inspectionFines) {
        this.inspectionFines = inspectionFines;
    }

    public String getTotalManageFees() {
        return totalManageFees;
    }

    public void setTotalManageFees(String totalManageFees) {
        this.totalManageFees = totalManageFees;
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
        this.createUser = createUser;
    }

    public List<SupplierFeeRecord> getSupplierFeeRecordList() {
        return supplierFeeRecordList;
    }

    public void setSupplierFeeRecordList(List<SupplierFeeRecord> supplierFeeRecordList) {
        this.supplierFeeRecordList = supplierFeeRecordList;
    }
}
