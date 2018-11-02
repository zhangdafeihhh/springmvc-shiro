package com.zhuanche.dto.busManage;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: BusDriverDTO
 * @Description:查询巴士可用司机(数据库查询DTO)
 * @author: yanyunpeng
 * @date: 2018年10月25日 下午2:41:49
 * 
 */
public class BusDriverRicherDTO extends BusDriverDTO {

	private static final long serialVersionUID = -7543573806194336293L;

	// 司机ID
	private Integer driverId;
	// 不可用司机ID
	private List<Integer> driverIds;
	// 供应商ID集合
	private Set<Integer> supplierIds;

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public List<Integer> getDriverIds() {
		return driverIds;
	}

	public void setDriverIds(List<Integer> driverIds) {
		this.driverIds = driverIds;
	}

	public Set<Integer> getSupplierIds() {
		return supplierIds;
	}

	public void setSupplierIds(Set<Integer> supplierIds) {
		this.supplierIds = supplierIds;
	}

}
