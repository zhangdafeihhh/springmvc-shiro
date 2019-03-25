package com.zhuanche.dto;
import java.util.List;

public class DriverCostDetailVO {
    private String actualPayAmount;
    private Integer city;
    private List<CostDetail> detail;
    private List<CostDetail> detail2;
    private List<CostDetail> detailM;
    private List<CostDetail> detailOther;
    private Integer groupId;
    private String groupName;
    private String rtnResult;
    private String serverDate;
    private String serviceName;
    private Integer serviceType;
    private String travelMileageStr;
    private String travelTimeStr;
	public String getActualPayAmount() {
		return actualPayAmount;
	}
	public void setActualPayAmount(String actualPayAmount) {
		this.actualPayAmount = actualPayAmount;
	}
	public Integer getCity() {
		return city;
	}
	public void setCity(Integer city) {
		this.city = city;
	}
	public List<CostDetail> getDetail() {
		return detail;
	}
	public void setDetail(List<CostDetail> detail) {
		this.detail = detail;
	}
	public List<CostDetail> getDetail2() {
		return detail2;
	}
	public void setDetail2(List<CostDetail> detail2) {
		this.detail2 = detail2;
	}
	public List<CostDetail> getDetailM() {
		return detailM;
	}
	public void setDetailM(List<CostDetail> detailM) {
		this.detailM = detailM;
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
	public String getRtnResult() {
		return rtnResult;
	}
	public void setRtnResult(String rtnResult) {
		this.rtnResult = rtnResult;
	}
	public String getServerDate() {
		return serverDate;
	}
	public void setServerDate(String serverDate) {
		this.serverDate = serverDate;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Integer getServiceType() {
		return serviceType;
	}
	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	public String getTravelMileageStr() {
		return travelMileageStr;
	}
	public void setTravelMileageStr(String travelMileageStr) {
		this.travelMileageStr = travelMileageStr;
	}
	public String getTravelTimeStr() {
		return travelTimeStr;
	}
	public void setTravelTimeStr(String travelTimeStr) {
		this.travelTimeStr = travelTimeStr;
	}

	public List<CostDetail> getDetailOther() {
		return detailOther;
	}

	public void setDetailOther(List<CostDetail> detailOther) {
		this.detailOther = detailOther;
	}
}