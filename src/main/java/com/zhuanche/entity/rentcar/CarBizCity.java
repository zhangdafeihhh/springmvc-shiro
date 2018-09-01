package com.zhuanche.entity.rentcar;

import java.util.Date;

public class CarBizCity {
    private Integer cityId;

    private String cityName;

    private String code;

    private String cityspell;

    private Integer status;

    private Integer testStatus;

    private Integer sort;

    private Integer createBy;

    private Integer updateBy;

    private Date createDate;

    private Date updateDate;

    private String centrelo;

    private String centrela;

    private String centrelobd;

    private String centrelabd;

    private Integer serviceStatus;

    private Integer multiStatus;

    private String regionNumber;

    private Integer cooperationCity;

    private Integer invoice;

    private Integer startaddr;

    private Integer isPostpaid;

    private Integer isShowTime;

    private String memo;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCityspell() {
        return cityspell;
    }

    public void setCityspell(String cityspell) {
        this.cityspell = cityspell == null ? null : cityspell.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Integer testStatus) {
        this.testStatus = testStatus;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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

    public String getCentrelo() {
        return centrelo;
    }

    public void setCentrelo(String centrelo) {
        this.centrelo = centrelo == null ? null : centrelo.trim();
    }

    public String getCentrela() {
        return centrela;
    }

    public void setCentrela(String centrela) {
        this.centrela = centrela == null ? null : centrela.trim();
    }

    public String getCentrelobd() {
        return centrelobd;
    }

    public void setCentrelobd(String centrelobd) {
        this.centrelobd = centrelobd == null ? null : centrelobd.trim();
    }

    public String getCentrelabd() {
        return centrelabd;
    }

    public void setCentrelabd(String centrelabd) {
        this.centrelabd = centrelabd == null ? null : centrelabd.trim();
    }

    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getMultiStatus() {
        return multiStatus;
    }

    public void setMultiStatus(Integer multiStatus) {
        this.multiStatus = multiStatus;
    }

    public String getRegionNumber() {
        return regionNumber;
    }

    public void setRegionNumber(String regionNumber) {
        this.regionNumber = regionNumber == null ? null : regionNumber.trim();
    }

    public Integer getCooperationCity() {
        return cooperationCity;
    }

    public void setCooperationCity(Integer cooperationCity) {
        this.cooperationCity = cooperationCity;
    }

    public Integer getInvoice() {
        return invoice;
    }

    public void setInvoice(Integer invoice) {
        this.invoice = invoice;
    }

    public Integer getStartaddr() {
        return startaddr;
    }

    public void setStartaddr(Integer startaddr) {
        this.startaddr = startaddr;
    }

    public Integer getIsPostpaid() {
        return isPostpaid;
    }

    public void setIsPostpaid(Integer isPostpaid) {
        this.isPostpaid = isPostpaid;
    }

    public Integer getIsShowTime() {
        return isShowTime;
    }

    public void setIsShowTime(Integer isShowTime) {
        this.isShowTime = isShowTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }
}