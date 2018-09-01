package com.zhuanche.entity.rentcar;

import com.zhuanche.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *    司机考勤查询参数
 */
public class DriverDutyStatisticParams {

    private String cityId;
    private String supplierId;
    private String teamId;
    private String groupIds;
    private String name;
    private String phone;
    private String licensePlates;
//    private String startDate;
//    private String endDate;
    private String startTime;
    private String endTime;
    private String driverId;
    private String driverIds;
    private String teamIds;//车队
    private String suppliers;//供应商
    private String cities;//城市


    private Integer page;
    private Integer pageSize;
    private String table;
    private String time;
    private String gpstable;
    private String yyyyMMdd;
    private Integer value;

    private String sortname;
    private String sortorder = " desc";

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

//    public String getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(String startDate) {
//        this.startDate = startDate;
//    }
//
//    public String getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(String endDate) {
//        this.endDate = endDate;
//    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGpstable() {
        return gpstable;
    }

    public void setGpstable(String gpstable) {
        this.gpstable = gpstable;
    }

    public String getYyyyMMdd() {
        return yyyyMMdd;
    }

    public void setYyyyMMdd(String yyyyMMdd) {
        this.yyyyMMdd = yyyyMMdd;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getSortname() {
        return sortname;
    }

    public void setSortname(String sortname) {
        this.sortname = sortname;
    }

    public String getSortorder() {
        return sortorder;
    }

    public void setSortorder(String sortorder) {
        this.sortorder = sortorder;
    }

    public String getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(String teamIds) {
        this.teamIds = teamIds;
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public DriverDutyStatisticParams(String cityId, String supplierId, String teamId, String groupIds, String name, String driverId, String phone, String licensePlates, String startDate, String endDate, String startTime, String endTime, Integer page, Integer pageSize) {
        this.cityId = cityId;
        this.supplierId = supplierId;
        this.teamId = teamId;
        this.groupIds = groupIds;
        this.name = name;
        this.driverIds = "'"+ driverId + "'";
        this.driverId = driverId;
        this.phone = phone;
        this.licensePlates = licensePlates;
//        this.startDate = startDate;
//        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.page = page;
        this.pageSize = pageSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy_MM");
        Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
        this.time = sdf.format(tableDate);
        this.table = "statistic_duty_"+sdf4.format(tableDate);
        this.gpstable = "car_gps_log_"+sdf1.format(tableDate);
        this.yyyyMMdd = sdf2.format(tableDate);
    }

    public DriverDutyStatisticParams(String cityId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy_MM");
        Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
        this.time = sdf.format(tableDate);
        this.table = "statistic_duty_"+sdf4.format(tableDate);
        this.gpstable = "car_gps_log_"+sdf1.format(tableDate);
        this.yyyyMMdd = sdf2.format(tableDate);
    }

    @Override
    public String toString() {
        return "DriverDutyStatisticParams{" +
                "cityId='" + cityId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", teamId='" + teamId + '\'' +
                ", groupIds='" + groupIds + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", licensePlates='" + licensePlates + '\'' +
//                ", startDate='" + startDate + '\'' +
//                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", driverId='" + driverId + '\'' +
                ", driverIds='" + driverIds + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", table='" + table + '\'' +
                ", time='" + time + '\'' +
                ", gpstable='" + gpstable + '\'' +
                ", yyyyMMdd='" + yyyyMMdd + '\'' +
                '}';
    }
}