package com.zhuanche.dto.rentcar;

import java.io.Serializable;
import java.util.Date;

public class CarSysMobileClientPublishDTO implements Serializable{


    private static final long serialVersionUID = -8285519195765080714L;

    private Integer versionId;

    private String versionName;

    private String versionNo;

    private Integer platform;

    private Integer forcedUpdate;

    private Integer forcedUpdateDown;

    private Integer publisher;

    private Date publishDate;

    private String memo;

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

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
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

}