package com.zhuanche.dto.rentcar;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: mp-manage
 * @description:参考：car-api中保存到mongoDB中的字段 CarInfoDTO
 * @author: niuzilian
 * @create: 2018-11-26 11:26
 **/
public class CarMongoDTO implements Serializable{
    //
    private Integer status = -1;
    //
    private Integer createBy = -1;
    //
    private Integer updateBy = -1;
    //@NotNull
    private Date createDate ;
    //@NotNull
    private Date updateDate ;

    private String carId = "";
    private String brand = "";
    private String carModel = "";// 车型
    private String licensePlates = "";
    private String color = "";
    private String modelDetail = "";// 具体车型
    private int seatNum = 0;
    private String imgUrl= "";

    private String groupId= "";
    private String groupName= "";
    private String cityId;
    private String cityName;


    private java.lang.Integer supplierId;

    private java.lang.String supplierName;

    private java.lang.Integer carModelId;

    private java.lang.String engineNo;

    private java.lang.String frameNo;

    private Date nextInspectDate;

    private Date nextMaintenanceDate;

    private Date rentalExpireDate;

    private java.lang.String memo;
    private Double basePrice = 0d;
    private Double mileagePrice = 0d;
    private Double minutePrice = 0d;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
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

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModelDetail() {
        return modelDetail;
    }

    public void setModelDetail(String modelDetail) {
        this.modelDetail = modelDetail;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public Integer getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(Integer carModelId) {
        this.carModelId = carModelId;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
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

    public Date getRentalExpireDate() {
        return rentalExpireDate;
    }

    public void setRentalExpireDate(Date rentalExpireDate) {
        this.rentalExpireDate = rentalExpireDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getMileagePrice() {
        return mileagePrice;
    }

    public void setMileagePrice(Double mileagePrice) {
        this.mileagePrice = mileagePrice;
    }

    public Double getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(Double minutePrice) {
        this.minutePrice = minutePrice;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
