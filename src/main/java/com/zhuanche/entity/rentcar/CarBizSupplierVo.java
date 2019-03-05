package com.zhuanche.entity.rentcar;

import java.util.List;

public class CarBizSupplierVo extends CarBizSupplier {
    private String supplierCityName;
    private String dispatcherPhone;
    private String cooperationName;
    private Integer isTwoShifts;
    private String email;
    private String supplierShortName;
    private List<GroupInfo> groupList;
    private String createName;
    private String updateName;
    private Integer twoLevelCooperation;
    private String twoLevelCooperationName;

    public String getSupplierCityName() {
        return supplierCityName;
    }

    public void setSupplierCityName(String supplierCityName) {
        this.supplierCityName = supplierCityName;
    }

    public String getDispatcherPhone() {
        return dispatcherPhone;
    }

    public void setDispatcherPhone(String dispatcherPhone) {
        this.dispatcherPhone = dispatcherPhone;
    }

    public String getCooperationName() {
        return cooperationName;
    }

    public void setCooperationName(String cooperationName) {
        this.cooperationName = cooperationName;
    }

    public Integer getIsTwoShifts() {
        return isTwoShifts;
    }

    public void setIsTwoShifts(Integer isTwoShifts) {
        this.isTwoShifts = isTwoShifts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSupplierShortName() {
        return supplierShortName;
    }

    public void setSupplierShortName(String supplierShortName) {
        this.supplierShortName = supplierShortName;
    }

    public List<GroupInfo> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupInfo> groupList) {
        this.groupList = groupList;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public Integer getTwoLevelCooperation() {
        return twoLevelCooperation;
    }

    public void setTwoLevelCooperation(Integer twoLevelCooperation) {
        this.twoLevelCooperation = twoLevelCooperation;
    }

    public String getTwoLevelCooperationName() {
        return twoLevelCooperationName;
    }

    public void setTwoLevelCooperationName(String twoLevelCooperationName) {
        this.twoLevelCooperationName = twoLevelCooperationName;
    }
}
