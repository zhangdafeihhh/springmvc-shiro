package com.zhuanche.common.web.datavalidate.custom;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * @ClassName: InArray
 * @Description: 值是否在数组里
 * @author: yanyunpeng
 * @date: 2018年11月23日 下午5:45:01
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { InArray.ArrayValidator.class })
public @interface InArray {
	String message() default "某一参数值不在有效范围内";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] values() default {};

	/**
	 * Defines several {@link InArray} annotations on the same element.
	 */
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		InArray[] value();
	}

	public class ArrayValidator implements ConstraintValidator<InArray, Object> {

		private String[] options;

		@Override
		public void initialize(InArray constraint) {
			this.options = constraint.values();
		}

		@Override
		public boolean isValid(Object value, ConstraintValidatorContext context) {
			if (options == null || options.length == 0) {
				return true;
			}
			if (value == null) {
				return true;
			}
			for (String option : options) {
				if (option.equals(value.toString())) {
					return true;
				}
			}
			return false;
		}
	}
}
