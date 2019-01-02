package com.zhuanche.entity.rentcar;

public class CarBizSupplierVo extends CarBizSupplier {
    private String supplierCityName;
    private String dispatcherPhone;
    private String cooperationName;
    private Integer isTwoShifts;
    private String email;
    private String supplierShortName;

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
}
