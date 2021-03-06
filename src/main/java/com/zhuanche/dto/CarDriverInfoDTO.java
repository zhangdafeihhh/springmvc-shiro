package com.zhuanche.dto;

import java.io.Serializable;

public class CarDriverInfoDTO implements Serializable{

    private static final long serialVersionUID = -4252589023529950229L;

    private String driverId;

    private String phone;

    private Integer gender;

    private String carGroupName;

    private Integer cooperationType;

    private Integer groupId;

    private String name;

    private String serviceCity;

    private String licensePlates;//车牌号

    private String supplieId;

    private String supplierName;

    private Integer status;

    private String createByName;

    private String updateByName;

    private String updateBy;

    private String idCardNo;

    private Integer age;

    // 服务监督码
    private String superintendNo;

    private String currentAddress;

    private String emergencyContactNumber;

    private String emergencyContactPerson;

    private String drivingLicenseType;

    private String issueDate;

    private String expireDate;

    private String createDateStr;

    private String updateDateStr;

    private String createDate;

    /** 扩展字段*/
    private String teamName;

    private String pid;

    private String teamId;

    private String licensePlatesId;

    private Double currAccount;
    private Double hisallAccount;
    private Double withdrawDeposit;
    private Double frozenAmount;
    private String genderString;
    private String statusString;
    private String photoSrc;
    private Integer cityId;

    private Integer supplierId;

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getLicensePlatesId() {
        return licensePlatesId;
    }

    public void setLicensePlatesId(String licensePlatesId) {
        this.licensePlatesId = licensePlatesId;
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

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
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

    public String getCarGroupName() {
        return carGroupName;
    }

    public void setCarGroupName(String carGroupName) {
        this.carGroupName = carGroupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceCity() {
        return serviceCity;
    }

    public void setServiceCity(String serviceCity) {
        this.serviceCity = serviceCity;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getSupplieId() {
        return supplieId;
    }

    public void setSupplieId(String supplieId) {
        this.supplieId = supplieId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSuperintendNo() {
        return superintendNo;
    }

    public void setSuperintendNo(String superintendNo) {
        this.superintendNo = superintendNo;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public String getEmergencyContactPerson() {
        return emergencyContactPerson;
    }

    public void setEmergencyContactPerson(String emergencyContactPerson) {
        this.emergencyContactPerson = emergencyContactPerson;
    }

    public String getDrivingLicenseType() {
        return drivingLicenseType;
    }

    public void setDrivingLicenseType(String drivingLicenseType) {
        this.drivingLicenseType = drivingLicenseType;
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

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getUpdateDateStr() {
        return updateDateStr;
    }

    public void setUpdateDateStr(String updateDateStr) {
        this.updateDateStr = updateDateStr;
    }
}