package com.zhuanche.entity.driver;


public class CarBizDriverInfo {

	private static final long serialVersionUID = 7868838087731509604L;
	
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
	public String getBankid() {
		return bankid;
	}
	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
	public String getQuickpayCustomerid() {
		return quickpayCustomerid;
	}
	public void setQuickpayCustomerid(String quickpayCustomerid) {
		this.quickpayCustomerid = quickpayCustomerid;
	}
	public Integer getPasswordReset() {
		return passwordReset;
	}
	public void setPasswordReset(Integer passwordReset) {
		this.passwordReset = passwordReset;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public String getTeamIds() {
		return teamIds;
	}
	public void setTeamIds(String teamIds) {
		this.teamIds = teamIds;
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
	
	public String getLicensePlatesId() {
		return licensePlatesId;
	}
	public void setLicensePlatesId(String licensePlatesId) {
		this.licensePlatesId = licensePlatesId;
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
	public String getDrivingTypeString() {
		return drivingTypeString;
	}
	public void setDrivingTypeString(String drivingTypeString) {
		this.drivingTypeString = drivingTypeString;
	}
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
	public void setCVN2(String cVN2) {
		CVN2 = cVN2;
	}
	public String getShortCardNo() {
		return shortCardNo;
	}
	public void setShortCardNo(String shortCardNo) {
		this.shortCardNo = shortCardNo;
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
	public String getServiceCity() {
		return serviceCity;
	}
	public void setServiceCity(String serviceCity) {
		this.serviceCity = serviceCity;
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
}
