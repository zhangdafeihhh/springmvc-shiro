package com.zhuanche.common.database;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/** 动态路由数据源：目的是支持主从切换、读写分离的场景
 *  特别说明：对于本项目，主要是达到对某一个相同结构的数据库进行主从切换（此时Mybatis Mapper 是支持主从复用的：即同一个Mapper，既可以针对主库，也可以针对从库）。
 **/
public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements BeanNameAware{
	private static final Logger log = LoggerFactory.getLogger(DynamicRoutingDataSource.class);
	/**此动态路由数据源标识名：即给动态数据源起的名称，代表此动态路由数据源（这里采用了spring xml配置文件Bean定义中的id属性）**/
	private String databaseTag;
	/**此动态路由数据源的主从配置（值为master或slave）**/
    private ThreadLocal<String> dbContextHolder = new ThreadLocal<String>();
    /**所有动态数据源的主从配置（key: 动态路由数据源标识名; value: 该动态路由数据源的主从配置）**/
    private static Map<String, ThreadLocal<String>> allContextHolderMappings = new HashMap<String, ThreadLocal<String>>(16);
	
    /**初始化此动态路由数据源的主从配置信息**/
	@Override
	public void setBeanName(String beanName) {
		this.databaseTag = beanName;
		allContextHolderMappings.put( this.databaseTag, dbContextHolder );
		log.info(">>>>>>>>>>>>>>>>>>>>>>>动态路由数据源初始化成功！动态路由数据源标识名：" + beanName );
	}
	/**重写此方法，以支持多个动态路由数据源**/
	@Override
    protected Object determineCurrentLookupKey() {
		ThreadLocal<String> holder = allContextHolderMappings.get(this.databaseTag);
		if(holder!=null) {
			return holder.get();
		}
		return null;
    }
	
	/**数据源类型**/
	public enum DataSourceMode{
		MASTER("master"),SLAVE("slave");
		
		private String value;
		DataSourceMode(String value){
			this.value = value;
		}
		String getValue() {
			return this.value;
		}
	}

	/***************************************切换主从数据源************************************/
	public static void setMasterSlave( String databaseTag, DataSourceMode mode) {
		ThreadLocal<String> holder = allContextHolderMappings.get(databaseTag);
		if(holder==null) {
			return;
		}
		if(mode!=null) {
			holder.set( mode.getValue()  );
		}
	}
	/***************************************切换为默认数据源*********************************/
    public static void setDefault(  String databaseTag ) {
		ThreadLocal<String> holder = allContextHolderMappings.get(databaseTag);
		if(holder==null) {
			return;
		}
		holder.remove();
    }
	/***************************************获得数据源的类型*********************************/
    public static DataSourceMode getMasterSlave(  String databaseTag ) {
		ThreadLocal<String> holder = allContextHolderMappings.get(databaseTag);
		if(holder==null) {
			return null;
		}
		if("master".equalsIgnoreCase(holder.get())) {
			return DataSourceMode.MASTER;
		}else if("slave".equalsIgnoreCase(holder.get())) {
			return DataSourceMode.SLAVE;
		}else {
			return null;
		}
    }
}