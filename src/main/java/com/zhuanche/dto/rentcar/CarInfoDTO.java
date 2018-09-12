package com.zhuanche.dto.rentcar;

import com.zhuanche.entity.common.BaseEntity;
import com.zhuanche.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarInfoDTO{


    private Integer carId;

    private Integer cityId;

    private Integer supplierId;

    private String licensePlates;

    private String brand;

    private Integer carModelId;

    private Date carPurchaseDate;

    private String modelDetail;

    private String color;

    private String carPhotographName;


    private String engineNo;

    private String frameNo;

    private String nextInspectDate;

    private String nextMaintenanceDate;

    private String rentalExpireDate;

    private String nextOperationDate;

    private String  nextSecurityDate;

    private String nextClassDate;

    private String twoLevelMaintenanceDate;

    private Integer status;

    private Integer createBy;

    private Integer updateBy;

    private String createDate;

    private String updateDate;

    private Integer auditingStatus;

    private String freighttype;

    private String totalmileage;


    /**
     * 车辆年度审验日期
     */
    private String auditingDate;

    /**
     * 发动机功率
     */
    private String vehicleEnginePower;

    /**
     * 车辆轴距
     */
    private String vehicleEngineWheelbase;

    /**
     * 车辆注册日期
     */
    private String vehicleRegistrationDate;

    /**
     * 车辆厂牌
     */
    private String vehicleBrand;

    /**
     * 卫星定位装置IMEI号
     */
    private String gpsImei;

    /**
     * 所属车主
     */
    private String vehicleOwner;

    /**
     * 车辆类型（以机动车行驶证为主）
     */
    private String vehicleType;

    /**
     * 车辆行驶证扫描件
     */
    private String vehicleDrivingLicense;

    private String vehicletec;

    private String vehiclesafe;

    private Integer lzbStatus;

    private String memo;




    /** 附加字段 **/
    private String cityName;
    private String modeName;
    private String supplierName;
    private String purchaseDate;
    private String carAge;
    private String createName;
    private String updateName;
    private String groupName; // 车型名称
    private Integer groupId; // 车型Id

    @SuppressWarnings("unused")
    private String purchaseDateString;//购车时间的String
    @SuppressWarnings("unused")
    private String rentalExpireDateString;//租赁时间的String
    @SuppressWarnings("unused")
    private String nextInspectDateString;//下次车检时间的String
    @SuppressWarnings("unused")
    private String nextMaintenanceDateString;//下次维保时间的String


    // 购买日期 开始时间
    private String purchaseDateBegin;

    // 购买日期 结束时间
    private String purchaseDateEnd;
    // 下次维保开始时间
    private String nextMaintenanceDateBegin;
    // 下次维保结束时间
    private String nextMaintenanceDateEnd;
    // 下次治安证开始时间
    private String nextSecurityDateBegin;
    // 下次治安证结束时间
    private String nextSecurityDateEnd;
    // 下次运营证开始时间
    private String nextOperationDateBegin;
    // 下次运营证结束时间
    private String nextOperationDateEnd;
    // 下次等级鉴定开始时间
    private String nextClassDateBegin;
    // 下次等级鉴定结束时间
    private String nextClassDateEnd;

    //核定载客位
    private String carryPassengers;
    //车牌颜色
    private String clicensePlatesColor;
    //车辆VIN码
    private String vehicleVINCode;
    //车辆燃料类型
    private Integer fuelType;
    //车辆发动机排量
    private String vehicleEngineDisplacement;
    //网络预约出租汽车运输证号
    private String transportNumber;
    //网络预约出租汽车运输证发证机构
    private String certificationAuthority;
    //经营区域
    private String operatingRegion;
    //网络预约出租汽车运输证有效期起
    private String transportNumberDateStart;
    //网络预约出租汽车运输证有效期止
    private String transportNumberDateEnd;
    //网约车初次登记日期
    private String firstDate;
    //车辆检修状态
    private Integer overHaulStatus;
    //发票打印设备序列号
    private String equipmentNumber;
    //卫星定位装置品牌
    private String gpsBrand;
    //卫星定位装置型号
    private String gpsType;
    //卫星定位装置安装日期
    private String gpsDate;

    private int cooperationType;//加盟类型  car_biz_cooperation_type

    private String cooperationName;//加盟类型

    //车辆状态
    private Integer isFree;

    /** 车辆图片 **/
    private String imageUrl;

    private String licensePlates1;

    private Integer oldCity;

    private Integer oldSupplierId;

    private String driverName;
    private String idCardNo;//司机身份证号

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getLicensePlates1() {
        return licensePlates1;
    }

    public void setLicensePlates1(String licensePlates1) {
        this.licensePlates1 = licensePlates1;
    }

    public Integer getOldCity() {
        return oldCity;
    }

    public void setOldCity(Integer oldCity) {
        this.oldCity = oldCity;
    }

    public Integer getOldSupplierId() {
        return oldSupplierId;
    }

    public void setOldSupplierId(Integer oldSupplierId) {
        this.oldSupplierId = oldSupplierId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }


    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates == null ? null : licensePlates.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand == null ? null : brand.trim();
    }

    public Integer getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(Integer carModelId) {
        this.carModelId = carModelId;
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

    public String getCarPhotographName() {
        return carPhotographName;
    }

    public void setCarPhotographName(String carPhotographName) {
        this.carPhotographName = carPhotographName == null ? null : carPhotographName.trim();
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo == null ? null : engineNo.trim();
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo == null ? null : frameNo.trim();
    }

    public String getNextInspectDate() {
        return nextInspectDate;
    }

    public void setNextInspectDate(String nextInspectDate) {
        this.nextInspectDate = nextInspectDate;
    }

    public String getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(String nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public String getRentalExpireDate() {
        return rentalExpireDate;
    }

    public void setRentalExpireDate(String rentalExpireDate) {
        this.rentalExpireDate = rentalExpireDate;
    }

    public String getNextOperationDate() {
        return nextOperationDate;
    }

    public void setNextOperationDate(String nextOperationDate) {
        this.nextOperationDate = nextOperationDate;
    }

    public String getNextSecurityDate() {
        return nextSecurityDate;
    }

    public void setNextSecurityDate(String nextSecurityDate) {
        this.nextSecurityDate = nextSecurityDate;
    }

    public String getNextClassDate() {
        return nextClassDate;
    }

    public void setNextClassDate(String nextClassDate) {
        this.nextClassDate = nextClassDate;
    }

    public String getTwoLevelMaintenanceDate() {
        return twoLevelMaintenanceDate;
    }

    public void setTwoLevelMaintenanceDate(String twoLevelMaintenanceDate) {
        this.twoLevelMaintenanceDate = twoLevelMaintenanceDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getAuditingStatus() {
        return auditingStatus;
    }

    public void setAuditingStatus(Integer auditingStatus) {
        this.auditingStatus = auditingStatus;
    }

    public String getFreighttype() {
        return freighttype;
    }

    public void setFreighttype(String freighttype) {
        this.freighttype = freighttype == null ? null : freighttype.trim();
    }

    public String getTotalmileage() {
        return totalmileage;
    }

    public void setTotalmileage(String totalmileage) {
        this.totalmileage = totalmileage == null ? null : totalmileage.trim();
    }


    public String getAuditingDate() {
        return auditingDate;
    }

    public void setAuditingDate(String auditingDate) {
        this.auditingDate = auditingDate == null ? null : auditingDate.trim();
    }

    public String getVehicleEnginePower() {
        return vehicleEnginePower;
    }

    public void setVehicleEnginePower(String vehicleEnginePower) {
        this.vehicleEnginePower = vehicleEnginePower == null ? null : vehicleEnginePower.trim();
    }

    public String getVehicleEngineWheelbase() {
        return vehicleEngineWheelbase;
    }

    public void setVehicleEngineWheelbase(String vehicleEngineWheelbase) {
        this.vehicleEngineWheelbase = vehicleEngineWheelbase == null ? null : vehicleEngineWheelbase.trim();
    }

    public String getVehicleRegistrationDate() {
        return vehicleRegistrationDate;
    }

    public void setVehicleRegistrationDate(String vehicleRegistrationDate) {
        this.vehicleRegistrationDate = vehicleRegistrationDate == null ? null : vehicleRegistrationDate.trim();
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand == null ? null : vehicleBrand.trim();
    }

    public String getGpsImei() {
        return gpsImei;
    }

    public void setGpsImei(String gpsImei) {
        this.gpsImei = gpsImei == null ? null : gpsImei.trim();
    }

    public String getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(String vehicleOwner) {
        this.vehicleOwner = vehicleOwner == null ? null : vehicleOwner.trim();
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType == null ? null : vehicleType.trim();
    }

    public String getVehicleDrivingLicense() {
        return vehicleDrivingLicense;
    }

    public void setVehicleDrivingLicense(String vehicleDrivingLicense) {
        this.vehicleDrivingLicense = vehicleDrivingLicense == null ? null : vehicleDrivingLicense.trim();
    }

    public String getVehicletec() {
        return vehicletec;
    }

    public void setVehicletec(String vehicletec) {
        this.vehicletec = vehicletec == null ? null : vehicletec.trim();
    }

    public String getVehiclesafe() {
        return vehiclesafe;
    }

    public void setVehiclesafe(String vehiclesafe) {
        this.vehiclesafe = vehiclesafe == null ? null : vehiclesafe.trim();
    }

    public Integer getLzbStatus() {
        return lzbStatus;
    }

    public void setLzbStatus(Integer lzbStatus) {
        this.lzbStatus = lzbStatus;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }


    /**-------------------------------------------------------------------扩展字段---------------------------------------------------------------**/
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getCarAge() {
        return carAge;
    }

    public void setCarAge(String carAge) {
        this.carAge = carAge;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }


    public String getPurchaseDateBegin() {
        return purchaseDateBegin;
    }

    public void setPurchaseDateBegin(String purchaseDateBegin) {
        this.purchaseDateBegin = purchaseDateBegin;
    }

    public String getPurchaseDateEnd() {
        return purchaseDateEnd;
    }

    public void setPurchaseDateEnd(String purchaseDateEnd) {
        this.purchaseDateEnd = purchaseDateEnd;
    }

    public String getNextMaintenanceDateBegin() {
        return nextMaintenanceDateBegin;
    }

    public void setNextMaintenanceDateBegin(String nextMaintenanceDateBegin) {
        this.nextMaintenanceDateBegin = nextMaintenanceDateBegin;
    }

    public String getNextMaintenanceDateEnd() {
        return nextMaintenanceDateEnd;
    }

    public void setNextMaintenanceDateEnd(String nextMaintenanceDateEnd) {
        this.nextMaintenanceDateEnd = nextMaintenanceDateEnd;
    }

    public String getNextSecurityDateBegin() {
        return nextSecurityDateBegin;
    }

    public void setNextSecurityDateBegin(String nextSecurityDateBegin) {
        this.nextSecurityDateBegin = nextSecurityDateBegin;
    }

    public String getNextSecurityDateEnd() {
        return nextSecurityDateEnd;
    }

    public void setNextSecurityDateEnd(String nextSecurityDateEnd) {
        this.nextSecurityDateEnd = nextSecurityDateEnd;
    }

    public String getNextOperationDateBegin() {
        return nextOperationDateBegin;
    }

    public void setNextOperationDateBegin(String nextOperationDateBegin) {
        this.nextOperationDateBegin = nextOperationDateBegin;
    }

    public String getNextOperationDateEnd() {
        return nextOperationDateEnd;
    }

    public void setNextOperationDateEnd(String nextOperationDateEnd) {
        this.nextOperationDateEnd = nextOperationDateEnd;
    }

    public String getNextClassDateBegin() {
        return nextClassDateBegin;
    }

    public void setNextClassDateBegin(String nextClassDateBegin) {
        this.nextClassDateBegin = nextClassDateBegin;
    }

    public String getNextClassDateEnd() {
        return nextClassDateEnd;
    }

    public void setNextClassDateEnd(String nextClassDateEnd) {
        this.nextClassDateEnd = nextClassDateEnd;
    }

    public String getCarryPassengers() {
        return carryPassengers;
    }

    public void setCarryPassengers(String carryPassengers) {
        this.carryPassengers = carryPassengers;
    }

    public String getClicensePlatesColor() {
        return clicensePlatesColor;
    }

    public void setClicensePlatesColor(String clicensePlatesColor) {
        this.clicensePlatesColor = clicensePlatesColor;
    }

    public String getVehicleVINCode() {
        return vehicleVINCode;
    }

    public void setVehicleVINCode(String vehicleVINCode) {
        this.vehicleVINCode = vehicleVINCode;
    }

    public Integer getFuelType() {
        return fuelType;
    }

    public void setFuelType(Integer fuelType) {
        this.fuelType = fuelType;
    }

    public String getVehicleEngineDisplacement() {
        return vehicleEngineDisplacement;
    }

    public void setVehicleEngineDisplacement(String vehicleEngineDisplacement) {
        this.vehicleEngineDisplacement = vehicleEngineDisplacement;
    }

    public String getTransportNumber() {
        return transportNumber;
    }

    public void setTransportNumber(String transportNumber) {
        this.transportNumber = transportNumber;
    }

    public String getCertificationAuthority() {
        return certificationAuthority;
    }

    public void setCertificationAuthority(String certificationAuthority) {
        this.certificationAuthority = certificationAuthority;
    }

    public String getOperatingRegion() {
        return operatingRegion;
    }

    public void setOperatingRegion(String operatingRegion) {
        this.operatingRegion = operatingRegion;
    }

    public String getTransportNumberDateStart() {
        return transportNumberDateStart;
    }

    public void setTransportNumberDateStart(String transportNumberDateStart) {
        this.transportNumberDateStart = transportNumberDateStart;
    }

    public String getTransportNumberDateEnd() {
        return transportNumberDateEnd;
    }

    public void setTransportNumberDateEnd(String transportNumberDateEnd) {
        this.transportNumberDateEnd = transportNumberDateEnd;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public Integer getOverHaulStatus() {
        return overHaulStatus;
    }

    public void setOverHaulStatus(Integer overHaulStatus) {
        this.overHaulStatus = overHaulStatus;
    }

    public String getEquipmentNumber() {
        return equipmentNumber;
    }

    public void setEquipmentNumber(String equipmentNumber) {
        this.equipmentNumber = equipmentNumber;
    }

    public String getGpsBrand() {
        return gpsBrand;
    }

    public void setGpsBrand(String gpsBrand) {
        this.gpsBrand = gpsBrand;
    }

    public String getGpsType() {
        return gpsType;
    }

    public void setGpsType(String gpsType) {
        this.gpsType = gpsType;
    }

    public String getGpsDate() {
        return gpsDate;
    }

    public void setGpsDate(String gpsDate) {
        this.gpsDate = gpsDate;
    }

    public int getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(int cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getCooperationName() {
        return cooperationName;
    }

    public void setCooperationName(String cooperationName) {
        this.cooperationName = cooperationName;
    }

    public String getPurchaseDateString() {
        return purchaseDateString;
    }

    public void setPurchaseDateString(String purchaseDateString) {
        this.purchaseDateString = purchaseDateString;
    }

    public String getRentalExpireDateString() {
        return rentalExpireDateString;
    }

    public void setRentalExpireDateString(String rentalExpireDateString) {
        this.rentalExpireDateString = rentalExpireDateString;
    }

    public String getNextInspectDateString() {
        return nextInspectDateString;
    }

    public void setNextInspectDateString(String nextInspectDateString) {
        this.nextInspectDateString = nextInspectDateString;
    }

    public String getNextMaintenanceDateString() {
        return nextMaintenanceDateString;
    }

    public void setNextMaintenanceDateString(String nextMaintenanceDateString) {
        this.nextMaintenanceDateString = nextMaintenanceDateString;
    }
}