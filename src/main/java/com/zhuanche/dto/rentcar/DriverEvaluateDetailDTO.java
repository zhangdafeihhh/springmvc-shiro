package com.zhuanche.dto.rentcar;


import java.io.Serializable;

/**
 * 司机评价详情
 * @author jiadongdong
 *
 */
public class DriverEvaluateDetailDTO implements Serializable{
	
	private static final long serialVersionUID = -1373760761780841082L;
	private String driverEvaluateRemark;//	备注
	private String evaluateCustomerId;//	评价用户ID
	private String memberRankName;//	会员级别名称
	private String customerName;//	乘客姓名
	private String orderSourceName;// 订单来源名称
	private String actualAboardTime;//	实际上车时间
	private String completeTime;//	订单完成时间
	private String actualOnboardLocation;//	实际上车地点
	private String actualDebusLocation;//	实际下车地点
	private String appScore;//	APP评价分数
	private String appEvaluateText;//	APP评价内容
	private String appRemark;//	APP评价备注
	private String osName;//	操作系统
	private String osVersion;//	操作系统版本
	private String supervisorNo;//	服务监督号
	public String getDriverEvaluateRemark() {
		return driverEvaluateRemark;
	}
	public void setDriverEvaluateRemark(String driverEvaluateRemark) {
		this.driverEvaluateRemark = driverEvaluateRemark;
	}
	public String getEvaluateCustomerId() {
		return evaluateCustomerId;
	}
	public void setEvaluateCustomerId(String evaluateCustomerId) {
		this.evaluateCustomerId = evaluateCustomerId;
	}
	public String getMemberRankName() {
		return memberRankName;
	}
	public void setMemberRankName(String memberRankName) {
		this.memberRankName = memberRankName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getOrderSourceName() {
		return orderSourceName;
	}
	public void setOrderSourceName(String orderSourceName) {
		this.orderSourceName = orderSourceName;
	}
	public String getActualAboardTime() {
		return actualAboardTime;
	}
	public void setActualAboardTime(String actualAboardTime) {
		this.actualAboardTime = actualAboardTime;
	}
	public String getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}
	public String getActualOnboardLocation() {
		return actualOnboardLocation;
	}
	public void setActualOnboardLocation(String actualOnboardLocation) {
		this.actualOnboardLocation = actualOnboardLocation;
	}
	public String getActualDebusLocation() {
		return actualDebusLocation;
	}
	public void setActualDebusLocation(String actualDebusLocation) {
		this.actualDebusLocation = actualDebusLocation;
	}
	public String getAppScore() {
		return appScore;
	}
	public void setAppScore(String appScore) {
		this.appScore = appScore;
	}
	public String getAppEvaluateText() {
		return appEvaluateText;
	}
	public void setAppEvaluateText(String appEvaluateText) {
		this.appEvaluateText = appEvaluateText;
	}
	public String getAppRemark() {
		return appRemark;
	}
	public void setAppRemark(String appRemark) {
		this.appRemark = appRemark;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getSupervisorNo() {
		return supervisorNo;
	}
	public void setSupervisorNo(String supervisorNo) {
		this.supervisorNo = supervisorNo;
	}
	
	
}