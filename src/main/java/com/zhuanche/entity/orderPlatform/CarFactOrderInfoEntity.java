package com.zhuanche.entity.orderPlatform;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/17 下午2:28
 * @Version 1.0
 */
public class CarFactOrderInfoEntity {
    private java.lang.String riderName;
    private java.lang.String riderPhone;
    private java.lang.String serviceName;
    private int cityId;
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
    // 订单号
    private String orderno;
    // 订单id
    private int orderId;
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
    private Long cost = 0l;
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
    private Long costTypeNameTcPrice = 0l;
    //
    private Long costTypeNameGsPrice = 0l;
    // 高速费
    private Long costTypeNameQtPrice = 0l;
    // 机场服务费
    private Long costTypeNameJcPrice = 0l;
    // 食宿费
    private Long costTypeNameYjPrice = 0l;

    private int status;
    private int serviceTypeId;
    private String airlineNo;
    private int bookingUserId;
    private int driverId;
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



    //指定司机附加费
    private double designatedDriverFee;
    //end
    //司机等候费
    private Double waitingPrice;
    //司机等候时长
    private Double waitingTime;
    //使用的优惠券1 还是折扣券2
    private double couponsType;
    //百度 携程 支付的钱数
    private Double baiDuOrCtripPrice;
    //订单类型
    private String channelsNum;

    private int couponId;

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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
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

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
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

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
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

    public Double getSurplus() {
        return surplus;
    }

    public void setSurplus(Double surplus) {
        this.surplus = surplus;
    }

    public double getNightdistancenum() {
        return nightdistancenum;
    }

    public void setNightdistancenum(double nightdistancenum) {
        this.nightdistancenum = nightdistancenum;
    }

    public double getNightdistanceprice() {
        return nightdistanceprice;
    }

    public void setNightdistanceprice(double nightdistanceprice) {
        this.nightdistanceprice = nightdistanceprice;
    }

    public Double getReductiontotalprice() {
        return reductiontotalprice;
    }

    public void setReductiontotalprice(Double reductiontotalprice) {
        this.reductiontotalprice = reductiontotalprice;
    }

    public long getDetailId() {
        return detailId;
    }

    public void setDetailId(long detailId) {
        this.detailId = detailId;
    }

    public String getPayperson() {
        return payperson;
    }

    public void setPayperson(String payperson) {
        this.payperson = payperson;
    }

    public Double getCustomeramount() {
        return customeramount;
    }

    public void setCustomeramount(Double customeramount) {
        this.customeramount = customeramount;
    }

    public Double getPaydriver() {
        return paydriver;
    }

    public void setPaydriver(Double paydriver) {
        this.paydriver = paydriver;
    }

    public String getBookinggroupids() {
        return bookinggroupids;
    }

    public void setBookinggroupids(String bookinggroupids) {
        this.bookinggroupids = bookinggroupids;
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

    public int getQxcancelstatus() {
        return qxcancelstatus;
    }

    public void setQxcancelstatus(int qxcancelstatus) {
        this.qxcancelstatus = qxcancelstatus;
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

    public String getBookingGroupnames() {
        return bookingGroupnames;
    }

    public void setBookingGroupnames(String bookingGroupnames) {
        this.bookingGroupnames = bookingGroupnames;
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

    public int getCutomerId() {
        return cutomerId;
    }

    public void setCutomerId(int cutomerId) {
        this.cutomerId = cutomerId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
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

    public Double getLongdistanceprice() {
        return longdistanceprice;
    }

    public void setLongdistanceprice(Double longdistanceprice) {
        this.longdistanceprice = longdistanceprice;
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

    public double getDesignatedDriverFee() {
        return designatedDriverFee;
    }

    public void setDesignatedDriverFee(double designatedDriverFee) {
        this.designatedDriverFee = designatedDriverFee;
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

    public String getChannelsNum() {
        return channelsNum;
    }

    public void setChannelsNum(String channelsNum) {
        this.channelsNum = channelsNum;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }
}
