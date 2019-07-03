package com.zhuanche.entity.mdbcarmanage;

import java.math.BigDecimal;
import java.util.Date;

public class DriverApplyAdvanceAudit {
    private Integer id;

    private String orderNum;

    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private String plateNum;

    private Integer cityId;

    private String cityName;

    private Integer cooperationType;

    private String cooperationName;

    private Integer orderStatus;

    private Integer riskStatus;

    private BigDecimal advancePaymentMoney;

    private Integer advancePaymentStatus;

    private Date serviceFinishDate;

    private String orderStartAddress;

    private String orderEndAddress;

    private Date createDate;

    private Date updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum == null ? null : orderNum.trim();
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum == null ? null : plateNum.trim();
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

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getCooperationName() {
        return cooperationName;
    }

    public void setCooperationName(String cooperationName) {
        this.cooperationName = cooperationName == null ? null : cooperationName.trim();
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(Integer riskStatus) {
        this.riskStatus = riskStatus;
    }

    public BigDecimal getAdvancePaymentMoney() {
        return advancePaymentMoney;
    }

    public void setAdvancePaymentMoney(BigDecimal advancePaymentMoney) {
        this.advancePaymentMoney = advancePaymentMoney;
    }

    public Integer getAdvancePaymentStatus() {
        return advancePaymentStatus;
    }

    public void setAdvancePaymentStatus(Integer advancePaymentStatus) {
        this.advancePaymentStatus = advancePaymentStatus;
    }

    public Date getServiceFinishDate() {
        return serviceFinishDate;
    }

    public void setServiceFinishDate(Date serviceFinishDate) {
        this.serviceFinishDate = serviceFinishDate;
    }

    public String getOrderStartAddress() {
        return orderStartAddress;
    }

    public void setOrderStartAddress(String orderStartAddress) {
        this.orderStartAddress = orderStartAddress == null ? null : orderStartAddress.trim();
    }

    public String getOrderEndAddress() {
        return orderEndAddress;
    }

    public void setOrderEndAddress(String orderEndAddress) {
        this.orderEndAddress = orderEndAddress == null ? null : orderEndAddress.trim();
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