package com.zhuanche.common.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.le.config.dict.Dicts;
import com.sq.monitor.pojo.dto.dingding.DingTextDTO;
import com.sq.monitor.utils.DingDingUtil;
import com.zhuanche.http.MpOkHttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.http.HttpStatus;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
/**
 * 对异常进行捕获，进行统一返回
 * @author zhaoyali
 **/
@ControllerAdvice
public class CustomExceptionAdvice {
	private static final Random random = new SecureRandom();
	private static final Logger logger = LoggerFactory.getLogger(CustomExceptionAdvice.class);
    @Value(value="${loginpage.url}")
    private String loginpageUrl;  //前端UI登录页面
	@Value("${homepage.url}")
	private String homepageUrl; //前端UI首页页面

	@Value("${dingding.token.url}")
	private String dingdingTokenUrl;
	
	/**Hibernate Validator 参数校验不通过时**/
	@ExceptionHandler( {BindException.class, MethodArgumentNotValidException.class })
	@ResponseBody
	public AjaxResponse handleMethodArgumentNotValidException( Exception e , HttpServletRequest request, HttpServletResponse response) {
    	String message = "参数校验不通过";
		BindingResult bindingResult = null;
		if( e instanceof BindException ) {
			bindingResult = ((BindException)e).getBindingResult();
		}else if( e instanceof MethodArgumentNotValidException  ) {
			bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
		}
    	if (bindingResult!=null && bindingResult.hasErrors() ) { 
	    	List<ObjectError> errors = bindingResult.getAllErrors();
	    	List<String> errorTexts = new ArrayList<String>( errors.size() );
	    	for(ObjectError error : errors) {
	    		String fieldname = "";
	    		if( error instanceof FieldError) {
	        		fieldname = "参数"+ ((FieldError)error).getField();
	    		}
	    		errorTexts.add(  fieldname +"："+ error.getDefaultMessage() );
	    	}
    	    message = errorTexts.stream().collect(Collectors.joining("，"));
    	}
    	logger.info( "[PARAM_VERIFY_ERROR] "+ message );
        return AjaxResponse.failMsg( RestErrorCode.HTTP_PARAM_INVALID,  message );
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	public AjaxResponse handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response){
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		String errorMsg = violations.stream().map(violation -> violation.getMessage()).collect(Collectors.toList()).toString();
		return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, errorMsg);
	}
	
	/**用户shiro授权不通过时**/
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseBody
	public AjaxResponse handleUnauthorizedException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		if(  isAjax  ) {
			AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
			return ajaxResponse;
		}else {
			response.sendRedirect( homepageUrl );
			//return "unauthorized_content";//------------返回禁止访问的错误页
			return null;
		}
	}
	
	/**出现其它业务异常时**/
	@ExceptionHandler( Exception.class)
	public ModelAndView handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		int ExceptionId = random.nextInt(2100000000);
		String exceptionMessage = ex.getMessage() + " (ExceptionId: "+ExceptionId+")";
		logger.error(exceptionMessage, ex );

		int dingding_alerm_switch = Dicts.getInt("dingding_alerm_switch", 0);
		String dingding_token_url = Dicts.getString("dingding_token_url", "https://oapi.dingtalk.com/robot/send?access_token=747d5c066ec3b79c229721eff222aa1dd63a813be5e810dd934b1c284097ab39");
		int dingding_alerm_timeout = Dicts.getInt("dingding_alerm_timeout", 3000);
		if(dingding_alerm_switch == 0){
			logger.debug("报警开关关闭，不发送钉钉报警通知");
		}else{
			try {
				DingTextDTO dingTextDTO = new DingTextDTO();
				dingTextDTO.setTitle("mp-restapi接口超时报警");
				String mess = MessageFormat.format("接口超时报警:项目ip:{0},tracdId:{1},项目端口:{2},接口地址:{3},请求方式:{4},错误信息:{5}",
						request.getServerName(),MDC.get("reqId"),request.getServerPort(),request.getRequestURI(),request.getMethod(),ex.getMessage());

				logger.info(mess);
				dingTextDTO.setContent(mess);
				DingDingUtil.sendMessage(dingTextDTO,dingding_token_url);
			} catch (Exception e) {
				logger.info("钉钉告警消息异常!" + e.getMessage());
			}
		}
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		String exceptionDetail = sw.toString();
		
		//1.判断是否为AJAX请求
		Boolean isAjax = (Boolean) request.getAttribute("X_IS_AJAX");
		//2.根据不同的请求类型，分别返回不同的结果
		if(isAjax){
			AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			ajaxResponse.setMsg("系统发生异常，异常ID："+ExceptionId+"，请联系管理员。");
			this.outJson(response, ajaxResponse);
			return null;
		}else{
			String envName = request.getServletContext().getInitParameter("env.name");
			request.setAttribute("envName", envName );
			request.setAttribute("exceptionMessage", exceptionMessage);
			request.setAttribute("exceptionDetail",  exceptionDetail);
			return new ModelAndView("exception" );
		}
	}
	
	@SuppressWarnings("deprecation")
	private void outJson(HttpServletResponse response, AjaxResponse ajaxResponse){
		PrintWriter out = null;
		try{
			response.setStatus(HttpStatus.SC_OK, ajaxResponse.getMsg() );
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


}