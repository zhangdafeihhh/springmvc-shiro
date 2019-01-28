package com.zhuanche.vo.busManage;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士车辆信息需要展示到页面的封装类
 * @author: niuzilian
 * @create: 2018-11-22 17:41
 * <result property="vehicleBrand" column="vehicle_brand"/>
 * <result property="nextInspectDate" column="next_inspect_date"/>
 * <result property="nextMaintenanceDate" column="next_maintenance_date"/>
 * <result property="nextOperationDate" column="next_operation_date"/>
 * <result property="carPurchaseDate" column="car_purchase_date"/>
 **/

public class BusInfoVO implements Serializable {
    private Integer carId;
    private String licensePlates;
    private String cityName;
    private String supplierName;
    private String groupName;
    private String modelDetail;
    private Integer status;
    private String color;
    private String fueltype;
    private String transportnumber;
    private String vehicleBrand;
    @JSONField(format = "yyyy-MM-dd")
    private Date createDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextInspectDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextMaintenanceDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextOperationDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date carPurchaseDate;

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getModelDetail() {
        return modelDetail;
    }

    public void setModelDetail(String modelDetail) {
        this.modelDetail = modelDetail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFueltype() {
        return fueltype;
    }

    public void setFueltype(String fueltype) {
        this.fueltype = fueltype;
    }

    public String getTransportnumber() {
        return transportnumber;
    }

    public void setTransportnumber(String transportnumber) {
        this.transportnumber = transportnumber;
    }
}
