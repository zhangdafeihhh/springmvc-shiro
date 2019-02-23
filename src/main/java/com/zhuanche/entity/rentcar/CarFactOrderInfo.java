package com.zhuanche.entity.rentcar;


import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import com.zhuanche.entity.common.BaseEntity;

/**
 * 订单明细页面实体（老车管）
 * @author jdd
 * @version 1.0
 * @since 1.0
 */

public class CarFactOrderInfo extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.lang.String riderName;
	private java.lang.String riderPhone;
	private java.lang.String serviceName;
	private int cityId;
	private Integer pushDriverType;
	private java.lang.String cityName;
	private String bookingStartAddr;
	private String bookingEndAddr;
	private String bookingStartPoint;
	private String bookingEndPoint;
	private String factStartAddr;
	private String factEndAddr;
	private String bookingDateStr;
	private String factDateStr;
	private String driverName;
	private String licensePlates;
	private String bookingGroupName;
	private String factGroupName;
	private Double travelTime;
	private Double travelMileage;
	private String memo;
	// 订单id
	private long orderId;
	// 下单时间
	private String createdate;
	// 预订人姓名
	private String bookingname;
	// 预订人手机号
	private String bookingphone;
	// 司机手机号
	private String driverphone;
	// 司机用车类型
	private String modeldetail;
	// 司机姓名
	private String drivername;
	// 异议的人
	private String yyperson;
	// 异议的原因
	private String yymemo;
	// 异议的时间
	private String yydate;
	// 取消时间
	private String qxdate;
	// 取消操作人
	private String qxperson;
	// 取消人
	private String qxreasonname;
	// 取消原因
	private String qxmemo;
	// 减免人
	private String jmname;
	// 减免时间
	private String jmdate;
	// 减免原因
	private String jmreason;
	// 减免金额
	private Double jmprice;
	// 减免订单最终金额
	private Double surplus;
	// 夜间里程数
	private double nightdistancenum;
	// 夜间里程费
	private double nightdistanceprice;
	// 减免的费用
	private Double reductiontotalprice;
	// 费用明细表的id
	private long detailId;
	// 付款人
	private String payperson;
	// 账户支付
	private Double customeramount;
	// 司机代付
	private Double paydriver;
	// 预定车型id
	private String bookinggroupids;
	// 信用卡支付
	private Double creditPay;
	// 提出异议的端口 0:pc端口 1：手机端口
	private int yystatus;
	// 微信 支付金额
	private Double weixin;
	// 支付宝 支付金额
	private Double zfb;
	// 取消原因端口
	private int qxcancelstatus;
	// 实际上车时间
	private String factDate;
	// 实际下车时间
	private String factEndDate;
	// 预约车型
	private String bookinkGroupids;
	// 预约车型的名字
	private String bookingGroupnames;

	// 抹零
	private Double decimalsFees;
	// 乘客实际支付
	private Double actualPayAmount;
	// 用户id
	private int cutomerId;
	// 用户 电话
	private String customerPhone;

	// 乘客信用卡支付
	private Double paymentCustomer;
	// 司机信用卡代收
	private Double paymentDriver;
	// 优惠券 实际抵扣面值
	private Double couponsAmount;
	// 充值账户支付
	private Double changeAmount;

	// 赠送账户支付
	private Double giftAmount;

	// 司机信用卡 支付
	private Double driverCreditcardAmount;

	// 司机代收金额

	private Double driverCashAmount;

	private Double customerRejectPay;
	// ADD lwl 2016-1-8 begin
	// 高峰时常费
	private Double hotDurationFees;
	// 高峰里程费
	private Double hotMileageFees;
	// 夜间时长费
	private Double nighitDurationFees;
	// 夜间时长
	private Double nighitDuration;
	// 高峰时长
	private Double hotDuration;
	// 高峰里程
	private Double hotMileage;
	// 付款人
	private Integer payFlag;
	//长途里程(公里)
	private Double distantNum=0.0;
	//长途费(元)
	private Double distantFee=0.0;
	
	// 优惠券 支付的钱
	private Double amount = 0.0;
	// 费用总计
	private Double totalAmount = 0.0;
	// 里程费
	private Double overMileagePrice = 0.0;
	// 时常费
	private Double overTimePrice = 0.0;
	// 空驶费
	private Double longDistancePrice = 0.0;
	private String costTypeName;
	private Long cost = 0L;
	// 停车目录
	private String costTypeNameTc;
	// 高速目录
	private String costTypeNameGs;
	// 其他目录
	private String costTypeNameQt;
	// 机场目录
	private String costTypeNameJc;
	// 食宿目录
	private String costTypeNameYj;
	// 停车费
	private Long costTypeNameTcPrice = 0L;
	//
	private Long costTypeNameGsPrice = 0L;
	// 高速费
	private Long costTypeNameQtPrice = 0L;
	// 机场服务费
	private Long costTypeNameJcPrice = 0L;
	// 食宿费
	private Long costTypeNameYjPrice = 0L;

	private int status;
	private int serviceTypeId;
	private String airlineNo;
	private int bookingUserId;
	private String driverId;
	// 基础价格
	private Double basePrice = 0.0;
	// 分钟数（含）
	private int includeminute = 0;
	// 公里数（含）
	private int includemileage = 0;

	private String factDriver;
	// 超里程数
	private Double overMileageNum = 0.0;
	// 超时分钟
	private Double overTimeNum = 0.0;
	// 空驶里程数
	private Double longDistanceNum = 0.0;
	private Double longdistanceprice = 0.0;
	private Double outServiceMileage = 0.0;
	private Double outServicePrice = 0.0;
	private Double nightServiceMileage = 0.0;
	private Double nightServicePrice = 0.0;
	private Double forecastAmount = 0.0;

	// 更新时间
	private String updatedate;
	// 指定司机附加费
	private double designatedDriverFee;
	// 机构订单存储机构ID
	private Integer businessId;
	// 订单类型 1普通订单 2机构订单
	private Integer type;
	// 身份证号
	private String bookingIdNumber;
	// 渠道号 addBy lwl 2015-10-21
	private String channelsNum;
	// 司机等待费
	private double waitingFee;
	// 司机等待时长
	private double waitingMinutes;
	// 使用的优惠券1 还是折扣券2
	private double couponsType;
	// 百度 携程 支付的钱数
	private Double baiDuOrCtripPrice;

	private Double posPay;// pos机支付
	// end

	// 2016-06-06日添加
	private Integer buyoutFlag;
	private Double buyoutPrice;

	private int couponId;
	private String noPage;
	// 费用总计
	private Date airlinePlanDate;

	//=========================
	// 用车类型 1随叫随到 2预约用车
	private Integer useCarFlag;
	// 航班日期
	private String airlineDate;
	// 预定人手机号
	private String bookingUserPhone;
	// 订单号
	private String orderNo;
	// 预定上车时间
	private Date bookingDate;
	// 订单创建时间
	private String cretaeDate;
	// 实际上车地点坐标
	private java.lang.String factStartPoint;
	// 实际下车地点坐标
	private java.lang.String factEndPoint;
	// 车组ID
	private java.lang.Integer carGroupId;
	// 机场(航站楼)ID
	private java.lang.Integer airportId;
	// 司机手机号
	private String driverPhone;
	// 优惠券金额
	private Double couponAmount;
	// 计划ID
	private String planId;
	// 计划版本号
	private String planVer;
	// 价格ID
	private Integer priceId;
	// 司机提成
	private Double driverGet;
	// 所用时长
	private String allTime;
	// 所用里程
	private String mileage;
	// 套餐费用
	private Double pac;
	// 套餐含分钟
	private String pacIncludeMinute;
	// 套餐含公里
	private String pacIncludeMileage;
	// 超时数
	private String outMin;
	// 超公里数
	private String outMil;
	// 超出里程费用
	private Double veryLong;
	// 超时费用
	private Double outTime;
	// 长途里程
	private String longMileage;
	// 夜间里程
	private String nightMileage;
	// 高速费(价外费用)
	private Double gsFees;
	// 停车费(价外费用)
	private Double tcFees;
	// 机场服务费(价外费用)
	private Double jcFees;
	// 夜间服务费
	private Double yjFees;
	// 空驶(长途费)
	private Double ksFees;
	// 食宿费(价外费用)
	private Double ssFees;
	// 司机代收金额
	private Double driverPay;
	// 司机预计代收金额
	private Double driverAdvance;
	// 服务开始时间（开始服务）
	private Date startdate;
	// 服务结束时间（服务完成）
	private Date enddate;
	// 航班状态(同时存在两条航班状态字段<需要优化>)
	private String flightState;
	private Integer airlineStatus;

	// 航班计划到达时间 addBy xiao 2015-08-07
	private String airlineArrDate;
	// 航班出发地三字码 addBy xiao 2015-08-06
	private String airlineDepCode;
	// 航班目的地三字码 addBy xiao 2015-08-06
	private String airlineArrCode;

	// 预订人是否接收短信 1 接收 2不接收
	private int receiveSMS;

	// 订单的预估费用
	private Double estimatedAmount;

	// IMEI 号
	private String imei;

	// 软件版本号
	private String version;

	// 手机品牌型号
	private String mobelVersion;

	// 操作系统版本号
	private String sysVersion;

	// 平台
	private String platform;
	// 日租半日组 订单id
	private int charteredId;

	// 日租半日租 订单号
	private String charteredOrderNo;
	// 是否代人叫车，0:不是；1:是
	private Integer isOrderOthers;
	// addBy xiao 2015-11-25 门童代人叫车乘车人是自己且乘车人付款时, 支付方式(1:系统支付 | 2:现金支付)
	private Integer doormanPayMethod;
	// addBy xiao 2015-11-25 代人叫车时预定人选择的谁付款(0:预订人 | 1:乘车人)
	private Integer selectedPayFlag;
	
	// add by zhou 20160407 增加一口价标志位&一口价价格
	private Integer buyOutFlag;
	private Double buyOutPrice;
	
	//是否自动升级
	private int autoLevelUp;
	//预定车型
	private String  bookingGroupIds;
	//addBy zsc 2016-5-10 指定司机门槛时查询取消指定司机最近的订单时间 
	private Date updateDate;
	//==================================
	
	//2017-05-12日添加
	private Double cancelOrderPenalty;//取消订单费
	private Double languageServiceFee;//语言服务费
	
	private Double depositAccountAmount;//充值账户预付定金结算
	private Double depositGiftAmount;//赠送账户预付定金结算
	private Double depositCreditAmount;//信用卡预付定金结算
	
	private Double depositSettleAmount;//待结算预付定金结算
	private Double pendingSettleAmount;//待结算账户结算金额
	
	//司机等候费
	private Double waitingPrice;
	//司机等候时长
	private Double waitingTime;
	
	private double passengerPendingPay;
	private String settleDate;
	private String payType;
	
	private String distantDetail;
	
	private String channelDiscountDriver;

	// 司机出发时间
	private String driverBeginTime;
	// 司机到达时间
	private String driverArriveTime;
	// 司机开始服务时间
	private String driverStartServiceTime;
	// 司机结算时间
	private String driverOrderCoformTime;
	// 司机服务完成时间
	private String driverOrderEndTime;
	// 订单取消时间
	private String orderCancleTime;
		
	//2018-08-13日添加
	private String mainOrderNo;//主订单号

	//等待时间明细
	private List<CarBizOrderWaitingPeriod> carBizOrderWaitingPeriodList;
	
	public String getMainOrderNo() {
		return mainOrderNo;
	}

	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}

	public String getChannelDiscountDriver() {
		return channelDiscountDriver;
	}

	public void setChannelDiscountDriver(String channelDiscountDriver) {
		this.channelDiscountDriver = channelDiscountDriver;
	}

	public Double getWaitingPrice() {
		return waitingPrice;
	}

	public void setWaitingPrice(Double waitingPrice) {
		this.waitingPrice = waitingPrice;
	}

	public Double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(Double waitingTime) {
		this.waitingTime = waitingTime;
	}

	public double getPassengerPendingPay() {
		return passengerPendingPay;
	}

	public void setPassengerPendingPay(double passengerPendingPay) {
		this.passengerPendingPay = passengerPendingPay;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getDistantDetail() {
		return distantDetail;
	}

	public void setDistantDetail(String distantDetail) {
		this.distantDetail = distantDetail;
	}

	public Double getCancelOrderPenalty() {
		return cancelOrderPenalty;
	}

	public void setCancelOrderPenalty(Double cancelOrderPenalty) {
		this.cancelOrderPenalty = cancelOrderPenalty;
	}

	public Double getLanguageServiceFee() {
		return languageServiceFee;
	}

	public void setLanguageServiceFee(Double languageServiceFee) {
		this.languageServiceFee = languageServiceFee;
	}

	public Double getDepositAccountAmount() {
		return depositAccountAmount;
	}

	public void setDepositAccountAmount(Double depositAccountAmount) {
		this.depositAccountAmount = depositAccountAmount;
	}

	public Double getDepositGiftAmount() {
		return depositGiftAmount;
	}

	public void setDepositGiftAmount(Double depositGiftAmount) {
		this.depositGiftAmount = depositGiftAmount;
	}

	public Double getDepositCreditAmount() {
		return depositCreditAmount;
	}

	public void setDepositCreditAmount(Double depositCreditAmount) {
		this.depositCreditAmount = depositCreditAmount;
	}

	public Double getDepositSettleAmount() {
		return depositSettleAmount;
	}

	public void setDepositSettleAmount(Double depositSettleAmount) {
		this.depositSettleAmount = depositSettleAmount;
	}

	public Double getPendingSettleAmount() {
		return pendingSettleAmount;
	}

	public void setPendingSettleAmount(Double pendingSettleAmount) {
		this.pendingSettleAmount = pendingSettleAmount;
	}

	public String getUpdatedate() {
		return updatedate;
	}

	public Integer getUseCarFlag() {
		return useCarFlag;
	}

	public void setUseCarFlag(Integer useCarFlag) {
		this.useCarFlag = useCarFlag;
	}

	public String getAirlineDate() {
		return airlineDate;
	}

	public void setAirlineDate(String airlineDate) {
		this.airlineDate = airlineDate;
	}

	public String getBookingUserPhone() {
		return bookingUserPhone;
	}

	public void setBookingUserPhone(String bookingUserPhone) {
		this.bookingUserPhone = bookingUserPhone;
	}

	public java.lang.String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(java.lang.String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getCretaeDate() {
		return cretaeDate;
	}

	public void setCretaeDate(String cretaeDate) {
		this.cretaeDate = cretaeDate;
	}

	public java.lang.String getFactStartPoint() {
		return factStartPoint;
	}

	public void setFactStartPoint(java.lang.String factStartPoint) {
		this.factStartPoint = factStartPoint;
	}

	public java.lang.String getFactEndPoint() {
		return factEndPoint;
	}

	public void setFactEndPoint(java.lang.String factEndPoint) {
		this.factEndPoint = factEndPoint;
	}

	public java.lang.Integer getCarGroupId() {
		return carGroupId;
	}

	public void setCarGroupId(java.lang.Integer carGroupId) {
		this.carGroupId = carGroupId;
	}

	public java.lang.Integer getAirportId() {
		return airportId;
	}

	public void setAirportId(java.lang.Integer airportId) {
		this.airportId = airportId;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public Double getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(Double couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getPlanVer() {
		return planVer;
	}

	public void setPlanVer(String planVer) {
		this.planVer = planVer;
	}

	public Integer getPriceId() {
		return priceId;
	}

	public void setPriceId(Integer priceId) {
		this.priceId = priceId;
	}

	public Double getDriverGet() {
		return driverGet;
	}

	public void setDriverGet(Double driverGet) {
		this.driverGet = driverGet;
	}

	public String getAllTime() {
		return allTime;
	}

	public void setAllTime(String allTime) {
		this.allTime = allTime;
	}

	public String getMileage() {
		return mileage;
	}

	public void setMileage(String mileage) {
		this.mileage = mileage;
	}

	public Double getPac() {
		return pac;
	}

	public void setPac(Double pac) {
		this.pac = pac;
	}

	public String getPacIncludeMinute() {
		return pacIncludeMinute;
	}

	public void setPacIncludeMinute(String pacIncludeMinute) {
		this.pacIncludeMinute = pacIncludeMinute;
	}

	public String getPacIncludeMileage() {
		return pacIncludeMileage;
	}

	public void setPacIncludeMileage(String pacIncludeMileage) {
		this.pacIncludeMileage = pacIncludeMileage;
	}

	public String getOutMin() {
		return outMin;
	}

	public void setOutMin(String outMin) {
		this.outMin = outMin;
	}

	public String getOutMil() {
		return outMil;
	}

	public void setOutMil(String outMil) {
		this.outMil = outMil;
	}

	public Double getVeryLong() {
		return veryLong;
	}

	public void setVeryLong(Double veryLong) {
		this.veryLong = veryLong;
	}

	public Double getOutTime() {
		return outTime;
	}

	public void setOutTime(Double outTime) {
		this.outTime = outTime;
	}

	public String getLongMileage() {
		return longMileage;
	}

	public void setLongMileage(String longMileage) {
		this.longMileage = longMileage;
	}

	public String getNightMileage() {
		return nightMileage;
	}

	public void setNightMileage(String nightMileage) {
		this.nightMileage = nightMileage;
	}

	public Double getGsFees() {
		return gsFees;
	}

	public void setGsFees(Double gsFees) {
		this.gsFees = gsFees;
	}

	public Double getTcFees() {
		return tcFees;
	}

	public void setTcFees(Double tcFees) {
		this.tcFees = tcFees;
	}

	public Double getJcFees() {
		return jcFees;
	}

	public void setJcFees(Double jcFees) {
		this.jcFees = jcFees;
	}

	public Double getYjFees() {
		return yjFees;
	}

	public void setYjFees(Double yjFees) {
		this.yjFees = yjFees;
	}

	public Double getKsFees() {
		return ksFees;
	}

	public void setKsFees(Double ksFees) {
		this.ksFees = ksFees;
	}

	public Double getSsFees() {
		return ssFees;
	}

	public void setSsFees(Double ssFees) {
		this.ssFees = ssFees;
	}

	public Double getDriverPay() {
		return driverPay;
	}

	public void setDriverPay(Double driverPay) {
		this.driverPay = driverPay;
	}

	public Double getDriverAdvance() {
		return driverAdvance;
	}

	public void setDriverAdvance(Double driverAdvance) {
		this.driverAdvance = driverAdvance;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getFlightState() {
		return flightState;
	}

	public void setFlightState(String flightState) {
		this.flightState = flightState;
	}

	public Integer getAirlineStatus() {
		return airlineStatus;
	}

	public void setAirlineStatus(Integer airlineStatus) {
		this.airlineStatus = airlineStatus;
	}

	public String getAirlineArrDate() {
		return airlineArrDate;
	}

	public void setAirlineArrDate(String airlineArrDate) {
		this.airlineArrDate = airlineArrDate;
	}

	public String getAirlineDepCode() {
		return airlineDepCode;
	}

	public void setAirlineDepCode(String airlineDepCode) {
		this.airlineDepCode = airlineDepCode;
	}

	public String getAirlineArrCode() {
		return airlineArrCode;
	}

	public void setAirlineArrCode(String airlineArrCode) {
		this.airlineArrCode = airlineArrCode;
	}

	public int getReceiveSMS() {
		return receiveSMS;
	}

	public void setReceiveSMS(int receiveSMS) {
		this.receiveSMS = receiveSMS;
	}

	public Double getEstimatedAmount() {
		return estimatedAmount;
	}

	public void setEstimatedAmount(Double estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMobelVersion() {
		return mobelVersion;
	}

	public void setMobelVersion(String mobelVersion) {
		this.mobelVersion = mobelVersion;
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

	public int getCharteredId() {
		return charteredId;
	}

	public void setCharteredId(int charteredId) {
		this.charteredId = charteredId;
	}

	public String getCharteredOrderNo() {
		return charteredOrderNo;
	}

	public void setCharteredOrderNo(String charteredOrderNo) {
		this.charteredOrderNo = charteredOrderNo;
	}

	public Integer getIsOrderOthers() {
		return isOrderOthers;
	}

	public void setIsOrderOthers(Integer isOrderOthers) {
		this.isOrderOthers = isOrderOthers;
	}

	public Integer getDoormanPayMethod() {
		return doormanPayMethod;
	}

	public void setDoormanPayMethod(Integer doormanPayMethod) {
		this.doormanPayMethod = doormanPayMethod;
	}

	public Integer getSelectedPayFlag() {
		return selectedPayFlag;
	}

	public void setSelectedPayFlag(Integer selectedPayFlag) {
		this.selectedPayFlag = selectedPayFlag;
	}

	public Integer getBuyOutFlag() {
		return buyOutFlag;
	}

	public void setBuyOutFlag(Integer buyOutFlag) {
		this.buyOutFlag = buyOutFlag;
	}

	public Double getBuyOutPrice() {
		return buyOutPrice;
	}

	public void setBuyOutPrice(Double buyOutPrice) {
		this.buyOutPrice = buyOutPrice;
	}

	public int getAutoLevelUp() {
		return autoLevelUp;
	}

	public void setAutoLevelUp(int autoLevelUp) {
		this.autoLevelUp = autoLevelUp;
	}

	public String getBookingGroupIds() {
		return bookingGroupIds;
	}

	public void setBookingGroupIds(String bookingGroupIds) {
		this.bookingGroupIds = bookingGroupIds;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

	public double getDesignatedDriverFee() {
		return designatedDriverFee;
	}

	public void setDesignatedDriverFee(double designatedDriverFee) {
		this.designatedDriverFee = designatedDriverFee;
	}

	public Integer getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getBookingIdNumber() {
		return bookingIdNumber;
	}

	public void setBookingIdNumber(String bookingIdNumber) {
		this.bookingIdNumber = bookingIdNumber;
	}

	public String getChannelsNum() {
		return channelsNum;
	}

	public void setChannelsNum(String channelsNum) {
		this.channelsNum = channelsNum;
	}

	public double getWaitingFee() {
		return waitingFee;
	}

	public void setWaitingFee(double waitingFee) {
		this.waitingFee = waitingFee;
	}

	public double getWaitingMinutes() {
		return waitingMinutes;
	}

	public void setWaitingMinutes(double waitingMinutes) {
		this.waitingMinutes = waitingMinutes;
	}

	public double getCouponsType() {
		return couponsType;
	}

	public void setCouponsType(double couponsType) {
		this.couponsType = couponsType;
	}

	public Double getBaiDuOrCtripPrice() {
		return baiDuOrCtripPrice;
	}

	public void setBaiDuOrCtripPrice(Double baiDuOrCtripPrice) {
		this.baiDuOrCtripPrice = baiDuOrCtripPrice;
	}

	public Double getPosPay() {
		return posPay;
	}

	public void setPosPay(Double posPay) {
		this.posPay = posPay;
	}

	public Integer getBuyoutFlag() {
		return buyoutFlag;
	}

	public void setBuyoutFlag(Integer buyoutFlag) {
		this.buyoutFlag = buyoutFlag;
	}

	public Double getBuyoutPrice() {
		return buyoutPrice;
	}

	public void setBuyoutPrice(Double buyoutPrice) {
		this.buyoutPrice = buyoutPrice;
	}

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}

	public String getNoPage() {
		return noPage;
	}

	public void setNoPage(String noPage) {
		this.noPage = noPage;
	}

	public Date getAirlinePlanDate() {
		return airlinePlanDate;
	}

	public void setAirlinePlanDate(Date airlinePlanDate) {
		this.airlinePlanDate = airlinePlanDate;
	}

	public Double getPaymentCustomer() {
		return paymentCustomer;
	}

	public void setPaymentCustomer(Double paymentCustomer) {
		this.paymentCustomer = paymentCustomer;
	}

	public Double getPaymentDriver() {
		return paymentDriver;
	}

	public void setPaymentDriver(Double paymentDriver) {
		this.paymentDriver = paymentDriver;
	}

	public Double getCouponsAmount() {
		return couponsAmount;
	}

	public void setCouponsAmount(Double couponsAmount) {
		this.couponsAmount = couponsAmount;
	}

	public Double getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(Double changeAmount) {
		this.changeAmount = changeAmount;
	}

	public Double getGiftAmount() {
		return giftAmount;
	}

	public void setGiftAmount(Double giftAmount) {
		this.giftAmount = giftAmount;
	}

	public Double getDriverCreditcardAmount() {
		return driverCreditcardAmount;
	}

	public void setDriverCreditcardAmount(Double driverCreditcardAmount) {
		this.driverCreditcardAmount = driverCreditcardAmount;
	}

	public Double getDriverCashAmount() {
		return driverCashAmount;
	}

	public void setDriverCashAmount(Double driverCashAmount) {
		this.driverCashAmount = driverCashAmount;
	}

	public Double getCustomerRejectPay() {
		return customerRejectPay;
	}

	public void setCustomerRejectPay(Double customerRejectPay) {
		this.customerRejectPay = customerRejectPay;
	}

	public Double getHotDurationFees() {
		return hotDurationFees;
	}

	public void setHotDurationFees(Double hotDurationFees) {
		this.hotDurationFees = hotDurationFees;
	}

	public Double getHotMileageFees() {
		return hotMileageFees;
	}

	public void setHotMileageFees(Double hotMileageFees) {
		this.hotMileageFees = hotMileageFees;
	}

	public Double getNighitDurationFees() {
		return nighitDurationFees;
	}

	public void setNighitDurationFees(Double nighitDurationFees) {
		this.nighitDurationFees = nighitDurationFees;
	}

	public Double getNighitDuration() {
		return nighitDuration;
	}

	public void setNighitDuration(Double nighitDuration) {
		this.nighitDuration = nighitDuration;
	}

	public Double getHotDuration() {
		return hotDuration;
	}

	public void setHotDuration(Double hotDuration) {
		this.hotDuration = hotDuration;
	}

	public Double getHotMileage() {
		return hotMileage;
	}

	public void setHotMileage(Double hotMileage) {
		this.hotMileage = hotMileage;
	}

	public Integer getPayFlag() {
		return payFlag;
	}

	public void setPayFlag(Integer payFlag) {
		this.payFlag = payFlag;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public Double getSurplus() {
		return surplus;
	}

	public void setSurplus(Double surplus) {
		this.surplus = surplus;
	}

	public int getCutomerId() {
		return cutomerId;
	}

	public void setCutomerId(int cutomerId) {
		this.cutomerId = cutomerId;
	}


	public Double getDecimalsFees() {
		return decimalsFees;
	}

	public void setDecimalsFees(Double decimalsFees) {
		this.decimalsFees = decimalsFees;
	}

	public Double getActualPayAmount() {
		return actualPayAmount;
	}

	public void setActualPayAmount(Double actualPayAmount) {
		this.actualPayAmount = actualPayAmount;
	}

	public String getBookingStartPoint() {
		return bookingStartPoint;
	}

	public void setBookingStartPoint(String bookingStartPoint) {
		this.bookingStartPoint = bookingStartPoint;
	}

	public String getBookingEndPoint() {
		return bookingEndPoint;
	}

	public void setBookingEndPoint(String bookingEndPoint) {
		this.bookingEndPoint = bookingEndPoint;
	}

	public String getBookingGroupnames() {
		return bookingGroupnames;
	}

	public void setBookingGroupnames(String bookingGroupnames) {
		this.bookingGroupnames = bookingGroupnames;
	}

	public String getFactDate() {
		return factDate;
	}

	public void setFactDate(String factDate) {
		this.factDate = factDate;
	}

	public String getFactEndDate() {
		return factEndDate;
	}

	public void setFactEndDate(String factEndDate) {
		this.factEndDate = factEndDate;
	}

	public String getBookinkGroupids() {
		return bookinkGroupids;
	}

	public void setBookinkGroupids(String bookinkGroupids) {
		this.bookinkGroupids = bookinkGroupids;
	}

	public String getBookinggroupids() {
		return bookinggroupids;
	}

	public void setBookinggroupids(String bookinggroupids) {
		this.bookinggroupids = bookinggroupids;
	}

	public int getQxcancelstatus() {
		return qxcancelstatus;
	}

	public void setQxcancelstatus(int qxcancelstatus) {
		this.qxcancelstatus = qxcancelstatus;
	}

	public Double getWeixin() {
		return weixin;
	}

	public void setWeixin(Double weixin) {
		this.weixin = weixin;
	}

	public Double getZfb() {
		return zfb;
	}

	public void setZfb(Double zfb) {
		this.zfb = zfb;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public double getNightdistanceprice() {
		return nightdistanceprice;
	}

	public void setNightdistanceprice(double nightdistanceprice) {
		this.nightdistanceprice = nightdistanceprice;
	}

	public Double getCreditPay() {
		return creditPay;
	}

	public void setCreditPay(Double creditPay) {
		this.creditPay = creditPay;
	}

	public int getYystatus() {
		return yystatus;
	}

	public void setYystatus(int yystatus) {
		this.yystatus = yystatus;
	}

	public Double getPaydriver() {
		return paydriver;
	}

	public void setPaydriver(Double paydriver) {
		this.paydriver = paydriver;
	}

	public Double getCustomeramount() {
		return customeramount;
	}

	public void setCustomeramount(Double customeramount) {
		this.customeramount = customeramount;
	}

	public String getPayperson() {
		return payperson;
	}

	public void setPayperson(String payperson) {
		this.payperson = payperson;
	}

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public Double getReductiontotalprice() {
		return reductiontotalprice;
	}

	public void setReductiontotalprice(Double reductiontotalprice) {
		this.reductiontotalprice = reductiontotalprice;
	}

	public double getNightdistancenum() {
		return nightdistancenum;
	}

	public void setNightdistancenum(double nightdistancenum) {
		this.nightdistancenum = nightdistancenum;
	}

	public String getJmname() {
		return jmname;
	}

	public void setJmname(String jmname) {
		this.jmname = jmname;
	}

	public String getJmdate() {
		return jmdate;
	}

	public void setJmdate(String jmdate) {
		this.jmdate = jmdate;
	}

	public String getJmreason() {
		return jmreason;
	}

	public void setJmreason(String jmreason) {
		this.jmreason = jmreason;
	}

	public Double getJmprice() {
		return jmprice;
	}

	public void setJmprice(Double jmprice) {
		this.jmprice = jmprice;
	}

	public String getQxdate() {
		return qxdate;
	}

	public void setQxdate(String qxdate) {
		this.qxdate = qxdate;
	}

	public String getQxperson() {
		return qxperson;
	}

	public void setQxperson(String qxperson) {
		this.qxperson = qxperson;
	}

	public String getQxreasonname() {
		return qxreasonname;
	}

	public void setQxreasonname(String qxreasonname) {
		this.qxreasonname = qxreasonname;
	}

	public String getQxmemo() {
		return qxmemo;
	}

	public void setQxmemo(String qxmemo) {
		this.qxmemo = qxmemo;
	}

	public String getYyperson() {
		return yyperson;
	}

	public void setYyperson(String yyperson) {
		this.yyperson = yyperson;
	}

	public String getYymemo() {
		return yymemo;
	}

	public void setYymemo(String yymemo) {
		this.yymemo = yymemo;
	}

	public String getYydate() {
		return yydate;
	}

	public void setYydate(String yydate) {
		this.yydate = yydate;
	}

	public String getDrivername() {
		return drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}

	public String getModeldetail() {
		return modeldetail;
	}

	public void setModeldetail(String modeldetail) {
		this.modeldetail = modeldetail;
	}

	public String getDriverphone() {
		return driverphone;
	}

	public void setDriverphone(String driverphone) {
		this.driverphone = driverphone;
	}

	public String getBookingname() {
		return bookingname;
	}

	public void setBookingname(String bookingname) {
		this.bookingname = bookingname;
	}

	public String getBookingphone() {
		return bookingphone;
	}

	public void setBookingphone(String bookingphone) {
		this.bookingphone = bookingphone;
	}

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getCostTypeNameJc() {
		return costTypeNameJc;
	}

	public void setCostTypeNameJc(String costTypeNameJc) {
		this.costTypeNameJc = costTypeNameJc;
	}

	public String getCostTypeNameYj() {
		return costTypeNameYj;
	}

	public void setCostTypeNameYj(String costTypeNameYj) {
		this.costTypeNameYj = costTypeNameYj;
	}

	public Long getCostTypeNameJcPrice() {
		return costTypeNameJcPrice;
	}

	public void setCostTypeNameJcPrice(Long costTypeNameJcPrice) {
		this.costTypeNameJcPrice = costTypeNameJcPrice;
	}

	public Long getCostTypeNameYjPrice() {
		return costTypeNameYjPrice;
	}

	public void setCostTypeNameYjPrice(Long costTypeNameYjPrice) {
		this.costTypeNameYjPrice = costTypeNameYjPrice;
	}

	public String getFactDriver() {
		return factDriver;
	}

	public void setFactDriver(String factDriver) {
		this.factDriver = factDriver;
	}

	public Double getOverMileageNum() {
		return overMileageNum;
	}

	public void setOverMileageNum(Double overMileageNum) {
		this.overMileageNum = overMileageNum;
	}

	public Double getLongDistanceNum() {
		return longDistanceNum;
	}

	public void setLongDistanceNum(Double longDistanceNum) {
		this.longDistanceNum = longDistanceNum;
	}

	public Double getOutServiceMileage() {
		return outServiceMileage;
	}

	public void setOutServiceMileage(Double outServiceMileage) {
		this.outServiceMileage = outServiceMileage;
	}

	public Double getOutServicePrice() {
		return outServicePrice;
	}

	public void setOutServicePrice(Double outServicePrice) {
		this.outServicePrice = outServicePrice;
	}

	public Double getNightServiceMileage() {
		return nightServiceMileage;
	}

	public void setNightServiceMileage(Double nightServiceMileage) {
		this.nightServiceMileage = nightServiceMileage;
	}

	public Double getNightServicePrice() {
		return nightServicePrice;
	}

	public void setNightServicePrice(Double nightServicePrice) {
		this.nightServicePrice = nightServicePrice;
	}

	public Double getForecastAmount() {
		return forecastAmount;
	}

	public void setForecastAmount(Double forecastAmount) {
		this.forecastAmount = forecastAmount;
	}

	public int getIncludeminute() {
		return includeminute;
	}

	public void setIncludeminute(int includeminute) {
		this.includeminute = includeminute;
	}

	public int getIncludemileage() {
		return includemileage;
	}

	public void setIncludemileage(int includemileage) {
		this.includemileage = includemileage;
	}

	public Double getOverTimeNum() {
		return overTimeNum;
	}

	public void setOverTimeNum(Double overTimeNum) {
		this.overTimeNum = overTimeNum;
	}

	public Double getLongdistanceprice() {
		return longdistanceprice;
	}

	public void setLongdistanceprice(Double longdistanceprice) {
		this.longdistanceprice = longdistanceprice;
	}

	
	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getServiceTypeId() {
		return serviceTypeId;
	}

	public void setServiceTypeId(int serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}

	public String getAirlineNo() {
		return airlineNo;
	}

	public void setAirlineNo(String airlineNo) {
		this.airlineNo = airlineNo;
	}

	public int getBookingUserId() {
		return bookingUserId;
	}

	public void setBookingUserId(int bookingUserId) {
		this.bookingUserId = bookingUserId;
	}

	public Long getCostTypeNameTcPrice() {
		return costTypeNameTcPrice;
	}

	public void setCostTypeNameTcPrice(Long costTypeNameTcPrice) {
		this.costTypeNameTcPrice = costTypeNameTcPrice;
	}

	public Long getCostTypeNameGsPrice() {
		return costTypeNameGsPrice;
	}

	public void setCostTypeNameGsPrice(Long costTypeNameGsPrice) {
		this.costTypeNameGsPrice = costTypeNameGsPrice;
	}

	public Long getCostTypeNameQtPrice() {
		return costTypeNameQtPrice;
	}

	public void setCostTypeNameQtPrice(Long costTypeNameQtPrice) {
		this.costTypeNameQtPrice = costTypeNameQtPrice;
	}

	public java.lang.String getRiderName() {
		return riderName;
	}

	public void setRiderName(java.lang.String riderName) {
		this.riderName = riderName;
	}

	public java.lang.String getRiderPhone() {
		return riderPhone;
	}

	public void setRiderPhone(java.lang.String riderPhone) {
		this.riderPhone = riderPhone;
	}

	public java.lang.String getServiceName() {
		return serviceName;
	}

	public void setServiceName(java.lang.String serviceName) {
		this.serviceName = serviceName;
	}

	public java.lang.String getCityName() {
		return cityName;
	}

	public void setCityName(java.lang.String cityName) {
		this.cityName = cityName;
	}

	public String getBookingStartAddr() {
		return bookingStartAddr;
	}

	public void setBookingStartAddr(String bookingStartAddr) {
		this.bookingStartAddr = bookingStartAddr;
	}

	public String getBookingEndAddr() {
		return bookingEndAddr;
	}

	public void setBookingEndAddr(String bookingEndAddr) {
		this.bookingEndAddr = bookingEndAddr;
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

	public String getBookingDateStr() {
		return bookingDateStr;
	}

	public void setBookingDateStr(String bookingDateStr) {
		this.bookingDateStr = bookingDateStr;
	}

	public String getFactDateStr() {
		return factDateStr;
	}

	public void setFactDateStr(String factDateStr) {
		this.factDateStr = factDateStr;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public String getBookingGroupName() {
		return bookingGroupName;
	}

	public void setBookingGroupName(String bookingGroupName) {
		this.bookingGroupName = bookingGroupName;
	}

	public String getFactGroupName() {
		return factGroupName;
	}

	public void setFactGroupName(String factGroupName) {
		this.factGroupName = factGroupName;
	}

	public Double getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(Double travelTime) {
		this.travelTime = travelTime;
	}

	public Double getTravelMileage() {
		return travelMileage;
	}

	public void setTravelMileage(Double travelMileage) {
		this.travelMileage = travelMileage;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getOverMileagePrice() {
		return overMileagePrice;
	}

	public void setOverMileagePrice(Double overMileagePrice) {
		this.overMileagePrice = overMileagePrice;
	}

	public Double getOverTimePrice() {
		return overTimePrice;
	}

	public void setOverTimePrice(Double overTimePrice) {
		this.overTimePrice = overTimePrice;
	}

	public Double getLongDistancePrice() {
		return longDistancePrice;
	}

	public void setLongDistancePrice(Double longDistancePrice) {
		this.longDistancePrice = longDistancePrice;
	}

	public String getCostTypeName() {
		return costTypeName;
	}

	public void setCostTypeName(String costTypeName) {
		this.costTypeName = costTypeName;
	}

	public Long getCost() {
		return cost;
	}

	public void setCost(Long cost) {
		this.cost = cost;
	}

	public String getCostTypeNameTc() {
		return costTypeNameTc;
	}

	public void setCostTypeNameTc(String costTypeNameTc) {
		this.costTypeNameTc = costTypeNameTc;
	}

	public String getCostTypeNameGs() {
		return costTypeNameGs;
	}

	public void setCostTypeNameGs(String costTypeNameGs) {
		this.costTypeNameGs = costTypeNameGs;
	}

	public String getCostTypeNameQt() {
		return costTypeNameQt;
	}

	public void setCostTypeNameQt(String costTypeNameQt) {
		this.costTypeNameQt = costTypeNameQt;
	}

	public Integer getPushDriverType() {
		return pushDriverType;
	}

	public void setPushDriverType(Integer pushDriverType) {
		this.pushDriverType = pushDriverType;
	}

	public List<CarBizOrderWaitingPeriod> getCarBizOrderWaitingPeriodList() {
		return carBizOrderWaitingPeriodList;
	}

	public void setCarBizOrderWaitingPeriodList(List<CarBizOrderWaitingPeriod> carBizOrderWaitingPeriodList) {
		this.carBizOrderWaitingPeriodList = carBizOrderWaitingPeriodList;
	}

	public Double getDistantNum() {
		return distantNum;
	}

	public void setDistantNum(Double distantNum) {
		this.distantNum = distantNum;
	}

	public Double getDistantFee() {
		return distantFee;
	}

	public void setDistantFee(Double distantFee) {
		this.distantFee = distantFee;
	}

	public String getDriverBeginTime() {
		return driverBeginTime;
	}

	public void setDriverBeginTime(String driverBeginTime) {
		this.driverBeginTime = driverBeginTime;
	}

	public String getDriverArriveTime() {
		return driverArriveTime;
	}

	public void setDriverArriveTime(String driverArriveTime) {
		this.driverArriveTime = driverArriveTime;
	}

	public String getDriverStartServiceTime() {
		return driverStartServiceTime;
	}

	public void setDriverStartServiceTime(String driverStartServiceTime) {
		this.driverStartServiceTime = driverStartServiceTime;
	}

	public String getDriverOrderCoformTime() {
		return driverOrderCoformTime;
	}

	public void setDriverOrderCoformTime(String driverOrderCoformTime) {
		this.driverOrderCoformTime = driverOrderCoformTime;
	}

	public String getDriverOrderEndTime() {
		return driverOrderEndTime;
	}

	public void setDriverOrderEndTime(String driverOrderEndTime) {
		this.driverOrderEndTime = driverOrderEndTime;
	}

	public String getOrderCancleTime() {
		return orderCancleTime;
	}

	public void setOrderCancleTime(String orderCancleTime) {
		this.orderCancleTime = orderCancleTime;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	//指派司机类型name
	private String pushDriverTypeName;
	public String getPushDriverTypeName() {
		if(pushDriverType!=null && this.pushDriverType==1){
			pushDriverTypeName= "自动派单";
		}else if(pushDriverType!=null && this.pushDriverType==2){
			pushDriverTypeName= "司机抢单";
		}else if(pushDriverType!=null && this.pushDriverType==3){
			pushDriverTypeName= "人工绑单";
		}else{
			pushDriverTypeName = "无";
		}
		return pushDriverTypeName;
	}

	public void setPushDriverTypeName(String pushDriverTypeName) {
		this.pushDriverTypeName = pushDriverTypeName;
	}
	//取消 发起人
	private String startPerson;
	public String getStartPerson() {
		if(qxcancelstatus==1){
			startPerson="pc端";
		}else if(qxcancelstatus==2){
			startPerson="乘客端APP";
		}else if(qxcancelstatus==2){
			startPerson="pc端";
		}else if(qxcancelstatus==3){
			startPerson="乘客端APP(超时)";
		}else if(qxcancelstatus==4){
			startPerson="系统自动取消";
		}else if(qxcancelstatus==5){
			startPerson="系统自动取消";
		}else if(qxcancelstatus==10){
			startPerson="乘客端APP";
		}else{
			startPerson="";
		}
		return startPerson;
	}

	public void setStartPerson(String startPerson) {
		this.startPerson = startPerson;
	}
	
	//操作人 
	private String operatePerson;
	public String getOperatePerson() {
		 if(yystatus==1){
			 operatePerson="pc端";
		 }else if(yystatus==2){
			 operatePerson="乘客端APP";
		 }else{
			 operatePerson="";
		 }
		return operatePerson;
	}

	public void setOperatePerson(String operatePerson) {
		this.operatePerson = operatePerson;
	}
	
	
	
}
