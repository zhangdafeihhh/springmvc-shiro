package com.zhuanche.dto.rentcar;

import java.math.BigDecimal;

/**
 * 司机服务分计算明细
 *
 * @author wuqiang
 * @date 2020.03.11
 */
public class DriverServiceScoreCalculateDetail {

    /**
     * 司机ID
     */
    private Integer driverId = 0;

    /**
     * 司机姓名
     */
    private String driverName = "";

    /**
     * 司机手机号
     */
    private String driverPhone = "";

    /**
     * 基础服务分
     */
    private BigDecimal baseServiceScore = BigDecimal.ZERO;

    /**
     * 完单分
     */
    private BigDecimal wandanScore = BigDecimal.ZERO;

    /**
     * 好评分
     */
    private BigDecimal haopingScore = BigDecimal.ZERO;

    /**
     * 稽查合格分
     */
    private BigDecimal jichaScore = BigDecimal.ZERO;

    /**
     * 夜间保障分
     */
    private BigDecimal yejianScore = BigDecimal.ZERO;

    /**
     * 其他分
     */
    private BigDecimal otherScore = BigDecimal.ZERO;

    /**
     * 	是否计入总分
     */
    private boolean isCollect = false;

    /**
     * 	所属日期<br>
     * 	    示例：2020-02-28
     */
    private String day;

    /**
     * 当日总分
     */
    private BigDecimal totalScore = BigDecimal.ZERO;

    /**
     * 服务加速分<br>
     *     服务加速分 = （当日高峰时长分 + 高峰空闲时长分 + 其他分（特殊鼓励分））* 加速卡
     */
    private BigDecimal growthScore = BigDecimal.ZERO;

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

    public BigDecimal getBaseServiceScore() {
        return baseServiceScore;
    }

    public void setBaseServiceScore(BigDecimal baseServiceScore) {
        this.baseServiceScore = baseServiceScore;
    }

    public BigDecimal getWandanScore() {
        return wandanScore;
    }

    public void setWandanScore(BigDecimal wandanScore) {
        this.wandanScore = wandanScore;
    }

    public BigDecimal getHaopingScore() {
        return haopingScore;
    }

    public void setHaopingScore(BigDecimal haopingScore) {
        this.haopingScore = haopingScore;
    }

    public BigDecimal getJichaScore() {
        return jichaScore;
    }

    public void setJichaScore(BigDecimal jichaScore) {
        this.jichaScore = jichaScore;
    }

    public BigDecimal getYejianScore() {
        return yejianScore;
    }

    public void setYejianScore(BigDecimal yejianScore) {
        this.yejianScore = yejianScore;
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
