package com.zhuanche.dto.busManage;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * @ClassName: BusSettlementInvoiceVO
 * @Description: 结算单确认收票信息保存
 * @author: yanyunpeng
 * @date: 2018年12月19日 下午2:29:14
 * 
 */
public class BusSettlementInvoiceDTO {

	/** 账单编号 **/
	@NotBlank(message = "账单编号不能为空")
	private String supplierBillId;

	/** 发票金额 **/
	@NotNull(message = "发票金额不能为空")
	@DecimalMin(value = "0", inclusive = false, message = "发票金额必须大于0")
	private BigDecimal invoiceAmount;

	/** 开票日期 **/
	@NotNull(message = "开票日期不能为空")
	@DateTimeFormat(iso = ISO.DATE)
	private Date invoiceDate;

	/** 开票人 **/
	private String invoiceName;

	public String getSupplierBillId() {
		return supplierBillId;
	}

	public void setSupplierBillId(String supplierBillId) {
		this.supplierBillId = supplierBillId;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceName() {
		return invoiceName;
	}

	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}

}
