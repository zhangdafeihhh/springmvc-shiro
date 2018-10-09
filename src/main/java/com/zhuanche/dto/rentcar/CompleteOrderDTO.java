package com.zhuanche.dto.rentcar;


import java.io.Serializable;

/**
 * 完成订单列表
 * @author jiadongdong
 *
 */
public class CompleteOrderDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
	/**
	 * 订单号
	 */
    private String orderNo;
    /**
     * 下单城市
     */
    private String orderCityName;

    /**
     * 订单完成时间 yyyy-MM-dd HH:mm:ss 字符串
     */
    private String completeTime;

    /**
     * 产品类型
     */
    private String productTypeName;
    /**
     * 预定车型
     */
    private String orderVehicleTypeName;
    /**
     * 总流水
     */
    private String totalPrice;
	/**
	 * 折扣后金额
	 */
    private String priceAfterDiscount;
    /**
     * 司机端金额
     */
    private String priceForDriver;
    /**
     * 优惠金额
     */
    private String discountAmount;
	/**
	 * 优惠券折扣
	 */
    private String couponDiscount;
    /**
     * 返现结算
     */
    private String returnCash;
    /**
     * 价外费用
     */
    private String specialFee;
    /**
     * 订单类别
     */
    private String orderTypeName;
	/**
	 * 订车人ID
	 */
    private String customerId;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 是否带人叫车
     */
    private String isReplace;
    /**
	 * 司机ID
	 */
    private String driverId;
    /**
     * 司机名称
     */
    private String driverName;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderCityName() {
		return orderCityName;
	}
	public void setOrderCityName(String orderCityName) {
		this.orderCityName = orderCityName;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getOrderVehicleTypeName() {
		return orderVehicleTypeName;
	}
	public void setOrderVehicleTypeName(String orderVehicleTypeName) {
		this.orderVehicleTypeName = orderVehicleTypeName;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getPriceAfterDiscount() {
		return priceAfterDiscount;
	}
	public void setPriceAfterDiscount(String priceAfterDiscount) {
		this.priceAfterDiscount = priceAfterDiscount;
	}
	public String getPriceForDriver() {
		return priceForDriver;
	}
	public void setPriceForDriver(String priceForDriver) {
		this.priceForDriver = priceForDriver;
	}
	public String getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(String discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getCouponDiscount() {
		return couponDiscount;
	}
	public void setCouponDiscount(String couponDiscount) {
		this.couponDiscount = couponDiscount;
	}
	public String getReturnCash() {
		return returnCash;
	}
	public void setReturnCash(String returnCash) {
		this.returnCash = returnCash;
	}
	public String getSpecialFee() {
		return specialFee;
	}
	public void setSpecialFee(String specialFee) {
		this.specialFee = specialFee;
	}
	public String getOrderTypeName() {
		return orderTypeName;
	}
	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getIsReplace() {
		return isReplace;
	}
	public void setIsReplace(String isReplace) {
		this.isReplace = isReplace;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
   
}