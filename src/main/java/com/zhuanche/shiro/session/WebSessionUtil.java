package com.zhuanche.shiro.session;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.zhuanche.constants.SaasConst;
import com.zhuanche.shiro.realm.SSOLoginUser;
/**当前登录用户 工具类**/
public final class WebSessionUtil {
	/**获取当前的登录用户**/
	public static SSOLoginUser getCurrentLoginUser() {
		Subject subject = SecurityUtils.getSubject();
		return (SSOLoginUser) subject.getPrincipal();
	}
	/**是否为：车管后台-超级管理员**/
	public static boolean isSupperAdmin() {
		Subject subject = SecurityUtils.getSubject();
		return subject.hasRole(SaasConst.SYSTEM_ROLE);
//		return subject.hasRole("car_manage_admin");
	}
	
	/**设置会话属性**/
	public static void setAttribute(String key, Object value) {
		Subject subject = SecurityUtils.getSubject();
		if(subject!=null) {
			subject.getSession().setAttribute(key, value);
		}
	}
	/**移除会话属性**/
	public static void removeAttribute(String key) {
		Subject subject = SecurityUtils.getSubject();
		if(subject!=null) {
			subject.getSession().removeAttribute(key);
		}
	}
	/**查询会话属性**/
	public static Object getAttribute(String key) {
		Subject subject = SecurityUtils.getSubject();
		if(subject!=null) {
			return subject.getSession().getAttribute(key);
		}
		return null;
	}
}