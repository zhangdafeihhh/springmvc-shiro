package com.zhuanche.dto.rentcar;

import java.util.Date;

public class DriverOutageDTO {
    private Integer id;

    private Integer driverId;

    private Date outStartDate;

    private Double outStopLongTime;

    private Date outEndDate;

    private Date factStartDate;

    private Date factEndDate;

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

    private String orderIds;

    private String orderNos;

    private Date updateDate;

    /** 前端显示扩展字段 **/

    //页面显示 查询
    private String driverName;
    private String driverPhone;
    private String licensePlates;
    private Integer cityId;
    private String cityName;
    private Integer supplierId;
    private String supplierName;
    private Integer carGroupId;//车组
    private String carGroupName;
    private String startDateBegin;
    private String startDateEnd;

    private String outStartDateStr;
    private String outEndDateStr;
    private String factStartDateStr;
    private String factEndDateStr;
    private String createDateStr;
    private String removeDateStr;

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

    public String getStartDateBegin() {
        return startDateBegin;
    }

    public void setStartDateBegin(String startDateBegin) {
        this.startDateBegin = startDateBegin;
    }

    public String getStartDateEnd() {
        return startDateEnd;
    }

    public void setStartDateEnd(String startDateEnd) {
        this.startDateEnd = startDateEnd;
    }

    public String getOutStartDateStr() {
        return outStartDateStr;
    }

    public void setOutStartDateStr(String outStartDateStr) {
        this.outStartDateStr = outStartDateStr;
    }

    public String getOutEndDateStr() {
        return outEndDateStr;
    }

    public void setOutEndDateStr(String outEndDateStr) {
        this.outEndDateStr = outEndDateStr;
    }

    public String getFactStartDateStr() {
        return factStartDateStr;
    }

    public void setFactStartDateStr(String factStartDateStr) {
        this.factStartDateStr = factStartDateStr;
    }

    public String getFactEndDateStr() {
        return factEndDateStr;
    }

    public void setFactEndDateStr(String factEndDateStr) {
        this.factEndDateStr = factEndDateStr;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getRemoveDateStr() {
        return removeDateStr;
    }

    public void setRemoveDateStr(String removeDateStr) {
        this.removeDateStr = removeDateStr;
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

    public Double getOutStopLongTime() {
        return outStopLongTime;
    }

    public void setOutStopLongTime(Double outStopLongTime) {
        this.outStopLongTime = outStopLongTime;
    }

    public Date getOutEndDate() {
        return outEndDate;
    }

    public void setOutEndDate(Date outEndDate) {
        this.outEndDate = outEndDate;
    }

    public Date getFactStartDate() {
        return factStartDate;
    }

    public void setFactStartDate(Date factStartDate) {
        this.factStartDate = factStartDate;
    }

    public Date getFactEndDate() {
        return factEndDate;
    }

    public void setFactEndDate(Date factEndDate) {
        this.factEndDate = factEndDate;
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

    public String getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(String orderIds) {
        this.orderIds = orderIds == null ? null : orderIds.trim();
    }

    public String getOrderNos() {
        return orderNos;
    }

    public void setOrderNos(String orderNos) {
        this.orderNos = orderNos == null ? null : orderNos.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}