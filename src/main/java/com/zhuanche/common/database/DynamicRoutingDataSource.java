package com.zhuanche.common.database;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/** 动态路由数据源：目的是支持主从切换、读写分离的场景
 *  特别说明：对于本项目，主要是达到对某一个相同结构的数据库进行主从切换（此时Mybatis Mapper 是支持主从复用的：即同一个Mapper，既可以针对主库，也可以针对从库）。
 **/
public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements BeanNameAware{
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
		System.out.println("DynamicRoutingDataSource动态路由数据源初始化成功！动态路由数据源标识名：" + beanName );
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
		holder.set( mode.getValue()  );
	}
	/***************************************切换为默认数据源*********************************/
    public static void setDefault(  String databaseTag ) {
		ThreadLocal<String> holder = allContextHolderMappings.get(databaseTag);
		if(holder==null) {
			return;
		}
		holder.remove();
    }
    
//	
//	
//    //-----------------------------------------driver 数据库-------------------------------------------------------BEGIN
//    private static final ThreadLocal<String> driverContextHolder = new ThreadLocal<String>();
//    /**设置为访问driver主库**/
//    public static void setDriver2Master() {
//    	driverContextHolder.set("master");
//    }
//    /**设置为访问driver从库**/
//    public static void setDriver2Slave() {
//    	driverContextHolder.set("slave");
//    }
//    /**设置为访问driver默认库**/
//    public static void setDriver2Default() {
//    	driverContextHolder.remove();
//    }
//    //-----------------------------------------driver 数据库-------------------------------------------------------END
//    
//    //-----------------------------------------rentcar 数据库-----------------------------------------------------BEGIN
//    private static final ThreadLocal<String> rentcarContextHolder = new ThreadLocal<String>();
//    /**设置为访问rentcar主库**/
//    public static void setRentcar2Master() {
//    	rentcarContextHolder.set("master");
//    }
//    /**设置为访问rentcar从库**/
//    public static void setRentcar2Slave() {
//    	rentcarContextHolder.set("slave");
//    }
//    /**设置为访问rentcar默认库**/
//    public static void setRentcar2Default() {
//    	rentcarContextHolder.remove();
//    }
    //-----------------------------------------rentcar 数据库-----------------------------------------------------END
   
    //TODO 此处将来可以继续进行增加，扩展更多的业务数据库达到主从切换的目的。（如下为demo代码模板）
    //-----------------------------------------demo 数据库------------------------------------------------------BEGIN
//    private static final ThreadLocal<String> demoContextHolder = new ThreadLocal<String>();
//    /**设置为访问demo主库**/
//    public static void setDemo2Master() {
//    	demoContextHolder.set("master");
//    }
//    /**设置为访问demo从库**/
//    public static void setDemo2Slave() {
//    	demoContextHolder.set("slave");
//    }
//    /**设置为访问demo默认库**/
//    public static void setDemo2Default() {
//    	demoContextHolder.remove();
//    }
    //-----------------------------------------demo 数据库------------------------------------------------------END
    
}