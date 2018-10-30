package com.zhuanche.serv.order.elasticsearch;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.JSON;

/**订单信息**/
public class OrderInfoDTO{
	private int     orderId;  //订单id
	private String orderNo;//订单号
	private long saveTime;//保存到es的时间戳
	private int     pushDriverType;//订单指派方式
	private int     airportId;//是否拼车 1拼车 其余不是
	private int     type;//订单类别
	private String riderName;//乘车人姓名
	private String riderPhone;//乘车人手机号
	private Date   createDate;//下单时间yyyy-MM-dd HH:mm:ss
	private int     serviceTypeId;//服务类型Id
	private int     carGroupId;//车型id
	private int     status;//订单状态
	private int     cityId;//订单城市id
	private String cityIdBatch;//订单城市id
	private int     driverId;//司机id
	private String bookingStartAddr;//预定上车地址
	private String bookingStartPoint;//预定上车坐标点
	private String bookingEndAddr;//预定下车地址
	private String bookingEndPoint;//预定下车坐标点
	private String factStartAddr;//实际上车地址
	private String factEndAddr;//实际下车地址
	private Date   bookingDate;//预约上车时间
	private String factStartPoint;//实际上车坐标点
	private String factEndPoint;//实际下车坐标点
	private String orderType;//订单类型
	private String channelsNum;//渠道号
	private String memo;//订单表memo
	private String charteredOrderNo;//日租半日租主表 订单号
	private int     charteredId;//日租半日租主表 主键
	private int     estimatedId;//预估id
	private int     businessId;//机构ID
	private String licensePlates;//车牌号
	private int     goHomeStatus;//回家模式
	private int     selectedPayFlag;//订单表中选择的支付方式
	private int     autoLevelUp;//车辆升级
	private int     buyoutFlag;//一口价标识
	private String buyoutPrice;//	一口价
	private String cityName;//下单城市
	private String driverName;//司机姓名
	private String driverPhone;//司机手机号
	private int     supplierId;//供应商id
	private int     groupId;//车型id
	private int     serviceCity;//司机服务城市i
	private String supplierFullName;//供应商
	private String serviceName;//服务类型
	private int     serviceId;//服务类型id
	private String serviceNo;//服务类别no
	private int     teamId;//车队ID
	private String teamName;//	车队名称
	private int    teamClassId ;//小组id
	private String teamClassName;//小组名称
	private String groupName;//车型类别
	private String dicName;//订单状态名称
	private String dicValue;//订单状态
	private String bookingUserName;//乘客姓名
	private String bookingUserPhone;//乘客手机号
	private int     bookingUserId;//乘客id
	private int     travelTime;//乘车时长
	private BigDecimal travelMileage; //乘车里程
	private BigDecimal actualPayAmount; //实际支付金额
	private int     couponId;//判断是否使用优惠券
	private Date  costEndDate; //完成时间yyyy-MM-dd HH:mm:ss
	private BigDecimal couponAmount; //优惠劵支付（元）
	private int     settleDetailId;//结算主键id
	private Date   tencentDate;//下单时间yyyy-MM-dd HH:mm:ss
	private String tencentRidePhone;//乘客手机号
	private Date   tencentCreateTime;//创建时间
	private BigDecimal jiesuanPrice; //结算价
	private BigDecimal shijijiaPrice; //实际价
	private BigDecimal estimatedAmount; //预估价
	
	private int     mainOrderId;//拼车单的主单id
	private String mainOrderNo;//拼车单的主单号
	private int     orderTotalNum;//拼车单子单数
	private int     finishOrderNum;//拼车单完成订单数
	private String driverStartShortAddr;//拼车单开始服务地址
	private Date   driverStartDate;//拼车单开始服务时间yyyy-MM-dd HH:mm:ss
	private String driverEndShortAddr;//拼车单结束服务地址
	private Date   driverEndDate;//拼车单结束服务时间yyyy-MM-dd HH:mm:ss
	private BigDecimal driverTotalMileage; //拼车单行驶总里程
	private BigDecimal driverTotalTime; //拼车单行驶总时长
	private BigDecimal driverTotalFee; //拼车单行驶金额
	
	private String cancelMemo;//取消原因
	private int     cancelStatus;//取消来源
	private String cancelType;//取消类型
	private Date   cancelUpdateDate;//取消更新时间yyyy-MM-dd HH:mm:ss
	private Date   cancelCreateDate;//取消时间yyyy-MM-dd HH:mm:ss
	private String companyName;//机构名称
	private int     multiOrderId;//多日主单id
	private String multiOrderNo;//多日主单订单号
	private int     multiSubOrderCount;//多日子单数量
	
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public long getSaveTime() {
		return saveTime;
	}
	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}
	public int getPushDriverType() {
		return pushDriverType;
	}
	public void setPushDriverType(int pushDriverType) {
		this.pushDriverType = pushDriverType;
	}
	public int getAirportId() {
		return airportId;
	}
	public void setAirportId(int airportId) {
		this.airportId = airportId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(int serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public int getCarGroupId() {
		return carGroupId;
	}
	public void setCarGroupId(int carGroupId) {
		this.carGroupId = carGroupId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public String getCityIdBatch() {
		return cityIdBatch;
	}
	public void setCityIdBatch(String cityIdBatch) {
		this.cityIdBatch = cityIdBatch;
	}
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public String getBookingStartAddr() {
		return bookingStartAddr;
	}
	public void setBookingStartAddr(String bookingStartAddr) {
		this.bookingStartAddr = bookingStartAddr;
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
	public String getBookingEndPoint() {
		return bookingEndPoint;
	}
	public void setBookingEndPoint(String bookingEndPoint) {
		this.bookingEndPoint = bookingEndPoint;
	}
	public String getFactStartAddr() {
		return factStartAddr;
	}
	public void setFactStartAddr(String factStartAddr) {
		this.factStartAddr = factStartAddr;
	}
	public String getFactEndAddr() {
		return factEndAddr;
	}
	public void setFactEndAddr(String factEndAddr) {
		this.factEndAddr = factEndAddr;
	}
	public Date getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}
	public String getFactStartPoint() {
		return factStartPoint;
	}
	public void setFactStartPoint(String factStartPoint) {
		this.factStartPoint = factStartPoint;
	}
	public String getFactEndPoint() {
		return factEndPoint;
	}
	public void setFactEndPoint(String factEndPoint) {
		this.factEndPoint = factEndPoint;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getChannelsNum() {
		return channelsNum;
	}
	public void setChannelsNum(String channelsNum) {
		this.channelsNum = channelsNum;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCharteredOrderNo() {
		return charteredOrderNo;
	}
	public void setCharteredOrderNo(String charteredOrderNo) {
		this.charteredOrderNo = charteredOrderNo;
	}
	public int getCharteredId() {
		return charteredId;
	}
	public void setCharteredId(int charteredId) {
		this.charteredId = charteredId;
	}
	public int getEstimatedId() {
		return estimatedId;
	}
	public void setEstimatedId(int estimatedId) {
		this.estimatedId = estimatedId;
	}
	public int getBusinessId() {
		return businessId;
	}
	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}
	public String getLicensePlates() {
		return licensePlates;
	}
	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}
	public int getGoHomeStatus() {
		return goHomeStatus;
	}
	public void setGoHomeStatus(int goHomeStatus) {
		this.goHomeStatus = goHomeStatus;
	}
	public int getSelectedPayFlag() {
		return selectedPayFlag;
	}
	public void setSelectedPayFlag(int selectedPayFlag) {
		this.selectedPayFlag = selectedPayFlag;
	}
	public int getAutoLevelUp() {
		return autoLevelUp;
	}
	public void setAutoLevelUp(int autoLevelUp) {
		this.autoLevelUp = autoLevelUp;
	}
	public int getBuyoutFlag() {
		return buyoutFlag;
	}
	public void setBuyoutFlag(int buyoutFlag) {
		this.buyoutFlag = buyoutFlag;
	}
	public String getBuyoutPrice() {
		return buyoutPrice;
	}
	public void setBuyoutPrice(String buyoutPrice) {
		this.buyoutPrice = buyoutPrice;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
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
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getServiceCity() {
		return serviceCity;
	}
	public void setServiceCity(int serviceCity) {
		this.serviceCity = serviceCity;
	}
	public String getSupplierFullName() {
		return supplierFullName;
	}
	public void setSupplierFullName(String supplierFullName) {
		this.supplierFullName = supplierFullName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceNo() {
		return serviceNo;
	}
	public void setServiceNo(String serviceNo) {
		this.serviceNo = serviceNo;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getTeamClassId() {
		return teamClassId;
	}
	public void setTeamClassId(int teamClassId) {
		this.teamClassId = teamClassId;
	}
	public String getTeamClassName() {
		return teamClassName;
	}
	public void setTeamClassName(String teamClassName) {
		this.teamClassName = teamClassName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDicName() {
		return dicName;
	}
	public void setDicName(String dicName) {
		this.dicName = dicName;
	}
	public String getDicValue() {
		return dicValue;
	}
	public void setDicValue(String dicValue) {
		this.dicValue = dicValue;
	}
	public String getBookingUserName() {
		return bookingUserName;
	}
	public void setBookingUserName(String bookingUserName) {
		this.bookingUserName = bookingUserName;
	}
	public String getBookingUserPhone() {
		return bookingUserPhone;
	}
	public void setBookingUserPhone(String bookingUserPhone) {
		this.bookingUserPhone = bookingUserPhone;
	}
	public int getBookingUserId() {
		return bookingUserId;
	}
	public void setBookingUserId(int bookingUserId) {
		this.bookingUserId = bookingUserId;
	}
	public int getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}
	public BigDecimal getTravelMileage() {
		return travelMileage;
	}
	public void setTravelMileage(BigDecimal travelMileage) {
		this.travelMileage = travelMileage;
	}
	public BigDecimal getActualPayAmount() {
		return actualPayAmount;
	}
	public void setActualPayAmount(BigDecimal actualPayAmount) {
		this.actualPayAmount = actualPayAmount;
	}
	public int getCouponId() {
		return couponId;
	}
	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}
	public Date getCostEndDate() {
		return costEndDate;
	}
	public void setCostEndDate(Date costEndDate) {
		this.costEndDate = costEndDate;
	}
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	public int getSettleDetailId() {
		return settleDetailId;
	}
	public void setSettleDetailId(int settleDetailId) {
		this.settleDetailId = settleDetailId;
	}
	public Date getTencentDate() {
		return tencentDate;
	}
	public void setTencentDate(Date tencentDate) {
		this.tencentDate = tencentDate;
	}
	public String getTencentRidePhone() {
		return tencentRidePhone;
	}
	public void setTencentRidePhone(String tencentRidePhone) {
		this.tencentRidePhone = tencentRidePhone;
	}
	public Date getTencentCreateTime() {
		return tencentCreateTime;
	}
	public void setTencentCreateTime(Date tencentCreateTime) {
		this.tencentCreateTime = tencentCreateTime;
	}
	public BigDecimal getJiesuanPrice() {
		return jiesuanPrice;
	}
	public void setJiesuanPrice(BigDecimal jiesuanPrice) {
		this.jiesuanPrice = jiesuanPrice;
	}
	public BigDecimal getShijijiaPrice() {
		return shijijiaPrice;
	}
	public void setShijijiaPrice(BigDecimal shijijiaPrice) {
		this.shijijiaPrice = shijijiaPrice;
	}
	public BigDecimal getEstimatedAmount() {
		return estimatedAmount;
	}
	public void setEstimatedAmount(BigDecimal estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}
	public int getMainOrderId() {
		return mainOrderId;
	}
	public void setMainOrderId(int mainOrderId) {
		this.mainOrderId = mainOrderId;
	}
	public String getMainOrderNo() {
		return mainOrderNo;
	}
	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}
	public int getOrderTotalNum() {
		return orderTotalNum;
	}
	public void setOrderTotalNum(int orderTotalNum) {
		this.orderTotalNum = orderTotalNum;
	}
	public int getFinishOrderNum() {
		return finishOrderNum;
	}
	public void setFinishOrderNum(int finishOrderNum) {
		this.finishOrderNum = finishOrderNum;
	}
	public String getDriverStartShortAddr() {
		return driverStartShortAddr;
	}
	public void setDriverStartShortAddr(String driverStartShortAddr) {
		this.driverStartShortAddr = driverStartShortAddr;
	}
	public Date getDriverStartDate() {
		return driverStartDate;
	}
	public void setDriverStartDate(Date driverStartDate) {
		this.driverStartDate = driverStartDate;
	}
	public String getDriverEndShortAddr() {
		return driverEndShortAddr;
	}
	public void setDriverEndShortAddr(String driverEndShortAddr) {
		this.driverEndShortAddr = driverEndShortAddr;
	}
	public Date getDriverEndDate() {
		return driverEndDate;
	}
	public void setDriverEndDate(Date driverEndDate) {
		this.driverEndDate = driverEndDate;
	}
	public BigDecimal getDriverTotalMileage() {
		return driverTotalMileage;
	}
	public void setDriverTotalMileage(BigDecimal driverTotalMileage) {
		this.driverTotalMileage = driverTotalMileage;
	}
	public BigDecimal getDriverTotalTime() {
		return driverTotalTime;
	}
	public void setDriverTotalTime(BigDecimal driverTotalTime) {
		this.driverTotalTime = driverTotalTime;
	}
	public BigDecimal getDriverTotalFee() {
		return driverTotalFee;
	}
	public void setDriverTotalFee(BigDecimal driverTotalFee) {
		this.driverTotalFee = driverTotalFee;
	}
	public String getCancelMemo() {
		return cancelMemo;
	}
	public void setCancelMemo(String cancelMemo) {
		this.cancelMemo = cancelMemo;
	}
	public int getCancelStatus() {
		return cancelStatus;
	}
	public void setCancelStatus(int cancelStatus) {
		this.cancelStatus = cancelStatus;
	}
	public String getCancelType() {
		return cancelType;
	}
	public void setCancelType(String cancelType) {
		this.cancelType = cancelType;
	}
	public Date getCancelUpdateDate() {
		return cancelUpdateDate;
	}
	public void setCancelUpdateDate(Date cancelUpdateDate) {
		this.cancelUpdateDate = cancelUpdateDate;
	}
	public Date getCancelCreateDate() {
		return cancelCreateDate;
	}
	public void setCancelCreateDate(Date cancelCreateDate) {
		this.cancelCreateDate = cancelCreateDate;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public int getMultiOrderId() {
		return multiOrderId;
	}
	public void setMultiOrderId(int multiOrderId) {
		this.multiOrderId = multiOrderId;
	}
	public String getMultiOrderNo() {
		return multiOrderNo;
	}
	public void setMultiOrderNo(String multiOrderNo) {
		this.multiOrderNo = multiOrderNo;
	}
	public int getMultiSubOrderCount() {
		return multiSubOrderCount;
	}
	public void setMultiSubOrderCount(int multiSubOrderCount) {
		this.multiSubOrderCount = multiSubOrderCount;
	}
	
	@Override
	public String toString() {
		return JSON.toJSONString(this, true);
	}
}
