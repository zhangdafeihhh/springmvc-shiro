package com.zhuanche.entity.driver;

import java.util.Date;

public class SysLog {
    private Integer sysLogId;

    private String username;

    private String module;

    private String logKey;

    private String method;

    private String beforeParams;

    private String operateParams;

    private Date startTime;

    private Date endTime;

    private Integer resultStatus;

    private String resultMsg;

    public Integer getSysLogId() {
        return sysLogId;
    }

    public void setSysLogId(Integer sysLogId) {
        this.sysLogId = sysLogId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    public String getLogKey() {
        return logKey;
    }

    public void setLogKey(String logKey) {
        this.logKey = logKey == null ? null : logKey.trim();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public String getBeforeParams() {
        return beforeParams;
    }

    public void setBeforeParams(String beforeParams) {
        this.beforeParams = beforeParams == null ? null : beforeParams.trim();
    }

    public String getOperateParams() {
        return operateParams;
    }

    public void setOperateParams(String operateParams) {
        this.operateParams = operateParams == null ? null : operateParams.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(Integer resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg == null ? null : resultMsg.trim();
    }
}