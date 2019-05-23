package com.zhuanche.entity.driver;

import java.util.Date;

import com.zhuanche.common.syslog.Column;
public class SysLogDTO {
    @Column(desc="记录ID")
    private Integer sysLogId;
    @Column(desc="操作人")
    private String username;
    @Column(desc="操作模块")
    private String module;
    @Column(desc="操作功能方法")
    private String method;
    @Column(desc="操作前数据信息")
    private String beforeParams;
    @Column(desc="操作后数据信息")
    private String operateParams;
    @Column(desc="操作开始时间")
    private Date startTime;
    @Column(desc="操作结束时间")
    private Date endTime;
    @Column(desc="操作状态")
    private Integer resultStatus;
    @Column(desc="操作返回值")
    private String resultMsg;
    
    private String remarks;

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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
    
    
}