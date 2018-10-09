package com.zhuanche.dto.rentcar;


import java.io.Serializable;

/**
 * 加盟商考核 
 * @author jiadongdong
 *
 */
public class AllianceCheckDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
	private String driverId;//	司机ID
	private String cityName;//	所属城市
	private String allianceName;//	加盟商
	private String driverName;//	司机姓名
	private String vehicleTypeName;//	车辆类型
	private String vehiclePlate;//	车牌号
	private String aboardDate;//	入职日期
	private String totalAboardDays;//	累计入职天数
	private String totalCompleteOrders;//	累计完成单数
	private String totalCriticisms;//	累计差评数
	private String completeOrders;//	完成订单
	private String criticisms;//	差评数
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
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
	public String getVehicleTypeName() {
		return vehicleTypeName;
	}
	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
	}
	public String getVehiclePlate() {
		return vehiclePlate;
	}
	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}
	public String getAboardDate() {
		return aboardDate;
	}
	public void setAboardDate(String aboardDate) {
		this.aboardDate = aboardDate;
	}
	public String getTotalAboardDays() {
		return totalAboardDays;
	}
	public void setTotalAboardDays(String totalAboardDays) {
		this.totalAboardDays = totalAboardDays;
	}
	public String getTotalCompleteOrders() {
		return totalCompleteOrders;
	}
	public void setTotalCompleteOrders(String totalCompleteOrders) {
		this.totalCompleteOrders = totalCompleteOrders;
	}
	public String getTotalCriticisms() {
		return totalCriticisms;
	}
	public void setTotalCriticisms(String totalCriticisms) {
		this.totalCriticisms = totalCriticisms;
	}
	public String getCompleteOrders() {
		return completeOrders;
	}
	public void setCompleteOrders(String completeOrders) {
		this.completeOrders = completeOrders;
	}
	public String getCriticisms() {
		return criticisms;
	}
	public void setCriticisms(String criticisms) {
		this.criticisms = criticisms;
	}

	
	
	
}