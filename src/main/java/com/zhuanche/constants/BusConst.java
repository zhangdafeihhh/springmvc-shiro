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
	DecimalFormat decimalFormat = new DecimalFormat("#########0.00");

	default String decimalFormat(BigDecimal value) {
		if (value == null) {
			return "0.00";
		}
		return decimalFormat.format(value);
	}

	// ==========================其它==============================
	/** 驾照类型 **/
	String[] DRIVING_LICENSE_TYPES = { "A1", "A2", "A3", "B1", "B2", "C1", "C2", "N", "P" };

	/**
	 * @ClassName: Pay
	 * @Description: 支付系统【重构版】(pay.01zhuanche.com)
	 * @author: yanyunpeng
	 * @date: 2018年12月12日 下午5:17:38
	 * 
	 */
	interface Pay {
		// =============================================供应商接口列表===================================================
		/** 供应商基本信息查询 **/
		String SETTLE_SUPPLIER_INFO = "/settle/supplier/info";

		/** 供应商增加基础信息 **/
		String SETTLE_SUPPLIER_INFO_ADD = "/settle/supplier/info/add";

		/** 供应商更新基础信息 **/
		String SETTLE_SUPPLIER_INFO_UPDATE = "/settle/supplier/info/update";

		/** 查询分佣协议 **/
		String SETTLE_SUPPLIER_PRORATE_DETAIL = "/settle/supplier/prorate/detail";

		/** 供应商分佣协议增加 **/
		String SETTLE_SUPPLIER_PRORATE_ADD = "/settle/supplier/prorate/add";

		/** 供应商分佣协议更新 **/
		String SETTLE_SUPPLIER_PRORATE_UPDATE = "/settle/supplier/prorate/update";

		/** 供应商分佣协议删除 **/
		String SETTLE_SUPPLIER_PRORATE_DELETE = "/settle/supplier/prorate/delete";

		/** 查找供应商返佣配置 **/
		String SETTLE_SUPPLIER_REBATE_INFO = "/settle/supplier/rebate/info";

		/** 供应商返点协议增加 **/
		String SETTLE_SUPPLIER_REBATE_ADD = "/settle/supplier/rebate/add";

		/** 供应商返点协议更新 **/
		String SETTLE_SUPPLIER_REBATE_UPDATE = "/settle/supplier/rebate/update";

		/** 供应商返点协议删除 **/
		String SETTLE_SUPPLIER_REBATE_DELETE = "/settle/supplier/rebate/delete";

		/** 根据有效比例获取供应商 **/
		String SETTLE_SUPPLIER_INFO_BY_PRORATE_RATE = "/settle/supplier/info/by/prorate/rate";

		/** 查询供应商分佣有关的信息（批量） **/
		String SETTLE_SUPPLIER_PRORATE_LIST = "/settle/supplier/prorate/list";
		
		/** 导出供应商分佣信息 **/
		String SETTLE_SUPPLIER_EXPORT_SUPPLIER_INFO = "/settle/supplier/export/supplier/info";

		// =============================================结算单接口列表===================================================
		/** 查询供应商的账单 **/
		String SETTLE_SUPPLIER_BILL_LIST = "/settle/supplier/bill/list";

		/** 查询账单信息列表（流水列表） */
		String SETTLT_DETAIL_LIST = "/settle/detail/list";

		/** 结算单或者账单信息修改 */
		String SETTLT_SUPPLIER_BILL_UPDATE = "/settle/supplier/bill/update";

		/** 供应商账单查询根据账单id **/
		String SETTLE_SUPPLIER_BILL_DETAIL = "/settle/supplier/bill/detail";

		/** 分佣流水查询根据id */
		String SETTLE_DETAIL_INFO = "/settle/detail/info";

		/** 供应商账单确认开票 **/
		String SETTLE_SUPPLIER_BILL_CONFIRM_INVOICE = "/settle/supplier/bill/confirm/invoice";

		/** 供应商账单确认打款 **/
		String SETTLE_SUPPLIER_BILL_CONFIRM_PAY = "/settle/supplier/bill/confirm/pay";

		/** 供应商账单确认打款 **/
		String SETTLE_SUPPLIER_BILL_CONFIRM_SETTLE = "/settle/supplier/bill/confirm/settle";

		/** 增加发票链接 **/
		String SETTLE_SUPPLIER_ADD_INVOICE_URL = "/settle/supplier/add/invoice/url";
		
		/** 查询发票链接 **/
		String SETTLE_SUPPLIER_QUERY_INVOICE_URL = "/settle/supplier/query/invoice/url";
	}

	/**
	 * @ClassName: Payment
	 * @Description: 支付系统【旧版】(payment.01zhuanche.com)
	 * @author: yanyunpeng
	 * @date: 2018年12月12日 下午5:24:03
	 * 
	 */
	interface Payment {
		/** 批量查询企业信息 **/
		String BUSINESS_QUERYBUSINESSINFOBATCH = "/business/queryBusinessInfoBatch";

		/** 大巴车支付明细 **/
		String BUS_PAY_DETAIL = "/pay/bus/details";
		/**订单评价*/
		String BUS_APPRAISAL = "/appraisal/queryAppraisalDetailsByOrderNos";
		/**查询企业ID*/
		String ORG_URL = "/api/v1/company/decide";
		/**查询企业折扣信息*/
		String ORG_COST_URL = "/business/queryBusinessInfo";
		/**巴士支付信息*/
		String PAY_LIST = "/pay/details/bus/list";
	}

	/**
	 * @ClassName: Charge
	 * @Description: 计费系统(charge.01zhuanche.com)
	 * @author: yanyunpeng
	 * @date: 2018年12月12日 下午5:19:56
	 * 
	 */
	interface Charge {
		/** 大巴车-批量获取费用明细 **/
		String BUSS_GETBUSCOSTDETAILLIST = "/buss/getBusCostDetailList";

		/** 费用详情 （巴士费用明细） **/
		String BUSS_BACK = "/buss/back";
	}

	/**
	 * @ClassName: BusinessRest
	 * @Description: 企业前端接口文档(business-rest.01zhuanche.com)
	 * @author: yanyunpeng
	 * @date: 2018年12月12日 下午5:52:06
	 * 
	 */
	interface BusinessRest {
		// ================================================企业信息接口列表===========================================================
		/** 根据手机号查询企业信息 **/
		String COMPANY_QUERYCOMPANYBYPHONE = "/api/v1/company/queryCompanyByPhone";
	}

	/**
	 * @ClassName: Order
	 * @Description: 新订单服务（order.01zhuanche.com）
	 * @author: yanyunpeng
	 * @date: 2018年12月12日 下午5:59:17
	 * 
	 */
	interface Order {
		/** 查询订单列表 **/
		String SELECT_ORDER_LIST = "/busOrder/selectOrderList";

		/** 服务中的司机列表 **/
		String BUS_IN_SERVICE_LIST = "/busInService/list";

		/** 巴士订单改派 **/
		String UPDATE_DRIVER = "/busOrder/updateDriver";

		/** 查询订单详情 **/
		String GET_ORDER_DETAIL = "/busOrder/getOrderDetail";
	}

	/**
	 * @ClassName: Dispatcher
	 * @Description: 派单服务(dispatcher.01zhuanche.com)
	 * @author: yanyunpeng
	 * @date: 2018年12月12日 下午6:03:34
	 * 
	 */
	interface Dispatcher {
		/** 指定司机绑单接口 **/
		String BUS_DISPATCHER = "/bus/busDispatcher";
	}

}
