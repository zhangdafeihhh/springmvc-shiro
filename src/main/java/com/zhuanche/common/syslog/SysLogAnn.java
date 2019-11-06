package com.zhuanche.common.syslog;

import java.lang.annotation.*;



/**
 * ClassName:SysLogAnn <br/>
 * Date: 2019年4月17日 下午6:25:45 <br/>
 * 
 * @author baiyunlong
 * @version 1.0.0
 */
@Target(ElementType.METHOD) // 注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) // 注解在哪个阶段执行
@Documented // 生成文档
public @interface SysLogAnn {
	/** 查询模块 */
	String module() default "";
	/** 查询模块名称 */
	String methods() default "";
	/** 查询的bean名称 */
	String serviceClass() default "";
	/** 查询单个详情的bean的方法 */
	String queryMethod() default "";
	/** 查询详情的参数类型 */
	String parameterType() default "";
	/**
	 * 从页面参数中解析出要查询的id， 如域名修改中要从参数中获取customerDomainId的值进行查询
	 */
	String parameterKey() default "";
	/** 是否为批量类型操作 */
	boolean paramIsArray() default false;
	/** 对象参数名*/
	String parameterObj() default "";

	//扩展的参数，满足enable,disable等只传id，但是少状态的场景。参数为json格式字符串
	String extendParam() default "";
	
	Class objClass();
}
