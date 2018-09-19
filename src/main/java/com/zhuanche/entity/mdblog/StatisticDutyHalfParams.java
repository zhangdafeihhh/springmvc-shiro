package com.zhuanche.entity.mdblog;

import com.zhuanche.util.DateUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 司机考勤天详情查询参数
 * @Auther  wanghongdong
 */
public class StatisticDutyHalfParams implements Serializable{

    private static final long serialVersionUID = -3530260791994627833L;

    private String driverId;
    private String table;
    private String gpstable;
    private String yyyyMMdd;
    private Integer value;
    private String time;
    private Integer page;
    private Integer pageSize;

    public StatisticDutyHalfParams(String driverId,String time, Integer page, Integer pageSize){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
        this.table = "statistic_"+sdf.format(tableDate);
        this.gpstable = "car_gps_log_"+sdf1.format(tableDate);
        this.yyyyMMdd = sdf2.format(tableDate);
        this.driverId = driverId.equals("null") ? "" : driverId;
        this.time = time.equals("null") ? "" : time;
        this.page = page;
        this.pageSize = pageSize;
    }

    public StatisticDutyHalfParams(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date tableDate = DateUtil.beforeNDayDate(new Date(), 1);
        this.table = "statistic_"+sdf.format(tableDate);
        this.gpstable = "car_gps_log_"+sdf1.format(tableDate);
        this.yyyyMMdd = sdf2.format(tableDate);
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize == null ? 30 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}