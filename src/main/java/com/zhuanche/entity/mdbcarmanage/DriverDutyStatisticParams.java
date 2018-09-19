package com.zhuanche.entity.mdbcarmanage;

import com.zhuanche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;

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
    private String gpsTable;
    private String yyyyMMdd;
    private Integer value;

    private String sortName;
    private String sortOrder = " desc";

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
        return page == null ? 1 : page ;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize  == null ? 30 : pageSize;
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

    public String getGpsTable() {
        return gpsTable;
    }

    public void setGpsTable(String gpsTable) {
        this.gpsTable = gpsTable;
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

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
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

    public DriverDutyStatisticParams(String cityId, String supplierId, String teamId, String groupIds, String name, String driverId, String phone, String licensePlates, String startTime, String endTime, String sortName, String sortOrder, Integer page, Integer pageSize) {
        this.cityId = cityId.equals("null") ? "" : cityId;
        this.supplierId = supplierId.equals("null") ? "" : supplierId;
        this.teamId = teamId.equals("null") ? "" : teamId;
        this.groupIds = groupIds.equals("null") ? "" : groupIds;
        this.name = name.equals("null") ? "" : name;
        this.driverIds = StringUtils.isNotEmpty(driverId) ? "'"+ driverId + "'" : null;
        this.driverId = driverId.equals("null") ? "" : driverId;
        this.phone = phone.equals("null") ? "" : phone;
        this.licensePlates = licensePlates.equals("null") ? "" : licensePlates;
        this.startTime = startTime.equals("null") ? "" : startTime;
        this.endTime = endTime.equals("null") ? "" : endTime;
        this.sortName = sortName.equals("null") ? "" : sortName;
        this.sortOrder = sortOrder.equals("null") ? "" : sortOrder;
        this.page = page;
        this.pageSize = pageSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy_MM");
        Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
        this.time = sdf.format(tableDate);
        this.table = "statistic_duty_"+sdf4.format(tableDate);
        this.gpsTable = "car_gps_log_"+sdf1.format(tableDate);
        this.yyyyMMdd = sdf2.format(tableDate);
    }

    public DriverDutyStatisticParams() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy_MM");
        Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
        this.time = sdf.format(tableDate);
        this.table = "statistic_duty_"+sdf4.format(tableDate);
        this.gpsTable = "car_gps_log_"+sdf1.format(tableDate);
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
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", driverId='" + driverId + '\'' +
                ", driverIds='" + driverIds + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", table='" + table + '\'' +
                ", time='" + time + '\'' +
                ", gpstable='" + gpsTable + '\'' +
                ", yyyyMMdd='" + yyyyMMdd + '\'' +
                '}';
    }
}