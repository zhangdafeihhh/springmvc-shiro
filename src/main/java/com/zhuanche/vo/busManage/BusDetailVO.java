package com.zhuanche.vo.busManage;

import java.io.Serializable;

/**
 * @program: mp-manage
 * @description: 巴士saas详情页展示
 * @author: niuzilian
 * @create: 2018-11-23 13:27
 **/
public class BusDetailVO implements Serializable{
    private Integer carId;
    private Integer cityId;
    private String cityName;
    private Integer supplierId;
    private String supplierName;
    private String licensePlates;
    private Integer groupId;
    private String groupName;
    private String vehicleBrand;
    private String modelDetail;
    private String color;
    private String fuelType;
    private String fuelName;
    private String transportNumber;
    private Integer status;

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getModelDetail() {
        return modelDetail;
    }

    public void setModelDetail(String modelDetail) {
        this.modelDetail = modelDetail;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelName() {
        return fuelName;
    }

    public void setFuelName(String fuelName) {
        this.fuelName = fuelName;
    }

    public String getTransportNumber() {
        return transportNumber;
    }

    public void setTransportNumber(String transportNumber) {
        this.transportNumber = transportNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
