package com.zhuanche.controller.busManage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.serv.busManage.BusSettlementAdviceService;

/**
 * @ClassName: BusSettlementAdviceController
 * @Description: 巴士结算单管理
 * @author: yanyunpeng
 * @date: 2018年12月7日 上午11:28:50
 * 
 */
@RestController
@RequestMapping("/bus/settlement")
@Validated
public class BusSettlementAdviceController {
	
	@Autowired
	private BusSettlementAdviceService busSettlementAdviceService;
	
	@RequestMapping(value = "/pageList")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse pageList() {
		// TODO
		return AjaxResponse.success(null);
	}
	
	@RequestMapping(value = "/exportList")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public void exportList() {
		// TODO
	}
	
	@RequestMapping(value = "/detail")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse detail() {
		// TODO
		return AjaxResponse.success(null);
	}

}
