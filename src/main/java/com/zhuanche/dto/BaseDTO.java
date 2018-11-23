package com.zhuanche.dto;

import java.io.Serializable;
import java.util.Set;

public class BaseDTO implements Serializable {

	private static final long serialVersionUID = -4735370780290630864L;

	/** 页码 **/
	protected Integer pageNum;

	/** 每页条数 **/
	protected Integer pageSize;
	
	//数据权限控制字段
    private Set<Integer> cityIds;//可以管理的所有城市ID
    private Set<Integer> supplierIds;//可以管理的所有供应商ID
    private Set<Integer> teamIds;//可以管理的所有车队ID
	

	public BaseDTO() {
		super();
		this.setPageNum(null);
		this.setPageSize(null);
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum == null ? 0 : pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize == null ? 30 : pageSize;
	}

	public Set<Integer> getCityIds() {
		return cityIds;
	}

	public void setCityIds(Set<Integer> cityIds) {
		this.cityIds = cityIds;
	}

	public Set<Integer> getSupplierIds() {
		return supplierIds;
	}

	public void setSupplierIds(Set<Integer> supplierIds) {
		this.supplierIds = supplierIds;
	}

	public Set<Integer> getTeamIds() {
		return teamIds;
	}

	public void setTeamIds(Set<Integer> teamIds) {
		this.teamIds = teamIds;
	}

}
