package com.zhuanche.common.web;

import com.zhuanche.common.enums.MenuEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义请求的功能名称信息
 * @author zhaoyali
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestFunction {
	MenuEnum menu();
}