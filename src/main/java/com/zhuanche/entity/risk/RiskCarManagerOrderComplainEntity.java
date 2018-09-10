package com.zhuanche.entity.risk;



import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
@Data
public class RiskCarManagerOrderComplainEntity   implements Serializable{

    private static final long serialVersionUID = -1962168490971137026L;
    /**
     * 主键
     */
    private String riskOrderId;

    /**
     * 订单号
     */
    private String  orderNo;
    
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 司机ID
     */
    private String driverId;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 司机手机号
     */
    private String driverPhone;
    /**
     * 订单风控状态1：待处理,2:扣除成功，3：扣除失败
     */
    private String orderRiskStatus;
    /**
     * 申诉状态，1:未申诉；2:待处理;3:申诉成功;3申诉驳回
     */
    private Integer appealStatus;
    /**
     * 申诉处理时间
     */
    private String appealProcessAt;
    /**
     * 申诉处理人
     */
    private String appealProcessBy;
    
    /**
     * 风控处理时间
     */
    private String createAt;
    private String createAtStart;//开始查询
    private String createAtEnd;//结束查询

    /**
     * 订单完成时间
     */
    private String orderEndDate;
    private String orderEndDateStart;//开始查询
    private String orderEndDateEnd;//结束查询

    /**
     * 费用总计
     */
    private BigDecimal totalCost;
   

    /**
     * 供应商ID
     */
    private String supplierId;
    
    /**
     * 供应商名称
     */
    private String leasingCompany;
    
    /**
     * 司机城市
     */
    private String cityId;

    /**
     * 司机城市名字
     */
    private String driverCityName;

    /**
     * 权限 供应商IDS
     */
    private String supplierIds;
    /**
     * 权限 司机城市
     */
    private String driverCityIds;

	private String ruleName;


    public String getRiskOrderId() {
        return riskOrderId;
    }

    public void setRiskOrderId(String riskOrderId) {
        this.riskOrderId = riskOrderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

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

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getOrderRiskStatus() {
        return orderRiskStatus;
    }

    public void setOrderRiskStatus(String orderRiskStatus) {
        this.orderRiskStatus = orderRiskStatus;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public String getAppealProcessAt() {
        return appealProcessAt;
    }

    public void setAppealProcessAt(String appealProcessAt) {
        this.appealProcessAt = appealProcessAt;
    }

    public String getAppealProcessBy() {
        return appealProcessBy;
    }

    public void setAppealProcessBy(String appealProcessBy) {
        this.appealProcessBy = appealProcessBy;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getCreateAtStart() {
        return createAtStart;
    }

    public void setCreateAtStart(String createAtStart) {
        this.createAtStart = createAtStart;
    }

    public String getCreateAtEnd() {
        return createAtEnd;
    }

    public void setCreateAtEnd(String createAtEnd) {
        this.createAtEnd = createAtEnd;
    }

    public String getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(String orderEndDate) {
        this.orderEndDate = orderEndDate;
    }

    public String getOrderEndDateStart() {
        return orderEndDateStart;
    }

    public void setOrderEndDateStart(String orderEndDateStart) {
        this.orderEndDateStart = orderEndDateStart;
    }

    public String getOrderEndDateEnd() {
        return orderEndDateEnd;
    }

    public void setOrderEndDateEnd(String orderEndDateEnd) {
        this.orderEndDateEnd = orderEndDateEnd;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getLeasingCompany() {
        return leasingCompany;
    }

    public void setLeasingCompany(String leasingCompany) {
        this.leasingCompany = leasingCompany;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getDriverCityName() {
        return driverCityName;
    }

    public void setDriverCityName(String driverCityName) {
        this.driverCityName = driverCityName;
    }

    public String getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(String supplierIds) {
        this.supplierIds = supplierIds;
    }

    public String getDriverCityIds() {
        return driverCityIds;
    }

    public void setDriverCityIds(String driverCityIds) {
        this.driverCityIds = driverCityIds;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
