package com.zhuanche.dto.driverDuty;

import com.zhuanche.entity.mdbcarmanage.CarDriverMonthDuty;
import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;

import java.util.Map;

public class CarDriverMonthDTO extends CarDriverMonthDuty {

    private static final long serialVersionUID = 5787710466657668410L;

    private Integer status;

    private String data;//该月排班详情

    private Map<String,String> map;//

    private String teamName;

    private String supplierName;

    private String cityName;

    private String monitorDate;//考勤日期

    public String getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(String monitorDate) {
        this.monitorDate = monitorDate;
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

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}