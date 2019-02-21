package com.zhuanche.util.objcompare.entity;

import com.zhuanche.util.objcompare.FieldNote;

import java.util.Date;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-01-14 10:43
 **/
public class BusCarCompareEntity {
    //private Integer carId;
    @FieldNote("城市名称")
    private String cityName;
    //private Integer supplierId;
    @FieldNote("供应商名称")
    private String supplierName;
    @FieldNote("车牌号")
    private String licensePlates;
    //private Integer groupId;
    @FieldNote("车型类别")
    private String groupName;
    @FieldNote("车辆厂牌")
    private String vehicleBrand;
    @FieldNote("具体车型")
    private String modelDetail;
    @FieldNote("颜色")
    private String color;
    @FieldNote("燃料")
    private String fuelName;
    @FieldNote("运输证字号")
    private String transportnumber;
    @FieldNote("状态")
    private String status;
    @FieldNote(value="下次车检时间",pattern = FieldNote.Pattern.DATE)
    private Date nextInspectDate;
    @FieldNote(value = "下次维保时间",pattern = FieldNote.Pattern.DATE)
    private Date nextMaintenanceDate;
    @FieldNote(value = "下次运营证检测时间",pattern = FieldNote.Pattern.DATE)
    private Date nextOperationDate;
    @FieldNote(value = "购买时间",pattern = FieldNote.Pattern.DATE)
    private Date carPurchaseDate;

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

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
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


    public String getFuelName() {
        return fuelName;
    }

    public void setFuelName(String fuelName) {
        this.fuelName = fuelName;
    }

    public String getTransportnumber() {
        return transportnumber;
    }

    public void setTransportnumber(String transportnumber) {
        this.transportnumber = transportnumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getNextInspectDate() {
        return nextInspectDate;
    }

    public void setNextInspectDate(Date nextInspectDate) {
        this.nextInspectDate = nextInspectDate;
    }

    public Date getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(Date nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public Date getNextOperationDate() {
        return nextOperationDate;
    }

    public void setNextOperationDate(Date nextOperationDate) {
        this.nextOperationDate = nextOperationDate;
    }

    public Date getCarPurchaseDate() {
        return carPurchaseDate;
    }

    public void setCarPurchaseDate(Date carPurchaseDate) {
        this.carPurchaseDate = carPurchaseDate;
    }
}
