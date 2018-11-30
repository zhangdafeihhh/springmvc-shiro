package com.zhuanche.constants;

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
	DateTimeFormatter FORMATTER_DATE_BY_HYPHEN= DateTimeFormatter.ofPattern("yyyy-MM-dd");

	
	// ==========================其它==============================
	/** 驾照类型 **/
	String[] DRIVING_LICENSE_TYPES = { "A1", "A2", "A3", "B1", "B2", "C1", "C2", "N", "P" };

}
