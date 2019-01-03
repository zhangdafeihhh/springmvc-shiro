package com.zhuanche.common.database.shard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 配置DB逻辑分表的注解
 * @author ZHAOYALI
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardTableConfig{
	/**表名**/
	String tablename() default "";
	/**此表相对应的PO持久层的对象领域模型的类**/
	Class<?> moduleClass();
}