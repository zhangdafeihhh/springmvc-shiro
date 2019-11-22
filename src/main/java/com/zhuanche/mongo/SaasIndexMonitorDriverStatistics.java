package com.zhuanche.mongo;


import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

public class SaasIndexMonitorDriverStatistics implements Serializable {
    private String id;
    private Integer cityId                  ;
    private String minTime                 ;
    private Integer supplierId              ;
    private Integer driverTeamId            ;
    private Integer inCycleTotalOnlineCnt   ;
    private Integer inCycleTotalServingCnt  ;
    private Integer inCycleTotalFreeCnt     ;
    private Integer inCycleTotalOfflineCnt  ;
    private Integer outCycleTotalOnlineCnt  ;
    private Integer outCycleTotalServingCnt ;
    private Integer outCycleTotalFreeCnt    ;
    private Integer outCycleTotalOfflineCnt ;
    private Integer totalOnlineCnt          ;
    private Integer totalServingCnt         ;
    private Integer totalFreeCnt            ;
    private Integer totalOfflineCnt         ;
    private Integer onLineInCycleRate       ;
    private Integer servingInCycleRate      ;
    private Integer freeInCycleRate         ;
    private Integer offLineInCycleRate      ;
    private Date updateTime              ;
    private Date createTime              ;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getDriverTeamId() {
        return driverTeamId;
    }

    public void setDriverTeamId(Integer driverTeamId) {
        this.driverTeamId = driverTeamId;
    }

    public Integer getInCycleTotalOnlineCnt() {
        return inCycleTotalOnlineCnt;
    }

    public void setInCycleTotalOnlineCnt(Integer inCycleTotalOnlineCnt) {
        this.inCycleTotalOnlineCnt = inCycleTotalOnlineCnt;
    }

    public Integer getInCycleTotalServingCnt() {
        return inCycleTotalServingCnt;
    }

    public void setInCycleTotalServingCnt(Integer inCycleTotalServingCnt) {
        this.inCycleTotalServingCnt = inCycleTotalServingCnt;
    }

    public Integer getInCycleTotalFreeCnt() {
        return inCycleTotalFreeCnt;
    }

    public void setInCycleTotalFreeCnt(Integer inCycleTotalFreeCnt) {
        this.inCycleTotalFreeCnt = inCycleTotalFreeCnt;
    }

    public Integer getInCycleTotalOfflineCnt() {
        return inCycleTotalOfflineCnt;
    }

    public void setInCycleTotalOfflineCnt(Integer inCycleTotalOfflineCnt) {
        this.inCycleTotalOfflineCnt = inCycleTotalOfflineCnt;
    }

    public Integer getOutCycleTotalOnlineCnt() {
        return outCycleTotalOnlineCnt;
    }

    public void setOutCycleTotalOnlineCnt(Integer outCycleTotalOnlineCnt) {
        this.outCycleTotalOnlineCnt = outCycleTotalOnlineCnt;
    }

    public Integer getOutCycleTotalServingCnt() {
        return outCycleTotalServingCnt;
    }

    public void setOutCycleTotalServingCnt(Integer outCycleTotalServingCnt) {
        this.outCycleTotalServingCnt = outCycleTotalServingCnt;
    }

    public Integer getOutCycleTotalFreeCnt() {
        return outCycleTotalFreeCnt;
    }

    public void setOutCycleTotalFreeCnt(Integer outCycleTotalFreeCnt) {
        this.outCycleTotalFreeCnt = outCycleTotalFreeCnt;
    }

    public Integer getOutCycleTotalOfflineCnt() {
        return outCycleTotalOfflineCnt;
    }

    public void setOutCycleTotalOfflineCnt(Integer outCycleTotalOfflineCnt) {
        this.outCycleTotalOfflineCnt = outCycleTotalOfflineCnt;
    }

    public Integer getTotalOnlineCnt() {
        return totalOnlineCnt;
    }

    public void setTotalOnlineCnt(Integer totalOnlineCnt) {
        this.totalOnlineCnt = totalOnlineCnt;
    }

    public Integer getTotalServingCnt() {
        return totalServingCnt;
    }

    public void setTotalServingCnt(Integer totalServingCnt) {
        this.totalServingCnt = totalServingCnt;
    }

    public Integer getTotalFreeCnt() {
        return totalFreeCnt;
    }

    public void setTotalFreeCnt(Integer totalFreeCnt) {
        this.totalFreeCnt = totalFreeCnt;
    }

    public Integer getTotalOfflineCnt() {
        return totalOfflineCnt;
    }

    public void setTotalOfflineCnt(Integer totalOfflineCnt) {
        this.totalOfflineCnt = totalOfflineCnt;
    }

    public Integer getOnLineInCycleRate() {
        return onLineInCycleRate;
    }

    public void setOnLineInCycleRate(Integer onLineInCycleRate) {
        this.onLineInCycleRate = onLineInCycleRate;
    }

    public Integer getServingInCycleRate() {
        return servingInCycleRate;
    }

    public void setServingInCycleRate(Integer servingInCycleRate) {
        this.servingInCycleRate = servingInCycleRate;
    }

    public Integer getFreeInCycleRate() {
        return freeInCycleRate;
    }

    public void setFreeInCycleRate(Integer freeInCycleRate) {
        this.freeInCycleRate = freeInCycleRate;
    }

    public Integer getOffLineInCycleRate() {
        return offLineInCycleRate;
    }

    public void setOffLineInCycleRate(Integer offLineInCycleRate) {
        this.offLineInCycleRate = offLineInCycleRate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
