package com.zhuanche.controller.busManage;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.serv.busManage.BusCommonService;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BusCommonController
 * @Description: 巴士公用接口
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:09:31
 * 
 */
@RestController
@RequestMapping("/bus/common")
@Validated
public class BusCommonController {

	@Autowired
	private BusCommonService busCommonService;
	
	/**
	 * @Title: changeLogs
	 * @Description: 查询操作日志
	 * @param businessType
	 * @param businessKey
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/changeLogs")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse changeLogs(@NotNull(message = "业务类型不能为空") Integer businessType,
			@NotBlank(message = "业务主键不能为空") String businessKey) {
		// 一、校验业务类型是否存在
		if (!BusinessType.isExist(businessType)) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "业务类型不存在");
		}
		
		// 二、查询日志
		List<Map<Object,Object>> logs = busCommonService.queryChangeLogs(businessType, businessKey);
		return AjaxResponse.success(logs);
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
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse suppliers(@NotNull(message = "城市ID不能为空") Integer cityId) {
		List<Map<Object, Object>> suppliers = busCommonService.querySuppliers(cityId);
		return AjaxResponse.success(suppliers);
	}
	
	/**
	 * @Title: groups
	 * @Description: 巴士车型类别
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/groups")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse groups() {
		List<Map<Object, Object>> groups = busCommonService.queryGroups();
		return AjaxResponse.success(groups);
	}
	
	/**
	 * @Title: services
	 * @Description: 查询巴士服务类型
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/services")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse services() {
		List<Map<Object, Object>> services = busCommonService.queryServices();
		return AjaxResponse.success(services);
	}

}
