package com.zhuanche.controller.busManage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@RestController
@RequestMapping("/bus/supplier")
public class BusSupplierController {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierController.class);

	// ===========================巴士业务拓展mapper==================================

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

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
	@SuppressWarnings("resource")
	@RequestMapping(value = "/querySupplierPageList")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	@Validated
	public AjaxResponse querySupplierPageList(BusSupplierQueryDTO queryDTO) {
		Integer pageNum = queryDTO.getPageNum();
		Integer pageSize = queryDTO.getPageSize();
		
		// 查询列表
		List<BusSupplierPageVO> resultList = busSupplierService.queryBusSupplierPageList(queryDTO);
        
        // 计算total
		queryDTO.setPageNum(pageNum);
		queryDTO.setPageSize(pageSize);
        queryDTO.setContractIds(null);
		queryDTO.setExcludeContractIds(null);
        List<BusSupplierPageVO> totalList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
        Page<BusSupplierPageVO> page = (Page<BusSupplierPageVO>) totalList;
		return AjaxResponse.success(new PageDTO(queryDTO.getPageNum(), queryDTO.getPageSize(), page.getTotal(), resultList));
	}
	
	

}
