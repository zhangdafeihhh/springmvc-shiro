package com.zhuanche.entity.common;

import java.io.Serializable;

/**
 * 日报查询参数
 * @author admin
 *
 */
public class DriverDailyReportBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String licensePlates;//车牌号
	private String driverName;//司机
	private String driverIds;
	private String teamIds;//车队
	private String suppliers;//供应商
	private String cities;//城市
	private String statDateStart;//开始时间
	private String statDateEnd;//结束时间
	private String sortName;//排序
	private String sortOrder=" desc";
	private String groupIds;//小组
	private Integer page;
	private Integer pageSize;

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverIds() {
		return driverIds;
	}

	public void setDriverIds(String driverIds) {
		this.driverIds = driverIds;
	}

	public String getTeamIds() {
		return teamIds;
	}

	public void setTeamIds(String teamIds) {
		this.teamIds = teamIds;
	}

	public String getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}

	public String getCities() {
		return cities;
	}

	public void setCities(String cities) {
		this.cities = cities;
	}

	public String getStatDateStart() {
		return statDateStart;
	}

	public void setStatDateStart(String statDateStart) {
		this.statDateStart = statDateStart;
	}

	public String getStatDateEnd() {
		return statDateEnd;
	}

	public void setStatDateEnd(String statDateEnd) {
		this.statDateEnd = statDateEnd;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}

	public Integer getPage() {
		return page == null ? 1 :page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize == null ? 30 : pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}