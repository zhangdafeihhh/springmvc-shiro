package com.zhuanche.dto.rentcar;


import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarPoolMainOrderEntity;
import com.zhuanche.util.DateUtils;

import java.util.Date;
import java.util.List;

public class CarPoolMainOrderDTO extends CarPoolMainOrderEntity {

    /**
     * 供应商ID
     */
    private Integer supplierId;
    private String supplierName;

    private String cityName;

    /**
     * 车队ID
     *
     * @return
     */
    private Integer teamId;

    /**
     * 司机手机号
     *
     * @return
     */
    private String driverPhone;

    private String createDateBegin;
    private String createDateEnd;

    private String endDateBegin;
    private String endDateEnd;

    private String driverIds;
    private String driverName;
    private String serviceTypeName;
    private String groupName;

    private List<CarFactOrderInfo> carFactOrderInfoList;

    private String modeldetail;

    private String driverStartDateStr;
    private String driverEndDateStr;
    private String createDateStr;
    private String updateDateStr;

    private String routeName;//线路名称

    public String getDriverStartDateStr() {
        return driverStartDateStr;
    }

    public void setDriverStartDateStr(String driverStartDateStr) {
        this.driverStartDateStr = driverStartDateStr;
    }

    public String getDriverEndDateStr() {
        return driverEndDateStr;
    }

    public void setDriverEndDateStr(String driverEndDateStr) {
        this.driverEndDateStr = driverEndDateStr;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getUpdateDateStr() {
        return updateDateStr;
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }


    public String getModeldetail() {
        return modeldetail;
    }

    public void setModeldetail(String modeldetail) {
        this.modeldetail = modeldetail;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getCreateDateBegin() {
        return createDateBegin;
    }

    public void setCreateDateBegin(String createDateBegin) {
        this.createDateBegin = createDateBegin;
    }

    public String getCreateDateEnd() {
        return createDateEnd;
    }

    public void setCreateDateEnd(String createDateEnd) {
        this.createDateEnd = createDateEnd;
    }

    public String getEndDateBegin() {
        return endDateBegin;
    }

    public void setEndDateBegin(String endDateBegin) {
        this.endDateBegin = endDateBegin;
    }

    public String getEndDateEnd() {
        return endDateEnd;
    }

    public void setEndDateEnd(String endDateEnd) {
        this.endDateEnd = endDateEnd;
    }

	public String getDriverIds() {
		return driverIds;
	}

	public void setDriverIds(String driverIds) {
		this.driverIds = driverIds;
	}

	public List<CarFactOrderInfo> getCarFactOrderInfoList() {
		return carFactOrderInfoList;
	}

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setCarFactOrderInfoList(List<CarFactOrderInfo> carFactOrderInfoList) {
		this.carFactOrderInfoList = carFactOrderInfoList;
	}

    @Override
    public void setDriverStartDate(Date driverStartDate) {
        super.setDriverStartDate(driverStartDate);
        driverStartDateStr = DateUtils.formatDateTime(driverStartDate);
    }

    @Override
    public void setDriverEndDate(Date driverEndDate) {
        super.setDriverEndDate(driverEndDate);
        driverEndDateStr = DateUtils.formatDateTime(driverEndDate);
    }

    @Override
    public void setCreateDate(Date createDate) {
        super.setCreateDate(createDate);
        createDateStr = DateUtils.formatDateTime(createDate);
    }

    @Override
    public void setUpdateDate(Date updateDate) {
        super.setUpdateDate(updateDate);
        updateDateStr = DateUtils.formatDateTime(updateDate);
    }
}
