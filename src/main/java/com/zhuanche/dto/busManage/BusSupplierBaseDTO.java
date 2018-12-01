package com.zhuanche.dto.busManage;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.zhuanche.common.web.datavalidate.custom.InArray;

public class BusSupplierBaseDTO implements BusSupplierDTO {

	// ========================接口字段=======================
	/** 供应商ID **/
	@NotNull(message = "供应商ID不能为空", groups = Update.class)
	private Integer supplierId;

	/** 供应商名称 **/
	@NotBlank(message = "供应商名称不能为空")
	private String supplierName;

	/** 城市ID **/
	@NotNull(message = "城市不能为空")
	private Integer supplierCity;

	/** 企业联系人 **/
	@NotBlank(message = "企业联系人不能为空")
	private String contacts;

	/** 企业联系人电话 **/
	@NotBlank(message = "企业联系人电话不能为空")
	private String contactsPhone;

	/** 调度员电话 **/
	@NotBlank(message = "调度员电话不能为空")
	private String dispatcherPhone;

	/** 加盟类型:car_biz_cooperation_type **/
	@NotNull(message = "加盟类型不能为空")
	private Integer cooperationType;

	/** 状态：1.有效 2.无效 **/
	@NotNull(message = "状态不能为空")
	@InArray(values = { "1", "2" }, message = "状态不在有效状态范围内")
	private Integer status;

	// ========================业务字段=========================
	/** 创建人ID **/
	private Integer createBy;

	/** 修改人ID **/
	private Integer updateBy;

	/** 创建时间 **/
	private Date createDate;

	/** 修改时间 **/
	private Date updateDate;

	/** 供应商地址 **/
	private String address;

	/** 供应商类型（0：其他，1：巴士供应商） **/
	private Integer supplierType;

	/** 是否客运企业:1是,2否 **/
	private Integer enterpriseType;

	/** 是否双班供应商：0单班(默认), 1双班 **/
	private Integer isTwoShifts;

	// =======================getter/setter====================

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

	public Integer getSupplierCity() {
		return supplierCity;
	}

	public void setSupplierCity(Integer supplierCity) {
		this.supplierCity = supplierCity;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getContactsPhone() {
		return contactsPhone;
	}

	public void setContactsPhone(String contactsPhone) {
		this.contactsPhone = contactsPhone;
	}

	public String getDispatcherPhone() {
		return dispatcherPhone;
	}

	public void setDispatcherPhone(String dispatcherPhone) {
		this.dispatcherPhone = dispatcherPhone;
	}

	public Integer getCooperationType() {
		return cooperationType;
	}

	public void setCooperationType(Integer cooperationType) {
		this.cooperationType = cooperationType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(Integer supplierType) {
		this.supplierType = supplierType;
	}

	public Integer getEnterpriseType() {
		return enterpriseType;
	}

	public void setEnterpriseType(Integer enterpriseType) {
		this.enterpriseType = enterpriseType;
	}

	public Integer getIsTwoShifts() {
		return isTwoShifts;
	}

	public void setIsTwoShifts(Integer isTwoShifts) {
		this.isTwoShifts = isTwoShifts;
	}

}