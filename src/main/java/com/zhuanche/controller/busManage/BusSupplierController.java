package com.zhuanche.controller.busManage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.busManage.BusDriverSaveDTO;
import com.zhuanche.serv.CarBizCarGroupService;
import com.zhuanche.serv.CarBizCarInfoService;
import com.zhuanche.serv.CarBizDriverInfoDetailService;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.busManage.BusCarBizDriverInfoService;
import com.zhuanche.serv.busManage.BusCarDriverTeamService;

@RestController
@RequestMapping("/bus/supplier")
public class BusSupplierController {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierController.class);

	// ===========================专车业务拓展service==================================

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusCarBizDriverInfoService busCarBizDriverInfoService;

	@RequestMapping(value = "/saveDriver")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse saveDriver(BusDriverSaveDTO saveDTO) {

		return AjaxResponse.success(null);
	}
}
