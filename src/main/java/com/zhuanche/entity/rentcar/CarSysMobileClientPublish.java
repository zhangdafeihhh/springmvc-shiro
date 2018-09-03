package com.zhuanche.entity.rentcar;

import java.io.Serializable;
import java.util.Date;

public class CarSysMobileClientPublish implements Serializable{


    private static final long serialVersionUID = -8285519195765080714L;

    private Integer versionId;

    private String versionName;

    private String versionNo;

    private Integer type;

    private Integer platform;

    private String downloadUrl;

    private Integer forcedUpdate;

    private Integer forcedUpdateDown;

    private String channelNum;

    private Integer publisher;

    private Date publishDate;

    private String memo;

    private Integer status;

    private Integer createBy;

    private Date createDate;

    private Integer updateBy;

    private Date updateDate;

    private String cityNames;

    private String supplierStr;

    private String effectiveDate;

    private String sortsNum;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName == null ? null : versionName.trim();
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo == null ? null : versionNo.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl == null ? null : downloadUrl.trim();
    }

    public Integer getForcedUpdate() {
        return forcedUpdate;
    }

    public void setForcedUpdate(Integer forcedUpdate) {
        this.forcedUpdate = forcedUpdate;
    }

    public Integer getForcedUpdateDown() {
        return forcedUpdateDown;
    }

    public void setForcedUpdateDown(Integer forcedUpdateDown) {
        this.forcedUpdateDown = forcedUpdateDown;
    }

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum == null ? null : channelNum.trim();
    }

    public Integer getPublisher() {
        return publisher;
    }

    public void setPublisher(Integer publisher) {
        this.publisher = publisher;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCityNames() {
        return cityNames;
    }

    public void setCityNames(String cityNames) {
        this.cityNames = cityNames == null ? null : cityNames.trim();
    }

    public String getSupplierStr() {
        return supplierStr;
    }

    public void setSupplierStr(String supplierStr) {
        this.supplierStr = supplierStr == null ? null : supplierStr.trim();
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate == null ? null : effectiveDate.trim();
    }

    public String getSortsNum() {
        return sortsNum;
    }

    public void setSortsNum(String sortsNum) {
        this.sortsNum = sortsNum == null ? null : sortsNum.trim();
    }
}