package com.zhuanche.common.web;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**对每一个请求，初始化各种变量及参数，以用于日志输出、展现层输出等目的
 * @author zhaoyali
 **/
public class InitRequestCommonDataFilter extends OncePerRequestFilter {
	private static String staticResourceVersion;//静态资源版本号
	private static String envName;                  //多环境信息
	private static String ssoLogoutUrl;            //SSO单点登出URL
	private static String cmsLogoutUrl;           //SSO单点登出本地回调URL
	
	private static final String SEED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String genRequestId(int length) {
		return NumberUtil.genRandomCode(length, SEED_CHARS);
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
        response.setHeader("Access-Control-Allow-Headers", "Origin,X-Requested-With,Content-Type,Accept,XRequestedWith,LastModified");
        response.setHeader("Access-Control-Allow-Credentials", "true");		
		/***************支持跨域请求END***********************/

		/***************是否为AJAX请求BEGIN******************/
		String XMLHttpRequest = request.getHeader("X-Requested-With");
		if( (XMLHttpRequest!=null && XMLHttpRequest.trim().length()>0) || request.getRequestURI().endsWith(".json") ){
			request.setAttribute("X_IS_AJAX", true);
		}else {
			request.setAttribute("X_IS_AJAX", false);
		}
		/***************是否为AJAX请求END*******************/
	
		//一、请求流水号(用于日志,实现对请求进行统一编号，方便于进行排查业务日志)
		String reqId = request.getParameter("x_requestId");
		if(reqId==null || "".equals(reqId.trim())  ) {
			reqId = request.getHeader("x_requestId");
		}
		if(reqId==null || "".equals(reqId.trim())  ) {
			reqId = this.genRequestId(6);
		}
		MDC.put("reqId", reqId);
		
		//二、多环境信息
		if(envName==null) {
			envName = request.getServletContext().getInitParameter("env.name");
			if(envName==null || !Arrays.asList(new String[]{"dev","test","pre","online"}).contains(envName)) {
				envName = "IDE";
			}
			envName = envName.toUpperCase();
		}
		MDC.put("env", envName);
		
		//三、静态资源版本号(用于页面渲染, 解决客户端浏览器本地缓存不失效问题)
		if(staticResourceVersion==null) {
			try {
				Properties pop = new Properties();
				pop.load( this.getClass().getClassLoader().getResourceAsStream("application-allenv.properties") );
				staticResourceVersion = pop.getProperty("staticResourceVersion");
			}catch(Exception e) {
				logger.error("", e);
			}
		}
		if(staticResourceVersion==null || "".equals(staticResourceVersion.trim())) {
			staticResourceVersion = new SimpleDateFormat("yyyyMMdd").format(new Date());
		}
		request.setAttribute("staticResourceVersion", staticResourceVersion);
		
		//四、SSO单点登出URL、本地应用登出URL
		if(ssoLogoutUrl==null || cmsLogoutUrl==null) {
			try {
				String env = request.getServletContext().getInitParameter("env.name");
				if( !Arrays.asList(new String[]{"dev","test","pre","online"}).contains(env) ) {//有的IDE不能自动替换
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
		
		//五、常用的页面变量参数(用于页面渲染)
		request.setAttribute("webctx", request.getContextPath() );
		
		//六、重置所有DB数据源的主从为默认设置（解决问题：当WEB容器比如TOMCAT采用线程池模型时，由于线程是重用的，所以当前后两个请求应用了同一个线程处理HTTP请求时会出现问题）
		DynamicRoutingDataSource.setDefault();
		
		//TODO 增加其它共性的逻辑
		filterChain.doFilter(request, response);
	}
}