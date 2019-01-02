package com.zhuanche.entity.rentcar;

import java.util.Set;

public class CarBizSupplierQuery extends CarBizSupplier {
    private Set<Integer> cityIds;
    private Set<Integer> supplierIds;

    public Set<Integer> getCityIds() {
        return cityIds;
    }

    public void setCityIds(Set<Integer> cityIds) {
        this.cityIds = cityIds;
    }

    public Set<Integer> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(Set<Integer> supplierIds) {
        this.supplierIds = supplierIds;
    }
}
