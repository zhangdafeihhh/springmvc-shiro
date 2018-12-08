package com.zhuanche.dto.busManage;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.zhuanche.common.web.datavalidate.custom.InArray;

/**
 * car_biz_driver_info
 */
public class BusDriverSaveDTO extends BusBaseStatisDTO {

	// =====================接口入参字段====================
	/** 服务城市 */
	@NotNull(message = "服务城市不能为空")
	private Integer serviceCity;

	/** 供应商 **/
	@NotNull(message = "供应商不能为空")
	private Integer supplierId;

	/** 服务类型car_biz_car_group **/
	@NotNull(message = "车型类别不能为空")
	private Integer groupId;

	/** 司机姓名 **/
	@NotBlank(message = "司机姓名不能为空")
	private String name;
	
	/** 司机性别，[1.男0.女] **/
	@NotNull(message = "司机性别不能为空")
	@InArray(values = { "0", "1" }, message = "司机性别不在有效范围内")
	private Integer gender;

	/** 身份证号 **/
	@NotBlank(message = "身份证号不能为空")
	private String idCardNo;

	/** 手机号 **/
	@NotBlank(message = "司机手机号不能为空")
	@Length(min = 11, max = 11, message = "司机手机号必须为11位")
	private String phone;

	/** 出生日期 **/
	@NotBlank(message = "出生日期不能为空")
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN)
	private String birthDay;

	/** 驾照类型 **/
	@NotBlank(message = "驾照类型不能为空")
	@InArray(values = { "A1", "A2", "A3", "B1", "B2", "C1", "C2", "N", "P" }, message = "驾照类型不在有效范围内")
	private String drivingLicenseType;

	/** 机动车驾驶证号 **/
	@NotBlank(message = "机动车驾驶证号不能为空")
	private String driverlicensenumber;

	/** 驾照领证日期 **/
	@NotNull(message = "驾照领证日期不能为空")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date issueDate;
	
	@NotBlank(message = "道路运输从业资格证编号不能为空")
	/** 道路运输从业资格证编号(巡游出租汽车驾驶员资格证号,巴士业务无“巡游...”业务，复用此字段) **/
	private String xyDriverNumber;
	/** 是否巡游出租汽车驾驶员 **/
	private Integer isxydriver = 1;// 巴士业务默认为"是"

	// ======================其它业务信息字段==================
	/** 司机Id,修改时必传 **/
	private Integer driverId;

	/** 密码重置 修改时传值[1.是 2.否] **/
	@InArray(values = { "1", "2" }, message = "密码重置标识不在有效范围内")
	private Integer passwordReset;

	/** 司机状态 (0:无效, 1:有效) **/
	@InArray(values = { "0", "1" }, message = "司机状态不在有效范围内")
	private Integer status;

	/** 密码 **/
	private String password;

	/** 年龄 **/
	@Min(value = 18, message = "年龄不能小于18岁")
	private Integer age;

	/** 车牌号 **/
	private String licensePlates;

	/** 现住址 **/
	private String currentAddress;

	/** 紧急联系人 **/
	private String emergencyContactPerson;

	/** 紧急联系方式 **/
	@Length(max = 11, message = "紧急联系方式不可超过11位")
	private String emergencyContactNumber;

	/** 服务监督码 **/
	private String superintendNo;

	/** 服务监督码链接 **/
	private String superintendUrl;

	/** 驾照到期时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date expireDate;

	/** 司机头像 **/
	private String photosrct;

	/** 驾驶证扫描件URL **/
	private String drivinglicenseimg;

	/** 国籍 **/
	private String nationality;

	/** 驾驶员民族 **/
	private String nation;

	/** 户籍 **/
	private String householdregister;

	/** 户口登记机关名称 **/
	private String houseHoldRegisterPermanent;

	/** 驾驶员婚姻状况 **/
	private String marriage;

	/** 外语能力(0无（默认） 1 英语 2 德语 3 法语 4 其他) **/
	@InArray(values = { "0", "1", "2", "3", "4" }, message = "外语能力不在有效范围内")
	private String foreignlanguage;

	/** 驾驶员学历 **/
	private String education;

	/** 初次领取驾驶证日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "初次领取驾驶证日期字符串格式不正确")
	private String firstdrivinglicensedate;

	/** 网络预约出租汽车驾驶员证初领日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "网络预约出租汽车驾驶员证初领日期字符串格式不正确")
	private String firstmeshworkdrivinglicensedate;

	/** 驾驶员合同（或协议）签署公司标识 **/
	private String corptype;

	/** 签署日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "签署日期字符串格式不正确")
	private String signdate;

	/** 合同（或协议）到期时间 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "合同（或协议）到期时间字符串格式不正确")
	private String signdateend;

	/** 有效合同时间 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "有效合同时间字符串格式不正确")
	private String contractdate;

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

	/** 驾驶员服务质量信誉考核结果 **/
	private String assessment;

	/** 驾驶员证发证日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "驾驶员证发证日期字符串格式不正确")
	private String driverlicenseissuingdatestart;

	/** 驾驶员证有效期截止日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "驾驶员证有效期截止日期字符串格式不正确")
	private String driverlicenseissuingdateend;

	/** 驾驶员证发证机构 **/
	private String driverlicenseissuingcorp;

	/** 网络预约出租汽车驾驶员证编号 **/
	private String driverlicenseissuingnumber;

	/** 注册日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "注册日期字符串格式不正确")
	private String driverLicenseIssuingRegisterDate;

	/** 初次领取资格证日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "初次领取资格证日期字符串格式不正确")
	private String driverLicenseIssuingFirstDate;

	/** 资格证发证日期 **/
	@Pattern(regexp = PATTERN_DATE_BY_HYPHEN, message = "资格证发证日期字符串格式不正确")
	private String driverLicenseIssuingGrantDate;

	/** 备注 **/
	private String memo;

	/** 银行卡开户行 **/
	private String bankCardBank;
	/** 银行卡卡号 **/
	private String bankCardNumber;
	/** 开户银行 **/
	private String accountBank;
	/** 档案编号 **/
	private String archivesNo;
	/** 附件地址 **/
	private String attachmentAddr;
	/** 附件名称 **/
	private String attachmentName;
	/** 银行账号 **/
	private String bankAccountNo;
	/** 驾龄 **/
	private Integer drivingYears;

	// =========================交通委需要修改以下信息的记录==========================
	private String oldPhone;// 手机号
	private String oldIdCardNo;// 身份证
	private String oldDriverLicenseNumber;// 机动车驾驶证号
	private String oldDriverLicenseIssuingNumber;// 网络预约出租汽车驾驶员资格证号
	private String oldLicensePlates;// 车牌号
	private Integer oldCity;// 城市
	private Integer oldSupplier;// 供应商

	// ====================getter/setter==================

	public Integer getServiceCity() {
		return serviceCity;
	}

	public void setServiceCity(Integer serviceCity) {
		this.serviceCity = serviceCity;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
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

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getDrivingLicenseType() {
		return drivingLicenseType;
	}

	public void setDrivingLicenseType(String drivingLicenseType) {
		this.drivingLicenseType = drivingLicenseType;
	}

	public String getDriverlicensenumber() {
		return driverlicensenumber;
	}

	public void setDriverlicensenumber(String driverlicensenumber) {
		this.driverlicensenumber = driverlicensenumber;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPasswordReset() {
		return passwordReset;
	}

	public void setPasswordReset(Integer passwordReset) {
		this.passwordReset = passwordReset;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
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

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getPhotosrct() {
		return photosrct;
	}

	public void setPhotosrct(String photosrct) {
		this.photosrct = photosrct;
	}

	public String getDrivinglicenseimg() {
		return drivinglicenseimg;
	}

	public void setDrivinglicenseimg(String drivinglicenseimg) {
		this.drivinglicenseimg = drivinglicenseimg;
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

	public String getHouseholdregister() {
		return householdregister;
	}

	public void setHouseholdregister(String householdregister) {
		this.householdregister = householdregister;
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

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
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

	public String getXyDriverNumber() {
		return xyDriverNumber;
	}

	public void setXyDriverNumber(String xyDriverNumber) {
		this.xyDriverNumber = xyDriverNumber;
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

	public String getHouseHoldRegisterPermanent() {
		return houseHoldRegisterPermanent;
	}

	public void setHouseHoldRegisterPermanent(String houseHoldRegisterPermanent) {
		this.houseHoldRegisterPermanent = houseHoldRegisterPermanent;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public String getAccountBank() {
		return accountBank;
	}

	public void setAccountBank(String accountBank) {
		this.accountBank = accountBank;
	}

	public String getArchivesNo() {
		return archivesNo;
	}

	public void setArchivesNo(String archivesNo) {
		this.archivesNo = archivesNo;
	}

	public String getAttachmentAddr() {
		return attachmentAddr;
	}

	public void setAttachmentAddr(String attachmentAddr) {
		this.attachmentAddr = attachmentAddr;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	public Integer getDrivingYears() {
		return drivingYears;
	}

	public void setDrivingYears(Integer drivingYears) {
		this.drivingYears = drivingYears;
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

}