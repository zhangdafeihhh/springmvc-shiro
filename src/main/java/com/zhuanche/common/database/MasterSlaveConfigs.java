package com.zhuanche.common.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**本注解用于方法上，在整个方法的运行期间，支持对一个或多个数据库标识进行主从切换，对整个方法有效。<br>
 * 同时，在方法内部， 又能同时支持编程式主从切换（DynamicRoutingDataSource类中的静态方法）。开发使用十分灵活。<br>
 * 
 * 用法格式： @MasterSlaveConfigs(configs={ @MasterSlaveConfig , @MasterSlaveConfig ....... })<br>
 * **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MasterSlaveConfigs {
	MasterSlaveConfig[] configs();
}