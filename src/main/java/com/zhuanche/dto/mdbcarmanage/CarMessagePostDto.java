package com.zhuanche.dto.mdbcarmanage;

import java.util.Date;

/**
 * @Author fanht
 * @Description 查询已读未读草稿等返回给用户的列表
 * @Date 2018/11/24 下午1:38
 * @Version 1.0
 */
public class CarMessagePostDto {

    private Long id;
    /**消息标题**/
    private String mesageTitle;
    /**消息内容**/
    private String messageContent;
    /**创建人**/
    private String userName;
    /**附件名称**/
    private String docName;
    /**附件地址**/
    private String docUrl;
    /**创建时间*/
    private Date createTime;
    /**更新时间**/
    private Date updateTime;
    /**通知状态 已读未读**/
    private Integer status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMesageTitle() {
        return mesageTitle;
    }

    public void setMesageTitle(String mesageTitle) {
        this.mesageTitle = mesageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
