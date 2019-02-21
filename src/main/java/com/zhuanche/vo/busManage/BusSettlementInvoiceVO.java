package com.zhuanche.vo.busManage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @ClassName: BusSettlementInvoiceVO
 * @Description: 结算单确认收票信息
 * @author: yanyunpeng
 * @date: 2018年12月19日 下午2:29:14
 * 
 */
@Data
public class BusSettlementInvoiceVO {

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

	/** 发票金额 **/
	private BigDecimal invoiceAmount;

	/** 开票日期 **/
	private Date invoiceDate;

	/** 发票附件地址 **/
	private List<Map<Object, Object>> invoiceFiles;

}
