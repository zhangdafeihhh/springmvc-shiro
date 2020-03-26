package com.zhuanche.entity.bigdata;

import java.util.Date;

public class DisinfectRecord {
    private Integer id;

    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private Integer stationId;

    private String stationName;

    private Integer siteStaffId;

    private String siteStaffName;

    private String siteStaffPhone;

    private Integer status;

    private Boolean type;

    private Integer cityId;

    private String cityName;

    private Integer stationType;

    private String imgUrl;

    private Date createTime;

    private Date updateTime;

    private Integer disinfectMethod;

    private Integer auditStatus;

    private String stationAddr;

    private String licensePlates;

    private Integer grantMask;

    private Integer grantMaskNumber;

    private Integer operateDay;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName == null ? null : stationName.trim();
    }

    public Integer getSiteStaffId() {
        return siteStaffId;
    }

    public void setSiteStaffId(Integer siteStaffId) {
        this.siteStaffId = siteStaffId;
    }

    public String getSiteStaffName() {
        return siteStaffName;
    }

    public void setSiteStaffName(String siteStaffName) {
        this.siteStaffName = siteStaffName == null ? null : siteStaffName.trim();
    }

    public String getSiteStaffPhone() {
        return siteStaffPhone;
    }

    public void setSiteStaffPhone(String siteStaffPhone) {
        this.siteStaffPhone = siteStaffPhone == null ? null : siteStaffPhone.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
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

    public Integer getStationType() {
        return stationType;
    }

    public void setStationType(Integer stationType) {
        this.stationType = stationType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
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

    public Integer getDisinfectMethod() {
        return disinfectMethod;
    }

    public void setDisinfectMethod(Integer disinfectMethod) {
        this.disinfectMethod = disinfectMethod;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getStationAddr() {
        return stationAddr;
    }

    public void setStationAddr(String stationAddr) {
        this.stationAddr = stationAddr == null ? null : stationAddr.trim();
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public Integer getGrantMask() {
        return grantMask;
    }

    public void setGrantMask(Integer grantMask) {
        this.grantMask = grantMask;
    }

    public Integer getGrantMaskNumber() {
        return grantMaskNumber;
    }

    public void setGrantMaskNumber(Integer grantMaskNumber) {
        this.grantMaskNumber = grantMaskNumber;
    }

    public Integer getOperateDay() {
        return operateDay;
    }

    public void setOperateDay(Integer operateDay) {
        this.operateDay = operateDay;
    }
}