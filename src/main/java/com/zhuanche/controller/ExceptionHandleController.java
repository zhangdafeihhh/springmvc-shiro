package com.zhuanche.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.exception.BusBizException;

/**
 * @ClassName:  ExceptionHandleController
 * @Description: Controller异常统一处理(优先执行明确异常class类型的异常处理器、优先执行本Controller内的异常处理器)
 * @author: yanyunpeng
 * @date:   2018年11月13日 下午5:09:32
 * 
 */
@RestControllerAdvice
public class ExceptionHandleController{
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandleController.class);
	
	/**
	 * @Title: handleMethodArgumentNotValidException
	 * @Description: 参数校验异常
	 * @param exception
	 * @return AjaxResponse
	 * @throws
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public AjaxResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();
		List<ObjectError> errors = bindingResult.getAllErrors();
		List<Object> list = new ArrayList<>(errors.size());
		errors.forEach(e -> list.add(e.getDefaultMessage()));
		return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, list.toString());
	}
	
	/**
	 * @Title: handleBindException
	 * @Description: 参数绑定异常
	 * @param exception
	 * @return AjaxResponse
	 * @throws
	 */
	@ExceptionHandler(BindException.class)
	public AjaxResponse handleBindException(BindException exception) {
		List<ObjectError> errors = exception.getAllErrors();
		List<String> list = new ArrayList<>(errors.size());
		errors.forEach(e -> list.add(e.getDefaultMessage()));
		return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, list.toString());
	}
	
	/**
	 * @Title: handleBusBizException
	 * @Description: 巴士异常
	 * @param exception
	 * @return AjaxResponse
	 * @throws
	 */
	@ExceptionHandler(BusBizException.class)
	public AjaxResponse handleBusBizException(BusBizException exception) {
		logger.error("[ ExceptionHandleController-handleBusBizException ] 异常信息:{}", exception.getMessage(), exception);
		return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, exception.getMessage());
	}
	
}