package com.zhuanche.entity.bigdata;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class BiDriverBusinessInfoDayReport {
    private Integer id;

    private String dataDate;

    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private Integer cooperationType;

    private String licensePlates;

    private Integer cityId;

    private Integer supplierId;

    private Integer driverTeamId;

    private Integer driverGroupId;

    private Integer carGroupId;

    private String upOnlineTime;

    private String downOnlineTime;

    private BigDecimal allTime;

    private BigDecimal businessVolume;

    private Integer finOrdCnt;

    private Integer badCnt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate == null ? null : dataDate.trim();
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

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
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

    public Integer getDriverTeamId() {
        return driverTeamId;
    }

    public void setDriverTeamId(Integer driverTeamId) {
        this.driverTeamId = driverTeamId;
    }

    public Integer getDriverGroupId() {
        return driverGroupId;
    }

    public void setDriverGroupId(Integer driverGroupId) {
        this.driverGroupId = driverGroupId;
    }

    public Integer getCarGroupId() {
        return carGroupId;
    }

    public void setCarGroupId(Integer carGroupId) {
        this.carGroupId = carGroupId;
    }

    public String getUpOnlineTime() {
        return upOnlineTime;
    }

    public void setUpOnlineTime(String upOnlineTime) {
        this.upOnlineTime = StringUtils.isEmpty(upOnlineTime) ? null :
                upOnlineTime.indexOf(".") > 0 ? upOnlineTime.trim().substring(0,upOnlineTime.indexOf(".")) : upOnlineTime.trim();
    }

    public String getDownOnlineTime() {
        return downOnlineTime;
    }

    public void setDownOnlineTime(String downOnlineTime) {
        this.downOnlineTime = StringUtils.isEmpty(downOnlineTime) ? null :
                downOnlineTime.indexOf(".") > 0 ? downOnlineTime.trim().substring(0,downOnlineTime.indexOf(".")) : downOnlineTime.trim();
    }

    public BigDecimal getAllTime() {
        return allTime;
    }

    public void setAllTime(BigDecimal allTime) {
        this.allTime = allTime;
    }

    public BigDecimal getBusinessVolume() {
        return businessVolume;
    }

    public void setBusinessVolume(BigDecimal businessVolume) {
        this.businessVolume = businessVolume;
    }

    public Integer getFinOrdCnt() {
        return finOrdCnt;
    }

    public void setFinOrdCnt(Integer finOrdCnt) {
        this.finOrdCnt = finOrdCnt;
    }

    public Integer getBadCnt() {
        return badCnt;
    }

    public void setBadCnt(Integer badCnt) {
        this.badCnt = badCnt;
    }
}