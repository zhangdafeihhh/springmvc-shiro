package com.zhuanche.controller.busManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.mdbcarmanage.ex.BusBizSupplierDetailExMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@RestController
@RequestMapping("/bus/supplier")
public class BusSupplierController {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierController.class);

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	@Autowired
	private BusBizSupplierDetailExMapper busBizSupplierDetailExMapper;

	// ===========================专车业务拓展service==================================

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusSupplierService busSupplierService;

	/**
	 * @Title: saveSupplier
	 * @Description: 保存/修改供应商
	 * @param baseDTO
	 * @param detailDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/saveSupplier")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER) })
	@Validated
	public AjaxResponse saveSupplier(BusSupplierBaseDTO baseDTO, BusSupplierDetailDTO detailDTO) {// TODO 封装分佣、返点信息
		return busSupplierService.saveSupplierInfo(baseDTO, detailDTO);
	}
	
	/**
	 * @Title: querySupplierPageList
	 * @Description: 查询供应商分页列表
	 * @param baseDTO
	 * @param detailDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/querySupplierPageList")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse querySupplierPageList(BusSupplierQueryDTO queryDTO) {
		
		// 数据权限控制SSOLoginUser
		Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
		Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
		queryDTO.setCityIds(permOfCity);
		queryDTO.setSupplierIds(permOfSupplier);
		queryDTO.setTeamIds(permOfTeam);
		
		// 一、TODO 调用分佣接口返回ids
		List<Integer> commissionIds = new ArrayList<>();
		// 二、查询快合同快到期供应商
		Map<Object,Object> param = new HashMap<>();
		param.put("commissionIds", commissionIds);
		List<BusBizSupplierDetail> list = busBizSupplierDetailExMapper.querySupplierList(param);
		
		
		
		// 按照先按合同时间排序,再按创建时间排序
		// 可先按bus_biz_supplier_detail查询合同时间<3个月的，先对detail表分页，直到某页数<pageSize时，再以pageNum=1,pageSize=pageSize-count(detail)去主表做分页
		List<BusSupplierPageVO> mainList = busSupplierService.querySupplierListByDetail(queryDTO);
		Page<BusSupplierPageVO> page = (Page<BusSupplierPageVO>) mainList;

		try(Page p = PageHelper.startPage(0, 0, true)) {
			List<BusSupplierPageVO> otherList  = busSupplierService.querySupplierListByMaster(queryDTO);
		}
		
		return AjaxResponse.success(new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), mainList););
	}
	
	
}
