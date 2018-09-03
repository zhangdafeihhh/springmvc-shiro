package com.zhuanche.dto.rentcar;

import com.zhuanche.entity.common.Base;

import java.util.Date;

/**
 * car_biz_driver_info
 */
public class CarBizDriverInfoDTO extends Base {
    private Integer driverId;

    private String password;

    private String phone;

    private Integer gender;

    private String name;

    /**
     * 供应商
     */
    private Integer supplierId;

    private Integer age;

    private String currentAddress;

    private String emergencyContactPerson;

    private String emergencyContactNumber;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 服务监督码
     */
    private String superintendNo;

    /**
     * 服务监督码链接
     */
    private String superintendUrl;

    /**
     * 驾照类型
     */
    private String drivingLicenseType;

    /**
     * 驾龄
     */
    private Integer drivingYears;

    /**
     * 档案编号
     */
    private String archivesNo;

    /**
     * 领证日期
     */
    private Date issueDate;

    /**
     * 驾照到期时间
     */
    private Date expireDate;

    /**
     * 服务城市
     */
    private Integer serviceCity;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件地址
     */
    private String attachmentAddr;

    /**
     * 开户银行
     */
    private String accountBank;

    /**
     * 银行账号
     */
    private String bankAccountNo;

    /**
     * 车牌号
     */
    private String licensePlates;

    private Date updateDate;

    private Date createDate;

    private Integer updateBy;

    private Integer createBy;

    private Integer status;

    private String mac;

    /**
     * 司机头像
     */
    private String photosrct;

    private String pushId;

    /**
     * 是否绑定信用卡 1绑定 0未绑定
     */
    private Integer isBindingCreditCard;

    private String appVersion;

    /**
     * 信用卡号
     */
    private String creditCardNo;

    /**
     * 信用卡短卡号（块钱接口返回）
     */
    private String storableCardNo;

    /**
     * 开户行
     */
    private String creditOpenAccountBank;

    private String creditCardPeriodOfValidity;

    /**
     * 司机是否打开可以抢单 默认0 不可以 1 可以
     */
    private Integer grabNotice;

    private String creditCvn2;

    private Date creditBindDate;

    /**
     * 快钱customerID,用于建立司机与信用卡的绑定关系，不能使用电话号码
     */
    private String quickpayCustomerid;

    /**
     * 是否有未读信息 0 没有；1 有
     */
    private Integer isRead;

    /**
     * 绑定信用卡时返回的bankid
     */
    private String bankid;

    /**
     * 回家模式地址
     */
    private String goHomeAddress;

    /**
     * 回家模式地址百度经度
     */
    private String goHomePointLobd;

    /**
     * 回家模式地址百度纬度
     */
    private String goHomePointLabd;

    /**
     * 回家模式地址纬度
     */
    private String goHomePointLa;

    /**
     * 回家模式地址经度
     */
    private String goHomePointLo;

    /**
     * 回家模式状态开启 1开启；0关闭
     */
    private Integer goHomeStatus;

    /**
     * 司机端修改回家模式时间 格式 2015-11-01
     */
    private String updateGoHomeDate;

    /**
     * IMEI
     */
    private String imei;

    /**
     * 机动车驾驶证号
     */
    private String driverlicensenumber;

    /**
     * 驾驶证扫描件URL
     */
    private String drivinglicenseimg;

    /**
     * 初次领取驾驶证日期
     */
    private String firstdrivinglicensedate;

    /**
     * 网络预约出租汽车驾驶员证初领日期
     */
    private String firstmeshworkdrivinglicensedate;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 户籍
     */
    private String householdregister;

    /**
     * 驾驶员民族
     */
    private String nation;

    /**
     * 驾驶员婚姻状况
     */
    private String marriage;

    /**
     *  0无（默认） 1 英语 2 德语  3 法语 4 其他
     */
    private String foreignlanguage;

    /**
     * 驾驶员通信地址
     */
    private String address;

    /**
     * 驾驶员学历
     */
    private String education;

    /**
     * 驾驶员培训课程名称
     */
    private String coursename;

    /**
     * 培训课程日期（多个日期用,分割）
     */
    private String coursedate;

    /**
     * 培训开始时间
     */
    private String coursedatestart;

    /**
     * 培训结束时间
     */
    private String coursedateend;

    /**
     * 培训时长
     */
    private String coursetime;

    /**
     * 驾驶员合同（或协议）签署公司标识
     */
    private String corptype;

    /**
     * 交通违章次数
     */
    private String trafficviolations;

    /**
     * 签署日期
     */
    private String signdate;

    /**
     * 合同（或协议）到期时间
     */
    private String signdateend;

    /**
     * 有效合同时间
     */
    private String contractdate;

    /**
     * 是否巡游出租汽车驾驶员
     */
    private Integer isxydriver;

    /**
     * 专职或兼职司机
     */
    private String parttimejobdri;

    /**
     * 司机手机型号
     */
    private String phonetype;

    /**
     * 司机手机运营商
     */
    private String phonecorp;

    /**
     * 司机使用app版本号
     */
    private String appversion;

    /**
     * 使用地图类型
     */
    private String maptype;

    /**
     * 紧急情况联系人通讯地址
     */
    private String emergencycontactaddr;

    /**
     * 驾驶员服务质量信誉考核结果
     */
    private String assessment;

    /**
     * 驾驶员证发证日期
     */
    private String driverlicenseissuingdatestart;

    /**
     * 驾驶员证有效期止
     */
    private String driverlicenseissuingdateend;

    /**
     * 驾驶员证发证机构
     */
    private String driverlicenseissuingcorp;

    /**
     * 网络预约出租汽车驾驶员证编号
     */
    private String driverlicenseissuingnumber;

    /**
     * 巡游出租汽车驾驶员资格证号
     */
    private String xyDriverNumber;

    /**
     * 注册日期
     */
    private String driverLicenseIssuingRegisterDate;

    /**
     * 初次领取资格证日期
     */
    private String driverLicenseIssuingFirstDate;

    /**
     * 资格证发证日期
     */
    private String driverLicenseIssuingGrantDate;

    /**
     * 出生日期
     */
    private String birthDay;

    /**
     * 户口登记机关名称
     */
    private String houseHoldRegisterPermanent;

    /**
     * 是否上传身份证照片；1：上传，非1：未上传
     */
    private Integer isUploadCard;

    /**
     * 是否必须进行人脸识别验证。1：必须验证；非1：不是必须验证
     */
    private Integer isMustConfirmation;

    private String englishName;

    /**
     * 合同类型： HT:合同、XY:协议
     */
    private String contractType;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同附件访问路径
     */
    private String contractFile;

    /**
     * 加盟类型 car_biz_cooperation_type
     */
    private Integer cooperationType;

    /**
     * 加盟类型名称
     */
    private String cooperationTypeName;

    /**
     * 司机类型：1=普通账号、2=内部测试账号
     */
    private Integer accountType;

    /**
     * 服务类型car_biz_car_group
     */
    private Integer groupId;

    /**
     * 是否双班司机 0默认为单班， 1双班
     */
    private Integer isTwoShifts;

    private Integer isImage;

    private String memo;

    private Integer passwordReset;//密码重置

    /**
     * 银行卡开户行
     */
    private String bankCardBank;

    /**
     * 银行卡卡号
     */
    private String bankCardNumber;

    /**
     * 可扩展1
     * 司机停运状态  1停运 2正常
     */
    private Integer ext1;

    /**
     * 可扩展2
     */
    private Integer ext2;

    /**
     * 可扩展3
     */
    private Integer ext3;

    /**
     * 可扩展4
     */
    private Integer ext4;

    /**
     * 可扩展5
     */
    private Integer ext5;

    /**
     * 可扩展6
     */
    private String ext6;

    /**
     * 可扩展7
     */
    private String ext7;

    /**
     * 可扩展8
     */
    private String ext8;

    /**
     * 可扩展9
     */
    private String ext9;

    /**
     * 可扩展10
     */
    private String ext10;

    /**
     * 服务类型名称
     */
    private String carGroupName;
    private Integer modelId;//车型ID
    private String modelName;//车型名称

    //交通委需要修改以下信息的记录
    private String oldPhone;//手机号
    private String oldIdCardNo;//身份证
    private String oldDriverLicenseNumber;//机动车驾驶证号
    private String oldDriverLicenseIssuingNumber;//网络预约出租汽车驾驶员资格证号
    private String oldLicensePlates;//车牌号
    private Integer oldCity;//城市
    private Integer oldSupplier;//供应商

    public String getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
    }

    public String getOldIdCardNo() {
        return oldIdCardNo;
    }

    public void setOldIdCardNo(String oldIdCardNo) {
        this.oldIdCardNo = oldIdCardNo;
    }

    public String getOldDriverLicenseNumber() {
        return oldDriverLicenseNumber;
    }

    public void setOldDriverLicenseNumber(String oldDriverLicenseNumber) {
        this.oldDriverLicenseNumber = oldDriverLicenseNumber;
    }

    public String getOldDriverLicenseIssuingNumber() {
        return oldDriverLicenseIssuingNumber;
    }

    public void setOldDriverLicenseIssuingNumber(String oldDriverLicenseIssuingNumber) {
        this.oldDriverLicenseIssuingNumber = oldDriverLicenseIssuingNumber;
    }

    public String getOldLicensePlates() {
        return oldLicensePlates;
    }

    public void setOldLicensePlates(String oldLicensePlates) {
        this.oldLicensePlates = oldLicensePlates;
    }

    public Integer getOldCity() {
        return oldCity;
    }

    public void setOldCity(Integer oldCity) {
        this.oldCity = oldCity;
    }

    public Integer getOldSupplier() {
        return oldSupplier;
    }

    public void setOldSupplier(Integer oldSupplier) {
        this.oldSupplier = oldSupplier;
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

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getEmergencyContactPerson() {
        return emergencyContactPerson;
    }

    public void setEmergencyContactPerson(String emergencyContactPerson) {
        this.emergencyContactPerson = emergencyContactPerson;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getServiceCity() {
        return serviceCity;
    }

    public void setServiceCity(Integer serviceCity) {
        this.serviceCity = serviceCity;
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

    public String getPhotosrct() {
        return photosrct;
    }

    public void setPhotosrct(String photosrct) {
        this.photosrct = photosrct;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public Integer getIsBindingCreditCard() {
        return isBindingCreditCard;
    }

    public void setIsBindingCreditCard(Integer isBindingCreditCard) {
        this.isBindingCreditCard = isBindingCreditCard;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
    }

    public String getStorableCardNo() {
        return storableCardNo;
    }

    public void setStorableCardNo(String storableCardNo) {
        this.storableCardNo = storableCardNo;
    }

    public String getCreditOpenAccountBank() {
        return creditOpenAccountBank;
    }

    public void setCreditOpenAccountBank(String creditOpenAccountBank) {
        this.creditOpenAccountBank = creditOpenAccountBank;
    }

    public String getCreditCardPeriodOfValidity() {
        return creditCardPeriodOfValidity;
    }

    public void setCreditCardPeriodOfValidity(String creditCardPeriodOfValidity) {
        this.creditCardPeriodOfValidity = creditCardPeriodOfValidity;
    }

    public Integer getGrabNotice() {
        return grabNotice;
    }

    public void setGrabNotice(Integer grabNotice) {
        this.grabNotice = grabNotice;
    }

    public String getCreditCvn2() {
        return creditCvn2;
    }

    public void setCreditCvn2(String creditCvn2) {
        this.creditCvn2 = creditCvn2;
    }

    public Date getCreditBindDate() {
        return creditBindDate;
    }

    public void setCreditBindDate(Date creditBindDate) {
        this.creditBindDate = creditBindDate;
    }

    public String getQuickpayCustomerid() {
        return quickpayCustomerid;
    }

    public void setQuickpayCustomerid(String quickpayCustomerid) {
        this.quickpayCustomerid = quickpayCustomerid;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
    }

    public String getGoHomeAddress() {
        return goHomeAddress;
    }

    public void setGoHomeAddress(String goHomeAddress) {
        this.goHomeAddress = goHomeAddress;
    }

    public String getGoHomePointLobd() {
        return goHomePointLobd;
    }

    public void setGoHomePointLobd(String goHomePointLobd) {
        this.goHomePointLobd = goHomePointLobd;
    }

    public String getGoHomePointLabd() {
        return goHomePointLabd;
    }

    public void setGoHomePointLabd(String goHomePointLabd) {
        this.goHomePointLabd = goHomePointLabd;
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

    public String getUpdateGoHomeDate() {
        return updateGoHomeDate;
    }

    public void setUpdateGoHomeDate(String updateGoHomeDate) {
        this.updateGoHomeDate = updateGoHomeDate;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDriverlicensenumber() {
        return driverlicensenumber;
    }

    public void setDriverlicensenumber(String driverlicensenumber) {
        this.driverlicensenumber = driverlicensenumber;
    }

    public String getDrivinglicenseimg() {
        return drivinglicenseimg;
    }

    public void setDrivinglicenseimg(String drivinglicenseimg) {
        this.drivinglicenseimg = drivinglicenseimg;
    }

    public String getFirstdrivinglicensedate() {
        return firstdrivinglicensedate;
    }

    public void setFirstdrivinglicensedate(String firstdrivinglicensedate) {
        this.firstdrivinglicensedate = firstdrivinglicensedate;
    }

    public String getFirstmeshworkdrivinglicensedate() {
        return firstmeshworkdrivinglicensedate;
    }

    public void setFirstmeshworkdrivinglicensedate(String firstmeshworkdrivinglicensedate) {
        this.firstmeshworkdrivinglicensedate = firstmeshworkdrivinglicensedate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getHouseholdregister() {
        return householdregister;
    }

    public void setHouseholdregister(String householdregister) {
        this.householdregister = householdregister;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getForeignlanguage() {
        return foreignlanguage;
    }

    public void setForeignlanguage(String foreignlanguage) {
        this.foreignlanguage = foreignlanguage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getCoursedate() {
        return coursedate;
    }

    public void setCoursedate(String coursedate) {
        this.coursedate = coursedate;
    }

    public String getCoursedatestart() {
        return coursedatestart;
    }

    public void setCoursedatestart(String coursedatestart) {
        this.coursedatestart = coursedatestart;
    }

    public String getCoursedateend() {
        return coursedateend;
    }

    public void setCoursedateend(String coursedateend) {
        this.coursedateend = coursedateend;
    }

    public String getCoursetime() {
        return coursetime;
    }

    public void setCoursetime(String coursetime) {
        this.coursetime = coursetime;
    }

    public String getCorptype() {
        return corptype;
    }

    public void setCorptype(String corptype) {
        this.corptype = corptype;
    }

    public String getTrafficviolations() {
        return trafficviolations;
    }

    public void setTrafficviolations(String trafficviolations) {
        this.trafficviolations = trafficviolations;
    }

    public String getSigndate() {
        return signdate;
    }

    public void setSigndate(String signdate) {
        this.signdate = signdate;
    }

    public String getSigndateend() {
        return signdateend;
    }

    public void setSigndateend(String signdateend) {
        this.signdateend = signdateend;
    }

    public String getContractdate() {
        return contractdate;
    }

    public void setContractdate(String contractdate) {
        this.contractdate = contractdate;
    }

    public Integer getIsxydriver() {
        return isxydriver;
    }

    public void setIsxydriver(Integer isxydriver) {
        this.isxydriver = isxydriver;
    }

    public String getParttimejobdri() {
        return parttimejobdri;
    }

    public void setParttimejobdri(String parttimejobdri) {
        this.parttimejobdri = parttimejobdri;
    }

    public String getPhonetype() {
        return phonetype;
    }

    public void setPhonetype(String phonetype) {
        this.phonetype = phonetype;
    }

    public String getPhonecorp() {
        return phonecorp;
    }

    public void setPhonecorp(String phonecorp) {
        this.phonecorp = phonecorp;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getMaptype() {
        return maptype;
    }

    public void setMaptype(String maptype) {
        this.maptype = maptype;
    }

    public String getEmergencycontactaddr() {
        return emergencycontactaddr;
    }

    public void setEmergencycontactaddr(String emergencycontactaddr) {
        this.emergencycontactaddr = emergencycontactaddr;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getDriverlicenseissuingdatestart() {
        return driverlicenseissuingdatestart;
    }

    public void setDriverlicenseissuingdatestart(String driverlicenseissuingdatestart) {
        this.driverlicenseissuingdatestart = driverlicenseissuingdatestart;
    }

    public String getDriverlicenseissuingdateend() {
        return driverlicenseissuingdateend;
    }

    public void setDriverlicenseissuingdateend(String driverlicenseissuingdateend) {
        this.driverlicenseissuingdateend = driverlicenseissuingdateend;
    }

    public String getDriverlicenseissuingcorp() {
        return driverlicenseissuingcorp;
    }

    public void setDriverlicenseissuingcorp(String driverlicenseissuingcorp) {
        this.driverlicenseissuingcorp = driverlicenseissuingcorp;
    }

    public String getDriverlicenseissuingnumber() {
        return driverlicenseissuingnumber;
    }

    public void setDriverlicenseissuingnumber(String driverlicenseissuingnumber) {
        this.driverlicenseissuingnumber = driverlicenseissuingnumber;
    }

    public String getXyDriverNumber() {
        return xyDriverNumber;
    }

    public void setXyDriverNumber(String xyDriverNumber) {
        this.xyDriverNumber = xyDriverNumber;
    }

    public String getDriverLicenseIssuingRegisterDate() {
        return driverLicenseIssuingRegisterDate;
    }

    public void setDriverLicenseIssuingRegisterDate(String driverLicenseIssuingRegisterDate) {
        this.driverLicenseIssuingRegisterDate = driverLicenseIssuingRegisterDate;
    }

    public String getDriverLicenseIssuingFirstDate() {
        return driverLicenseIssuingFirstDate;
    }

    public void setDriverLicenseIssuingFirstDate(String driverLicenseIssuingFirstDate) {
        this.driverLicenseIssuingFirstDate = driverLicenseIssuingFirstDate;
    }

    public String getDriverLicenseIssuingGrantDate() {
        return driverLicenseIssuingGrantDate;
    }

    public void setDriverLicenseIssuingGrantDate(String driverLicenseIssuingGrantDate) {
        this.driverLicenseIssuingGrantDate = driverLicenseIssuingGrantDate;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getHouseHoldRegisterPermanent() {
        return houseHoldRegisterPermanent;
    }

    public void setHouseHoldRegisterPermanent(String houseHoldRegisterPermanent) {
        this.houseHoldRegisterPermanent = houseHoldRegisterPermanent;
    }

    public Integer getIsUploadCard() {
        return isUploadCard;
    }

    public void setIsUploadCard(Integer isUploadCard) {
        this.isUploadCard = isUploadCard;
    }

    public Integer getIsMustConfirmation() {
        return isMustConfirmation;
    }

    public void setIsMustConfirmation(Integer isMustConfirmation) {
        this.isMustConfirmation = isMustConfirmation;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractFile() {
        return contractFile;
    }

    public void setContractFile(String contractFile) {
        this.contractFile = contractFile;
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getIsTwoShifts() {
        return isTwoShifts;
    }

    public void setIsTwoShifts(Integer isTwoShifts) {
        this.isTwoShifts = isTwoShifts;
    }

    public Integer getIsImage() {
        return isImage;
    }

    public void setIsImage(Integer isImage) {
        this.isImage = isImage;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getPasswordReset() {
        return passwordReset;
    }

    public void setPasswordReset(Integer passwordReset) {
        this.passwordReset = passwordReset;
    }

    public String getBankCardBank() {
        return bankCardBank;
    }

    public void setBankCardBank(String bankCardBank) {
        this.bankCardBank = bankCardBank;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public Integer getExt1() {
        return ext1;
    }

    public void setExt1(Integer ext1) {
        this.ext1 = ext1;
    }

    public Integer getExt2() {
        return ext2;
    }

    public void setExt2(Integer ext2) {
        this.ext2 = ext2;
    }

    public Integer getExt3() {
        return ext3;
    }

    public void setExt3(Integer ext3) {
        this.ext3 = ext3;
    }

    public Integer getExt4() {
        return ext4;
    }

    public void setExt4(Integer ext4) {
        this.ext4 = ext4;
    }

    public Integer getExt5() {
        return ext5;
    }

    public void setExt5(Integer ext5) {
        this.ext5 = ext5;
    }

    public String getExt6() {
        return ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getExt7() {
        return ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }

    public String getExt8() {
        return ext8;
    }

    public void setExt8(String ext8) {
        this.ext8 = ext8;
    }

    public String getExt9() {
        return ext9;
    }

    public void setExt9(String ext9) {
        this.ext9 = ext9;
    }

    public String getExt10() {
        return ext10;
    }

    public void setExt10(String ext10) {
        this.ext10 = ext10;
    }

    public String getCooperationTypeName() {
        return cooperationTypeName;
    }

    public void setCooperationTypeName(String cooperationTypeName) {
        this.cooperationTypeName = cooperationTypeName;
    }
}