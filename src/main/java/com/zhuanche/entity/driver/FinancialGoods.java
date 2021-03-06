package com.zhuanche.entity.driver;

import java.math.BigDecimal;
import java.util.Date;

public class FinancialGoods {
    private Integer goodsId;

    private Long brandId;

    private Long modelId;

    private Integer basicsVehiclesId;

    private String goodsNumber;

    private String goodsName;

    private Byte salesTarget;

    private Byte goodsType;

    private Integer supplierId;

    private String supplierFullName;

    private Integer cityId;

    private String cityName;

    private Integer channelId;

    private String reason;

    private String expInfo;

    private String pictureUrl;

    private String keyword;

    private Integer vehicleAge;

    private Integer mileage;

    private Integer vehicleProperties;

    private Integer sourceFundsId;

    private Integer leaseTerm;

    private BigDecimal rentEveryTerm;

    private BigDecimal frontMoney;

    private BigDecimal firstRent;

    private BigDecimal securityDeposit;

    private BigDecimal totalPrice;

    private String color;

    private String additionalServicesId;

    private String additionalServicesInfo;

    private Integer stock;

    private Byte status;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
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

    public Integer getBasicsVehiclesId() {
        return basicsVehiclesId;
    }

    public void setBasicsVehiclesId(Integer basicsVehiclesId) {
        this.basicsVehiclesId = basicsVehiclesId;
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

    public Byte getSalesTarget() {
        return salesTarget;
    }

    public void setSalesTarget(Byte salesTarget) {
        this.salesTarget = salesTarget;
    }

    public Byte getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Byte goodsType) {
        this.goodsType = goodsType;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierFullName() {
        return supplierFullName;
    }

    public void setSupplierFullName(String supplierFullName) {
        this.supplierFullName = supplierFullName == null ? null : supplierFullName.trim();
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

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getExpInfo() {
        return expInfo;
    }

    public void setExpInfo(String expInfo) {
        this.expInfo = expInfo == null ? null : expInfo.trim();
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl == null ? null : pictureUrl.trim();
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

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getVehicleProperties() {
        return vehicleProperties;
    }

    public void setVehicleProperties(Integer vehicleProperties) {
        this.vehicleProperties = vehicleProperties;
    }

    public Integer getSourceFundsId() {
        return sourceFundsId;
    }

    public void setSourceFundsId(Integer sourceFundsId) {
        this.sourceFundsId = sourceFundsId;
    }

    public Integer getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(Integer leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public BigDecimal getRentEveryTerm() {
        return rentEveryTerm;
    }

    public void setRentEveryTerm(BigDecimal rentEveryTerm) {
        this.rentEveryTerm = rentEveryTerm;
    }

    public BigDecimal getFrontMoney() {
        return frontMoney;
    }

    public void setFrontMoney(BigDecimal frontMoney) {
        this.frontMoney = frontMoney;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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