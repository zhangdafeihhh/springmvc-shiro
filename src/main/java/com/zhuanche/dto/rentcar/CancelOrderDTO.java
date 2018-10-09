package com.zhuanche.dto.rentcar;


import java.io.Serializable;

/**
 * 取消订单列表
 * @author jiadongdong
 *
 */
public class CancelOrderDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
	private String orderCityName;	//下单城市
	private String createTime;	//创建时间
	private String planOnboardTime;	//预计乘车时间
	private String driverStartTime;	//司机出发时间
	private String cancelTime;	//取消时间
	private String cancelDuration;	//取消时长
	private String orderNo;	//订单号
	private String driverId;	//司机ID
	private String driverTypeName;	//司机类型
	private String allianceName;	//加盟商名称
	private String driverName;	//司机姓名
	private String customerId;	//订车人ID
	private String predictPrice;	//预估金额
	private String channelName;	//下单渠道名称
	private String productTypeName;	//产品类型名称
	private String orderVehicleTypeName;	//预定车型名称
	
	public String getOrderCityName() {
		return orderCityName;
	}
	public void setOrderCityName(String orderCityName) {
		this.orderCityName = orderCityName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getPlanOnboardTime() {
		return planOnboardTime;
	}
	public void setPlanOnboardTime(String planOnboardTime) {
		this.planOnboardTime = planOnboardTime;
	}
	public String getDriverStartTime() {
		return driverStartTime;
	}
	public void setDriverStartTime(String driverStartTime) {
		this.driverStartTime = driverStartTime;
	}
	public String getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}
	public String getCancelDuration() {
		return cancelDuration;
	}
	public void setCancelDuration(String cancelDuration) {
		this.cancelDuration = cancelDuration;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getDriverTypeName() {
		return driverTypeName;
	}
	public void setDriverTypeName(String driverTypeName) {
		this.driverTypeName = driverTypeName;
	}
	public String getAllianceName() {
		return allianceName;
	}
	public void setAllianceName(String allianceName) {
		this.allianceName = allianceName;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPredictPrice() {
		return predictPrice;
	}
	public void setPredictPrice(String predictPrice) {
		this.predictPrice = predictPrice;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
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
	
	
	
   
}