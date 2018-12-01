package com.zhuanche.vo.busManage;

import java.math.BigDecimal;

public class BusSupplierPageVO {

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商名称 **/
	private String supplierNum;

	/** 城市名称 **/
	private Integer cityName;

	/** 分佣比例 **/
	// TODO

	/** 加盟费 **/
	private BigDecimal franchiseFee;

	/** 保证金 **/
	private BigDecimal deposit;

	/** 是否有返点 **/
	// TODO

	/** 合同时间 **/
	// TODO

	/** 状态 **/
	private Integer status;

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierNum() {
		return supplierNum;
	}

	public void setSupplierNum(String supplierNum) {
		this.supplierNum = supplierNum;
	}

	public Integer getCityName() {
		return cityName;
	}

	public void setCityName(Integer cityName) {
		this.cityName = cityName;
	}

	public BigDecimal getFranchiseFee() {
		return franchiseFee;
	}

	public void setFranchiseFee(BigDecimal franchiseFee) {
		this.franchiseFee = franchiseFee;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
