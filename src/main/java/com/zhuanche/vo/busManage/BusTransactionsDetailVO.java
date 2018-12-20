package com.zhuanche.vo.busManage;

import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士流水明细回显VO
 * @author: niuzilian
 * @create: 2018-12-20 11:57
 **/
public class BusTransactionsDetailVO {
    private String supplierBillId;
    private String orderNo;
    private BigDecimal orderAmount;
    private String serviceTypeName;
    private String bookingGroupName;
    private BigDecimal settleRatio;
    private BigDecimal billAmount;
    private String licensePlates;
    private String bookingUserName;


    public String getSupplierBillId() {
        return supplierBillId;
    }

    public void setSupplierBillId(String supplierBillId) {
        this.supplierBillId = supplierBillId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getBookingGroupName() {
        return bookingGroupName;
    }

    public void setBookingGroupName(String bookingGroupName) {
        this.bookingGroupName = bookingGroupName;
    }

    public BigDecimal getSettleRatio() {
        return settleRatio;
    }

    public void setSettleRatio(BigDecimal settleRatio) {
        this.settleRatio = settleRatio;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getBookingUserName() {
        return bookingUserName;
    }

    public void setBookingUserName(String bookingUserName) {
        this.bookingUserName = bookingUserName;
    }
}
