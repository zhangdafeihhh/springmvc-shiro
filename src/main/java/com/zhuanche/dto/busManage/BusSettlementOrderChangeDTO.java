package com.zhuanche.dto.busManage;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士供应商订单流水修正
 * @author: niuzilian
 * @create: 2018-12-18 14:18
 **/
public class BusSettlementOrderChangeDTO {
    @NotNull(message = "账单ID不能为空")
    private String supplierBillId;
    @NotNull(message = "供应商ID不能为空")
    private Integer supplierId;
    @NotNull(message = "订单号不能为空")
    private String orderNo;
    @NotNull(message = "金额不能为空")
    private BigDecimal addMoney;
    @NotNull(message = "修改原因不能为空")
    private Integer reasonCode;
    @NotNull(message = "分佣方式不能为空")
    private Integer settleWay;
    private String desc;

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

    public BigDecimal getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(BigDecimal addMoney) {
        this.addMoney = addMoney;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Integer getSettleWay() {
        return settleWay;
    }

    public void setSettleWay(Integer settleWay) {
        this.settleWay = settleWay;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
}
