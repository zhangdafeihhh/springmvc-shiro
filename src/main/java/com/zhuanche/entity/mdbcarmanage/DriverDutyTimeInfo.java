package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class DriverDutyTimeInfo {
    private Integer id;

    private Date time;

    private Integer driverId;

    private String mustDutyTime;

    private String dutyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getMustDutyTime() {
        return mustDutyTime;
    }

    public void setMustDutyTime(String mustDutyTime) {
        this.mustDutyTime = mustDutyTime == null ? null : mustDutyTime.trim();
    }

    public String getDutyTime() {
        return dutyTime;
    }

    public void setDutyTime(String dutyTime) {
        this.dutyTime = dutyTime == null ? null : dutyTime.trim();
    }
}