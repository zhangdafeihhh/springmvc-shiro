package com.zhuanche.serv.busManage;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import mapper.mdbcarmanage.ex.BusBizSupplierDetailExMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
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
			BusSupplierCommissionInfoDTO commissionDTO, List<BusSupplierProrateDTO> prorateList,
			List<BusSupplierRebateDTO> rebateList) {
		String method = "UPDATE";
		int f = 0;
		Integer supplierId = baseDTO.getSupplierId();
		// 一、操作主表
		baseDTO.setUpdateBy(WebSessionUtil.getCurrentLoginUser().getId());
		if (supplierId == null || supplierId == 0) {
			method = "CREATE";
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
		if ("UPDATE".equals(method)) {
			carBizDriverInfoService.updateDriverCooperationTypeBySupplierId(baseDTO.getSupplierId(), baseDTO.getCooperationType());
			carBizDriverInfoTempService.updateDriverCooperationTypeBySupplierId(baseDTO.getSupplierId(), baseDTO.getCooperationType());
		}

		// 四、调用分佣接口，修改分佣、返点信息
		StringBuilder errorMsg = new StringBuilder();

		String commissionMsg = saveSupplierCommission(commissionDTO, supplierId);// 分佣基本信息
		if (StringUtils.isNotBlank(commissionMsg)) {
			errorMsg.append(commissionMsg).append(";");
		}

		String prorateMsg = saveSupplierProrate(prorateList, supplierId);// 分佣信息
		if (StringUtils.isNotBlank(prorateMsg)) {
			errorMsg.append(prorateMsg).append(";");
		}

		String rebateMsg = saveSupplierRebate(rebateList, supplierId);// 返点信息
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
			CommonRocketProducer.publishMessage("vipSupplierTopic", method, String.valueOf(baseDTO.getSupplierId()), msgMap);
		} catch (Exception e) {
			logger.error("[ BusSupplierService-saveSupplierInfo ] 供应商信息发送MQ出错", e.getMessage(), e);
		}
		logger.info("***********************新增/修改 厂商信息  END***********************");
		if (f > 0 && StringUtils.isBlank(errorMsg.toString())) {
			return AjaxResponse.success(null);
		} else {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, StringUtils.defaultIfBlank(errorMsg.toString(), "操作失败"));
		}
	}
	
	/**
	 * @Title: saveSupplierCommission
	 * @Description: 保存分佣基本信息
	 * @param commissionDTO
	 * @param supplierId
	 * @return 
	 * @return String
	 * @throws
	 */
	private String saveSupplierCommission(BusSupplierCommissionInfoDTO commissionDTO, Integer supplierId) {
		commissionDTO.setSupplierId(supplierId);
		commissionDTO.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
		commissionDTO.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());
		
		String jsonString = JSON.toJSONStringWithDateFormat(commissionDTO, JSON.DEFFAULT_DATE_FORMAT, new SerializerFeature[0]);
		JSONObject json = (JSONObject) JSONObject.parse(jsonString);
		Map<String, Object> params = json.getInnerMap();
		
		Long id = commissionDTO.getId();// 主键
		try {
			String url = null;
			if (id == null) {
				url = orderPayUrl + Pay.SETTLE_SUPPLIER_INFO_ADD;
			} else {
				url = orderPayUrl + Pay.SETTLE_SUPPLIER_INFO_UPDATE;
			}
			
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
	 * @Title: saveSupplierProrate
	 * @Description: 保存供应商分佣信息
	 * @param prorateDTOList
	 * @param supplierId
	 * @return 
	 * @return String
	 * @throws
	 */
	private String saveSupplierProrate(List<BusSupplierProrateDTO> prorateList, Integer supplierId) {
		if (prorateList == null || prorateList.isEmpty()) {
			return null;
		}

		for (BusSupplierProrateDTO prorate : prorateList) {
			prorate.setSupplierId(supplierId);
			prorate.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
			prorate.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());

			String jsonString = JSON.toJSONStringWithDateFormat(prorate, JSON.DEFFAULT_DATE_FORMAT, new SerializerFeature[0]);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> params = json.getInnerMap();

			Long id = prorate.getId();// 主键
			try {
				String url = null;
				if (id == null) {
					url = orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_ADD;
				} else {
					url = orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_UPDATE;
				}
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
	 * @Title: saveSupplierRebate
	 * @Description: 保存供应商返点信息
	 * @param rebateDTOList
	 * @param supplierId
	 * @return 
	 * @return String
	 * @throws
	 */
	private String saveSupplierRebate(List<BusSupplierRebateDTO> rebateList, Integer supplierId) {
		if (rebateList == null || rebateList.isEmpty()) {
			return null;
		}

		for (BusSupplierRebateDTO rebate : rebateList) {
			rebate.setSupplierId(supplierId);
			rebate.setCreateName(WebSessionUtil.getCurrentLoginUser().getName());
			rebate.setUpdateName(WebSessionUtil.getCurrentLoginUser().getName());

			String jsonString = JSON.toJSONStringWithDateFormat(rebate, JSON.DEFFAULT_DATE_FORMAT, new SerializerFeature[0]);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> params = json.getInnerMap();

			Integer id = rebate.getId();// 主键
			try {
				String url = null;
				if (id == null) {
					url = orderPayUrl + Pay.SETTLE_SUPPLIER_REBATE_ADD;
				} else {
					url = orderPayUrl + Pay.SETTLE_SUPPLIER_REBATE_UPDATE;
				}
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
	 * @Title: queryBusSupplierPageList
	 * @Description: 查询巴士供应商列表
	 * @param queryDTO
	 * @return 
	 * @return List<BusSupplierPageVO>
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public List<BusSupplierPageVO> queryBusSupplierPageList(BusSupplierQueryDTO queryDTO) {
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
			try(Page p = PageHelper.offsetPage(offset - contractSuppliers.size(), limit)) {
				List<BusSupplierPageVO> supplierList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
				supplierList.forEach(this::completeDetailInfo);// 补充巴士供应商其它信息
				resultList.addAll(supplierList);
			}
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
				try(Page p = PageHelper.offsetPage(0, limit)) {
					queryDTO.setContractIds(null);
					queryDTO.setExcludeContractIds(contractIds);// 其它供应商(not in 上面的供应商)
					List<BusSupplierPageVO> otherList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
					otherList.forEach(this::completeDetailInfo);// 补充巴士供应商其它信息
					
					resultList.addAll(subList);
					resultList.addAll(otherList);
				}
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
		// 组装数据
		if (jsonArray != null) {
			list.forEach(supplier -> {
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
			});
		}
		
		
		list.forEach(supplier -> {
			/** 行数据 */
			StringBuilder builder = new StringBuilder();
			BusBizSupplierDetail detail = busBizSupplierDetailExMapper.selectBySupplierId(supplier.getSupplierId());
	
			// 一、供应商
			String supplierName = supplier.getSupplierName();
			builder.append(StringUtils.defaultIfBlank(supplierName, "")).append(",");
	
			// 二、城市
			String ciytName = supplier.getCityName();
			builder.append(StringUtils.defaultIfBlank(ciytName, "")).append(",");
	
			// 三、分佣比例
			Double supplierRate = supplier.getSupplierRate();
			builder.append(supplierRate).append(",");
	
			// 四、加盟费
			BigDecimal franchiseFee = detail.getFranchiseFee();
			builder.append(decimalFormat(franchiseFee)).append(",");
	
			// 五、保证金
			BigDecimal deposit = detail.getDeposit();
			builder.append(decimalFormat(deposit)).append(",");
	
			// 六、是否有返点
			String isRebate = supplier.getIsRebate() == 0 ? "不返点" : "返点";
			builder.append(isRebate).append(",");
	
			// 七、合同开始时间
			Date contractDateStart = detail.getContractDateStart();
			String contractDateStartFormatter = "";
			if (contractDateStart != null) {
				contractDateStartFormatter = formatDate(FORMATTER_DATE_BY_HYPHEN, contractDateStart);
			}
			builder.append(StringUtils.defaultIfBlank(contractDateStartFormatter, "")).append(",");
	
			// 八、合同到期时间
			Date contractDateEnd = detail.getContractDateEnd();
			String contractDateEndFormatter = "";
			if (contractDateEnd != null) {
				contractDateEndFormatter = formatDate(FORMATTER_DATE_BY_HYPHEN, contractDateEnd);
			}
			builder.append(StringUtils.defaultIfBlank(contractDateEndFormatter, "")).append(",");
	
			// 九、状态
			String status = supplier.getStatus() == 1 ? "有效" : "无效";
			builder.append(status).append(",");
	
			csvDataList.add(builder.toString());
		});
		return csvDataList;
	}

	/**
	 * @Title: querySupplierById
	 * @Description: 根据供应商ID查询巴士供应商详情
	 * @param supplierId
	 * @return 
	 * @return BusSupplierInfoVO
	 * @throws
	 */
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public BusSupplierInfoVO querySupplierById(Integer supplierId) {
		// 一、查询供应商基础信息
		BusSupplierInfoVO supplierVO = busCarBizSupplierExMapper.selectBusSupplierById(supplierId);
		// 二、查询巴士供应商其它信息
		completeDetailInfo(supplierVO);

		// 三、调用分佣接口，查询结算、分佣、返点信息
		supplierVO.setCommissionInfo(getRemoteCommissionInfo(supplierId));
		supplierVO.setProrateList(getRemoteProrateInfoList(supplierId));
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
	 * @return void
	 * @throws
	 */
	private JSON getRemoteProrateInfoList(Integer supplierId) {
		Map<String, Object> params = new HashMap<>();
		params.put("supplierId", supplierId);
		params.put("status", 0);
		try {
			logger.info("[ BusSupplierService-getRemoteProrateInfoList ] 查询供应商分佣信息,params={}", params);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_PRORATE_INFO_LIST, params, 2000, "查询供应商分佣信息");
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
