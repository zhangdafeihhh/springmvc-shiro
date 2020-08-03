package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class InterCityEchelon {
    private Integer id;

    private Integer teamId;

    private Integer sort;

    private String echelonDate;

    private Date createTime;

    private Date updateTime;

    private String echelonName;

    private String echelonMonth;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getEchelonDate() {
        return echelonDate;
    }

    public void setEchelonDate(String echelonDate) {
        this.echelonDate = echelonDate == null ? null : echelonDate.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getEchelonName() {
        return echelonName;
    }

    public void setEchelonName(String echelonName) {
        this.echelonName = echelonName == null ? null : echelonName.trim();
    }

    public String getEchelonMonth() {
        return echelonMonth;
    }

    public void setEchelonMonth(String echelonMonth) {
        this.echelonMonth = echelonMonth == null ? null : echelonMonth.trim();
    }
}