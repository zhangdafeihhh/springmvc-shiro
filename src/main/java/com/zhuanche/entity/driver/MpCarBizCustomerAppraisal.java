package com.zhuanche.entity.driver;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class MpCarBizCustomerAppraisal implements Serializable{

    private static final long serialVersionUID = 3607995917594876584L;

    private Integer appraisalId;

    private Integer orderId;

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

    private String driverName;

    private String driverPhone;

    private String licensePlates;

    /**
     * 评论是否有效 0:有效 1:无效
     */
    private Integer appraisalStatus;

    /**
     * 备用字段
     */
    private String remarks;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date orderFinishTime;

    /**
     * 申诉表主键
     */
    private Integer appealId;
    /**
     * 申诉时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date appealTime;
    /**
     * 申诉状态：0:未申诉   1:已申诉（待审核） 2:申诉成功  3:申诉失败 4：撤销申诉
     */
    private Integer appealStatus;
    /**
     * 是否允许申诉
     */
    private Integer isAllowedAppeal;

    private Integer isAlreadyAppeal;

    /**
     * 是否已回访 0 未回访 1 已回访
     */
    private Integer callbackStatus;

    public Integer getIsAlreadyAppeal() {
        return isAlreadyAppeal;
    }

    public void setIsAlreadyAppeal(Integer isAlreadyAppeal) {
        this.isAlreadyAppeal = isAlreadyAppeal;
    }

    public Integer getAppraisalId() {
        return appraisalId;
    }

    public void setAppraisalId(Integer appraisalId) {
        this.appraisalId = appraisalId;
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

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
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

    public Date getOrderFinishTime() {
        return orderFinishTime;
    }

    public void setOrderFinishTime(Date orderFinishTime) {
        this.orderFinishTime = orderFinishTime;
    }

    public Integer getAppealId() {
        return appealId;
    }

    public void setAppealId(Integer appealId) {
        this.appealId = appealId;
    }

    public Date getAppealTime() {
        return appealTime;
    }

    public void setAppealTime(Date appealTime) {
        this.appealTime = appealTime;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public Integer getIsAllowedAppeal() {
        return isAllowedAppeal;
    }

    public void setIsAllowedAppeal(Integer isAllowedAppeal) {
        this.isAllowedAppeal = isAllowedAppeal;
    }

    public Integer getCallbackStatus() {
        return callbackStatus;
    }

    public void setCallbackStatus(Integer callbackStatus) {
        this.callbackStatus = callbackStatus;
    }
}