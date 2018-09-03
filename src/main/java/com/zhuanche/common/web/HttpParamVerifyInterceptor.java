package com.zhuanche.common.web;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

/**基于Spring MVC拦截器的参数合法性验证
 * @author zhaoyali
 **/
public class HttpParamVerifyInterceptor implements HandlerInterceptor{
	private static final Logger log  = LoggerFactory.getLogger(HttpParamVerifyInterceptor.class);
	/**规则校验器**/
	private Object validator = new HttpParamVerifyValidator();
	public Object getValidator() {
		return validator;
	}
	public void setValidator(Object validator) {
		this.validator = validator;
	}
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
		if( handler instanceof HandlerMethod == false) {
			return true;//无需校验
		}
		Method method = ((HandlerMethod)handler).getMethod();
		if(method.getParameterCount()==0) {
			return true;//无需校验
		}
		//一、针对方法中的每个参数，依次循环进行校验
		String invalidReason = this.execueValidate(request, method);
		//二、校验不通过
		if(invalidReason!=null) {
			log.info( "[PARAM_VERIFY_ERROR] "+ invalidReason );
			AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
			ajaxResponse.setMsg(invalidReason);
			this.outJson(response, ajaxResponse);
			return false;
		}else {
			return true;
		}
	}
	private void outJson(HttpServletResponse response, AjaxResponse ajaxResponse){
		PrintWriter out = null;
		try{
			response.setStatus(HttpStatus.SC_OK );
			out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=UTF-8");
			response.setHeader("pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			out.write( JSON.toJSONString(ajaxResponse, true) );
			return;
		} catch (Exception e) {
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	
	private String execueValidate( HttpServletRequest request , Method method  ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for( Parameter p : method.getParameters()) {
			String invalidReason = this.execueValidate(request, method, p);
			if(invalidReason!=null) {
				return invalidReason;
			}
		}
		return null;
	}
	private String execueValidate( HttpServletRequest request , Method method , Parameter p) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Verify verify = p.getAnnotation(Verify.class);//取得校验规则的注解
		if(verify==null) {
			return null;
		}
		if( StringUtils.isEmpty(verify.param()) || StringUtils.isEmpty(verify.rule()) ) {
			return null;
		}
		//---------------------------------------------准备：本参数名称、参数值、规则列表
		String httpParam = verify.param().trim();//参数名
		String[] httpValues = request.getParameterValues( httpParam );
		List<String> rules = new ArrayList<String>();
		for(String rule : verify.rule().split("\\|") ) {
			if( StringUtils.isNotEmpty( rule.trim() )) {
				rules.add(rule.trim());
			}
		}
		//----------------------------------------------先判断必须有值
		if(rules.contains("required") ) {
			if(httpValues==null || httpValues.length==0) {
				return "参数"+httpParam+"：必须传入值";
			}else if( httpValues.length==1 ) {
				if( StringUtils.isEmpty(httpValues[0]) ) {
					return "参数"+httpParam+"：必须传入值";
				}
			}else if( httpValues.length>1 ){
				for(String v : httpValues ) {
					if( StringUtils.isEmpty(v) ) {
						return "参数"+httpParam+"：传入了多个值，但存在空值";
					}
				}
			}
		}
		rules.remove("required");
		//---------------------------------------------再判断其它规则
		if(httpValues==null) {
			return null;
		}
		for( String rule : rules ) {
			//取得校验器方法名称、配置阀值、校验器方法对象
			String validatorMethodName = null;
			String validatorThreadhold    = null;
			Method validatorMethod      = null;
			if( rule.endsWith(")") ) {
				validatorMethodName = rule.substring(0, rule.indexOf("("));
				validatorThreadhold    = rule.substring(rule.indexOf("(")+1, rule.lastIndexOf(")"));
				try {
					validatorMethod = validator.getClass().getMethod(validatorMethodName, String.class, String.class );
				}catch(Exception ex) {
					log.error("找不到方法：" + validator.getClass().getName() + "." + validatorMethodName );
				}finally {
				}
				if( validatorMethod ==null ) {
					return "参数"+httpParam+"：无法校验此参数";
				}
				//对每个值，进行校验
				for( String value : httpValues ) {
					String invalidReason = (String) validatorMethod.invoke(validator, value , validatorThreadhold );////校验不通过的原因
					if( invalidReason!=null ) {
						return "参数"+httpParam+"："+invalidReason;
					}
				}
			}else {
				validatorMethodName = rule;
				validatorThreadhold     = null;
				try {
					validatorMethod = validator.getClass().getMethod(validatorMethodName, String.class);
				}catch(Exception ex) {
					log.error("找不到方法：" + validator.getClass().getName() + "." + validatorMethodName );
				}finally {
				}
				if( validatorMethod ==null ) {
					return "参数"+httpParam+"：无法校验此参数";
				}
				//对每个值，进行校验
				for( String value : httpValues ) {
					String invalidReason = (String) validatorMethod.invoke(validator, value );////校验不通过的原因
					if( invalidReason!=null ) {
						return "参数"+httpParam+"："+invalidReason;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 	ModelAndView modelAndView) throws Exception {
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)	throws Exception {
	}
}