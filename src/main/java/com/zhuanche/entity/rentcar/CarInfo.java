package com.zhuanche.entity.rentcar;

import com.zhuanche.entity.common.BaseEntity;
import com.zhuanche.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarInfo extends BaseEntity {

    // alias
    public static final String TABLE_ALIAS = "CarBizCarInfo";
    public static final String ALIAS_CAR_ID = "carId";
    public static final String ALIAS_SUPPLIER_ID = "supplierId";
    public static final String ALIAS_LICENSE_PLATES = "车牌号";
    public static final String ALIAS_BRAND = "品牌";
    public static final String ALIAS_CAR_MODEL_ID = "车型 关联车型表";
    public static final String ALIAS_MODEL_DETAIL = "具体车型";
    public static final String ALIAS_COLOR = "color";
    public static final String ALIAS_ENGINE_NO = "发动机编号";
    public static final String ALIAS_FRAME_NO = "车架号";
    public static final String ALIAS_NEXT_INSPECT_DATE = "下次检车时间";
    public static final String ALIAS_NEXT_MAINTENANCE_DATE = "下次维保时间";
    public static final String ALIAS_RENTAL_EXPIRE_DATE = "租赁到期时间";
    public static final String ALIAS_MEMO = "memo";
    public static final String ALIAS_STATUS = "status";
    public static final String ALIAS_CREATE_BY = "createBy";
    public static final String ALIAS_UPDATE_BY = "updateBy";
    public static final String ALIAS_CREATE_DATE = "createDate";
    public static final String ALIAS_UPDATE_DATE = "updateDate";

    // date formats
    public static final String FORMAT_NEXT_INSPECT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_NEXT_MAINTENANCE_DATE = "yyyy-MM-dd";
    public static final String FORMAT_RENTAL_EXPIRE_DATE = "yyyy-MM-dd";
    public static final String FORMAT_CREATE_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_UPDATE_DATE = "yyyy-MM-dd HH:mm:ss";

    // update
    public static final String FORMAT_CAR_PURCHASE_DATE = "yyyy-MM-dd";

    private Integer carId;

    private Integer cityId;

    private Integer driverId;

    private Integer supplierId;

    /**
     * 车牌号
     */
    private String licensePlates;

    private String brand;

    private Integer carModelId;

    private Date carPurchaseDate;

    private String modelDetail;

    private String color;

    private String carPhotographName;

    private String carPhotograph;

    private String engineNo;

    private String frameNo;

    private String nextInspectDate;

    private String nextMaintenanceDate;

    private String rentalExpireDate;

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

    private String vehicleVinCode;

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
    private String timeOfnextInspect;
    private String timeOfnextMaintenance;
    private String driverName;
    private String createName;
    private String updateName;
    private String groupName; // 车型名称
    private Integer groupId; // 车型Id
    //
    private java.lang.String carModelIds;

    @SuppressWarnings("unused")
    private String purchaseDateString;//购车时间的String
    @SuppressWarnings("unused")
    private String rentalExpireDateString;//租赁时间的String
    @SuppressWarnings("unused")
    private String nextInspectDateString;//下次车检时间的String
    @SuppressWarnings("unused")
    private String nextMaintenanceDateString;//下次维保时间的String

    // add by xiao 原有车牌号
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
//    private String nextOperationDateEnd;
    // 下次等级鉴定开始时间
//    private String nextClassDateBegin;
    // 下次等级鉴定结束时间
    private String nextClassDateEnd;

    // 权限
    private String supplierIds;
    private String cities;
    private String teamIds;

    private String fileName;//批量导入上传文件名称

    //update
    private int inspectFlag;
    private int MaintenanceFlag;

    private String createDateBegin;
    private String createDateEnd;


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
    //年度审验状态
    private Integer auditingStatus;
    //发票打印设备序列号
    private String equipmentNumber;
    //卫星定位装置品牌
    private String gpsBrand;
    //卫星定位装置型号
    private String gpsType;
    //卫星定位装置安装日期
    private String gpsDate;

    //update 2017-05-22  车辆更改供应商，需要把关联司机从原有供应商更改
    private Integer oldSupplierId;//

    private Integer oldCity;//

    //update zll 2017-07-11
    private String carIds;

    private int cooperationType;//加盟类型  car_biz_cooperation_type

    private String cooperationName;//加盟类型

    //车辆状态
    private java.lang.Integer isFree;

    /** 车辆图片 **/
    private String imageUrl;

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

    public String getCarModelIds() {
        return carModelIds;
    }

    public void setCarModelIds(String carModelIds) {
        this.carModelIds = carModelIds;
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

    public String getNextClassDateEnd() {
        return nextClassDateEnd;
    }

    public void setNextClassDateEnd(String nextClassDateEnd) {
        this.nextClassDateEnd = nextClassDateEnd;
    }

    @Override
    public String getSupplierIds() {
        return supplierIds;
    }

    @Override
    public void setSupplierIds(String supplierIds) {
        this.supplierIds = supplierIds;
    }

    @Override
    public String getCities() {
        return cities;
    }

    @Override
    public void setCities(String cities) {
        this.cities = cities;
    }

    @Override
    public String getTeamIds() {
        return teamIds;
    }

    @Override
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

    public Integer getAuditingStatus() {
        return auditingStatus;
    }

    public void setAuditingStatus(Integer auditingStatus) {
        this.auditingStatus = auditingStatus;
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

    public String getPurchaseDateString() {
        String str = "";
        if(getPurchaseDate()!=null&&!"".equals(getPurchaseDate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Date date = new Date();
            try {
                date = sdf.parse(getPurchaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            str = DateUtils.formatDate(date,FORMAT_CAR_PURCHASE_DATE);
        }
        return str;
    }

    public void setPurchaseDateString(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        setPurchaseDate(sdf.format(DateUtils.parse(value, FORMAT_CAR_PURCHASE_DATE,
                java.util.Date.class)));
        this.purchaseDateString = value;
    }

    public String getRentalExpireDateString() {
        String str = "";
        if(getRentalExpireDate()!=null&&!"".equals(getRentalExpireDate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Date date = new Date();
            try {
                date = sdf.parse(getRentalExpireDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            str = DateUtils.formatDate(date,FORMAT_CAR_PURCHASE_DATE);
        }
        return str;
    }
    public void setRentalExpireDateString(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        setRentalExpireDate(sdf.format(DateUtils.parse(value, FORMAT_CAR_PURCHASE_DATE,
                java.util.Date.class)));
        this.rentalExpireDateString = value;
    }
    public String getNextInspectDateString() {
        String str = "";
        if(getNextInspectDate()!=null&&!"".equals(getNextInspectDate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Date date = new Date();
            try {
                date = sdf.parse(getNextInspectDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            str = DateUtils.formatDate(date,FORMAT_CAR_PURCHASE_DATE);
        }
        return str;
    }

    public void setNextInspectDateString(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        setNextInspectDate(sdf.format(DateUtils.parse(value, FORMAT_CAR_PURCHASE_DATE,
                java.util.Date.class)));
        this.nextInspectDateString = value;
    }
    public String getNextMaintenanceDateString() {
        String str = "";
        if(getNextMaintenanceDate()!=null&&!"".equals(getNextMaintenanceDate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            Date date = new Date();
            try {
                date = sdf.parse(getNextMaintenanceDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            str = DateUtils.formatDate(date,FORMAT_CAR_PURCHASE_DATE);
        }
        return str;
    }

    public void setNextMaintenanceDateString(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        setNextMaintenanceDate(sdf.format(DateUtils.parse(value, FORMAT_CAR_PURCHASE_DATE,
                java.util.Date.class)));
        this.nextMaintenanceDateString = value;
    }
}