package com.zhuanche.dto.rentcar;

public class CarBizSupplierDTO {

    private Integer supplierId;

    private String supplierFullName;

    private Integer supplierCity;

    private String cityName;

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierFullName() {
        return supplierFullName;
    }

    public void setSupplierFullName(String supplierFullName) {
        this.supplierFullName = supplierFullName;
    }

    public Integer getSupplierCity() {
        return supplierCity;
    }

    public void setSupplierCity(Integer supplierCity) {
        this.supplierCity = supplierCity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}