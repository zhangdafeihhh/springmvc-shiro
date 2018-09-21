package com.zhuanche.dto.rentcar;

import java.io.Serializable;

/**
 * 完成订单详细
 * @author jiadongdong
 *
 */
public class CompleteOrderDetailDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
    
	/**
     * 车牌号
     */
    private String vehiclePlateNo;
    /**
     * 服务车型
     */
    private String serviceVehicleTypeName;
	/**
	 * 加盟商名称
	 */
    private String allianceName;
    /**
     * 计价前行驶里程
     */
    private String beforeChargeMiles;
    /**
     * 载客里程
     */
    private String loadedMiles;
    /**
     * 计价后行驶里程
     */
    private String chargeMiles;
    /**
     * 计价前行驶时长
     */
    private String beforeChargeDuration;
    /**
	 * 载客时长
	 */
    private String loadedDuration;
    /**
     * 计价后行驶时长
     */
    private String afterChargeDuration;
    /**
     * 实际上车地址
     */
    private String actualAbordLocation;
    /**
     * 实际下车地址
     */
    private String actualDebusLocation;
	/**
	 * 机构名称
	 */
    private String orgnizationName;
    /**
     * 酒店名称
     */
    private String hotelName;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 等待分钟
     */
    private String waitingMinutes;
	public String getVehiclePlateNo() {
		return vehiclePlateNo;
	}
	public void setVehiclePlateNo(String vehiclePlateNo) {
		this.vehiclePlateNo = vehiclePlateNo;
	}
	public String getServiceVehicleTypeName() {
		return serviceVehicleTypeName;
	}
	public void setServiceVehicleTypeName(String serviceVehicleTypeName) {
		this.serviceVehicleTypeName = serviceVehicleTypeName;
	}
	public String getAllianceName() {
		return allianceName;
	}
	public void setAllianceName(String allianceName) {
		this.allianceName = allianceName;
	}
	public String getBeforeChargeMiles() {
		return beforeChargeMiles;
	}
	public void setBeforeChargeMiles(String beforeChargeMiles) {
		this.beforeChargeMiles = beforeChargeMiles;
	}
	public String getLoadedMiles() {
		return loadedMiles;
	}
	public void setLoadedMiles(String loadedMiles) {
		this.loadedMiles = loadedMiles;
	}
	public String getChargeMiles() {
		return chargeMiles;
	}
	public void setChargeMiles(String chargeMiles) {
		this.chargeMiles = chargeMiles;
	}
	public String getBeforeChargeDuration() {
		return beforeChargeDuration;
	}
	public void setBeforeChargeDuration(String beforeChargeDuration) {
		this.beforeChargeDuration = beforeChargeDuration;
	}
	public String getLoadedDuration() {
		return loadedDuration;
	}
	public void setLoadedDuration(String loadedDuration) {
		this.loadedDuration = loadedDuration;
	}
	public String getAfterChargeDuration() {
		return afterChargeDuration;
	}
	public void setAfterChargeDuration(String afterChargeDuration) {
		this.afterChargeDuration = afterChargeDuration;
	}
	public String getActualAbordLocation() {
		return actualAbordLocation;
	}
	public void setActualAbordLocation(String actualAbordLocation) {
		this.actualAbordLocation = actualAbordLocation;
	}
	public String getActualDebusLocation() {
		return actualDebusLocation;
	}
	public void setActualDebusLocation(String actualDebusLocation) {
		this.actualDebusLocation = actualDebusLocation;
	}
	public String getOrgnizationName() {
		return orgnizationName;
	}
	public void setOrgnizationName(String orgnizationName) {
		this.orgnizationName = orgnizationName;
	}
	public String getHotelName() {
		return hotelName;
	}
	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getWaitingMinutes() {
		return waitingMinutes;
	}
	public void setWaitingMinutes(String waitingMinutes) {
		this.waitingMinutes = waitingMinutes;
	}
    
}