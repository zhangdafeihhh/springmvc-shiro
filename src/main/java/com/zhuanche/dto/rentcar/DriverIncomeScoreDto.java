package com.zhuanche.dto.rentcar;

import lombok.Data;

public class DriverIncomeScoreDto {
    private String incomeScore;//收入分
    private String beatRate;//击败司机比率，90代表90%
    private Integer driverId;
    private Long updateTime;//更新时间，时间戳
    private String serviceScore;//服务分
    private String tripScore;//时长分
    private String appendScore;//附加分

    public String getIncomeScore() {
        return incomeScore;
    }

    public void setIncomeScore(String incomeScore) {
        this.incomeScore = incomeScore;
    }

    public String getBeatRate() {
        return beatRate;
    }

    public void setBeatRate(String beatRate) {
        this.beatRate = beatRate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(String serviceScore) {
        this.serviceScore = serviceScore;
    }

    public String getTripScore() {
        return tripScore;
    }

    public void setTripScore(String tripScore) {
        this.tripScore = tripScore;
    }

    public String getAppendScore() {
        return appendScore;
    }

    public void setAppendScore(String appendScore) {
        this.appendScore = appendScore;
    }
}
