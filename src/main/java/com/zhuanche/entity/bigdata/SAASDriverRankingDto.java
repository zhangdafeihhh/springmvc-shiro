package com.zhuanche.entity.bigdata;

import java.io.Serializable;

public class SAASDriverRankingDto implements Serializable {
    private Integer serial;
    private String driverId;
    private String driverName;
    private String motocadeName;
    private String orderAmount;
    private String scoreAvg;
    private String serviceTimeAmount;
    private String totalAmount7;

    public SAASDriverRankingDto() {
    }

    public Integer getSerial() {
        return this.serial;
    }

    public String getDriverId() {
        return this.driverId;
    }

    public String getDriverName() {
        return this.driverName;
    }

    public String getMotocadeName() {
        return this.motocadeName;
    }

    public String getOrderAmount() {
        return this.orderAmount;
    }

    public String getScoreAvg() {
        return this.scoreAvg;
    }

    public String getServiceTimeAmount() {
        return this.serviceTimeAmount;
    }

    public String getTotalAmount7() {
        return this.totalAmount7;
    }

    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setMotocadeName(String motocadeName) {
        this.motocadeName = motocadeName;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public void setScoreAvg(String scoreAvg) {
        this.scoreAvg = scoreAvg;
    }

    public void setServiceTimeAmount(String serviceTimeAmount) {
        this.serviceTimeAmount = serviceTimeAmount;
    }

    public void setTotalAmount7(String totalAmount7) {
        this.totalAmount7 = totalAmount7;
    }

}
