package com.zhuanche.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

/**用户名、密码、短信验证码**/
public class UsernamePasswordMsgcodeToken extends UsernamePasswordToken {
	private static final long serialVersionUID = 100000000000000045L;
	
	public UsernamePasswordMsgcodeToken(final String username, final char[] password , final String msgcode) {
        super(username, password);
        this.msgcode = msgcode;
    }
	
	public UsernamePasswordMsgcodeToken(String msgcode) {
		super();
		this.msgcode = msgcode;
	}
	
	private String msgcode;
	public String getMsgcode() {
		return msgcode;
	}
	public void setMsgcode(String msgcode) {
		this.msgcode = msgcode;
	}

}
