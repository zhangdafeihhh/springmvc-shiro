package com.zhuanche.entity.rentcar;

import com.zhuanche.entity.common.BaseEntity;
import com.zhuanche.util.DateUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarInfo extends BaseEntity {

    private static final long serialVersionUID = 5454155825314635342L;

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

    // 可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    // columns START
    //
    private java.lang.Integer carId;

    //
    private java.lang.Integer supplierId;
    // @Length(max=10)
    private java.lang.String licensePlates;
    // @Length(max=40)
    private java.lang.String brand;
    //
    private java.lang.Integer carModelId;
    private java.lang.String carModelIds;
    // @Length(max=50)
    private java.lang.String modelDetail;
    // @Length(max=10)
    private java.lang.String color;
    // @Length(max=40)
    private java.lang.String engineNo;
    // @Length(max=40)
    private java.lang.String frameNo;
    //
    private String nextInspectDate;
    //
    private String nextMaintenanceDate;
    //
    private String rentalExpireDate;
    // @Length(max=65535)
    private java.lang.String memo;
    //
    private java.lang.Integer status;
    //车辆状态
    private java.lang.Integer isFree;
    //
    private java.lang.Integer createBy;
    //
    private java.lang.Integer updateBy;
    // @NotNull
    private String createDate;
    // @NotNull
    private String updateDate;
    // columns END
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

    //update
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

    //update 2017-05-22  车辆更改供应商，需要把关联司机从原有供应商更改
    private Integer oldSupplierId;//

    private Integer oldCity;//

    //update zll 2017-07-11
    private String carIds;

    private int cooperationType;//加盟类型  car_biz_cooperation_type

    private String cooperationName;//加盟类型

    private String idCardNo;//司机身份证号

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
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

    public java.lang.String getCarModelIds() {
        return carModelIds;
    }

    public void setCarModelIds(java.lang.String carModelIds) {
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

    public java.lang.Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(java.lang.Integer isFree) {
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

    public CarInfo() {
    }

    public CarInfo(java.lang.Integer carId) {
        this.carId = carId;
    }

    public void setCarId(java.lang.Integer value) {
        this.carId = value;
    }

    public java.lang.Integer getCarId() {
        return this.carId;
    }

    public void setSupplierId(java.lang.Integer value) {
        this.supplierId = value;
    }

    public java.lang.Integer getSupplierId() {
        return this.supplierId;
    }

    public void setLicensePlates(java.lang.String value) {
        this.licensePlates = value;
    }

    public java.lang.String getLicensePlates() {
        return this.licensePlates;
    }

    public void setBrand(java.lang.String value) {
        this.brand = value;
    }

    public java.lang.String getBrand() {
        return this.brand;
    }

    public void setCarModelId(java.lang.Integer value) {
        this.carModelId = value;
    }

    public java.lang.Integer getCarModelId() {
        return this.carModelId;
    }

    public void setModelDetail(java.lang.String value) {
        this.modelDetail = value;
    }

    public java.lang.String getModelDetail() {
        return this.modelDetail;
    }

    public void setColor(java.lang.String value) {
        this.color = value;
    }

    public java.lang.String getColor() {
        return this.color;
    }

    public void setEngineNo(java.lang.String value) {
        this.engineNo = value;
    }

    public java.lang.String getEngineNo() {
        return this.engineNo;
    }

    public void setFrameNo(java.lang.String value) {
        this.frameNo = value;
    }

    public java.lang.String getFrameNo() {
        return this.frameNo;
    }

    public void setMemo(java.lang.String value) {
        this.memo = value;
    }

    public java.lang.String getMemo() {
        return this.memo;
    }

    public void setStatus(java.lang.Integer value) {
        this.status = value;
    }

    public java.lang.Integer getStatus() {
        return this.status;
    }

    public void setCreateBy(java.lang.Integer value) {
        this.createBy = value;
    }

    public java.lang.Integer getCreateBy() {
        return this.createBy;
    }

    public void setUpdateBy(java.lang.Integer value) {
        this.updateBy = value;
    }

    public java.lang.Integer getUpdateBy() {
        return this.updateBy;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getCarId()).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof CarInfo == false)
            return false;
        if (this == obj)
            return true;
        CarInfo other = (CarInfo) obj;
        return new EqualsBuilder().append(getCarId(), other.getCarId())
                .isEquals();
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