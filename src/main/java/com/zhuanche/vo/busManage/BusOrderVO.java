package com.zhuanche.vo.busManage;

import java.util.Date;

public class BusOrderVO {

	// 订单id
	private Integer orderId;
	// 订单号
	private String orderNo;
	// 订单类型(1:普通用户订单, 2:企业用户订单)
	private Integer type;
	// 订单类型 0 安卓 1 | IOS 2 客服后台创建 | 3机构后台创建
	private Integer orderType;
	// 是否往返 0-否（即单程） 1-是（即往返）
	private Integer isReturn;
	// 订单类型ID
	private Integer serviceTypeId;
	// 订单类型名称
	private String serviceTypeName;
	// 预订人id
	private Integer bookingUserId;
	// 预订人电话
	private String bookingUserPhone;
	// 预定车型
	private String bookingGroupid;
	// 预定车型名称
	private String bookingGroupName;
	// 预约用车时间
	private Date bookingDate;
	// 预估里程（单位/公里）
	private Double estimateDistance;
	// 预收费用
	private Double estimatedAmountYuan;
	// 下单时间
	private Date createDate;
	// 城市id
	private Integer cityId;
	// 城市名称
	private String cityName;
	// 乘车人姓名
	private String riderName;
	// 乘车人手机号
	private String riderPhone;
	// 上车地点
	private String bookingStartAddr;
	// 预定开始地点短地址
	private String bookingStartAddrShort;
	// 预定上车坐标点
	private String bookingStartPoint;
	// 下车地点
	private String bookingEndAddr;
	// 预定结束地点短地址
	private String bookingEndAddrShort;
	// 预定下车坐标点
	private String bookingEndPoint;
	/*
	 * 
	 * 订单状态：10100, 预定中/待支付 10103, 支付成功/待接单 10105, 待服务 10200, 已出发 10205, 已到达 10300,
	 * 服务中 10305, 服务结束 10400, 待结算 10402,支付中 10403,扣款中 10404,后付 10405, 已结算 10500, 已完成
	 * 10505, 订单异议 10600, 已取消
	 */
	private Integer status;
	// 取消原因
	private String memo;
	// 车牌号
	private String licensePlates;
	// 司机ID
	private Integer driverId;
	// 司机姓名
	private String driverName;
	// 司机手机号
	private String driverPhone;
	// 本单司机评分
	private String driverScore;
	// 付款类型（费用类型：1 预付费；-1 后付费）
	private Integer companyType;
	// 企业名称
	private String businessName;
	// 企业折扣
	private Integer percent;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(Integer isReturn) {
		this.isReturn = isReturn;
	}

	public Integer getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(Integer serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public Integer getBookingUserId() {
		return bookingUserId;
	}

	public void setBookingUserId(Integer bookingUserId) {
		this.bookingUserId = bookingUserId;
	}

	public String getBookingUserPhone() {
		return bookingUserPhone;
	}

	public void setBookingUserPhone(String bookingUserPhone) {
		this.bookingUserPhone = bookingUserPhone;
	}

	public String getBookingGroupid() {
		return bookingGroupid;
	}

	public void setBookingGroupid(String bookingGroupid) {
		this.bookingGroupid = bookingGroupid;
	}

	public String getBookingGroupName() {
		return bookingGroupName;
	}

	public void setBookingGroupName(String bookingGroupName) {
		this.bookingGroupName = bookingGroupName;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Double getEstimateDistance() {
		return estimateDistance;
	}

	public void setEstimateDistance(Double estimateDistance) {
		this.estimateDistance = estimateDistance;
	}

	public Double getEstimatedAmountYuan() {
		return estimatedAmountYuan;
	}

	public void setEstimatedAmountYuan(Double estimatedAmountYuan) {
		this.estimatedAmountYuan = estimatedAmountYuan;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public String getRiderName() {
		return riderName;
	}

	public void setRiderName(String riderName) {
		this.riderName = riderName;
	}

	public String getRiderPhone() {
		return riderPhone;
	}

	public void setRiderPhone(String riderPhone) {
		this.riderPhone = riderPhone;
	}

	public String getBookingStartAddr() {
		return bookingStartAddr;
	}

	public void setBookingStartAddr(String bookingStartAddr) {
		this.bookingStartAddr = bookingStartAddr;
	}

	public String getBookingStartAddrShort() {
		return bookingStartAddrShort;
	}

	public void setBookingStartAddrShort(String bookingStartAddrShort) {
		this.bookingStartAddrShort = bookingStartAddrShort;
	}

	public String getBookingStartPoint() {
		return bookingStartPoint;
	}

	public void setBookingStartPoint(String bookingStartPoint) {
		this.bookingStartPoint = bookingStartPoint;
	}

	public String getBookingEndAddr() {
		return bookingEndAddr;
	}

	public void setBookingEndAddr(String bookingEndAddr) {
		this.bookingEndAddr = bookingEndAddr;
	}

	public String getBookingEndAddrShort() {
		return bookingEndAddrShort;
	}

	public void setBookingEndAddrShort(String bookingEndAddrShort) {
		this.bookingEndAddrShort = bookingEndAddrShort;
	}

	public String getBookingEndPoint() {
		return bookingEndPoint;
	}

	public void setBookingEndPoint(String bookingEndPoint) {
		this.bookingEndPoint = bookingEndPoint;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
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

	public String getDriverScore() {
		return driverScore;
	}

	public void setDriverScore(String driverScore) {
		this.driverScore = driverScore;
	}

	public Integer getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}

}
