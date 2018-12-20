package com.zhuanche.vo.busManage;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName: BusSettlementInvoiceVO
 * @Description: 结算单确认收款信息
 * @author: yanyunpeng
 * @date: 2018年12月19日 下午2:29:14
 * 
 */
public class BusSettlementPaymentVO {

	/** 供应商名称 **/
	private String supplierName;

	/** 账单周期开始日期 **/
	private Date startTime;

	/** 账单周期结束日期 **/
	private Date endTime;

	/** 账单编号 **/
	private String supplierBillId;

	/** 账单金额 **/
	private BigDecimal billAmount;

	/** 付款金额 **/
	private BigDecimal payAmount;

	/** 付款日期 **/
	private Date payDate;

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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

	public String getSupplierBillId() {
		return supplierBillId;
	}

	public void setSupplierBillId(String supplierBillId) {
		this.supplierBillId = supplierBillId;
	}

	public BigDecimal getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
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

}
