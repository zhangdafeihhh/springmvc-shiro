package com.zhuanche.entity.driver;

import java.util.Date;

public class CustomerAppraisal {
    private Integer id;

    private Integer orderId;

    private String orderNo;

    private Integer bookingCustomerId;

    private String bookingCustomerPhone;

    private Integer customerLv;

    private String riderPhone;

    private Integer driverId;

    private Integer carId;

    private String evaluateScore;

    private String evaluate;

    private Date createAt;

    private Date updateAt;

    private Boolean appraisalStatus;

    private String remarks;

    private String instrumentAndService;

    private String environmentAndEquipped;

    private String efficiencyAndSafety;

    private String memo;

    private Integer createBy;

    private Integer updateBy;

    private String platformEvaluate;

    private String platformEvaluateScore;

    private String platformMemo;

    private String customerIp;

    private String point;

    private String pointbd;

    private String driverPhone;

    private String driverName;

    private String licensePlates;

    private String bookingCustomerName;

    private String riderName;

    private Byte type;

    private Integer orderType;

    private Integer serviceTypeId;

    private String serviceTypeName;

    private Integer cityId;

    private String cityName;

    private Date orderCreateTime;

    private Date orderFinishTime;

    private Integer carGroupId;

    private String carGroupName;

    private Integer businessId;

    private String businessName;

    private Integer supplierId;

    private String supplierName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getBookingCustomerId() {
        return bookingCustomerId;
    }

    public void setBookingCustomerId(Integer bookingCustomerId) {
        this.bookingCustomerId = bookingCustomerId;
    }

    public String getBookingCustomerPhone() {
        return bookingCustomerPhone;
    }

    public void setBookingCustomerPhone(String bookingCustomerPhone) {
        this.bookingCustomerPhone = bookingCustomerPhone == null ? null : bookingCustomerPhone.trim();
    }

    public Integer getCustomerLv() {
        return customerLv;
    }

    public void setCustomerLv(Integer customerLv) {
        this.customerLv = customerLv;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone == null ? null : riderPhone.trim();
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Boolean getAppraisalStatus() {
        return appraisalStatus;
    }

    public void setAppraisalStatus(Boolean appraisalStatus) {
        this.appraisalStatus = appraisalStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getInstrumentAndService() {
        return instrumentAndService;
    }

    public void setInstrumentAndService(String instrumentAndService) {
        this.instrumentAndService = instrumentAndService == null ? null : instrumentAndService.trim();
    }

    public String getEnvironmentAndEquipped() {
        return environmentAndEquipped;
    }

    public void setEnvironmentAndEquipped(String environmentAndEquipped) {
        this.environmentAndEquipped = environmentAndEquipped == null ? null : environmentAndEquipped.trim();
    }

    public String getEfficiencyAndSafety() {
        return efficiencyAndSafety;
    }

    public void setEfficiencyAndSafety(String efficiencyAndSafety) {
        this.efficiencyAndSafety = efficiencyAndSafety == null ? null : efficiencyAndSafety.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
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

    public String getPlatformEvaluate() {
        return platformEvaluate;
    }

    public void setPlatformEvaluate(String platformEvaluate) {
        this.platformEvaluate = platformEvaluate == null ? null : platformEvaluate.trim();
    }

    public String getPlatformEvaluateScore() {
        return platformEvaluateScore;
    }

    public void setPlatformEvaluateScore(String platformEvaluateScore) {
        this.platformEvaluateScore = platformEvaluateScore == null ? null : platformEvaluateScore.trim();
    }

    public String getPlatformMemo() {
        return platformMemo;
    }

    public void setPlatformMemo(String platformMemo) {
        this.platformMemo = platformMemo == null ? null : platformMemo.trim();
    }

    public String getCustomerIp() {
        return customerIp;
    }

    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp == null ? null : customerIp.trim();
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point == null ? null : point.trim();
    }

    public String getPointbd() {
        return pointbd;
    }

    public void setPointbd(String pointbd) {
        this.pointbd = pointbd == null ? null : pointbd.trim();
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

    public String getBookingCustomerName() {
        return bookingCustomerName;
    }

    public void setBookingCustomerName(String bookingCustomerName) {
        this.bookingCustomerName = bookingCustomerName == null ? null : bookingCustomerName.trim();
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName == null ? null : riderName.trim();
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName == null ? null : serviceTypeName.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public Date getOrderFinishTime() {
        return orderFinishTime;
    }

    public void setOrderFinishTime(Date orderFinishTime) {
        this.orderFinishTime = orderFinishTime;
    }

    public Integer getCarGroupId() {
        return carGroupId;
    }

    public void setCarGroupId(Integer carGroupId) {
        this.carGroupId = carGroupId;
    }

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName == null ? null : carGroupName.trim();
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName == null ? null : businessName.trim();
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
}