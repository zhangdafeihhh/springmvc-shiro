package com.zhuanche.dto.rentcar;

import java.math.BigDecimal;

/**
 * 司机时长分计算明细
 *
 * @author wuqiang
 * @date 2020.03.11
 */
public class DriverTimeLengthScoreCalculateDetail {

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
     * 基础时长分
     */
    private BigDecimal baseDurationScore;

    /**
     * 高峰时长分
     */
    private BigDecimal gaofengScore;

    /**
     * 空闲时长分
     */
    private BigDecimal kongxianScore;

    /**
     * 调度分
     */
    private BigDecimal diaoduScore;

    /**
     * 	应答保障分
     */
    private BigDecimal yingdaScore;

    /**
     * 其他分
     */
    private BigDecimal otherScore;

    /**
     * 是否计入总分
     */
    private boolean isCollect;

    /**
     * 日期<br>
     *     示例：2020-02-28
     */
    private String day;

    /**
     * 当日总分
     */
    private BigDecimal totalScore;

    /**
     * 时长加速分<br>
     *     时长加速分 = （高峰时长分 + 高峰空闲时长分 + 其他分（特殊鼓励分））* 加速卡
     */
    private BigDecimal growthScore;

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

    public BigDecimal getBaseDurationScore() {
        return baseDurationScore;
    }

    public void setBaseDurationScore(BigDecimal baseDurationScore) {
        this.baseDurationScore = baseDurationScore;
    }

    public BigDecimal getGaofengScore() {
        return gaofengScore;
    }

    public void setGaofengScore(BigDecimal gaofengScore) {
        this.gaofengScore = gaofengScore;
    }

    public BigDecimal getKongxianScore() {
        return kongxianScore;
    }

    public void setKongxianScore(BigDecimal kongxianScore) {
        this.kongxianScore = kongxianScore;
    }

    public BigDecimal getDiaoduScore() {
        return diaoduScore;
    }

    public void setDiaoduScore(BigDecimal diaoduScore) {
        this.diaoduScore = diaoduScore;
    }

    public BigDecimal getYingdaScore() {
        return yingdaScore;
    }

    public void setYingdaScore(BigDecimal yingdaScore) {
        this.yingdaScore = yingdaScore;
    }

    public BigDecimal getOtherScore() {
        return otherScore;
    }

    public void setOtherScore(BigDecimal otherScore) {
        this.otherScore = otherScore;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getGrowthScore() {
        return growthScore;
    }

    public void setGrowthScore(BigDecimal growthScore) {
        this.growthScore = growthScore;
    }
}
