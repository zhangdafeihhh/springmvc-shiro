package com.zhuanche.entity.mdbcarmanage;

import java.io.Serializable;

/**
 * 日报查询参数
 * @author admin
 *
 */
public class DriverDailyReportParams implements Serializable{
	
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

	public DriverDailyReportParams(String licensePlates, String driverName, String driverIds, String teamIds, String suppliers, String cities, String statDateStart,
		   String statDateEnd, String sortName, String sortOrder, String groupIds, Integer page, Integer pageSize) {
		this.licensePlates = licensePlates.equals("null") ? "" : licensePlates;
		this.driverName = driverName.equals("null") ? "" : driverName;
		this.driverIds = driverIds.equals("null") ? "" : driverIds;
		this.teamIds = teamIds.equals("null") ? "" : teamIds;
		this.suppliers = suppliers.equals("null") ? "" : suppliers;
		this.cities = cities.equals("null") ? "" : cities;
		this.statDateStart = statDateStart.equals("null") ? "" : statDateStart;
		this.statDateEnd = statDateEnd.equals("null") ? "" : statDateEnd;
		this.sortName = sortName.equals("null") ? "" : sortName;
		this.sortOrder = sortOrder.equals("null") ? "" : sortOrder;
		this.groupIds = groupIds.equals("null") ? "" : groupIds;
		this.page = page;
		this.pageSize = pageSize;
	}

	public DriverDailyReportParams(String driverIds, String statDateStart,
								   String statDateEnd, String sortName, String sortOrder, Integer page, Integer pageSize) {
		this.driverIds = driverIds.equals("null") ? "" : driverIds;
		this.statDateStart = statDateStart.equals("null") ? "" : statDateStart;
		this.statDateEnd = statDateEnd.equals("null") ? "" : statDateEnd;
		this.sortName = sortName.equals("null") ? "" : sortName;
		this.sortOrder = sortOrder.equals("null") ? "" : sortOrder;
		this.page = page;
		this.pageSize = pageSize;
	}


	public DriverDailyReportParams() {
	}

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

	@Override
	public String toString() {
		return "DriverDailyReportParams{" +
				"licensePlates='" + licensePlates + '\'' +
				", driverName='" + driverName + '\'' +
				", driverIds='" + driverIds + '\'' +
				", teamIds='" + teamIds + '\'' +
				", suppliers='" + suppliers + '\'' +
				", cities='" + cities + '\'' +
				", statDateStart='" + statDateStart + '\'' +
				", statDateEnd='" + statDateEnd + '\'' +
				", sortName='" + sortName + '\'' +
				", sortOrder='" + sortOrder + '\'' +
				", groupIds='" + groupIds + '\'' +
				", page=" + page +
				", pageSize=" + pageSize +
				'}';
	}
}