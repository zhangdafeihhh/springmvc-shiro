package com.zhuanche.mongo.driverwidemongo;

/**
 * @author (yangbo)
 * @Date: 2019/4/9 10:56
 * @Description:(司机宽表基本信息)
 */
public class DriverWideInfoMongo {

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 车队名称
     */
    private String teamName;

    /**
     * 小组名称
     */
    private String teamGroupName;

    /**
     * 车辆类型名称
     */
    private String cooperationTypeName;

    /**
     * 服务类型名称
     */
    private String groupName;

    /**
     * 车型名称
     */
    private String modelName;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamGroupName() {
        return teamGroupName;
    }

    public void setTeamGroupName(String teamGroupName) {
        this.teamGroupName = teamGroupName;
    }

    public String getCooperationTypeName() {
        return cooperationTypeName;
    }

    public void setCooperationTypeName(String cooperationTypeName) {
        this.cooperationTypeName = cooperationTypeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
