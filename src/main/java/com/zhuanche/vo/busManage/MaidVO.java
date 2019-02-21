package com.zhuanche.vo.busManage;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 分佣明细
 * @author: niuzilian
 * @create: 2018-12-10 14:04
 **/
public class MaidVO implements Serializable {
    /**
     * 订单号
     */
    private Integer orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 城市ID
     */
    private Integer cityCode;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 订单完成时间
     */
    private String settleDate;
    /**
     * 订单总金额
     */
    private BigDecimal orderAmount;
    /**
     * 预付金额
     */
    private BigDecimal prePayAmount;
    /**
     * 代收金额
     */
    private BigDecimal proxyAmount;

    /**
     * 高速费
     */
    private BigDecimal highWayFee;

    /**
     * 停车费
     */
    private BigDecimal parkFee;
    /**
     * 住宿费
     */
    private BigDecimal hotelFee;
    /**
     * 餐饮费
     */
    private BigDecimal foodFee;
    /**
     * 抽佣比例
     */
    private Double settleRatio;

    /**
     * 司机实际收入
     */
    private BigDecimal settleAmount;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getPrePayAmount() {
        return prePayAmount;
    }

    public void setPrePayAmount(BigDecimal prePayAmount) {
        this.prePayAmount = prePayAmount;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getProxyAmount() {
        return proxyAmount;
    }

    public BigDecimal getHotelFee() {
        return hotelFee;
    }

    public void setHotelFee(BigDecimal hotelFee) {
        this.hotelFee = hotelFee;
    }

    public BigDecimal getFoodFee() {
        return foodFee;
    }

    public void setFoodFee(BigDecimal foodFee) {
        this.foodFee = foodFee;
    }

    public void setProxyAmount(BigDecimal proxyAmount) {
        this.proxyAmount = proxyAmount;
    }

    @Override
    public String toString() {
        //String MAID_EXPORT_HEAD="订单号,城市名称,手机号,结算时间,订单总金额,预付金额,代收金额,高速费,停车费,住宿费,餐饮费,抽佣比例（%）,司机实际收入";
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.defaultString(orderNo)).append(",")
                .append(StringUtils.defaultString(cityName)).append(",\t")
                .append(StringUtils.defaultString(phone)).append(",\t")
                .append(StringUtils.defaultString(settleDate)).append(",")
                .append(orderAmount == null ? "0" : orderAmount).append(",")
                .append(prePayAmount == null ? "0" : prePayAmount).append(",")
                .append(proxyAmount == null ? "0" : proxyAmount).append(",")
                .append(highWayFee == null ? "0" : highWayFee).append(",")
                .append(parkFee == null ? "0" : parkFee).append(",")
                .append(hotelFee == null ? "0" : hotelFee).append(",")
                .append(foodFee == null ? "0" : foodFee).append(",")
                .append(settleRatio == null ? "0" : settleRatio).append(",")
                .append(settleAmount == null ? "0" : settleAmount);
        return sb.toString();

    }
}
