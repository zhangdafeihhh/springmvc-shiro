package com.zhuanche.mongo;

import java.io.Serializable;
import java.util.Date;

public class OutCycleDriverList  implements Serializable {

    private String id            ;
    private Integer cityId       ;
    private String minTime       ;
    private Integer driverId     ;
    private String licensePlates ;
    private long lastPositionTime;
    private String lastTimeStr   ;
    private Integer type         ;
    private Integer supplierId   ;
    private Integer driverTeamId ;
    //private Date createTime      ;

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

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public long getLastPositionTime() {
        return lastPositionTime;
    }

    public void setLastPositionTime(long lastPositionTime) {
        this.lastPositionTime = lastPositionTime;
    }

    public String getLastTimeStr() {
        return lastTimeStr;
    }

    public void setLastTimeStr(String lastTimeStr) {
        this.lastTimeStr = lastTimeStr;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

}
