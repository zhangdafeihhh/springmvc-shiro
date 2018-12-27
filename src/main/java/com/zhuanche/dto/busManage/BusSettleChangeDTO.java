package com.zhuanche.dto.busManage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士供应商结算单修正DTO
 * @author: niuzilian
 * @create: 2018-12-19 10:03
 **/
public class BusSettleChangeDTO{
    @NotBlank(message = "账单ID不能为空")
    private String supplierBillId;
    @NotNull(message = "供应商ID不能为空")
    private Integer supplierId;
    @NotNull(message = "金额不能为空")
    private BigDecimal addMoney;
    @NotNull(message = "修改原因不能为空")
    private Integer reasonCode;
    private String desc;

    public String getSupplierBillId() {
        return supplierBillId;
    }

    public void setSupplierBillId(String supplierBillId) {
        this.supplierBillId = supplierBillId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
