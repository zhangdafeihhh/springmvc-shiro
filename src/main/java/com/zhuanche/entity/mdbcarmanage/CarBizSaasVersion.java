package com.zhuanche.entity.mdbcarmanage;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: nysspring@163.com
 * @Description: car_biz_saas_version
 * @Date: 18:33 2019/5/13
 */
public class CarBizSaasVersion implements Serializable {
    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 版本号
     */
    private String version;

    /**
     * 发版时间
     */
    private Date versionTakeEffectDate;

    /**
     * 版本概述
     */
    private String versionSummary;

    /**
     * 版本详细说明
     */
    private String versionDetail;

    /**
     * 1:正常2：无效
     */
    private Integer status;

    /**
     * 创建人id
     */
    private Integer createUserid;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 供应商城市id
     */
    private String cityId;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getVersionTakeEffectDate() {
        return versionTakeEffectDate;
    }

    public void setVersionTakeEffectDate(Date versionTakeEffectDate) {
        this.versionTakeEffectDate = versionTakeEffectDate;
    }

    public String getVersionSummary() {
        return versionSummary;
    }

    public void setVersionSummary(String versionSummary) {
        this.versionSummary = versionSummary;
    }

    public String getVersionDetail() {
        return versionDetail;
    }

    public void setVersionDetail(String versionDetail) {
        this.versionDetail = versionDetail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateUserid() {
        return createUserid;
    }

    public void setCreateUserid(Integer createUserid) {
        this.createUserid = createUserid;
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
        CarBizSaasVersion other = (CarBizSaasVersion) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getVersionTakeEffectDate() == null ? other.getVersionTakeEffectDate() == null : this.getVersionTakeEffectDate().equals(other.getVersionTakeEffectDate()))
            && (this.getVersionSummary() == null ? other.getVersionSummary() == null : this.getVersionSummary().equals(other.getVersionSummary()))
            && (this.getVersionDetail() == null ? other.getVersionDetail() == null : this.getVersionDetail().equals(other.getVersionDetail()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateUserid() == null ? other.getCreateUserid() == null : this.getCreateUserid().equals(other.getCreateUserid()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getUpdateDate() == null ? other.getUpdateDate() == null : this.getUpdateDate().equals(other.getUpdateDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getVersionTakeEffectDate() == null) ? 0 : getVersionTakeEffectDate().hashCode());
        result = prime * result + ((getVersionSummary() == null) ? 0 : getVersionSummary().hashCode());
        result = prime * result + ((getVersionDetail() == null) ? 0 : getVersionDetail().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateUserid() == null) ? 0 : getCreateUserid().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getUpdateDate() == null) ? 0 : getUpdateDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", version=").append(version);
        sb.append(", versionTakeEffectDate=").append(versionTakeEffectDate);
        sb.append(", versionSummary=").append(versionSummary);
        sb.append(", versionDetail=").append(versionDetail);
        sb.append(", status=").append(status);
        sb.append(", createUserid=").append(createUserid);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateDate=").append(updateDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}