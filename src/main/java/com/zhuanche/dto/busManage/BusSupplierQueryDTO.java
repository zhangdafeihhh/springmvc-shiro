package com.zhuanche.dto.busManage;

import java.util.List;

import com.zhuanche.common.web.datavalidate.custom.InArray;
import com.zhuanche.dto.BaseDTO;

public class BusSupplierQueryDTO extends BaseDTO {

	// ========================接口字段=======================

	private static final long serialVersionUID = 9106573709870053672L;

	/** 城市ID **/
	private Integer cityId;

	/** 供应商ID **/
	private Integer supplierId;

	/** 状态：1.有效 0.无效 **/
	@InArray(values = { "1", "0" }, message = "状态不在有效状态范围内")
	private Integer status;

	// ========================业务字段=========================

	/** 某分佣比例下的供应商ids **/
	private List<Integer> commissionIds;

	/** 合同快到期的供应商ids **/
	private List<Integer> contractIds;

	private List<Integer> excludeContractIds;

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

	public List<Integer> getCommissionIds() {
		return commissionIds;
	}

	public void setCommissionIds(List<Integer> commissionIds) {
		this.commissionIds = commissionIds;
	}

	public List<Integer> getContractIds() {
		return contractIds;
	}

	public void setContractIds(List<Integer> contractIds) {
		this.contractIds = contractIds;
	}

	public List<Integer> getExcludeContractIds() {
		return excludeContractIds;
	}

	public void setExcludeContractIds(List<Integer> excludeContractIds) {
		this.excludeContractIds = excludeContractIds;
	}

}