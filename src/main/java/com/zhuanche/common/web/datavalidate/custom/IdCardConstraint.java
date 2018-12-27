package com.zhuanche.common.web.datavalidate.custom;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.zhuanche.util.IdNumberUtil;
/**
 * Hibernate Validator 扩展：适用于验证中国的身份证号码格式的合法性
 * @author zhaoyali
 **/
public class IdCardConstraint implements ConstraintValidator<IdCard,String>{
	@Override 
	public void initialize(IdCard phoneAnnotation) { 
	}

	@Override 
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		if (value.toUpperCase().matches("^[1-9]{1}[0-9]{16}[0-9X]{1}$")==false) {
			return false; 
		}
		return IdNumberUtil.check(value.toUpperCase());
	}
}