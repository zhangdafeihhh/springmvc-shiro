package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/3/24 下午4:49
 * @Version 1.0
 */
public class CarBizSupplierTipsDetail{


    /**ID**/
    private Integer id;

    /**标题**/
    private String tipsTitle;

    /**内容**/
    private String tipsContent;

    /**创建时间**/
    private Date createTime;

    /**修改时间**/
    private Date updateTime;

    /**userId**/
    private Integer userId;

    /**创建人名称**/
    private String userName;


    /**阅读数**/
    private Integer readCount;

    /**附件详情**/
    private List<TipsDocDto> carBizTipsDocList;

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipsTitle() {
        return tipsTitle;
    }

    public void setTipsTitle(String tipsTitle) {
        this.tipsTitle = tipsTitle;
    }

    public String getTipsContent() {
        return tipsContent;
    }

    public void setTipsContent(String tipsContent) {
        this.tipsContent = tipsContent;
    }



    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<TipsDocDto> getCarBizTipsDocList() {
        return carBizTipsDocList;
    }

    public void setCarBizTipsDocList(List<TipsDocDto> carBizTipsDocList) {
        this.carBizTipsDocList = carBizTipsDocList;
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

    public CarBizSupplierTipsDetail(CarBizSupplierTips tips) {
        this.id = tips.getId();
        this.tipsTitle = tips.getTipsTitle();
        this.tipsContent = tips.getTipsContent();
        this.createTime = tips.getCreateTime();
        this.updateTime = tips.getUpdateTime();
        this.userId = tips.getUserId();
        this.readCount = tips.getReadCount();
    }
}
