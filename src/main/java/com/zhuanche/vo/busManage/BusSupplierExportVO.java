package com.zhuanche.vo.busManage;

/**
 * @ClassName: BusSupplierExportVO
 * @Description: 巴士供应商信息VO
 * @author: yanyunpeng
 * @date: 2018年12月4日 下午4:38:15
 * 
 */
public class BusSupplierExportVO {

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商 **/
	private String supplierName;

	/** 城市 */
	private String cityName;

	/** 状态：1.有效 0.无效 **/
	private Integer status;

	/** 分佣比例 **/
	private Double supplierRate;

	/** 是否有返点  0不返点 1返点 **/
	private Integer isRebate;

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getSupplierRate() {
		return supplierRate;
	}

	public void setSupplierRate(Double supplierRate) {
		this.supplierRate = supplierRate;
	}

	public Integer getIsRebate() {
		return isRebate;
	}

	public void setIsRebate(Integer isRebate) {
		this.isRebate = isRebate;
	}

}
