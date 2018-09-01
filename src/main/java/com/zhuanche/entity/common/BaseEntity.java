package com.zhuanche.entity.common;


import java.io.Serializable;
import java.util.List;

/**
 * @author will
 *
 */
public class BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer total;
	private List<?> list;
	private Integer pagesize = 30;
	private Integer page = 1;
	private Integer offSet = 0;
	private Integer pagerSize;
	protected String pagerUrl;
	
	private String sortname;
	private String sortorder=" desc";
	

	private String cities;
	private String supplierIds;
	private String teamIds;
	private String groupIds;
	private String driverIds;
	private String paramIds;
	
	public String getCities() {
		return cities;
	}
	public void setCities(String cities) {
		this.cities = cities;
	}
	public String getSupplierIds() {
		return supplierIds;
	}
	public void setSupplierIds(String supplierIds) {
		this.supplierIds = supplierIds;
	}
	public String getTeamIds() {
		return teamIds;
	}
	public void setTeamIds(String teamIds) {
		this.teamIds = teamIds;
	}
	public String getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}
	public String getDriverIds() {
		return driverIds;
	}
	public void setDriverIds(String driverIds) {
		this.driverIds = driverIds;
	}
	
	public String getParamIds() {
		return paramIds;
	}
	
	public void setParamIds(String paramIds) {
		this.paramIds = paramIds;
	}
	
	public String getSortname() {
		return sortname;
	}
	public void setSortname(String sortname) {
		this.sortname = sortname;
	}
	public String getSortorder() {
		return sortorder;
	}
	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public List<?> getList() {
		return list;
	}
	public void setList(List<?> list) {
		this.list = list;
	}
	public Integer getPagerSize() {
		return pagerSize;
	}
	public void setPagerSize(Integer pagerSize) {
		this.pagerSize = pagerSize;
	}
	public String getPagerUrl() {
		return pagerUrl;
	}
	public void setPagerUrl(String pagerUrl) {
		this.pagerUrl = pagerUrl;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
		if(null != page && page>0){
			this.setOffSet((page-1)*this.getPagesize());
		}
	}
	public Integer getOffSet() {
		return offSet;
	}
	private void setOffSet(Integer offSet) {
		this.offSet = offSet;
	}
	public Integer getPagesize() {
		return pagesize;
	}
	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
		if(null != pagesize && pagesize>0){
			this.setOffSet((this.getPage()-1)*pagesize);
		}
	}
	
}
