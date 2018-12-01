package com.zhuanche.dto.busManage;

import com.zhuanche.common.web.datavalidate.custom.InArray;
import com.zhuanche.dto.BaseDTO;

public class BusSupplierQueryDTO extends BaseDTO {

	// ========================接口字段=======================

	private static final long serialVersionUID = 9106573709870053672L;

	/** 城市ID **/
	private Integer cityId;

	/** 供应商ID **/
	private Integer supplierId;

	/** 状态：1.有效 2.无效 **/
	@InArray(values = { "1", "2" }, message = "状态不在有效状态范围内")
	private Integer status;

	// TODO 分佣比例

	// ========================业务字段=========================

	// =======================getter/setter====================

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}