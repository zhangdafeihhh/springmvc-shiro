package com.zhuanche.common.web;

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
	public String name1();//一级功能名称
	public String name2();//二级功能名称
	public String name3();//三级功能名称
	public String name4();//四级功能名称
}