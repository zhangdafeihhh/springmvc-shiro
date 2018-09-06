package com.zhuanche.dto.driverDuty;


import java.io.Serializable;

public class DutyExcelDTO implements Serializable{

    String[] title = {"司机姓名","手机号","城市","供应商","车队",
            "排班日期","强制上班时间","排班时长","状态"};

    private String driverName;
    private String phone; // 司机手机号
    private String cityName;
    private String supplierName;
    private String teamName;
    private String dutyTimes;
    private String forcedTimes;
    private String dutyIds;
    private Integer status;

    public String[] getTitle() {
        return title;
    }

    public void setTitle(String[] title) {
        this.title = title;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getDutyTimes() {
        return dutyTimes;
    }

    public void setDutyTimes(String dutyTimes) {
        this.dutyTimes = dutyTimes;
    }

    public String getForcedTimes() {
        return forcedTimes;
    }

    public void setForcedTimes(String forcedTimes) {
        this.forcedTimes = forcedTimes;
    }

    public String getDutyIds() {
        return dutyIds;
    }

    public void setDutyIds(String dutyIds) {
        this.dutyIds = dutyIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    private static final long serialVersionUID = 4705761636123728898L;


}