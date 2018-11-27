package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class CarMessageReceiver {
    private Long id;

    private Integer status;

    private Integer receiveUserId;

    private Integer messageId;

    private Date createTime;

    private Date updateTime;

    private String isSender;

    public enum  ReadStatus{

        read(1), //已读
        unRead(2);   //未读
        private int value;

        ReadStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
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

    public String getIsSender() {
        return isSender;
    }

    public void setIsSender(String isSender) {
        this.isSender = isSender == null ? null : isSender.trim();
    }

    @Override
    public String toString() {
        return "CarMessageReceiver{" +
                "id=" + id +
                ", status=" + status +
                ", receiveUserId=" + receiveUserId +
                ", messageId=" + messageId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isSender='" + isSender + '\'' +
                '}';
    }
}