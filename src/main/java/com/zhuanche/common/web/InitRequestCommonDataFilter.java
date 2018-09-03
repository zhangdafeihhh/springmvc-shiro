package com.zhuanche.common.web;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**对每一个请求，初始化各种变量及参数，以用于日志输出、展现层输出等目的
 * @author zhaoyali
 **/
public class InitRequestCommonDataFilter extends OncePerRequestFilter {
	private static String staticResourceVersion; //静态资源版本号
	private static String ssoLogoutUrl; //SSO单点登出URL
	private static String cmsLogoutUrl; //SSO单点登出本地回调URL
	
	private static final String SEED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final Random rnd = new  SecureRandom();
	private String genRequestId( int length ) {
		StringBuffer sb = new StringBuffer( length  );
		for(int i=0;i<length;i++) {
			int index = rnd.nextInt( SEED_CHARS.length()  );
			char cha = SEED_CHARS.charAt(index);
			sb.append(cha);
		}
		return sb.toString();
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
		/***************支持跨域请求BEGIN***********************/
		String Origin = request.getHeader("Origin");
		if(StringUtils.isNotEmpty(Origin)) {
			response.setHeader("Access-Control-Allow-Origin", Origin);
		}else {
			response.setHeader("Access-Control-Allow-Origin", "*");
		}
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "1800");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");		
		/***************支持跨域请求END***********************/
		
		
		//一、请求流水号(用于日志,实现对请求进行统一编号，方便于进行排查业务日志)
		String reqId = request.getParameter("x_requestId");
		if(reqId==null || "".equals(reqId.trim())  ) {
			reqId = this.genRequestId(6);
		}
		MDC.put("reqId", reqId);
		
		//二、静态资源版本号(用于页面渲染, 解决客户端浏览器本地缓存不失效问题)
		if(staticResourceVersion==null) {
			try {
				Properties pop = new Properties();
				pop.load( this.getClass().getClassLoader().getResourceAsStream("application-allenv.properties") );
				staticResourceVersion = pop.getProperty("staticResourceVersion");
			}catch(Exception e) {
			}
		}
		if(staticResourceVersion==null || "".equals(staticResourceVersion.trim())) {
			staticResourceVersion = new SimpleDateFormat("yyyyMMdd").format(new Date());
		}
		request.setAttribute("staticResourceVersion", staticResourceVersion);
		
		//三、SSO单点登出URL、本地应用登出URL
		if(ssoLogoutUrl==null || cmsLogoutUrl==null) {
			try {
				String env = request.getServletContext().getInitParameter("env.name");
				
				if(env.equalsIgnoreCase("${env.name}")) {//有的IDE不能自动替换
					env = "dev";
				}
				Properties pop = new Properties();
				pop.load( this.getClass().getClassLoader().getResourceAsStream(env+"/application.properties") );
				ssoLogoutUrl = pop.getProperty("sso.logout.url");
				cmsLogoutUrl = pop.getProperty("cms.logout.url");
			}catch(Exception e) {
			}
		}
		if(ssoLogoutUrl==null) {//读取配置文件失败时的默认值
			ssoLogoutUrl = "https://sso.01zhuanche.com/logout";
		}
		if(cmsLogoutUrl==null) {//读取配置文件失败时的默认值
			if(request.getServerPort()==80){
				cmsLogoutUrl = "http://"+request.getServerName()+request.getContextPath()+"/logout.html";
			}else{
				cmsLogoutUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/logout.html";
			}
		}
		request.setAttribute("ssoLogoutUrl", ssoLogoutUrl );
		request.setAttribute("cmsLogoutUrl", cmsLogoutUrl );
		
		//四、常用的页面变量参数(用于页面渲染)
		request.setAttribute("webctx", request.getContextPath() );
		
		//TODO 增加其它共性的逻辑
		filterChain.doFilter(request, response);
	}
}