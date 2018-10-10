package com.zhuanche.serv;

import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;

@Service
public class DemoService2{
   @MasterSlaveConfigs(configs={ 
		  @MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.MASTER ),
		  @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
    } )
	public void sayhello() {
		System.out.println("DemoService2-----------------------sayhello 主从切换测试开始");
		System.out.println("DemoService2-----------------------sayhello 主从切换测试结束");
	}
}