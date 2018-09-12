package com.zhuanche.dto.rentcar;

public class CarFactOrderInfoDetailDTO{
	//下单时间
	private String createDate;
	//司机出发 
	private String driverBeginTime;
	//司机到达上车地点 
	private String driverArriveTime;
	//开始服务 
	private String driverStartServiceTime;
	//结算 
	private String driverOrderCoformTime;
	//服务完成 
	private String driverOrderEndTime;
	//取消订单 
	private String orderCancleTime;
	//订单号 
	private String orderno;
	//下单时间 
	private String createdate;
	//服务类型 
	private String serviceName;
	//城市 
	private String cityName;
	//预定会员/代理人 
	private String bookingname;
	//预订人手机 
	private String bookingphone;
	//乘车人 
	private String riderName;
	//乘车人手机 
	private String riderPhone;
	//预约上车时间 
	private String bookingDateStr;
	//预约上车地点 
	private String bookingStartAddr;
	//预约下车地点 
	private String bookingEndAddr;
	//预定车型类别 
	private String bookingGroupnames;
	//实际上车时间 
	private String factDate;
	//实际上车地点 
	private String factStartAddr;
	//实际下车地点 
	private String factEndAddr;
	//实际选择车型 
	private String bookingGroupName;
	//实际下车时间 
	private String factEndDate;
	//付款人 
	private String payperson;
	//接机航班号 
	private String airlineNo;
	//指派司机类型 
	private String pushDriverType;
	//预定人身份证号码 
	private String bookingIdNumber;
	//是否拼车单 
	private String airportId;
	//主订单编号 
	private String mainOrderNo;
	//司机姓名 
	private String drivername;
	//司机手机 
	private String driverphone;
	//车型 
	private String modeldetail;
	//车牌号 
	private String licensePlates;
	//实际里程(公里) 
	private String travelMileage;
	//实际时长(分钟) 
	private String travelTime;
	//开始时间 
	private String startWaitingTime;
	//结束时间 
	private String endWaitingTime;
	//套餐费(元) 
	private String basePrice;
	//公里数(含) 
	private String includemileage;
	//分钟数(含) 
	private String includeminute;
	//指定司机附加费 
	private String designatedDriverFee;
	//-- 语言服务费(元) 
	private String languageServiceFee;
	//-- 长途里程(公里) 
	private String distantNum;
	//-- 长途费(元) 
	private String distantFee;
	//超里程(公里) 
	private String overMileageNum;
	//超时(分钟) 
	private String overTimeNum;
	//空驶里程(公里) 
	private String longDistanceNum;
	//夜间里程(公里) 
	private String nightdistancenum;
	//里程费(元) 
	private String overMileagePrice;
	//时长费(元) 
	private String overTimePrice;
	//空驶费(元) 
	private String longdistanceprice;
	//夜间服务费(元) 
	private String nightdistanceprice;
	//高峰里程(公里) 
	private String hotMileage;
	//高峰时长(分钟) 
	private String hotDuration;
	//夜间时长(分钟) 
	private String nighitDuration;
	//-- 司机等候时长(分钟) 
	private String waitingTime;
	//高峰里程费(元) 
	private String hotMileageFees;
	//高峰时长费(元) 
	private String hotDurationFees;
	//夜间时长费(元) 
	private String nighitDurationFees;
	//-- 司机等候费用(元) 
	private String waitingPrice;
	//-- 高速服务费(元) 
	private String costTypeNameGsPrice;
	//-- 停车费(元) 
	private String costTypeNameTcPrice;
	//-- 机场服务费(元) 
	private String costTypeNameJcPrice;
	//-- 食宿费(元) 
	private String costTypeNameYjPrice;
	//-- 费用总计(元) 
	private String channelDiscountDriver;
	//抹零(元) 
	private String decimalsFees;
	//--  取消费(元)
	private String cancelOrderPenalty;
	
	//优惠券支付(元) (couponsType : 实际面值amount,抵扣面值couponsAmount )
	//实际面值
	private String amount;
	//抵扣面值
	private String couponsAmount;
	//账户支付(元) (customeramount :充值账户changeAmount,赠送账户giftAmount)
	//充值账户
	private String changeAmount;
	//赠送账户
	private String giftAmount;
	//信用卡支付(元)   
	private String paymentCustomer;
	//微信支付(元) 
	private String weixin;
	//支付宝支付(元) 
	private String zfb;
	//POS机支付(元) 
	private String posPay;
	//账户定金支付(元)( 充值账户定金depositAccountAmount , 赠送账户定金depositGiftAmount )
	private String depositAccountAmount;
	//赠送账户定金
	private String depositGiftAmount;
	//信用卡定金支付(元) 
	private String depositCreditAmount;
	//渠道优惠(元) (实际面值 couponsAmount ,抵扣面值couponsAmount )
	//实际面值 
	//private String couponsAmount;
	//抵扣面值
	//private String couponsAmount;
	//渠道代收(元) 
	private String baiDuOrCtripPrice;
	//司机代收(元)  IFNULL(b.driver_pay,0) as paydriver, ( 其中现金代收driverCashAmount , 司机信用卡代收driverCreditcardAmount)
	//现金代收
	private String driverCashAmount;
	//司机信用卡代收
	private String driverCreditcardAmount;
	//退还司机代收(乘客拒付) 
	private String customerRejectPay;
	//待支付金额(元) 
	private String passengerPendingPay;
	//乘客后支付时间 settleDate
	private String settleDate;
	//发起人 y.cancel_status as qxcancelstatus, (qxcancelstatus: 1 pc端 ,  2 乘客端APP , 3 乘客端APP(超时)  ,  4 系统自动取消(日租/半日租乘客15分钟内未付定金) ， 5 系统自动取消 , 10 乘客端APP )
	private String qxcancelstatus;
	//操作人 qxperson
	//操作时间 qxdate
	//取消原因 
	private String qxmemo;
	//操作人  (yystatus: 1 pc端 ,  2 乘客端APP )
	private String yystatus;
	//操作时间 
	private String yydate;
	//异议原因  
	private String yymemo;
	//减免后总计(元) 
	private String reductiontotalprice;
	//减免金额(元) 
	private String jmprice;
	//操作人 
	private String jmname;
	//操作时间 
	private String jmdate;
	//减免原因 
	private String jmreason;
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
	public String getOrderno() {
		return orderno;
	}
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
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
	public String getBookingDateStr() {
		return bookingDateStr;
	}
	public void setBookingDateStr(String bookingDateStr) {
		this.bookingDateStr = bookingDateStr;
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
	public String getBookingGroupName() {
		return bookingGroupName;
	}
	public void setBookingGroupName(String bookingGroupName) {
		this.bookingGroupName = bookingGroupName;
	}
	public String getFactEndDate() {
		return factEndDate;
	}
	public void setFactEndDate(String factEndDate) {
		this.factEndDate = factEndDate;
	}
	public String getPayperson() {
		return payperson;
	}
	public void setPayperson(String payperson) {
		this.payperson = payperson;
	}
	public String getAirlineNo() {
		return airlineNo;
	}
	public void setAirlineNo(String airlineNo) {
		this.airlineNo = airlineNo;
	}
	public String getPushDriverType() {
		return pushDriverType;
	}
	public void setPushDriverType(String pushDriverType) {
		this.pushDriverType = pushDriverType;
	}
	public String getBookingIdNumber() {
		return bookingIdNumber;
	}
	public void setBookingIdNumber(String bookingIdNumber) {
		this.bookingIdNumber = bookingIdNumber;
	}
	public String getAirportId() {
		return airportId;
	}
	public void setAirportId(String airportId) {
		this.airportId = airportId;
	}
	public String getMainOrderNo() {
		return mainOrderNo;
	}
	public void setMainOrderNo(String mainOrderNo) {
		this.mainOrderNo = mainOrderNo;
	}
	public String getDrivername() {
		return drivername;
	}
	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}
	public String getDriverphone() {
		return driverphone;
	}
	public void setDriverphone(String driverphone) {
		this.driverphone = driverphone;
	}
	public String getModeldetail() {
		return modeldetail;
	}
	public void setModeldetail(String modeldetail) {
		this.modeldetail = modeldetail;
	}
	public String getLicensePlates() {
		return licensePlates;
	}
	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}
	public String getTravelMileage() {
		return travelMileage;
	}
	public void setTravelMileage(String travelMileage) {
		this.travelMileage = travelMileage;
	}
	public String getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}
	public String getStartWaitingTime() {
		return startWaitingTime;
	}
	public void setStartWaitingTime(String startWaitingTime) {
		this.startWaitingTime = startWaitingTime;
	}
	public String getEndWaitingTime() {
		return endWaitingTime;
	}
	public void setEndWaitingTime(String endWaitingTime) {
		this.endWaitingTime = endWaitingTime;
	}
	public String getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}
	public String getIncludemileage() {
		return includemileage;
	}
	public void setIncludemileage(String includemileage) {
		this.includemileage = includemileage;
	}
	public String getIncludeminute() {
		return includeminute;
	}
	public void setIncludeminute(String includeminute) {
		this.includeminute = includeminute;
	}
	public String getDesignatedDriverFee() {
		return designatedDriverFee;
	}
	public void setDesignatedDriverFee(String designatedDriverFee) {
		this.designatedDriverFee = designatedDriverFee;
	}
	public String getLanguageServiceFee() {
		return languageServiceFee;
	}
	public void setLanguageServiceFee(String languageServiceFee) {
		this.languageServiceFee = languageServiceFee;
	}
	public String getDistantNum() {
		return distantNum;
	}
	public void setDistantNum(String distantNum) {
		this.distantNum = distantNum;
	}
	public String getDistantFee() {
		return distantFee;
	}
	public void setDistantFee(String distantFee) {
		this.distantFee = distantFee;
	}
	public String getOverMileageNum() {
		return overMileageNum;
	}
	public void setOverMileageNum(String overMileageNum) {
		this.overMileageNum = overMileageNum;
	}
	public String getOverTimeNum() {
		return overTimeNum;
	}
	public void setOverTimeNum(String overTimeNum) {
		this.overTimeNum = overTimeNum;
	}
	public String getLongDistanceNum() {
		return longDistanceNum;
	}
	public void setLongDistanceNum(String longDistanceNum) {
		this.longDistanceNum = longDistanceNum;
	}
	public String getNightdistancenum() {
		return nightdistancenum;
	}
	public void setNightdistancenum(String nightdistancenum) {
		this.nightdistancenum = nightdistancenum;
	}
	public String getOverMileagePrice() {
		return overMileagePrice;
	}
	public void setOverMileagePrice(String overMileagePrice) {
		this.overMileagePrice = overMileagePrice;
	}
	public String getOverTimePrice() {
		return overTimePrice;
	}
	public void setOverTimePrice(String overTimePrice) {
		this.overTimePrice = overTimePrice;
	}
	public String getLongdistanceprice() {
		return longdistanceprice;
	}
	public void setLongdistanceprice(String longdistanceprice) {
		this.longdistanceprice = longdistanceprice;
	}
	public String getNightdistanceprice() {
		return nightdistanceprice;
	}
	public void setNightdistanceprice(String nightdistanceprice) {
		this.nightdistanceprice = nightdistanceprice;
	}
	public String getHotMileage() {
		return hotMileage;
	}
	public void setHotMileage(String hotMileage) {
		this.hotMileage = hotMileage;
	}
	public String getHotDuration() {
		return hotDuration;
	}
	public void setHotDuration(String hotDuration) {
		this.hotDuration = hotDuration;
	}
	public String getNighitDuration() {
		return nighitDuration;
	}
	public void setNighitDuration(String nighitDuration) {
		this.nighitDuration = nighitDuration;
	}
	public String getWaitingTime() {
		return waitingTime;
	}
	public void setWaitingTime(String waitingTime) {
		this.waitingTime = waitingTime;
	}
	public String getHotMileageFees() {
		return hotMileageFees;
	}
	public void setHotMileageFees(String hotMileageFees) {
		this.hotMileageFees = hotMileageFees;
	}
	public String getHotDurationFees() {
		return hotDurationFees;
	}
	public void setHotDurationFees(String hotDurationFees) {
		this.hotDurationFees = hotDurationFees;
	}
	public String getNighitDurationFees() {
		return nighitDurationFees;
	}
	public void setNighitDurationFees(String nighitDurationFees) {
		this.nighitDurationFees = nighitDurationFees;
	}
	public String getWaitingPrice() {
		return waitingPrice;
	}
	public void setWaitingPrice(String waitingPrice) {
		this.waitingPrice = waitingPrice;
	}
	public String getCostTypeNameGsPrice() {
		return costTypeNameGsPrice;
	}
	public void setCostTypeNameGsPrice(String costTypeNameGsPrice) {
		this.costTypeNameGsPrice = costTypeNameGsPrice;
	}
	public String getCostTypeNameTcPrice() {
		return costTypeNameTcPrice;
	}
	public void setCostTypeNameTcPrice(String costTypeNameTcPrice) {
		this.costTypeNameTcPrice = costTypeNameTcPrice;
	}
	public String getCostTypeNameJcPrice() {
		return costTypeNameJcPrice;
	}
	public void setCostTypeNameJcPrice(String costTypeNameJcPrice) {
		this.costTypeNameJcPrice = costTypeNameJcPrice;
	}
	public String getCostTypeNameYjPrice() {
		return costTypeNameYjPrice;
	}
	public void setCostTypeNameYjPrice(String costTypeNameYjPrice) {
		this.costTypeNameYjPrice = costTypeNameYjPrice;
	}
	public String getChannelDiscountDriver() {
		return channelDiscountDriver;
	}
	public void setChannelDiscountDriver(String channelDiscountDriver) {
		this.channelDiscountDriver = channelDiscountDriver;
	}
	public String getDecimalsFees() {
		return decimalsFees;
	}
	public void setDecimalsFees(String decimalsFees) {
		this.decimalsFees = decimalsFees;
	}
	public String getCancelOrderPenalty() {
		return cancelOrderPenalty;
	}
	public void setCancelOrderPenalty(String cancelOrderPenalty) {
		this.cancelOrderPenalty = cancelOrderPenalty;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCouponsAmount() {
		return couponsAmount;
	}
	public void setCouponsAmount(String couponsAmount) {
		this.couponsAmount = couponsAmount;
	}
	public String getChangeAmount() {
		return changeAmount;
	}
	public void setChangeAmount(String changeAmount) {
		this.changeAmount = changeAmount;
	}
	public String getGiftAmount() {
		return giftAmount;
	}
	public void setGiftAmount(String giftAmount) {
		this.giftAmount = giftAmount;
	}
	public String getPaymentCustomer() {
		return paymentCustomer;
	}
	public void setPaymentCustomer(String paymentCustomer) {
		this.paymentCustomer = paymentCustomer;
	}
	public String getWeixin() {
		return weixin;
	}
	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}
	public String getZfb() {
		return zfb;
	}
	public void setZfb(String zfb) {
		this.zfb = zfb;
	}
	public String getPosPay() {
		return posPay;
	}
	public void setPosPay(String posPay) {
		this.posPay = posPay;
	}
	public String getDepositAccountAmount() {
		return depositAccountAmount;
	}
	public void setDepositAccountAmount(String depositAccountAmount) {
		this.depositAccountAmount = depositAccountAmount;
	}
	public String getDepositGiftAmount() {
		return depositGiftAmount;
	}
	public void setDepositGiftAmount(String depositGiftAmount) {
		this.depositGiftAmount = depositGiftAmount;
	}
	public String getDepositCreditAmount() {
		return depositCreditAmount;
	}
	public void setDepositCreditAmount(String depositCreditAmount) {
		this.depositCreditAmount = depositCreditAmount;
	}
	public String getBaiDuOrCtripPrice() {
		return baiDuOrCtripPrice;
	}
	public void setBaiDuOrCtripPrice(String baiDuOrCtripPrice) {
		this.baiDuOrCtripPrice = baiDuOrCtripPrice;
	}
	public String getDriverCashAmount() {
		return driverCashAmount;
	}
	public void setDriverCashAmount(String driverCashAmount) {
		this.driverCashAmount = driverCashAmount;
	}
	public String getDriverCreditcardAmount() {
		return driverCreditcardAmount;
	}
	public void setDriverCreditcardAmount(String driverCreditcardAmount) {
		this.driverCreditcardAmount = driverCreditcardAmount;
	}
	public String getCustomerRejectPay() {
		return customerRejectPay;
	}
	public void setCustomerRejectPay(String customerRejectPay) {
		this.customerRejectPay = customerRejectPay;
	}
	public String getPassengerPendingPay() {
		return passengerPendingPay;
	}
	public void setPassengerPendingPay(String passengerPendingPay) {
		this.passengerPendingPay = passengerPendingPay;
	}
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	public String getQxcancelstatus() {
		return qxcancelstatus;
	}
	public void setQxcancelstatus(String qxcancelstatus) {
		this.qxcancelstatus = qxcancelstatus;
	}
	public String getQxmemo() {
		return qxmemo;
	}
	public void setQxmemo(String qxmemo) {
		this.qxmemo = qxmemo;
	}
	public String getYystatus() {
		return yystatus;
	}
	public void setYystatus(String yystatus) {
		this.yystatus = yystatus;
	}
	public String getYydate() {
		return yydate;
	}
	public void setYydate(String yydate) {
		this.yydate = yydate;
	}
	public String getYymemo() {
		return yymemo;
	}
	public void setYymemo(String yymemo) {
		this.yymemo = yymemo;
	}
	public String getReductiontotalprice() {
		return reductiontotalprice;
	}
	public void setReductiontotalprice(String reductiontotalprice) {
		this.reductiontotalprice = reductiontotalprice;
	}
	public String getJmprice() {
		return jmprice;
	}
	public void setJmprice(String jmprice) {
		this.jmprice = jmprice;
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

}