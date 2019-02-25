package com.zhuanche.entity.driverPreparate;

import java.io.Serializable;

public class CarBizReportPictureExt implements Serializable{

    private static final long serialVersionUID = -455082020089017315L;
    /**
     * 图片id
     */
    private Integer id;
    /**
     * 图片名称
     */
    private String pictureName;
    /**
     * 图片地址
     */
    private String pictureUrl;
    /**
     * 报备id
     */
    private Integer reportId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName == null ? null : pictureName.trim();
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl == null ? null : pictureUrl.trim();
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
}