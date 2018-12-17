package com.zhuanche.entity.busManage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * @program: car-manage
 * @description:
 * @author: niuzilian
 * @create: 2018-10-15 17:50
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaidOrderEntity {
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 预支付金额
     */
    private BigDecimal prePayAmount;
    /**
     * 高速费
     */
    private BigDecimal highWayFee;
    /**
     * 停车费
     */
    private BigDecimal parkFee;
    /**
     * 结算比例
     */
    private Double settleRatio;
    /**
     * 餐补费
     */
    private BigDecimal foodFee;
    /**
     * 住宿费
     */
    private BigDecimal hotelFee;
    /**
     * 其他费用
     */
    private BigDecimal otherFee;
    /**
     * 超预估费
     */
    private BigDecimal outOfPreEstimateAmount;
    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;
    /**
     * 代收总金额
     */
    private BigDecimal settleAmount;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPrePayAmount() {
        return prePayAmount;
    }

    public void setPrePayAmount(BigDecimal prePayAmount) {
        this.prePayAmount = prePayAmount;
    }

    public BigDecimal getHighWayFee() {
        return highWayFee;
    }

    public void setHighWayFee(BigDecimal highWayFee) {
        this.highWayFee = highWayFee;
    }

    public BigDecimal getParkFee() {
        return parkFee;
    }

    public void setParkFee(BigDecimal parkFee) {
        this.parkFee = parkFee;
    }

    public Double getSettleRatio() {
        return settleRatio;
    }

    public void setSettleRatio(Double settleRatio) {
        this.settleRatio = settleRatio;
    }

    public BigDecimal getFoodFee() {
        return foodFee;
    }

    public void setFoodFee(BigDecimal foodFee) {
        this.foodFee = foodFee;
    }

    public BigDecimal getHotelFee() {
        return hotelFee;
    }

    public void setHotelFee(BigDecimal hotelFee) {
        this.hotelFee = hotelFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getOutOfPreEstimateAmount() {
        return outOfPreEstimateAmount;
    }

    public void setOutOfPreEstimateAmount(BigDecimal outOfPreEstimateAmount) {
        this.outOfPreEstimateAmount = outOfPreEstimateAmount;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }
}
