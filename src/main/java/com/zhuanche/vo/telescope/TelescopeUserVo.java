package com.zhuanche.vo.telescope;

public class TelescopeUserVo implements java.io.Serializable{
    private static final long serialVersionUID = 9034555636821642409L;

    private Integer driverId;

    private Integer driverStatus;

    private Integer level;

    private Integer status;

    private String phone;

    private String name;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer teamId;

    private String teamName;

    private Integer teamGroupId;

    private String teamGroupName;

    private String permissionCityIds;

    private String permissionSupplierIds;

    private String permissionTeamIds;

    private String permissionTeamGroupIds;

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(Integer driverStatus) {
        this.driverStatus = driverStatus;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public String getPermissionCityIds() {
        return permissionCityIds;
    }

    public void setPermissionCityIds(String permissionCityIds) {
        this.permissionCityIds = permissionCityIds;
    }

    public String getPermissionSupplierIds() {
        return permissionSupplierIds;
    }

    public void setPermissionSupplierIds(String permissionSupplierIds) {
        this.permissionSupplierIds = permissionSupplierIds;
    }

    public String getPermissionTeamIds() {
        return permissionTeamIds;
    }

    public void setPermissionTeamIds(String permissionTeamIds) {
        this.permissionTeamIds = permissionTeamIds;
    }

    public String getPermissionTeamGroupIds() {
        return permissionTeamGroupIds;
    }

    public void setPermissionTeamGroupIds(String permissionTeamGroupIds) {
        this.permissionTeamGroupIds = permissionTeamGroupIds;
    }

    @Override
    public String toString() {
        return "TelescopeUserVo{" +
                "driverId=" + driverId +
                ", driverStatus=" + driverStatus +
                ", level=" + level +
                ", status=" + status +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", teamGroupId=" + teamGroupId +
                ", teamGroupName='" + teamGroupName + '\'' +
                ", permissionCityIds='" + permissionCityIds + '\'' +
                ", permissionSupplierIds=" + permissionSupplierIds +
                ", permissionTeamIds=" + permissionTeamIds +
                ", permissionTeamGroupIds=" + permissionTeamGroupIds +
                '}';
    }
}
