package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class CarDriverTeam {
    private Integer id;

    private String teamName;

    private String sort;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;

    private String city;

    private String supplier;

    private String remark;

    private Boolean status;

    private Integer pId;

    private String dutyStartDate;

    private String dutyEndDate;

    private String charge1;

    private String charge2;

    private String charge3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName == null ? null : teamName.trim();
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort == null ? null : sort.trim();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier == null ? null : supplier.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
    }

    public String getDutyStartDate() {
        return dutyStartDate;
    }

    public void setDutyStartDate(String dutyStartDate) {
        this.dutyStartDate = dutyStartDate == null ? null : dutyStartDate.trim();
    }

    public String getDutyEndDate() {
        return dutyEndDate;
    }

    public void setDutyEndDate(String dutyEndDate) {
        this.dutyEndDate = dutyEndDate == null ? null : dutyEndDate.trim();
    }

    public String getCharge1() {
        return charge1;
    }

    public void setCharge1(String charge1) {
        this.charge1 = charge1 == null ? null : charge1.trim();
    }

    public String getCharge2() {
        return charge2;
    }

    public void setCharge2(String charge2) {
        this.charge2 = charge2 == null ? null : charge2.trim();
    }

    public String getCharge3() {
        return charge3;
    }

    public void setCharge3(String charge3) {
        this.charge3 = charge3 == null ? null : charge3.trim();
    }
}