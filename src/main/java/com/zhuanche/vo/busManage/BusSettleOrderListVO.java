package com.zhuanche.vo.busManage;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士供应商 分佣账单流水 列表返回的VO
 * @author: niuzilian
 * @create: 2018-12-19 18:26
 **/
public class BusSettleOrderListVO {
    private Integer id;
    private String orderNo;
    private BigDecimal settleAmount;
    private String createDate;
    private Integer accountType;
    //订单创建时间
    private Date orderCreateDate;
    //预约用车时间
    private Date bookingDate;
    //预约上车地点
    private String bookingStartAddr;
    //预约下车地点
    private String bookingEndAddr;
    //司机姓名
    private String driverName;
    //服务类型
    private String serviceName;
    //预约车型类别
    private String bookingGroupName;
    //预估金额
    private Double estimatedAmountYuan;
    //订单总金额
 	private BigDecimal amount;

    private String orderDetail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        this.orderDetail = orderDetail;
    }

    public Double getEstimatedAmountYuan() {
        return estimatedAmountYuan;
    }

    public void setEstimatedAmountYuan(Double estimatedAmountYuan) {
        this.estimatedAmountYuan = estimatedAmountYuan;
    }

    public Date getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(Date orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingStartAddr() {
        return bookingStartAddr;
    }

    public void setBookingStartAddr(String bookingStartAddr) {
        this.bookingStartAddr = bookingStartAddr;
    }

    public String getBookingEndAddr() {
        return bookingEndAddr;
    }

    public void setBookingEndAddr(String bookingEndAddr) {
        this.bookingEndAddr = bookingEndAddr;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBookingGroupName() {
        return bookingGroupName;
    }

    public void setBookingGroupName(String bookingGroupName) {
        this.bookingGroupName = bookingGroupName;
    }

    public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
    public String toString() {
        //http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=28755726
        String typeName = "";
        if (accountType == 5) {
            typeName = "巴士分佣收入";
        } else if (accountType == 6) {
            typeName = "修正订单";
        } else if (accountType == 7) {
            typeName = "修正账单";
        }else{
            typeName="未知类型";
        }
        return orderNo + "," + settleAmount + ",\t" + createDate +","+typeName;
    }
}
