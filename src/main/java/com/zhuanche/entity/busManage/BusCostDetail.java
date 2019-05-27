package com.zhuanche.entity.busManage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class BusCostDetail implements Serializable {

	private static final long serialVersionUID = 798342256536459969L;

	/**
	 * 主键id
	 */
	private Integer id;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 订单id
	 */
	private Integer orderId;
	/**
	 * 订单总金额
	 */
	private BigDecimal amount;
	/**
	 * 基础价格
	 */
	private BigDecimal baseFee;
	/**
	 * 套餐含公里数（单位：公里）
	 */
	private BigDecimal includeMileage;
	/**
	 * 套餐含分钟数（单位：分钟）
	 */
	private Integer includeMinute;
	/**
	 * 车型id（详见 car_biz_car_group）
	 */
	private Integer groupId;
	/**
	 * 车型名称（详见 car_biz_car_group）
	 */
	private String groupName;
	/**
	 * 行驶总距离 （单位/公里）
	 */
	private BigDecimal distance;
	/**
	 * 超里程（单位/公里）
	 */
	private BigDecimal overDistance;
	/**
	 * 超里程单价（单位/ 元）
	 */
	private BigDecimal overDistancePrice;
	/**
	 * 超里程费（单位/ 元） --预收里程费
	 */
	private BigDecimal overDistanceFee;
	/**
	 * 总时长（单位/分钟）
	 */
	private Integer duration;
	/**
	 * 超时长（单位/分钟）
	 */
	private Integer overDuration;
	/**
	 * 超时长单价（单位/ 元）
	 */
	private BigDecimal overDurationPrice;
	/**
	 * 超时长费（单位/ 元） --预收里程费
	 */
	private BigDecimal overDurationFee;
	/**
	 * 夜间时长 （单位/分钟） --预收夜间附加费
	 */
	private int nightDuration;
	/**
	 * 夜间时长单价 （分钟/元）
	 */
	private BigDecimal nightDurationPrice;
	/**
	 * 夜间时长费 （元）
	 */
	private BigDecimal nightDurationFee;
	/**
	 * 长途里程（单位/公里）
	 */
	private double longDistant;
	/**
	 * 长途里程费 （元） --预收空驶费
	 */
	private BigDecimal longDistantFee;
	/**
	 * 其他费用（高速、机场、食宿、语言）（元）
	 */
	private BigDecimal otherFee;
	/**
	 * 取消订单违约金
	 */
	private BigDecimal damageFee;
	/**
	 * 退款金额
	 */
	private BigDecimal refundFee;
	/**
	 * 退款时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date refundDate;
	/**
	 * 创建时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;
	/**
	 * 更新时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date updateDate;
	/**
	 * @description 车型图片
	 */
	String imgUrl;
	private Integer couponId;
	/**
	 * 优惠金额 --预收优惠金额
	 */
	private BigDecimal couponAmount;
	/**
	 * 超预估里程（单位/公里）
	 */
	private BigDecimal overEstimateDistance = BigDecimal.ZERO;
	/**
	 * 超预估里程费（单位/ 元） --代收里程费
	 */
	private BigDecimal overEstimateDistanceFee = BigDecimal.ZERO;
	/**
	 * 超预估时长（单位/分钟）
	 */
	private Integer overEstimateDuration = 0;
	/**
	 * 超预估时长费（单位/ 元） --代收时长费
	 */
	private BigDecimal overEstimateDurationFee = BigDecimal.ZERO;
	/**
	 * 长途单价
	 */
	private BigDecimal longDistantPrice;
	/**
	 * 空驶定义
	 */
	private Integer includeLongDistance;
	/**
	 * 等待费单价
	 */
	private BigDecimal waitingPrice;
	/**
	 * 收费的等待时长
	 */
	private Integer waitingMinutes;
	/**
	 * 等待费
	 */
	private BigDecimal waitingFee;

	/**
	 * 价外费明细
	 */

	/**
	 * 司机住宿费 --预收司机住宿费
	 */
	private BigDecimal hotelFee = BigDecimal.ZERO;
	/**
	 * 司机餐费 --预收司机餐费
	 */
	private BigDecimal mealFee = BigDecimal.ZERO;
	/**
	 * 高速费 --代收高速费
	 */
	private BigDecimal gsFee = BigDecimal.ZERO;
	/**
	 * 停车费 --代收停车费
	 */
	private BigDecimal tcFee = BigDecimal.ZERO;
	/**
	 * 机场费
	 */
	private BigDecimal jcFee = BigDecimal.ZERO;
	/**
	 * 食宿费
	 */
	private BigDecimal ssFee = BigDecimal.ZERO;
	/**
	 * 其他费
	 */
	private BigDecimal qtFee = BigDecimal.ZERO;
	/**
	 * 代结算金额 行程完成后的支付金额 ---代收总金额
	 */
	private BigDecimal settleAmount = BigDecimal.ZERO;

	private BigDecimal settleOriginalAmount=BigDecimal.ZERO;
	/**
	 * --代收结算类型。0-帐户支付 1-信用卡支付 2-司机代收 1000：微信扫码；2000：支付宝扫码；3000：微信APP；4000：支付宝APP
	 */
	private Integer payType;

	/**
	 * 待结算金额
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date settleDate;

	/* 结算增加字段 */
	private Double overNightDistance = 0D; // 超预估夜间里程
	private BigDecimal overNightDistanceFee = BigDecimal.ZERO; // 超预估夜间里程费
	private int overNightDuration = 0; // 超预估夜间时长,单位分钟
	private BigDecimal overNightDurationFee = BigDecimal.ZERO;
	private BigDecimal nightDistanceActual = BigDecimal.ZERO; // 实际夜间里程
	private BigDecimal nightDistanceFeeActual = BigDecimal.ZERO; // 实际夜间里程费
	private int nightDurationActual = 0; // 实际夜间时长,单位分钟
	private BigDecimal nightDurationFeeActual = BigDecimal.ZERO; // 实际夜间时长费
	private BigDecimal overEstimateLongDistant = BigDecimal.ZERO; // 超预估空驶里程(公里)
	private BigDecimal overEstimateLongDistantFee = BigDecimal.ZERO; // 超预估空驶里程费(元) 代收空驶费
	private BigDecimal longDistantActual = BigDecimal.ZERO;// 实际空驶里程(公里)
	private BigDecimal longDistantFeeActual = BigDecimal.ZERO; // 实际空驶里程费(元)
	private int nightDistance = 0; // 夜间里程 （单位/公里）
	private BigDecimal nightDistancePrice = BigDecimal.ZERO; // 夜间里程单价 （分钟/元）
	private BigDecimal nightDistanceFee = BigDecimal.ZERO; // 夜间里程费 （元）

	private BigDecimal baseDiscountAmount = BigDecimal.ZERO; // 基础价折扣金额

	private BigDecimal estimateDuration = BigDecimal.ZERO; // 预估总时长 （单位/分钟）
	private BigDecimal estimateDistance = BigDecimal.ZERO; // 预估总里程 （单位/公里）

	public BigDecimal getEstimateDuration() {
		return estimateDuration;
	}

	public void setEstimateDuration(BigDecimal estimateDuration) {
		this.estimateDuration = estimateDuration;
	}

	public BigDecimal getEstimateDistance() {
		return estimateDistance;
	}

	public void setEstimateDistance(BigDecimal estimateDistance) {
		this.estimateDistance = estimateDistance;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getBaseFee() {
		return baseFee;
	}

	public void setBaseFee(BigDecimal baseFee) {
		this.baseFee = baseFee;
	}

	public BigDecimal getIncludeMileage() {
		return includeMileage;
	}

	public void setIncludeMileage(BigDecimal includeMileage) {
		this.includeMileage = includeMileage;
	}

	public Integer getIncludeMinute() {
		return includeMinute;
	}

	public void setIncludeMinute(Integer includeMinute) {
		this.includeMinute = includeMinute;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	public BigDecimal getOverDistance() {
		return overDistance;
	}

	public void setOverDistance(BigDecimal overDistance) {
		this.overDistance = overDistance;
	}

	public BigDecimal getOverDistancePrice() {
		return overDistancePrice;
	}

	public void setOverDistancePrice(BigDecimal overDistancePrice) {
		this.overDistancePrice = overDistancePrice;
	}

	public BigDecimal getOverDistanceFee() {
		return overDistanceFee;
	}

	public void setOverDistanceFee(BigDecimal overDistanceFee) {
		this.overDistanceFee = overDistanceFee;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getOverDuration() {
		return overDuration;
	}

	public void setOverDuration(Integer overDuration) {
		this.overDuration = overDuration;
	}

	public BigDecimal getOverDurationPrice() {
		return overDurationPrice;
	}

	public void setOverDurationPrice(BigDecimal overDurationPrice) {
		this.overDurationPrice = overDurationPrice;
	}

	public BigDecimal getOverDurationFee() {
		return overDurationFee;
	}

	public void setOverDurationFee(BigDecimal overDurationFee) {
		this.overDurationFee = overDurationFee;
	}

	public int getNightDuration() {
		return nightDuration;
	}

	public void setNightDuration(int nightDuration) {
		this.nightDuration = nightDuration;
	}

	public BigDecimal getNightDurationPrice() {
		return nightDurationPrice;
	}

	public void setNightDurationPrice(BigDecimal nightDurationPrice) {
		this.nightDurationPrice = nightDurationPrice;
	}

	public BigDecimal getNightDurationFee() {
		return nightDurationFee;
	}

	public void setNightDurationFee(BigDecimal nightDurationFee) {
		this.nightDurationFee = nightDurationFee;
	}

	public double getLongDistant() {
		return longDistant;
	}

	public void setLongDistant(double longDistant) {
		this.longDistant = longDistant;
	}

	public BigDecimal getLongDistantFee() {
		return longDistantFee;
	}

	public void setLongDistantFee(BigDecimal longDistantFee) {
		this.longDistantFee = longDistantFee;
	}

	public BigDecimal getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(BigDecimal otherFee) {
		this.otherFee = otherFee;
	}

	public BigDecimal getDamageFee() {
		return damageFee;
	}

	public void setDamageFee(BigDecimal damageFee) {
		this.damageFee = damageFee;
	}

	public BigDecimal getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(BigDecimal refundFee) {
		this.refundFee = refundFee;
	}

	public Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Integer getCouponId() {
		return couponId;
	}

	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	public BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public BigDecimal getOverEstimateDistance() {
		return overEstimateDistance;
	}

	public void setOverEstimateDistance(BigDecimal overEstimateDistance) {
		this.overEstimateDistance = overEstimateDistance;
	}

	public BigDecimal getOverEstimateDistanceFee() {
		return overEstimateDistanceFee;
	}

	public void setOverEstimateDistanceFee(BigDecimal overEstimateDistanceFee) {
		this.overEstimateDistanceFee = overEstimateDistanceFee;
	}

	public Integer getOverEstimateDuration() {
		return overEstimateDuration;
	}

	public void setOverEstimateDuration(Integer overEstimateDuration) {
		this.overEstimateDuration = overEstimateDuration;
	}

	public BigDecimal getOverEstimateDurationFee() {
		return overEstimateDurationFee;
	}

	public void setOverEstimateDurationFee(BigDecimal overEstimateDurationFee) {
		this.overEstimateDurationFee = overEstimateDurationFee;
	}

	public BigDecimal getLongDistantPrice() {
		return longDistantPrice;
	}

	public void setLongDistantPrice(BigDecimal longDistantPrice) {
		this.longDistantPrice = longDistantPrice;
	}

	public Integer getIncludeLongDistance() {
		return includeLongDistance;
	}

	public void setIncludeLongDistance(Integer includeLongDistance) {
		this.includeLongDistance = includeLongDistance;
	}

	public BigDecimal getWaitingPrice() {
		return waitingPrice;
	}

	public void setWaitingPrice(BigDecimal waitingPrice) {
		this.waitingPrice = waitingPrice;
	}

	public Integer getWaitingMinutes() {
		return waitingMinutes;
	}

	public void setWaitingMinutes(Integer waitingMinutes) {
		this.waitingMinutes = waitingMinutes;
	}

	public BigDecimal getWaitingFee() {
		return waitingFee;
	}

	public void setWaitingFee(BigDecimal waitingFee) {
		this.waitingFee = waitingFee;
	}

	public BigDecimal getHotelFee() {
		return hotelFee;
	}

	public void setHotelFee(BigDecimal hotelFee) {
		this.hotelFee = hotelFee;
	}

	public BigDecimal getMealFee() {
		return mealFee;
	}

	public void setMealFee(BigDecimal mealFee) {
		this.mealFee = mealFee;
	}

	public BigDecimal getGsFee() {
		return gsFee;
	}

	public void setGsFee(BigDecimal gsFee) {
		this.gsFee = gsFee;
	}

	public BigDecimal getTcFee() {
		return tcFee;
	}

	public void setTcFee(BigDecimal tcFee) {
		this.tcFee = tcFee;
	}

	public BigDecimal getJcFee() {
		return jcFee;
	}

	public void setJcFee(BigDecimal jcFee) {
		this.jcFee = jcFee;
	}

	public BigDecimal getSsFee() {
		return ssFee;
	}

	public void setSsFee(BigDecimal ssFee) {
		this.ssFee = ssFee;
	}

	public BigDecimal getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Date getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	public Double getOverNightDistance() {
		return overNightDistance;
	}

	public void setOverNightDistance(Double overNightDistance) {
		this.overNightDistance = overNightDistance;
	}

	public BigDecimal getOverNightDistanceFee() {
		return overNightDistanceFee;
	}

	public void setOverNightDistanceFee(BigDecimal overNightDistanceFee) {
		this.overNightDistanceFee = overNightDistanceFee;
	}

	public int getOverNightDuration() {
		return overNightDuration;
	}

	public void setOverNightDuration(int overNightDuration) {
		this.overNightDuration = overNightDuration;
	}

	public BigDecimal getOverNightDurationFee() {
		return overNightDurationFee;
	}

	public void setOverNightDurationFee(BigDecimal overNightDurationFee) {
		this.overNightDurationFee = overNightDurationFee;
	}

	public BigDecimal getNightDistanceActual() {
		return nightDistanceActual;
	}

	public void setNightDistanceActual(BigDecimal nightDistanceActual) {
		this.nightDistanceActual = nightDistanceActual;
	}

	public BigDecimal getNightDistanceFeeActual() {
		return nightDistanceFeeActual;
	}

	public void setNightDistanceFeeActual(BigDecimal nightDistanceFeeActual) {
		this.nightDistanceFeeActual = nightDistanceFeeActual;
	}

	public int getNightDurationActual() {
		return nightDurationActual;
	}

	public void setNightDurationActual(int nightDurationActual) {
		this.nightDurationActual = nightDurationActual;
	}

	public BigDecimal getNightDurationFeeActual() {
		return nightDurationFeeActual;
	}

	public void setNightDurationFeeActual(BigDecimal nightDurationFeeActual) {
		this.nightDurationFeeActual = nightDurationFeeActual;
	}

	public BigDecimal getOverEstimateLongDistant() {
		return overEstimateLongDistant;
	}

	public void setOverEstimateLongDistant(BigDecimal overEstimateLongDistant) {
		this.overEstimateLongDistant = overEstimateLongDistant;
	}

	public BigDecimal getOverEstimateLongDistantFee() {
		return overEstimateLongDistantFee;
	}

	public void setOverEstimateLongDistantFee(BigDecimal overEstimateLongDistantFee) {
		this.overEstimateLongDistantFee = overEstimateLongDistantFee;
	}

	public BigDecimal getLongDistantActual() {
		return longDistantActual;
	}

	public void setLongDistantActual(BigDecimal longDistantActual) {
		this.longDistantActual = longDistantActual;
	}

	public BigDecimal getLongDistantFeeActual() {
		return longDistantFeeActual;
	}

	public void setLongDistantFeeActual(BigDecimal longDistantFeeActual) {
		this.longDistantFeeActual = longDistantFeeActual;
	}

	public int getNightDistance() {
		return nightDistance;
	}

	public void setNightDistance(int nightDistance) {
		this.nightDistance = nightDistance;
	}

	public BigDecimal getNightDistancePrice() {
		return nightDistancePrice;
	}

	public void setNightDistancePrice(BigDecimal nightDistancePrice) {
		this.nightDistancePrice = nightDistancePrice;
	}

	public BigDecimal getNightDistanceFee() {
		return nightDistanceFee;
	}

	public void setNightDistanceFee(BigDecimal nightDistanceFee) {
		this.nightDistanceFee = nightDistanceFee;
	}

	public BigDecimal getBaseDiscountAmount() {
		return baseDiscountAmount;
	}

	public void setBaseDiscountAmount(BigDecimal baseDiscountAmount) {
		this.baseDiscountAmount = baseDiscountAmount;
	}

	public BigDecimal getQtFee() {
		return qtFee;
	}

	public void setQtFee(BigDecimal qtFee) {
		this.qtFee = qtFee;
	}

	public BigDecimal getSettleOriginalAmount() {
		return settleOriginalAmount;
	}

	public void setSettleOriginalAmount(BigDecimal settleOriginalAmount) {
		this.settleOriginalAmount = settleOriginalAmount;
	}
}