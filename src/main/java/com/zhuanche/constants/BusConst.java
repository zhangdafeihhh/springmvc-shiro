package com.zhuanche.constants;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

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

	// ========================DecimalFormat========================
	default String decimalFormat(BigDecimal value) {
		if (value == null) {
			return "0.00";
		}
		DecimalFormat format = new DecimalFormat("#########0.00");
		return format.format(value);
	}
	// ==========================其它==============================
	/** 驾照类型 **/
	String[] DRIVING_LICENSE_TYPES = { "A1", "A2", "A3", "B1", "B2", "C1", "C2", "N", "P" };
	
	/**
	 * @ClassName:  SettlementAdvice
	 * @Description: 结算单接口列表
	 * @author: yanyunpeng
	 * @date:   2018年12月7日 上午11:37:41
	 */
	interface SettlementAdviceRemote{
		/** 查询供应商的账单 **/
		String SETTLE_DETAIL_LIST = "/settle/detail/list";
		
		/** 查询供应商分佣有关的信息（批量） **/
		String SETTLE_SUPPLIER_PRORATE_LIST = "/settle/supplier/prorate/list";
	}

}
