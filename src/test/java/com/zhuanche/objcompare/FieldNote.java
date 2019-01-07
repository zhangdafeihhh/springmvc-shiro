package com.zhuanche.objcompare;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldNote {
	String value();

	Pattern pattern() default Pattern.DATE_TIME;

	enum Pattern {

		DATE("yyyy-MM-dd"),

		TIME("HH:mm:ss"),

		DATE_TIME("yyyy-MM-dd HH:mm:ss");

		private String value;

		private Pattern(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}

	}
}
