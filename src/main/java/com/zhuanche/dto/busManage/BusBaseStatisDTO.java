package com.zhuanche.dto.busManage;

import java.util.Date;

import com.zhuanche.constants.BusConst;

/**
 * @ClassName: BusBaseStatisDTO
 * @Description: 查询城市名称，供应商名称，服务类型，加盟类型
 * @author: yanyunpeng
 * @date: 2018年11月25日 下午3:13:57
 * 
 */
public class BusBaseStatisDTO implements BusConst {

	/** 司机Id **/
	private Integer driverId;
	/** 城市ID **/
	private Integer serviceCity;
	private Integer cityId;
	/** 城市名称 **/
	private String cityName;
	/** 供应商ID **/
	private Integer supplierId;
	/** 供应商名称 **/
	private String supplierName;
	/** 车队ID **/
	private Integer teamId;
	/** 车队名称 **/
	private String teamName;
	/** 车队下小组ID **/
	private Integer teamGroupId;
	/** 车队下小组名称 **/
	private String teamGroupName;
	/** 加盟类型ID **/
	private Integer cooperationType;
	/** 加盟类型名称 **/
	private String cooperationTypeName;

	/** 创建人ID **/
	private Integer createBy;
	/** 创建人名称 **/
	private String createName;
	/** 创建时间 **/
	private Date createDate;
	/** 修改人ID **/
	private Integer updateBy;
	/** 修改人名称 **/
	private String updateName;
	/** 修改时间 **/
	private Date updateDate;

	/** 车牌号 **/
	private String licensePlates;
	/** 车型ID **/
	private Integer modelId;
	/** 车型名称 **/
	private String modelName;
	/** 车型类型ID **/
	private Integer groupId;
	/** 车型类型名称 **/
	private String groupName;

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public Integer getServiceCity() {
		return serviceCity;
	}

	public void setServiceCity(Integer serviceCity) {
		this.serviceCity = serviceCity;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getTeamGroupId() {
		return teamGroupId;
	}

	public void setTeamGroupId(Integer teamGroupId) {
		this.teamGroupId = teamGroupId;
	}

	public String getTeamGroupName() {
		return teamGroupName;
	}

	public void setTeamGroupName(String teamGroupName) {
		this.teamGroupName = teamGroupName;
	}

	public Integer getCooperationType() {
		return cooperationType;
	}

	public void setCooperationType(Integer cooperationType) {
		this.cooperationType = cooperationType;
	}

	public String getCooperationTypeName() {
		return cooperationTypeName;
	}

	public void setCooperationTypeName(String cooperationTypeName) {
		this.cooperationTypeName = cooperationTypeName;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
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

}
