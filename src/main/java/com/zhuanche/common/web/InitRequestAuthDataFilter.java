package com.zhuanche.common.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**对每一个请求，获取当前登录用户信息，以用于日志输出、页面渲染
 * @author zhaoyali
 **/
public class InitRequestAuthDataFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
		//一、当前登录用户(用于日志输出)
		SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
		if(loginUser==null) {
			MDC.put("loginUser", "【游客身份】");
		}else {
			MDC.put("loginUser", "【"+ loginUser.getId() +"-"+ loginUser.getName() +"-"+ loginUser.getLoginName()+"】");
		}
		//二、用于页面渲染
		request.setAttribute("currentLoginUser", loginUser );
		
		filterChain.doFilter(request, response);
	}
}