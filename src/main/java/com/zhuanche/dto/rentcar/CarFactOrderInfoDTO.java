package com.zhuanche.dto.rentcar;

import com.zhuanche.dto.CarFactOrderMemo;

public class CarFactOrderInfoDTO{
	//订单号 
	private String orderNo;
	//订单ID
	private String orderId;
	//订单指派方式 
	private String pushDriverType;
	//城市 
	private String cityName;
	//服务类别 
	private String serviceName;
	//车型类别 
	private String groupName;
	//订单类别 
	private String type;
	//预订人
	private String bookingUserName;
	//预订人手机号 
	private String bookingUserPhone;
	//乘车人  --1
	private String riderName;
	//乘车人手机号 
	private String riderPhone;
	//司机 
	private String driverName;
	//司机手机号 
	private String driverPhone;
	//司机车牌号 
	private String licensePlates;
	//供应商 
	private String supplierFullName;   
	//乘车时长(分钟) 
	private String travelTime;
	//乘车里程 
	private String travelMileage;
	//金额
	private String actualPayAmount;
	//是否使用优惠劵 
	private String couponId;
	//优惠劵支付（元） 
	private String couponAmount; 
	//下单时间  
	private String createDate; 
	//完成时间  
	private String costEndDate; 
	//实际上车地址 
	private String factStartAddr; 
	//实际下车地址 
	private String factEndAddr; 
	//订单状态  
	private String status; 
	//订单状态  名称
	private String dicName; 
	//是否拼车单  
	private String airportId; 
	//主订单号  
	private String mainOrderNo;
	//
	private String memo;

	private String actualPayAmountDriver;

	private CarFactOrderMemo memoObj;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPushDriverType() {
		if("2".equals(pushDriverType)){
    		return "抢单";
    	} else if("1".equals(pushDriverType) || "3".equals(pushDriverType)){
    		return "绑单";
    	}else{
    		return "";
    	}
	}
	public void setPushDriverType(String pushDriverType) {
		this.pushDriverType = pushDriverType;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getType() {
		if("1".equals(type)){
    		return "普通订单";
    	}else if("2".equals(type)){
    		return "机构订单";
    	}else{
    		return "";
    	}	
	}
	public void setType(String type) {
		this.type = type;
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
	public String getLicensePlates() {
		return licensePlates;
	}
	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}
	public String getSupplierFullName() {
		return supplierFullName;
	}
	public void setSupplierFullName(String supplierFullName) {
		this.supplierFullName = supplierFullName;
	}
	public String getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}
	public String getTravelMileage() {
		return travelMileage;
	}
	public void setTravelMileage(String travelMileage) {
		this.travelMileage = travelMileage;
	}
	public String getActualPayAmount() {
		return actualPayAmount;
	}
	public void setActualPayAmount(String actualPayAmount) {
		this.actualPayAmount = actualPayAmount;
	}
	public String getCouponId() { // couponId!=null && "-1".equals(couponId) && 
		if(couponAmount!=null && Double.valueOf(couponAmount)>0){
    		return "使用";
    	}else{
    		return "未使用";
    	}
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(String couponAmount) {
		this.couponAmount = couponAmount;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCostEndDate() {
		return costEndDate;
	}
	public void setCostEndDate(String costEndDate) {
		this.costEndDate = costEndDate;
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
	public String getStatus() {
		 return status;
		/*List<String> dfw = Arrays.asList("10,15,13".split(","));
		List<String> fwz = Arrays.asList("20,25,30,35,40".split(","));
		List<String> ywc = Arrays.asList("45,50,55,42,43".split(","));
		List<String> yqx = Arrays.asList("60".split(","));
		 if(dfw.contains(status)){
			return "待服务";
		 }  else if(fwz.contains(status)){
			return "服务中";
		 } else if(ywc.contains(status)){
			return "已完成";
		 } else if(yqx.contains(status)){
			return "已取消";
		 }else {
			 return status;
		 }*/
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getDicName() {
		return dicName;
	}
	public void setDicName(String dicName) {
		this.dicName = dicName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public CarFactOrderMemo getMemoObj() {
		return memoObj;
	}

	public void setMemoObj(CarFactOrderMemo memoObj) {
		this.memoObj = memoObj;
	}

	public String getActualPayAmountDriver() {
		return actualPayAmountDriver;
	}

	public void setActualPayAmountDriver(String actualPayAmountDriver) {
		this.actualPayAmountDriver = actualPayAmountDriver;
	}

	/*
    private String licensePlates;

    private String brand;

    private Integer carModelId;

    private Date carPurchaseDate;

    private String modelDetail;

    private String color;

    private String carPhotographName;


    private String engineNo;

    private String frameNo;

    private String nextInspectDate;

    private String nextMaintenanceDate;

    private String rentalExpireDate;

    private String nextOperationDate;

    private String  nextSecurityDate;

    private String nextClassDate;

    private String twoLevelMaintenanceDate;

    private Integer status;

    private Integer createBy;

    private Integer updateBy;

    private Date createDate;

    private Date updateDate;

    private Integer auditingstatus;

    private String freighttype;

    private String totalmileage;


 
    private String auditingDate;

 
    


//下单 
createDate
//司机出发 
driverBeginTime
//司机到达上车地点 
driverArriveTime
//开始服务 
driverStartServiceTime 
//结算 
driverOrderCoformTime
//服务完成 
driverOrderEndTime
//取消订单 
orderCancleTime

//订单号 
orderno
//下单时间 
createdate
//服务类型 
serviceName
//城市 
cityName
预定会员/代理人 
bookingname
预订人手机 
bookingphone
乘车人 
riderName
乘车人手机 
riderPhone
预约上车时间 
bookingDateStr
预约上车地点 
bookingStartAddr
预约下车地点 
bookingEndAddr
预定车型类别 
bookingGroupnames 
实际上车时间 
factDate
实际上车地点 
factStartAddr
实际下车地点 
factEndAddr
实际选择车型 
bookingGroupName
实际下车时间 
factEndDate
付款人 
payperson
接机航班号 
airlineNo
指派司机类型 
pushDriverType
预定人身份证号码 
bookingIdNumber
是否拼车单 
airportId
主订单编号 
mainOrderNo
司机姓名 
drivername
司机手机 
driverphone
车型 
modeldetail
车牌号 
licensePlates
实际里程(公里) 
travelMileage
实际时长(分钟) 
travelTime


开始时间 
startWaitingTime
结束时间 
endWaitingTime


套餐费(元) 
basePrice
公里数(含) 
includemileage
分钟数(含) 
includeminute
指定司机附加费 
designatedDriverFee
-- 语言服务费(元) 
languageServiceFee
-- 长途里程(公里) 
distantNum
-- 长途费(元) 
distantFee
超里程(公里) 
overMileageNum
超时(分钟) 
overTimeNum
空驶里程(公里) 
longDistanceNum
夜间里程(公里) 
nightdistancenum
里程费(元) 
overMileagePrice
时长费(元) 
overTimePrice
空驶费(元) 
longdistanceprice
夜间服务费(元) 
nightdistanceprice
高峰里程(公里) 
hotMileage
高峰时长(分钟) 
hotDuration
夜间时长(分钟) 
nighitDuration
-- 司机等候时长(分钟) 
waitingTime
高峰里程费(元) 
hotMileageFees
高峰时长费(元) 
hotDurationFees
夜间时长费(元) 
nighitDurationFees
-- 司机等候费用(元) 
waitingPrice
-- 高速服务费(元) 
costTypeNameGsPrice
-- 停车费(元) 
costTypeNameTcPrice
-- 机场服务费(元) 
costTypeNameJcPrice
-- 食宿费(元) 
costTypeNameYjPrice
-- 费用总计(元) 
channelDiscountDriver
抹零(元) 
decimalsFees
--  取消费(元)
cancelOrderPenalty

优惠券支付(元) (couponsType : 实际面值amount,抵扣面值couponsAmount )
实际面值
amount
抵扣面值
couponsAmount 
账户支付(元) (customeramount :充值账户changeAmount,赠送账户giftAmount)
充值账户
changeAmount
赠送账户
giftAmount
信用卡支付(元)   
paymentCustomer
微信支付(元) 
weixin
支付宝支付(元) 
zfb
POS机支付(元) 
posPay
账户定金支付(元)( 充值账户定金depositAccountAmount , 赠送账户定金depositGiftAmount )
depositAccountAmount 
赠送账户定金
depositGiftAmount
信用卡定金支付(元) 
depositCreditAmount
渠道优惠(元) (实际面值 couponsAmount ,抵扣面值couponsAmount )
实际面值 
couponsAmount 
抵扣面值
couponsAmount
渠道代收(元) 
baiDuOrCtripPrice

//司机代收(元)  IFNULL(b.driver_pay,0) as paydriver, ( 其中现金代收driverCashAmount , 司机信用卡代收driverCreditcardAmount)
//现金代收
driverCashAmount
//司机信用卡代收
driverCreditcardAmount
//退还司机代收(乘客拒付) 
customerRejectPay 
 
//待支付金额(元) 
passengerPendingPay
//乘客后支付时间 settleDate
settleDate
//发起人 y.cancel_status as qxcancelstatus, (qxcancelstatus: 1 pc端 ,  2 乘客端APP , 3 乘客端APP(超时)  ,  4 系统自动取消(日租/半日租乘客15分钟内未付定金) ， 5 系统自动取消 , 10 乘客端APP )
qxcancelstatus
//操作人 qxperson
//操作时间 qxdate
//取消原因 
qxmemo
//操作人  (yystatus: 1 pc端 ,  2 乘客端APP )
yystatus
//操作时间 
yydate
//异议原因  
yymemo
//减免后总计(元) 
reductiontotalprice;
//减免金额(元) 
jmprice
//操作人 
jmname
//操作时间 
jmdate
//减免原因 
jmreason



*/

}