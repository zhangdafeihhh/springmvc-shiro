package com.zhuanche.dto.rentcar;

import java.util.Date;

public class DriverOutageAllDTO {
    private Integer id;

    private Integer driverId;

    private Date outStartDate;

    private Date outEndDate;

    private Integer outageSource;

    private String outageReason;

    private Integer createBy;

    private String createName;

    private Date createDate;

    private Integer removeBy;

    private String removeName;

    private Date removeDate;

    private String removeReason;

    private Integer removeStatus;

    private Date updateDate;

    /** 页面显示用 **/
    private String driverName;
    private String driverPhone;
    private String licensePlates;
    private Integer cityId;
    private String cityName;
    private Integer supplierId;
    private String supplierName;
    private Integer carGroupId;//车组
    private String carGroupName;

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
        this.cityName = cityName;
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
        this.supplierName = supplierName;
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
        this.carGroupName = carGroupName;
    }

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

    public Date getOutStartDate() {
        return outStartDate;
    }

    public void setOutStartDate(Date outStartDate) {
        this.outStartDate = outStartDate;
    }

    public Date getOutEndDate() {
        return outEndDate;
    }

    public void setOutEndDate(Date outEndDate) {
        this.outEndDate = outEndDate;
    }

    public Integer getOutageSource() {
        return outageSource;
    }

    public void setOutageSource(Integer outageSource) {
        this.outageSource = outageSource;
    }

    public String getOutageReason() {
        return outageReason;
    }

    public void setOutageReason(String outageReason) {
        this.outageReason = outageReason == null ? null : outageReason.trim();
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getRemoveBy() {
        return removeBy;
    }

    public void setRemoveBy(Integer removeBy) {
        this.removeBy = removeBy;
    }

    public String getRemoveName() {
        return removeName;
    }

    public void setRemoveName(String removeName) {
        this.removeName = removeName == null ? null : removeName.trim();
    }

    public Date getRemoveDate() {
        return removeDate;
    }

    public void setRemoveDate(Date removeDate) {
        this.removeDate = removeDate;
    }

    public String getRemoveReason() {
        return removeReason;
    }

    public void setRemoveReason(String removeReason) {
        this.removeReason = removeReason == null ? null : removeReason.trim();
    }

    public Integer getRemoveStatus() {
        return removeStatus;
    }

    public void setRemoveStatus(Integer removeStatus) {
        this.removeStatus = removeStatus;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}