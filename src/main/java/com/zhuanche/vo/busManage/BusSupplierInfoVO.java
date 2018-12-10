package com.zhuanche.vo.busManage;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName: BusSupplierInfoVO
 * @Description: 供应商详情
 * @author: yanyunpeng
 * @date: 2018年12月5日 上午10:07:18
 * 
 */
public class BusSupplierInfoVO {

	/** 供应商Id,修改时必传 **/
	private Integer supplierId;

	/** 供应商名称 **/
	private String supplierName;

	/** 所属城市ID **/
	private Integer supplierCity;

	/** 城市名称 **/
	private String cityName;

	/** 企业联系人 **/
	private String contacts;

	/** 企业联系人电话 **/
	private String contactsPhone;

	/** 调度员电话 **/
	private String dispatcherPhone;

	/** 加盟类型 **/
	private Integer cooperationType;

	/** 状态：1. 有效 0. 无效 **/
	private Integer status;

	/** 保证金 **/
	private BigDecimal deposit;

	/** 加盟费 **/
	private BigDecimal franchiseFee;

	/** 合同开始日期(yyyy-MM-dd HH:mm:ss) **/
	private String contractDateStart;

	/** 合同结束日期(yyyy-MM-dd HH:mm:ss) **/
	private String contractDateEnd;

	/** 公司名称 **/
	private String invoiceCompanyName;

	/** 电话号码 **/
	private String invoiceCompanyPhone;

	/** 公司地址 **/
	private String invoiceCompanyAddr;

	/** 开户银行 **/
	private String invoiceDepositBank;

	/** 银行账户 **/
	private String invoiceBankAccount;

	/** 税号 **/
	private String invoiceDutyParagraph;

	/** 供应商基本信息 **/
	private JSON commissionInfo;

	/** 供应商返佣配置 **/
	private JSON rebateInfo;

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getSupplierCity() {
		return supplierCity;
	}

	public void setSupplierCity(Integer supplierCity) {
		this.supplierCity = supplierCity;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getContactsPhone() {
		return contactsPhone;
	}

	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}

	public String getDispatcherPhone() {
		return dispatcherPhone;
	}

	public void setDispatcherPhone(String dispatcherPhone) {
		this.dispatcherPhone = dispatcherPhone;
	}

	public Integer getCooperationType() {
		return cooperationType;
	}

	public void setCooperationType(Integer cooperationType) {
		this.cooperationType = cooperationType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getContractDateStart() {
		return contractDateStart;
	}

	public void setContractDateStart(String contractDateStart) {
		this.contractDateStart = contractDateStart;
	}

	public String getContractDateEnd() {
		return contractDateEnd;
	}

	public void setContractDateEnd(String contractDateEnd) {
		this.contractDateEnd = contractDateEnd;
	}

	public String getInvoiceCompanyName() {
		return invoiceCompanyName;
	}

	public void setInvoiceCompanyName(String invoiceCompanyName) {
		this.invoiceCompanyName = invoiceCompanyName;
	}

	public String getInvoiceCompanyPhone() {
		return invoiceCompanyPhone;
	}

	public void setInvoiceCompanyPhone(String invoiceCompanyPhone) {
		this.invoiceCompanyPhone = invoiceCompanyPhone;
	}

	public String getInvoiceCompanyAddr() {
		return invoiceCompanyAddr;
	}

	public void setInvoiceCompanyAddr(String invoiceCompanyAddr) {
		this.invoiceCompanyAddr = invoiceCompanyAddr;
	}

	public String getInvoiceDepositBank() {
		return invoiceDepositBank;
	}

	public void setInvoiceDepositBank(String invoiceDepositBank) {
		this.invoiceDepositBank = invoiceDepositBank;
	}

	public String getInvoiceBankAccount() {
		return invoiceBankAccount;
	}

	public void setInvoiceBankAccount(String invoiceBankAccount) {
		this.invoiceBankAccount = invoiceBankAccount;
	}

	public String getInvoiceDutyParagraph() {
		return invoiceDutyParagraph;
	}

	public void setInvoiceDutyParagraph(String invoiceDutyParagraph) {
		this.invoiceDutyParagraph = invoiceDutyParagraph;
	}

	public JSON getCommissionInfo() {
		return commissionInfo;
	}

	public void setCommissionInfo(JSON commissionInfo) {
		this.commissionInfo = commissionInfo;
	}

	public JSON getRebateInfo() {
		return rebateInfo;
	}

	public void setRebateInfo(JSON rebateInfo) {
		this.rebateInfo = rebateInfo;
	}

}
