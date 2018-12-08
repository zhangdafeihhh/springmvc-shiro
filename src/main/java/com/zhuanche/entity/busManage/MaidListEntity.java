package com.zhuanche.entity.busManage;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @program: car-manage
 * @description: 巴士分佣实体类
 * @author: niuzilian
 * @create: 2018-09-17 10:45
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaidListEntity {
    /**
     * 账户ID
     */
    private String accountId;
    /**
     * 城市id
     */
    private Integer cityCode;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 参与分佣金额
     */
    private BigDecimal settleAmount;
    /**
     * 结算创建时间，yyyy-MM-dd HH:mm:ss
     */
    private String settleDate;
    /**
     * 分佣比例，例：百分之二十，如20
     */
    private BigDecimal settleRatio;
    /**
     * 分佣结果
     */
    private BigDecimal settleResult;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    private String orderDetail;

    private MaidOrderEntity orderDetailJSON;


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getSettleRatio() {
        return settleRatio;
    }

    public void setSettleRatio(BigDecimal settleRatio) {
        this.settleRatio = settleRatio;
    }

    public BigDecimal getSettleResult() {
        return settleResult;
    }

    public void setSettleResult(BigDecimal settleResult) {
        this.settleResult = settleResult;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        this.orderDetail = orderDetail;
    }

    public MaidOrderEntity getOrderDetailJSON() {
        return orderDetailJSON;
    }

    public void setOrderDetailJSON(MaidOrderEntity orderDetailJSON) {
        this.orderDetailJSON = orderDetailJSON;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        if (orderDetail != null) {
            setOrderDetailJSON(JSONObject.parseObject(orderDetail, MaidOrderEntity.class));
            if (orderDetailJSON.getOrderAmount() != null && orderDetailJSON.getPrePayAmount() != null) {
                orderDetailJSON.setSettleAmount(orderDetailJSON.getOrderAmount().subtract(orderDetailJSON.getPrePayAmount()));
            }
        }

        return StringUtils.defaultString(orderNo) + "," + cityName + ",\t" + phone + ",\t" + settleDate + ","
                + orderDetailJSON.getOrderAmount() + "," + orderDetailJSON.getPrePayAmount() + ","
                + orderDetailJSON.getSettleAmount() + "," + orderDetailJSON.getHighWayFee() + ","
                + orderDetailJSON.getParkFee() + "," + orderDetailJSON.getSettleRatio() + ","
                + settleAmount;
    }
}