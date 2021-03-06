package com.zhuanche.entity.bigdata;

import java.math.BigDecimal;

public class BiDriverDisinfectMeasureDay {
    private Integer id;

    private String dataDate;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer comOrderNum;

    private Integer factOverOrderNum;

    private BigDecimal factOverAmount;

    private Integer inUseDriverNum;

    private Integer orderNum;

    private Integer pushOrderNum;

    private Integer bindOrderNum;

    private Integer onlineDriverNum;

    private Integer totalDriverNum;

    private Integer bindOrderDriverCnt;

    private Integer disinfectDriverCnt;

    private Integer noDisinfectDriverCnt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate == null ? null : dataDate.trim();
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName == null ? null : cityName.trim();
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName == null ? null : supplierName.trim();
    }

    public Integer getComOrderNum() {
        return comOrderNum;
    }

    public void setComOrderNum(Integer comOrderNum) {
        this.comOrderNum = comOrderNum;
    }

    public Integer getFactOverOrderNum() {
        return factOverOrderNum;
    }

    public void setFactOverOrderNum(Integer factOverOrderNum) {
        this.factOverOrderNum = factOverOrderNum;
    }

    public BigDecimal getFactOverAmount() {
        return factOverAmount;
    }

    public void setFactOverAmount(BigDecimal factOverAmount) {
        this.factOverAmount = factOverAmount;
    }

    public Integer getInUseDriverNum() {
        return inUseDriverNum;
    }

    public void setInUseDriverNum(Integer inUseDriverNum) {
        this.inUseDriverNum = inUseDriverNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getPushOrderNum() {
        return pushOrderNum;
    }

    public void setPushOrderNum(Integer pushOrderNum) {
        this.pushOrderNum = pushOrderNum;
    }

    public Integer getBindOrderNum() {
        return bindOrderNum;
    }

    public void setBindOrderNum(Integer bindOrderNum) {
        this.bindOrderNum = bindOrderNum;
    }

    public Integer getOnlineDriverNum() {
        return onlineDriverNum;
    }

    public void setOnlineDriverNum(Integer onlineDriverNum) {
        this.onlineDriverNum = onlineDriverNum;
    }

    public Integer getTotalDriverNum() {
        return totalDriverNum;
    }

    public void setTotalDriverNum(Integer totalDriverNum) {
        this.totalDriverNum = totalDriverNum;
    }

    public Integer getBindOrderDriverCnt() {
        return bindOrderDriverCnt;
    }

    public void setBindOrderDriverCnt(Integer bindOrderDriverCnt) {
        this.bindOrderDriverCnt = bindOrderDriverCnt;
    }

    public Integer getDisinfectDriverCnt() {
        return disinfectDriverCnt;
    }

    public void setDisinfectDriverCnt(Integer disinfectDriverCnt) {
        this.disinfectDriverCnt = disinfectDriverCnt;
    }

    public Integer getNoDisinfectDriverCnt() {
        return noDisinfectDriverCnt;
    }

    public void setNoDisinfectDriverCnt(Integer noDisinfectDriverCnt) {
        this.noDisinfectDriverCnt = noDisinfectDriverCnt;
    }
}