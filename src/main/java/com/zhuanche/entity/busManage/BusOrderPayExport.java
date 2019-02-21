package com.zhuanche.entity.busManage;

import java.math.BigDecimal;

/**
 * @program: car-manage
 * @description:
 * @author: admin
 * @create: 2018-08-20 16:14
 **/
public class BusOrderPayExport {
    /**
     * 	创建日期
     */
    private String createDate;
    /**
     * 支付日期（已完成订单才有）
     */
    private String finishDate;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 	业务订单号
     */
    private String 	tradeOrderNo;
    /**
     * 支付金额（实际支付=订单原始金额-折扣金额）
     */
    private BigDecimal payAmt;
    /**
     * 订单原始金额
     */
    private BigDecimal payOrderAmt;
    /**
     * 详见：支付状态
     */
    private String payStatus;
    /**
     * 	支付类型
     */
    private String payToolName;
    /**
     * 	子支付类型
     */
    private String paySubToolName;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTradeOrderNo() {
        return tradeOrderNo;
    }

    public void setTradeOrderNo(String tradeOrderNo) {
        this.tradeOrderNo = tradeOrderNo;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public BigDecimal getPayOrderAmt() {
        return payOrderAmt;
    }

    public void setPayOrderAmt(BigDecimal payOrderAmt) {
        this.payOrderAmt = payOrderAmt;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayToolName() {
        return payToolName;
    }

    public void setPayToolName(String payToolName) {
        this.payToolName = payToolName;
    }

    public String getPaySubToolName() {
        return paySubToolName;
    }

    public void setPaySubToolName(String paySubToolName) {
        this.paySubToolName = paySubToolName;
    }
}
