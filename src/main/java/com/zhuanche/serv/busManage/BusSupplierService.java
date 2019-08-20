package com.zhuanche.serv.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.rocketmq.CommonRocketProducerDouble;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.*;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverInfoTempService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.threads.ThreadSupplierTasker;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.util.objcompare.CompareObjectUtils;
import com.zhuanche.util.objcompare.entity.supplier.*;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import mapper.mdbcarmanage.ex.BusBizSupplierDetailExMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizCooperationTypeExMapper;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Validated
public class BusSupplierService implements BusConst {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierService.class);
	
	// ===========================表基础mapper==================================
	@Autowired
	private CarAdmUserMapper carAdmUserMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	// ===========================专车业务拓展mapper==================================
	@Autowired
	private CarBizCityExMapper carBizCityExMapper;
	
	@Autowired
	private CarBizCooperationTypeExMapper carBizCooperationTypeExMapper;

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
	
	@Autowired
	private CarBizSupplierService carBizSupplierService;

	// ===========================巴士业务拓展service==================================
	@Autowired
	private BusBizChangeLogService busBizChangeLogService;

	// ===============================专车其它服务===================================

	// ===============================巴士其它服务===================================
	@Value("${order.pay.url}")
	private String orderPayUrl;

	@Value("${driver.server.api.url}")
	private String driverServiceApiUrl;

	private static ThreadFactory threadFactoryBuilder = new ThreadFactoryBuilder().setNameFormat("bus-update-CooperationType-%d").build();

	private final static ExecutorService excutorService = new ThreadPoolExecutor(5,10,0L,
			TimeUnit.MILLISECONDS,new LinkedBlockingDeque<Runnable>(50),threadFactoryBuilder,new ThreadPoolExecutor.AbortPolicy());

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
		// 校验是否存在
		Map<String,Object> params = new HashMap<>();
		params.put("supplierId", supplierId);
		params.put("supplierCity", baseDTO.getSupplierCity());
		params.put("supplierName", baseDTO.getSupplierName());
		int count = busCarBizSupplierExMapper.checkIfExists(params);
		if (count > 0) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "供应商已存在");
		}
		
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
			//TODO 修改供应商的加盟类型需要发送driver_info的topic-Mq 消息
			CarBizSupplier carBizSupplier = carBizSupplierService.selectByPrimaryKey(baseDTO.getSupplierId());
			if (carBizSupplier != null) {
				try {
					excutorService.submit(new ThreadSupplierTasker(carBizSupplier.getSupplierId(), carBizSupplier.getSupplierFullName(),
							carBizSupplier.getSupplierCity(), baseDTO.getCooperationType(), carBizCityExMapper, driverServiceApiUrl));
				} catch (Exception e) {
					logger.info("供应商修改加盟类型,调用清除接口异常=" + e.getMessage());
				}
			}
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

		// 五、MQ消息写入 供应商
		try {
			sendSupplierToMq(method, supplierId);
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

	/**
	 * @Title: sendSupplierToMq
	 * @Description: 发送供应商信息
	 * @param method
	 * @param supplierId void
	 * @throws
	 */
	enum Method {
		UPDATE, CREATE
	}
	private void sendSupplierToMq(Method methodEnum, Integer supplierId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		// 供应商信息
		CarBizSupplier supplier = carBizSupplierService.selectByPrimaryKey(supplierId);
		// 一、创建人名称
		String createName = "";
        if (supplier.getCreateBy() != null) {
            CarAdmUser carAdmUser = carAdmUserMapper.selectByPrimaryKey(supplier.getCreateBy());
            if (carAdmUser != null) {
            	createName = carAdmUser.getUserName();
            }
        }
        // 二、修改人名称
        String updateName = "";
        if (supplier.getUpdateBy() != null) {
            CarAdmUser carAdmUser = carAdmUserMapper.selectByPrimaryKey(supplier.getUpdateBy());
            if (carAdmUser != null) {
            	updateName = carAdmUser.getUserName();
            }
        }
        // 三、创建时间
        String createDate = "";
        if (supplier.getCreateDate() != null) {
        	createDate = formatter.format(LocalDateTime.ofInstant(supplier.getCreateDate().toInstant(), ZoneId.systemDefault()));
		}
        // 四、修改时间
        String updateDate = "";
        if (supplier.getUpdateDate() != null) {
        	updateDate = formatter.format(LocalDateTime.ofInstant(supplier.getUpdateDate().toInstant(), ZoneId.systemDefault()));
        }
        
        String method = methodEnum.name();
		Map<String, Object> data = new HashMap<>();
		data.put("supplierId", supplier.getSupplierId());
		data.put("supplierNum", supplier.getSupplierNum());
		data.put("supplierFullName", supplier.getSupplierFullName());
		data.put("supplierCity", supplier.getSupplierCity());
		data.put("type", supplier.getType());
		data.put("contacts", supplier.getContacts());
		data.put("contactsPhone", supplier.getContactsPhone());
		data.put("memo", supplier.getMemo());
		data.put("status", supplier.getStatus());
		data.put("createBy", supplier.getCreateBy());
		data.put("updateBy", supplier.getUpdateBy());
		data.put("createDate", createDate);
		data.put("updateDate", updateDate);
		data.put("iscommission", supplier.getIscommission());
		data.put("pospayflag", supplier.getPospayflag());
		data.put("cooperationType", supplier.getCooperationType());
		data.put("createName", createName);
		data.put("updateName", updateName);
		
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("method", method);
		msgMap.put("data", JSON.toJSONString(data));
		logger.info("专车供应商，同步发送数据：", JSON.toJSONString(msgMap));
		CommonRocketProducer.publishMessage("vipSupplierTopic", method, String.valueOf(supplierId), msgMap);
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String envName = request.getServletContext().getInitParameter("env.name");
		if (Objects.nonNull(envName) && Arrays.asList(new String[]{"online","prod"}).contains(envName)){
			CommonRocketProducerDouble.publishMessage("vipSupplierTopic", method, String.valueOf(supplierId), msgMap);
		}
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
		if (id == null || id == 0) {
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
				return "保存分佣基本信息:" + msg;
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
			if (id == null || id == 0) {
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
					return "保存供应商分佣信息:" + msg;
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
			if (id == null || id == 0) {
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
					return "保存供应商返点信息:" + msg;
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
		
		// 供应商结算信息集合
		Map<Integer,JSONObject> settleInfoMap = new HashMap<>();
		List<JSONObject> settleInfoList = new ArrayList<>();
		list.forEach(supplier -> {
			Integer supplierId = supplier.getSupplierId();
			// 供应商结算信息
			JSONObject settleInfo = Optional.ofNullable(getSettleExportInfo(supplierId)).orElseGet(JSONObject::new);
			settleInfoMap.put(supplierId, settleInfo);
			settleInfoList.add(settleInfo);
			
		});
		int maxCount2SupplierRate = settleInfoList.stream().mapToInt(settleInfo -> {
			JSONArray supplierRates = Optional.ofNullable(settleInfo.getJSONArray("prorateRateList")).orElseGet(JSONArray::new);
			return supplierRates.size();
		}).max().orElse(0);// 结算比例配置项最大数
	
		int maxCount2RebateRate = settleInfoList.stream().mapToInt(settleInfo -> {
			JSONArray rebateRates = Optional.ofNullable(settleInfo.getJSONArray("rebateRateList")).orElseGet(JSONArray::new);
			return rebateRates.size();
		}).max().orElse(0);// 返点比例配置项最大数
		
		// 表头
		List<Object> heads = new ArrayList<>();
		List<Object> baseHeads = new ArrayList<>();// 基本信息
		baseHeads.add("城市");
		baseHeads.add("供应商ID");
		baseHeads.add("供应商名称");
		baseHeads.add("状态");
		baseHeads.add("企业联系人");
		baseHeads.add("企业联系人电话");
		baseHeads.add("调度员电话");
		baseHeads.add("加盟类型");
		
		List<Object> settleHeads = new ArrayList<>();// 结算信息
		settleHeads.add("结算周期");
		settleHeads.add("结算天数");
		settleHeads.add("合同开始时间");
		settleHeads.add("合同结束时间");
		settleHeads.add("结算方式");
		settleHeads.add("结算类型");
		for (int i = 1; i < maxCount2SupplierRate + 1; i++) {
			settleHeads.add("结算比例"+i);
			settleHeads.add("生效起始时间"+i);
			settleHeads.add("生效结束时间"+i);
		}
		
		List<Object> invoiceHeads = new ArrayList<>();// 付款信息
		invoiceHeads.add("账户名称");
		invoiceHeads.add("开户银行");
		invoiceHeads.add("银行账户");
		invoiceHeads.add("电话号码");
		invoiceHeads.add("税号");
		invoiceHeads.add("单位地址");
		
		List<Object> otherHeads = new ArrayList<>();
		otherHeads.add("加盟费");
		otherHeads.add("保证金");
		otherHeads.add("是否提前结算");
        otherHeads.add("提前结算金额");
		otherHeads.add("是否有返点");
		for (int i = 1; i < maxCount2RebateRate + 1; i++) {
			otherHeads.add("返点比例"+i);
			otherHeads.add("金额"+i);
			otherHeads.add("生效起始时间"+i);
			otherHeads.add("生效结束时间"+i);
		}
		
		heads.addAll(baseHeads);
		heads.addAll(settleHeads);
		heads.addAll(invoiceHeads);
		heads.addAll(otherHeads);
		csvDataList.add(StringUtils.join(heads, ","));
		
		// 数据区
		list.forEach(supplier -> {

			Integer supplierId = supplier.getSupplierId();

			/** 行数据 */
			StringBuilder builder = new StringBuilder();
			// 供应商其它信息
			BusBizSupplierDetail detail = Optional.ofNullable(busBizSupplierDetailExMapper.selectBySupplierId(supplierId)).orElseGet(BusBizSupplierDetail::new);
			// 供应商结算信息
			JSONObject settleInfo = Optional.ofNullable(settleInfoMap.get(supplierId)).orElseGet(JSONObject::new);

			// ====================================基本信息===========================================
			// 一、城市
			String ciytName = supplier.getCityName();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(ciytName, "")).append(",");

			// 二、供应商ID
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



			// ====================================结算信息===========================================
			// 一、结算周期
			String settleCycle = Optional.ofNullable(settleInfo.getInteger("settleCycle")).map(value -> {
				switch (value) {
				case 0:
					return "周";
				case 1:
					return "月";
				case 2:
					return "季度";
				}
				return "未知类型";
			}).orElse("");
			builder.append(CsvUtils.tab).append(settleCycle).append(",");

			// 二、结算天数
			String settleBillCycle = StringUtils.defaultIfBlank(settleInfo.getString("settleBillCycle"), "");
			builder.append(CsvUtils.tab).append(settleBillCycle).append(",");
             // 三、合同开始时间
             Date contractDateStart = detail.getContractDateStart();
			String contractDateStartFormatter = "";
			if (contractDateStart != null) {
				contractDateStartFormatter = formatDate(FORMATTER_DATE_BY_HYPHEN, contractDateStart);
			}
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(contractDateStartFormatter, "")).append(",");

			// 四、合同到期时间
			Date contractDateEnd = detail.getContractDateEnd();
			String contractDateEndFormatter = "";
			if (contractDateEnd != null) {
				contractDateEndFormatter = formatDate(FORMATTER_DATE_BY_HYPHEN, contractDateEnd);
			}
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(contractDateEndFormatter, "")).append(",");
			// 五、结算方式
			String settleType = Optional.ofNullable(settleInfo.getInteger("settleType")).map(value -> {
				switch (value) {
				case 0:
					return "手动";
				case 1:
					return "自动";
				}
				return "未知类型";
			}).orElse("");
			builder.append(CsvUtils.tab).append(settleType).append(",");

			// 六、分佣类型
			String shareType = Optional.ofNullable(settleInfo.getInteger("shareType")).map(value -> {
				switch (value) {
				case 0:
					return "订单";
				}
				return "未知类型";
			}).orElse("");
			builder.append(CsvUtils.tab).append(shareType).append(",");

			JSONArray supplierRates = Optional.ofNullable(settleInfo.getJSONArray("prorateRateList")).orElseGet(JSONArray::new);
			supplierRates.forEach(object -> {
				// 结算比例信息
				JSONObject rate = (JSONObject) JSON.toJSON(object);

				// 七、结算比例
				String supplierRate = StringUtils.defaultIfBlank(rate.getString("supplierRate"), "0.00");
				builder.append(CsvUtils.tab).append(supplierRate).append(",");

				// 六、生效起始时间
				String startTime = StringUtils.defaultIfBlank(rate.getString("startTime"), "");
				builder.append(CsvUtils.tab).append(startTime).append(",");

				// 七、生效结束时间
				String endTime = StringUtils.defaultIfBlank(rate.getString("endTime"), "");
				builder.append(CsvUtils.tab).append(endTime).append(",");
			});
			for (int i = 0; i < maxCount2SupplierRate - supplierRates.size(); i++) {
				builder.append(CsvUtils.tab).append("").append(",");
				builder.append(CsvUtils.tab).append("").append(",");
				builder.append(CsvUtils.tab).append("").append(",");
			}

			// ====================================付款信息===========================================
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

			// 六、单位地址
			String invoiceCompanyAddr = detail.getInvoiceCompanyAddr();
			builder.append(CsvUtils.tab).append(StringUtils.defaultIfBlank(invoiceCompanyAddr, "")).append(",");

			// ====================================其他信息===========================================
			// 一、加盟费
			BigDecimal franchiseFee = detail.getFranchiseFee();
			builder.append(CsvUtils.tab).append(decimalFormat(franchiseFee)).append(",");

			// 二、保证金
			BigDecimal deposit = detail.getDeposit();
			builder.append(CsvUtils.tab).append(decimalFormat(deposit)).append(",");

			// 三、是否提前结算
			String isAdvance = Optional.ofNullable(settleInfo.getInteger("isAdvance")).map(value -> {
				switch (value) {
				case 0:
					return "否";
				case 1:
					return "是";
				}
				return "未知类型";
			}).orElse("");
			builder.append(CsvUtils.tab).append(isAdvance).append(",");

			//四、提前结算金额
            BigDecimal settleAmount = Optional.ofNullable(settleInfo.getBigDecimal("settleAmount")).orElse(BigDecimal.ZERO);
            builder.append(CsvUtils.tab).append(settleAmount).append(",");

            // 五、是否有返点
			String isRebate = Optional.ofNullable(settleInfo.getInteger("isRebate")).map(value -> {
				switch (value) {
				case 0:
					return "不返点";
				case 1:
					return "返点";
				}
				return "未知类型";
			}).orElse("");
			builder.append(CsvUtils.tab).append(isRebate).append(",");

			JSONArray rebateRates = Optional.ofNullable(settleInfo.getJSONArray("rebateRateList")).orElseGet(JSONArray::new);
			rebateRates.forEach(object -> {
				// 结算比例信息
				JSONObject rate = (JSONObject) JSON.toJSON(object);

				// 六、返点比例
				String rebateRate = StringUtils.defaultIfBlank(rate.getString("rebateRate"), "0.00");
				builder.append(CsvUtils.tab).append(rebateRate).append(",");

				// 七、金额
				String maxMoney = StringUtils.defaultIfBlank(rate.getString("maxMoney"), "0.00");
				builder.append(CsvUtils.tab).append(maxMoney).append(",");

				// 八、生效起始时间
				String startTime = StringUtils.defaultIfBlank(rate.getString("startTime"), "");
				builder.append(CsvUtils.tab).append(startTime).append(",");

				// 九、生效结束时间
				String endTime = StringUtils.defaultIfBlank(rate.getString("endTime"), "");
				builder.append(CsvUtils.tab).append(endTime).append(",");
			});
			for (int i = 0; i < maxCount2RebateRate - rebateRates.size(); i++) {
				builder.append(CsvUtils.tab).append("").append(",");
				builder.append(CsvUtils.tab).append("").append(",");
				builder.append(CsvUtils.tab).append("").append(",");
				builder.append(CsvUtils.tab).append("").append(",");
			}

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
	 * @Title: getSupplierByProrateRate
	 * @Description: 按结算比例筛选供应商
	 * @param supplierRate
	 * @return JSONArray
	 * @throws
	 */
	public JSONArray getSupplierByProrateRate(Double supplierRate) {
		if (supplierRate != null) {
			Map<String, Object> params = new HashMap<>();
			params.put("supplierRate", supplierRate);
			try {
				logger.info("[ BusSupplierService-getSupplierByProrateRate ] 按结算比例筛选供应商,params={}", params);
				JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_INFO_BY_PRORATE_RATE, params , 2000, "按结算比例筛选供应商");
				if (result.getIntValue("code") == 0) {
					JSONArray jsonArray = result.getJSONArray("data");
					return jsonArray;
				}
				logger.info("[ BusSupplierService-getSupplierByProrateRate ] 按结算比例筛选供应商调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
			} catch (Exception e) {
				logger.error("[ BusSupplierService-getSupplierByProrateRate ] 按结算比例筛选供应商异常,params={},errorMsg={}", params, e.getMessage(), e);
			}
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
				}
				logger.info("[ BusSupplierService-getProrateList ] 补充分佣信息(分佣比例、是否有返点)调用接口出错,params={},errorMsg={}", params, result.getString("msg"));
			} catch (Exception e) {
				logger.error("[ BusSupplierService-getProrateList ] 补充分佣信息(分佣比例、是否有返点)异常,params={},errorMsg={}", params, e.getMessage(), e);
			}
		}
		return null;
	}
	
	/**
	 * @Title: getSettleExportInfo
	 * @Description: 查询供应商列表导出结算相关信息
	 * @param supplierId
	 * @return JSONObject
	 * @throws
	 */
	public JSONObject getSettleExportInfo(Integer supplierId) {
		Map<String, Object> params = new HashMap<>();
		params.put("supplierId", supplierId);
		try {
			logger.info("[ BusSupplierService-getSettleExportInfo ] 查询供应商列表导出结算相关信息,params={}", params);
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(orderPayUrl + Pay.SETTLE_SUPPLIER_EXPORT_SUPPLIER_INFO, params, 2000, "查询供应商列表导出结算相关信息");
			if (result.getIntValue("code") == 0) {
				JSONObject jsonObject = result.getJSONObject("data");
				return jsonObject;
			}
			logger.info("[ BusSupplierService-getSettleExportInfo ] 查询供应商列表导出结算相关信息接口出错,params={},errorMsg={}", params, result.getString("msg"));
		} catch (Exception e) {
			logger.error("[ BusSupplierService-getSettleExportInfo ] 查询供应商列表导出结算相关信息异常,params={},errorMsg={}", params, e.getMessage(), e);
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

	
	/**
	 * @Title: getContents
	 * @Description: 获取比对信息
	 * @param supplierId
	 * @return List<Object>
	 * @throws
	 */
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public List<Object> getContents(Integer supplierId) {
		try {
			List<Object> resultList = new ArrayList<>();
			/** 供应商基本信息**/
			BusSupplierBaseCO supplierCO = busCarBizSupplierExMapper.querySupplierCOById(supplierId);
			
			/** 供应商其它信息**/
			BusSupplierDetailCO detailCO = busBizSupplierDetailExMapper.queryDetailCOBySupplierId(supplierId);
			
			/** 供应商结算信息**/
			// 基本结算信息
			BusSupplierCommissionInfoCO commissionInfo = Optional.ofNullable((JSONObject) getRemoteCommissionInfo(supplierId)).map(object -> {
				BusSupplierCommissionInfoCO commissionInfoCO = new BusSupplierCommissionInfoCO();
				
				commissionInfoCO.setRoleType (Optional.ofNullable(object.getInteger("roleType")).map(value -> {
					switch (value) {
					case 0:
						return "大巴车";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setSettleCycle (Optional.ofNullable(object.getInteger("settleCycle")).map(value -> {
					switch (value) {
					case 0:
						return "周";
					case 1:
						return "月";
					case 2:
						return "季度";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setSettleBillCycle (object.getString("settleBillCycle"));
				commissionInfoCO.setSettleType (Optional.ofNullable(object.getInteger("settleType")).map(value -> {
					switch (value) {
					case 0:
						return "手动";
					case 1:
						return "自动";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setShareWay (Optional.ofNullable(object.getInteger("shareWay")).map(value -> {
					switch (value) {
					case 0:
						return "固定比例";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setShareType (Optional.ofNullable(object.getInteger("shareType")).map(value -> {
					switch (value) {
					case 0:
						return "订单";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setIsRebate (Optional.ofNullable(object.getInteger("isRebate")).map(value -> {
					switch (value) {
					case 0:
						return "不返点";
					case 1:
						return "返点";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setRebateType (Optional.ofNullable(object.getInteger("rebateType")).map(value -> {
					switch (value) {
					case 0:
						return "金额";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setRebateCycle (Optional.ofNullable(object.getInteger("rebateCycle")).map(value -> {
					switch (value) {
					case 0:
						return "月";
					case 1:
						return "季度";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setIsAdvance (Optional.ofNullable(object.getInteger("isAdvance")).map(value -> {
					switch (value) {
					case 0:
						return "否";
					case 1:
						return "是";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setAdvanceType (Optional.ofNullable(object.getInteger("advanceType")).map(value -> {
					switch (value) {
					case 0:
						return "金额";
					}
					return "未知类型";
				}).orElse(""));
				commissionInfoCO.setSettleAmount (object.getString("settleAmount"));
				
				return commissionInfoCO;
			}).orElseGet(BusSupplierCommissionInfoCO::new);
			// 结算比例
			List<BusSupplierProrateCO> prorateList = Optional.ofNullable((JSONArray) getRemoteProrateInfoList(supplierId, null)).map(jsonArray -> {
				List<BusSupplierProrateCO> list = new ArrayList<>();
				jsonArray.forEach(object -> {
					JSONObject prorate = (JSONObject) JSON.toJSON(object);
					BusSupplierProrateCO prorateCO = new BusSupplierProrateCO();
					
					prorateCO.setSupplierRate(prorate.getString("supplierRate"));
					prorateCO.setStartTime(prorate.getString("startTime"));
					prorateCO.setEndTime(prorate.getString("endTime"));
					
					list.add(prorateCO);
				});
				if (jsonArray.isEmpty()) {
					list.add(new BusSupplierProrateCO());
				}
				return list;
			}).orElseGet(() -> {
				List<BusSupplierProrateCO> list = new ArrayList<>();
				list.add(new BusSupplierProrateCO());
				return list;
			});
			// 返点比例
			List<BusSupplierRebateCO> rebateList = Optional.ofNullable((JSONArray) getRemoteRebateInfo(supplierId)).map(jsonArray -> {
				List<BusSupplierRebateCO> list = new ArrayList<>();
				jsonArray.forEach(object -> {
					JSONObject rebate = (JSONObject) JSON.toJSON(object);
					BusSupplierRebateCO rebateCO = new BusSupplierRebateCO();
					
					rebateCO.setRebateRate(rebate.getString("rebateRate"));
					rebateCO.setMaxMoney(rebate.getString("maxMoney"));
					rebateCO.setStartTime(rebate.getString("startTime"));
					rebateCO.setEndTime(rebate.getString("endTime"));
					
					list.add(rebateCO);
				});
				if (jsonArray.isEmpty()) {
					list.add(new BusSupplierRebateCO());
				}
				return list;
			}).orElseGet(() -> {
				List<BusSupplierRebateCO> list = new ArrayList<>();
				list.add(new BusSupplierRebateCO());
				return list;
			});
			
			resultList.add(supplierCO);
			resultList.add(detailCO);
			resultList.add(commissionInfo);
			resultList.add(prorateList);
			resultList.add(rebateList);
			return resultList;
		} catch (Exception e) {
			logger.error("[ BusSupplierService-getContents ] 获取比对信息异常,supplierId={},errorMsg={}", supplierId, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param supplierId 
	 * @Title: saveChangeLog
	 * @Description: 对比操作记录并保存日志
	 * @param old
	 * @param fresh void
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER))
	public void saveChangeLog(Integer supplierId, List<Object> oldList, List<Object> freshList) {
		try {
			String result = compareSupplierContents(oldList, freshList);
			String description = StringUtils.isBlank(result) ? "未变更信息" : result;
			busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), description, new Date());
		} catch (Exception e) {
			logger.error("[ BusSupplierService-saveChangeLog ] 对比操作记录并保存日志异常,supplierId={},errorMsg={}", supplierId, e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String compareSupplierContents(List<Object> oldList, List<Object> freshList) throws InstantiationException, IllegalAccessException {
		
		logger.info("[ BusSupplierService-compareSupplierContents ] 对比操作记录, oldList={}, freshList={}", oldList, freshList);
		
		// 比对结果
		List<String> result = new ArrayList<>(oldList.size());
		// 其它list
		int oldSize = oldList.size();
		int freshSize = freshList.size();
		String prefix = "";
		int size = oldSize - freshSize;
		if (Math.min(oldSize, freshSize) > 0) {
			Object object = null;
			StringBuilder builder = new StringBuilder(prefix);
			if (size > 0) {
				object = freshList.get(0);
				builder.append("删除");
				for (int i = 0; i < Math.abs(size); i++) {
					freshList.add(object.getClass().newInstance());
				}
			}
			if (size < 0) {
				object = oldList.get(0);
				builder.append("新增");
				for (int i = 0; i < Math.abs(size); i++) {
					oldList.add(object.getClass().newInstance());
				}
			}
			if (object instanceof BusSupplierProrateCO) {
				builder.append("结算比例:");
			}
			if (object instanceof BusSupplierRebateCO) {
				builder.append("返点比例:");
			}
			prefix = builder.toString();
		}

		for (int i = 0; i < Math.max(oldSize, freshSize); i++) {
			Object old = oldList.get(i);
			Object fresh = freshList.get(i);
			if (old instanceof List) {
				// 递归操作
				String join = compareSupplierContents((List) old, (List) fresh);
				if (StringUtils.isNotBlank(join)) {
					result.add(join);
				}
			} else {
				List<Object> list = CompareObjectUtils.contrastObj(old, fresh, null);
				String join = StringUtils.join(list, CompareObjectUtils.separator);
				if (StringUtils.isNotBlank(join)) {
					if (i >= Math.max(oldSize, freshSize) - Math.abs(size)) {
						result.add(join = prefix + join);
					} else {
						result.add(join);
					}
				}
			}
		}
		return StringUtils.join(result, CompareObjectUtils.separator);
	}
	
}
