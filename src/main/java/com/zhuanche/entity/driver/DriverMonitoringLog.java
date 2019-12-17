package com.zhuanche.entity.driver;

import java.util.Date;

public class DriverMonitoringLog {
    private Long monitorId;

    private String msgid;

    private String operator;

    private String operationAccount;

    private Date createTime;

    private Date updateTime;

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid == null ? null : msgid.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getOperationAccount() {
        return operationAccount;
    }

    public void setOperationAccount(String operationAccount) {
        this.operationAccount = operationAccount == null ? null : operationAccount.trim();
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
}