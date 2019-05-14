package com.zhuanche.bo.order;

/**
 * @descreption
 * @Author answer
 * @Date 2019/5/13 15 02
 */
public class OrderEstimatedRouteBo {

    private String driverId;
    private String factDate;
    private String faceEndDate;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单的预估费用
     */
    private Double estimatedAmount;

    /**
     * 实际里程(公里)
     */
    private Double travelMileage;

    /**
     * 实际时长（分钟）
     */
    private Double travelTimeShow;

    /**
     * 乘客实际支付金额
     */
    private Double actualPayAmount;


    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getFactDate() {
        return factDate;
    }

    public void setFactDate(String factDate) {
        this.factDate = factDate;
    }

    public String getFaceEndDate() {
        return faceEndDate;
    }

    public void setFaceEndDate(String faceEndDate) {
        this.faceEndDate = faceEndDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(Double estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    public Double getTravelMileage() {
        return travelMileage;
    }

    public void setTravelMileage(Double travelMileage) {
        this.travelMileage = travelMileage;
    }

    public Double getTravelTimeShow() {
        return travelTimeShow;
    }

    public void setTravelTimeShow(Double travelTimeShow) {
        this.travelTimeShow = travelTimeShow;
    }

    public Double getActualPayAmount() {
        return actualPayAmount;
    }

    public void setActualPayAmount(Double actualPayAmount) {
        this.actualPayAmount = actualPayAmount;
    }
}
