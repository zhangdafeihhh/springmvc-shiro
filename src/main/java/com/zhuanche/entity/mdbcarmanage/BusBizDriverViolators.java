package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class BusBizDriverViolators {
    private Integer id;

    private Integer busDriverId;

    private String busDriverName;

    private String busDriverPhone;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer groupId;

    private String groupName;

    private String evaluateScore;

    private String idNumber;

    private Short punishStatus;

    private String punishReason;

    private Integer punishDuration;

    private Date punishStartTime;

    private Date punishEndTime;

    private Date createTime;

    private Date updateTime;

    private Short status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBusDriverId() {
        return busDriverId;
    }

    public void setBusDriverId(Integer busDriverId) {
        this.busDriverId = busDriverId;
    }

    public String getBusDriverName() {
        return busDriverName;
    }

    public void setBusDriverName(String busDriverName) {this.busDriverName = busDriverName == null ? null : busDriverName.trim();}

    public String getBusDriverPhone() {
        return busDriverPhone;
    }

    public void setBusDriverPhone(String busDriverPhone) {this.busDriverPhone = busDriverPhone == null ? null : busDriverPhone.trim();}

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

    public void setSupplierName(String supplierName) {this.supplierName = supplierName == null ? null : supplierName.trim();}

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
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getEvaluateScore() {
        return evaluateScore;
    }

    public void setEvaluateScore(String evaluateScore) {this.evaluateScore = evaluateScore == null ? null : evaluateScore.trim();}

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber == null ? null : idNumber.trim();
    }

    public Short getPunishStatus() {
        return punishStatus;
    }

    public void setPunishStatus(Short punishStatus) {
        this.punishStatus = punishStatus;
    }

    public String getPunishReason() {
        return punishReason;
    }

    public void setPunishReason(String punishReason) {this.punishReason = punishReason == null ? null : punishReason.trim();}

    public Integer getPunishDuration() {
        return punishDuration;
    }

    public void setPunishDuration(Integer punishDuration) {
        this.punishDuration = punishDuration;
    }

    public Date getPunishStartTime() {
        return punishStartTime;
    }

    public void setPunishStartTime(Date punishStartTime) {
        this.punishStartTime = punishStartTime;
    }

    public Date getPunishEndTime() {
        return punishEndTime;
    }

    public void setPunishEndTime(Date punishEndTime) {
        this.punishEndTime = punishEndTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}