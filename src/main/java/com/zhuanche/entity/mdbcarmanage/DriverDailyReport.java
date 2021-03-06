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

    private String upOnlineTime;

    private double orderTime;

    private double serviceTime;

    private double onlineTime;

    private double actualPay;

    private double driverGetPay;

    private double driverOutPay;

    private double orderMileage;

    private double serviceMileage;

    private Integer assignOrderNum;

    private Integer contendOrderNum;

    private Integer platformOrderNum;

    private Integer getPlaneNum;

    private Integer outPlaneNum;

    private Integer carTeamId;

    private Integer travelTime;

    private double travelMileage;

    private Integer travelTimeStart;

    private double travelMileageStart;

    private Integer travelTimeEnd;

    private double travelMileageEnd;

    private Integer supplierId;

    private Integer cityId;

    private double forcedTime;
    
    private String teamName;
    private String groupName;
    
    private String upOnlineTimeStart;
    private String upOnlineTimeEnd;
    
    private String statDateStart;
    private String statDateEnd;

    private String downOnlineTime;//收车时间

    private BigDecimal allTime;//总时间（司机上班就算）

    private Integer workStatus;//是否出车；1出车，0未出车（all_time>0,代表出车）

    private Date createDate;

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

    public String getUpOnlineTime() {
        return upOnlineTime;
    }

    public void setUpOnlineTime(String upOnlineTime) {
        this.upOnlineTime = upOnlineTime;
    }

    public double getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(double orderTime) {
        this.orderTime = orderTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(double onlineTime) {
        this.onlineTime = onlineTime;
    }

    public double getActualPay() {
        return actualPay;
    }

    public void setActualPay(double actualPay) {
        this.actualPay = actualPay;
    }

    public double getDriverGetPay() {
        return driverGetPay;
    }

    public void setDriverGetPay(double driverGetPay) {
        this.driverGetPay = driverGetPay;
    }

    public double getDriverOutPay() {
        return driverOutPay;
    }

    public void setDriverOutPay(double driverOutPay) {
        this.driverOutPay = driverOutPay;
    }

    public double getOrderMileage() {
        return orderMileage;
    }

    public void setOrderMileage(double orderMileage) {
        this.orderMileage = orderMileage;
    }

    public double getServiceMileage() {
        return serviceMileage;
    }

    public void setServiceMileage(double serviceMileage) {
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

    public double getTravelMileage() {
        return travelMileage;
    }

    public void setTravelMileage(double travelMileage) {
        this.travelMileage = travelMileage;
    }

    public Integer getTravelTimeStart() {
        return travelTimeStart;
    }

    public void setTravelTimeStart(Integer travelTimeStart) {
        this.travelTimeStart = travelTimeStart;
    }

    public double getTravelMileageStart() {
        return travelMileageStart;
    }

    public void setTravelMileageStart(double travelMileageStart) {
        this.travelMileageStart = travelMileageStart;
    }

    public Integer getTravelTimeEnd() {
        return travelTimeEnd;
    }

    public void setTravelTimeEnd(Integer travelTimeEnd) {
        this.travelTimeEnd = travelTimeEnd;
    }

    public double getTravelMileageEnd() {
        return travelMileageEnd;
    }

    public void setTravelMileageEnd(double travelMileageEnd) {
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

    public double getForcedTime() {
        return forcedTime;
    }

    public void setForcedTime(double forcedTime) {
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

    public String getDownOnlineTime() {
        return downOnlineTime;
    }

    public void setDownOnlineTime(String downOnlineTime) {
        this.downOnlineTime = downOnlineTime;
    }

    public BigDecimal getAllTime() {
        return allTime;
    }

    public void setAllTime(BigDecimal allTime) {
        this.allTime = allTime;
    }

    public Integer getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(Integer workStatus) {
        this.workStatus = workStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}