package com.zhuanche.entity.mdbcarmanage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VersionModel implements Serializable {

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

    /**
     * 版本更新记录附件列表
     */
    private List<CarBizSaasVersionDetail> versionDetailList;


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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public List<CarBizSaasVersionDetail> getVersionDetailList() {
        return versionDetailList;
    }

    public void setVersionDetailList(List<CarBizSaasVersionDetail> versionDetailList) {
        this.versionDetailList = versionDetailList;
    }
}
