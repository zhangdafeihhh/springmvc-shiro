package com.zhuanche.entity.driver;

import java.util.Date;

/**
 * supplier_ext
 * @author
 */
public class SupplierExtDto {
    private Long id;

    /**
     * 供应商id
     */
    private Integer supplierId;

    /**
     * 状态 1启用 0禁用
     */
    private Byte status;

    /**
     * 供应商简称
     */
    private String supplierShortName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 二级加盟类型id
     */
    private Integer twoLevelCooperation;

    /**
     * 结算类型 0 无效类型 1自主结算 2 对公结算 3其他
     */
    private Integer settlementType;

    /**
     * 结算周期 0 无效周期 1月结 2 半月结 3 周结
     */
    private Integer settlementCycle;

    /**
     * 结算日 0为无效结算日
     */
    private Integer settlementDay;

    /**
     * 打款账户名称
     */
    private String settlementAccount;

    /**
     * 打款银行账号
     */
    private String bankAccount;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 联行号
     */
    private String bankIdentify;

    /**
     * 结算供应商全称
     */
    private String settlementFullName;

    /**
     * 首次签约时间
     */
    private Date firstSignTime;

    /**
     * 税率 3%、6%、9%、10%、13%、16%
     */
    private String taxRate;

    /**
     * 发票类型 0.无效发票类型 1.专票、2.普票、3.电子发票（普票）
     */
    private Integer invoiceType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getSupplierShortName() {
        return supplierShortName;
    }

    public void setSupplierShortName(String supplierShortName) {
        this.supplierShortName = supplierShortName == null ? null : supplierShortName.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getTwoLevelCooperation() {
        return twoLevelCooperation;
    }

    public void setTwoLevelCooperation(Integer twoLevelCooperation) {
        this.twoLevelCooperation = twoLevelCooperation;
    }

    public Integer getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }

    public Integer getSettlementCycle() {
        return settlementCycle;
    }

    public void setSettlementCycle(Integer settlementCycle) {
        this.settlementCycle = settlementCycle;
    }

    public Integer getSettlementDay() {
        return settlementDay;
    }

    public void setSettlementDay(Integer settlementDay) {
        this.settlementDay = settlementDay;
    }

    public String getSettlementAccount() {
        return settlementAccount;
    }

    public void setSettlementAccount(String settlementAccount) {
        this.settlementAccount = settlementAccount == null ? null : settlementAccount.trim();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName == null ? null : bankName.trim();
    }

    public String getBankIdentify() {
        return bankIdentify;
    }

    public void setBankIdentify(String bankIdentify) {
        this.bankIdentify = bankIdentify == null ? null : bankIdentify.trim();
    }

    public String getSettlementFullName() {
        return settlementFullName;
    }

    public void setSettlementFullName(String settlementFullName) {
        this.settlementFullName = settlementFullName == null ? null : settlementFullName.trim();
    }

    public Date getFirstSignTime() {
        return firstSignTime;
    }

    public void setFirstSignTime(Date firstSignTime) {
        this.firstSignTime = firstSignTime;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate == null ? null : taxRate.trim();
    }

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }
}