package com.zhuanche.mongo;

import java.io.Serializable;
import java.util.Date;

public class DriverMongo implements Serializable {

    private static final long serialVersionUID = -8966813548462594294L;

    //司机GPS坐标数据格式存储 {116.1312354 , 89.354658}
    private Double loc[];
    //司机GPS坐标数据格式存储 {116.1312354 , 89.354658}
    private Double baidu_loc[]; //百度坐标添加

    //方位角
    private Double bearing;

    public Double speed;//瞬时速度
    public String zipcode;//行政区编号
    public Double altitude;//海拔高度
    public String passwordCode;//加密标识

    // 用于推送消息
    private String pushId;

    // 此部分为扩展消息
    // 司机心跳时间（随心跳更新）(update by cuiw 2015/7/27)
    private String heartDate;
    // 司机当前GPS位置（心跳请求）
    private String currentGPSPoint;
    // 司机当前GPS位置（心跳请求）
    private String baidu_currentGPSPoint;
    // 司机一天内心跳次数
    private Integer count;
    // 司机一天内高峰心跳次数
    private Integer hotCount;
    // 车组ID
    private Integer groupId;
    // 车组名称
    private String groupName;
    // 车型ID
    private Integer modelId;
    // 车组名称
    private String modelName;
    // 服务城市
    private String serviceCityName;
    // 距离上车地点的距离
    private Double distince;
    // 距离上车地点的距离（米）
    private String distinceMi;
    // 性别
    private String sex;
    // 今日服务订单数
    private long servicecount;
    // 生成的订单号
    private String orderNo;
    // 服务监督吗
    private String superintendNo = "";
    // 服务监督吗 链接
    private String superintendUrl = "";

    /**
     * ADD bookingDate chenlei 2015-7-30 为计算司机下单服务时间而增加，如发现对之前功能有影响请声明
     */
    private String bookingDate;
    private String distinceStr;

    // 预订时间字符串版本
    private String bookingDateStr;

    // add by lwl 司机是否接受抢单 2015-08-24
    private Integer grabNotice;

    // 当前登陆版本号
    private String appVersion;

    // 回家模式地址 add cl
    private String goHomeAddress;
    // 回家模式地址纬度 add cl
    private String goHomePointLa;
    // 回家模式地址经度 add cl
    private String goHomePointLo;
    // 回家模式地址纬度 add cl
    private String goHomePointLaBD;
    // 回家模式地址经度 add cl
    private String goHomePointLoBD;
    // 回家模式状态开启 1开启；0关闭 add cl
    private Integer goHomeStatus;

    //回家模式修改时间
    private String updateGoHomeDate;

    // 司机回家成功标记（运行时的临时值，mongo里不存储）
    private Integer goHomeFlag;

    //司机在线时长 小时数 （班制内在线时长+加班时长)
    private Float driverDutyTotalHour;

    //update 2017-04-21 zll 加入加盟类型
    private Integer cooperationType;//加盟类型

    //update 2017-05-03 zll 停运状态
    private Integer outageStatus;//停运状态  1停运 2正常
    private Integer outageSource;//停运来源:1系统停运 2人工停运
    private String outStartDateStr;//开始停运时间
    private double outStopLongTime;//停运时长(h)
    private double outStopLongTimeSurplus;//停运剩余时长(min)
    private String outageReason;//停运原因
    private Double outageSuodingTime;//司机在停运状态下，锁定时长

    private Integer carAlarmType; // 车辆报警类型
    private String carAlarmName; // 车辆报警名称

    private Integer isTwoShifts; //是否是双班司机  0/空值:单班   1:双班

    public Integer getIsTwoShifts() {
        return isTwoShifts;
    }

    public void setIsTwoShifts(Integer isTwoShifts) {
        this.isTwoShifts = isTwoShifts;
    }

    public Integer getCarAlarmType() {
        return carAlarmType;
    }

    public void setCarAlarmType(Integer carAlarmType) {
        this.carAlarmType = carAlarmType;
    }

    public String getCarAlarmName() {
        return carAlarmName;
    }

    public void setCarAlarmName(String carAlarmName) {
        this.carAlarmName = carAlarmName;
    }

    public Double getOutageSuodingTime() {
        return outageSuodingTime;
    }

    public void setOutageSuodingTime(Double outageSuodingTime) {
        this.outageSuodingTime = outageSuodingTime;
    }

    public String getOutageReason() {
        return outageReason;
    }

    public void setOutageReason(String outageReason) {
        this.outageReason = outageReason;
    }

    public Integer getOutageSource() {
        return outageSource;
    }

    public void setOutageSource(Integer outageSource) {
        this.outageSource = outageSource;
    }

    public Integer getOutageStatus() {
        return outageStatus;
    }

    public void setOutageStatus(Integer outageStatus) {
        this.outageStatus = outageStatus;
    }

    public String getOutStartDateStr() {
        return outStartDateStr;
    }

    public void setOutStartDateStr(String outStartDateStr) {
        this.outStartDateStr = outStartDateStr;
    }

    public double getOutStopLongTime() {
        return outStopLongTime;
    }

    public void setOutStopLongTime(double outStopLongTime) {
        this.outStopLongTime = outStopLongTime;
    }

    public double getOutStopLongTimeSurplus() {
        return outStopLongTimeSurplus;
    }

    public void setOutStopLongTimeSurplus(double outStopLongTimeSurplus) {
        this.outStopLongTimeSurplus = outStopLongTimeSurplus;
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public Float getDriverDutyTotalHour() {
        return driverDutyTotalHour;
    }

    public void setDriverDutyTotalHour(Float driverDutyTotalHour) {
        this.driverDutyTotalHour = driverDutyTotalHour;
    }

    public Double[] getLoc() {
        return loc;
    }

    public void setLoc(Double[] loc) {
        this.loc = loc;
    }

    public Integer getGoHomeFlag() {
        return goHomeFlag;
    }

    public void setGoHomeFlag(Integer goHomeFlag) {
        this.goHomeFlag = goHomeFlag;
    }

    public String getUpdateGoHomeDate() {
        return updateGoHomeDate;
    }

    public void setUpdateGoHomeDate(String updateGoHomeDate) {
        this.updateGoHomeDate = updateGoHomeDate;
    }

    public String getGoHomeAddress() {
        return goHomeAddress;
    }

    public void setGoHomeAddress(String goHomeAddress) {
        this.goHomeAddress = goHomeAddress;
    }

    public String getGoHomePointLa() {
        return goHomePointLa;
    }

    public void setGoHomePointLa(String goHomePointLa) {
        this.goHomePointLa = goHomePointLa;
    }

    public String getGoHomePointLo() {
        return goHomePointLo;
    }

    public void setGoHomePointLo(String goHomePointLo) {
        this.goHomePointLo = goHomePointLo;
    }

    public Integer getGoHomeStatus() {
        return goHomeStatus;
    }

    public void setGoHomeStatus(Integer goHomeStatus) {
        this.goHomeStatus = goHomeStatus;
    }

    public Integer getGrabNotice() {
        return grabNotice;
    }

    public void setGrabNotice(Integer grabNotice) {
        this.grabNotice = grabNotice;
    }

    public String getDistinceStr() {
        return distinceStr;
    }

    public void setDistinceStr(String distinceStr) {
        this.distinceStr = distinceStr;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    // 以下为基本消息
    // 司机ID
    private Integer driverId;
    // @Length(max=32)
    private String password;
    // @Length(max=11)
    private String phone;
    //
    private Integer gender;
    // @Length(max=20)
    private String name;
    //
    private Integer supplierId;
    //
    private String supplierName;
    //
    private Integer age;
    // @Length(max=18)
    private String idCardNo;
    // @Length(max=10)
    private String drivingLicenseType;
    //
    private Integer drivingYears;
    //@Length(max=20)
    private String archivesNo;
    //
    private String issueDate;
    //
    private String expireDate;
    //
    private Integer serviceCityId;
    //@Length(max=255)
    private String attachmentName;
    //@Length(max=255)
    private String attachmentAddr;
    //@Length(max=60)
    private String accountBank;
    //@Length(max=30)
    private String bankAccountNo;
    //@Length(max=10)
    private String licensePlates;
    //@NotNull
    private Date updateDate;
    //@NotNull
    private Date createDate;
    //
    private Integer updateBy;
    //
    private Integer createBy;
    //服务状态 1待服务2服务中3异动4锁定中
    private Integer serviceStatus;
    //上班状态
    private Integer dutyStatus;
    //在线状态
    private Integer onlineStatus;

    private String memo;
    //司机有效状态
    private Integer status;

    private String mac;

    private String photoSrc;

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public String getBookingDateStr() {
        return bookingDateStr;
    }

    public void setBookingDateStr(String bookingDateStr) {
        this.bookingDateStr = bookingDateStr;
    }

    public String getHeartDate() {
        return heartDate;
    }

    public void setHeartDate(String heartDate) {
        this.heartDate = heartDate;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getCurrentGPSPoint() {
        return currentGPSPoint;
    }

    public void setCurrentGPSPoint(String currentGPSPoint) {
        this.currentGPSPoint = currentGPSPoint;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getHotCount() {
        return hotCount;
    }

    public void setHotCount(Integer hotCount) {
        this.hotCount = hotCount;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getServiceCityName() {
        return serviceCityName;
    }

    public void setServiceCityName(String serviceCityName) {
        this.serviceCityName = serviceCityName;
    }

    public String getDistinceMi() {
        return distinceMi;
    }

    public void setDistinceMi(String distinceMi) {
        this.distinceMi = distinceMi;
    }

    public Double getDistince() {
        return distince;
    }

    public void setDistince(Double distince) {
        this.distince = distince;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getServicecount() {
        return servicecount;
    }

    public void setServicecount(long servicecount) {
        this.servicecount = servicecount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSuperintendNo() {
        return superintendNo;
    }

    public void setSuperintendNo(String superintendNo) {
        this.superintendNo = superintendNo;
    }

    public String getSuperintendUrl() {
        return superintendUrl;
    }

    public void setSuperintendUrl(String superintendUrl) {
        this.superintendUrl = superintendUrl;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getDrivingLicenseType() {
        return drivingLicenseType;
    }

    public void setDrivingLicenseType(String drivingLicenseType) {
        this.drivingLicenseType = drivingLicenseType;
    }

    public Integer getDrivingYears() {
        return drivingYears;
    }

    public void setDrivingYears(Integer drivingYears) {
        this.drivingYears = drivingYears;
    }

    public String getArchivesNo() {
        return archivesNo;
    }

    public void setArchivesNo(String archivesNo) {
        this.archivesNo = archivesNo;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getServiceCityId() {
        return serviceCityId;
    }

    public void setServiceCityId(Integer serviceCityId) {
        this.serviceCityId = serviceCityId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentAddr() {
        return attachmentAddr;
    }

    public void setAttachmentAddr(String attachmentAddr) {
        this.attachmentAddr = attachmentAddr;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getDutyStatus() {
        return dutyStatus;
    }

    public void setDutyStatus(Integer dutyStatus) {
        this.dutyStatus = dutyStatus;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPhotoSrc() {
        if(photoSrc == null || "".equals(photoSrc) ) {
            this.photoSrc = "//images.01zhuanche.com/statics/touch/images/card01.jpg";
        }
        return photoSrc;
    }

    public void setPhotoSrc(String photoSrc) {
        this.photoSrc = photoSrc;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Double[] getBaidu_loc() {
        return baidu_loc;
    }

    public void setBaidu_loc(Double[] baidu_loc) {
        this.baidu_loc = baidu_loc;
    }

    public String getBaidu_currentGPSPoint() {
        return baidu_currentGPSPoint;
    }

    public void setBaidu_currentGPSPoint(String baidu_currentGPSPoint) {
        this.baidu_currentGPSPoint = baidu_currentGPSPoint;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String getPasswordCode() {
        return passwordCode;
    }

    public void setPasswordCode(String passwordCode) {
        this.passwordCode = passwordCode;
    }

    public String getGoHomePointLoBD() {
        return goHomePointLoBD;
    }

    public void setGoHomePointLoBD(String goHomePointLoBD) {
            this.goHomePointLoBD = goHomePointLoBD;
        }
}
