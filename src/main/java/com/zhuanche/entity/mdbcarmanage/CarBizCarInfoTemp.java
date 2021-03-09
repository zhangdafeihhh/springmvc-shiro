package com.zhuanche.entity.mdbcarmanage;

import com.zhuanche.util.AliyunImgUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class CarBizCarInfoTemp {
    private Integer carId;

    private Integer supplierId;

    private String licensePlates;

    private String brand;

    private Integer carModelId;

    private String carModelIds;

    private String modelDetail;

    private String color;

    private String engineNo;

    private String frameNo;

    private String nextInspectDate;

    private String nextMaintenanceDate;

    private String rentalExpireDate;

    private String memo;

    private Integer status;

    private Integer isFree;

    private Integer createBy;

    private Integer updateBy;

    private String createDate;

    private String updateDate;

    private String imageUrl;// 车辆图片

    // 关联 司机的id
    private Integer driverid;

    // 下次运营证检验时间
    private String nextOperationDate;

    // 下次治安证检测时间
    private String nextSecurityDate;

    // 下次等级验证时间
    private String nextClassDate;

    // 二级维护时间
    private String twoLevelMaintenanceDate;

    private Integer cityId;

    private String cityName;

    private String modeName;

    private String supplierName;

    private String purchaseDate;

    private String carAge;

    private String timeOfnextInspect;

    private String timeOfnextMaintenance;

    private String driverName;

    private String carPhotographName;

    private String createName;

    private String updateName;

    private String groupName; // 车型名称

    private Integer groupId; // 车型Id

    private String purchaseDateString;//购车时间的

    private String rentalExpireDateString;//租赁时间的

    private String nextInspectDateString;//下次车检时间的

    private String nextMaintenanceDateString;//下次维保时间的

    //原有车牌号
    private String oldLicensePlates;

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

    // 权限
    private String supplierIds;

    private String cities;

    private String teamIds;

    private String fileName;//批量导入上传文件名称

    private int inspectFlag;

    private int MaintenanceFlag;

    private String createDateBegin;

    private String createDateEnd;

    //核定载客位
    private String carryPassengers;

    //车辆厂牌
    private String vehicleBrand;

    //车牌颜色
    private String clicensePlatesColor;

    //车辆VIN码
    private String vehicleVINCode;

    //车辆注册日期
    private String vehicleRegistrationDate;

    //车辆燃料类型
    private Integer fuelType;

    //车辆发动机排量
    private String vehicleEngineDisplacement;

    //发动机功率
    private String vehicleEnginePower;

    //车辆轴距
    private String vehicleEngineWheelbase;

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

    //年度审验状态
    private Integer auditingStatus;

    //车辆年度审验日期
    private String auditingDate;

    //发票打印设备序列号
    private String equipmentNumber;

    //卫星定位装置品牌
    private String gpsBrand;

    //卫星定位装置型号
    private String gpsType;

    //卫星定位装置IMEI号
    private String gpsImei;

    //卫星定位装置安装日期
    private String gpsDate;

    //所属车主
    private String vehicleOwner;

    //车辆类型（以机动车行驶证为主）
    private String vehicleType;

    //车辆行驶证扫描件
    private String vehicleDrivingLicense;

    //车辆更改供应商，需要把关联司机从原有供应商更改
    private Integer oldSupplierId;

    private Integer oldCity;

    private String carIds;

    private int cooperationType;

    private String cooperationName;

    private Integer driverId;

    private Date carPurchaseDate;

    private String carPhotograph;

    private String insurancecompany;

    private String insurancenumber;

    private String insurancetype;

    private String insurancemoney;

    private String insurancedatestart;

    private String insurancedateend;

    private String freighttype;

    private String totalmileage;

    private String vehicleVinCode;

    private String vehicletec;

    private String vehiclesafe;

    private Integer taxiInvoicePrint;


    private Long newBrandId = null;
    private String newBrandName = null;

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
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
        this.licensePlates = licensePlates;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(Integer carModelId) {
        this.carModelId = carModelId;
    }

    public String getCarModelIds() {
        return carModelIds;
    }

    public void setCarModelIds(String carModelIds) {
        this.carModelIds = carModelIds;
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

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(String frameNo) {
        this.frameNo = frameNo;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDriverid() {
        return driverid;
    }

    public void setDriverid(Integer driverid) {
        this.driverid = driverid;
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

    public String getTimeOfnextInspect() {
        return timeOfnextInspect;
    }

    public void setTimeOfnextInspect(String timeOfnextInspect) {
        this.timeOfnextInspect = timeOfnextInspect;
    }

    public String getTimeOfnextMaintenance() {
        return timeOfnextMaintenance;
    }

    public void setTimeOfnextMaintenance(String timeOfnextMaintenance) {
        this.timeOfnextMaintenance = timeOfnextMaintenance;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarPhotographName() {
        return carPhotographName;
    }

    public void setCarPhotographName(String carPhotographName) {
        this.carPhotographName = carPhotographName;
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

    public String getOldLicensePlates() {
        return oldLicensePlates;
    }

    public void setOldLicensePlates(String oldLicensePlates) {
        this.oldLicensePlates = oldLicensePlates;
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

    public String getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(String supplierIds) {
        this.supplierIds = supplierIds;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(String teamIds) {
        this.teamIds = teamIds;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getInspectFlag() {
        return inspectFlag;
    }

    public void setInspectFlag(int inspectFlag) {
        this.inspectFlag = inspectFlag;
    }

    public int getMaintenanceFlag() {
        return MaintenanceFlag;
    }

    public void setMaintenanceFlag(int maintenanceFlag) {
        MaintenanceFlag = maintenanceFlag;
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

    public String getCarryPassengers() {
        return carryPassengers;
    }

    public void setCarryPassengers(String carryPassengers) {
        this.carryPassengers = carryPassengers;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
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

    public String getVehicleRegistrationDate() {
        return vehicleRegistrationDate;
    }

    public void setVehicleRegistrationDate(String vehicleRegistrationDate) {
        this.vehicleRegistrationDate = vehicleRegistrationDate;
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

    public String getVehicleEnginePower() {
        return vehicleEnginePower;
    }

    public void setVehicleEnginePower(String vehicleEnginePower) {
        this.vehicleEnginePower = vehicleEnginePower;
    }

    public String getVehicleEngineWheelbase() {
        return vehicleEngineWheelbase;
    }

    public void setVehicleEngineWheelbase(String vehicleEngineWheelbase) {
        this.vehicleEngineWheelbase = vehicleEngineWheelbase;
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

    public Integer getAuditingStatus() {
        return auditingStatus;
    }

    public void setAuditingStatus(Integer auditingStatus) {
        this.auditingStatus = auditingStatus;
    }

    public String getAuditingDate() {
        return auditingDate;
    }

    public void setAuditingDate(String auditingDate) {
        this.auditingDate = auditingDate;
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

    public String getGpsImei() {
        return gpsImei;
    }

    public void setGpsImei(String gpsImei) {
        this.gpsImei = gpsImei;
    }

    public String getGpsDate() {
        return gpsDate;
    }

    public void setGpsDate(String gpsDate) {
        this.gpsDate = gpsDate;
    }

    public String getVehicleOwner() {
        return vehicleOwner;
    }

    public void setVehicleOwner(String vehicleOwner) {
        this.vehicleOwner = vehicleOwner;
    }

    public String getVehicleType() {
        return AliyunImgUtils.transUrl(vehicleType);
    }

    public void setVehicleType(String vehicleType) {

        this.vehicleType = vehicleType;
    }

    public String getVehicleDrivingLicense() {
        return vehicleDrivingLicense;
    }

    public void setVehicleDrivingLicense(String vehicleDrivingLicense) {
        this.vehicleDrivingLicense = vehicleDrivingLicense;
    }

    public Integer getOldSupplierId() {
        return oldSupplierId;
    }

    public void setOldSupplierId(Integer oldSupplierId) {
        this.oldSupplierId = oldSupplierId;
    }

    public Integer getOldCity() {
        return oldCity;
    }

    public void setOldCity(Integer oldCity) {
        this.oldCity = oldCity;
    }

    public String getCarIds() {
        return carIds;
    }

    public void setCarIds(String carIds) {
        this.carIds = carIds;
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

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Date getCarPurchaseDate() {
        return carPurchaseDate;
    }

    public void setCarPurchaseDate(Date carPurchaseDate) {
        this.carPurchaseDate = carPurchaseDate;
    }

    public String getCarPhotograph() {
        return carPhotograph;
    }

    public void setCarPhotograph(String carPhotograph) {
        this.carPhotograph = carPhotograph;
    }

    public String getInsurancecompany() {
        return insurancecompany;
    }

    public void setInsurancecompany(String insurancecompany) {
        this.insurancecompany = insurancecompany;
    }

    public String getInsurancenumber() {
        return insurancenumber;
    }

    public void setInsurancenumber(String insurancenumber) {
        this.insurancenumber = insurancenumber;
    }

    public String getInsurancetype() {
        return insurancetype;
    }

    public void setInsurancetype(String insurancetype) {
        this.insurancetype = insurancetype;
    }

    public String getInsurancemoney() {
        return insurancemoney;
    }

    public void setInsurancemoney(String insurancemoney) {
        this.insurancemoney = insurancemoney;
    }

    public String getInsurancedatestart() {
        return insurancedatestart;
    }

    public void setInsurancedatestart(String insurancedatestart) {
        this.insurancedatestart = insurancedatestart;
    }

    public String getInsurancedateend() {
        return insurancedateend;
    }

    public void setInsurancedateend(String insurancedateend) {
        this.insurancedateend = insurancedateend;
    }

    public String getFreighttype() {
        return freighttype;
    }

    public void setFreighttype(String freighttype) {
        this.freighttype = freighttype;
    }

    public String getTotalmileage() {
        return totalmileage;
    }

    public void setTotalmileage(String totalmileage) {
        this.totalmileage = totalmileage;
    }

    public String getVehicleVinCode() {
        return vehicleVinCode;
    }

    public void setVehicleVinCode(String vehicleVinCode) {
        this.vehicleVinCode = vehicleVinCode;
    }

    public String getVehicletec() {
        return vehicletec;
    }

    public void setVehicletec(String vehicletec) {
        this.vehicletec = vehicletec;
    }

    public String getVehiclesafe() {
        return vehiclesafe;
    }

    public void setVehiclesafe(String vehiclesafe) {
        this.vehiclesafe = vehiclesafe;
    }

    public Integer getTaxiInvoicePrint() {
        return taxiInvoicePrint;
    }

    public void setTaxiInvoicePrint(Integer taxiInvoicePrint) {
        this.taxiInvoicePrint = taxiInvoicePrint;
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
}