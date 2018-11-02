package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;

public class BusOrderDTO extends BaseDTO {

	private static final long serialVersionUID = 4684174070702790458L;

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
	// 下单开始时间(yyyy-MM-dd)
	private String createDateBegin;
	// 下单结束时间(yyyy-MM-dd)
	private String createDateEnd;
	// 订单状态
	private Integer status;

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setPageSize(Integer pageSize) {
		super.pageSize = pageSize == null ? 20 : pageSize;
	}

}
