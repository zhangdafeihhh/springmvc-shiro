package com.zhuanche.entity.driver;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialClueGoods {
    private Integer clueGoodsId;

    private Integer clueId;

    private Integer leaseTerm;

    private BigDecimal frontMoney;

    private BigDecimal rentEveryTerm;

    private BigDecimal firstRent;

    private BigDecimal securityDeposit;

    private BigDecimal totalPrice;

    private Integer basicsVehiclesId;

    private String vehiclesDetailedName;

    private Integer brandId;

    private Integer modelId;

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

    private Double fastPercentage;

    private Byte salesTarget;

    private String goodsNumber;

    private String goodsName;

    private Byte goodsType;

    private String channel;

    private String keyword;

    private Integer vehicleAge;

    private Integer vehicleProperties;

    private String color;

    private String additionalServicesId;

    private String additionalServicesInfo;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    public Integer getClueGoodsId() {
        return clueGoodsId;
    }

    public void setClueGoodsId(Integer clueGoodsId) {
        this.clueGoodsId = clueGoodsId;
    }

    public Integer getClueId() {
        return clueId;
    }

    public void setClueId(Integer clueId) {
        this.clueId = clueId;
    }

    public Integer getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(Integer leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public BigDecimal getFrontMoney() {
        return frontMoney;
    }

    public void setFrontMoney(BigDecimal frontMoney) {
        this.frontMoney = frontMoney;
    }

    public BigDecimal getRentEveryTerm() {
        return rentEveryTerm;
    }

    public void setRentEveryTerm(BigDecimal rentEveryTerm) {
        this.rentEveryTerm = rentEveryTerm;
    }

    public BigDecimal getFirstRent() {
        return firstRent;
    }

    public void setFirstRent(BigDecimal firstRent) {
        this.firstRent = firstRent;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

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

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
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

    public Double getFastPercentage() {
        return fastPercentage;
    }

    public void setFastPercentage(Double fastPercentage) {
        this.fastPercentage = fastPercentage;
    }

    public Byte getSalesTarget() {
        return salesTarget;
    }

    public void setSalesTarget(Byte salesTarget) {
        this.salesTarget = salesTarget;
    }

    public String getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(String goodsNumber) {
        this.goodsNumber = goodsNumber == null ? null : goodsNumber.trim();
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public Byte getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Byte goodsType) {
        this.goodsType = goodsType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    public Integer getVehicleAge() {
        return vehicleAge;
    }

    public void setVehicleAge(Integer vehicleAge) {
        this.vehicleAge = vehicleAge;
    }

    public Integer getVehicleProperties() {
        return vehicleProperties;
    }

    public void setVehicleProperties(Integer vehicleProperties) {
        this.vehicleProperties = vehicleProperties;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    public String getAdditionalServicesId() {
        return additionalServicesId;
    }

    public void setAdditionalServicesId(String additionalServicesId) {
        this.additionalServicesId = additionalServicesId == null ? null : additionalServicesId.trim();
    }

    public String getAdditionalServicesInfo() {
        return additionalServicesInfo;
    }

    public void setAdditionalServicesInfo(String additionalServicesInfo) {
        this.additionalServicesInfo = additionalServicesInfo == null ? null : additionalServicesInfo.trim();
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