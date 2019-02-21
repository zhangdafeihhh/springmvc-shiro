package com.zhuanche.vo.busManage;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 用于结算单修改页面的回显
 * @author: niuzilian
 * @create: 2018-12-20 00:10
 **/
public class BusSettlementUdateInitVO {
    private String supplierBillId;
    private Date startTime;
    private Date endTime;
    private String supplierName;
    private BigDecimal billAmount;

    public String getSupplierBillId() {
        return supplierBillId;
    }

    public void setSupplierBillId(String supplierBillId) {
        this.supplierBillId = supplierBillId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }
}
