package com.zhuanche.common.web.datavalidate.custom;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * Hibernate Validator 扩展：适用于验证中国境内的移动手机号码格式的合法性
 * @author zhaoyali
 **/
public class MobilePhoneConstraint implements ConstraintValidator<MobilePhone,String>{
	private String regexp;
	@Override 
	public void initialize(MobilePhone phoneAnnotation) { 
		this.regexp = phoneAnnotation.regexp(); 
	}

	@Override 
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		if (value.matches(regexp)) {
			return true; 
		}else {
			return false;
		}
	}
}