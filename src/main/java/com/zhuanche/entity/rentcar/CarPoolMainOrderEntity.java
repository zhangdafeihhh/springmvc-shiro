package com.zhuanche.entity.rentcar;


import java.math.BigDecimal;
import java.util.Date;

public class CarPoolMainOrderEntity{
    private static final long serialVersionUID = 1L;

    private Integer mainOrderId;

    /**
     * 主单号
     */
    private String mainOrderNo;

    private Integer serviceTypeId;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 15:待服务 20:已出发 30:服务中 50:已完成 60:取消
     */
    private Integer status;

    /**
     * 子订单数
     */
    private Integer orderTotalNum;

    /**
     * 完成订单数
     */
    private Integer finishOrderNum;

    /**
     * 首单id
     */
    private Integer firstOrderId;

    /**
     * 尾单id
     */
    private Integer lastOrderId;

    /**
     * 司机首单开始服务长地址
     */
    private String driverStartLongAddr;

    /**
     * 司机首单开始服务短地址
     */
    private String driverStartShortAddr;

    /**
     * 司机开始服务坐标点(经度,纬度;经度,纬度 前高德后百度)
     */
    private String driverStartPoint;

    /**
     * 司机开始服务时间
     */
    private Date driverStartDate;

    /**
     * 司机尾单结束服务长地址
     */
    private String driverEndLongAddr;

    /**
     * 司机尾单结束服务短地址
     */
    private String driverEndShortAddr;

    /**
     * 司机结束服务坐标点(经度,纬度;经度,纬度 前高德后百度)
     */
    private String driverEndPoint;

    /**
     * 司机结束服务时间
     */
    private Date driverEndDate;

    /**
     * 接单司机id
     */
    private Integer driverId;

    /**
     * 车型id
     */
    private Integer groupId;

    /**
     * 车牌
     */
    private String licensePlates;

    /**
     * 司机行驶总里程
     */
    private BigDecimal driverTotalMileage;

    /**
     * 司机行驶总时长
     */
    private Integer driverTotalTime;

    /**
     * 司机总金额
     */
    private BigDecimal driverTotalFee;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 修改人
     */
    private Integer updateBy;

    public Integer getMainOrderId() {
        return mainOrderId;
    }

    public void setMainOrderId(Integer mainOrderId) {
        this.mainOrderId = mainOrderId;
    }

    public String getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(String mainOrderNo) {
        this.mainOrderNo = mainOrderNo;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderTotalNum() {
        return orderTotalNum;
    }

    public void setOrderTotalNum(Integer orderTotalNum) {
        this.orderTotalNum = orderTotalNum;
    }

    public Integer getFinishOrderNum() {
        return finishOrderNum;
    }

    public void setFinishOrderNum(Integer finishOrderNum) {
        this.finishOrderNum = finishOrderNum;
    }

    public Integer getFirstOrderId() {
        return firstOrderId;
    }

    public void setFirstOrderId(Integer firstOrderId) {
        this.firstOrderId = firstOrderId;
    }

    public Integer getLastOrderId() {
        return lastOrderId;
    }

    public void setLastOrderId(Integer lastOrderId) {
        this.lastOrderId = lastOrderId;
    }

    public String getDriverStartLongAddr() {
        return driverStartLongAddr;
    }

    public void setDriverStartLongAddr(String driverStartLongAddr) {
        this.driverStartLongAddr = driverStartLongAddr;
    }

    public String getDriverStartShortAddr() {
        return driverStartShortAddr;
    }

    public void setDriverStartShortAddr(String driverStartShortAddr) {
        this.driverStartShortAddr = driverStartShortAddr;
    }

    public String getDriverStartPoint() {
        return driverStartPoint;
    }

    public void setDriverStartPoint(String driverStartPoint) {
        this.driverStartPoint = driverStartPoint;
    }

    public Date getDriverStartDate() {
        return driverStartDate;
    }

    public void setDriverStartDate(Date driverStartDate) {
        this.driverStartDate = driverStartDate;
    }

    public String getDriverEndLongAddr() {
        return driverEndLongAddr;
    }

    public void setDriverEndLongAddr(String driverEndLongAddr) {
        this.driverEndLongAddr = driverEndLongAddr;
    }

    public String getDriverEndShortAddr() {
        return driverEndShortAddr;
    }

    public void setDriverEndShortAddr(String driverEndShortAddr) {
        this.driverEndShortAddr = driverEndShortAddr;
    }

    public String getDriverEndPoint() {
        return driverEndPoint;
    }

    public void setDriverEndPoint(String driverEndPoint) {
        this.driverEndPoint = driverEndPoint;
    }

    public Date getDriverEndDate() {
        return driverEndDate;
    }

    public void setDriverEndDate(Date driverEndDate) {
        this.driverEndDate = driverEndDate;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public BigDecimal getDriverTotalMileage() {
        return driverTotalMileage;
    }

    public void setDriverTotalMileage(BigDecimal driverTotalMileage) {
        this.driverTotalMileage = driverTotalMileage;
    }

    public Integer getDriverTotalTime() {
        return driverTotalTime;
    }

    public void setDriverTotalTime(Integer driverTotalTime) {
        this.driverTotalTime = driverTotalTime;
    }

    public BigDecimal getDriverTotalFee() {
        return driverTotalFee;
    }

    public void setDriverTotalFee(BigDecimal driverTotalFee) {
        this.driverTotalFee = driverTotalFee;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CarPoolMainOrderEntity other = (CarPoolMainOrderEntity) that;
        return (this.getMainOrderId() == null ? other.getMainOrderId() == null : this.getMainOrderId().equals(other.getMainOrderId()))
                && (this.getMainOrderNo() == null ? other.getMainOrderNo() == null : this.getMainOrderNo().equals(other.getMainOrderNo()))
                && (this.getServiceTypeId() == null ? other.getServiceTypeId() == null : this.getServiceTypeId().equals(other.getServiceTypeId()))
                && (this.getCityId() == null ? other.getCityId() == null : this.getCityId().equals(other.getCityId()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getOrderTotalNum() == null ? other.getOrderTotalNum() == null : this.getOrderTotalNum().equals(other.getOrderTotalNum()))
                && (this.getFinishOrderNum() == null ? other.getFinishOrderNum() == null : this.getFinishOrderNum().equals(other.getFinishOrderNum()))
                && (this.getFirstOrderId() == null ? other.getFirstOrderId() == null : this.getFirstOrderId().equals(other.getFirstOrderId()))
                && (this.getLastOrderId() == null ? other.getLastOrderId() == null : this.getLastOrderId().equals(other.getLastOrderId()))
                && (this.getDriverStartLongAddr() == null ? other.getDriverStartLongAddr() == null : this.getDriverStartLongAddr().equals(other.getDriverStartLongAddr()))
                && (this.getDriverStartShortAddr() == null ? other.getDriverStartShortAddr() == null : this.getDriverStartShortAddr().equals(other.getDriverStartShortAddr()))
                && (this.getDriverStartPoint() == null ? other.getDriverStartPoint() == null : this.getDriverStartPoint().equals(other.getDriverStartPoint()))
                && (this.getDriverStartDate() == null ? other.getDriverStartDate() == null : this.getDriverStartDate().equals(other.getDriverStartDate()))
                && (this.getDriverEndLongAddr() == null ? other.getDriverEndLongAddr() == null : this.getDriverEndLongAddr().equals(other.getDriverEndLongAddr()))
                && (this.getDriverEndShortAddr() == null ? other.getDriverEndShortAddr() == null : this.getDriverEndShortAddr().equals(other.getDriverEndShortAddr()))
                && (this.getDriverEndPoint() == null ? other.getDriverEndPoint() == null : this.getDriverEndPoint().equals(other.getDriverEndPoint()))
                && (this.getDriverEndDate() == null ? other.getDriverEndDate() == null : this.getDriverEndDate().equals(other.getDriverEndDate()))
                && (this.getDriverId() == null ? other.getDriverId() == null : this.getDriverId().equals(other.getDriverId()))
                && (this.getGroupId() == null ? other.getGroupId() == null : this.getGroupId().equals(other.getGroupId()))
                && (this.getLicensePlates() == null ? other.getLicensePlates() == null : this.getLicensePlates().equals(other.getLicensePlates()))
                && (this.getDriverTotalMileage() == null ? other.getDriverTotalMileage() == null : this.getDriverTotalMileage().equals(other.getDriverTotalMileage()))
                && (this.getDriverTotalTime() == null ? other.getDriverTotalTime() == null : this.getDriverTotalTime().equals(other.getDriverTotalTime()))
                && (this.getDriverTotalFee() == null ? other.getDriverTotalFee() == null : this.getDriverTotalFee().equals(other.getDriverTotalFee()))
                && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
                && (this.getCreateBy() == null ? other.getCreateBy() == null : this.getCreateBy().equals(other.getCreateBy()))
                && (this.getUpdateDate() == null ? other.getUpdateDate() == null : this.getUpdateDate().equals(other.getUpdateDate()))
                && (this.getUpdateBy() == null ? other.getUpdateBy() == null : this.getUpdateBy().equals(other.getUpdateBy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMainOrderId() == null) ? 0 : getMainOrderId().hashCode());
        result = prime * result + ((getMainOrderNo() == null) ? 0 : getMainOrderNo().hashCode());
        result = prime * result + ((getServiceTypeId() == null) ? 0 : getServiceTypeId().hashCode());
        result = prime * result + ((getCityId() == null) ? 0 : getCityId().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getOrderTotalNum() == null) ? 0 : getOrderTotalNum().hashCode());
        result = prime * result + ((getFinishOrderNum() == null) ? 0 : getFinishOrderNum().hashCode());
        result = prime * result + ((getFirstOrderId() == null) ? 0 : getFirstOrderId().hashCode());
        result = prime * result + ((getLastOrderId() == null) ? 0 : getLastOrderId().hashCode());
        result = prime * result + ((getDriverStartLongAddr() == null) ? 0 : getDriverStartLongAddr().hashCode());
        result = prime * result + ((getDriverStartShortAddr() == null) ? 0 : getDriverStartShortAddr().hashCode());
        result = prime * result + ((getDriverStartPoint() == null) ? 0 : getDriverStartPoint().hashCode());
        result = prime * result + ((getDriverStartDate() == null) ? 0 : getDriverStartDate().hashCode());
        result = prime * result + ((getDriverEndLongAddr() == null) ? 0 : getDriverEndLongAddr().hashCode());
        result = prime * result + ((getDriverEndShortAddr() == null) ? 0 : getDriverEndShortAddr().hashCode());
        result = prime * result + ((getDriverEndPoint() == null) ? 0 : getDriverEndPoint().hashCode());
        result = prime * result + ((getDriverEndDate() == null) ? 0 : getDriverEndDate().hashCode());
        result = prime * result + ((getDriverId() == null) ? 0 : getDriverId().hashCode());
        result = prime * result + ((getGroupId() == null) ? 0 : getGroupId().hashCode());
        result = prime * result + ((getLicensePlates() == null) ? 0 : getLicensePlates().hashCode());
        result = prime * result + ((getDriverTotalMileage() == null) ? 0 : getDriverTotalMileage().hashCode());
        result = prime * result + ((getDriverTotalTime() == null) ? 0 : getDriverTotalTime().hashCode());
        result = prime * result + ((getDriverTotalFee() == null) ? 0 : getDriverTotalFee().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getCreateBy() == null) ? 0 : getCreateBy().hashCode());
        result = prime * result + ((getUpdateDate() == null) ? 0 : getUpdateDate().hashCode());
        result = prime * result + ((getUpdateBy() == null) ? 0 : getUpdateBy().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", mainOrderId=").append(mainOrderId);
        sb.append(", mainOrderNo=").append(mainOrderNo);
        sb.append(", serviceTypeId=").append(serviceTypeId);
        sb.append(", cityId=").append(cityId);
        sb.append(", status=").append(status);
        sb.append(", orderTotalNum=").append(orderTotalNum);
        sb.append(", finishOrderNum=").append(finishOrderNum);
        sb.append(", firstOrderId=").append(firstOrderId);
        sb.append(", lastOrderId=").append(lastOrderId);
        sb.append(", driverStartLongAddr=").append(driverStartLongAddr);
        sb.append(", driverStartShortAddr=").append(driverStartShortAddr);
        sb.append(", driverStartPoint=").append(driverStartPoint);
        sb.append(", driverStartDate=").append(driverStartDate);
        sb.append(", driverEndLongAddr=").append(driverEndLongAddr);
        sb.append(", driverEndShortAddr=").append(driverEndShortAddr);
        sb.append(", driverEndPoint=").append(driverEndPoint);
        sb.append(", driverEndDate=").append(driverEndDate);
        sb.append(", driverId=").append(driverId);
        sb.append(", groupId=").append(groupId);
        sb.append(", licensePlates=").append(licensePlates);
        sb.append(", driverTotalMileage=").append(driverTotalMileage);
        sb.append(", driverTotalTime=").append(driverTotalTime);
        sb.append(", driverTotalFee=").append(driverTotalFee);
        sb.append(", createDate=").append(createDate);
        sb.append(", createBy=").append(createBy);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", updateBy=").append(updateBy);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
