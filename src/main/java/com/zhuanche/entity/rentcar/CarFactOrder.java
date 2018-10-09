package com.zhuanche.entity.rentcar;

import java.math.BigDecimal;
import java.util.Date;

public class CarFactOrder {
    private Integer orderId;

    private String orderNo;

    private Integer type;

    private Integer pushDriverType;

    private Integer orderType;

    private Integer bookingUserId;

    private Date bookingDate;

    private String riderName;

    private String riderPhone;

    private String bookingStartAddr;

    private String bookingStartPoint;

    private String bookingEndAddr;

    private String bookingEndPoint;

    private String factStartAddr;

    private String factStartPoint;

    private String factEndAddr;

    private String factEndPoint;

    private Integer cityId;

    private Integer serviceTypeId;

    private Integer carGroupId;

    private Integer driverId;

    private String licensePlates;

    private String airlineNo;

    private String airlineStatus;

    private Date airlinePlanDate;

    private Date airlineArrDate;

    private String airlineDepCode;

    private String airlineArrCode;

    private String channelsNum;

    private Integer receiveSms;

    private BigDecimal estimatedAmount;

    private Integer airportId;

    private Integer status;

    private Integer createBy;

    private Integer updateBy;

    private Date createDate;

    private Date updateDate;

    private Date factDate;

    private String bookingGroupids;

    private Integer factDriverId;

    private Date factEndDate;

    private String imei;

    private String version;

    private String mobelVersion;

    private String sysVersion;

    private String platform;

    private Integer payFlag;

    private BigDecimal cancelorderPenalty;

    private Integer charteredId;

    private String charteredOrderNo;

    private Integer isOrderOthers;

    private Integer doormanPayMethod;

    private Integer selectedPayFlag;

    private Integer businessId;

    private Integer goHomeStatus;

    private Integer autoLevelUp;

    private Integer bookingDriverId;

    private Integer isOtherDrivers;

    private String bookingCurrentAddr;

    private String bookingCurrentPoint;

    private String bookingUserPhone;

    private Integer buyoutFlag;

    private BigDecimal buyoutPrice;

    private String bookingIdNumber;

    private String estimatedId;

    private Integer agentId;

    private String memo;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPushDriverType() {
        return pushDriverType;
    }

    public void setPushDriverType(Integer pushDriverType) {
        this.pushDriverType = pushDriverType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Integer getBookingUserId() {
        return bookingUserId;
    }

    public void setBookingUserId(Integer bookingUserId) {
        this.bookingUserId = bookingUserId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName == null ? null : riderName.trim();
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone == null ? null : riderPhone.trim();
    }

    public String getBookingStartAddr() {
        return bookingStartAddr;
    }

    public void setBookingStartAddr(String bookingStartAddr) {
        this.bookingStartAddr = bookingStartAddr == null ? null : bookingStartAddr.trim();
    }

    public String getBookingStartPoint() {
        return bookingStartPoint;
    }

    public void setBookingStartPoint(String bookingStartPoint) {
        this.bookingStartPoint = bookingStartPoint == null ? null : bookingStartPoint.trim();
    }

    public String getBookingEndAddr() {
        return bookingEndAddr;
    }

    public void setBookingEndAddr(String bookingEndAddr) {
        this.bookingEndAddr = bookingEndAddr == null ? null : bookingEndAddr.trim();
    }

    public String getBookingEndPoint() {
        return bookingEndPoint;
    }

    public void setBookingEndPoint(String bookingEndPoint) {
        this.bookingEndPoint = bookingEndPoint == null ? null : bookingEndPoint.trim();
    }

    public String getFactStartAddr() {
        return factStartAddr;
    }

    public void setFactStartAddr(String factStartAddr) {
        this.factStartAddr = factStartAddr == null ? null : factStartAddr.trim();
    }

    public String getFactStartPoint() {
        return factStartPoint;
    }

    public void setFactStartPoint(String factStartPoint) {
        this.factStartPoint = factStartPoint == null ? null : factStartPoint.trim();
    }

    public String getFactEndAddr() {
        return factEndAddr;
    }

    public void setFactEndAddr(String factEndAddr) {
        this.factEndAddr = factEndAddr == null ? null : factEndAddr.trim();
    }

    public String getFactEndPoint() {
        return factEndPoint;
    }

    public void setFactEndPoint(String factEndPoint) {
        this.factEndPoint = factEndPoint == null ? null : factEndPoint.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Integer getCarGroupId() {
        return carGroupId;
    }

    public void setCarGroupId(Integer carGroupId) {
        this.carGroupId = carGroupId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public String getAirlineNo() {
        return airlineNo;
    }

    public void setAirlineNo(String airlineNo) {
        this.airlineNo = airlineNo == null ? null : airlineNo.trim();
    }

    public String getAirlineStatus() {
        return airlineStatus;
    }

    public void setAirlineStatus(String airlineStatus) {
        this.airlineStatus = airlineStatus == null ? null : airlineStatus.trim();
    }

    public Date getAirlinePlanDate() {
        return airlinePlanDate;
    }

    public void setAirlinePlanDate(Date airlinePlanDate) {
        this.airlinePlanDate = airlinePlanDate;
    }

    public Date getAirlineArrDate() {
        return airlineArrDate;
    }

    public void setAirlineArrDate(Date airlineArrDate) {
        this.airlineArrDate = airlineArrDate;
    }

    public String getAirlineDepCode() {
        return airlineDepCode;
    }

    public void setAirlineDepCode(String airlineDepCode) {
        this.airlineDepCode = airlineDepCode == null ? null : airlineDepCode.trim();
    }

    public String getAirlineArrCode() {
        return airlineArrCode;
    }

    public void setAirlineArrCode(String airlineArrCode) {
        this.airlineArrCode = airlineArrCode == null ? null : airlineArrCode.trim();
    }

    public String getChannelsNum() {
        return channelsNum;
    }

    public void setChannelsNum(String channelsNum) {
        this.channelsNum = channelsNum == null ? null : channelsNum.trim();
    }

    public Integer getReceiveSms() {
        return receiveSms;
    }

    public void setReceiveSms(Integer receiveSms) {
        this.receiveSms = receiveSms;
    }

    public BigDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(BigDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public Integer getAirportId() {
        return airportId;
    }

    public void setAirportId(Integer airportId) {
        this.airportId = airportId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getFactDate() {
        return factDate;
    }

    public void setFactDate(Date factDate) {
        this.factDate = factDate;
    }

    public String getBookingGroupids() {
        return bookingGroupids;
    }

    public void setBookingGroupids(String bookingGroupids) {
        this.bookingGroupids = bookingGroupids == null ? null : bookingGroupids.trim();
    }

    public Integer getFactDriverId() {
        return factDriverId;
    }

    public void setFactDriverId(Integer factDriverId) {
        this.factDriverId = factDriverId;
    }

    public Date getFactEndDate() {
        return factEndDate;
    }

    public void setFactEndDate(Date factEndDate) {
        this.factEndDate = factEndDate;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getMobelVersion() {
        return mobelVersion;
    }

    public void setMobelVersion(String mobelVersion) {
        this.mobelVersion = mobelVersion == null ? null : mobelVersion.trim();
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion == null ? null : sysVersion.trim();
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform == null ? null : platform.trim();
    }

    public Integer getPayFlag() {
        return payFlag;
    }

    public void setPayFlag(Integer payFlag) {
        this.payFlag = payFlag;
    }

    public BigDecimal getCancelorderPenalty() {
        return cancelorderPenalty;
    }

    public void setCancelorderPenalty(BigDecimal cancelorderPenalty) {
        this.cancelorderPenalty = cancelorderPenalty;
    }

    public Integer getCharteredId() {
        return charteredId;
    }

    public void setCharteredId(Integer charteredId) {
        this.charteredId = charteredId;
    }

    public String getCharteredOrderNo() {
        return charteredOrderNo;
    }

    public void setCharteredOrderNo(String charteredOrderNo) {
        this.charteredOrderNo = charteredOrderNo == null ? null : charteredOrderNo.trim();
    }

    public Integer getIsOrderOthers() {
        return isOrderOthers;
    }

    public void setIsOrderOthers(Integer isOrderOthers) {
        this.isOrderOthers = isOrderOthers;
    }

    public Integer getDoormanPayMethod() {
        return doormanPayMethod;
    }

    public void setDoormanPayMethod(Integer doormanPayMethod) {
        this.doormanPayMethod = doormanPayMethod;
    }

    public Integer getSelectedPayFlag() {
        return selectedPayFlag;
    }

    public void setSelectedPayFlag(Integer selectedPayFlag) {
        this.selectedPayFlag = selectedPayFlag;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getGoHomeStatus() {
        return goHomeStatus;
    }

    public void setGoHomeStatus(Integer goHomeStatus) {
        this.goHomeStatus = goHomeStatus;
    }

    public Integer getAutoLevelUp() {
        return autoLevelUp;
    }

    public void setAutoLevelUp(Integer autoLevelUp) {
        this.autoLevelUp = autoLevelUp;
    }

    public Integer getBookingDriverId() {
        return bookingDriverId;
    }

    public void setBookingDriverId(Integer bookingDriverId) {
        this.bookingDriverId = bookingDriverId;
    }

    public Integer getIsOtherDrivers() {
        return isOtherDrivers;
    }

    public void setIsOtherDrivers(Integer isOtherDrivers) {
        this.isOtherDrivers = isOtherDrivers;
    }

    public String getBookingCurrentAddr() {
        return bookingCurrentAddr;
    }

    public void setBookingCurrentAddr(String bookingCurrentAddr) {
        this.bookingCurrentAddr = bookingCurrentAddr == null ? null : bookingCurrentAddr.trim();
    }

    public String getBookingCurrentPoint() {
        return bookingCurrentPoint;
    }

    public void setBookingCurrentPoint(String bookingCurrentPoint) {
        this.bookingCurrentPoint = bookingCurrentPoint == null ? null : bookingCurrentPoint.trim();
    }

    public String getBookingUserPhone() {
        return bookingUserPhone;
    }

    public void setBookingUserPhone(String bookingUserPhone) {
        this.bookingUserPhone = bookingUserPhone == null ? null : bookingUserPhone.trim();
    }

    public Integer getBuyoutFlag() {
        return buyoutFlag;
    }

    public void setBuyoutFlag(Integer buyoutFlag) {
        this.buyoutFlag = buyoutFlag;
    }

    public BigDecimal getBuyoutPrice() {
        return buyoutPrice;
    }

    public void setBuyoutPrice(BigDecimal buyoutPrice) {
        this.buyoutPrice = buyoutPrice;
    }

    public String getBookingIdNumber() {
        return bookingIdNumber;
    }

    public void setBookingIdNumber(String bookingIdNumber) {
        this.bookingIdNumber = bookingIdNumber == null ? null : bookingIdNumber.trim();
    }

    public String getEstimatedId() {
        return estimatedId;
    }

    public void setEstimatedId(String estimatedId) {
        this.estimatedId = estimatedId == null ? null : estimatedId.trim();
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }
}