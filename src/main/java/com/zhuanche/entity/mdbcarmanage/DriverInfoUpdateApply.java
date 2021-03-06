package com.zhuanche.entity.mdbcarmanage;

import java.io.Serializable;
import java.util.Date;

/**
 * driver_info_update_apply
 * @author
 */
public class DriverInfoUpdateApply implements Serializable {
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
     * 司机身份证号(新,车辆修改申请所需)
     */
    private String idCardNoNew;

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

    /**
     * 车辆所有人(原)
     */
    private String vehicleOwner;

    /**
     * 车辆注册日期(原)
     */
    private String vehicleRegistrationDate;

    /**
     * 行驶证扫描件(原)
     */
    private String vehicleDrivingLicense;

    /**
     * 人车合影扫描件(原)
     */
    private String vehiclePhotoGroup;

    /**
     * 车辆所有人(新)
     */
    private String vehicleOwnerNew;

    /**
     * 车辆注册日期(新)
     */
    private String vehicleRegistrationDateNew;

    /**
     * 行驶证扫描件(新)
     */
    private String vehicleDrivingLicenseNew;

    /**
     * 人车合影扫描件(新)
     */
    private String vehiclePhotoGroupNew;

    /**
     * 发起方类型  1：加盟商  2：司机
     */
    private Integer initiatorType;

    /**
     * 发动机编号
     */
    private String engineNoNew;

    /**
     * 车架号
     */
    private String vinCodeNew;

    private Long newBrandId;

    private String newBrandName;


    private Long newBrandIdNew;

    private String newBrandNameNew;

    private static final long serialVersionUID = 1L;

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

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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

    public String getIdCardNoNew() {
        return idCardNoNew;
    }

    public void setIdCardNoNew(String idCardNoNew) {
        this.idCardNoNew = idCardNoNew;
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

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
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

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
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

    public String getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(String vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }

    public String getVehicleRegistrationDate() {
        return vehicleRegistrationDate;
    }

    public void setVehicleRegistrationDate(String vehicleRegistrationDate) {
        this.vehicleRegistrationDate = vehicleRegistrationDate;
    }

    public String getVehicleDrivingLicense() {
        return vehicleDrivingLicense;
    }

    public void setVehicleDrivingLicense(String vehicleDrivingLicense) {
        this.vehicleDrivingLicense = vehicleDrivingLicense;
    }

    public String getVehiclePhotoGroup() {
        return vehiclePhotoGroup;
    }

    public void setVehiclePhotoGroup(String vehiclePhotoGroup) {
        this.vehiclePhotoGroup = vehiclePhotoGroup;
    }

    public String getVehicleOwnerNew() {
        return vehicleOwnerNew;
    }

    public void setVehicleOwnerNew(String vehicleOwnerNew) {
        this.vehicleOwnerNew = vehicleOwnerNew;
    }

    public String getVehicleRegistrationDateNew() {
        return vehicleRegistrationDateNew;
    }

    public void setVehicleRegistrationDateNew(String vehicleRegistrationDateNew) {
        this.vehicleRegistrationDateNew = vehicleRegistrationDateNew;
    }

    public String getVehicleDrivingLicenseNew() {
        return vehicleDrivingLicenseNew;
    }

    public void setVehicleDrivingLicenseNew(String vehicleDrivingLicenseNew) {
        this.vehicleDrivingLicenseNew = vehicleDrivingLicenseNew;
    }

    public String getVehiclePhotoGroupNew() {
        return vehiclePhotoGroupNew;
    }

    public void setVehiclePhotoGroupNew(String vehiclePhotoGroupNew) {
        this.vehiclePhotoGroupNew = vehiclePhotoGroupNew;
    }

    public Integer getInitiatorType() {
        return initiatorType;
    }

    public void setInitiatorType(Integer initiatorType) {
        this.initiatorType = initiatorType;
    }

    public String getEngineNoNew() {
        return engineNoNew;
    }

    public void setEngineNoNew(String engineNoNew) {
        this.engineNoNew = engineNoNew;
    }

    public String getVinCodeNew() {
        return vinCodeNew;
    }

    public void setVinCodeNew(String vinCodeNew) {
        this.vinCodeNew = vinCodeNew;
    }

    public Long getNewBrandId() {
        return newBrandId;
    }

    public void setNewBrandId(Long newBrandId) {
        this.newBrandId = newBrandId;
    }

    public String getNewBrandName() {
        return newBrandName;
    }

    public void setNewBrandName(String newBrandName) {
        this.newBrandName = newBrandName;
    }

    public Long getNewBrandIdNew() {
        return newBrandIdNew;
    }

    public void setNewBrandIdNew(Long newBrandIdNew) {
        this.newBrandIdNew = newBrandIdNew;
    }

    public String getNewBrandNameNew() {
        return newBrandNameNew;
    }

    public void setNewBrandNameNew(String newBrandNameNew) {
        this.newBrandNameNew = newBrandNameNew;
    }

}