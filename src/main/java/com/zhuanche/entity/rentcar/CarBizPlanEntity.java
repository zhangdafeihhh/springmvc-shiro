package com.zhuanche.entity.rentcar;

public class CarBizPlanEntity {

	// 计划id
	private java.lang.Integer planId;
	// 计划版本号
	private String planVer;
	// 价格Id
	private int priceId;
	//
	private java.util.Date startDate;
	//
	private java.util.Date endDate;
	// 使用时间 1：当天 2：明天
	private java.lang.Integer planUseDate;
	// 服务类型
	private java.lang.Integer serviceId;
	// 每公里价格（时租）
	private Double mileagePrice;
	// 每分钟价格（时租）
	private Double minutePrice;
	// 超时每公里价格
	private Double overTimePrice;
	// 超时每分钟价格
	private Double overMileagePrice;
	// 套餐价
	private Double basePrice;
	// 含分钟
	private java.lang.Integer includeMinute;
	// 含公里数
	private java.lang.Integer includeMileage;
	// 长途公里数（超出这个数就算长途）
	private java.lang.Integer includeLongDistance;
	// 长途费价格
	private Double longDistancePriceRate;
	// 夜间服务费价格（里程）
	private Double nightServicePrice;

	// 夜间服务费价格(分钟)lwl 2015-11-25
	private Double nightServicePriceTime;

	// 车组
	// private java.lang.Integer groupId;
	// 高峰时段价格（里程）

	private Double peakPrice;

	// 高峰时段价格（分钟）

	private Double peakPriceTime;

	public Double getNightServicePriceTime() {
		return nightServicePriceTime == null ? 0.00 : nightServicePriceTime;
	}

	public void setNightServicePriceTime(Double nightServicePriceTime) {
		this.nightServicePriceTime = nightServicePriceTime;
	}

	public Double getPeakPrice() {
		return peakPrice == null ? 0.00 : peakPrice;
	}

	public void setPeakPrice(Double peakPrice) {
		this.peakPrice = peakPrice;
	}

	public Double getPeakPriceTime() {
		return peakPriceTime == null ? 0.00 : peakPriceTime;
	}

	public void setPeakPriceTime(Double peakPriceTime) {
		this.peakPriceTime = peakPriceTime;
	}

	public java.lang.Integer getPlanUseDate() {
		return planUseDate;
	}

	public int getPriceId() {
		return priceId;
	}

	public void setPriceId(int priceId) {
		this.priceId = priceId;
	}

	public Double getMileagePrice() {
		return mileagePrice;
	}

	public void setMileagePrice(Double mileagePrice) {
		this.mileagePrice = mileagePrice;
	}

	public Double getMinutePrice() {
		return minutePrice;
	}

	public void setMinutePrice(Double minutePrice) {
		this.minutePrice = minutePrice;
	}

	public Double getOverTimePrice() {
		return overTimePrice;
	}

	public void setOverTimePrice(Double overTimePrice) {
		this.overTimePrice = overTimePrice;
	}

	public Double getOverMileagePrice() {
		return overMileagePrice;
	}

	public void setOverMileagePrice(Double overMileagePrice) {
		this.overMileagePrice = overMileagePrice;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public Double getLongDistancePriceRate() {
		return longDistancePriceRate;
	}

	public void setLongDistancePriceRate(Double longDistancePriceRate) {
		this.longDistancePriceRate = longDistancePriceRate;
	}

	public Double getNightServicePrice() {
		return nightServicePrice;
	}

	public void setNightServicePrice(Double nightServicePrice) {
		this.nightServicePrice = nightServicePrice;
	}

	public void setPlanUseDate(java.lang.Integer planUseDate) {
		this.planUseDate = planUseDate;
	}

	public java.lang.Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(java.lang.Integer serviceId) {
		this.serviceId = serviceId;
	}

	public java.lang.Integer getPlanId() {
		return planId;
	}

	public void setPlanId(java.lang.Integer planId) {
		this.planId = planId;
	}

	public java.util.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	public java.lang.Integer getIncludeLongDistance() {
		return includeLongDistance;
	}

	public void setIncludeLongDistance(java.lang.Integer includeLongDistance) {
		this.includeLongDistance = includeLongDistance;
	}

	public java.lang.Integer getIncludeMinute() {
		return includeMinute;
	}

	public void setIncludeMinute(java.lang.Integer includeMinute) {
		this.includeMinute = includeMinute;
	}

	public java.lang.Integer getIncludeMileage() {
		return includeMileage;
	}

	public void setIncludeMileage(java.lang.Integer includeMileage) {
		this.includeMileage = includeMileage;
	}

	public String getPlanVer() {
		return planVer;
	}

	public void setPlanVer(String planVer) {
		this.planVer = planVer;
	}
}
