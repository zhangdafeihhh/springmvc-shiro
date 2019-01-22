package com.zhuanche.entity.rentcar;

import com.alibaba.fastjson.JSONArray;

public class CarBizSupplierVo extends CarBizSupplier {
    private String supplierCityName;
    private String dispatcherPhone;
    private String cooperationName;
    private Integer isTwoShifts;
    private String email;
    private String supplierShortName;
    private JSONArray groupList;
    private String createName;
    private String updateName;

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

    public JSONArray getGroupList() {
        return groupList;
    }

    public void setGroupList(JSONArray groupList) {
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
}
