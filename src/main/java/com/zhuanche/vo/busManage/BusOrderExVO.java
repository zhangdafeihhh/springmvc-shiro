package com.zhuanche.vo.busManage;

import java.util.Date;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-01-07 20:18
 **/
public class BusOrderExVO extends BusOrderVO{

    // carGroupName  riderCount  factDate   factStartAddr  factEndAddr  supplierName
     /** 实际预定车型**/
    private String carGroupName;
     /** 乘车人数量**/
    private Integer riderCount;
    /** 实际上车时间**/
    private Date factDate;
    /** 实际上车地点**/
    private String factStartAddr;
    /** 实际下车地点 **/
    private String factEndAddr;
    /** 行李箱数**/
    private Integer luggageCount;
    /** 预定人名称**/
    private String bookingUserName;
    /** 供应商名称*/
    private String supplierName;


    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName;
    }

    public Integer getRiderCount() {
        return riderCount;
    }

    public void setRiderCount(Integer riderCount) {
        this.riderCount = riderCount;
    }

    public Date getFactDate() {
        return factDate;
    }

    public void setFactDate(Date factDate) {
        this.factDate = factDate;
    }

    public String getFactStartAddr() {
        return factStartAddr;
    }

    public void setFactStartAddr(String factStartAddr) {
        this.factStartAddr = factStartAddr;
    }

    public String getFactEndAddr() {
        return factEndAddr;
    }

    public void setFactEndAddr(String factEndAddr) {
        this.factEndAddr = factEndAddr;
    }

    public Integer getLuggageCount() {
        return luggageCount;
    }

    public void setLuggageCount(Integer luggageCount) {
        this.luggageCount = luggageCount;
    }

    public String getBookingUserName() {
        return bookingUserName;
    }

    public void setBookingUserName(String bookingUserName) {
        this.bookingUserName = bookingUserName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }


}
