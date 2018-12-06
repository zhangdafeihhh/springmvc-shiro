package com.zhuanche.controller.busManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.BusBizChangeLog;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.busManage.BusCommonService;

/**
 * @ClassName: BusCommonController
 * @Description: 巴士公用接口
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:09:31
 * 
 */
@RestController
@RequestMapping("/bus/common")
public class BusCommonController {

	@Autowired
	private BusCommonService busCommonService;
	
	@RequestMapping(value = "/changeLogs")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse changeLogs(@NotBlank(message = "业务类型不能为空") String businessType,
			@NotBlank(message = "业务主键不能为空") String businessKey) {
		
		List<BusBizChangeLog> logs = busCommonService.queryChangeLogs(businessType, businessKey);
		List<Map<Object, Object>> list = logs.stream().map(log -> {
			Map<Object,Object> map = new HashMap<>();
			map.put("updateName", log.getUpdateName());
			map.put("updateDate", log.getUpdateDate());
			return map;
		}).collect(Collectors.toList());
		
		return AjaxResponse.success(list);
	}

	/**
	 * @Title: suppliers
	 * @Description: 查询供应商
	 * @param cityId
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/suppliers")
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse suppliers(@NotNull(message = "城市ID不能为空") Integer cityId) {
		
		List<CarBizSupplier> suppliers = busCommonService.querySuppliers(cityId);
		List<Map<String,Object>> list = suppliers.stream().map(supplier -> {
			Map<String, Object> map = new HashMap<>();
			map.put("supplierId", supplier.getSupplierId());
			map.put("supplierName", supplier.getSupplierFullName());
			return map;
		}).collect(Collectors.toList());
		
		return AjaxResponse.success(list);
	}

}
