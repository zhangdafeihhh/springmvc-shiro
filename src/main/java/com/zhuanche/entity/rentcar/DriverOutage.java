package com.zhuanche.entity.rentcar;

import java.util.Date;


/**
 *
 * @Description:
 * @see: 此处填写需要参考的类
 * @version 2017年05月04日 下午 18:47:40ce
 * @autor
 */
public class DriverOutage{

    /**
     * serialVersionUID
     * @since JDK 1.6
     */
    private static final long serialVersionUID = 1L;

    private Integer outageId;

    /**
     * 司机id
     */
    private Integer driverId;

    /**
     * 开始停运时间
     */
    private Date outStartDate;

    /**
     * 停运时长(h)
     */
    private Double outStopLongTime;

    /**
     * 解除停运时间
     */
    private Date outEndDate;

    /**
     * 实际停运开始时间
     */
    private Date factStartDate;

    /**
     * 实际停运结束时间
     */
    private Date factEndDate;

    /**
     * 停运来源:1系统停运 2人工停运
     */
    private Integer outageSource;

    /**
     * 停运原因
     */
    private String outageReason;

    /**
     * 创建人id
     */
    private Integer createBy;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 解除人id
     */
    private Integer removeBy;

    /**
     * 解除人
     */
    private String removeName;

    /**
     * 解除时间
     */
    private Date removeDate;

    /**
     * 解除原因
     */
    private String removeReason;

    /**
     * 解除状态 1：已执行 2：执行中 3：待执行 4：撤销
     */
    private Integer removeStatus;

    /**
     * 系统停运所对应的订单号，多个订单号用逗号隔开;人工停运不填写
     */
    private String orderNos;


    //页面显示 查询
    private String driverName;
    private String driverPhone;
    private String licensePlates;
    private Integer cityId;
    private String cityName;
    private Integer supplierId;
    private String supplierName;
    private Integer carGroupId;//车组
    private String carGroupName;
    private String startDateBegin;
    private String startDateEnd;

    private String outStartDateStr;
    private String outEndDateStr;
    private String factStartDateStr;
    private String factEndDateStr;
    private String createDateStr;
    private String removeDateStr;

    private String outageIds;//批量解除是用到

    public String getOutageIds() {
        return outageIds;
    }

    public void setOutageIds(String outageIds) {
        this.outageIds = outageIds;
    }

    public String getOutStartDateStr() {
        return outStartDateStr;
    }

    public void setOutStartDateStr(String outStartDateStr) {
        this.outStartDateStr = outStartDateStr;
    }

    public String getOutEndDateStr() {
        return outEndDateStr;
    }

    public void setOutEndDateStr(String outEndDateStr) {
        this.outEndDateStr = outEndDateStr;
    }

    public String getFactStartDateStr() {
        return factStartDateStr;
    }

    public void setFactStartDateStr(String factStartDateStr) {
        this.factStartDateStr = factStartDateStr;
    }

    public String getFactEndDateStr() {
        return factEndDateStr;
    }

    public void setFactEndDateStr(String factEndDateStr) {
        this.factEndDateStr = factEndDateStr;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getRemoveDateStr() {
        return removeDateStr;
    }

    public void setRemoveDateStr(String removeDateStr) {
        this.removeDateStr = removeDateStr;
    }

    public Integer getOutageId() {
        return outageId;
    }

    public void setOutageId(Integer outageId) {
        this.outageId = outageId;
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

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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

    public Integer getCarGroupId() {
        return carGroupId;
    }

    public void setCarGroupId(Integer carGroupId) {
        this.carGroupId = carGroupId;
    }

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName;
    }

    public String getStartDateBegin() {
        return startDateBegin;
    }

    public void setStartDateBegin(String startDateBegin) {
        this.startDateBegin = startDateBegin;
    }

    public String getStartDateEnd() {
        return startDateEnd;
    }

    public void setStartDateEnd(String startDateEnd) {
        this.startDateEnd = startDateEnd;
    }

    /**
     * @return
     */
    public Integer getDriverId() {
        return driverId;
    }

    /**
     * @param driverId
     * @return
     */
    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    /**
     * @return
     */
    public Date getOutStartDate() {
        return outStartDate;
    }

    /**
     * @param outStartDate
     * @return
     */
    public void setOutStartDate(Date outStartDate) {
        this.outStartDate = outStartDate;
    }

    /**
     * @return
     */
    public Double getOutStopLongTime() {
        return outStopLongTime;
    }

    /**
     * @param outStopLongTime
     * @return
     */
    public void setOutStopLongTime(Double outStopLongTime) {
        this.outStopLongTime = outStopLongTime;
    }

    /**
     * @return
     */
    public Date getOutEndDate() {
        return outEndDate;
    }

    /**
     * @param outEndDate
     * @return
     */
    public void setOutEndDate(Date outEndDate) {
        this.outEndDate = outEndDate;
    }

    /**
     * @return
     */
    public Date getFactStartDate() {
        return factStartDate;
    }

    /**
     * @param factStartDate
     * @return
     */
    public void setFactStartDate(Date factStartDate) {
        this.factStartDate = factStartDate;
    }

    /**
     * @return
     */
    public Date getFactEndDate() {
        return factEndDate;
    }

    /**
     * @param factEndDate
     * @return
     */
    public void setFactEndDate(Date factEndDate) {
        this.factEndDate = factEndDate;
    }

    /**
     * @return
     */
    public Integer getOutageSource() {
        return outageSource;
    }

    /**
     * @param outageSource
     * @return
     */
    public void setOutageSource(Integer outageSource) {
        this.outageSource = outageSource;
    }

    /**
     * @return
     */
    public String getOutageReason() {
        return outageReason;
    }

    /**
     * @param outageReason
     * @return
     */
    public void setOutageReason(String outageReason) {
        this.outageReason = outageReason == null ? null : outageReason.trim();
    }

    /**
     * @return
     */
    public Integer getCreateBy() {
        return createBy;
    }

    /**
     * @param createBy
     * @return
     */
    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    /**
     * @return
     */
    public String getCreateName() {
        return createName;
    }

    /**
     * @param createName
     * @return
     */
    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    /**
     * @return
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     * @return
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return
     */
    public Integer getRemoveBy() {
        return removeBy;
    }

    /**
     * @param removeBy
     * @return
     */
    public void setRemoveBy(Integer removeBy) {
        this.removeBy = removeBy;
    }

    /**
     * @return
     */
    public String getRemoveName() {
        return removeName;
    }

    /**
     * @param removeName
     * @return
     */
    public void setRemoveName(String removeName) {
        this.removeName = removeName == null ? null : removeName.trim();
    }

    /**
     * @return
     */
    public Date getRemoveDate() {
        return removeDate;
    }

    /**
     * @param removeDate
     * @return
     */
    public void setRemoveDate(Date removeDate) {
        this.removeDate = removeDate;
    }

    /**
     * @return
     */
    public String getRemoveReason() {
        return removeReason;
    }

    /**
     * @param removeReason
     * @return
     */
    public void setRemoveReason(String removeReason) {
        this.removeReason = removeReason == null ? null : removeReason.trim();
    }

    /**
     * @return
     */
    public Integer getRemoveStatus() {
        return removeStatus;
    }

    /**
     * @param removeStatus
     * @return
     */
    public void setRemoveStatus(Integer removeStatus) {
        this.removeStatus = removeStatus;
    }

    /**
     * @return
     */
    public String getOrderNos() {
        return orderNos;
    }

    /**
     * @param orderNos
     * @return
     */
    public void setOrderNos(String orderNos) {
        this.orderNos = orderNos == null ? null : orderNos.trim();
    }

    @Override
    public String toString() {
        return "DriverOutageEntity [driverId="+ driverId + ", outStartDate=" + outStartDate
                + ", outStopLongTime=" + outStopLongTime + ", driverName="+ driverName
                + ", driverPhone=" + driverPhone + "]";
    }
}