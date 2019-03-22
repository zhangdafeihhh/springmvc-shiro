package com.zhuanche.serv.busManage;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.shiro.realm.SSOLoginUser;
import mapper.mdbcarmanage.ex.SaasRolePermissionRalationExMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper;
import mapper.rentcar.ex.BusCarBizCarGroupExMapper;
import mapper.rentcar.ex.BusCarBizServiceExMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

/**
 * @ClassName: BusCommonService
 * @Description: 巴士公用接口服务类
 * @author: yanyunpeng
 * @date: 2018年12月6日 上午10:25:09
 * 
 */
@Service
public class BusCommonService {

	@Autowired
	private BusBizChangeLogExMapper busBizChangeLogExMapper;

	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	@Autowired
	private BusCarBizCarGroupExMapper busCarBizCarGroupExMapper;

	@Autowired
	private BusCarBizServiceExMapper busCarBizServiceExMapper;
	@Autowired
	private SaasRolePermissionRalationExMapper saasRolePermissionRalationExMapper;
	/**
	 * 巴士运营角色code
	 */
	@Value("${operator.role.code}")
	private String operatorRoleCode;
	/**
	 * 巴士供应商角色code
	 */
	@Value("${supplier.role.code}")
	private String supplierRoleCode;


	/**
	 * @param now
	 * @param businessType
	 * @param businessKey
	 * @return List<BusBizChangeLog>
	 * @throws
	 * @Title: queryChangeLogs
	 * @Description: 查询操作日志
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public List<Map<Object, Object>> queryChangeLogs(Integer businessType, String businessKey, Date date) {
		Map<String, Object> param = new HashMap<>();
		param.put("businessType", businessType);
		param.put("businessKey", businessKey);
		param.put("startDate", date);
		return busBizChangeLogExMapper.queryRecnetlyChangeLogs(param);
	}

	/**
	 * @param cityId
	 * @return List<CarBizSupplier>
	 * @throws
	 * @Title: querySuppliers
	 * @Description: 查询供应商
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<Map<Object, Object>> querySuppliers(Integer cityId) {
		// 数据权限控制SSOLoginUser
		Set<Integer> authOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> authOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID

		if (authOfCity == null || authOfCity.isEmpty() || authOfCity.contains(cityId)) {
			Map<String, Object> param = new HashMap<>();
			param.put("cityId", cityId);
			param.put("authOfSupplier", authOfSupplier);
			return busCarBizSupplierExMapper.querySuppliers(param);
		}
		return null;
	}

	/**
	 * @return List<CarBizCarGroup>
	 * @throws
	 * @Title: queryGroups
	 * @Description: 查询巴士车型类别
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<Map<Object, Object>> queryGroups() {
		return busCarBizCarGroupExMapper.queryGroups();
	}

	/**
	 * @return List<Map<Object,Object>>
	 * @throws
	 * @Title: queryServices
	 * @Description: 查询巴士服务类型
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<Map<Object, Object>> queryServices() {
		return busCarBizServiceExMapper.queryServices();
	}


	/**
	 * 判断是否是巴士运营角色
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public boolean ifOperate() {
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		List<String> rolecodes = saasRolePermissionRalationExMapper.queryRoleCodeList(currentLoginUser.getId());
		boolean roleBoolean = rolecodes.contains(operatorRoleCode);
		return roleBoolean;
	}

	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public Set<Integer> getSupplierIds() {
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
		//权限级别
		Integer levelCode = currentLoginUser.getLevel();
		PermissionLevelEnum enumByCode = PermissionLevelEnum.getEnumByCode(levelCode);
		switch (enumByCode) {
			case ALL:
				return new HashSet<>();
			case CITY:
				//查询权限城市下的所有供应商ID
				Set<Integer> cityIds = currentLoginUser.getCityIds();
				Map<String, Set<Integer>> cityIdsMap = new HashMap<>();
				cityIdsMap.put("cityIds", cityIds);
				List<Integer> supplierIds = busCarBizSupplierExMapper.querySupplierIdByCitys(cityIdsMap);
				Set<Integer> supplierIdSet= supplierIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
				return supplierIdSet;
			case SUPPLIER:
				Set<Integer> userSupplierSet = currentLoginUser.getSupplierIds();
				return userSupplierSet;
			default:
				return null;
		}

	}
}