package com.zhuanche.shiro.realm;

import java.io.Serializable;

/**当前的登录用户信息**/
public  final class SSOLoginUser implements Serializable{
	private static final long serialVersionUID = -1373760761780841081L;
	//eg:   {"loginName":"wangjiaqi","mobile":"17600606250","name":"王佳琪","id":287,"type":2,"email":"wangjiaqi@01zhuanche.com","status":1}
	/**用户ID**/
	private long id;
	/**登录名**/
	private String loginName;
	/**手机号码**/
	private String mobile;
	/**真实姓名**/
	private String name;
	/**邮箱地址**/
	private String email;
	/**TODO：**/
	private long type;
	/**状态**/
	private long status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public long getStatus() {
		return status;
	}
	public void setStatus(long status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "SSOLoginUser [id=" + id + ", loginName=" + loginName + ", mobile=" + mobile + ", name=" + name + ", email=" + email + ", type=" + type + ", status=" + status + "]";
	}
}