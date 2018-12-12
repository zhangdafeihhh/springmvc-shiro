package com.zhuanche.constants;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @ClassName: BusConst
 * @Description: 巴士常量值
 * @author: yanyunpeng
 * @date: 2018年11月26日 下午2:27:14
 * 
 */
public interface BusConst {

	// ========================日期正则========================

	/** 日期(yyyy-MM-dd) **/
	String PATTERN_DATE_BY_HYPHEN = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";

	// ========================日期格式化器========================
	DateTimeFormatter FORMATTER_DATE_BY_HYPHEN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	default String formatDate(DateTimeFormatter formatter, Date date) {
		if (formatter == null || date == null) {
			return "";
		}
		return formatter.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
	}

	// ========================DecimalFormat========================
    DecimalFormat format = new DecimalFormat("##########.00");

	default String decimalFormat(BigDecimal value) {
		if (value == null) {
			return "0.00";
		}
		return format.format(value);
	}
	// ==========================其它==============================
	/** 驾照类型 **/
	String[] DRIVING_LICENSE_TYPES = { "A1", "A2", "A3", "B1", "B2", "C1", "C2", "N", "P" };
	
	// =============================================结算单接口列表===================================================
	/** 供应商基本信息查询 **/
	String SETTLE_SUPPLIER_INFO = "/settle/supplier/info";
	
	/** 查询分佣协议 **/
	String SETTLE_SUPPLIER_PRORATE_INFO_LIST = "/settle/supplier/prorate/info/list";
	
	/** 查找供应商返佣配置 **/
	String SETTLE_SUPPLIER_REBATE_INFO = "/settle/supplier/rebate/info";

	/** 查询供应商分佣有关的信息（批量） **/
	String SETTLE_SUPPLIER_PRORATE_LIST = "/settle/supplier/prorate/list";

	/** 查询供应商的账单 **/
	String SETTLE_DETAIL_LIST = "/settle/detail/list";

}
