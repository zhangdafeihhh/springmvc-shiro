package com.zhuanche.dto.rentcar;


import java.io.Serializable;

/**
 * 司机评价
 * @author jiadongdong
 *
 */
public class DriverEvaluateDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
	private String driverId;//	司机ID
	private String driverName;//	司机姓名
	private String vehiclePlateNo;//	车牌号
	private String driverScore;//	司机评分
	private String driverCityName;//	司机所属城市名称
	private String evaluateTime;//	评价时间
	private String driverTypeName;//	司机类型名称
	private String allianceName;//	加盟商
	private String motorcardName;//	车队名称
	private String orderNo;//	订单号
	private String driverEvaluateText;//	司机评价内容
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
	public String getVehiclePlateNo() {
		return vehiclePlateNo;
	}
	public void setVehiclePlateNo(String vehiclePlateNo) {
		this.vehiclePlateNo = vehiclePlateNo;
	}
	public String getDriverScore() {
		return driverScore;
	}
	public void setDriverScore(String driverScore) {
		this.driverScore = driverScore;
	}
	public String getDriverCityName() {
		return driverCityName;
	}
	public void setDriverCityName(String driverCityName) {
		this.driverCityName = driverCityName;
	}
	public String getEvaluateTime() {
		return evaluateTime;
	}
	public void setEvaluateTime(String evaluateTime) {
		this.evaluateTime = evaluateTime;
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
	public String getMotorcardName() {
		return motorcardName;
	}
	public void setMotorcardName(String motorcardName) {
		this.motorcardName = motorcardName;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getDriverEvaluateText() {
		return driverEvaluateText;
	}
	public void setDriverEvaluateText(String driverEvaluateText) {
		this.driverEvaluateText = driverEvaluateText;
	}
   
}