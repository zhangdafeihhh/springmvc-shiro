package com.zhuanche.dto.bigdata;

import java.math.BigDecimal;

public class BiDriverBusinessInfoMonthReportDTO {
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

    private BigDecimal allTime;

    private BigDecimal allTimeDays;

    private BigDecimal businessVolume;

    private Integer finOrdCnt;

    private Integer badCnt;

    private String cityName;

    private String supplierName;

    private String driverTeamName;

    private String driverGroupName;

    private String cooperateName;

    private String carGroupName;

    private BigDecimal travelHour;

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

    public BigDecimal getAllTime() {
        return allTime;
    }

    public void setAllTime(BigDecimal allTime) {
        this.allTime = allTime;
    }

    public BigDecimal getAllTimeDays() {
        return allTimeDays;
    }

    public void setAllTimeDays(BigDecimal allTimeDays) {
        this.allTimeDays = allTimeDays;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getDriverTeamName() {
        return driverTeamName;
    }

    public void setDriverTeamName(String driverTeamName) {
        this.driverTeamName = driverTeamName;
    }

    public String getDriverGroupName() {
        return driverGroupName;
    }

    public void setDriverGroupName(String driverGroupName) {
        this.driverGroupName = driverGroupName;
    }

    public String getCooperateName() {
        return cooperateName;
    }

    public void setCooperateName(String cooperateName) {
        this.cooperateName = cooperateName;
    }

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName;
    }

    public BigDecimal getTravelHour() {
        return travelHour;
    }

    public void setTravelHour(BigDecimal travelHour) {
        this.travelHour = travelHour;
    }
}