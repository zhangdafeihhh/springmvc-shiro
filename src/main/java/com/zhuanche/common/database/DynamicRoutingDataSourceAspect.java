package com.zhuanche.common.database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
/**AOP切面：应用于注解@MasterSlaveConfig的数据源动态主从切换**/
@Component
@Aspect
public class DynamicRoutingDataSourceAspect {
	private static ThreadLocal<List<Map<String,DataSourceMode>>> dBStackOfMethodInvoke = new ThreadLocal<List<Map<String,DataSourceMode>>>();//方法调用栈
	
	@Before("@annotation(msc)")
    public void beforeMethod(JoinPoint jp , MasterSlaveConfigs msc){
		if(msc==null || msc.configs()==null || msc.configs().length==0) {
			return;
		}
		
		//一、初始化方法调用栈
		List<Map<String,DataSourceMode>> stack = dBStackOfMethodInvoke.get();
		if(stack==null) {
			stack =  new LinkedList<Map<String,DataSourceMode>>();
		}
		
		//二、保存以前的数据源配置，同时更新为新的数据源配置
		Map<String,DataSourceMode> originDataSourceConfigs = new HashMap<String,DataSourceMode>(16);//用于保存之前的数据库主从配置信息，用MAP存储
		for(MasterSlaveConfig config : msc.configs()) {
			String databaseTag = config.databaseTag();
			if(databaseTag==null || databaseTag.trim().length()==0) {
				continue;
			}
			//保存以前的数据源配置
			DataSourceMode origin_master_slave_value = DynamicRoutingDataSource.getMasterSlave(databaseTag);
			originDataSourceConfigs.put(databaseTag, origin_master_slave_value);
			//更新为新的数据源配置
			DataSourceMode newer_master_slave_value = config.mode();
			DynamicRoutingDataSource.setMasterSlave(databaseTag, newer_master_slave_value );
		}
		
		//三、入栈
		stack.add(originDataSourceConfigs);
		dBStackOfMethodInvoke.set(stack);
        //System.out.println("【AOP数据源主从切换BEGIN】before method 【" +jp.getSignature().toString()+ "】：" + Arrays.asList(msc.configs()) );
    }
	
	@After("@annotation(msc)")
    public void afterMethod(JoinPoint jp , MasterSlaveConfigs msc){
		if(msc==null || msc.configs()==null || msc.configs().length==0) {
			return;
		}
		//一、出栈
		List<Map<String,DataSourceMode>> stack = dBStackOfMethodInvoke.get();
		Map<String,DataSourceMode> originDataSourceConfigs = stack.get(stack.size()-1);//之前的数据库主从配置信息
		//二、恢复之前的配置
		for(String databaseTag: originDataSourceConfigs.keySet() ) {
			DataSourceMode origin_master_slave_value = originDataSourceConfigs.get(databaseTag);
			if(origin_master_slave_value!=null) {
				DynamicRoutingDataSource.setMasterSlave(databaseTag, origin_master_slave_value  );
			}else {
				DynamicRoutingDataSource.setDefault( databaseTag );
			}
		}
		stack.remove(stack.size()-1);
		originDataSourceConfigs.clear();
		originDataSourceConfigs=null;
		
		//三、如果栈空了，则清除方法调用栈
		if(stack.size()==0) {
			stack = null;
			dBStackOfMethodInvoke.remove();
		}
        //System.out.println("【AOP数据源主从切换END】after method 【" +jp.getSignature().toString()+ "】：" + Arrays.asList(msc.configs()) );
    }
}