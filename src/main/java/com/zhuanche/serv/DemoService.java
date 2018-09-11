package com.zhuanche.serv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;

@Service
public class DemoService{
	@Autowired
	private DemoService2 demoService2;

   @MasterSlaveConfigs(configs={ 
		  @MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE ),
		  @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.MASTER )
    } )
	public void sayhello() {
		System.out.println("DemoService-----------------------sayhello 主从切换测试开始");
	   demoService2.sayhello();
		System.out.println("DemoService-----------------------sayhello 主从切换测试结束");
	}
}