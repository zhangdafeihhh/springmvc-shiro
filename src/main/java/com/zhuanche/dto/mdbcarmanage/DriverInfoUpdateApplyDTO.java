package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.entity.common.Base;

import java.util.Date;

/**
 * driver_info_update_apply
 * @author
 */
public class DriverInfoUpdateApplyDTO extends Base {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 司机ID
     */
    private Integer driverId;

    /**
     * 司机名称
     */
    private String driverName;

    /**
     * 车牌号
     */
    private String licensePlates;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 车队ID
     */
    private Integer teamId;

    /**
     * 车队名称
     */
    private String teamName;

    /**
     * 司机手机号(原)
     */
    private String driverPhone;

    /**
     * 车型 关联车型表(原)
     */
    private Integer carModelId;

    /**
     * 车型名称(原)
     */
    private String carModelName;

    /**
     * 购买日期(原)
     */
    private Date carPurchaseDate;

    /**
     * 具体车型(原)
     */
    private String modelDetail;

    /**
     * 车辆颜色(原)
     */
    private String color;

    /**
     * 司机ID(新,车辆修改申请所需)
     */
    private Integer driverIdNew;

    /**
     * 司机名称(新,车辆修改申请所需)
     */
    private String driverNameNew;

    /**
     * 手机号(新,司机修改申请所需)
     */
    private String driverPhoneNew;

    /**
     * 车型 关联车型表(新,车辆修改申请所需)
     */
    private Integer carModelIdNew;

    /**
     * 车型名称(新,车辆修改申请所需)
     */
    private String carModelNameNew;

    /**
     * 购买日期(新,车辆修改申请所需)
     */
    private Date carPurchaseDateNew;

    /**
     * 具体车型(新,车辆修改申请所需)
     */
    private String modelDetailNew;

    /**
     * 车辆颜色(新,车辆修改申请所需)
     */
    private String colorNew;

    /**
     * 操作状态[1-已提交,2-已完成,3-未通过]
     */
    private Integer status;

    /**
     * 业务类型(1-司机修改,2-车辆修改)
     */
    private Integer type;

    /**
     * 操作人ID
     */
    private Integer createId;

    /**
     * 操作人姓名
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作人ID
     */
    private Integer updateId;

    /**
     * 操作人姓名
     */
    private String updateName;

    /**
     * 处理意见
     */
    private String operateReason;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String createDateBegin;
    private String createDateEnd;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        this.driverName = driverName;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Integer getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(Integer carModelId) {
        this.carModelId = carModelId;
    }

    public String getCarModelName() {
        return carModelName;
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
    }

    public Date getCarPurchaseDate() {
        return carPurchaseDate;
    }

    public void setCarPurchaseDate(Date carPurchaseDate) {
        this.carPurchaseDate = carPurchaseDate;
    }

    public String getModelDetail() {
        return modelDetail;
    }

    public void setModelDetail(String modelDetail) {
        this.modelDetail = modelDetail;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDriverIdNew() {
        return driverIdNew;
    }

    public void setDriverIdNew(Integer driverIdNew) {
        this.driverIdNew = driverIdNew;
    }

    public String getDriverNameNew() {
        return driverNameNew;
    }

    public void setDriverNameNew(String driverNameNew) {
        this.driverNameNew = driverNameNew;
    }

    public String getDriverPhoneNew() {
        return driverPhoneNew;
    }

    public void setDriverPhoneNew(String driverPhoneNew) {
        this.driverPhoneNew = driverPhoneNew;
    }

    public Integer getCarModelIdNew() {
        return carModelIdNew;
    }

    public void setCarModelIdNew(Integer carModelIdNew) {
        this.carModelIdNew = carModelIdNew;
    }

    public String getCarModelNameNew() {
        return carModelNameNew;
    }

    public void setCarModelNameNew(String carModelNameNew) {
        this.carModelNameNew = carModelNameNew;
    }

    public Date getCarPurchaseDateNew() {
        return carPurchaseDateNew;
    }

    public void setCarPurchaseDateNew(Date carPurchaseDateNew) {
        this.carPurchaseDateNew = carPurchaseDateNew;
    }

    public String getModelDetailNew() {
        return modelDetailNew;
    }

    public void setModelDetailNew(String modelDetailNew) {
        this.modelDetailNew = modelDetailNew;
    }

    public String getColorNew() {
        return colorNew;
    }

    public void setColorNew(String colorNew) {
        this.colorNew = colorNew;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCreateId() {
        return createId;
    }

    public void setCreateId(Integer createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    public String getOperateReason() {
        return operateReason;
    }

    public void setOperateReason(String operateReason) {
        this.operateReason = operateReason;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
        DriverInfoUpdateApplyDTO other = (DriverInfoUpdateApplyDTO) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDriverId() == null ? other.getDriverId() == null : this.getDriverId().equals(other.getDriverId()))
                && (this.getDriverName() == null ? other.getDriverName() == null : this.getDriverName().equals(other.getDriverName()))
                && (this.getLicensePlates() == null ? other.getLicensePlates() == null : this.getLicensePlates().equals(other.getLicensePlates()))
                && (this.getIdCardNo() == null ? other.getIdCardNo() == null : this.getIdCardNo().equals(other.getIdCardNo()))
                && (this.getCityId() == null ? other.getCityId() == null : this.getCityId().equals(other.getCityId()))
                && (this.getCityName() == null ? other.getCityName() == null : this.getCityName().equals(other.getCityName()))
                && (this.getSupplierId() == null ? other.getSupplierId() == null : this.getSupplierId().equals(other.getSupplierId()))
                && (this.getSupplierName() == null ? other.getSupplierName() == null : this.getSupplierName().equals(other.getSupplierName()))
                && (this.getTeamId() == null ? other.getTeamId() == null : this.getTeamId().equals(other.getTeamId()))
                && (this.getTeamName() == null ? other.getTeamName() == null : this.getTeamName().equals(other.getTeamName()))
                && (this.getDriverPhone() == null ? other.getDriverPhone() == null : this.getDriverPhone().equals(other.getDriverPhone()))
                && (this.getCarModelId() == null ? other.getCarModelId() == null : this.getCarModelId().equals(other.getCarModelId()))
                && (this.getCarModelName() == null ? other.getCarModelName() == null : this.getCarModelName().equals(other.getCarModelName()))
                && (this.getCarPurchaseDate() == null ? other.getCarPurchaseDate() == null : this.getCarPurchaseDate().equals(other.getCarPurchaseDate()))
                && (this.getModelDetail() == null ? other.getModelDetail() == null : this.getModelDetail().equals(other.getModelDetail()))
                && (this.getColor() == null ? other.getColor() == null : this.getColor().equals(other.getColor()))
                && (this.getDriverIdNew() == null ? other.getDriverIdNew() == null : this.getDriverIdNew().equals(other.getDriverIdNew()))
                && (this.getDriverNameNew() == null ? other.getDriverNameNew() == null : this.getDriverNameNew().equals(other.getDriverNameNew()))
                && (this.getDriverPhoneNew() == null ? other.getDriverPhoneNew() == null : this.getDriverPhoneNew().equals(other.getDriverPhoneNew()))
                && (this.getCarModelIdNew() == null ? other.getCarModelIdNew() == null : this.getCarModelIdNew().equals(other.getCarModelIdNew()))
                && (this.getCarModelNameNew() == null ? other.getCarModelNameNew() == null : this.getCarModelNameNew().equals(other.getCarModelNameNew()))
                && (this.getCarPurchaseDateNew() == null ? other.getCarPurchaseDateNew() == null : this.getCarPurchaseDateNew().equals(other.getCarPurchaseDateNew()))
                && (this.getModelDetailNew() == null ? other.getModelDetailNew() == null : this.getModelDetailNew().equals(other.getModelDetailNew()))
                && (this.getColorNew() == null ? other.getColorNew() == null : this.getColorNew().equals(other.getColorNew()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
                && (this.getCreateId() == null ? other.getCreateId() == null : this.getCreateId().equals(other.getCreateId()))
                && (this.getCreateName() == null ? other.getCreateName() == null : this.getCreateName().equals(other.getCreateName()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateId() == null ? other.getUpdateId() == null : this.getUpdateId().equals(other.getUpdateId()))
                && (this.getUpdateName() == null ? other.getUpdateName() == null : this.getUpdateName().equals(other.getUpdateName()))
                && (this.getOperateReason() == null ? other.getOperateReason() == null : this.getOperateReason().equals(other.getOperateReason()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDriverId() == null) ? 0 : getDriverId().hashCode());
        result = prime * result + ((getDriverName() == null) ? 0 : getDriverName().hashCode());
        result = prime * result + ((getLicensePlates() == null) ? 0 : getLicensePlates().hashCode());
        result = prime * result + ((getIdCardNo() == null) ? 0 : getIdCardNo().hashCode());
        result = prime * result + ((getCityId() == null) ? 0 : getCityId().hashCode());
        result = prime * result + ((getCityName() == null) ? 0 : getCityName().hashCode());
        result = prime * result + ((getSupplierId() == null) ? 0 : getSupplierId().hashCode());
        result = prime * result + ((getSupplierName() == null) ? 0 : getSupplierName().hashCode());
        result = prime * result + ((getTeamId() == null) ? 0 : getTeamId().hashCode());
        result = prime * result + ((getTeamName() == null) ? 0 : getTeamName().hashCode());
        result = prime * result + ((getDriverPhone() == null) ? 0 : getDriverPhone().hashCode());
        result = prime * result + ((getCarModelId() == null) ? 0 : getCarModelId().hashCode());
        result = prime * result + ((getCarModelName() == null) ? 0 : getCarModelName().hashCode());
        result = prime * result + ((getCarPurchaseDate() == null) ? 0 : getCarPurchaseDate().hashCode());
        result = prime * result + ((getModelDetail() == null) ? 0 : getModelDetail().hashCode());
        result = prime * result + ((getColor() == null) ? 0 : getColor().hashCode());
        result = prime * result + ((getDriverIdNew() == null) ? 0 : getDriverIdNew().hashCode());
        result = prime * result + ((getDriverNameNew() == null) ? 0 : getDriverNameNew().hashCode());
        result = prime * result + ((getDriverPhoneNew() == null) ? 0 : getDriverPhoneNew().hashCode());
        result = prime * result + ((getCarModelIdNew() == null) ? 0 : getCarModelIdNew().hashCode());
        result = prime * result + ((getCarModelNameNew() == null) ? 0 : getCarModelNameNew().hashCode());
        result = prime * result + ((getCarPurchaseDateNew() == null) ? 0 : getCarPurchaseDateNew().hashCode());
        result = prime * result + ((getModelDetailNew() == null) ? 0 : getModelDetailNew().hashCode());
        result = prime * result + ((getColorNew() == null) ? 0 : getColorNew().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getCreateId() == null) ? 0 : getCreateId().hashCode());
        result = prime * result + ((getCreateName() == null) ? 0 : getCreateName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateId() == null) ? 0 : getUpdateId().hashCode());
        result = prime * result + ((getUpdateName() == null) ? 0 : getUpdateName().hashCode());
        result = prime * result + ((getOperateReason() == null) ? 0 : getOperateReason().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", driverId=").append(driverId);
        sb.append(", driverName=").append(driverName);
        sb.append(", licensePlates=").append(licensePlates);
        sb.append(", idCardNo=").append(idCardNo);
        sb.append(", cityId=").append(cityId);
        sb.append(", cityName=").append(cityName);
        sb.append(", supplierId=").append(supplierId);
        sb.append(", supplierName=").append(supplierName);
        sb.append(", teamId=").append(teamId);
        sb.append(", teamName=").append(teamName);
        sb.append(", driverPhone=").append(driverPhone);
        sb.append(", carModelId=").append(carModelId);
        sb.append(", carModelName=").append(carModelName);
        sb.append(", carPurchaseDate=").append(carPurchaseDate);
        sb.append(", modelDetail=").append(modelDetail);
        sb.append(", color=").append(color);
        sb.append(", driverIdNew=").append(driverIdNew);
        sb.append(", driverNameNew=").append(driverNameNew);
        sb.append(", driverPhoneNew=").append(driverPhoneNew);
        sb.append(", carModelIdNew=").append(carModelIdNew);
        sb.append(", carModelNameNew=").append(carModelNameNew);
        sb.append(", carPurchaseDateNew=").append(carPurchaseDateNew);
        sb.append(", modelDetailNew=").append(modelDetailNew);
        sb.append(", colorNew=").append(colorNew);
        sb.append(", status=").append(status);
        sb.append(", type=").append(type);
        sb.append(", createId=").append(createId);
        sb.append(", createName=").append(createName);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateId=").append(updateId);
        sb.append(", updateName=").append(updateName);
        sb.append(", operateReason=").append(operateReason);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}