package com.zhuanche.vo.busManage;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName: BusSupplierPageVO
 * @Description:
 * @author: yanyunpeng
 * @date: 2018年12月10日 下午4:26:17
 * 
 */
public class BusSupplierPageVO {

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商名称 **/
	private String supplierName;

	/** 城市名称 **/
	private String cityName;

	/** 分佣比例 **/
	private Double supplierRate;

	/** 加盟费 **/
	private BigDecimal franchiseFee;

	/** 保证金 **/
	private BigDecimal deposit;

	/** 是否有返点   0不返点 1返点**/
	private Integer isRebate;

	/** 合同开始时间 **/
	private Date contractDateStart;

	/** 合同结束时间 **/
	private Date contractDateEnd;

	/** 是否快过期  0否  1是 **/
	private Integer isExpireSoon;

	/** 状态：1.有效 0.无效 **/
	private Integer status;

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Double getSupplierRate() {
		return supplierRate;
	}

	public void setSupplierRate(Double supplierRate) {
		this.supplierRate = supplierRate;
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

	public Integer getIsRebate() {
		return isRebate;
	}

	public void setIsRebate(Integer isRebate) {
		this.isRebate = isRebate;
	}

	public Date getContractDateStart() {
		return contractDateStart;
	}

	public void setContractDateStart(Date contractDateStart) {
		this.contractDateStart = contractDateStart;
	}

	public Date getContractDateEnd() {
		return contractDateEnd;
	}

	public void setContractDateEnd(Date contractDateEnd) {
		this.contractDateEnd = contractDateEnd;
	}

	public Integer getIsExpireSoon() {
		return isExpireSoon;
	}

	public void setIsExpireSoon(Integer isExpireSoon) {
		this.isExpireSoon = isExpireSoon;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	// ====================其它业务数据======================

}
