package com.zhuanche.entity.rentcar;

import java.util.Date;

public class CarBizSupplier {
    private Integer supplierId;

    private String supplierNum;

    private String supplierFullName;

    private Integer supplierCity;

    private Integer type;

    private String contacts;

    private String contactsPhone;

    private Integer status;

    private Integer createBy;

    private Integer updateBy;

    private Date createDate;

    private Date updateDate;

    private Integer iscommission;

    private Integer pospayflag;

    private Integer cooperationType;

    private Integer istest;

    private Integer enterpriseType;

    private String address;

    private String memo;

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierNum() {
        return supplierNum;
    }

    public void setSupplierNum(String supplierNum) {
        this.supplierNum = supplierNum == null ? null : supplierNum.trim();
    }

    public String getSupplierFullName() {
        return supplierFullName;
    }

    public void setSupplierFullName(String supplierFullName) {
        this.supplierFullName = supplierFullName == null ? null : supplierFullName.trim();
    }

    public Integer getSupplierCity() {
        return supplierCity;
    }

    public void setSupplierCity(Integer supplierCity) {
        this.supplierCity = supplierCity;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts == null ? null : contacts.trim();
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone == null ? null : contactsPhone.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getIscommission() {
        return iscommission;
    }

    public void setIscommission(Integer iscommission) {
        this.iscommission = iscommission;
    }

    public Integer getPospayflag() {
        return pospayflag;
    }

    public void setPospayflag(Integer pospayflag) {
        this.pospayflag = pospayflag;
    }

    public Integer getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(Integer cooperationType) {
        this.cooperationType = cooperationType;
    }

    public Integer getIstest() {
        return istest;
    }

    public void setIstest(Integer istest) {
        this.istest = istest;
    }

    public Integer getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(Integer enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }
}