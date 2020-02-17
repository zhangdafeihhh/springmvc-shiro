package com.zhuanche.dto.rentcar;

import com.zhuanche.entity.common.Base;

import java.util.Date;

public class CarBizCustomerAppraisalDTO extends Base {

    private static final long serialVersionUID = 1296427662939958799L;

    private Integer appraisalId;

    private Long orderId;

    private String orderNo;

    private Integer bookingCustomerId;

    private String bookingCustomerPhone;

    private String riderPhone;

    private Integer driverId;

    private Integer carId;

    private String instrumentAndService;

    private String environmentAndEquipped;

    private String efficiencyAndSafety;

    private String memo;

    private Integer createBy;

    private Date createDate;

    private Integer updateBy;

    private Date updateDate;

    private String evaluateScore;

    private String evaluate;

    private String platformEvaluate;

    private String platformEvaluateScore;

    private String platformMemo;

    private String customerIp;

    private String point;

    private String pointbd;

    /**
     * 评论是否有效 0:有效 1:无效
     */
    private Integer appraisalStatus;

    /**
     * 备用字段
     */
    private String remarks;

    //查询条件
    private Integer cityId;
    private Integer supplierId;
    private String name;
    private String phone;
    private String licensePlates;
    private String createDateBegin;
    private String createDateEnd;

    private String driverName;
    private String driverPhone;


    public Integer getAppraisalId() {
        return appraisalId;
    }

    public void setAppraisalId(Integer appraisalId) {
        this.appraisalId = appraisalId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getCreateDateBegin() {
        return createDateBegin;
    }

    public void setCreateDateBegin(String createDateBegin) {
        this.createDateBegin = createDateBegin;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public Integer getAppraisalStatus() {
        return appraisalStatus;
    }

    public void setAppraisalStatus(Integer appraisalStatus) {
        this.appraisalStatus = appraisalStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }
}