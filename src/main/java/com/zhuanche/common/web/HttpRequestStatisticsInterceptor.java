package com.zhuanche.common.web;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zhuanche.mongo.UserOperationLog;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.IpAddr;

/**HTTP 打印请求参数、打印请求状况统计
 * @author zhaoyali
 **/
public class HttpRequestStatisticsInterceptor implements HandlerInterceptor,  InitializingBean{
	private static final Logger log  = LoggerFactory.getLogger(HttpRequestStatisticsInterceptor.class);
	private static final Map<String, AtomicLong> URI_COUNTER = new ConcurrentHashMap<String, AtomicLong>(); //每个URI的请求数计数器
	private static final String  HTTP_REQUEST_START_TIMESTAMP = "HTTP_REQUEST_START_TIMESTAMP";
	private static final String  HTTP_REQUEST_STOP_WATCH          = "HTTP_REQUEST_STOP_WATCH";
	
	private static final ArrayBlockingQueue<UserOperationLog> USER_LOG_QUEUE = new ArrayBlockingQueue<>(2000);//一个内存队列（用户的操作日志）
	private int insertLog2DBWorkers = 2; //插入日志的线程数
	private MongoTemplate userOperationMongoTemplate;
	public void setUserOperationMongoTemplate(MongoTemplate userOperationMongoTemplate) {
		this.userOperationMongoTemplate = userOperationMongoTemplate;
	}
	public void setInsertLog2DBWorkers(int insertLog2DBWorkers) {
		this.insertLog2DBWorkers = insertLog2DBWorkers;
	}
	
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
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)	throws Exception {
		//读出初始时间
		long startTimestamp  = (long)request.getAttribute(HTTP_REQUEST_START_TIMESTAMP);
		StopWatch stopWatch = (Slf4JStopWatch)request.getAttribute(HTTP_REQUEST_STOP_WATCH);
		String uri = request.getRequestURI();
		//URI 请求数计数器
		AtomicLong atomicLong = URI_COUNTER.get(uri);
		long costMiliseconds = System.currentTimeMillis()-startTimestamp;
		log.info("[HTTP_STATIS] "+ uri + " ,COST: "+ costMiliseconds + "ms, TOTAL_REQUEST: "+ atomicLong.get() );
		stopWatch.stop(uri);
		stopWatch = null;
		
		//记录用户操作日志 
		this.addRequestLog2DBAsync( request, response, handler , startTimestamp, costMiliseconds );
		
		//消毁
		request.removeAttribute(HTTP_REQUEST_START_TIMESTAMP);
		request.removeAttribute(HTTP_REQUEST_STOP_WATCH);
	}
	
	//记录用户操作日志 (added by zhaoyali 20190105)
	private void addRequestLog2DBAsync( HttpServletRequest request, HttpServletResponse response, Object handler , long reqTimeStamp, long costMiliseconds ) {
		String uri = request.getRequestURI();
		if( StringUtils.isBlank(uri) || "/".equals(uri) ) {
			return;
		}
		//一、生成用户操作日志的记录
		UserOperationLog opLog = new UserOperationLog();
		SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
		if(loginUser!=null) {
			opLog.setUserId(loginUser.getId());                         //用户ID
			opLog.setUserName(loginUser.getName());             //用户名称
			opLog.setLoginName(loginUser.getLoginName());    //用户登录账号
		}else {
			opLog.setUserId(0);                                                //用户ID
			opLog.setUserName("未登录用户");                      //用户名称
			opLog.setLoginName( null );                                  //用户登录账号
		}
        opLog.setClientIp( IpAddr.getIpAddr(request)  );           //用户IP
        opLog.setRequestUri(uri);                                            //请求的URI
        opLog.setRequestFuncName( null );                             //请求的功能名称--------------------------------------------------后续再获取进行补充
        opLog.setRequestMethod(request.getMethod().toUpperCase());   //请求方法
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");       //请求类型
        if(  isAjax ) {
        	opLog.setRequestType("XHR");
        }else {
        	opLog.setRequestType("HTTP");
        }
        opLog.setRequestId(MDC.get("reqId"));                                       //请求流水ID
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
		opLog.setRequestParams( queryString.toString() );                       //请求的参数
		opLog.setRequestTime(  new Date( reqTimeStamp )   );               //请求时间
		opLog.setCostTime( costMiliseconds );                                       //请求耗时
		//二、异步保存到mongodb
		try{
			USER_LOG_QUEUE.offer(opLog , 2, TimeUnit.SECONDS );
		} catch (InterruptedException e) {
		}
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		//启动异步执行插入日志的线程
		Runnable insertLogRunnable = new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						UserOperationLog item = USER_LOG_QUEUE.take();
						userOperationMongoTemplate.insert(item);
						item = null;
					}catch(Exception ex) {
						log.error("插入用户操作日志表异常！", ex );
					}
				}
			}
		};
		//启动n个线程
		for(int i=1; i<=insertLog2DBWorkers;i++) {
			Thread insertLogThread = new Thread(insertLogRunnable, "InsertUserRequestLog2DBWorker-"+i );
			insertLogThread.setDaemon(true);
			insertLogThread.start();
		}
		log.info(">>>>>>>>>>>>>>>>>>异步执行插入日志的线程启动成功！线程数量："+ insertLog2DBWorkers + "个");
	}
}