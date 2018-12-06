package com.zhuanche.dto;

import java.io.Serializable;
import java.util.Set;

public class BaseDTO implements Serializable {

	private static final long serialVersionUID = -4735370780290630864L;

	/** 页码 **/
	public Integer pageNum;

	/** 每页条数 **/
	public Integer pageSize;

	// 数据权限控制字段
	private Set<Integer> authOfCity;// 可以管理的所有城市ID
	private Set<Integer> authOfSupplier;// 可以管理的所有供应商ID
	private Set<Integer> authOfTeam;// 可以管理的所有车队ID

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

	public Set<Integer> getAuthOfCity() {
		return authOfCity;
	}

	public void setAuthOfCity(Set<Integer> authOfCity) {
		this.authOfCity = authOfCity;
	}

	public Set<Integer> getAuthOfSupplier() {
		return authOfSupplier;
	}

	public void setAuthOfSupplier(Set<Integer> authOfSupplier) {
		this.authOfSupplier = authOfSupplier;
	}

	public Set<Integer> getAuthOfTeam() {
		return authOfTeam;
	}

	public void setAuthOfTeam(Set<Integer> authOfTeam) {
		this.authOfTeam = authOfTeam;
	}

}
