package com.zhuanche.entity.rentcar;


public class CarBizCarInfo{
	private static final long serialVersionUID = 5454155825314635342L;

	public static final String FORMAT_CAR_PURCHASE_DATE = "yyyy-MM-dd";
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

	//车辆图片
	private String imageUrl;

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

    //车型名称
	private String groupName;

    //车型Id
	private Integer groupId;

	//购车时间的String
	private String purchaseDateString;

    ////租赁时间
	private String rentalExpireDateString;

    //下次车检时间的
	private String nextInspectDateString;

    //下次维保时间的
	private String nextMaintenanceDateString;

	//原有车牌号
	private String licensePlates1;

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

    //加盟类型  car_biz_cooperation_type
	private int cooperationType;

	//加盟类型
	private String cooperationName;

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

	public String getCarIds() {
		return carIds;
	}

	public void setCarIds(String carIds) {
		this.carIds = carIds;
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

	public String getVehicleDrivingLicense() {
		return vehicleDrivingLicense;
	}

	public void setVehicleDrivingLicense(String vehicleDrivingLicense) {
		this.vehicleDrivingLicense = vehicleDrivingLicense;
	}

	public String getVehicleOwner() {
		return vehicleOwner;
	}

	public void setVehicleOwner(String vehicleOwner) {
		this.vehicleOwner = vehicleOwner;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
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

	public String getCarModelIds() {
		return carModelIds;
	}

	public void setCarModelIds(String carModelIds) {
		this.carModelIds = carModelIds;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public Integer getIsFree() {
		return isFree;
	}

	public void setIsFree(Integer isFree) {
		this.isFree = isFree;
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


	public String getCarAge() {
		return carAge;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
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


	public String getLicensePlates1() {
		return licensePlates1;
	}

	public void setLicensePlates1(String licensePlates1) {
		this.licensePlates1 = licensePlates1;
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

	public Integer getDriverid() {
		return driverid;
	}

	public void setDriverid(Integer driverid) {
		this.driverid = driverid;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public CarBizCarInfo() {
	}

	public CarBizCarInfo(Integer carId) {
		this.carId = carId;
	}

	public void setCarId(Integer value) {
		this.carId = value;
	}

	public Integer getCarId() {
		return this.carId;
	}

	public void setSupplierId(Integer value) {
		this.supplierId = value;
	}

	public Integer getSupplierId() {
		return this.supplierId;
	}

	public void setLicensePlates(String value) {
		this.licensePlates = value;
	}

	public String getLicensePlates() {
		return this.licensePlates;
	}

	public void setBrand(String value) {
		this.brand = value;
	}

	public String getBrand() {
		return this.brand;
	}

	public void setCarModelId(Integer value) {
		this.carModelId = value;
	}

	public Integer getCarModelId() {
		return this.carModelId;
	}

	public void setModelDetail(String value) {
		this.modelDetail = value;
	}

	public String getModelDetail() {
		return this.modelDetail;
	}

	public void setColor(String value) {
		this.color = value;
	}

	public String getColor() {
		return this.color;
	}

	public void setEngineNo(String value) {
		this.engineNo = value;
	}

	public String getEngineNo() {
		return this.engineNo;
	}

	public void setFrameNo(String value) {
		this.frameNo = value;
	}

	public String getFrameNo() {
		return this.frameNo;
	}

	public void setMemo(String value) {
		this.memo = value;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setStatus(Integer value) {
		this.status = value;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setCreateBy(Integer value) {
		this.createBy = value;
	}

	public Integer getCreateBy() {
		return this.createBy;
	}

	public void setUpdateBy(Integer value) {
		this.updateBy = value;
	}

	public Integer getUpdateBy() {
		return this.updateBy;
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
}

