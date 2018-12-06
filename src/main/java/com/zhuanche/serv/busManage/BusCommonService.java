package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.BusBizChangeLog;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.shiro.session.WebSessionUtil;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper;
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

	private static final Logger logger = LoggerFactory.getLogger(BusCommonService.class);

	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;
	
	@Autowired
	private BusBizChangeLogExMapper busBizChangeLogExMapper;
	
	/**
	 * @Title: queryChangeLogs
	 * @Description: 查询操作日志
	 * @param businessType
	 * @param businessKey
	 * @return 
	 * @return List<BusBizChangeLog>
	 * @throws
	 */
	public List<BusBizChangeLog> queryChangeLogs(String businessType,
			String businessKey) {
		Map<String,Object> param = new HashMap<>();
		param.put("businessType", businessType);
		param.put("businessKey", businessKey);
		return busBizChangeLogExMapper.queryRecnetlyChangeLogs(param);
	}

	/**
	 * @Title: querySuppliers
	 * @Description: 查询供应商
	 * @param cityId
	 * @return 
	 * @return List<CarBizSupplier>
	 * @throws
	 */
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	public List<CarBizSupplier> querySuppliers(Integer cityId) {
		// 数据权限控制SSOLoginUser
		Set<Integer> authOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> authOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID

		if (authOfCity != null && authOfCity.contains(cityId)) {
			Map<String, Object> param = new HashMap<>();
			param.put("cityId", cityId);
			param.put("authOfSupplier", authOfSupplier);
			return busCarBizSupplierExMapper.querySuppliers(param);
		}
		return null;
	}

}
