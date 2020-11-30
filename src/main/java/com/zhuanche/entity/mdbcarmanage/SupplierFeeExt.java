package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class SupplierFeeExt {
    private Integer id;

    private String fieldName;

    private String fieldValue;

    private Date createDate;

    private Date updateDate;

    private Integer supplierFeeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName == null ? null : fieldName.trim();
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue == null ? null : fieldValue.trim();
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

    public Integer getSupplierFeeId() {
        return supplierFeeId;
    }

    public void setSupplierFeeId(Integer supplierFeeId) {
        this.supplierFeeId = supplierFeeId;
    }
}