package com.zhuanche.dto.rentcar;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;

/**
 * 司机派单分概括
 *
 * @author wuqiang
 * @date 2020.04.07
 */
public class DriverDispatchScoreGeneralize {

    /**
     * 城市ID
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 车队ID
     */
    private Integer teamId;

    /**
     * 车队名称
     */
    private String teamName;

    /**
     * 车牌号
     */
    private String licensePlates;

    /**
     * 当前派单分
     */
    private BigDecimal currentDispatchScore = BigDecimal.ZERO;

    /**
     * 服务基础分
     */
    private BigDecimal serviceBaseScore = BigDecimal.ZERO;

    /**
     * 服务分加速分
     */
    private BigDecimal serviceAccelerateScore = BigDecimal.ZERO;

    /**
     * 时长基础分
     */
    private BigDecimal timeLengthBaseScore = BigDecimal.ZERO;

    /**
     * 时长分加速分
     */
    private BigDecimal timeLengthAccelerateScore = BigDecimal.ZERO;

    /**
     * 不良行为扣分
     */
    private BigDecimal badBehaviorDeductionScore = BigDecimal.ZERO;

    /**
     * 更新时间<br>
     *     YYYY-MM-DD
     */
    private String updateDate;

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
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
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
        this.teamName = teamName;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public BigDecimal getCurrentDispatchScore() {
        return currentDispatchScore;
    }

    public void setCurrentDispatchScore(BigDecimal currentDispatchScore) {
        this.currentDispatchScore = currentDispatchScore;
    }

    public BigDecimal getServiceBaseScore() {
        return serviceBaseScore;
    }

    public void setServiceBaseScore(BigDecimal serviceBaseScore) {
        this.serviceBaseScore = serviceBaseScore;
    }

    public BigDecimal getServiceAccelerateScore() {
        return serviceAccelerateScore;
    }

    public void setServiceAccelerateScore(BigDecimal serviceAccelerateScore) {
        this.serviceAccelerateScore = serviceAccelerateScore;
    }

    public BigDecimal getTimeLengthBaseScore() {
        return timeLengthBaseScore;
    }

    public void setTimeLengthBaseScore(BigDecimal timeLengthBaseScore) {
        this.timeLengthBaseScore = timeLengthBaseScore;
    }

    public BigDecimal getTimeLengthAccelerateScore() {
        return timeLengthAccelerateScore;
    }

    public void setTimeLengthAccelerateScore(BigDecimal timeLengthAccelerateScore) {
        this.timeLengthAccelerateScore = timeLengthAccelerateScore;
    }

    public BigDecimal getBadBehaviorDeductionScore() {
        return badBehaviorDeductionScore;
    }

    public void setBadBehaviorDeductionScore(BigDecimal badBehaviorDeductionScore) {
        this.badBehaviorDeductionScore = badBehaviorDeductionScore;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
