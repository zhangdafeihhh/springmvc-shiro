package com.zhuanche.dto.rentcar;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;

/**
 * 策略工具组返回的司机派单分概括
 *
 * @author wuqiang
 * @date 2020.04.07
 */
public class DriverIntegralDispatchScoreGeneralize {

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 当前派单分
     */
    private BigDecimal totalScore;

    /**
     * 服务基础分
     */
    private BigDecimal serviceScore;

    /**
     * 服务分加速分
     */
    private BigDecimal servGrowthScore;

    /**
     * 时长基础分
     */
    private BigDecimal durationScore;

    /**
     * 时长分加速分
     */
    private BigDecimal duraGrowthScore;

    /**
     * 不良行为扣分
     */
    private BigDecimal badScore;

    /**
     * 更新时间，时间戳
     */
    private long updateTime;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(BigDecimal serviceScore) {
        this.serviceScore = serviceScore;
    }

    public BigDecimal getServGrowthScore() {
        return servGrowthScore;
    }

    public void setServGrowthScore(BigDecimal servGrowthScore) {
        this.servGrowthScore = servGrowthScore;
    }

    public BigDecimal getDurationScore() {
        return durationScore;
    }

    public void setDurationScore(BigDecimal durationScore) {
        this.durationScore = durationScore;
    }

    public BigDecimal getDuraGrowthScore() {
        return duraGrowthScore;
    }

    public void setDuraGrowthScore(BigDecimal duraGrowthScore) {
        this.duraGrowthScore = duraGrowthScore;
    }

    public BigDecimal getBadScore() {
        return badScore;
    }

    public void setBadScore(BigDecimal badScore) {
        this.badScore = badScore;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
