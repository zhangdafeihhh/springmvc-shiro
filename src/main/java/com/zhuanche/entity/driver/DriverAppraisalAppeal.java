package com.zhuanche.entity.driver;

import java.util.Date;

public class DriverAppraisalAppeal {
    private Integer id;

    private Integer appraisalId;

    private Integer appraisalStatus;

    private String orderNo;

    private Integer orderId;

    private String bookingCustomerName;

    private String bookingCustomerPhone;

    private Integer bookingCustomerId;

    private String riderPhone;

    private String riderName;

    private String evaluateScore;

    private String evaluate;

    private String memo;

    private Integer driverId;

    private String driverPhone;

    private String driverName;

    private String licensePlates;

    private String cityName;

    private Integer cityId;

    private Integer supplierId;

    private String supplierName;

    private Integer appealStatus;

    private String appealContent;

    private String fileName;

    private Date auditTime;

    private String auditRemark;

    private Date createTime;

    private Date updateTime;

    private Integer createBy;

    private Integer updateBy;

    private String auditName;

    private Integer auditBy;

    private String createName;

    private String updateName;

    private Integer callbackStatus;

    private String url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppraisalId() {
        return appraisalId;
    }

    public void setAppraisalId(Integer appraisalId) {
        this.appraisalId = appraisalId;
    }

    public Integer getAppraisalStatus() {
        return appraisalStatus;
    }

    public void setAppraisalStatus(Integer appraisalStatus) {
        this.appraisalStatus = appraisalStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBookingCustomerName() {
        return bookingCustomerName;
    }

    public void setBookingCustomerName(String bookingCustomerName) {
        this.bookingCustomerName = bookingCustomerName == null ? null : bookingCustomerName.trim();
    }

    public String getBookingCustomerPhone() {
        return bookingCustomerPhone;
    }

    public void setBookingCustomerPhone(String bookingCustomerPhone) {
        this.bookingCustomerPhone = bookingCustomerPhone == null ? null : bookingCustomerPhone.trim();
    }

    public Integer getBookingCustomerId() {
        return bookingCustomerId;
    }

    public void setBookingCustomerId(Integer bookingCustomerId) {
        this.bookingCustomerId = bookingCustomerId;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone == null ? null : riderPhone.trim();
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName == null ? null : riderName.trim();
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {
        this.evaluateScore = evaluateScore == null ? null : evaluateScore.trim();
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate == null ? null : evaluate.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public String getAppealContent() {
        return appealContent;
    }

    public void setAppealContent(String appealContent) {
        this.appealContent = appealContent == null ? null : appealContent.trim();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark == null ? null : auditRemark.trim();
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

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public String getAuditName() {
        return auditName;
    }

    public void setAuditName(String auditName) {
        this.auditName = auditName == null ? null : auditName.trim();
    }

    public Integer getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(Integer auditBy) {
        this.auditBy = auditBy;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName == null ? null : updateName.trim();
    }

    public Integer getCallbackStatus() {
        return callbackStatus;
    }

    public void setCallbackStatus(Integer callbackStatus) {
        this.callbackStatus = callbackStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }
}