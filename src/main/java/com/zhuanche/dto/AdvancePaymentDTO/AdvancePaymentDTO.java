package com.zhuanche.dto.AdvancePaymentDTO;

import com.zhuanche.dto.BaseDTO;

import java.math.BigDecimal;

/**
 * Created by ghg on 2019/7/1.
 */
public class AdvancePaymentDTO extends BaseDTO {

    //司机id
    private Integer driverId;
    //订单号
    private String tradeOrderNo;
    //垫付金额
    private Number money;
    //垫付状态 0 失败 1成功
    private Integer status;
    //垫付类型
    private String platformPayType;
    //垫付时间
    private String createTime;
    //垫付人
    private String createName;
    //垫付原因
    private String reason;
    //订单完成时间
    private String orderFinishiTime;
    //订单原始金额
    private BigDecimal orderMoney;
    //城市
    private String cityName;
    //供应商
    private String supplierName;
    //司机姓名
    private String driverName;
    //司机手机号
    private String driverPhone;
    //垫付备注
    private String advanceRemark;
    //申请垫付时间
    private String applyAdvanceTime;
    //订单完成开始时间
    private String startTimeStr;
    //订单完成结束时间
    private String endTimeStr;
    //订单id
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getTradeOrderNo() {
        return tradeOrderNo;
    }

    public void setTradeOrderNo(String tradeOrderNo) {
        this.tradeOrderNo = tradeOrderNo;
    }

    public Number getMoney() {
        return money;
    }

    public void setMoney(Number money) {
        this.money = money;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPlatformPayType() {
        return platformPayType;
    }

    public void setPlatformPayType(String platformPayType) {
        this.platformPayType = platformPayType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOrderFinishiTime() {
        return orderFinishiTime;
    }

    public void setOrderFinishiTime(String orderFinishiTime) {
        this.orderFinishiTime = orderFinishiTime;
    }

    public BigDecimal getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(BigDecimal orderMoney) {
        this.orderMoney = orderMoney;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getAdvanceRemark() {
        return advanceRemark;
    }

    public void setAdvanceRemark(String advanceRemark) {
        this.advanceRemark = advanceRemark;
    }

    public String getApplyAdvanceTime() {
        return applyAdvanceTime;
    }

    public void setApplyAdvanceTime(String applyAdvanceTime) {
        this.applyAdvanceTime = applyAdvanceTime;
    }
}
