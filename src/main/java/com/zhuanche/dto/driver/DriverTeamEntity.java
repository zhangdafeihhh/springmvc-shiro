package com.zhuanche.web.entity.driver;

import com.zhuanche.web.entity.common.BaseEntity;


public class DriverTeamEntity extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String teamId;
	private String teamName;
	private Integer createBy;
	private Integer updateBy;
	private String createDate;
	private String updateDate;
	private String cities;
	private String suppliers;
	private String remark;
	private String driverId;
	
	private String cityName;
	private String supplierName;
	
	private Integer status;//状态   0关闭 1启用
	
	private Integer pId;
	
	private String dutyStartDate;
	private String dutyEndDate;
	
	private String sort;
	
	private String charge1;//负责人1
	private String charge2;//负责人2
	private String charge3;//负责人3
	
	public String getCharge1() {
		return charge1;
	}
	public void setCharge1(String charge1) {
		this.charge1 = charge1;
	}
	public String getCharge2() {
		return charge2;
	}
	public void setCharge2(String charge2) {
		this.charge2 = charge2;
	}
	public String getCharge3() {
		return charge3;
	}
	public void setCharge3(String charge3) {
		this.charge3 = charge3;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getDutyStartDate() {
		return dutyStartDate;
	}
	public void setDutyStartDate(String dutyStartDate) {
		this.dutyStartDate = dutyStartDate;
	}
	public String getDutyEndDate() {
		return dutyEndDate;
	}
	public void setDutyEndDate(String dutyEndDate) {
		this.dutyEndDate = dutyEndDate;
	}
	public Integer getpId() {
		return pId;
	}
	public void setpId(Integer pId) {
		this.pId = pId;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public Integer getCreateBy() {
		return createBy;
	}
	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}
	public Integer getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}
	
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getCities() {
		return cities;
	}
	public void setCities(String cities) {
		this.cities = cities;
	}
	public String getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(String suppliers) {
		this.suppliers = suppliers;
	}
	
	
}
