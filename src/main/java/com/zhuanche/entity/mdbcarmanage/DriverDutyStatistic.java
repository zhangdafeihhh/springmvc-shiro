package com.zhuanche.entity.mdbcarmanage;

public class DriverDutyStatistic {
    private Integer id;

    private Integer driverid;

    private String licenseplates;

    private String time;

    private String name;

    private String phone;

    private Integer cityid;

    private Double alltime;

    private Double zgftime;

    private Double wgftime;

    private Double dutytime;

    private Double forcedtime;

    private Double overtime;

    private Integer supplierid;

    private Double forcedtime1;

    private Double forcedtime2;

    private Double forcedtime3;

    private Double forcedtime4;

    private Integer cargroupid;

    private String dutyTimeAll;

    private String forcedTimeAll;

    private String cityName;

    private String supplierName;

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

    public Integer getCityid() {
        return cityid;
    }

    public void setCityid(Integer cityid) {
        this.cityid = cityid;
    }

    public Double getAlltime() {
        return alltime;
    }

    public void setAlltime(Double alltime) {
        this.alltime = alltime;
    }

    public Double getZgftime() {
        return zgftime;
    }

    public void setZgftime(Double zgftime) {
        this.zgftime = zgftime;
    }

    public Double getWgftime() {
        return wgftime;
    }

    public void setWgftime(Double wgftime) {
        this.wgftime = wgftime;
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

    public Integer getSupplierid() {
        return supplierid;
    }

    public void setSupplierid(Integer supplierid) {
        this.supplierid = supplierid;
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

    public Integer getCargroupid() {
        return cargroupid;
    }

    public void setCargroupid(Integer cargroupid) {
        this.cargroupid = cargroupid;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}