package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.Feedback;
import com.zhuanche.entity.mdbcarmanage.FeedbackDoc;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

public class FeedBackDetailDto {

    private Integer id;

    private String feedbackContent;

    private Integer senderId;

    private String senderName;

    private Integer manageId;

    private String manageName;

    private Date createTime;

    private Date updateTime;

    private Integer manageStatus;

    private Date manageTime;

    private String manageContent;

    private List<FeedbackDoc> feedbackDocList;

    private Integer feedbackType;

    public static FeedBackDetailDto build(Feedback feedback,List<FeedbackDoc> feedbackDocs){
        FeedBackDetailDto feedBackDetailDto = new FeedBackDetailDto();

        BeanUtils.copyProperties(feedback, feedBackDetailDto);
        feedBackDetailDto.setFeedbackDocList(feedbackDocs);

        return feedBackDetailDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getManageId() {
        return manageId;
    }

    public void setManageId(Integer manageId) {
        this.manageId = manageId;
    }

    public String getManageName() {
        return manageName;
    }

    public void setManageName(String manageName) {
        this.manageName = manageName;
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

    public Integer getManageStatus() {
        return manageStatus;
    }

    public void setManageStatus(Integer manageStatus) {
        this.manageStatus = manageStatus;
    }

    public Date getManageTime() {
        return manageTime;
    }

    public void setManageTime(Date manageTime) {
        this.manageTime = manageTime;
    }

    public String getManageContent() {
        return manageContent;
    }

    public void setManageContent(String manageContent) {
        this.manageContent = manageContent;
    }

    public List<FeedbackDoc> getFeedbackDocList() {
        return feedbackDocList;
    }

    public void setFeedbackDocList(List<FeedbackDoc> feedbackDocList) {
        this.feedbackDocList = feedbackDocList;
    }

    public Integer getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(Integer feedbackType) {
        this.feedbackType = feedbackType;
    }
}
