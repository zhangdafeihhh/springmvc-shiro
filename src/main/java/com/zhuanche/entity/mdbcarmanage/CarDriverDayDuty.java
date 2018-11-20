package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class CarDriverDayDuty {
    private Integer id;

    private Integer driverId;

    private String driverName;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer teamId;

    private String teamName;

    private String time;//排班日期

    private String forcedIds;

    private String dutyIds;

    private String forcedTimes;

    private String dutyTimes;//排班时长

    private Integer type;

    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        this.driverName = driverName == null ? null : driverName.trim();
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getForcedIds() {
        return forcedIds;
    }

    public void setForcedIds(String forcedIds) {
        this.forcedIds = forcedIds == null ? null : forcedIds.trim();
    }

    public String getDutyIds() {
        return dutyIds;
    }

    public void setDutyIds(String dutyIds) {
        this.dutyIds = dutyIds == null ? null : dutyIds.trim();
    }

    public String getForcedTimes() {
        return forcedTimes;
    }

    public void setForcedTimes(String forcedTimes) {
        this.forcedTimes = forcedTimes == null ? null : forcedTimes.trim();
    }

    public String getDutyTimes() {//排班时长
        return dutyTimes;
    }

    public void setDutyTimes(String dutyTimes) {
        this.dutyTimes = dutyTimes == null ? null : dutyTimes.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}