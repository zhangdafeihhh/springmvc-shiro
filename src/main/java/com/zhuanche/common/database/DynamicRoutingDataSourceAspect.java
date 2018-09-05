package com.zhuanche.common.database;

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
	
	@Before("@annotation(msc)")
    public void beforeMethod(JoinPoint jp , MasterSlaveConfigs msc){
		if(msc==null || msc.configs()==null || msc.configs().length==0) {
			return;
		}
		
		for(MasterSlaveConfig config : msc.configs()) {
			String databaseTag = config.databaseTag();
			DataSourceMode master_slave = config.mode();
			if(databaseTag==null || databaseTag.trim().length()==0) {
				continue;
			}
			DynamicRoutingDataSource.setMasterSlave(databaseTag, master_slave );
			
		}
        //System.out.println("【AOP数据源主从切换BEGIN】before method 【" +jp.getSignature().toString()+ "】：" + Arrays.asList(msc.configs()) );
    }
	@After("@annotation(msc)")
    public void afterMethod(JoinPoint jp , MasterSlaveConfigs msc){
		if(msc==null || msc.configs()==null || msc.configs().length==0) {
			return;
		}
		for(MasterSlaveConfig config : msc.configs()) {
			String databaseTag = config.databaseTag();
			if(databaseTag==null || databaseTag.trim().length()==0) {
				continue;
			}
			DynamicRoutingDataSource.setDefault(databaseTag);
		}
        //System.out.println("【AOP数据源主从切换END】after method 【" +jp.getSignature().toString()+ "】：" + Arrays.asList(msc.configs()) );
    }
}