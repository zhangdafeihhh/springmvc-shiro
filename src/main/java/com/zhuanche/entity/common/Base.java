package com.zhuanche.entity.common;

import java.io.Serializable;
import java.util.Set;

public class Base implements Serializable {

    private static final long serialVersionUID = 4959453183678167556L;

    //数据权限控制字段
    private Set<Integer> cityIds;//可以管理的所有城市ID
    private Set<Integer> supplierIds;//可以管理的所有供应商ID
    private Set<Integer> teamIds;//可以管理的所有车队ID
    //查询条件
    private String driverIds;//司机IDS
    //页面展示
    private String cityName;//城市名称
    private String supplierName;//供应商名称
    private Integer teamId;//车队ID
    private String teamName;//车队名称
    private Integer teamGroupId;//车队下小组ID
    private String teamGroupName;//车队下小组名称
    private String createName;//创建人名称
    private String updateName;//修改人名称

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

    public Set<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Set<Integer> teamIds) {
        this.teamIds = teamIds;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getTeamGroupId() {
        return teamGroupId;
    }

    public void setTeamGroupId(Integer teamGroupId) {
        this.teamGroupId = teamGroupId;
    }

    public String getTeamGroupName() {
        return teamGroupName;
    }

    public void setTeamGroupName(String teamGroupName) {
        this.teamGroupName = teamGroupName;
    }

    public String getDriverIds() {
        return driverIds;
    }

    public void setDriverIds(String driverIds) {
        this.driverIds = driverIds;
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

