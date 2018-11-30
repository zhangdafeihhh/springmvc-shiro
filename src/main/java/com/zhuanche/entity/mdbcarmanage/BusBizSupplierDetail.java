package com.zhuanche.entity.mdbcarmanage;

import java.math.BigDecimal;
import java.util.Date;

public class BusBizSupplierDetail {
    private Integer id;

    private Integer supplierId;

    private Integer createBy;

    private Integer updateBy;

    private Date createDate;

    private Date updateDate;

    private BigDecimal deposit;

    private BigDecimal franchiseFee;

    private String invoiceCompanyName;

    private String invoiceCompanyPhone;

    private String invoiceCompanyAddr;

    private String invoiceDepositBank;

    private String invoiceBankAccount;

    private String invoiceDutyParagraph;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
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

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getFranchiseFee() {
        return franchiseFee;
    }

    public void setFranchiseFee(BigDecimal franchiseFee) {
        this.franchiseFee = franchiseFee;
    }

    public String getInvoiceCompanyName() {
        return invoiceCompanyName;
    }

    public void setInvoiceCompanyName(String invoiceCompanyName) {
        this.invoiceCompanyName = invoiceCompanyName == null ? null : invoiceCompanyName.trim();
    }

    public String getInvoiceCompanyPhone() {
        return invoiceCompanyPhone;
    }

    public void setInvoiceCompanyPhone(String invoiceCompanyPhone) {
        this.invoiceCompanyPhone = invoiceCompanyPhone == null ? null : invoiceCompanyPhone.trim();
    }

    public String getInvoiceCompanyAddr() {
        return invoiceCompanyAddr;
    }

    public void setInvoiceCompanyAddr(String invoiceCompanyAddr) {
        this.invoiceCompanyAddr = invoiceCompanyAddr == null ? null : invoiceCompanyAddr.trim();
    }

    public String getInvoiceDepositBank() {
        return invoiceDepositBank;
    }

    public void setInvoiceDepositBank(String invoiceDepositBank) {
        this.invoiceDepositBank = invoiceDepositBank == null ? null : invoiceDepositBank.trim();
    }

    public String getInvoiceBankAccount() {
        return invoiceBankAccount;
    }

    public void setInvoiceBankAccount(String invoiceBankAccount) {
        this.invoiceBankAccount = invoiceBankAccount == null ? null : invoiceBankAccount.trim();
    }

    public String getInvoiceDutyParagraph() {
        return invoiceDutyParagraph;
    }

    public void setInvoiceDutyParagraph(String invoiceDutyParagraph) {
        this.invoiceDutyParagraph = invoiceDutyParagraph == null ? null : invoiceDutyParagraph.trim();
    }
}