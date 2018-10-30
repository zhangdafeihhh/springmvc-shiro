package com.zhuanche.dto.driver;


import com.zhuanche.entity.rentcar.DriverEntity;

/**
 * ClassName: DriverVoEntity 
 * date: 2016年11月7日 上午11:58:42 
 * 
 * @author zhulingling 
 * @version  
 * @since JDK 1.6 
 */
public class DriverVoEntity extends DriverEntity {

	/** 
	 * serialVersionUID
	 * @since JDK 1.6 
	 */  
	private static final long serialVersionUID = 1L;

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
    /**
     * 是否维护形象
     */
    private Integer isImage;
    /**
     * 司机当日积分
     */
    private String dayIntegral;
    /**
     * 司机当日积分
     */
    private String monthIntegral;
    /**
     *司机等级 
     */
    private String membershipName;
    
    public String getMembershipName() {
		return membershipName;
	}

	public void setMembershipName(String membershipName) {
		this.membershipName = membershipName;
	}

	public String getDayIntegral() {
		return dayIntegral;
	}

	public void setDayIntegral(String dayIntegral) {
		this.dayIntegral = dayIntegral;
	}

	public String getMonthIntegral() {
		return monthIntegral;
	}

	public void setMonthIntegral(String monthIntegral) {
		this.monthIntegral = monthIntegral;
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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Integer getOldCityId() {
		return oldCityId;
	}

	public void setOldCityId(Integer oldCityId) {
		this.oldCityId = oldCityId;
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

	public int getCooperationType() {
		return cooperationType;
	}

	public void setCooperationType(int cooperationType) {
		this.cooperationType = cooperationType;
	}

	//
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

	public void setOldDriverLicenseIssuingNumber(
			String oldDriverLicenseIssuingNumber) {
		this.oldDriverLicenseIssuingNumber = oldDriverLicenseIssuingNumber;
	}

	public String getIsDriverBlack() {
		return isDriverBlack;
	}

	public void setIsDriverBlack(String isDriverBlack) {
		this.isDriverBlack = isDriverBlack;
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

	public void setFirstMeshworkDrivingLicenseDate(
			String firstMeshworkDrivingLicenseDate) {
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

	public void setDriverLicenseIssuingDateStart(
			String driverLicenseIssuingDateStart) {
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

	public void setDriverLicenseIssuingRegisterDate(
			String driverLicenseIssuingRegisterDate) {
		this.driverLicenseIssuingRegisterDate = driverLicenseIssuingRegisterDate;
	}

	public String getDriverLicenseIssuingFirstDate() {
		return driverLicenseIssuingFirstDate;
	}

	public void setDriverLicenseIssuingFirstDate(
			String driverLicenseIssuingFirstDate) {
		this.driverLicenseIssuingFirstDate = driverLicenseIssuingFirstDate;
	}

	public String getDriverLicenseIssuingGrantDate() {
		return driverLicenseIssuingGrantDate;
	}

	public void setDriverLicenseIssuingGrantDate(
			String driverLicenseIssuingGrantDate) {
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
	
	@Override  
    public boolean equals(Object obj) {  
		DriverVoEntity s=(DriverVoEntity)obj;   
	    return super.getPhone().equals(s.getPhone());   
    }   
    @Override
    public int hashCode() {  
	    String in =  super.getPhone();  
	    return in.hashCode();  
    }

	public Integer getIsImage() {
		return isImage;
	}

	public void setIsImage(Integer isImage) {
		this.isImage = isImage;
	}

}
  