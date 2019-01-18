package com.zhuanche.controller.busManage;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierCommissionInfoDTO;
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.dto.busManage.BusSupplierProrateDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.dto.busManage.BusSupplierRebateDTO;
import com.zhuanche.serv.busManage.BusBizChangeLogService;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@RestController
@RequestMapping("/bus/supplier")
@Validated
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

	@Autowired
	private BusBizChangeLogService busBizChangeLogService;

	// ============================巴士共有服务service==================================

	@Autowired
	@Qualifier("hibernateValidator")
	private Validator validator;

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
	public AjaxResponse saveSupplier(@Validated BusSupplierBaseDTO baseDTO, @Validated BusSupplierDetailDTO detailDTO,
			@Validated BusSupplierCommissionInfoDTO commissionDTO, String prorateList, String rebateList) {
		// 分佣
		JSONArray prorateArray = null;
		try {
			prorateArray = JSONArray.parseArray(prorateList);
		} catch (Exception e) {
			logger.error("[ BusSupplierController-saveSupplier ] 分佣协议格式不正确", e.getMessage(), e);
			return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "分佣协议格式不正确");
		}
		List<BusSupplierProrateDTO> prorates = new ArrayList<>();
		if (prorateArray != null) {
			prorateArray.stream().forEach(e -> {
				JSONObject jsonObject = (JSONObject) JSON.toJSON(e);
				BusSupplierProrateDTO prorate = JSON.toJavaObject(jsonObject, BusSupplierProrateDTO.class);
				prorates.add(prorate);
			});
			for (BusSupplierProrateDTO prorateDTO : prorates) {
				BindingResult result = new BeanPropertyBindingResult(prorateDTO, "prorateDTO");
				validator.validate(prorateDTO, result);
				if (result.hasErrors()) {
					String errors = result.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList()).toString();
					return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, errors);
				}
			}
		}
		
		// 返点
		JSONArray rebateArray = null;
		try {
			rebateArray = JSONArray.parseArray(rebateList);
		} catch (Exception e) {
			logger.error("[ BusSupplierController-saveSupplier ] 返点协议格式不正确", e.getMessage(), e);
			return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "返点协议格式不正确");
		}
		List<BusSupplierRebateDTO> rebates = new ArrayList<>();
		if (rebateArray != null) {
			rebateArray.stream().forEach(e -> {
				JSONObject jsonObject = (JSONObject) JSON.toJSON(e);
				BusSupplierRebateDTO prorate = JSON.toJavaObject(jsonObject, BusSupplierRebateDTO.class);
				rebates.add(prorate);
			});
			for (BusSupplierRebateDTO rebateDTO : rebates) {
				BindingResult result = new BeanPropertyBindingResult(rebateDTO, "rebateDTO");
				validator.validate(rebateDTO, result);
				if (result.hasErrors()) {
					String errors = result.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList()).toString();
					return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, errors);
				}
			}
		}
		
		// 一、判断
		Integer supplierId = baseDTO.getSupplierId();
		boolean isAdd = true;
		List<Object> old = null;
		if (supplierId != null && supplierId != 0) {
			isAdd = false;
			old = busSupplierService.getContents(supplierId);
		}
		
		// 二、保存数据
		AjaxResponse response = busSupplierService.saveSupplierInfo(baseDTO, detailDTO, commissionDTO, prorates, rebates);
		
		// 三、保存操作记录
		if (isAdd) {
			busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), "创建供应商", new Date());
		} else {
			List<Object> fresh = busSupplierService.getContents(supplierId);;
			busSupplierService.saveChangeLog(supplierId, old, fresh);
		}
		
		return response;
	}
	
	/**
	 * @Title: deleteProrate
	 * @Description: 根据ID删除供应商分佣协议
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/deleteProrate")
	public AjaxResponse deleteProrate(@NotNull(message = "id不能为空") Long id) {
		return busSupplierService.deleteProrate(id);
	}

	/**
	 * @Title: deleteRebate
	 * @Description: 根据ID删除供应商返点协议
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/deleteRebate")
	public AjaxResponse deleteRebate(@NotNull(message = "id不能为空") Integer id) {
		return busSupplierService.deleteRebate(id);
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
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public AjaxResponse querySupplierPageList(@Validated BusSupplierQueryDTO queryDTO) {
		Integer pageNum = queryDTO.getPageNum();
		Integer pageSize = queryDTO.getPageSize();

		// 数据权限控制SSOLoginUser
		Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
		Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
		queryDTO.setAuthOfCity(permOfCity);
		queryDTO.setAuthOfSupplier(permOfSupplier);
		queryDTO.setAuthOfTeam(permOfTeam);
		// 按结算比例筛选供应商
		if (queryDTO.getSupplierRate() != null) {
			List<Integer> supplierRateIds = new ArrayList<>();
			Optional.ofNullable(busSupplierService.getSupplierByProrateRate(queryDTO.getSupplierRate())).orElseGet(JSONArray::new).forEach(item -> {
				supplierRateIds.add((Integer) item);
			});
			queryDTO.setSupplierRateIds(supplierRateIds);
		}
		// 一、查询供应商列表（如果有合同快到期的情况下）
		List<BusSupplierPageVO> resultList = busSupplierService.queryBusSupplierPageList(queryDTO);
        
        // 二、计算total
		queryDTO.setPageNum(pageNum);
		queryDTO.setPageSize(pageSize);
        queryDTO.setContractIds(null);
		queryDTO.setExcludeContractIds(null);
		logger.info("[ BusSupplierController-querySupplierPageList ] 查询供应商分页列表params={}", JSON.toJSONString(queryDTO));
        List<BusSupplierPageVO> totalList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
        Page<BusSupplierPageVO> page = (Page<BusSupplierPageVO>) totalList;
        if (resultList == null || resultList.isEmpty()) {
        	resultList = totalList;
        	if (resultList == null || resultList.isEmpty()) {
        		return AjaxResponse.success(new PageDTO(queryDTO.getPageNum(), queryDTO.getPageSize(), page.getTotal(), resultList));
			} else {
				// 补充巴士供应商其它信息
				resultList.forEach(busSupplierService::completeDetailInfo);
			}
        }
        
        // 三、补充信息
		String supplierIds = resultList.stream().map(e -> e.getSupplierId().toString()).collect(Collectors.joining(","));
		JSONArray jsonArray = busSupplierService.getProrateList(supplierIds);
		// 组装数据
		resultList.forEach(supplier -> {
			Integer supplierId = supplier.getSupplierId();
			// 补充分佣信息(分佣比例、是否有返点)
			if (jsonArray != null) {
				// 查找对应供应商数据
				jsonArray.stream().filter(e -> {
					JSONObject jsonObject = (JSONObject) JSON.toJSON(e);
					return supplierId.equals(jsonObject.getInteger("supplierId"));
				}).findFirst().ifPresent(e -> {
					JSONObject jsonObject = (JSONObject) JSON.toJSON(e);
					supplier.setSupplierRate(jsonObject.getDouble("supplierRate"));
					supplier.setIsRebate(jsonObject.getInteger("isRebate"));
				});
			}
		});
		return AjaxResponse.success(new PageDTO(queryDTO.getPageNum(), queryDTO.getPageSize(), page.getTotal(), resultList));
	}

	/**
	 * @Title: exportSupplierList
	 * @Description: 导出供应商列表
	 * @param queryDTO
	 * @param request
	 * @param response
	 * @return
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/exportSupplierList")
	public void exportSupplierList(@Validated BusSupplierQueryDTO queryDTO, HttpServletRequest request,
			HttpServletResponse response) {
        long start = System.currentTimeMillis(); // 获取开始时间
		try {
			// 数据权限控制SSOLoginUser
			Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
			Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
			Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
			queryDTO.setAuthOfCity(permOfCity);
			queryDTO.setAuthOfSupplier(permOfSupplier);
			queryDTO.setAuthOfTeam(permOfTeam);
			// 按结算比例筛选供应商
			if (queryDTO.getSupplierRate() != null) {
				List<Integer> supplierRateIds = new ArrayList<>();
				Optional.ofNullable(busSupplierService.getSupplierByProrateRate(queryDTO.getSupplierRate())).orElseGet(JSONArray::new).forEach(item -> {
					supplierRateIds.add((Integer) item);
				});
				queryDTO.setSupplierRateIds(supplierRateIds);
			}

			// 文件名
			LocalDateTime now = LocalDateTime.now();
			String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
			String fileName = "供应商信息" + suffix + ".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); // 获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) { // IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else { // 其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}

			CsvUtils utilEntity = new CsvUtils();

			/**导出逻辑*/
			// 查询数据
			queryDTO.pageNum = null;
			queryDTO.pageSize = null;
			List<BusSupplierExportVO> list = busSupplierService.querySupplierExportList(queryDTO);

			// 数据区
			// 如果查询结果为空
			if (list == null || list.isEmpty()) {
				logger.info("[ BusSupplierController-exportSupplierList ] 导出条件params={}没有查询出对应的巴士供应商信息", JSON.toJSONString(queryDTO));
				List<String> csvDataList = new ArrayList<>();
				csvDataList.add("没有查到符合条件的数据");
				utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);
			}
			
			// 导出查询数据
			List<String> csvDataList = busSupplierService.completeSupplierExportList(list);// 补充其它字段
			utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);

			// 获取结束时间
			long end = System.currentTimeMillis();
			logger.info("巴士供应商导出成功，参数为：" + JSON.toJSONString(queryDTO) + ",耗时=" + (end - start) + "ms");
		} catch (Exception e) {
			// 获取结束时间
			long end = System.currentTimeMillis();
			logger.error("巴士供应商导出成功，参数为：" + JSON.toJSONString(queryDTO) + ",耗时=" + (end - start) + "ms", e);
		}
	}

	/**
	 * @Title: querySupplierById
	 * @Description: 查询供应商详情
	 * @param supplierId
	 * @param prorateStatus
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/querySupplierById")
	public AjaxResponse querySupplierById(@NotNull(message = "供应商ID不能为空") Integer supplierId, Integer prorateStatus) {
		BusSupplierInfoVO supplierVO = busSupplierService.querySupplierById(supplierId, prorateStatus);
		return AjaxResponse.success(supplierVO);
	}

}
