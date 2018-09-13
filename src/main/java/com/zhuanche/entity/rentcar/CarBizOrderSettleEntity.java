package com.zhuanche.entity.rentcar;

public class CarBizOrderSettleEntity {
	
	private int orderId;
	private String orderNo;
	private double orderAmount;//订单原始金额（抹零后）
	private int driverId;
	private String driverPhone;
	private String customerPhone;
	private int couponId;//优惠劵ID
	private double couponAmount;//优惠劵面值
	private double couponSettleAmount;//优惠劵抵扣订单金额
	private double chargeSettleAmount = 0.00;//充值账户结算金额
	private double giftSettleAmount = 0.00;//赠送账户结算金额
	private double customerCreditcardAmount = 0.00;//乘客信用卡结算金额
	private double driverPay = 0.00;//司机代收金额（订单结算时保存）
	private double driverActualPay = 0.00;//司机实际代收金额（订单结算时保存）
	
	private double driverCreditcardAmount = 0.00;//司机信用卡结算金额（司机代收时结算时保存）
	private double driverCashAmount = 0.00;//司机代收现金结算金额（司机代收时结算时保存）
	
	private String settleDate;//结算时间
	private String creteDate;//创建时间
	private String updateDate;//最后更新时间
	
	private double customerRejectPay; //乘客拒付金额
	private double posPay;
	//add by zhou 20160301 
	private double percent=1.0;//折扣率
	private Double settleAmount; // 折扣后金额
	//addBy zsc 2016-4-6
	private Double customerDebt;//取消订单时欠款
	
	//addBy lwl 2016-04-19
	private double couponsType;//1 优惠券 2 折扣券
	
	
	
	public double getPosPay() {
		return posPay;
	}
	public void setPosPay(double posPay) {
		this.posPay = posPay;
	}
	public double getCustomerRejectPay() {
		return customerRejectPay;
	}
	public void setCustomerRejectPay(double customerRejectPay) {
		this.customerRejectPay = customerRejectPay;
	}
	public double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}
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
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public int getCouponId() {
		return couponId;
	}
	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}
	public double getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(double couponAmount) {
		this.couponAmount = couponAmount;
	}
	public double getCouponSettleAmount() {
		return couponSettleAmount;
	}
	public void setCouponSettleAmount(double couponSettleAmount) {
		this.couponSettleAmount = couponSettleAmount;
	}
	public double getChargeSettleAmount() {
		return chargeSettleAmount;
	}
	public void setChargeSettleAmount(double chargeSettleAmount) {
		this.chargeSettleAmount = chargeSettleAmount;
	}
	public double getGiftSettleAmount() {
		return giftSettleAmount;
	}
	public void setGiftSettleAmount(double giftSettleAmount) {
		this.giftSettleAmount = giftSettleAmount;
	}
	public double getCustomerCreditcardAmount() {
		return customerCreditcardAmount;
	}
	public void setCustomerCreditcardAmount(double customerCreditcardAmount) {
		this.customerCreditcardAmount = customerCreditcardAmount;
	}
	public double getDriverPay() {
		return driverPay;
	}
	public void setDriverPay(double driverPay) {
		this.driverPay = driverPay;
	}
	public double getDriverCreditcardAmount() {
		return driverCreditcardAmount;
	}
	public void setDriverCreditcardAmount(double driverCreditcardAmount) {
		this.driverCreditcardAmount = driverCreditcardAmount;
	}
	public double getDriverCashAmount() {
		return driverCashAmount;
	}
	public void setDriverCashAmount(double driverCashAmount) {
		this.driverCashAmount = driverCashAmount;
	}
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	public String getCreteDate() {
		return creteDate;
	}
	public void setCreteDate(String creteDate) {
		this.creteDate = creteDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public double getDriverActualPay() {
		return driverActualPay;
	}
	public void setDriverActualPay(double driverActualPay) {
		this.driverActualPay = driverActualPay;
	}
	

	//add by zhou20151205 增加预付定金结算数据
	private double depositCreditAmount =0.00; //信用卡支付定金结算
	private double depositAccountAmount= 0.00; //充值账户预付定金结算金额
	private double depositGiftAmount= 0.00; //赠送账户预付定金结算金额
	private double depositSettleAmount= 0.00; //待结算账户预付定金结算金额(预付费／后付费)
	private double pendingSettleAmount=0.00; //待结算账户结算金额(预付费／后付费)
	public double getDepositCreditAmount()
	{
		return this.depositCreditAmount;
	}
	public void setDepositCreditAmount(double depositCreditAmount)
	{
		this.depositCreditAmount = depositCreditAmount;
	}
	public double getDepositAccountAmount()
	{
		return this.depositAccountAmount;
	}
	public void setDepositAccountAmount(double depositAccountAmount)
	{
		this.depositAccountAmount = depositAccountAmount;
	}
	public double getDepositGiftAmount()
	{
		return this.depositGiftAmount;
	}
	public void setDepositGiftAmount(double depositGiftAmount)
	{
		this.depositGiftAmount = depositGiftAmount;
	}
	public double getDepositSettleAmount()
	{
		return this.depositSettleAmount;
	}
	public void setDepositSettleAmount(double depositSettleAmount)
	{
		this.depositSettleAmount = depositSettleAmount;
	}
	public double getPendingSettleAmount()
	{
		return this.pendingSettleAmount;
	}
	public void setPendingSettleAmount(double pendingSettleAmount)
	{
		this.pendingSettleAmount = pendingSettleAmount;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public Double getSettleAmount() {
		return settleAmount;
	}
	public void setSettleAmount(Double settleAmount) {
		this.settleAmount = settleAmount;
	}
	public Double getCustomerDebt() {
		return customerDebt;
	}
	public void setCustomerDebt(Double customerDebt) {
		this.customerDebt = customerDebt;
	}
	public double getCouponsType() {
		return couponsType;
	}
	public void setCouponsType(double couponsType) {
		this.couponsType = couponsType;
	}
	
}
