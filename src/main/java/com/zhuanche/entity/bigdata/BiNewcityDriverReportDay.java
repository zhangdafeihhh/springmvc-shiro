package com.zhuanche.entity.bigdata;

import java.math.BigDecimal;
import java.util.Date;

public class BiNewcityDriverReportDay {
    private Long tableauId;

    private Date dataDate;

    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private String licensePlates;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer bindOrderNum;

    private Integer settleOrderNum;

    private BigDecimal totalAmount;

    public Long getTableauId() {
        return tableauId;
    }

    public void setTableauId(Long tableauId) {
        this.tableauId = tableauId;
    }

    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
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

    public Integer getBindOrderNum() {
        return bindOrderNum;
    }

    public void setBindOrderNum(Integer bindOrderNum) {
        this.bindOrderNum = bindOrderNum;
    }

    public Integer getSettleOrderNum() {
        return settleOrderNum;
    }

    public void setSettleOrderNum(Integer settleOrderNum) {
        this.settleOrderNum = settleOrderNum;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}