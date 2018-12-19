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
 * @Description: 结算单确认收款窗口保存
 * @author: yanyunpeng
 * @date: 2018年12月19日 下午2:29:14
 * 
 */
public class BusSettlementPaymentDTO {

	/** 账单编号 **/
	@NotBlank(message = "账单编号不能为空")
	private String supplierBillId;

	/** 付款金额 **/
	@NotNull(message = "付款金额不能为空")
	@DecimalMin(value = "0", inclusive = false, message = "付款金额必须大于0")
	private BigDecimal payAmount;

	/** 付款日期 **/
	@NotNull(message = "付款日期不能为空")
	@DateTimeFormat(iso = ISO.DATE)
	private Date payDate;

	/** 开票人 **/
	private String payName;

	public String getSupplierBillId() {
		return supplierBillId;
	}

	public void setSupplierBillId(String supplierBillId) {
		this.supplierBillId = supplierBillId;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

}
