package com.zhuanche.entity.busManage;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @ClassName: BusOrderDetail
 * @Description: 订单详情
 * @author: yanyunpeng
 * @date: 2018年10月31日 下午1:35:56
 * 
 */
public class BusOrderDetail implements Serializable {

	private static final long serialVersionUID = 768264376102834599L;

	private Integer bookingUserId;// 预订人id
	private String BookingUserName;// 预订人姓名
	private String bookingUserPhone;// 预订人电话
	private Integer receivesSms;// 预订人是否接收短信 1 接收 2 不接受
	private String bookingGroupid;// 预定车型
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date bookingDate;// 预约用车时间 (时间戳)
	private String bookingCurrentAddr;// 预订人下单时地点
	private String bookingCurrentPoint;// 预订人下车时坐标

	private String factStartAddr;// 实际上车地点
	private String factStartAddrShort;// 实际开始地点短地址
	private String factStartPoint;// 实际上车坐标点
	private String factEndAddr;// 实际下车地点
	private String factEndAddrShort;// 实际结束地点短地址
	private String factEndPoint;// 实际下车坐标点
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date factDate;// 实际上车时间(时间戳)
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date factEndDate;// 实际下车时间(时间戳)（订单完成时间）

	private String bookingStartAddr;// 上车地点
	private String bookingStartAddrShort;// 预定开始地点短地址
	private String bookingStartPoint;// 预定上车坐标点
	private String bookingEndAddr;// 下车地点
	private String bookingEndAddrShort;// 预定结束地点短地址
	private String bookingEndPoint;// 预定下车坐标点

	private Integer businessId;// 机构id

	private Integer cancelCreateBy;// 取消操作人
	private String cancelName;// 取消操作人姓名
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date cancelCreateDate;// 取消操作时间(时间戳)
	private String cancelType;// 取消来源
	private String memo;// 取消原因

	private Integer driverId;// 司机id
	private String driverName;// 司机姓名
	private String driverPhone;// 司机电话
	private Integer cityId;// 城市id
	private String cityName;// 城市名称
	private Integer supplierId;// 供应商ID
	private String supplierName;// 供应商名称

	private String licensePlates;// 车牌号
	private Integer carModelId;// 车型ID
	private String carModelName;// 车型名称
	private Integer carGroupId;// 车组
	private String carGroupName;// 车型名称

	private String channelsNum;// 渠道号
	private Double commission;// 分佣

	private Integer isDisplay;//
	private String imei;// 手机imei
	private String mobelVersion;// 手机版本号
	private String version;// 软件版本号
	private String sysVersion;//
	private String platform;// 平台
	private Integer mapType;// 地图类型 2高德、3百度，默认高德

	private Long estimatedAmount;// 预估金额(单位 : 分)
	private Double estimatedAmountYuan;// 预估金额(单位 : 元)
	private Integer isEat;// 包吃 1 是 0 否
	private Integer isLive;// 包住 1 是 0 否
	private Integer isParking;// 包停车费 1是 0 否
	private Integer isReturn;// 是否往返 0-否（即单程） 1-是（即往返）
	private Integer luggageCount;// 行李箱数量

	private String airlineNo;// 航班号
	private String multiBookingDate;// 多日预订日期天数 "3" 代表3天
	/*
	 * 多日预订地点 ([{ "bookingDate":1504342541000,出发日期 "booking":[{ "bookingAddr":"天安门",
	 * 地点名称 "bookingPointLo":"123", 坐标经度 "bookingPointLa":"123", 坐标纬度
	 * "bookingStatus":"0" 地点状态:0 未经过 1 已经过}] }])
	 */
	private String multiPoint;

	private Integer orderId;// 订单id
	private String orderNo;// 订单号
	private Integer orderType;// 订单类型 0 安卓; 1 IOS; 2 客服后台创建; 7 机构； 12 h5； 13 微信小程序；
	private Integer payChannel;// 支付方式 1个人账户，2机构账户 3 微信 4支付宝
	private Integer payFlag;// 谁付款 0:预订人
	private Integer pushDriverType;// 订单指派司机类型 1 系统强派| 2 司机抢单| 3 客服后台人工指派 | 4机构后台指定司机改派
	private Integer riderCount;// 乘车人数量
	private String riderName;// 乘车人姓名
	private String riderPhone;// 乘车人手机号
	private Integer serviceTypeId;// 订单类型 30:巴士接机 31:巴士送机 32:巴士日租 33:巴士半日租 34:巴士特色路线 50:多日租
	private Integer specialLineId;// 特色路线id
	private String remark;// 备注

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;// 创建时间 (时间戳)
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;// 修改时间 (时间戳)

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date bindDriverDate;// 绑定司机时间(时间戳)
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date startOffDate;// 司机出发时间(时间戳)
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date arriveDate;// 司机到达时间(时间戳)

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date startServiceDate;// 服务开始时间(时间戳)
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date endServiceDate;// 服务结束时间(时间戳)
	/*
	 * 订单状态：10100, 预定中/待支付 10103, 支付成功/待接单 10105, 待服务 10200, 已出发 10205, 已到达 10300,
	 * 服务中,10305, 服务结束 10400, 待结算 10402,支付中 10403,扣款中 10404,后付 10405, 已结算 10500, 已完成
	 * 10505, 订单异议 10600, 已取消
	 */
	private Integer status;
	private Integer type;// 订单类型(1:普通用户订单, 2:企业用户订单)

	public Integer getBookingUserId() {
		return bookingUserId;
	}

	public void setBookingUserId(Integer bookingUserId) {
		this.bookingUserId = bookingUserId;
	}

	public String getBookingUserName() {
		return BookingUserName;
	}

	public void setBookingUserName(String bookingUserName) {
		BookingUserName = bookingUserName;
	}

	public String getBookingUserPhone() {
		return bookingUserPhone;
	}

	public void setBookingUserPhone(String bookingUserPhone) {
		this.bookingUserPhone = bookingUserPhone;
	}

	public Integer getReceivesSms() {
		return receivesSms;
	}

	public void setReceivesSms(Integer receivesSms) {
		this.receivesSms = receivesSms;
	}

	public String getBookingGroupid() {
		return bookingGroupid;
	}

	public void setBookingGroupid(String bookingGroupid) {
		this.bookingGroupid = bookingGroupid;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getBookingCurrentAddr() {
		return bookingCurrentAddr;
	}

	public void setBookingCurrentAddr(String bookingCurrentAddr) {
		this.bookingCurrentAddr = bookingCurrentAddr;
	}

	public String getBookingCurrentPoint() {
		return bookingCurrentPoint;
	}

	public void setBookingCurrentPoint(String bookingCurrentPoint) {
		this.bookingCurrentPoint = bookingCurrentPoint;
	}

	public String getFactStartAddr() {
		return factStartAddr;
	}

	public void setFactStartAddr(String factStartAddr) {
		this.factStartAddr = factStartAddr;
	}

	public String getFactStartAddrShort() {
		return factStartAddrShort;
	}

	public void setFactStartAddrShort(String factStartAddrShort) {
		this.factStartAddrShort = factStartAddrShort;
	}

	public String getFactStartPoint() {
		return factStartPoint;
	}

	public void setFactStartPoint(String factStartPoint) {
		this.factStartPoint = factStartPoint;
	}

	public String getFactEndAddr() {
		return factEndAddr;
	}

	public void setFactEndAddr(String factEndAddr) {
		this.factEndAddr = factEndAddr;
	}

	public String getFactEndAddrShort() {
		return factEndAddrShort;
	}

	public void setFactEndAddrShort(String factEndAddrShort) {
		this.factEndAddrShort = factEndAddrShort;
	}

	public String getFactEndPoint() {
		return factEndPoint;
	}

	public void setFactEndPoint(String factEndPoint) {
		this.factEndPoint = factEndPoint;
	}

	public Date getFactDate() {
		return factDate;
	}

	public void setFactDate(Date factDate) {
		this.factDate = factDate;
	}

	public Date getFactEndDate() {
		return factEndDate;
	}

	public void setFactEndDate(Date factEndDate) {
		this.factEndDate = factEndDate;
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

	public Integer getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}

	public Integer getCancelCreateBy() {
		return cancelCreateBy;
	}

	public void setCancelCreateBy(Integer cancelCreateBy) {
		this.cancelCreateBy = cancelCreateBy;
	}

	public String getCancelName() {
		return cancelName;
	}

	public void setCancelName(String cancelName) {
		this.cancelName = cancelName;
	}

	public Date getCancelCreateDate() {
		return cancelCreateDate;
	}

	public void setCancelCreateDate(Date cancelCreateDate) {
		this.cancelCreateDate = cancelCreateDate;
	}

	public String getCancelType() {
		return cancelType;
	}

	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public Integer getCarModelId() {
		return carModelId;
	}

	public void setCarModelId(Integer carModelId) {
		this.carModelId = carModelId;
	}

	public String getCarModelName() {
		return carModelName;
	}

	public void setCarModelName(String carModelName) {
		this.carModelName = carModelName;
	}

	public Integer getCarGroupId() {
		return carGroupId;
	}

	public void setCarGroupId(Integer carGroupId) {
		this.carGroupId = carGroupId;
	}

	public String getCarGroupName() {
		return carGroupName;
	}

	public void setCarGroupName(String carGroupName) {
		this.carGroupName = carGroupName;
	}

	public String getChannelsNum() {
		return channelsNum;
	}

	public void setChannelsNum(String channelsNum) {
		this.channelsNum = channelsNum;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Integer getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(Integer isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getMobelVersion() {
		return mobelVersion;
	}

	public void setMobelVersion(String mobelVersion) {
		this.mobelVersion = mobelVersion;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSysVersion() {
		return sysVersion;
	}

	public void setSysVersion(String sysVersion) {
		this.sysVersion = sysVersion;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Integer getMapType() {
		return mapType;
	}

	public void setMapType(Integer mapType) {
		this.mapType = mapType;
	}

	public Long getEstimatedAmount() {
		return estimatedAmount;
	}

	public void setEstimatedAmount(Long estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}

	public Double getEstimatedAmountYuan() {
		return estimatedAmountYuan;
	}

	public void setEstimatedAmountYuan(Double estimatedAmountYuan) {
		this.estimatedAmountYuan = estimatedAmountYuan;
	}

	public Integer getIsEat() {
		return isEat;
	}

	public void setIsEat(Integer isEat) {
		this.isEat = isEat;
	}

	public Integer getIsLive() {
		return isLive;
	}

	public void setIsLive(Integer isLive) {
		this.isLive = isLive;
	}

	public Integer getIsParking() {
		return isParking;
	}

	public void setIsParking(Integer isParking) {
		this.isParking = isParking;
	}

	public Integer getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(Integer isReturn) {
		this.isReturn = isReturn;
	}

	public Integer getLuggageCount() {
		return luggageCount;
	}

	public void setLuggageCount(Integer luggageCount) {
		this.luggageCount = luggageCount;
	}

	public String getAirlineNo() {
		return airlineNo;
	}

	public void setAirlineNo(String airlineNo) {
		this.airlineNo = airlineNo;
	}

	public String getMultiBookingDate() {
		return multiBookingDate;
	}

	public void setMultiBookingDate(String multiBookingDate) {
		this.multiBookingDate = multiBookingDate;
	}

	public String getMultiPoint() {
		return multiPoint;
	}

	public void setMultiPoint(String multiPoint) {
		this.multiPoint = multiPoint;
	}

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

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(Integer payChannel) {
		this.payChannel = payChannel;
	}

	public Integer getPayFlag() {
		return payFlag;
	}

	public void setPayFlag(Integer payFlag) {
		this.payFlag = payFlag;
	}

	public Integer getPushDriverType() {
		return pushDriverType;
	}

	public void setPushDriverType(Integer pushDriverType) {
		this.pushDriverType = pushDriverType;
	}

	public Integer getRiderCount() {
		return riderCount;
	}

	public void setRiderCount(Integer riderCount) {
		this.riderCount = riderCount;
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

	public Integer getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(Integer serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public Integer getSpecialLineId() {
		return specialLineId;
	}

	public void setSpecialLineId(Integer specialLineId) {
		this.specialLineId = specialLineId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Date getBindDriverDate() {
		return bindDriverDate;
	}

	public void setBindDriverDate(Date bindDriverDate) {
		this.bindDriverDate = bindDriverDate;
	}

	public Date getStartOffDate() {
		return startOffDate;
	}

	public void setStartOffDate(Date startOffDate) {
		this.startOffDate = startOffDate;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public Date getStartServiceDate() {
		return startServiceDate;
	}

	public void setStartServiceDate(Date startServiceDate) {
		this.startServiceDate = startServiceDate;
	}

	public Date getEndServiceDate() {
		return endServiceDate;
	}

	public void setEndServiceDate(Date endServiceDate) {
		this.endServiceDate = endServiceDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
