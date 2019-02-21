package com.zhuanche.util.objcompare.entity;

import com.zhuanche.util.objcompare.FieldNote;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

/**
 * @program: mp-manage
 * @description: 对比操作记录
 * @author: niuzilian
 * @create: 2019-01-14 18:04
 **/
public class BusDriverCompareEntity {
    @FieldNote("城市")
    private String cityName;
    @FieldNote("供应商")
    private String supplierName;
    @FieldNote("司机姓名")
    private String name;
    @FieldNote("车型类别")
    private String groupName;
    @FieldNote("身份证号")
    private String idCardNo;
    @FieldNote("手机号")
    private String phone;
    /** 司机性别 (1.男;0.女) **/
    @FieldNote("性别")
    private String gender;
    @FieldNote("出生日期")
    private String birthDay;
    @FieldNote("驾照类型")
    private String drivingLicenseType;
    /** 机动车驾驶证号 **/
    @FieldNote("驾驶证号")
    private String driverlicensenumber;
    /** 初次领取驾驶证日期 yyyy-MM-dd **/
    @FieldNote(value = "驾驶证领证日期",pattern = FieldNote.Pattern.DATE)
    private Date issueDate;
    @FieldNote(value = "道路运输从业资格证编号",pattern = FieldNote.Pattern.DATE)
    private String xyDriverNumber;
    //状态（1.有效 0.无效）
    @FieldNote(value = "状态",pattern = FieldNote.Pattern.DATE)
    private String status;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
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

    public String getXyDriverNumber() {
        return xyDriverNumber;
    }

    public void setXyDriverNumber(String xyDriverNumber) {
        this.xyDriverNumber = xyDriverNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
