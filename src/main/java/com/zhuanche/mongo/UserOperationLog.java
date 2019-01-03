package com.zhuanche.mongo;

import java.io.Serializable;
import java.util.Date;

/**用户操作日志**/
public class UserOperationLog implements Serializable {
	private static final long serialVersionUID = 3317672716224120984L;
	//用户ID
	private Integer userId;
	//用户名称
	private String userName;
	//用户登录账号
	private String loginName;
	//用户IP
	private String clientIp;

	//请求的URI
	private String requestUri;
	//请求的功能名称
	private String requestFuncName;
	//请求方法
	private String requestMethod;
	//请求类型
	private String requestType;
	//请求流水ID
	private String requestId;
	//请求的参数
	private String requestParams;
	//请求时间
	private Date requestTime;
	//请求耗时
	private long costTime;
	
	

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

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getRequestFuncName() {
		return requestFuncName;
	}

	public void setRequestFuncName(String requestFuncName) {
		this.requestFuncName = requestFuncName;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public long getCostTime() {
		return costTime;
	}

	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}
}