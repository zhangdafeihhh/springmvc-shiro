package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class CarMessageGroup {
    private Long id;

    private String groupName;

    private String groupDesc;

    private Integer level;

    private String cities;

    private String suppliers;

    private String teamids;

    private Integer status;

    private String creater;

    private Integer createrId;

    private Date createTime;

    private Date updateTime;

    private String beginCreateTime;

    private String endCreateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc == null ? null : groupDesc.trim();
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities == null ? null : cities.trim();
    }

    public String getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(String suppliers) {
        this.suppliers = suppliers == null ? null : suppliers.trim();
    }

    public String getTeamids() {
        return teamids;
    }

    public void setTeamids(String teamids) {
        this.teamids = teamids == null ? null : teamids.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    public Integer getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Integer createrId) {
        this.createrId = createrId;
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

    public String getBeginCreateTime() {
        return beginCreateTime;
    }

    public void setBeginCreateTime(String beginCreateTime) {
        this.beginCreateTime = beginCreateTime;
    }

    public String getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(String endCreateTime) {
        this.endCreateTime = endCreateTime;
    }
}