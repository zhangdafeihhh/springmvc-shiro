package com.zhuanche.mongo;

import java.io.Serializable;
import java.util.Date;

/**  
 * ClassName:SysSaveOrUpdateLog <br/>  
 * Date:     2019年5月5日 上午9:48:08 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class SysSaveOrUpdateLog implements Serializable {
	private static final long serialVersionUID = 8954576202978318584L;
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
		this.username = username;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getLogKey() {
		return logKey;
	}

	public void setLogKey(String logKey) {
		this.logKey = logKey;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getBeforeParams() {
		return beforeParams;
	}

	public void setBeforeParams(String beforeParams) {
		this.beforeParams = beforeParams;
	}

	public String getOperateParams() {
		return operateParams;
	}

	public void setOperateParams(String operateParams) {
		this.operateParams = operateParams;
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
		this.resultMsg = resultMsg;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
  
