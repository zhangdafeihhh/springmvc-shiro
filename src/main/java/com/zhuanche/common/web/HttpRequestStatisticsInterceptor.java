package com.zhuanche.common.web;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**HTTP 打印请求参数、打印请求状况统计
 * @author zhaoyali
 **/
public class HttpRequestStatisticsInterceptor implements HandlerInterceptor{
	private static final Logger log  = LoggerFactory.getLogger(HttpRequestStatisticsInterceptor.class);
	private static final Map<String, AtomicLong> URI_COUNTER = new ConcurrentHashMap<String, AtomicLong>(); //每个URI的请求数计数器
	private static final String  HTTP_REQUEST_START_TIMESTAMP = "HTTP_REQUEST_START_TIMESTAMP";
	private static final String  HTTP_REQUEST_STOP_WATCH          = "HTTP_REQUEST_STOP_WATCH";
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
		//写入初始时间
		request.setAttribute(HTTP_REQUEST_START_TIMESTAMP, System.currentTimeMillis() );
		request.setAttribute(HTTP_REQUEST_STOP_WATCH, new Slf4JStopWatch() );
		
		//更新URI 请求数计数器
		String uri = request.getRequestURI();
		if( !URI_COUNTER.containsKey(uri) ) {
			URI_COUNTER.put(uri, new AtomicLong(1) );
		}else {
			AtomicLong atomicLong = URI_COUNTER.get(uri);
			atomicLong.incrementAndGet();
		}
		//输出URI及参数详情
		StringBuffer queryString = new StringBuffer();
		Enumeration<String> paramNames = request.getParameterNames();
		while( paramNames!=null && paramNames.hasMoreElements() ) {
			String paramName = paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			queryString.append(paramName+"=");
			if(paramValues!=null) {
				if(paramValues.length==1) {
					queryString.append(  paramValues[0]  );
				}else {
					queryString.append(  Arrays.asList(paramValues)  );
				}
			}
			if( paramNames.hasMoreElements() ) {
				queryString.append( "&" );
			}
		}
		log.info("[HTTP_STATIS] "+ uri + " ,QS: "+ queryString.toString() );
		queryString = null;
		return true;
	}
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 	ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)	throws Exception {
		//读出初始时间
		long startTimestamp  = (long)request.getAttribute(HTTP_REQUEST_START_TIMESTAMP);
		StopWatch stopWatch = (Slf4JStopWatch)request.getAttribute(HTTP_REQUEST_STOP_WATCH);
		String uri = request.getRequestURI();
		//URI 请求数计数器
		AtomicLong atomicLong = URI_COUNTER.get(uri);
		log.info("[HTTP_STATIS] "+ uri + " ,COST: "+ (System.currentTimeMillis()-startTimestamp) + "ms, TOTAL_REQUEST: "+ atomicLong.get() );
		stopWatch.stop(uri);
		
		//消毁
		request.removeAttribute(HTTP_REQUEST_START_TIMESTAMP);
		request.removeAttribute(HTTP_REQUEST_STOP_WATCH);
		stopWatch = null;
	}
}