package com.zhuanche.vo.busManage;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @ClassName: BusDriverInfoPageVO
 * @Description: 巴士司机信息VO
 * @author: yanyunpeng
 * @date: 2018年11月22日 下午5:37:44
 * 
 */
public class BusDriverInfoPageVO {

	/** 司机ID **/
	private Integer driverId;

	/** 司机姓名 **/
	private String name;

	/** 手机号 **/
	private String phone;

	/** 城市ID **/
	@JSONField(serialize = false)
	private Integer cityId;

	/** 城市名称 **/
	private String cityName;

	/** 供应商ID **/
	@JSONField(serialize = false)
	private Integer supplierId;

	/** 供应商名称 **/
	private String supplierName;

	/** 车型类别ID **/
	@JSONField(serialize = false)
	private Integer groupId;

	/** 车型类别名称 **/
	private String groupName;

	/** 创建时间 **/
	private Date createDate;

	/** 完成订单数 **/
	private Integer finishedOrderCount;

	/** 平均评分 **/
	private String average;

	/** 司机状态 **/
	private Integer status;

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getFinishedOrderCount() {
		return finishedOrderCount;
	}

	public void setFinishedOrderCount(Integer finishedOrderCount) {
		this.finishedOrderCount = finishedOrderCount;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
