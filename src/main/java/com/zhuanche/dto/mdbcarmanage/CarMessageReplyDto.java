package com.zhuanche.dto.mdbcarmanage;

import java.util.List;
import java.util.Set;

/**
 * @Author fanht
 * @Description 消息回复dto
 * @Date 2020/3/1 上午10:58
 * @Version 1.0
 */
public class CarMessageReplyDto {

    private Integer messageId;

    private String noticeStartTime;

    private String noticeEndTime;

    private String createStartTime;

    private String createEndTime;

    private String replyStartTime;

    private String replyEndTime;

    private Integer status;

    private List<Integer> receiveUserIds;

    private String replyContent;

    private String senderName;


    private String createTimeStr;

    private String readTimeStr;

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getReadTimeStr() {
        return readTimeStr;
    }

    public void setReadTimeStr(String readTimeStr) {
        this.readTimeStr = readTimeStr;
    }

    public String getReplyTimeStr() {
        return replyTimeStr;
    }

    public void setReplyTimeStr(String replyTimeStr) {
        this.replyTimeStr = replyTimeStr;
    }

    private String replyTimeStr;



    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getNoticeStartTime() {
        return noticeStartTime;
    }

    public void setNoticeStartTime(String noticeStartTime) {
        this.noticeStartTime = noticeStartTime;
    }

    public String getNoticeEndTime() {
        return noticeEndTime;
    }

    public void setNoticeEndTime(String noticeEndTime) {
        this.noticeEndTime = noticeEndTime;
    }

    public String getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(String createStartTime) {
        this.createStartTime = createStartTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getReplyStartTime() {
        return replyStartTime;
    }

    public void setReplyStartTime(String replyStartTime) {
        this.replyStartTime = replyStartTime;
    }

    public String getReplyEndTime() {
        return replyEndTime;
    }

    public void setReplyEndTime(String replyEndTime) {
        this.replyEndTime = replyEndTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getReceiveUserIds() {
        return receiveUserIds;
    }

    public void setReceiveUserIds(List<Integer> receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
