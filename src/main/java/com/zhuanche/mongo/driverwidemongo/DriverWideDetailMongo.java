package com.zhuanche.mongo.driverwidemongo;


import java.util.Date;

/**
 * @author (yangbo)
 * @Date: 2019/4/9 11:00
 * @Description:(司机宽表明细信息)
 */
public class DriverWideDetailMongo {

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 名族
     */
    private String nation;

    /**
     * 户口登记机关
     */
    private String houseHoldRegisterPermanent;

    /**
     * 户籍所在地
     */
    private String houseHoldRegister;

    /**
     * 出生日期
     */
    private String birthDay;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 司机手机型号
     */
    private String phoneType;

    /**
     * 司机手机运营商
     */
    private String phoneCorp;

    /**
     * 司机年龄
     */
    private Integer age;

    /**
     * 婚姻状况
     */
    private String marriage;

    /**
     * 学历
     */
    private String education;

    /**
     * 外语能力 0无（默认） 1 英语 2 德语  3 法语 4 其他
     */
    private String foreignlanguage;

    /**
     * 现住址
     */
    private String currentAddress;

    /**
     * 紧急联系人
     */
    private String emergencyContactPerson;

    /**
     * 紧急联系方式
     */
    private String emergencyContactNumber;

    /**
     * 紧急联系地址
     */
    private String emergencyContactAddr;

    /**
     * 司机头像
     */
    private String photoSrc;

    /**
     * 驾驶证扫描件
     */
    private String drivingLicenseImg;

    /**
     * 驾照类型
     */
    private String drivingLicenseType;

    /**
     * 领证日期
     */
    private Date issueDate;

    /**
     * 到期时间
     */
    private Date expireDate;

    /**
     * 服务监督码
     */
    private String superintendNo;

    /**
     * 服务监督链接
     */
    private String superintendUrl;

    /**
     * 档案编号
     */
    private String archivesNo;

    /**
     * 驾龄
     */
    private Integer drivingYears;

    /**
     * 机动车驾驶证
     */
    private String driverLicenseNumber;

    /**
     * 初次领证日期
     */
    private String firstDrivingLicenseDate;

    /**
     * 网络预约出租汽车驾驶证初领日期
     */
    private String firstMeshworkDrivingLicenseDate;

    /**
     * 驾驶员签署公司标识
     */
    private String corpType;

    /**
     * 签署日期
     */
    private String signDate;

    /**
     * 合同到期时间
     */
    private String signDateEnd;

    /**
     * 有效合同时间
     */
    private String contractDate;

    /**
     * 是否巡游出租车驾驶员
     */
    private Integer isXyDriver;

    /**
     * 巡游出租车驾驶员资格证号
     */
    private String xyDriverNumber;

    /**
     * 是否专职司机
     */
    private String partTimeJobDri;

    /**
     * 地图类型
     */
    private String mapType;

    /**
     * 驾驶员服务质量信誉考核结果【评估】
     */
    private String assessment;

    /**
     * 资格证有效起始日期
     */
    private String driverLicenseIssuingDateStart;

    /**
     * 资格证有效截止日期
     */
    private String driverLicenseIssuingDateEnd;

    /**
     * 网络预约出租汽车驾驶员证发证机构
     */
    private String driverLicenseIssuingCorp;

    /**
     * 网络预约出租汽车驾驶员资格证号
     */
    private String driverLicenseIssuingNumber;

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
     * 银行卡卡号
     */
    private String bankCardNumber;

    /**
     * 银行卡开户行
     */
    private String bankCardBank;

    /**
     * 信用卡开户行
     */
    private String creditOpenAccountBank;

    /**
     * 信用卡卡号
     */
    private String creditCardNo;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * app版本
     */
    private String appVersion;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getHouseHoldRegisterPermanent() {
        return houseHoldRegisterPermanent;
    }

    public void setHouseHoldRegisterPermanent(String houseHoldRegisterPermanent) {
        this.houseHoldRegisterPermanent = houseHoldRegisterPermanent;
    }

    public String getHouseHoldRegister() {
        return houseHoldRegister;
    }

    public void setHouseHoldRegister(String houseHoldRegister) {
        this.houseHoldRegister = houseHoldRegister;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getForeignlanguage() {
        return foreignlanguage;
    }

    public void setForeignlanguage(String foreignlanguage) {
        this.foreignlanguage = foreignlanguage;
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

    public String getEmergencyContactAddr() {
        return emergencyContactAddr;
    }

    public void setEmergencyContactAddr(String emergencyContactAddr) {
        this.emergencyContactAddr = emergencyContactAddr;
    }

    public String getPhotoSrc() {
        return photoSrc;
    }

    public void setPhotoSrc(String photoSrc) {
        this.photoSrc = photoSrc;
    }

    public String getDrivingLicenseImg() {
        return drivingLicenseImg;
    }

    public void setDrivingLicenseImg(String drivingLicenseImg) {
        this.drivingLicenseImg = drivingLicenseImg;
    }

    public String getDrivingLicenseType() {
        return drivingLicenseType;
    }

    public void setDrivingLicenseType(String drivingLicenseType) {
        this.drivingLicenseType = drivingLicenseType;
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

    public String getArchivesNo() {
        return archivesNo;
    }

    public void setArchivesNo(String archivesNo) {
        this.archivesNo = archivesNo;
    }

    public Integer getDrivingYears() {
        return drivingYears;
    }

    public void setDrivingYears(Integer drivingYears) {
        this.drivingYears = drivingYears;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
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

    public String getCorpType() {
        return corpType;
    }

    public void setCorpType(String corpType) {
        this.corpType = corpType;
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

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
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

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getBankCardBank() {
        return bankCardBank;
    }

    public void setBankCardBank(String bankCardBank) {
        this.bankCardBank = bankCardBank;
    }

    public String getCreditOpenAccountBank() {
        return creditOpenAccountBank;
    }

    public void setCreditOpenAccountBank(String creditOpenAccountBank) {
        this.creditOpenAccountBank = creditOpenAccountBank;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
    }
}
