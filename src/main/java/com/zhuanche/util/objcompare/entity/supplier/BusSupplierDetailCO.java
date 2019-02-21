package com.zhuanche.util.objcompare.entity.supplier;

import java.math.BigDecimal;
import java.util.Date;

import com.zhuanche.util.objcompare.FieldNote;

import lombok.Data;

@Data
public class BusSupplierDetailCO {

	@FieldNote("保证金")
	private BigDecimal deposit;

	@FieldNote("加盟费")
	private BigDecimal franchiseFee;

	@FieldNote("合同开始日期")
	private Date contractDateStart;

	@FieldNote("合同结束日期")
	private Date contractDateEnd;

	@FieldNote("账户名称")
	private String invoiceCompanyName;

	@FieldNote("开户银行")
	private String invoiceDepositBank;

	@FieldNote("银行账户")
	private String invoiceBankAccount;

	@FieldNote("电话号码")
	private String invoiceCompanyPhone;

	@FieldNote("税号")
	private String invoiceDutyParagraph;

	@FieldNote("单位地址")
	private String invoiceCompanyAddr;

}