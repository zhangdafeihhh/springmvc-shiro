package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

/**
 * @Author fanht
 * @Description
 * @Date 2019/3/24 下午4:22
 * @Version 1.0
 */
public class CarBizSupplierTipsDto {

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

    /**文档名称**/
    private String docName;

    /**文档url**/
    private String docUrl;

    /**userId**/
    private Integer userId;

    /**创建人名称**/
    private String userName;


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


    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
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
    public String toString() {
        return "CarBizSupplierTipsDto{" +
                "id=" + id +
                ", tipsTitle='" + tipsTitle + '\'' +
                ", tipsContent='" + tipsContent + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", docName='" + docName + '\'' +
                ", docUrl='" + docUrl + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
