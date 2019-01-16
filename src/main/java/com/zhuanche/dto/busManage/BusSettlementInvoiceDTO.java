package com.zhuanche.dto.busManage;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * @ClassName: BusSettlementInvoiceVO
 * @Description: 结算单确认收票信息保存
 * @author: yanyunpeng
 * @date: 2018年12月19日 下午2:29:14
 * 
 */
@Data
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

	/** 发票附件 **/
	private MultipartFile invoiceFile;

	/** 开票人 **/
	private String invoiceName;

}
