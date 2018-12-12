package com.zhuanche.dto.driver;

public class TelescopeDriverInfo implements java.io.Serializable{

    private static final long serialVersionUID = -792956995002084292L;

    private Integer driverId; //		司机ID
    private String name;//			司机姓名
    private String licensePlates;//			司机车牌号
    private Integer serviceCity;//			城市ID
    private String cityName;//			城市名称
    private Integer supplierId;//			供应商ID
    private String supplierName;//			供应商名称
    private Integer teamId;//			车队ID
    private String teamName;//			车队名称
    private Integer teamGroupId;//			车队下小组ID
    private String teamGroupName;//			车队下小组名称
    private boolean auth;//是否有千里眼权限

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public Integer getServiceCity() {
        return serviceCity;
    }

    public void setServiceCity(Integer serviceCity) {
        this.serviceCity = serviceCity;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "TelescopeDriverInfo{" +
                "driverId=" + driverId +
                ", name='" + name + '\'' +
                ", licensePlates='" + licensePlates + '\'' +
                ", serviceCity=" + serviceCity +
                ", cityName='" + cityName + '\'' +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", teamGroupId=" + teamGroupId +
                ", teamGroupName='" + teamGroupName + '\'' +
                ", auth=" + auth +
                '}';
    }
}
