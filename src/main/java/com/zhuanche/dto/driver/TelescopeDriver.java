package com.zhuanche.dto.driver;

public class TelescopeDriver implements java.io.Serializable{

    private static final long serialVersionUID = -3507726673832227461L;
    private String name;

    private String phone;

    private Integer driverStatus;

    private String idCardNo;

    private Integer cityId;

    private Integer supplierId;

    private Integer teamId;

    private Integer groupId;

    private String dataCityIds;

    private String dataSupplierIds;

    private String dataTeamIds;

    private String dataGroupIds;

    private Integer level;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(Integer driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getDataCityIds() {
        return dataCityIds;
    }

    public void setDataCityIds(String dataCityIds) {
        this.dataCityIds = dataCityIds;
    }

    public String getDataSupplierIds() {
        return dataSupplierIds;
    }

    public void setDataSupplierIds(String dataSupplierIds) {
        this.dataSupplierIds = dataSupplierIds;
    }

    public String getDataTeamIds() {
        return dataTeamIds;
    }

    public void setDataTeamIds(String dataTeamIds) {
        this.dataTeamIds = dataTeamIds;
    }

    public String getDataGroupIds() {
        return dataGroupIds;
    }

    public void setDataGroupIds(String dataGroupIds) {
        this.dataGroupIds = dataGroupIds;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "TelescopeDriver{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", driverStatus=" + driverStatus +
                ", idCardNo='" + idCardNo + '\'' +
                ", cityId=" + cityId +
                ", supplierId=" + supplierId +
                ", teamId=" + teamId +
                ", groupId=" + groupId +
                ", dataCityIds='" + dataCityIds + '\'' +
                ", dataSupplierIds='" + dataSupplierIds + '\'' +
                ", dataTeamIds='" + dataTeamIds + '\'' +
                ", dataGroupIds='" + dataGroupIds + '\'' +
                ", level=" + level +
                '}';
    }
}
