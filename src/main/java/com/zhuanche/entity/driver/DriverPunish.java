package com.zhuanche.entity.driver;

import java.math.BigDecimal;
import java.util.Date;

public class DriverPunish {
    private Integer punishId;

    private String businessId;

    private Long orderId;

    private String orderNo;

    private Integer punishType;

    private String punishTypeName;

    private String punishReason;

    private BigDecimal stopDay;

    private String stopId;

    private BigDecimal punishPrice;

    private BigDecimal punishIntegral;

    private BigDecimal punishFlow;

    private Date appealDate;

    private Integer driverId;

    private String phone;

    private String name;

    private String licensePlates;

    private Integer cooperationType;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer teamId;

    private String teamName;

    private Integer currentAuditNode;

    private String auditNode;

    private Integer status;

    private Date expireDate;

    private Date createDate;

    private Date updateDate;

    private BigDecimal dispatchPoints;

    private String orderOrigin;

    private Integer channelAppealResult;

    private Integer channelAppealState;

    public Integer getPunishId() {
        return punishId;
    }

    public void setPunishId(Integer punishId) {
        this.punishId = punishId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId == null ? null : businessId.trim();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getPunishType() {
        return punishType;
    }

    public void setPunishType(Integer punishType) {
        this.punishType = punishType;
    }

    public String getPunishTypeName() {
        return punishTypeName;
    }

    public void setPunishTypeName(String punishTypeName) {
        this.punishTypeName = punishTypeName == null ? null : punishTypeName.trim();
    }

    public String getPunishReason() {
        return punishReason;
    }

    public void setPunishReason(String punishReason) {
        this.punishReason = punishReason == null ? null : punishReason.trim();
    }

    public BigDecimal getStopDay() {
        return stopDay;
    }

    public void setStopDay(BigDecimal stopDay) {
        this.stopDay = stopDay;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId == null ? null : stopId.trim();
    }

    public BigDecimal getPunishPrice() {
        return punishPrice;
    }

    public void setPunishPrice(BigDecimal punishPrice) {
        this.punishPrice = punishPrice;
    }

    public BigDecimal getPunishIntegral() {
        return punishIntegral;
    }

    public void setPunishIntegral(BigDecimal punishIntegral) {
        this.punishIntegral = punishIntegral;
    }

    public BigDecimal getPunishFlow() {
        return punishFlow;
    }

    public void setPunishFlow(BigDecimal punishFlow) {
        this.punishFlow = punishFlow;
    }

    public Date getAppealDate() {
        return appealDate;
    }

    public void setAppealDate(Date appealDate) {
        this.appealDate = appealDate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
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

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName == null ? null : teamName.trim();
    }

    public Integer getCurrentAuditNode() {
        return currentAuditNode;
    }

    public void setCurrentAuditNode(Integer currentAuditNode) {
        this.currentAuditNode = currentAuditNode;
    }

    public String getAuditNode() {
        return auditNode;
    }

    public void setAuditNode(String auditNode) {
        this.auditNode = auditNode == null ? null : auditNode.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
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

    public BigDecimal getDispatchPoints() {
        return dispatchPoints;
    }

    public void setDispatchPoints(BigDecimal dispatchPoints) {
        this.dispatchPoints = dispatchPoints;
    }

    public String getOrderOrigin() {
        return orderOrigin;
    }

    public void setOrderOrigin(String orderOrigin) {
        this.orderOrigin = orderOrigin == null ? null : orderOrigin.trim();
    }

    public Integer getChannelAppealResult() {
        return channelAppealResult;
    }

    public void setChannelAppealResult(Integer channelAppealResult) {
        this.channelAppealResult = channelAppealResult;
    }

    public Integer getChannelAppealState() {
        return channelAppealState;
    }

    public void setChannelAppealState(Integer channelAppealState) {
        this.channelAppealState = channelAppealState;
    }
}