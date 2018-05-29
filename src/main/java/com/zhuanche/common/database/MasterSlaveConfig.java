package com.zhuanche.common.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;

/**本注解用于@MasterSlaveConfigs内，在整个方法的运行期间，表示为对一个数据库标识进行主从切换，对整个方法有效。<br>
 * 同时，在方法内部， 又能同时支持编程式主从切换（DynamicRoutingDataSource类中的静态方法）。开发使用十分灵活。<br>
 * 
 * 用法格式： @MasterSlaveConfig(databaseTag=数据库标识, mode=DataSourceMode })<br>
 * 用法示例： @MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER )<br>
 * 此示例将解析为：对于标识为driver-DataSource的数据源，访问主库；<br>
 * **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface MasterSlaveConfig{
	String databaseTag() default "dataSource";
	DataSourceMode mode() default DataSourceMode.MASTER ;
}