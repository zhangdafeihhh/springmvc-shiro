package com.zhuanche.dto.busManage;

import java.util.List;

import com.zhuanche.common.web.datavalidate.custom.InArray;
import com.zhuanche.dto.BaseDTO;

import lombok.Data;

@Data
public class BusSupplierQueryDTO {

	// ========================接口字段=======================

	private static final long serialVersionUID = 9106573709870053672L;

	/** 城市ID **/
	private Integer cityId;

	/** 供应商ID **/
	private Integer supplierId;

	/** 状态：1.有效 0.无效 **/
	@InArray(values = { "1", "0" }, message = "状态不在有效状态范围内")
	private Integer status;
	
	/** 结算比例 **/
	private Double supplierRate;

	// ========================业务字段=========================

	/** 某分佣比例下的供应商ids **/
	private List<Integer> supplierIds;


}