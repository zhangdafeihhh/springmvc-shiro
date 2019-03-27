package com.zhuanche.vo.busManage;

import java.io.Serializable;
import java.util.Date;

public class BusDriverDetailInfoVO implements Serializable {

	private static final long serialVersionUID = 3499280025355834858L;

	/** 司机ID **/
	private Integer driverId;

	/** 司机手机号 **/
	private String phone;

	/** 司机性别 (1.男;0.女) **/
	private Integer gender;

	/** 司机姓名 **/
	private String name;

	/** 司机年龄 **/
	private Integer age;
	/** 城市id **/
	private Integer cityId;

	/** 城市ID **/
	private Integer serviceCity;

	/** 城市名称 **/
	private String cityName;

	/** 供应商ID **/
	private Integer supplierId;

	/** 供应商名称 **/
	private String supplierName;

	/** 加盟类型ID **/
	private Byte cooperationType;

	/** 加盟类型名称 **/
	private String cooperationTypeName;

	/** 服务类型ID **/
	private Integer groupId;

	/** 服务类型名称 **/
	private String groupName;

	/** 现住址 **/
	private String currentAddress;

	/** 紧急联系人 **/
	private String emergencyContactPerson;

	/** 紧急联系方式 **/
	private String emergencyContactNumber;

	/** 身份证号 **/
	private String idCardNo;

	/** 服务监督码 **/
	private String superintendNo;

	/** 服务监督码链接 **/
	private String superintendUrl;

	/** 驾照类型,例：A1,B1,C1 **/
	private String drivingLicenseType;

	/** 驾龄 **/
	private Integer drivingYears;

	/** 档案编号 **/
	private String archivesNo;

	/** 领证日期 yyyy-MM-dd **/
	private Date issueDate;

	/** 驾照到期时间 yyyy-MM-dd **/
	private Date expireDate;

	/** 司机车牌号 **/
	private String licensePlates;

	/** 状态（1.有效 0.无效） **/
	private Integer status;

	/** 司机头像,图片url **/
	private String photosrct;

	/** 是否绑定信用卡 1绑定 0未绑定 **/
	private Integer isBindingCreditCard;

	/** 信用卡号 **/
	private String creditCardNo;

	/** 信用卡短卡号（块钱接口返回） **/
	private String storableCardNo;

	/** 信用卡开户行 **/
	private String creditOpenAccountBank;

	/** 快钱customerID,用于建立司机与信用卡的绑定关系，不能使用电话号码 **/
	private String quickpayCustomerid;

	/** imei **/
	private String imei;

	/** 机动车驾驶证号 **/
	private String driverlicensenumber;

	/** 驾驶证扫描件URL **/
	private String drivinglicenseimg;

	/** 初次领取驾驶证日期 yyyy-MM-dd **/
	private String firstdrivinglicensedate;

	/** 网络预约出租汽车驾驶员证初领日期 yyyy-MM-dd **/
	private String firstmeshworkdrivinglicensedate;

	/** 国籍 **/
	private String nationality;

	/** 户籍 **/
	private String householdregister;

	/** 驾驶员民族 **/
	private String nation;

	/** 驾驶员婚姻状况 **/
	private String marriage;

	/** 外语能力 (0无（默认） 1 英语 2 德语 3 法语 4 其他) **/
	private String foreignlanguage;

	/** 驾驶员通信地址 **/
	private String address;

	/** 驾驶员学历 **/
	private String education;

	/** 驾驶员合同（或协议）签署公司标识 yyyy-MM-dd **/
	private String corptype;

	/** 签署日期 yyyy-MM-dd **/
	private String signdate;

	/** 合同（或协议）到期时间 yyyy-MM-dd **/
	private String signdateend;

	/** 有效合同时间 yyyy-MM-dd **/
	private String contractdate;

	/** 是否巡游出租汽车驾驶员(1.是 0.否) **/
	private Integer isxydriver;

	/** 专职或兼职司机 **/
	private String parttimejobdri;

	/** 司机手机型号 **/
	private String phonetype;

	/** 司机手机运营商 **/
	private String phonecorp;

	/** 使用地图类型 **/
	private String maptype;

	/** 紧急情况联系人通讯地址 **/
	private String emergencycontactaddr;

	/** 评估，驾驶员服务质量信誉考核结果 **/
	private String assessment;

	/** 资格证有效起始日期，驾驶员证发证日期 yyyy-MM-dd **/
	private String driverlicenseissuingdatestart;

	/** 资格证有效截止日期，驾驶员证有效期止 yyyy-MM-dd **/
	private String driverlicenseissuingdateend;

	/** 网络预约出租汽车驾驶员证发证机构,驾驶员证发证机构 **/
	private String driverlicenseissuingcorp;

	/** 网络预约出租汽车驾驶员资格证号,网络预约出租汽车驾驶员证编号 **/
	private String driverlicenseissuingnumber;

	/** 巡游出租汽车驾驶员资格证号 **/
	private String xyDriverNumber;

	/** 注册日期 yyyy-MM-dd **/
	private String driverLicenseIssuingRegisterDate;

	/** 初次领取资格证日期 yyyy-MM-dd **/
	private String driverLicenseIssuingFirstDate;

	/** 资格证发证日期 yyyy-MM-dd **/
	private String driverLicenseIssuingGrantDate;

	/** 出生日期 yyyy-MM-dd **/
	private String birthDay;

	/** 户口登记机关名称 **/
	private String houseHoldRegisterPermanent;

	/** 司机是否维护形象 (0.未维护 1.已维护) **/
	private Byte isImage;

	/** 备注 **/
	private String memo;

	/** 创建人ID **/
	private Integer createBy;
	/** 创建人名称 **/
	private String createName;
	/** 创建时间 **/
	private Date createDate;
	/** 修改人ID **/
	private Integer updateBy;
	/** 修改人名称 **/
	private String updateName;
	/** 修改时间 **/
	private Date updateDate;

	/** 车队ID **/
	private Integer teamId;
	/** 车队名称 **/
	private String teamName;
	/** 车队下小组ID **/
	private Integer teamGroupId;
	/** 车队下小组名称 **/
	private String teamGroupName;

	/** 银行卡开户行 **/
	private String bankCardBank;
	/** 银行卡卡号 **/
	private String bankCardNumber;

	/** 高德IOS_IMEI **/
	private String ext6;

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getCityId() {return cityId;}

	public void setCityId(Integer cityId) {this.cityId = cityId;}

	public Integer getServiceCity() {
		return serviceCity;
	}

	public void setServiceCity(Integer serviceCity) {
		this.serviceCity = serviceCity;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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

	public Byte getCooperationType() {
		return cooperationType;
	}

	public void setCooperationType(Byte cooperationType) {
		this.cooperationType = cooperationType;
	}

	public String getCooperationTypeName() {
		return cooperationTypeName;
	}

	public void setCooperationTypeName(String cooperationTypeName) {
		this.cooperationTypeName = cooperationTypeName;
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

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPhotosrct() {
		return photosrct;
	}

	public void setPhotosrct(String photosrct) {
		this.photosrct = photosrct;
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

	public String getQuickpayCustomerid() {
		return quickpayCustomerid;
	}

	public void setQuickpayCustomerid(String quickpayCustomerid) {
		this.quickpayCustomerid = quickpayCustomerid;
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

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
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

	public Byte getIsImage() {
		return isImage;
	}

	public void setIsImage(Byte isImage) {
		this.isImage = isImage;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
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

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getTeamId() {
		return teamId;
	}

	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public Integer getTeamGroupId() {
		return teamGroupId;
	}

	public void setTeamGroupId(Integer teamGroupId) {
		this.teamGroupId = teamGroupId;
	}

	public String getTeamGroupName() {
		return teamGroupName;
	}

	public void setTeamGroupName(String teamGroupName) {
		this.teamGroupName = teamGroupName;
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

	public String getExt6() {
		return ext6;
	}

	public void setExt6(String ext6) {
		this.ext6 = ext6;
	}

}