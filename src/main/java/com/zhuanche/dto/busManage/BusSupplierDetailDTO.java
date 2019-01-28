package com.zhuanche.dto.busManage;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @ClassName: BusSupplierDetailDTO
 * @Description: 巴士供应商其它信息
 * @author: yanyunpeng
 * @date: 2018年11月30日 下午2:42:13
 * 
 */
public class BusSupplierDetailDTO implements BusSupplierDTO {

	// ========================接口字段=======================
	/** 供应商ID **/
	@NotNull(message = "供应商ID不能为空", groups = Update.class)
	private Integer supplierId;

	/** 保证金 **/
	@NotNull(message = "保证金不能为空")
	@DecimalMin(value = "0", inclusive = true, message = "保证金必须大于或等于0元")
	@DecimalMax(value = "9999999999.99", message = "保证金必须小于 9999999999.99 元")
	private BigDecimal deposit;

	/** 加盟费 **/
	@NotNull(message = "加盟费不能为空")
	@DecimalMin(value = "0", inclusive = true, message = "加盟费必须大于或等于0元")
	@DecimalMax(value = "9999999999.99", message = "加盟费必须小于 9999999999.99 元")
	private BigDecimal franchiseFee;

	/** 合同开始日期 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message = "合同开始日期不能为空")
	private Date contractDateStart;

	/** 合同结束日期 **/
	@NotNull(message = "合同结束日期不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date contractDateEnd;

	/** 公司名称(发票信息) **/
	@NotBlank(message = "公司名称不能为空")
	@Size(max = 100, message = "公司名称长度不能超过255")
	private String invoiceCompanyName;

	/** 电话号码(发票信息) **/
	@Size(max = 100, message = "电话号码长度不能超过30")
	private String invoiceCompanyPhone;

	/** 公司地址(发票信息) **/
	@NotBlank(message = "公司地址不能为空")
	@Size(max = 100, message = "公司地址长度不能超过255")
	private String invoiceCompanyAddr;

	/** 开户银行(发票信息) **/
	@NotBlank(message = "开户银行不能为空")
	@Size(max = 100, message = "开户银行长度不能超过255")
	private String invoiceDepositBank;

	/** 银行账户(发票信息) **/
	@NotBlank(message = "银行账户不能为空")
	@Size(max = 100, message = "银行账户长度不能超过50")
	private String invoiceBankAccount;

	/** 税号(发票信息) **/
	@NotBlank(message = "税号不能为空")
	@Size(max = 100, message = "税号长度不能超过50")
	private String invoiceDutyParagraph;

	// ========================业务字段=========================
	/** 创建人ID **/
	private Integer createBy;

	/** 修改人ID **/
	private Integer updateBy;

	/** 创建时间 **/
	private Date createDate;

	/** 修改时间 **/
	private Date updateDate;

	// =======================getter/setter====================

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
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

	public Date getContractDateStart() {
		return contractDateStart;
	}

	public void setContractDateStart(Date contractDateStart) {
		this.contractDateStart = contractDateStart;
	}

	public Date getContractDateEnd() {
		return contractDateEnd;
	}

	public void setContractDateEnd(Date contractDateEnd) {
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

}
