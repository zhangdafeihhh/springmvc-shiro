package com.zhuanche.dto.transportMonitor;

import java.math.BigDecimal;

public class IndexMonitorDriverStatisticsDto {
    private String id;
    private Integer cityId                  ;
    private String minTime                 ;
    private Integer supplierId              ;
    private Integer driverTeamId            ;
    /**总数量(圈内)上线司机数*/
    private Integer inCycleTotalOnlineCnt   ;
    /**总数量(圈内)服务中司机数*/
    private Integer inCycleTotalServingCnt  ;
    /**总数量(圈内)空闲司机数*/
    private Integer inCycleTotalFreeCnt     ;
    /**总数量(圈内)线下听单司机数*/
    private Integer inCycleTotalOfflineCnt  ;
    /**总数量(圈外)上线司机数*/
    private Integer outCycleTotalOnlineCnt  ;
    /**总数量(圈外)服务中司机数*/
    private Integer outCycleTotalServingCnt ;
    /**总数量(圈外)空闲司机数*/
    private Integer outCycleTotalFreeCnt    ;
    /**总数量(圈外)线下听单司机数*/
    private Integer outCycleTotalOfflineCnt ;
    /**总数量(全城)上线司机数*/
    private Integer totalOnlineCnt          ;
    /**总数量(全城)服务中司机数*/
    private Integer totalServingCnt         ;
    /**总数量(全城)空闲司机数*/
    private Integer totalFreeCnt            ;
    /**总数量(全城)线下听单司机数*/
    private Integer totalOfflineCnt         ;
    /**
     *上线司机 运力在圈率
     */
    private BigDecimal onLineInCycleRate;
    /**
     *服务中司机 运力在圈率
     */
    private BigDecimal servingInCycleRate;
    /**
     *空闲司机 运力在圈率
     */
    private BigDecimal freeInCycleRate;
    /**
     *线下听单司机 运力在圈率
     */
    private BigDecimal offLineInCycleRate;
    /**
     *全城运营率
     */
    private BigDecimal cityOperatingRate;
    /**
     *圈内运营率
     */
    private BigDecimal withinCircleOperatingRate;
    /**
     *圈外运营率
     */
    private BigDecimal outSideCircleOperatingRate;
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

    public BigDecimal getOnLineInCycleRate() {
        return onLineInCycleRate;
    }

    public void setOnLineInCycleRate(BigDecimal onLineInCycleRate) {
        this.onLineInCycleRate = onLineInCycleRate;
    }

    public BigDecimal getServingInCycleRate() {
        return servingInCycleRate;
    }

    public void setServingInCycleRate(BigDecimal servingInCycleRate) {
        this.servingInCycleRate = servingInCycleRate;
    }

    public BigDecimal getFreeInCycleRate() {
        return freeInCycleRate;
    }

    public void setFreeInCycleRate(BigDecimal freeInCycleRate) {
        this.freeInCycleRate = freeInCycleRate;
    }

    public BigDecimal getOffLineInCycleRate() {
        return offLineInCycleRate;
    }

    public void setOffLineInCycleRate(BigDecimal offLineInCycleRate) {
        this.offLineInCycleRate = offLineInCycleRate;
    }

    public BigDecimal getCityOperatingRate() {
        return cityOperatingRate;
    }

    public void setCityOperatingRate(BigDecimal cityOperatingRate) {
        this.cityOperatingRate = cityOperatingRate;
    }

    public BigDecimal getWithinCircleOperatingRate() {
        return withinCircleOperatingRate;
    }

    public void setWithinCircleOperatingRate(BigDecimal withinCircleOperatingRate) {
        this.withinCircleOperatingRate = withinCircleOperatingRate;
    }

    public BigDecimal getOutSideCircleOperatingRate() {
        return outSideCircleOperatingRate;
    }

    public void setOutSideCircleOperatingRate(BigDecimal outSideCircleOperatingRate) {
        this.outSideCircleOperatingRate = outSideCircleOperatingRate;
    }
}
