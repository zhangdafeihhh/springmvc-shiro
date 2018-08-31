package com.zhuanche.dto.driver;

import java.io.Serializable;
import java.util.Date;

public class DriverVerifyDto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;

    private Long driverId;

    private String mobile;

    private String name;

    private String idCard;

    private Long cityId;

    private String cityName;

    private Long brandId;

    private String brandName;

    private Long vehicleId;

    private String vehicleName;

    private Integer verifyStatus;

    private String licenseNum;

    private String plateNum;

    private String vehicleType;

    private Integer criminalRecord;

    private String idcardSex;

    private String race;

    private String idcardBirthday;

    private String idcardAddress;

    private String driverLicenseName;

    private String driverLicenseSex;

    private String driverLicenseBirthday;

    private String nationality;

    private String driverLicenseNum;

    private String driverLicenseAddress;

    private String driverLicenseIssueDate;

    private String driverLicenseFromDate;

    private String driverLicenseValidFor;

    private String owner;

    private String useCharacter;

    private String vehicleLicenseAddress;

    private String vehicleLicenseIssueDate;

    private String driverClass;

    private String vehicleLicenseArchivesNum;

    private String engineNum;

    private String totalMass;

    private String inspectionRecord;

    private String passengersNum;

    private String externalDimension;

    private String model;

    private String vin;

    private String carWeight;

    private String vehicleLicenseRegisterDate;

    private String supplier;

    private String supplierName;

    private String serviceType;

    private String presentAddress;

    private String colour;

    private String emergencyContact;

    private String emergencyInformation;

    private String frameNum;

    private String wheelbase;

    private String fuelType;

    private Integer modelId;

    private String displacement;

    private Date createAt;

    private Date dataAuditTime;

    private Date trainTime;

    private Date auditTime;

    private Date updateAt;

    private String driverAgreementCompany;

    private String domicileAddr;

    private Byte carFlag;

    private Byte informStatus;

    private String notJoinReason;

    private String source;
    
    private String serviceTypeName;// 服务类型名称
    
    private String modelIdName;// 车型名称（rentcar:car_biz_model）
    
    public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public String getModelIdName() {
		return modelIdName;
	}

	public void setModelIdName(String modelIdName) {
		this.modelIdName = modelIdName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName == null ? null : vehicleName.trim();
    }

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getLicenseNum() {
        return licenseNum;
    }

    public void setLicenseNum(String licenseNum) {
        this.licenseNum = licenseNum == null ? null : licenseNum.trim();
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum == null ? null : plateNum.trim();
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType == null ? null : vehicleType.trim();
    }

    public Integer getCriminalRecord() {
        return criminalRecord;
    }

    public void setCriminalRecord(Integer criminalRecord) {
        this.criminalRecord = criminalRecord;
    }

    public String getIdcardSex() {
        return idcardSex;
    }

    public void setIdcardSex(String idcardSex) {
        this.idcardSex = idcardSex == null ? null : idcardSex.trim();
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race == null ? null : race.trim();
    }

    public String getIdcardBirthday() {
        return idcardBirthday;
    }

    public void setIdcardBirthday(String idcardBirthday) {
        this.idcardBirthday = idcardBirthday == null ? null : idcardBirthday.trim();
    }

    public String getIdcardAddress() {
        return idcardAddress;
    }

    public void setIdcardAddress(String idcardAddress) {
        this.idcardAddress = idcardAddress == null ? null : idcardAddress.trim();
    }

    public String getDriverLicenseName() {
        return driverLicenseName;
    }

    public void setDriverLicenseName(String driverLicenseName) {
        this.driverLicenseName = driverLicenseName == null ? null : driverLicenseName.trim();
    }

    public String getDriverLicenseSex() {
        return driverLicenseSex;
    }

    public void setDriverLicenseSex(String driverLicenseSex) {
        this.driverLicenseSex = driverLicenseSex == null ? null : driverLicenseSex.trim();
    }

    public String getDriverLicenseBirthday() {
        return driverLicenseBirthday;
    }

    public void setDriverLicenseBirthday(String driverLicenseBirthday) {
        this.driverLicenseBirthday = driverLicenseBirthday == null ? null : driverLicenseBirthday.trim();
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality == null ? null : nationality.trim();
    }

    public String getDriverLicenseNum() {
        return driverLicenseNum;
    }

    public void setDriverLicenseNum(String driverLicenseNum) {
        this.driverLicenseNum = driverLicenseNum == null ? null : driverLicenseNum.trim();
    }

    public String getDriverLicenseAddress() {
        return driverLicenseAddress;
    }

    public void setDriverLicenseAddress(String driverLicenseAddress) {
        this.driverLicenseAddress = driverLicenseAddress == null ? null : driverLicenseAddress.trim();
    }

    public String getDriverLicenseIssueDate() {
        return driverLicenseIssueDate;
    }

    public void setDriverLicenseIssueDate(String driverLicenseIssueDate) {
        this.driverLicenseIssueDate = driverLicenseIssueDate == null ? null : driverLicenseIssueDate.trim();
    }

    public String getDriverLicenseFromDate() {
        return driverLicenseFromDate;
    }

    public void setDriverLicenseFromDate(String driverLicenseFromDate) {
        this.driverLicenseFromDate = driverLicenseFromDate == null ? null : driverLicenseFromDate.trim();
    }

    public String getDriverLicenseValidFor() {
        return driverLicenseValidFor;
    }

    public void setDriverLicenseValidFor(String driverLicenseValidFor) {
        this.driverLicenseValidFor = driverLicenseValidFor == null ? null : driverLicenseValidFor.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getUseCharacter() {
        return useCharacter;
    }

    public void setUseCharacter(String useCharacter) {
        this.useCharacter = useCharacter == null ? null : useCharacter.trim();
    }

    public String getVehicleLicenseAddress() {
        return vehicleLicenseAddress;
    }

    public void setVehicleLicenseAddress(String vehicleLicenseAddress) {
        this.vehicleLicenseAddress = vehicleLicenseAddress == null ? null : vehicleLicenseAddress.trim();
    }

    public String getVehicleLicenseIssueDate() {
        return vehicleLicenseIssueDate;
    }

    public void setVehicleLicenseIssueDate(String vehicleLicenseIssueDate) {
        this.vehicleLicenseIssueDate = vehicleLicenseIssueDate == null ? null : vehicleLicenseIssueDate.trim();
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass == null ? null : driverClass.trim();
    }

    public String getVehicleLicenseArchivesNum() {
        return vehicleLicenseArchivesNum;
    }

    public void setVehicleLicenseArchivesNum(String vehicleLicenseArchivesNum) {
        this.vehicleLicenseArchivesNum = vehicleLicenseArchivesNum == null ? null : vehicleLicenseArchivesNum.trim();
    }

    public String getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(String engineNum) {
        this.engineNum = engineNum == null ? null : engineNum.trim();
    }

    public String getTotalMass() {
        return totalMass;
    }

    public void setTotalMass(String totalMass) {
        this.totalMass = totalMass == null ? null : totalMass.trim();
    }

    public String getInspectionRecord() {
        return inspectionRecord;
    }

    public void setInspectionRecord(String inspectionRecord) {
        this.inspectionRecord = inspectionRecord == null ? null : inspectionRecord.trim();
    }

    public String getPassengersNum() {
        return passengersNum;
    }

    public void setPassengersNum(String passengersNum) {
        this.passengersNum = passengersNum == null ? null : passengersNum.trim();
    }

    public String getExternalDimension() {
        return externalDimension;
    }

    public void setExternalDimension(String externalDimension) {
        this.externalDimension = externalDimension == null ? null : externalDimension.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin == null ? null : vin.trim();
    }

    public String getCarWeight() {
        return carWeight;
    }

    public void setCarWeight(String carWeight) {
        this.carWeight = carWeight == null ? null : carWeight.trim();
    }

    public String getVehicleLicenseRegisterDate() {
        return vehicleLicenseRegisterDate;
    }

    public void setVehicleLicenseRegisterDate(String vehicleLicenseRegisterDate) {
        this.vehicleLicenseRegisterDate = vehicleLicenseRegisterDate == null ? null : vehicleLicenseRegisterDate.trim();
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier == null ? null : supplier.trim();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType == null ? null : serviceType.trim();
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress == null ? null : presentAddress.trim();
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour == null ? null : colour.trim();
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact == null ? null : emergencyContact.trim();
    }

    public String getEmergencyInformation() {
        return emergencyInformation;
    }

    public void setEmergencyInformation(String emergencyInformation) {
        this.emergencyInformation = emergencyInformation == null ? null : emergencyInformation.trim();
    }

    public String getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(String frameNum) {
        this.frameNum = frameNum == null ? null : frameNum.trim();
    }

    public String getWheelbase() {
        return wheelbase;
    }

    public void setWheelbase(String wheelbase) {
        this.wheelbase = wheelbase == null ? null : wheelbase.trim();
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType == null ? null : fuelType.trim();
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement == null ? null : displacement.trim();
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getDataAuditTime() {
        return dataAuditTime;
    }

    public void setDataAuditTime(Date dataAuditTime) {
        this.dataAuditTime = dataAuditTime;
    }

    public Date getTrainTime() {
        return trainTime;
    }

    public void setTrainTime(Date trainTime) {
        this.trainTime = trainTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getDriverAgreementCompany() {
        return driverAgreementCompany;
    }

    public void setDriverAgreementCompany(String driverAgreementCompany) {
        this.driverAgreementCompany = driverAgreementCompany == null ? null : driverAgreementCompany.trim();
    }

    public String getDomicileAddr() {
        return domicileAddr;
    }

    public void setDomicileAddr(String domicileAddr) {
        this.domicileAddr = domicileAddr == null ? null : domicileAddr.trim();
    }

    public Byte getCarFlag() {
        return carFlag;
    }

    public void setCarFlag(Byte carFlag) {
        this.carFlag = carFlag;
    }

    public Byte getInformStatus() {
        return informStatus;
    }

    public void setInformStatus(Byte informStatus) {
        this.informStatus = informStatus;
    }

    public String getNotJoinReason() {
        return notJoinReason;
    }

    public void setNotJoinReason(String notJoinReason) {
        this.notJoinReason = notJoinReason == null ? null : notJoinReason.trim();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }
}