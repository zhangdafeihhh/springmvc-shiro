package com.zhuanche.dto.AdvancePaymentDTO;

import java.io.Serializable;

/**
 * Created by 郭宏光 on 2019/5/28.
 */
public class OrderShortDTO implements Serializable {
    private Long orderId;
    private Integer status;
    private String orderNo;
    private String bookingDate;
    private Integer driverId;
    private Integer serviceTypeId;
    private Integer orderType;
    private Long factEndDate;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getFactEndDate() {
        return factEndDate;
    }

    public void setFactEndDate(Long factEndDate) {
        this.factEndDate = factEndDate;
    }
}
