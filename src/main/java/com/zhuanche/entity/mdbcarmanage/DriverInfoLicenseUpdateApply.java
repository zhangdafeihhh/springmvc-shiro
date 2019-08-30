package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class DriverInfoLicenseUpdateApply {
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
     * 司机手机号
     */
    private String driverPhone;

    /**
     * 车牌号
     */
    private String licensePlates;

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
     * 操作状态[10 初始态 20 草稿 30 审核中 40 审核失败  50 等待验车 60 已通知验车 70 验车未通过 80 验车通过]
     */
    private Integer status;

    /**
     * 资料提交成功时间
     */
    private Date submitTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 验真结果
     */
    private String verifyReason;

    /**
     * 通知验车时间
     */
    private Date notifyTime;

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
     * 更新时间
     */
    private Date updateTime;

    /**
     * 处理意见
     */
    private String operateReason;

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
     * 新车牌号
     */
    private String licensePlatesNew;

    /**
     * 租车协议
     */
    private String carRentProtocol;

    /**
     * 老车辆识别码
     */
    private String frameNo;

    /**
     * 车辆识别码(新)
     */
    private String frameNoNew;

    /**
     * 车辆更换状态 0：未更换 1：已更换
     */
    private Integer changeStatus;

    /**
     * 司机换车方式 0：新绑车辆 1：更换车辆
     */
    private Integer changeWay;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 车型名称
     */
    private String modelName;

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
        this.driverName = driverName == null ? null : driverName.trim();
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone == null ? null : driverPhone.trim();
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
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
        this.cityName = cityName == null ? null : cityName.trim();
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
        this.supplierName = supplierName == null ? null : supplierName.trim();
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
        this.teamName = teamName == null ? null : teamName.trim();
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
        this.carModelName = carModelName == null ? null : carModelName.trim();
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
        this.modelDetail = modelDetail == null ? null : modelDetail.trim();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
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
        this.carModelNameNew = carModelNameNew == null ? null : carModelNameNew.trim();
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
        this.modelDetailNew = modelDetailNew == null ? null : modelDetailNew.trim();
    }

    public String getColorNew() {
        return colorNew;
    }

    public void setColorNew(String colorNew) {
        this.colorNew = colorNew == null ? null : colorNew.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getVerifyReason() {
        return verifyReason;
    }

    public void setVerifyReason(String verifyReason) {
        this.verifyReason = verifyReason == null ? null : verifyReason.trim();
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
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
        this.createName = createName == null ? null : createName.trim();
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
        this.updateName = updateName == null ? null : updateName.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOperateReason() {
        return operateReason;
    }

    public void setOperateReason(String operateReason) {
        this.operateReason = operateReason == null ? null : operateReason.trim();
    }

    public String getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(String vehicleOwner) {
        this.vehicleOwner = vehicleOwner == null ? null : vehicleOwner.trim();
    }

    public String getVehicleRegistrationDate() {
        return vehicleRegistrationDate;
    }

    public void setVehicleRegistrationDate(String vehicleRegistrationDate) {
        this.vehicleRegistrationDate = vehicleRegistrationDate == null ? null : vehicleRegistrationDate.trim();
    }

    public String getVehicleDrivingLicense() {
        return vehicleDrivingLicense;
    }

    public void setVehicleDrivingLicense(String vehicleDrivingLicense) {
        this.vehicleDrivingLicense = vehicleDrivingLicense == null ? null : vehicleDrivingLicense.trim();
    }

    public String getVehiclePhotoGroup() {
        return vehiclePhotoGroup;
    }

    public void setVehiclePhotoGroup(String vehiclePhotoGroup) {
        this.vehiclePhotoGroup = vehiclePhotoGroup == null ? null : vehiclePhotoGroup.trim();
    }

    public String getVehicleOwnerNew() {
        return vehicleOwnerNew;
    }

    public void setVehicleOwnerNew(String vehicleOwnerNew) {
        this.vehicleOwnerNew = vehicleOwnerNew == null ? null : vehicleOwnerNew.trim();
    }

    public String getVehicleRegistrationDateNew() {
        return vehicleRegistrationDateNew;
    }

    public void setVehicleRegistrationDateNew(String vehicleRegistrationDateNew) {
        this.vehicleRegistrationDateNew = vehicleRegistrationDateNew == null ? null : vehicleRegistrationDateNew.trim();
    }

    public String getVehicleDrivingLicenseNew() {
        return vehicleDrivingLicenseNew;
    }

    public void setVehicleDrivingLicenseNew(String vehicleDrivingLicenseNew) {
        this.vehicleDrivingLicenseNew = vehicleDrivingLicenseNew == null ? null : vehicleDrivingLicenseNew.trim();
    }

    public String getVehiclePhotoGroupNew() {
        return vehiclePhotoGroupNew;
    }

    public void setVehiclePhotoGroupNew(String vehiclePhotoGroupNew) {
        this.vehiclePhotoGroupNew = vehiclePhotoGroupNew == null ? null : vehiclePhotoGroupNew.trim();
    }

    public Integer getInitiatorType() {
        return initiatorType;
    }

    public void setInitiatorType(Integer initiatorType) {
        this.initiatorType = initiatorType;
    }

    public String getLicensePlatesNew() {
        return licensePlatesNew;
    }

    public void setLicensePlatesNew(String licensePlatesNew) {
        this.licensePlatesNew = licensePlatesNew == null ? null : licensePlatesNew.trim();
    }

    public String getCarRentProtocol() {
        return carRentProtocol;
    }

    public void setCarRentProtocol(String carRentProtocol) {
        this.carRentProtocol = carRentProtocol == null ? null : carRentProtocol.trim();
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo == null ? null : frameNo.trim();
    }

    public String getFrameNoNew() {
        return frameNoNew;
    }

    public void setFrameNoNew(String frameNoNew) {
        this.frameNoNew = frameNoNew == null ? null : frameNoNew.trim();
    }

    public Integer getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(Integer changeStatus) {
        this.changeStatus = changeStatus;
    }

    public Integer getChangeWay() {
        return changeWay;
    }

    public void setChangeWay(Integer changeWay) {
        this.changeWay = changeWay;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? null : modelName.trim();
    }
}