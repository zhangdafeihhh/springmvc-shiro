package com.zhuanche.vo.busManage;

import lombok.Data;

/**
 * @ClassName: BusSupplierExportVO
 * @Description: 巴士供应商信息VO
 * @author: yanyunpeng
 * @date: 2018年12月4日 下午4:38:15
 * 
 */
@Data
public class BusSupplierExportVO {

	/** 城市 */
	private String cityName;

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商 **/
	private String supplierName;

	/** 状态：1.有效 0.无效 **/
	private Integer status;

	/** 企业联系人 **/
	private String contacts;

	/** 企业联系人电话 **/
	private String contactsPhone;

	/** 调度员电话 **/
	private String dispatcherPhone;

	/** 加盟类型:car_biz_cooperation_type **/
	private String cooperationName;

	// ======================结算信息======================

	/** 分佣比例 **/
	private Double supplierRate;

	/** 是否有返点 0不返点 1返点 **/
	private Integer isRebate;

}
