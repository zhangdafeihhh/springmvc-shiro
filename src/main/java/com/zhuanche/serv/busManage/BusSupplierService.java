package com.zhuanche.serv.busManage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusSupplierBaseDTO;
import com.zhuanche.dto.busManage.BusSupplierCommissionInfoDTO;
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.dto.busManage.BusSupplierProrateDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.dto.busManage.BusSupplierRebateDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverInfoTempService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import mapper.mdbcarmanage.ex.BusBizSupplierDetailExMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Validated
public class BusSupplierService implements BusConst {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierService.class);
	
	// ===========================表基础mapper==================================

	// ===========================专车业务拓展mapper==================================

	// ===========================巴士业务拓展mapper==================================
	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	@Autowired
	private BusBizSupplierDetailExMapper busBizSupplierDetailExMapper;

	// ===========================专车业务拓展service==================================
	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Autowired
	private CarBizDriverInfoTempService carBizDriverInfoTempService;

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusBizChangeLogService busBizChangeLogService;

	// ===============================专车其它服务===================================

	// ===============================巴士其它服务===================================
	@Value("${order.pay.url}")
	private String orderPayUrl;

	/**
	 * @param rebateDTO 
	 * @param prorateDTO 
	 * @param commissionDTO 
	 * @Title: saveSupplierInfo
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
	public AjaxResponse saveSupplierInfo(BusSupplierBaseDTO baseDTO, BusSupplierDetailDTO detailDTO,
			BusSupplierCommissionInfoDTO commissionDTO, @Validated List<BusSupplierProrateDTO> prorateList,
			@Validated List<BusSupplierRebateDTO> rebateList) {
		Method method = Method.UPDATE;
		int f = 0;
		Integer supplierId = baseDTO.getSupplierId();
		// 一、操作主表
		baseDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
		if (supplierId == null || supplierId == 0) {
			method = Method.CREATE;
			baseDTO.setAddress(detailDTO.getInvoiceCompanyAddr());// 默认发票公司地址
			baseDTO.setSupplierType(1);// 巴士供应商
			baseDTO.setEnterpriseType(2);// 非客运企业
			baseDTO.setIsTwoShifts(1);// 双班
			baseDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());// 操作人ID
			f = busCarBizSupplierExMapper.insertSelective(baseDTO);
			supplierId = baseDTO.getSupplierId();// 返回ID
		} else {
			f = busCarBizSupplierExMapper.updateByPrimaryKeySelective(baseDTO);
		}
		// 二、操作巴士供应商专用表
		detailDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
		if (busBizSupplierDetailExMapper.checkExist(supplierId) > 0) {// 校验是否存在
			busBizSupplierDetailExMapper.updateBySupplierIdSelective(detailDTO);
		} else {
			// 将主表ID赋值给巴士供应商表
			detailDTO.setSupplierId(supplierId);
			detailDTO.setCreateBy(WebSessionUtil.getCurrentLoginUser().getId());// 操作人ID
			busBizSupplierDetailExMapper.insertSelective(detailDTO);
		}

		// 三、查询供应商底下是否有司机，并修改司机加盟类型
		if (Method.UPDATE.equals(method)) {
			carBizDriverInfoService.updateDriverCooperationTypeBySupplierId(baseDTO.getSupplierId(), baseDTO.getCooperationType());
			carBizDriverInfoTempService.updateDriverCooperationTypeBySupplierId(baseDTO.getSupplierId(), baseDTO.getCooperationType());
		}

		// 四、调用分佣接口，修改分佣、返点信息
		StringBuilder errorMsg = new StringBuilder();

		String commissionMsg = saveSupplierCommission(commissionDTO, supplierId, method);// 分佣基本信息
		if (StringUtils.isNotBlank(commissionMsg)) {
			errorMsg.append(commissionMsg).append(";");
		}

		String prorateMsg = saveSupplierProrate(prorateList, supplierId, method);// 分佣信息
		if (StringUtils.isNotBlank(prorateMsg)) {
			errorMsg.append(prorateMsg).append(";");
		}

		String rebateMsg = saveSupplierRebate(rebateList, supplierId, method);// 返点信息
		if (StringUtils.isNotBlank(rebateMsg)) {
			errorMsg.append(rebateMsg).append(";");
		}

		// 五、保存操作记录
		busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), new Date());

		// 六、MQ消息写入 供应商
		try {
			Map<String, Object> msgMap = new HashMap<String, Object>();
			msgMap.put("method", method);
			msgMap.put("data", JSON.toJSONString(baseDTO));
			logger.info("专车供应商，同步发送数据：", JSON.toJSONString(msgMap));
			CommonRocketProducer.publishMessage("vipSupplierTopic", method.name(), String.valueOf(baseDTO.getSupplierId()), msgMap);
		} catch (Exception e) {
			logger.error("[ BusSupplierService-saveSupplierInfo ] 供应商信息发送MQ出错", e.getMessage(), e);
		}
		logger.info("***********************新增/修改 厂商信息  END***********************");
		if (f > 0) {
			return AjaxResponse.success(null);
		} else {
			if (StringUtils.isBlank(errorMsg.toString())) {
				return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "保存供应商失败");
			} else {
				return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "保存分佣结算信息失败:" + errorMsg.toString());
			}
		}
	}
	
	enum Method {
		UPDATE, CREATE
	}

	/**
	 * @Title: deleteProrate
	 * @Description: 根据ID删除供应商分佣协议
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	public AjaxResponse deleteProrate(@NotNull(message = "id不能为空") Long id) {
		String errorMsg = prorateDelete(id);
		if (StringUtils.isNotBlank(errorMsg)) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, errorMsg);
		}
		return AjaxResponse.success(null);
	}

	/**
	 * @Title: deleteRebate
	 * @Description: 根据ID删除供应商返点协议
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	public AjaxResponse deleteRebate(@NotNull(message = "id不能为空") Integer id) {
		String errorMsg = rebateDelete(id);
		if (StringUtils.isNotBlank(errorMsg)) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, errorMsg);
		}
		return AjaxResponse.success(null);
	}

	/**
	 * @param method 
	 * @Title: saveSupplierCommission
	 * @Description: 保存分佣基本信息
	 * @param commissionDTO
	 * @param supplierId
	 * @return 
	 * @return String
	 * @throws
	 */
	private String saveSupplierCommission(BusSupplierCommissionInfoDTO commissionDTO, Integer supplierId, Method method) {
		commissionDTO.setSupplierId(supplierId);
		commissionDTO.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
		
		Long id = commissionDTO.getId();// 主键
		String url = null;
		if (id == null) {
			url = orderPayUrl + Pay.SETTLE_SUPPLIER_INFO_ADD;
			commissionDTO.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
		} else {
			url = orderPayUrl + Pay.SETTLE_SUPPLIER_INFO_UPDATE;
		}
		
		String jsonString = JSON.toJSONStringWithDateFormat(commissionDTO, JSON.DEFFAULT_DATE_FORMAT);
		JSONObject json = (JSONObject) JSONObject.parse(jsonString);
		Map<String, Object> params = json.getInnerMap();
		try {
			logger.info("[ BusSupplierService-saveSupplierCommission ] 保存分佣基本信息,params={},url={}", params, url);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params , 2000, "保存分佣基本信息");
			if (result.getIntValue("code") != 0) {
				String msg = result.getString("msg");
				logger.info("[ BusSupplierService-saveSupplierCommission ] 保存分佣基本信息调用接口出错,params={},errorMsg={}", params, msg);
				return msg;
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-saveSupplierCommission ] 保存分佣基本信息异常,params={},errorMsg={}", params, e.getMessage(), e);
			return "保存分佣基本信息" + (id == null ? "调用新增接口失败" : "调用修改接口失败");
		}
		return null;
	}

	/**
	 * @param method 
	 * @Title: saveSupplierProrate
	 * @Description: 保存供应商分佣信息
	 * @param prorateDTOList
	 * @param supplierId
	 * @return 
	 * @return String
	 * @throws
	 */
	private String saveSupplierProrate(List<BusSupplierProrateDTO> prorateList, Integer supplierId, Method method) {
		if (prorateList == null || prorateList.isEmpty()) {
			return null;
		}

		for (BusSupplierProrateDTO prorate : prorateList) {
			prorate.setSupplierId(supplierId);
			prorate.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());

			Long id = prorate.getId();// 主键
			String url = null;
			if (id == null) {
				url = orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_ADD;
				prorate.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
			} else {
				url = orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_UPDATE;
			}

			String jsonString = JSON.toJSONStringWithDateFormat(prorate, JSON.DEFFAULT_DATE_FORMAT);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> params = json.getInnerMap();
			try {
				logger.info("[ BusSupplierService-saveSupplierProrate ] 保存供应商分佣信息,params={},url={}", params, url);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 2000, "保存供应商分佣信息");
				if (result.getIntValue("code") != 0) {
					String msg = result.getString("msg");
					logger.info("[ BusSupplierService-saveSupplierProrate ] 保存供应商分佣信息调用接口出错,params={},errorMsg={}", params, msg);
					return msg;
				}
			} catch (Exception e) {
				logger.error("[ BusSupplierService-saveSupplierProrate ] 保存供应商分佣信息异常,params={},errorMsg={}", params, e.getMessage(), e);
				return "保存供应商分佣信息" + (id == null ? "调用新增接口失败" : "调用修改接口失败");
			}
		}
		return null;
	}

	/**
	 * @param method 
	 * @Title: saveSupplierRebate
	 * @Description: 保存供应商返点信息
	 * @param rebateDTOList
	 * @param supplierId
	 * @return 
	 * @return String
	 * @throws
	 */
	private String saveSupplierRebate(List<BusSupplierRebateDTO> rebateList, Integer supplierId, Method method) {
		if (rebateList == null || rebateList.isEmpty()) {
			return null;
		}

		for (BusSupplierRebateDTO rebate : rebateList) {
			rebate.setSupplierId(supplierId);
			rebate.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());

			Integer id = rebate.getId();// 主键
			String url = null;
			if (id == null) {
				url = orderPayUrl + Pay.SETTLE_SUPPLIER_REBATE_ADD;
				rebate.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
			} else {
				url = orderPayUrl + Pay.SETTLE_SUPPLIER_REBATE_UPDATE;
			}
			
			String jsonString = JSON.toJSONStringWithDateFormat(rebate, JSON.DEFFAULT_DATE_FORMAT);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> params = json.getInnerMap();
			try {
				logger.info("[ BusSupplierService-saveSupplierRebate ] 保存供应商返点信息,params={},url={}", params, url);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 2000, "保存供应商返点信息");
				if (result.getIntValue("code") != 0) {
					String msg = result.getString("msg");
					logger.info("[ BusSupplierService-saveSupplierRebate ] 保存供应商返点信息调用接口出错,params={},errorMsg={}", params, msg);
					return msg;
				}
			} catch (Exception e) {
				logger.error("[ BusSupplierService-saveSupplierRebate ] 保存供应商返点信息异常,params={},errorMsg={}", params, e.getMessage(), e);
				return "保存供应商返点信息" + (id == null ? "调用新增接口失败" : "调用修改接口失败");
			}
		}
		return null;
	}

	/**
	 * @Title: prorateDelete
	 * @Description: 供应商分佣协议删除
	 * @param id
	 * @return String
	 * @throws
	 */
	private String prorateDelete(Long id) {
		if (id == null) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("updateName", WebSessionUtil.getCurrentLoginUser().getName());
		try {
			String url = orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_DELETE;
			logger.info("[ BusSupplierService-prorateDelete ] 供应商分佣协议删除,params={},url={}", params, url);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 2000, "供应商分佣协议删除");
			if (result.getIntValue("code") != 0) {
				String msg = result.getString("msg");
				logger.info("[ BusSupplierService-prorateDelete ] 供应商分佣协议删除,调用接口出错,params={},errorMsg={}", params, msg);
				return msg;
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-prorateDelete ] 供应商分佣协议删除,异常,params={},errorMsg={}", params, e.getMessage(), e);
			return "供应商分佣协议删除异常";
		}
		return null;
	}

	/**
	 * @Title: rebateDelete
	 * @Description: 供应商返点协议删除
	 * @param id
	 * @return String
	 * @throws
	 */
	private String rebateDelete(Integer id) {
		if (id == null) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("updateName", WebSessionUtil.getCurrentLoginUser().getName());
		try {
			String url = orderPayUrl + Pay.SETTLE_SUPPLIER_REBATE_DELETE;
			logger.info("[ BusSupplierService-rebateDelete ] 供应商返点协议删除,params={},url={}", params, url);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 2000, "供应商返点协议删除");
			if (result.getIntValue("code") != 0) {
				String msg = result.getString("msg");
				logger.info("[ BusSupplierService-rebateDelete ] 供应商返点协议删除,调用接口出错,params={},errorMsg={}", params, msg);
				return msg;
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-rebateDelete ] 供应商返点协议删除,异常,params={},errorMsg={}", params, e.getMessage(), e);
			return "供应商返点协议删除异常";
		}
		return null;
	}

	/**
	 * @Title: queryBusSupplierPageList
	 * @Description: 查询巴士供应商列表
	 * @param queryDTO
	 * @return List<BusSupplierPageVO>
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public List<BusSupplierPageVO> queryBusSupplierPageList(BusSupplierQueryDTO queryDTO) {
		
		logger.info("[ BusSupplierController-querySupplierPageList ] 查询供应商分页列表params={}", JSON.toJSONString(queryDTO));
		
		Integer pageNum = queryDTO.getPageNum();
		Integer pageSize = queryDTO.getPageSize();
		queryDTO.pageNum = null;
		queryDTO.pageSize = null;
		
		// 一、查询快合同快到期供应商
		Map<Object,Object> param = new HashMap<>();
		param.put("permOfSupplier", queryDTO.getAuthOfSupplier());
		List<BusSupplierPageVO> contractList = busBizSupplierDetailExMapper.querySupplierContractExpireSoonList(param);
		if (contractList.isEmpty()) {
			return null;// 没有则走正常查询
		}
		// 二、所有快到期的供应商的基础信息
		List<Integer> contractIds = contractList.stream().map(BusSupplierPageVO::getSupplierId).collect(Collectors.toList());
		queryDTO.setContractIds(contractIds);
		List<BusSupplierPageVO> contractSuppliers = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
		int offset = (Math.max(pageNum, 1) - 1) * pageSize;
		int limit = offset + pageSize;
		// 三、封装结果集(按区间分别查询:完全不在快到期区间、一部分在快到期区间、完全在快到期区间)
		List<BusSupplierPageVO> resultList = new ArrayList<>();
		if (offset > contractSuppliers.size()) {
			queryDTO.setContractIds(null);
			queryDTO.setExcludeContractIds(contractIds);// 其它供应商(not in 上面的供应商)
			List<BusSupplierPageVO> supplierList = null;
			try(Page p = PageHelper.offsetPage(offset - contractSuppliers.size(), limit)) {
				supplierList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
			}
			supplierList.forEach(this::completeDetailInfo);// 补充巴士供应商其它信息
			resultList.addAll(supplierList);
		} else {
			// 组合
			List<BusSupplierPageVO> allContractList = new ArrayList<>();
			for (BusSupplierPageVO carBizSupplier : contractSuppliers) {
				for (BusSupplierPageVO busSupplierDetail : contractList) {
					if (carBizSupplier.getSupplierId().equals(busSupplierDetail.getSupplierId())) {
						carBizSupplier.setDeposit(busSupplierDetail.getDeposit());
						carBizSupplier.setFranchiseFee(busSupplierDetail.getFranchiseFee());
						carBizSupplier.setContractDateStart(busSupplierDetail.getContractDateStart());
						carBizSupplier.setContractDateEnd(busSupplierDetail.getContractDateEnd());
						carBizSupplier.setIsExpireSoon(busSupplierDetail.getIsExpireSoon());
						allContractList.add(carBizSupplier);
					}
				}
			}
			// 排序
			List<BusSupplierPageVO> orderedAllContractList = allContractList.stream()
					.sorted(Comparator.comparing(BusSupplierPageVO::getContractDateEnd).reversed())
					.collect(Collectors.toList());
			if (limit > contractSuppliers.size()) {
				// 合同快到期供应商
				List<BusSupplierPageVO> subList = orderedAllContractList.subList(offset, contractSuppliers.size());
				// 正常供应商
				queryDTO.setContractIds(null);
				queryDTO.setExcludeContractIds(contractIds);// 其它供应商(not in 上面的供应商)
				List<BusSupplierPageVO> otherList = null;
				try (Page p = PageHelper.offsetPage(0, limit - contractSuppliers.size())) {
					otherList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
				}
				otherList.forEach(this::completeDetailInfo);// 补充巴士供应商其它信息
				
				resultList.addAll(subList);
				resultList.addAll(otherList);
			} else {
				// 合同快到期供应商
				List<BusSupplierPageVO> subList = orderedAllContractList.subList(offset, limit);
				resultList.addAll(subList);
			}
		}
		
		return resultList;
	}

	/**
	 * @Title: completeDetailInfo
	 * @Description: 补充巴士供应商信息
	 * @param t 
	 * @return void
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public <T> void completeDetailInfo(T t) {
		if (t == null) {
			return;
		}
		BusBizSupplierDetail scope = new BusBizSupplierDetail();
		BeanUtils.copyProperties(t, scope);

		BusBizSupplierDetail detail = busBizSupplierDetailExMapper.selectBySupplierId(scope.getSupplierId());

		// 返回补充的信息
		if (detail != null) {
			BeanUtils.copyProperties(detail, t);
		}
	}
	
	/**
	 * @Title: querySupplierExportList
	 * @Description: 查询供应商导出列表
	 * @param queryDTO
	 * @return 
	 * @return List<BusDriverInfoExportVO>
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public List<BusSupplierExportVO> querySupplierExportList(BusSupplierQueryDTO queryDTO) {
		List<BusSupplierExportVO> supplierList = busCarBizSupplierExMapper.querySupplierExportList(queryDTO);
		return supplierList;
	}

	/**
	 * @Title: completeSupplierExportList
	 * @Description: 补充其它信息
	 * @param list
	 * @return 
	 * @return List<String>
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE))
	public List<String> completeSupplierExportList(List<BusSupplierExportVO> list) {
		// 返回结果
		List<String> csvDataList = new ArrayList<>();
		if (list == null || list.isEmpty()) {
			return csvDataList;
		}
		// 补充分佣信息(分佣比例、是否有返点)
		String supplierIds = list.stream().map(e -> e.getSupplierId().toString()).collect(Collectors.joining(","));
		JSONArray jsonArray = getProrateList(supplierIds);
		list.forEach(supplier -> {
			
			if (jsonArray != null) {
				// 查找对应供应商数据
				Integer supplierId = supplier.getSupplierId();
				jsonArray.stream().filter(e -> {
					JSONObject jsonObject = (JSONObject) JSON.toJSON(e);
					return supplierId.equals(jsonObject.getInteger("supplierId"));
				}).findFirst().ifPresent(e -> {
					JSONObject jsonObject = (JSONObject) JSON.toJSON(e);
					supplier.setSupplierRate(jsonObject.getDouble("supplierRate"));
					supplier.setIsRebate(jsonObject.getInteger("isRebate"));
				});
			}
			
			/** 行数据 */
			StringBuilder builder = new StringBuilder();
			BusBizSupplierDetail detail = busBizSupplierDetailExMapper.selectBySupplierId(supplier.getSupplierId());
			detail = detail == null ? new BusBizSupplierDetail() : detail;
	
			//====================================基本信息===========================================
			// 一、城市
			String ciytName = supplier.getCityName();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(ciytName, "")).append(",");
			
			// 二、供应商ID
			Integer supplierId = supplier.getSupplierId();
			builder.append(CsvUtils.tab).append(String.valueOf(supplierId)).append(",");
	
			// 三、供应商名称
			String supplierName = supplier.getSupplierName();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(supplierName, "")).append(",");
	
			// 四、状态
			String status = supplier.getStatus() != null && supplier.getStatus() == 1 ? "有效" : "无效";
			builder.append(CsvUtils.tab).append(status).append(",");

			// 五、企业联系人
			String contacts = supplier.getContacts();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(contacts, "")).append(",");
			
			// 六、企业联系人电话
			String contactsPhone = supplier.getContactsPhone();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(contactsPhone, "")).append(",");
			
			// 七、调度员电话
			String dispatcherPhone = supplier.getDispatcherPhone();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(dispatcherPhone, "")).append(",");
			
			// 八、加盟类型
			String cooperationName = supplier.getCooperationName();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(cooperationName, "")).append(",");
			
//			// 七、合同开始时间
//			Date contractDateStart = detail.getContractDateStart();
//			String contractDateStartFormatter = "";
//			if (contractDateStart != null) {
//				contractDateStartFormatter = formatDate(FORMATTER_DATE_BY_HYPHEN, contractDateStart);
//			}
//			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(contractDateStartFormatter, "")).append(",");
//			
//			// 八、合同到期时间
//			Date contractDateEnd = detail.getContractDateEnd();
//			String contractDateEndFormatter = "";
//			if (contractDateEnd != null) {
//				contractDateEndFormatter = formatDate(FORMATTER_DATE_BY_HYPHEN, contractDateEnd);
//			}
//			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(contractDateEndFormatter, "")).append(",");
			//====================================结算信息===========================================
			
			// 三、分佣比例
			Double supplierRate = supplier.getSupplierRate();
			builder.append(CsvUtils.tab).append(supplierRate == null ? 0.00 : supplierRate).append(",");
			
			//====================================付款信息===========================================
			// 一、账户名称
			String invoiceCompanyName = detail.getInvoiceCompanyName();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceCompanyName, "")).append(",");
			
			// 二、开户银行
			String invoiceDepositBank = detail.getInvoiceDepositBank();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceDepositBank, "")).append(",");
			
			// 三、银行账户
			String invoiceBankAccount = detail.getInvoiceBankAccount();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceBankAccount, "")).append(",");
			
			// 四、电话号码
			String invoiceCompanyPhone = detail.getInvoiceCompanyPhone();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceCompanyPhone, "")).append(",");
			
			// 五、税号
			String invoiceDutyParagraph = detail.getInvoiceDutyParagraph();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceDutyParagraph, "")).append(",");
			
			// 五、单位地址
			String invoiceCompanyAddr = detail.getInvoiceCompanyAddr();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceCompanyAddr, "")).append(",");
			
			//====================================其他信息===========================================
			
			// 四、加盟费
			BigDecimal franchiseFee = detail.getFranchiseFee();
			builder.append(CsvUtils.tab).append(decimalFormat(franchiseFee)).append(",");
			
			// 五、保证金
			BigDecimal deposit = detail.getDeposit();
			builder.append(CsvUtils.tab).append(decimalFormat(deposit)).append(",");
			
			// 六、是否有返点
			String isRebate = supplier.getIsRebate() == null || supplier.getIsRebate() == 0 ? "不返点" : "返点";
			builder.append(CsvUtils.tab).append(isRebate).append(",");
			
	
			csvDataList.add(builder.toString());
		});
		return csvDataList;
	}

	/**
	 * @Title: querySupplierById
	 * @Description: 根据供应商ID查询巴士供应商详情
	 * @param supplierId
	 * @param prorateStatus
	 * @return BusSupplierInfoVO
	 * @throws
	 */
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public BusSupplierInfoVO querySupplierById(Integer supplierId, Integer prorateStatus) {
		// 一、查询供应商基础信息
		BusSupplierInfoVO supplierVO = busCarBizSupplierExMapper.selectBusSupplierById(supplierId);
		if (supplierVO == null) {
			return supplierVO;
		}
		// 二、查询巴士供应商其它信息
		completeDetailInfo(supplierVO);

		// 三、调用分佣接口，查询结算、分佣、返点信息
		supplierVO.setCommissionInfo(getRemoteCommissionInfo(supplierId));
		supplierVO.setProrateList(getRemoteProrateInfoList(supplierId, prorateStatus));
		supplierVO.setRebateList(getRemoteRebateInfo(supplierId));

		return supplierVO;
	}

	/**
	 * @Title: getRemoteCommissionInfo
	 * @Description: 获取供应商结算信息
	 * @param supplierId
	 * @return 
	 * @return Map<String,Object>
	 * @throws
	 */
	private JSON getRemoteCommissionInfo(Integer supplierId) {
		Map<String, Object> params = new HashMap<>();
		params.put("supplierId", supplierId);
		
		try {
			logger.info("[ BusSupplierService-getRemoteCommissionInfo ] 查询供应商结算信息,params={}", params);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_INFO, params, 2000, "查询供应商结算信息");
			if (result.getIntValue("code") == 0) {
				JSONObject jsonObject = result.getJSONObject("data");
				return jsonObject;
			} else {
				logger.info("[ BusSupplierService-getRemoteCommissionInfo ] 查询供应商结算信息调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-getRemoteCommissionInfo ] 查询供应商结算信息异常,params={},errorMsg={}", params, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @Title: getRemoteProrateInfoList
	 * @Description: 获取供应商分佣信息
	 * @param supplierId
	 * @param prorateStatus
	 * @return JSON
	 * @throws
	 */
	private JSON getRemoteProrateInfoList(Integer supplierId, Integer prorateStatus) {
		Map<String, Object> params = new HashMap<>();
		params.put("supplierId", supplierId);
		params.put("status", prorateStatus == null ? 0 : prorateStatus);
		try {
			logger.info("[ BusSupplierService-getRemoteProrateInfoList ] 查询供应商分佣信息,params={}", params);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_DETAIL, params, 2000, "查询供应商分佣信息");
			if (result.getIntValue("code") == 0) {
				JSONArray jsonArray = result.getJSONArray("data");
				return jsonArray;
			} else {
				logger.info("[ BusSupplierService-getRemoteProrateInfoList ] 查询供应商分佣信息调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-getRemoteProrateInfoList ] 查询供应商分佣信息异常,params={},errorMsg={}", params, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @Title: getRemoteRebateInfo
	 * @Description: 获取供应商返点信息
	 * @param supplierId 
	 * @return void
	 * @throws
	 */
	private JSON getRemoteRebateInfo(Integer supplierId) {
		Map<String, Object> params = new HashMap<>();
		params.put("supplierId", supplierId);
		params.put("status", 0);
		try {
			logger.info("[ BusSupplierService-getRemoteRebateInfo ] 查询供应商返点信息,params={}", params);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_REBATE_INFO, params, 2000, "查询供应商返点信息");
			if (result.getIntValue("code") == 0) {
				JSONArray jsonArray = result.getJSONArray("data");
				return jsonArray;
			} else {
				logger.info("[ BusSupplierService-getRemoteRebateInfo ] 查询供应商返点信息调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
			}
		} catch (Exception e) {
			logger.error("[ BusSupplierService-getRemoteRebateInfo ] 查询供应商返点信息异常,params={},errorMsg={}", params, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @Title: getProrateList
	 * @Description: 查询供应商分佣有关的信息（批量）
	 * @param supplierIds
	 * @return 
	 * @return JSONArray
	 * @throws
	 */
	public JSONArray getProrateList(String supplierIds) {
		if (StringUtils.isNotBlank(supplierIds)) {
			Map<String, Object> params = new HashMap<>();
			params.put("supplierIds", supplierIds);
			try {
				logger.info("[ BusSupplierService-getProrateList ] 补充分佣信息(分佣比例、是否有返点),params={}", params);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_LIST, params , 2000, "查询供应商分佣信息（分佣比例、是否有返点）");
				if (result.getIntValue("code") == 0) {
					JSONArray jsonArray = result.getJSONArray("data");
					return jsonArray;
				} else {
					logger.info("[ BusSupplierService-getProrateList ] 补充分佣信息(分佣比例、是否有返点)调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
				}
			} catch (Exception e) {
				logger.error("[ BusSupplierService-getProrateList ] 补充分佣信息(分佣比例、是否有返点)异常,params={},errorMsg={}", params, e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * @Title: getProrateList
	 * @Description: 查询供应商分佣有关的信息（批量）
	 * @param cityIds
	 * @return
	 * @return JSONArray
	 * @throws
	 */
	public List<Integer> querySupplierIdByCitys(Map<String,Set<Integer>> cityIds){
		return busCarBizSupplierExMapper.querySupplierIdByCitys(cityIds);
	}

	/**
	 * 批量查询供应商基本信息
	 * @param supplierIds
	 * @return
	 */
	public List<BusSupplierInfoVO> queryBasicInfoByIds(Set<Integer> supplierIds){
		if(supplierIds == null || supplierIds.isEmpty()){
			return new ArrayList<>();
		}
		Map<String,Set<Integer>> param = new HashMap<>(4);
		param.put("supplierIds",supplierIds);
		return busCarBizSupplierExMapper.queryBasicInfoByIds(param);
	}

	public List<BusBizSupplierDetail> querySettleInfoByIds(Set<Integer> supplierIds){
		if(supplierIds == null || supplierIds.isEmpty()){
			return new ArrayList<>();
		}
		Map<String,Set<Integer>> param = new HashMap<>(4);
		param.put("supplierIds",supplierIds);
		return busBizSupplierDetailExMapper.querySettleInfoByIds(param);
	}

}
