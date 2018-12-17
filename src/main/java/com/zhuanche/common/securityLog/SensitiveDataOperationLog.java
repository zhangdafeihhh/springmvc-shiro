package com.zhuanche.common.securityLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * ClassName: SensitiveDataOperationLog.java 
 * Date: 2018年12月12日 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 * 本注解用于用户操作敏感数据时，实现对方法所实现功能进行描述，以便在记录敏感数据操作日志时获取方法描述信息<br>
 * 用法格式： @SensitiveDataOperationLog(primaryDataType=一级数据类型, secondaryDataType=二级数据类型, desc=描述信息)<br>
 * 用法示例： @SensitiveDataOperationLog(primaryDataType=司机数据, secondaryDataType=司机个人信息, desc=查询司机个人信息 )<br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SensitiveDataOperationLog {

	String primaryDataType() default "无一级数据类型";
	String secondaryDataType() default "无二级数据类型";
	String desc() default "无描述信息";
}
