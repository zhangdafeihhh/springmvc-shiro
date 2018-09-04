package com.zhuanche.dto.mdbcarmanage;

public class CarBizDriverInfoTempDTO {
    private String driverId;
    private String password;
    private String phone;
    private Integer gender;
    private String name;
    private Integer supplierId;
    // 服务监督码
    private String superintendNo;
    // 服务监督url
    private String superintendUrl;
    private Integer age;
    private String idCardNo;
    private String drivingLicenseType;
    private Integer drivingYears;
    private String archivesNo;
    private String issueDate;
    private String expireDate;
    private Integer serviceCityId;
    private String serviceCity;
    private String attachmentName;
    private String attachmentAddr;
    private String accountBank;
    private String bankAccountNo;
    private String licensePlates;//车牌号
    private String licensePlatesId;//update    车牌号所对应的车辆id
    private String updateDate;
    private String createDate;
    private String updateDateStr;
    private String createDateStr;
    private Integer updateBy;
    private Integer createBy;
    private Integer status;
    private String memo;
    private String mac;
    private String workStatus;// 上班状态
    private String serviceStatus;// 服务状态
    private String onlineStatus;// 在线状态
    private String loginStatus;// 登录状态
    private String createByName;
    private String updateByName;
    private String supplierName;
    private String currentAddress;
    private String emergencyContactPerson;
    private String emergencyContactNumber;
    //信用卡
    private Integer isBindingCreditCard;
    private String creditCardNo;
    private String creditOpenAccountBank;
    private String creditCardPeriodOfValidity;
    private String CVN2;
    private String shortCardNo;
    //update----------
    private String quickpayCustomerid;//快钱customerID,用于建立司机与信用卡的绑定关系，不能使用电话号码
    private String bankid;//bankid，快钱给的银行名称缩写

    private String createDateBegin;
    private String createDateEnd;
    private Integer groupid;
    private String groupId;
    private String carGroupName;
    //原始的车牌号
    private String licensePlatesOld;
    private String orderNo;
    //事件ID -- update by cuiw 2015/8/4 --
    private Integer actionId;
    //是否打开 抢单服务 0 不抢 1 抢单
    private int grabNotice;

    //
    private Double creditBalance;
    //
    private Double creditBalanceHisall;
    //
    private Double outCurrAccount;
    //
    private Double outHisallAccount;
    private Double accountAmount;
    private Double settleAccount;
    //
    private Double currAccount;
    private Double hisallAccount;
    private Double withdrawDeposit;
    private Double frozenAmount;
    private String genderString;
    private String statusString;
    private String photoSrc;

    private String drivingTypeString;

    private String appVersion;

    private String pushId;


    private String isLimit;
    private int cityId;

    //update  权限设置
    private String cities;
    private String suppliers;
    private String teamid;//车队
    private String teamName;
    private String teamIds;

    private String groupIds;//车队下的小组
    private String groupName;

    private String driverIds;

    private String fileName;//批量导入上传文件名称

    private String cityName;//城市名称

    private Integer passwordReset;//密码重置

    private String goHomeAddress;
    private String goHomePointLa;
    private String goHomePointLo;

    private String driverLicenseNumber;//机动车驾驶证号

    private String drivingLicenseImg;//驾驶证扫描件URL

    private String firstDrivingLicenseDate;//初次领取驾驶证日期

    private String firstMeshworkDrivingLicenseDate;//网络预约出租汽车驾驶员证初领日期

    private String nationAlity;//国籍

    private String houseHoldRegister;//户籍

    private String nation;//驾驶员民族

    private String marriage;//驾驶员婚姻状况

    private String foreignLanguage;//驾驶员外语能力

    private String education;//驾驶员学历

    private String courseName;//驾驶员培训课程名称

    private String courseDate;//培训课程日期（多个日期用,分割）

    private String courseDateStart;//培训开始时间

    private String courseDateEnd;//培训结束时间

    private String courseTime;//培训时长

    private String corpType;//驾驶员合同（或协议）签署公司标识

    private String trafficViolations;//交通违章次数

    private String signDate;//签署日期

    private String signDateEnd;//合同（或协议）到期时间

    private String contractDate;//有效合同时间

    private Integer isXyDriver;//是否巡游出租汽车驾驶员

    private String xyDriverNumber;//巡游出租汽车驾驶员资格证号

    private String partTimeJobDri;//专职或兼职司机

    private String phoneType;//司机手机型号

    private String phoneCorp;//司机手机运营商

    private String mapType;//

    private String emergencyContactAddr;//紧急情况联系人通讯地址

    private String assessment;//评估

    private String driverLicenseIssuingDateStart;//资格证有效起始日期

    private String driverLicenseIssuingDateEnd;//资格证有效截止日期

    private String driverLicenseIssuingCorp;//网络预约出租汽车驾驶员证发证机构

    private String driverLicenseIssuingNumber;//网络预约出租汽车驾驶员资格证号

    private String driverLicenseIssuingRegisterDate;//注册日期

    private String driverLicenseIssuingFirstDate;//初次领取资格证日期

    private String driverLicenseIssuingGrantDate;//资格证发证日期

    private String birthDay;//出生日期

    private String houseHoldRegisterPermanent;//户口登记机关名称

    private String isDriverBlack;//是否在驾驶员黑名单内

    //update  zll  2017-04-11 交通委需要修改以下信息的记录
    private String oldPhone;//手机号
    private String oldIdCardNo;//身份证
    private String oldDriverLicenseNumber;//机动车驾驶证号
    private String oldDriverLicenseIssuingNumber;//网络预约出租汽车驾驶员资格证号

    //update 2017-04-21 zll 加入加盟类型
    private int cooperationType;//加盟类型  car_biz_cooperation_type

    private String cooperationName;//加盟类型

    //update 2017-04-28 司机更改供应商，需要把改司机从原有供应商下的车队和车组删除
    private Integer oldSupplierId;//

    private Integer oldCityId;

    private String imei;

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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
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

    public String getServiceCity() {
        return serviceCity;
    }

    public void setServiceCity(String serviceCity) {
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

    public String getLicensePlatesId() {
        return licensePlatesId;
    }

    public void setLicensePlatesId(String licensePlatesId) {
        this.licensePlatesId = licensePlatesId;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDateStr() {
        return updateDateStr;
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public Integer getIsBindingCreditCard() {
        return isBindingCreditCard;
    }

    public void setIsBindingCreditCard(Integer isBindingCreditCard) {
        this.isBindingCreditCard = isBindingCreditCard;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
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

    public String getCVN2() {
        return CVN2;
    }

    public void setCVN2(String CVN2) {
        this.CVN2 = CVN2;
    }

    public String getShortCardNo() {
        return shortCardNo;
    }

    public void setShortCardNo(String shortCardNo) {
        this.shortCardNo = shortCardNo;
    }

    public String getQuickpayCustomerid() {
        return quickpayCustomerid;
    }

    public void setQuickpayCustomerid(String quickpayCustomerid) {
        this.quickpayCustomerid = quickpayCustomerid;
    }

    public String getBankid() {
        return bankid;
    }

    public void setBankid(String bankid) {
        this.bankid = bankid;
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

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName;
    }

    public String getLicensePlatesOld() {
        return licensePlatesOld;
    }

    public void setLicensePlatesOld(String licensePlatesOld) {
        this.licensePlatesOld = licensePlatesOld;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public int getGrabNotice() {
        return grabNotice;
    }

    public void setGrabNotice(int grabNotice) {
        this.grabNotice = grabNotice;
    }

    public Double getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(Double creditBalance) {
        this.creditBalance = creditBalance;
    }

    public Double getCreditBalanceHisall() {
        return creditBalanceHisall;
    }

    public void setCreditBalanceHisall(Double creditBalanceHisall) {
        this.creditBalanceHisall = creditBalanceHisall;
    }

    public Double getOutCurrAccount() {
        return outCurrAccount;
    }

    public void setOutCurrAccount(Double outCurrAccount) {
        this.outCurrAccount = outCurrAccount;
    }

    public Double getOutHisallAccount() {
        return outHisallAccount;
    }

    public void setOutHisallAccount(Double outHisallAccount) {
        this.outHisallAccount = outHisallAccount;
    }

    public Double getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(Double accountAmount) {
        this.accountAmount = accountAmount;
    }

    public Double getSettleAccount() {
        return settleAccount;
    }

    public void setSettleAccount(Double settleAccount) {
        this.settleAccount = settleAccount;
    }

    public Double getCurrAccount() {
        return currAccount;
    }

    public void setCurrAccount(Double currAccount) {
        this.currAccount = currAccount;
    }

    public Double getHisallAccount() {
        return hisallAccount;
    }

    public void setHisallAccount(Double hisallAccount) {
        this.hisallAccount = hisallAccount;
    }

    public Double getWithdrawDeposit() {
        return withdrawDeposit;
    }

    public void setWithdrawDeposit(Double withdrawDeposit) {
        this.withdrawDeposit = withdrawDeposit;
    }

    public Double getFrozenAmount() {
        return frozenAmount;
    }

    public void setFrozenAmount(Double frozenAmount) {
        this.frozenAmount = frozenAmount;
    }

    public String getGenderString() {
        return genderString;
    }

    public void setGenderString(String genderString) {
        this.genderString = genderString;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public String getPhotoSrc() {
        return photoSrc;
    }

    public void setPhotoSrc(String photoSrc) {
        this.photoSrc = photoSrc;
    }

    public String getDrivingTypeString() {
        return drivingTypeString;
    }

    public void setDrivingTypeString(String drivingTypeString) {
        this.drivingTypeString = drivingTypeString;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getIsLimit() {
        return isLimit;
    }

    public void setIsLimit(String isLimit) {
        this.isLimit = isLimit;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(String teamIds) {
        this.teamIds = teamIds;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getPasswordReset() {
        return passwordReset;
    }

    public void setPasswordReset(Integer passwordReset) {
        this.passwordReset = passwordReset;
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

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getDrivingLicenseImg() {
        return drivingLicenseImg;
    }

    public void setDrivingLicenseImg(String drivingLicenseImg) {
        this.drivingLicenseImg = drivingLicenseImg;
    }

    public String getFirstDrivingLicenseDate() {
        return firstDrivingLicenseDate;
    }

    public void setFirstDrivingLicenseDate(String firstDrivingLicenseDate) {
        this.firstDrivingLicenseDate = firstDrivingLicenseDate;
    }

    public String getFirstMeshworkDrivingLicenseDate() {
        return firstMeshworkDrivingLicenseDate;
    }

    public void setFirstMeshworkDrivingLicenseDate(String firstMeshworkDrivingLicenseDate) {
        this.firstMeshworkDrivingLicenseDate = firstMeshworkDrivingLicenseDate;
    }

    public String getNationAlity() {
        return nationAlity;
    }

    public void setNationAlity(String nationAlity) {
        this.nationAlity = nationAlity;
    }

    public String getHouseHoldRegister() {
        return houseHoldRegister;
    }

    public void setHouseHoldRegister(String houseHoldRegister) {
        this.houseHoldRegister = houseHoldRegister;
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

    public String getForeignLanguage() {
        return foreignLanguage;
    }

    public void setForeignLanguage(String foreignLanguage) {
        this.foreignLanguage = foreignLanguage;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public String getCourseDateStart() {
        return courseDateStart;
    }

    public void setCourseDateStart(String courseDateStart) {
        this.courseDateStart = courseDateStart;
    }

    public String getCourseDateEnd() {
        return courseDateEnd;
    }

    public void setCourseDateEnd(String courseDateEnd) {
        this.courseDateEnd = courseDateEnd;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public String getCorpType() {
        return corpType;
    }

    public void setCorpType(String corpType) {
        this.corpType = corpType;
    }

    public String getTrafficViolations() {
        return trafficViolations;
    }

    public void setTrafficViolations(String trafficViolations) {
        this.trafficViolations = trafficViolations;
    }

    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }

    public String getSignDateEnd() {
        return signDateEnd;
    }

    public void setSignDateEnd(String signDateEnd) {
        this.signDateEnd = signDateEnd;
    }

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }

    public Integer getIsXyDriver() {
        return isXyDriver;
    }

    public void setIsXyDriver(Integer isXyDriver) {
        this.isXyDriver = isXyDriver;
    }

    public String getXyDriverNumber() {
        return xyDriverNumber;
    }

    public void setXyDriverNumber(String xyDriverNumber) {
        this.xyDriverNumber = xyDriverNumber;
    }

    public String getPartTimeJobDri() {
        return partTimeJobDri;
    }

    public void setPartTimeJobDri(String partTimeJobDri) {
        this.partTimeJobDri = partTimeJobDri;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneCorp() {
        return phoneCorp;
    }

    public void setPhoneCorp(String phoneCorp) {
        this.phoneCorp = phoneCorp;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getEmergencyContactAddr() {
        return emergencyContactAddr;
    }

    public void setEmergencyContactAddr(String emergencyContactAddr) {
        this.emergencyContactAddr = emergencyContactAddr;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getDriverLicenseIssuingDateStart() {
        return driverLicenseIssuingDateStart;
    }

    public void setDriverLicenseIssuingDateStart(String driverLicenseIssuingDateStart) {
        this.driverLicenseIssuingDateStart = driverLicenseIssuingDateStart;
    }

    public String getDriverLicenseIssuingDateEnd() {
        return driverLicenseIssuingDateEnd;
    }

    public void setDriverLicenseIssuingDateEnd(String driverLicenseIssuingDateEnd) {
        this.driverLicenseIssuingDateEnd = driverLicenseIssuingDateEnd;
    }

    public String getDriverLicenseIssuingCorp() {
        return driverLicenseIssuingCorp;
    }

    public void setDriverLicenseIssuingCorp(String driverLicenseIssuingCorp) {
        this.driverLicenseIssuingCorp = driverLicenseIssuingCorp;
    }

    public String getDriverLicenseIssuingNumber() {
        return driverLicenseIssuingNumber;
    }

    public void setDriverLicenseIssuingNumber(String driverLicenseIssuingNumber) {
        this.driverLicenseIssuingNumber = driverLicenseIssuingNumber;
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

    public String getIsDriverBlack() {
        return isDriverBlack;
    }

    public void setIsDriverBlack(String isDriverBlack) {
        this.isDriverBlack = isDriverBlack;
    }

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

    public Integer getOldSupplierId() {
        return oldSupplierId;
    }

    public void setOldSupplierId(Integer oldSupplierId) {
        this.oldSupplierId = oldSupplierId;
    }

    public Integer getOldCityId() {
        return oldCityId;
    }

    public void setOldCityId(Integer oldCityId) {
        this.oldCityId = oldCityId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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
}