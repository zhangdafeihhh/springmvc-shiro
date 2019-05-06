package com.zhuanche.entity.driver;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialBasicsVehicles {
    private Integer basicsVehiclesId;

    private String vehiclesDetailedName;

    private Long brandId;

    private Long modelId;

    private String vehicleStyle;

    private String yearStyle;

    private Integer energyType;

    private Integer variableBox;

    private BigDecimal guidancePrice;

    private Integer discharge;

    private Integer mileage;

    private String autoHomeUrl;

    private String lengthWidthHeight;

    private String qualityAssurance;

    private Integer wheelbase;

    private String environmentalProtectionStandard;

    private Integer fastChargingTime;

    private Integer slowChargingTime;

    private String imgUrl;

    private Double fastPercentage;

    private Byte enableStatus;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    public Integer getBasicsVehiclesId() {
        return basicsVehiclesId;
    }

    public void setBasicsVehiclesId(Integer basicsVehiclesId) {
        this.basicsVehiclesId = basicsVehiclesId;
    }

    public String getVehiclesDetailedName() {
        return vehiclesDetailedName;
    }

    public void setVehiclesDetailedName(String vehiclesDetailedName) {
        this.vehiclesDetailedName = vehiclesDetailedName == null ? null : vehiclesDetailedName.trim();
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getVehicleStyle() {
        return vehicleStyle;
    }

    public void setVehicleStyle(String vehicleStyle) {
        this.vehicleStyle = vehicleStyle == null ? null : vehicleStyle.trim();
    }

    public String getYearStyle() {
        return yearStyle;
    }

    public void setYearStyle(String yearStyle) {
        this.yearStyle = yearStyle == null ? null : yearStyle.trim();
    }

    public Integer getEnergyType() {
        return energyType;
    }

    public void setEnergyType(Integer energyType) {
        this.energyType = energyType;
    }

    public Integer getVariableBox() {
        return variableBox;
    }

    public void setVariableBox(Integer variableBox) {
        this.variableBox = variableBox;
    }

    public BigDecimal getGuidancePrice() {
        return guidancePrice;
    }

    public void setGuidancePrice(BigDecimal guidancePrice) {
        this.guidancePrice = guidancePrice;
    }

    public Integer getDischarge() {
        return discharge;
    }

    public void setDischarge(Integer discharge) {
        this.discharge = discharge;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getAutoHomeUrl() {
        return autoHomeUrl;
    }

    public void setAutoHomeUrl(String autoHomeUrl) {
        this.autoHomeUrl = autoHomeUrl == null ? null : autoHomeUrl.trim();
    }

    public String getLengthWidthHeight() {
        return lengthWidthHeight;
    }

    public void setLengthWidthHeight(String lengthWidthHeight) {
        this.lengthWidthHeight = lengthWidthHeight == null ? null : lengthWidthHeight.trim();
    }

    public String getQualityAssurance() {
        return qualityAssurance;
    }

    public void setQualityAssurance(String qualityAssurance) {
        this.qualityAssurance = qualityAssurance == null ? null : qualityAssurance.trim();
    }

    public Integer getWheelbase() {
        return wheelbase;
    }

    public void setWheelbase(Integer wheelbase) {
        this.wheelbase = wheelbase;
    }

    public String getEnvironmentalProtectionStandard() {
        return environmentalProtectionStandard;
    }

    public void setEnvironmentalProtectionStandard(String environmentalProtectionStandard) {
        this.environmentalProtectionStandard = environmentalProtectionStandard == null ? null : environmentalProtectionStandard.trim();
    }

    public Integer getFastChargingTime() {
        return fastChargingTime;
    }

    public void setFastChargingTime(Integer fastChargingTime) {
        this.fastChargingTime = fastChargingTime;
    }

    public Integer getSlowChargingTime() {
        return slowChargingTime;
    }

    public void setSlowChargingTime(Integer slowChargingTime) {
        this.slowChargingTime = slowChargingTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public Double getFastPercentage() {
        return fastPercentage;
    }

    public void setFastPercentage(Double fastPercentage) {
        this.fastPercentage = fastPercentage;
    }

    public Byte getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Byte enableStatus) {
        this.enableStatus = enableStatus;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}