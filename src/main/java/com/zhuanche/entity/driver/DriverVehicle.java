package com.zhuanche.entity.driver;

import java.math.BigDecimal;
import java.util.Date;

public class DriverVehicle {
    private Long id;

    private String vehicleName;

    private Byte status;

    private Integer vehicleType;

    private Long brandId;

    private String vehicleSpell;

    private Integer groupId;

    private Date createDate;

    private Date updateDate;

    private String remark;

    private String brandName;

    private BigDecimal originPrice;

    private String vehicleLevel;

    private Integer vehicleLength;

    private Integer axlesLength;

    private String vehicleStructure;

    private Integer engineDisplacement;

    private String airInflow;

    private String vehicleFuel;

    private String groupName;

    private Byte vehicleFlag;

    private Long modelId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName == null ? null : vehicleName.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getVehicleSpell() {
        return vehicleSpell;
    }

    public void setVehicleSpell(String vehicleSpell) {
        this.vehicleSpell = vehicleSpell == null ? null : vehicleSpell.trim();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public String getVehicleLevel() {
        return vehicleLevel;
    }

    public void setVehicleLevel(String vehicleLevel) {
        this.vehicleLevel = vehicleLevel == null ? null : vehicleLevel.trim();
    }

    public Integer getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(Integer vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public Integer getAxlesLength() {
        return axlesLength;
    }

    public void setAxlesLength(Integer axlesLength) {
        this.axlesLength = axlesLength;
    }

    public String getVehicleStructure() {
        return vehicleStructure;
    }

    public void setVehicleStructure(String vehicleStructure) {
        this.vehicleStructure = vehicleStructure == null ? null : vehicleStructure.trim();
    }

    public Integer getEngineDisplacement() {
        return engineDisplacement;
    }

    public void setEngineDisplacement(Integer engineDisplacement) {
        this.engineDisplacement = engineDisplacement;
    }

    public String getAirInflow() {
        return airInflow;
    }

    public void setAirInflow(String airInflow) {
        this.airInflow = airInflow == null ? null : airInflow.trim();
    }

    public String getVehicleFuel() {
        return vehicleFuel;
    }

    public void setVehicleFuel(String vehicleFuel) {
        this.vehicleFuel = vehicleFuel == null ? null : vehicleFuel.trim();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public Byte getVehicleFlag() {
        return vehicleFlag;
    }

    public void setVehicleFlag(Byte vehicleFlag) {
        this.vehicleFlag = vehicleFlag;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
}