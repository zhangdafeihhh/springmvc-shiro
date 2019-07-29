package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;

public class BusOrderDTO extends BaseDTO {

	private static final long serialVersionUID = 4684174070702790458L;

	// 订单号
	private String orderNo;
	// 服务类别ID
	private Integer serviceTypeId;
	// 实际车型类别ID
	private Integer carGroupId;
	// 预约车型类别ID
	private Integer bookingGroupid;
	// 预订人手机号
	private String bookingUserPhone;
	// 乘车人手机号
	private String riderPhone;
	// 车牌号
	private String licensePlates;
	// 城市ID
	private Integer cityId;
	// 供应商ID
	private Integer supplierId;
	// 司机姓名
	private String driverName;
	// 司机手机号
	private String driverPhone;
	// 下单开始时间(yyyy-MM-dd)
	private String createDateBegin;
	// 下单结束时间(yyyy-MM-dd)
	private String createDateEnd;
	// 预定上车开始时间(yyyy-MM-dd)
	private String bookingDateBegin;
	// 预定上车结束时间(yyyy-MM-dd)
	private String bookingDateEnd;
	// 下车开始时间(yyyy-MM-dd)
	private String factEndDateBegin;
	// 下车结束时间(yyyy-MM-dd)
	private String factEndDateEnd;
	// 订单状态
	private Integer status;
	/**
	 * 所有供应商下关联的businessId
	 */
	private String businessIds;
	/**
	 * 指定供应商下关联的businessId
	 */
	private String specialBusinessIds;
	/**
	 * 当前城市下企业跨地区绑定的businessId
	 */
	private String crossDomainBusinessIds;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(Integer serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public Integer getCarGroupId() {
		return carGroupId;
	}

	public void setCarGroupId(Integer carGroupId) {
		this.carGroupId = carGroupId;
	}

	public Integer getBookingGroupid() {
		return bookingGroupid;
	}

	public void setBookingGroupid(Integer bookingGroupid) {
		this.bookingGroupid = bookingGroupid;
	}

	public String getBookingUserPhone() {
		return bookingUserPhone;
	}

	public void setBookingUserPhone(String bookingUserPhone) {
		this.bookingUserPhone = bookingUserPhone;
	}

	public String getRiderPhone() {
		return riderPhone;
	}

	public void setRiderPhone(String riderPhone) {
		this.riderPhone = riderPhone;
	}

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getCreateDateBegin() {
		return createDateBegin;
	}

	public void setCreateDateBegin(String createDateBegin) {
		this.createDateBegin = createDateBegin;
	}

	public String getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(String createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public String getBookingDateBegin() {
		return bookingDateBegin;
	}

	public void setBookingDateBegin(String bookingDateBegin) {
		this.bookingDateBegin = bookingDateBegin;
	}

	public String getBookingDateEnd() {
		return bookingDateEnd;
	}

	public void setBookingDateEnd(String bookingDateEnd) {
		this.bookingDateEnd = bookingDateEnd;
	}

	public String getFactEndDateBegin() {
		return factEndDateBegin;
	}

	public void setFactEndDateBegin(String factEndDateBegin) {
		this.factEndDateBegin = factEndDateBegin;
	}

	public String getFactEndDateEnd() {
		return factEndDateEnd;
	}

	public void setFactEndDateEnd(String factEndDateEnd) {
		this.factEndDateEnd = factEndDateEnd;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setPageSize(Integer pageSize) {
		super.pageSize = pageSize == null ? 20 : pageSize;
	}

	public String getBusinessIds() {
		return businessIds;
	}

	public void setBusinessIds(String businessIds) {
		this.businessIds = businessIds;
	}

	public String getSpecialBusinessIds() {
		return specialBusinessIds;
	}

	public void setSpecialBusinessIds(String specialBusinessIds) {
		this.specialBusinessIds = specialBusinessIds;
	}

	public String getCrossDomainBusinessIds() {
		return crossDomainBusinessIds;
	}

	public void setCrossDomainBusinessIds(String crossDomainBusinessIds) {
		this.crossDomainBusinessIds = crossDomainBusinessIds;
	}
}
