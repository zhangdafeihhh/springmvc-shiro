package com.zhuanche.dto.busManage;

import java.util.List;
import java.util.Set;

/**
 * @ClassName: BusCarRicherDTO
 * @Description:查询巴士可用车辆(数据库查询DTO)
 * @author: yanyunpeng
 * @date: 2018年10月30日 下午3:18:54
 * 
 */
public class BusCarRicherDTO extends BusCarDTO {

	private static final long serialVersionUID = 2418814219259764973L;

	/** 不可用车牌号 **/
	private List<String> licensePlatesList;
	/** 供应商ID集合 **/
	private Set<Integer> supplierIds;

	public List<String> getLicensePlatesList() {
		return licensePlatesList;
	}

	public void setLicensePlatesList(List<String> licensePlatesList) {
		this.licensePlatesList = licensePlatesList;
	}

	public Set<Integer> getSupplierIds() {
		return supplierIds;
	}

	public void setSupplierIds(Set<Integer> supplierIds) {
		this.supplierIds = supplierIds;
	}

}
