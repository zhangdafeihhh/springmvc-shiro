package com.zhuanche.dto.rentcar;

import java.io.Serializable;

/**
 * 取消订单详细
 * @author jiadongdong
 *
 */
public class CancelOrderDetailDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
	
	private String allianceName;	//加盟商名称
	private String driverName;	//司机姓名
	private String customerId;	//订车人ID
	private String predictPrice;	//预估金额
	private String channelName;	//下单渠道名称
	private String productTypeName;	//产品类型名称
	private String orderVehicleTypeName;	//预定车型名称
	private String bindVehicleTypeName;//	绑单车型名称
	private String assignOrderRadius;//	派单半径
	private String leanFactor;//	倾斜系数
	private String driverGuideDistance;//	司乘导航距离
	private String cancelReason;//	取消原因
	private String planAbordLocation;//	预定上车地址
	private String planDebusLocation;//	预定下车地址
	
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
	public String getBindVehicleTypeName() {
		return bindVehicleTypeName;
	}
	public void setBindVehicleTypeName(String bindVehicleTypeName) {
		this.bindVehicleTypeName = bindVehicleTypeName;
	}
	public String getAssignOrderRadius() {
		return assignOrderRadius;
	}
	public void setAssignOrderRadius(String assignOrderRadius) {
		this.assignOrderRadius = assignOrderRadius;
	}
	public String getLeanFactor() {
		return leanFactor;
	}
	public void setLeanFactor(String leanFactor) {
		this.leanFactor = leanFactor;
	}
	public String getDriverGuideDistance() {
		return driverGuideDistance;
	}
	public void setDriverGuideDistance(String driverGuideDistance) {
		this.driverGuideDistance = driverGuideDistance;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public String getPlanAbordLocation() {
		return planAbordLocation;
	}
	public void setPlanAbordLocation(String planAbordLocation) {
		this.planAbordLocation = planAbordLocation;
	}
	public String getPlanDebusLocation() {
		return planDebusLocation;
	}
	public void setPlanDebusLocation(String planDebusLocation) {
		this.planDebusLocation = planDebusLocation;
	}

}