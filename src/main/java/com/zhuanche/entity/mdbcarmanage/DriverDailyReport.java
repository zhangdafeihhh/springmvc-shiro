package com.zhuanche.entity.mdbcarmanage;

import java.math.BigDecimal;
import java.util.Date;

public class DriverDailyReport {
    private Integer id;

    private String statDate;

    private String licensePlates;

    private Integer driverId;

    private String driverName;

    private Integer operationNum;

    private Date upOnlineTime;

    private BigDecimal orderTime;

    private BigDecimal serviceTime;

    private BigDecimal onlineTime;

    private BigDecimal actualPay;

    private BigDecimal driverGetPay;

    private BigDecimal driverOutPay;

    private BigDecimal orderMileage;

    private BigDecimal serviceMileage;

    private Integer assignOrderNum;

    private Integer contendOrderNum;

    private Integer platformOrderNum;

    private Integer getPlaneNum;

    private Integer outPlaneNum;

    private Integer carTeamId;

    private Integer travelTime;

    private BigDecimal travelMileage;

    private Integer travelTimeStart;

    private BigDecimal travelMileageStart;

    private Integer travelTimeEnd;

    private BigDecimal travelMileageEnd;

    private Integer supplierId;

    private Integer cityId;

    private BigDecimal forcedTime;
    
    private String teamName;
    private String groupName;
    
    private String upOnlineTimeStart;
    private String upOnlineTimeEnd;
    
    private String statDateStart;
    private String statDateEnd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public Integer getOperationNum() {
        return operationNum;
    }

    public void setOperationNum(Integer operationNum) {
        this.operationNum = operationNum;
    }

    public Date getUpOnlineTime() {
        return upOnlineTime;
    }

    public void setUpOnlineTime(Date upOnlineTime) {
        this.upOnlineTime = upOnlineTime;
    }

    public BigDecimal getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(BigDecimal orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(BigDecimal serviceTime) {
        this.serviceTime = serviceTime;
    }

    public BigDecimal getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(BigDecimal onlineTime) {
        this.onlineTime = onlineTime;
    }

    public BigDecimal getActualPay() {
        return actualPay;
    }

    public void setActualPay(BigDecimal actualPay) {
        this.actualPay = actualPay;
    }

    public BigDecimal getDriverGetPay() {
        return driverGetPay;
    }

    public void setDriverGetPay(BigDecimal driverGetPay) {
        this.driverGetPay = driverGetPay;
    }

    public BigDecimal getDriverOutPay() {
        return driverOutPay;
    }

    public void setDriverOutPay(BigDecimal driverOutPay) {
        this.driverOutPay = driverOutPay;
    }

    public BigDecimal getOrderMileage() {
        return orderMileage;
    }

    public void setOrderMileage(BigDecimal orderMileage) {
        this.orderMileage = orderMileage;
    }

    public BigDecimal getServiceMileage() {
        return serviceMileage;
    }

    public void setServiceMileage(BigDecimal serviceMileage) {
        this.serviceMileage = serviceMileage;
    }

    public Integer getAssignOrderNum() {
        return assignOrderNum;
    }

    public void setAssignOrderNum(Integer assignOrderNum) {
        this.assignOrderNum = assignOrderNum;
    }

    public Integer getContendOrderNum() {
        return contendOrderNum;
    }

    public void setContendOrderNum(Integer contendOrderNum) {
        this.contendOrderNum = contendOrderNum;
    }

    public Integer getPlatformOrderNum() {
        return platformOrderNum;
    }

    public void setPlatformOrderNum(Integer platformOrderNum) {
        this.platformOrderNum = platformOrderNum;
    }

    public Integer getGetPlaneNum() {
        return getPlaneNum;
    }

    public void setGetPlaneNum(Integer getPlaneNum) {
        this.getPlaneNum = getPlaneNum;
    }

    public Integer getOutPlaneNum() {
        return outPlaneNum;
    }

    public void setOutPlaneNum(Integer outPlaneNum) {
        this.outPlaneNum = outPlaneNum;
    }

    public Integer getCarTeamId() {
        return carTeamId;
    }

    public void setCarTeamId(Integer carTeamId) {
        this.carTeamId = carTeamId;
    }

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    public BigDecimal getTravelMileage() {
        return travelMileage;
    }

    public void setTravelMileage(BigDecimal travelMileage) {
        this.travelMileage = travelMileage;
    }

    public Integer getTravelTimeStart() {
        return travelTimeStart;
    }

    public void setTravelTimeStart(Integer travelTimeStart) {
        this.travelTimeStart = travelTimeStart;
    }

    public BigDecimal getTravelMileageStart() {
        return travelMileageStart;
    }

    public void setTravelMileageStart(BigDecimal travelMileageStart) {
        this.travelMileageStart = travelMileageStart;
    }

    public Integer getTravelTimeEnd() {
        return travelTimeEnd;
    }

    public void setTravelTimeEnd(Integer travelTimeEnd) {
        this.travelTimeEnd = travelTimeEnd;
    }

    public BigDecimal getTravelMileageEnd() {
        return travelMileageEnd;
    }

    public void setTravelMileageEnd(BigDecimal travelMileageEnd) {
        this.travelMileageEnd = travelMileageEnd;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public BigDecimal getForcedTime() {
        return forcedTime;
    }

    public void setForcedTime(BigDecimal forcedTime) {
        this.forcedTime = forcedTime;
    }

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUpOnlineTimeStart() {
		return upOnlineTimeStart;
	}

	public void setUpOnlineTimeStart(String upOnlineTimeStart) {
		this.upOnlineTimeStart = upOnlineTimeStart;
	}

	public String getUpOnlineTimeEnd() {
		return upOnlineTimeEnd;
	}

	public void setUpOnlineTimeEnd(String upOnlineTimeEnd) {
		this.upOnlineTimeEnd = upOnlineTimeEnd;
	}

	public String getStatDateStart() {
		return statDateStart;
	}

	public void setStatDateStart(String statDateStart) {
		this.statDateStart = statDateStart;
	}

	public String getStatDateEnd() {
		return statDateEnd;
	}

	public void setStatDateEnd(String statDateEnd) {
		this.statDateEnd = statDateEnd;
	}
}