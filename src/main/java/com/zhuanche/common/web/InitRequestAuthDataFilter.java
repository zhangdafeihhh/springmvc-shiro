package com.zhuanche.common.web;

import com.le.config.dict.Dicts;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.IPv4Util2;
import mp.mvc.logger.entity.LoggerDto;
import mp.mvc.logger.message.MpLoggerMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**对每一个请求，获取当前登录用户信息，以用于日志输出、页面渲染
 * @author zhaoyali
 **/
public class InitRequestAuthDataFilter extends OncePerRequestFilter {

	/**
	 * log 追踪ID
	 */
	private final static String TRACE_KEY = "X-Request-Id";

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


		//先从param里取，没有的话从header里取，还没有的话再创建
		String reqId = request.getParameter("X-Request-Id");
		if(reqId==null || "".equals(reqId.trim())  ) {
			reqId = request.getHeader("X-Request-Id");
		}
		if(reqId==null || "".equals(reqId.trim())  ) {
			reqId =   UUID.randomUUID().toString().replace("-", "");
		}
		MDC.put(TRACE_KEY, reqId);
		//该traceId是让日志打印出来的key值
		MDC.put("traceId",reqId);
		/**防止MDC 多次生成，引入的sq-component-log 有拦截 header 头信息**/

		//自动拦截用户操作业务
		Boolean userLoggerSwitch = Dicts.getBoolean("user_logger_switch",true);
		if(userLoggerSwitch){
			if(loginUser != null && loginUser.getId() != null){
				LoggerDto dto = this.getBuiness(request,reqId,loginUser);
				MpLoggerMessage.sendLoggerMessage(dto,request);
			}
		}
		
		filterChain.doFilter(request, response);

		MDC.remove(TRACE_KEY);
	}

	private LoggerDto getBuiness(HttpServletRequest request ,String traceId,SSOLoginUser loginUser) {
		LoggerDto dto = new LoggerDto();
		dto.setCreateTime(System.currentTimeMillis());
		String sessionId = (String) SecurityUtils.getSubject().getSession().getId();
		dto.setSessionId(StringUtils.isNotBlank(sessionId) ? sessionId : traceId);
		dto.setUserAccount(StringUtils.isNotBlank(loginUser.getLoginName()) ? loginUser.getLoginName() : null);
		dto.setUserIp(IPv4Util2.getClientIpAddr(request));
		dto.setUserId(String.valueOf(loginUser.getId()));
		dto.setRemark(StringUtils.isNotBlank(loginUser.getName()) ? loginUser.getName() : loginUser.getLoginName());
		dto.setTraceId(traceId);
		return dto;
	}
}