package com.zhuanche.dto.rentcar;

import java.util.Date;

public class DriverDutyStatisticDTO {
    private Integer id;

    private Integer driverid;

    private String licenseplates;

    private String time;

    private String name;

    private String phone;

    private Double dutytime;

    private Double forcedtime;

    private Double overtime;

    private Double forcedtime1;

    private Double forcedtime2;

    private Double forcedtime3;

    private Double forcedtime4;

    private String cityName;

    private String dutyTimeAll;

    private String forcedTimeAll;

    public String getDutyTimeAll() {
        return dutyTimeAll;
    }

    public void setDutyTimeAll(String dutyTimeAll) {
        this.dutyTimeAll = dutyTimeAll;
    }

    public String getForcedTimeAll() {
        return forcedTimeAll;
    }

    public void setForcedTimeAll(String forcedTimeAll) {
        this.forcedTimeAll = forcedTimeAll;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverid() {
        return driverid;
    }

    public void setDriverid(Integer driverid) {
        this.driverid = driverid;
    }

    public String getLicenseplates() {
        return licenseplates;
    }

    public void setLicenseplates(String licenseplates) {
        this.licenseplates = licenseplates == null ? null : licenseplates.trim();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Double getDutytime() {
        return dutytime;
    }

    public void setDutytime(Double dutytime) {
        this.dutytime = dutytime;
    }

    public Double getForcedtime() {
        return forcedtime;
    }

    public void setForcedtime(Double forcedtime) {
        this.forcedtime = forcedtime;
    }

    public Double getOvertime() {
        return overtime;
    }

    public void setOvertime(Double overtime) {
        this.overtime = overtime;
    }

    public Double getForcedtime1() {
        return forcedtime1;
    }

    public void setForcedtime1(Double forcedtime1) {
        this.forcedtime1 = forcedtime1;
    }

    public Double getForcedtime2() {
        return forcedtime2;
    }

    public void setForcedtime2(Double forcedtime2) {
        this.forcedtime2 = forcedtime2;
    }

    public Double getForcedtime3() {
        return forcedtime3;
    }

    public void setForcedtime3(Double forcedtime3) {
        this.forcedtime3 = forcedtime3;
    }

    public Double getForcedtime4() {
        return forcedtime4;
    }

    public void setForcedtime4(Double forcedtime4) {
        this.forcedtime4 = forcedtime4;
    }

}