package com.zhuanche.entity.busManage;

import java.util.Date;

/**
 * @program: mp-manage
 * @description: car_biz_car_info表对应的实体类，专用于巴士业务
 * @author: niuzilian
 * @create: 2018-11-26 15:48
 **/
public class BusCarInfo {
    private Integer carId;

    private Integer cityId;

    private Integer driverId;

    private Integer supplierId;

    private String licensePlates;

    private String brand;

    private Integer carModelId;

    private Integer groupId;

    private Date carPurchaseDate;

    private String modelDetail;

    private String color;

    private String carPhotographName;

    private String carPhotograph;

    private String engineNo;

    private String frameNo;

    private Date nextInspectDate;

    private Date nextMaintenanceDate;

    private Date rentalExpireDate;

    private Date nextOperationDate;

    private Date nextSecurityDate;

    private Date nextClassDate;

    private Date twoLevelMaintenanceDate;

    private Integer status;

    private Integer createBy;

    private Integer updateBy;

    private Date createDate;

    private Date updateDate;

    private String gpsdate;

    private String clicenseplatescolor;

    private String fueltype;

    private String carrypassengers;

    private Integer auditingstatus;

    private String insurancecompany;

    private String insurancenumber;

    private String insurancetype;

    private String insurancemoney;

    private String insurancedatestart;

    private String insurancedateend;

    private String freighttype;

    private String vehicleenginedisplacement;

    private String totalmileage;

    private Integer overhaulstatus;

    private String transportnumber;

    private String certificationauthority;

    private String operatingregion;

    private String firstdate;

    private String transportnumberdatestart;

    private String transportnumberdateend;

    private String equipmentnumber;

    private String gpsbrand;

    private String gpstype;

    private String auditingDate;

    private String vehicleEnginePower;

    private String vehicleEngineWheelbase;

    private String vehicleRegistrationDate;

    private String vehicleVinCode;

    private String vehicleBrand;

    private String gpsImei;

    private String vehicleOwner;

    private String vehicleType;

    private String vehicleDrivingLicense;

    private String vehicletec;

    private String vehiclesafe;

    private Integer lzbStatus;

    private String memo;

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

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
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

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public String getCarPhotograph() {
        return carPhotograph;
    }

    public void setCarPhotograph(String carPhotograph) {
        this.carPhotograph = carPhotograph == null ? null : carPhotograph.trim();
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

    public Date getNextInspectDate() {
        return nextInspectDate;
    }

    public void setNextInspectDate(Date nextInspectDate) {
        this.nextInspectDate = nextInspectDate;
    }

    public Date getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }

    public void setNextMaintenanceDate(Date nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public Date getRentalExpireDate() {
        return rentalExpireDate;
    }

    public void setRentalExpireDate(Date rentalExpireDate) {
        this.rentalExpireDate = rentalExpireDate;
    }

    public Date getNextOperationDate() {
        return nextOperationDate;
    }

    public void setNextOperationDate(Date nextOperationDate) {
        this.nextOperationDate = nextOperationDate;
    }

    public Date getNextSecurityDate() {
        return nextSecurityDate;
    }

    public void setNextSecurityDate(Date nextSecurityDate) {
        this.nextSecurityDate = nextSecurityDate;
    }

    public Date getNextClassDate() {
        return nextClassDate;
    }

    public void setNextClassDate(Date nextClassDate) {
        this.nextClassDate = nextClassDate;
    }

    public Date getTwoLevelMaintenanceDate() {
        return twoLevelMaintenanceDate;
    }

    public void setTwoLevelMaintenanceDate(Date twoLevelMaintenanceDate) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getGpsdate() {
        return gpsdate;
    }

    public void setGpsdate(String gpsdate) {
        this.gpsdate = gpsdate == null ? null : gpsdate.trim();
    }

    public String getClicenseplatescolor() {
        return clicenseplatescolor;
    }

    public void setClicenseplatescolor(String clicenseplatescolor) {
        this.clicenseplatescolor = clicenseplatescolor == null ? null : clicenseplatescolor.trim();
    }

    public String getFueltype() {
        return fueltype;
    }

    public void setFueltype(String fueltype) {
        this.fueltype = fueltype == null ? null : fueltype.trim();
    }

    public String getCarrypassengers() {
        return carrypassengers;
    }

    public void setCarrypassengers(String carrypassengers) {
        this.carrypassengers = carrypassengers == null ? null : carrypassengers.trim();
    }

    public Integer getAuditingstatus() {
        return auditingstatus;
    }

    public void setAuditingstatus(Integer auditingstatus) {
        this.auditingstatus = auditingstatus;
    }

    public String getInsurancecompany() {
        return insurancecompany;
    }

    public void setInsurancecompany(String insurancecompany) {
        this.insurancecompany = insurancecompany == null ? null : insurancecompany.trim();
    }

    public String getInsurancenumber() {
        return insurancenumber;
    }

    public void setInsurancenumber(String insurancenumber) {
        this.insurancenumber = insurancenumber == null ? null : insurancenumber.trim();
    }

    public String getInsurancetype() {
        return insurancetype;
    }

    public void setInsurancetype(String insurancetype) {
        this.insurancetype = insurancetype == null ? null : insurancetype.trim();
    }

    public String getInsurancemoney() {
        return insurancemoney;
    }

    public void setInsurancemoney(String insurancemoney) {
        this.insurancemoney = insurancemoney == null ? null : insurancemoney.trim();
    }

    public String getInsurancedatestart() {
        return insurancedatestart;
    }

    public void setInsurancedatestart(String insurancedatestart) {
        this.insurancedatestart = insurancedatestart == null ? null : insurancedatestart.trim();
    }

    public String getInsurancedateend() {
        return insurancedateend;
    }

    public void setInsurancedateend(String insurancedateend) {
        this.insurancedateend = insurancedateend == null ? null : insurancedateend.trim();
    }

    public String getFreighttype() {
        return freighttype;
    }

    public void setFreighttype(String freighttype) {
        this.freighttype = freighttype == null ? null : freighttype.trim();
    }

    public String getVehicleenginedisplacement() {
        return vehicleenginedisplacement;
    }

    public void setVehicleenginedisplacement(String vehicleenginedisplacement) {
        this.vehicleenginedisplacement = vehicleenginedisplacement == null ? null : vehicleenginedisplacement.trim();
    }

    public String getTotalmileage() {
        return totalmileage;
    }

    public void setTotalmileage(String totalmileage) {
        this.totalmileage = totalmileage == null ? null : totalmileage.trim();
    }

    public Integer getOverhaulstatus() {
        return overhaulstatus;
    }

    public void setOverhaulstatus(Integer overhaulstatus) {
        this.overhaulstatus = overhaulstatus;
    }

    public String getTransportnumber() {
        return transportnumber;
    }

    public void setTransportnumber(String transportnumber) {
        this.transportnumber = transportnumber == null ? null : transportnumber.trim();
    }

    public String getCertificationauthority() {
        return certificationauthority;
    }

    public void setCertificationauthority(String certificationauthority) {
        this.certificationauthority = certificationauthority == null ? null : certificationauthority.trim();
    }

    public String getOperatingregion() {
        return operatingregion;
    }

    public void setOperatingregion(String operatingregion) {
        this.operatingregion = operatingregion == null ? null : operatingregion.trim();
    }

    public String getFirstdate() {
        return firstdate;
    }

    public void setFirstdate(String firstdate) {
        this.firstdate = firstdate == null ? null : firstdate.trim();
    }

    public String getTransportnumberdatestart() {
        return transportnumberdatestart;
    }

    public void setTransportnumberdatestart(String transportnumberdatestart) {
        this.transportnumberdatestart = transportnumberdatestart == null ? null : transportnumberdatestart.trim();
    }

    public String getTransportnumberdateend() {
        return transportnumberdateend;
    }

    public void setTransportnumberdateend(String transportnumberdateend) {
        this.transportnumberdateend = transportnumberdateend == null ? null : transportnumberdateend.trim();
    }

    public String getEquipmentnumber() {
        return equipmentnumber;
    }

    public void setEquipmentnumber(String equipmentnumber) {
        this.equipmentnumber = equipmentnumber == null ? null : equipmentnumber.trim();
    }

    public String getGpsbrand() {
        return gpsbrand;
    }

    public void setGpsbrand(String gpsbrand) {
        this.gpsbrand = gpsbrand == null ? null : gpsbrand.trim();
    }

    public String getGpstype() {
        return gpstype;
    }

    public void setGpstype(String gpstype) {
        this.gpstype = gpstype == null ? null : gpstype.trim();
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

    public String getVehicleVinCode() {
        return vehicleVinCode;
    }

    public void setVehicleVinCode(String vehicleVinCode) {
        this.vehicleVinCode = vehicleVinCode == null ? null : vehicleVinCode.trim();
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
}
