package com.zhuanche.entity.rentcar;

import java.util.Date;


public class CarGroupEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// date formats
	public static final String FORMAT_CREATE_DATE = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_UPDATE_DATE = "yyyy-MM-dd HH:mm:ss";
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
	private String cityname;
	// 日租半日租 状态
	private Integer charteredStatus;
	private int[] upgradeModelId;
	private String upgradeModelName;
	private String createName;
	private String updateName;
	private int[] cityid;
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
	public String getCityname() {
		return cityname;
	}
	public void setCityname(String cityname) {
		this.cityname = cityname;
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
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	public int[] getCityid() {
		return cityid;
	}
	public void setCityid(int[] cityid) {
		this.cityid = cityid;
	}
	
}
