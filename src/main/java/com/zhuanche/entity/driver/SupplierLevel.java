package com.zhuanche.entity.driver;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * supplier_level
 * @author 
 */
public class SupplierLevel implements Serializable {
    private Integer id;

    private Integer supplierId;

    private String supplierName;

    private Integer cityId;

    private String cityName;

    private Date startTime;

    private Date endTime;

    private String month;

    /**
     * 规模分
     */
    private BigDecimal scaleScore;

    /**
     * 服务分
     */
    private BigDecimal serviceScore;

    /**
     * 效率分
     */
    private BigDecimal efficiencyScore;

    /**
     * 附加分
     */
    private BigDecimal additionalScore;

    /**
     * 等级分
     */
    private BigDecimal gradeScore;

    private String gradeLevel;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getScaleScore() {
        return scaleScore;
    }

    public void setScaleScore(BigDecimal scaleScore) {
        this.scaleScore = scaleScore;
    }

    public BigDecimal getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(BigDecimal serviceScore) {
        this.serviceScore = serviceScore;
    }

    public BigDecimal getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(BigDecimal efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public BigDecimal getAdditionalScore() {
        return additionalScore;
    }

    public void setAdditionalScore(BigDecimal additionalScore) {
        this.additionalScore = additionalScore;
    }

    public BigDecimal getGradeScore() {
        return gradeScore;
    }

    public void setGradeScore(BigDecimal gradeScore) {
        this.gradeScore = gradeScore;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SupplierLevel other = (SupplierLevel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSupplierId() == null ? other.getSupplierId() == null : this.getSupplierId().equals(other.getSupplierId()))
            && (this.getSupplierName() == null ? other.getSupplierName() == null : this.getSupplierName().equals(other.getSupplierName()))
            && (this.getCityId() == null ? other.getCityId() == null : this.getCityId().equals(other.getCityId()))
            && (this.getCityName() == null ? other.getCityName() == null : this.getCityName().equals(other.getCityName()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getMonth() == null ? other.getMonth() == null : this.getMonth().equals(other.getMonth()))
            && (this.getScaleScore() == null ? other.getScaleScore() == null : this.getScaleScore().equals(other.getScaleScore()))
            && (this.getServiceScore() == null ? other.getServiceScore() == null : this.getServiceScore().equals(other.getServiceScore()))
            && (this.getEfficiencyScore() == null ? other.getEfficiencyScore() == null : this.getEfficiencyScore().equals(other.getEfficiencyScore()))
            && (this.getAdditionalScore() == null ? other.getAdditionalScore() == null : this.getAdditionalScore().equals(other.getAdditionalScore()))
            && (this.getGradeScore() == null ? other.getGradeScore() == null : this.getGradeScore().equals(other.getGradeScore()))
            && (this.getGradeLevel() == null ? other.getGradeLevel() == null : this.getGradeLevel().equals(other.getGradeLevel()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSupplierId() == null) ? 0 : getSupplierId().hashCode());
        result = prime * result + ((getSupplierName() == null) ? 0 : getSupplierName().hashCode());
        result = prime * result + ((getCityId() == null) ? 0 : getCityId().hashCode());
        result = prime * result + ((getCityName() == null) ? 0 : getCityName().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getMonth() == null) ? 0 : getMonth().hashCode());
        result = prime * result + ((getScaleScore() == null) ? 0 : getScaleScore().hashCode());
        result = prime * result + ((getServiceScore() == null) ? 0 : getServiceScore().hashCode());
        result = prime * result + ((getEfficiencyScore() == null) ? 0 : getEfficiencyScore().hashCode());
        result = prime * result + ((getAdditionalScore() == null) ? 0 : getAdditionalScore().hashCode());
        result = prime * result + ((getGradeScore() == null) ? 0 : getGradeScore().hashCode());
        result = prime * result + ((getGradeLevel() == null) ? 0 : getGradeLevel().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", supplierId=").append(supplierId);
        sb.append(", supplierName=").append(supplierName);
        sb.append(", cityId=").append(cityId);
        sb.append(", cityName=").append(cityName);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", month=").append(month);
        sb.append(", scaleScore=").append(scaleScore);
        sb.append(", serviceScore=").append(serviceScore);
        sb.append(", efficiencyScore=").append(efficiencyScore);
        sb.append(", additionalScore=").append(additionalScore);
        sb.append(", gradeScore=").append(gradeScore);
        sb.append(", gradeLevel=").append(gradeLevel);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}