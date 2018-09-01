package com.zhuanche.entity.rentcar;

import java.util.Date;

public class CarBizCarGroup{
	private static final long serialVersionUID = 1L;
	
	private Integer groupId;
	private String groupName;
	private int seatNum;
	private int rank;
	private int sort;
	private int status;
	private int createBy;
	private int updateBy;
	private Date createDate;
	private Date updateDate;
	private String imgUrl;
	private String selectedImgUrl;
	private String memo;
	private int[] cityid;
	private String cityId;
	private String cityname;
	
	//日租半日租 状态
	
	private Integer charteredStatus;
	
	private int[] upgradeModelId;
	private String upgradeModelName;
	
	private int modelId;
	private String modelName;
	private int selected;
		
	//权限
	private String cities;
	private String suppliers;
	private String teamIdS;
	
	private String type;
	
		
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
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
	public String getTeamIdS() {
		return teamIdS;
	}
	public void setTeamIdS(String teamIdS) {
		this.teamIdS = teamIdS;
	}
	public Integer getCharteredStatus() {
		return charteredStatus;
	}
	public void setCharteredStatus(Integer charteredStatus) {
		this.charteredStatus = charteredStatus;
	}
	public int[] getUpgradeModelId() {
		return upgradeModelId;
	}
	public void setUpgradeModelId(int[] upgradeModelId) {
		this.upgradeModelId = upgradeModelId;
	}
	public String getUpgradeModelName() {
		return upgradeModelName;
	}
	public void setUpgradeModelName(String upgradeModelName) {
		this.upgradeModelName = upgradeModelName;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public int getSelected() {
		return selected;
	}
	public void setSelected(int selected) {
		this.selected = selected;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getSeatNum() {
		return seatNum;
	}
	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCreateBy() {
		return createBy;
	}
	public void setCreateBy(int createBy) {
		this.createBy = createBy;
	}
	public int getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(int updateBy) {
		this.updateBy = updateBy;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getSelectedImgUrl() {
		return selectedImgUrl;
	}
	public void setSelectedImgUrl(String selectedImgUrl) {
		this.selectedImgUrl = selectedImgUrl;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int[] getCityid() {
		return cityid;
	}
	public void setCityid(int[] cityid) {
		this.cityid = cityid;
	}
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
