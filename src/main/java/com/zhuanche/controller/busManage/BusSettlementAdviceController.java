package com.zhuanche.controller.busManage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.busManage.BusSupplierSettleListDTO;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusSettlementAdviceService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

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

	private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceController.class);

	@Autowired
	private BusCommonService busCommonService;
	
	@Autowired
	private BusSettlementAdviceService busSettlementAdviceService;

	/**
	 * 查询供应商分佣订单明细 TODO 等计费完成接口完善
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/pageList")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse querySettleDetailList(BusSupplierSettleListDTO dto) {
		logger.info("巴士供应商查询账单列表参数=" + JSON.toJSONString(dto));
		SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
		Set<Integer> supplierIds = loginUser.getSupplierIds();
		Set<Integer> cityIds = loginUser.getCityIds();
		Set<Integer> teamIds = loginUser.getTeamIds();
		dto.setAuthOfCity(cityIds);
		dto.setAuthOfSupplier(supplierIds);
		dto.setAuthOfTeam(teamIds);
		Integer cityId = dto.getCityId();
		// 按照城市查询，需要查询该城市下所有的供应商
		if (cityId != null && StringUtils.isBlank(dto.getSupplierIds())) {
			List<Map<Object, Object>> maps = busCommonService.querySuppliers(cityId);
			if (maps.isEmpty()) {
				return AjaxResponse.success(new ArrayList<>());
			}
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> map : maps) {
				String supplierId = String.valueOf(map.get("supplierId"));
				sb.append(supplierId).append(",");
			}
			dto.setSupplierIds(sb.substring(0, sb.length() - 1));
		}
		// 城市条件和供应商条件都没有，获取session中的supplierid
		if (cityId == null && StringUtils.isBlank(dto.getSupplierIds())) {
			if (supplierIds != null && !supplierIds.isEmpty()) {
				StringBuffer sb = new StringBuffer();
				for (Integer supplierId : supplierIds) {
					sb.append(supplierId).append(",");
				}
				dto.setSupplierIds(sb.substring(0, sb.length() - 1));
			}
		}
		JSONArray array = busSettlementAdviceService.querySettleDetailList(dto);
		return AjaxResponse.success(array);
	}
}
