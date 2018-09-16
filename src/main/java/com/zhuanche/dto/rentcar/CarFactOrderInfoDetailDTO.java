package com.zhuanche.dto.rentcar;

import java.text.NumberFormat;
import java.util.List;

import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;

public class CarFactOrderInfoDetailDTO{
	//司机Id
	private String driverId;
	//司机出发时间
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
	private String orderNo;
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
	//预定车型类别   --1
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
	private Integer pushDriverType;
	//指派司机类型name
	private String pushDriverTypeName;
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
	//车型    --1 
	private String modeldetail;
	//车牌号 
	private String licensePlates;
	//实际里程(公里) 
	private Double travelMileage;
	//实际时长(分钟) 
	private Double travelTime;
	//套餐费(元) 
	private Double basePrice;
	//公里数(含) 
	private int includemileage;
	//分钟数(含) 
	private int includeminute;
	//指定司机附加费 
	private double designatedDriverFee;
	//-- 语言服务费(元) 
	private double languageServiceFee;
	//-- 长途里程(公里) 
	private Double distantNum =0.0;
	//-- 长途费(元) 
	private Double distantFee=0.0;
	//超里程(公里) 
	private Double overMileageNum;
	//超时(分钟) 
	private Double overTimeNum;
	//空驶里程(公里) 
	private Double longDistanceNum;
	//夜间里程(公里) 
	private Double nightdistancenum;
	//里程费(元) 
	private Double overMileagePrice;
	//时长费(元) 
	private Double overTimePrice;
	//空驶费(元) 
	private Double longdistanceprice;
	//夜间服务费(元) 
	private Double nightdistanceprice;
	//高峰里程(公里) 
	private Double hotMileage;
	//高峰时长(分钟) 
	private Double hotDuration;
	//夜间时长(分钟) 
	private Double nighitDuration;
	//-- 司机等候时长(分钟) 
	private Double waitingTime;
	//高峰里程费(元) 
	private Double hotMileageFees;
	//高峰时长费(元) 
	private Double hotDurationFees;
	//夜间时长费(元) 
	private Double nighitDurationFees;
	//-- 司机等候费用(元) 
	private Double waitingPrice;
	//-- 高速服务费(元) 
	private Long costTypeNameGsPrice;
	//-- 停车费(元) 
	private Long costTypeNameTcPrice;
	//-- 机场服务费(元) 
	private Long costTypeNameJcPrice;
	//-- 食宿费(元) 
	private Long costTypeNameYjPrice;
	//-- 费用总计(元) 
	private String channelDiscountDriver;
	//抹零(元) 
	private Double decimalsFees;
	//--  取消费(元)
	private Double cancelOrderPenalty;
	//优惠券支付(元) (couponsType : 实际面值amount,抵扣面值couponsAmount )
	//实际面值
	private Double amount;
	// 优惠券 实际抵扣面值
	private Double couponsAmount;
	//账户支付(元) (customeramount :充值账户changeAmount,赠送账户giftAmount)
	//充值账户
	private Double changeAmount;
	//赠送账户
	private Double giftAmount;
	//信用卡支付(元)   
	private Double paymentCustomer;
	//微信支付(元) 
	private Double weixin;
	//支付宝支付(元) 
	private Double zfb;
	//POS机支付(元) 
	private Double posPay;
	//账户定金支付(元)( 充值账户定金depositAccountAmount , 赠送账户定金depositGiftAmount )
	private Double depositAccountAmount;
	//赠送账户定金
	private Double depositGiftAmount;
	//信用卡定金支付(元) 
	private Double depositCreditAmount;
	//渠道优惠(元) (实际面值 couponsAmount ,抵扣面值couponsAmount )
	//实际面值 
	 //private String couponsAmount;
	//抵扣面值
	//private String couponsAmount;
	//渠道代收(元) 
	private Double baiDuOrCtripPrice;
	//司机代收(元)  IFNULL(b.driver_pay,0) as paydriver, ( 其中现金代收driverCashAmount , 司机信用卡代收driverCreditcardAmount)
	//现金代收
	private Double driverCashAmount;
	//司机信用卡代收
	private Double driverCreditcardAmount;
	//退还司机代收(乘客拒付) 
	private Double customerRejectPay;
	//待支付金额(元) 
	private Double passengerPendingPay;
	//乘客后支付时间 settleDate
	private String settleDate;
	//发起人 y.cancel_status as qxcancelstatus, (qxcancelstatus: 1 pc端 ,  2 乘客端APP , 3 乘客端APP(超时)  ,  4 系统自动取消(日租/半日租乘客15分钟内未付定金) ， 5 系统自动取消 , 10 乘客端APP )
	private int qxcancelstatus;
	//操作人 qxperson
	//操作时间 qxdate
	//取消原因 
	private String qxmemo;
	//操作人  (yystatus: 1 pc端 ,  2 乘客端APP )
	private int yystatus;
	//操作时间 
	private String yydate;
	//异议原因  
	private String yymemo;
	//减免后总计(元) 
	private Double reductiontotalprice;
	//减免金额(元) 
	private Double jmprice;
	//操作人 
	private String jmname;
	//操作时间 
	private String jmdate;
	//减免原因 
	private String jmreason;
	
	private List<CarBizOrderWaitingPeriod> carBizOrderWaitingPeriodList;

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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public Integer getPushDriverType() {
		return pushDriverType;
	}
	
	public String getPushDriverTypeName() {
		if(this.pushDriverType==1){
			pushDriverTypeName= "自动派单";
		}else if(this.pushDriverType==2){
			pushDriverTypeName= "司机抢单";
		}else if(this.pushDriverType==3){
			pushDriverTypeName= "人工绑单";
		}else{
			pushDriverTypeName = "无";
		}
		return pushDriverTypeName;
	}

	public void setPushDriverTypeName(String pushDriverTypeName) {
		this.pushDriverTypeName = pushDriverTypeName;
	}

	public void setPushDriverType(Integer pushDriverType) {
		this.pushDriverType = pushDriverType;
	}

	public String getBookingIdNumber() {
		return bookingIdNumber;
	}

	public void setBookingIdNumber(String bookingIdNumber) {
		this.bookingIdNumber = bookingIdNumber;
	}

	public String getAirportId() {
		return "1".equals(airportId) ? "是" : "否";
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

	public Double getTravelMileage() {
		return travelMileage;
	}

	public void setTravelMileage(Double travelMileage) {
		this.travelMileage = travelMileage;
	}
	public Double getTravelTime() {
		// 毫秒转分钟
		if (this.travelTime == null || this.travelTime.longValue() == 0) {
			return 0.0;
		} else {
			NumberFormat df = NumberFormat.getInstance();
			df.setMaximumFractionDigits(2);
			String timeStr = df.format(travelTime / 60000);
			if (timeStr != null) {
				if (timeStr.contains(",")) {
					timeStr = timeStr.replace(",", "");
				}
			}
			return Double.valueOf(timeStr);
		}
	}

	public void setTravelTime(Double travelTime) {
		this.travelTime = travelTime;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public int getIncludemileage() {
		return includemileage;
	}

	public void setIncludemileage(int includemileage) {
		this.includemileage = includemileage;
	}

	public int getIncludeminute() {
		return includeminute;
	}

	public void setIncludeminute(int includeminute) {
		this.includeminute = includeminute;
	}

	public double getDesignatedDriverFee() {
		return designatedDriverFee;
	}

	public void setDesignatedDriverFee(double designatedDriverFee) {
		this.designatedDriverFee = designatedDriverFee;
	}

	public double getLanguageServiceFee() {
		return languageServiceFee;
	}

	public void setLanguageServiceFee(double languageServiceFee) {
		this.languageServiceFee = languageServiceFee;
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

	public Double getOverMileageNum() {
		return overMileageNum;
	}

	public void setOverMileageNum(Double overMileageNum) {
		this.overMileageNum = overMileageNum;
	}

	public Double getOverTimeNum() {
		return overTimeNum;
	}

	public void setOverTimeNum(Double overTimeNum) {
		this.overTimeNum = overTimeNum;
	}

	public Double getLongDistanceNum() {
		return longDistanceNum;
	}

	public void setLongDistanceNum(Double longDistanceNum) {
		this.longDistanceNum = longDistanceNum;
	}

	public Double getNightdistancenum() {
		return nightdistancenum;
	}

	public void setNightdistancenum(Double nightdistancenum) {
		this.nightdistancenum = nightdistancenum;
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

	public Double getLongdistanceprice() {
		return longdistanceprice;
	}

	public void setLongdistanceprice(Double longdistanceprice) {
		this.longdistanceprice = longdistanceprice;
	}

	public Double getNightdistanceprice() {
		return nightdistanceprice;
	}

	public void setNightdistanceprice(Double nightdistanceprice) {
		this.nightdistanceprice = nightdistanceprice;
	}

	public Double getHotMileage() {
		return hotMileage;
	}

	public void setHotMileage(Double hotMileage) {
		this.hotMileage = hotMileage;
	}

	public Double getHotDuration() {
		return hotDuration;
	}

	public void setHotDuration(Double hotDuration) {
		this.hotDuration = hotDuration;
	}

	public Double getNighitDuration() {
		return nighitDuration;
	}

	public void setNighitDuration(Double nighitDuration) {
		this.nighitDuration = nighitDuration;
	}

	public Double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(Double waitingTime) {
		this.waitingTime = waitingTime;
	}

	public Double getHotMileageFees() {
		return hotMileageFees;
	}

	public void setHotMileageFees(Double hotMileageFees) {
		this.hotMileageFees = hotMileageFees;
	}

	public Double getHotDurationFees() {
		return hotDurationFees;
	}

	public void setHotDurationFees(Double hotDurationFees) {
		this.hotDurationFees = hotDurationFees;
	}

	public Double getNighitDurationFees() {
		return nighitDurationFees;
	}

	public void setNighitDurationFees(Double nighitDurationFees) {
		this.nighitDurationFees = nighitDurationFees;
	}

	public Double getWaitingPrice() {
		return waitingPrice;
	}

	public void setWaitingPrice(Double waitingPrice) {
		this.waitingPrice = waitingPrice;
	}

	public Long getCostTypeNameGsPrice() {
		return costTypeNameGsPrice;
	}

	public void setCostTypeNameGsPrice(Long costTypeNameGsPrice) {
		this.costTypeNameGsPrice = costTypeNameGsPrice;
	}

	public Long getCostTypeNameTcPrice() {
		return costTypeNameTcPrice;
	}

	public void setCostTypeNameTcPrice(Long costTypeNameTcPrice) {
		this.costTypeNameTcPrice = costTypeNameTcPrice;
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

	public String getChannelDiscountDriver() {
		return channelDiscountDriver;
	}

	public void setChannelDiscountDriver(String channelDiscountDriver) {
		this.channelDiscountDriver = channelDiscountDriver;
	}

	public Double getDecimalsFees() {
		return decimalsFees;
	}

	public void setDecimalsFees(Double decimalsFees) {
		this.decimalsFees = decimalsFees;
	}

	public Double getCancelOrderPenalty() {
		return cancelOrderPenalty;
	}

	public void setCancelOrderPenalty(Double cancelOrderPenalty) {
		this.cancelOrderPenalty = cancelOrderPenalty;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public Double getPaymentCustomer() {
		return paymentCustomer;
	}

	public void setPaymentCustomer(Double paymentCustomer) {
		this.paymentCustomer = paymentCustomer;
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

	public Double getPosPay() {
		return posPay;
	}

	public void setPosPay(Double posPay) {
		this.posPay = posPay;
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

	public Double getBaiDuOrCtripPrice() {
		return baiDuOrCtripPrice;
	}

	public void setBaiDuOrCtripPrice(Double baiDuOrCtripPrice) {
		this.baiDuOrCtripPrice = baiDuOrCtripPrice;
	}

	public Double getDriverCashAmount() {
		return driverCashAmount;
	}

	public void setDriverCashAmount(Double driverCashAmount) {
		this.driverCashAmount = driverCashAmount;
	}

	public Double getDriverCreditcardAmount() {
		return driverCreditcardAmount;
	}

	public void setDriverCreditcardAmount(Double driverCreditcardAmount) {
		this.driverCreditcardAmount = driverCreditcardAmount;
	}

	public Double getCustomerRejectPay() {
		return customerRejectPay;
	}

	public void setCustomerRejectPay(Double customerRejectPay) {
		this.customerRejectPay = customerRejectPay;
	}

	public Double getPassengerPendingPay() {
		return passengerPendingPay;
	}

	public void setPassengerPendingPay(Double passengerPendingPay) {
		this.passengerPendingPay = passengerPendingPay;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}

	public int getQxcancelstatus() {
		return qxcancelstatus;
	}

	public void setQxcancelstatus(int qxcancelstatus) {
		this.qxcancelstatus = qxcancelstatus;
	}

	public String getQxmemo() {
		return qxmemo;
	}

	public void setQxmemo(String qxmemo) {
		this.qxmemo = qxmemo;
	}

	public int getYystatus() {
		return yystatus;
	}

	public void setYystatus(int yystatus) {
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

	public Double getReductiontotalprice() {
		return reductiontotalprice;
	}

	public void setReductiontotalprice(Double reductiontotalprice) {
		this.reductiontotalprice = reductiontotalprice;
	}

	public Double getJmprice() {
		return jmprice;
	}

	public void setJmprice(Double jmprice) {
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

	public List<CarBizOrderWaitingPeriod> getCarBizOrderWaitingPeriodList() {
		return carBizOrderWaitingPeriodList;
	}

	public void setCarBizOrderWaitingPeriodList(List<CarBizOrderWaitingPeriod> carBizOrderWaitingPeriodList) {
		this.carBizOrderWaitingPeriodList = carBizOrderWaitingPeriodList;
	}


	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	
	
	
}