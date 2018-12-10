package com.zhuanche.serv.busManage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
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
import com.zhuanche.dto.busManage.BusSupplierDetailDTO;
import com.zhuanche.dto.busManage.BusSupplierQueryDTO;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.mdbcarmanage.CarBizDriverInfoTempService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;

import mapper.mdbcarmanage.CarAdmUserMapper;
import mapper.mdbcarmanage.ex.BusBizSupplierDetailExMapper;
import mapper.rentcar.CarBizCityMapper;
import mapper.rentcar.CarBizCooperationTypeMapper;
import mapper.rentcar.CarBizSupplierMapper;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;

@Service
public class BusSupplierService implements BusConst {

	private static final Logger logger = LoggerFactory.getLogger(BusSupplierService.class);

	// ===========================表基础mapper==================================
	@Autowired
	private CarBizCityMapper carBizCityMapper;

	@Autowired
	private CarBizSupplierMapper carBizSupplierMapper;

	@Autowired
	private CarBizCooperationTypeMapper carBizCooperationTypeMapper;

	@Autowired
	private CarAdmUserMapper carAdmUserMapper;

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

	// ===============================专车其它服务===================================

	// ===============================巴士其它服务===================================

	/**
	 * @Title: saveSupplierInfo
	 * @Description: 
	 * @param baseDTO
	 * @param detailDTO
	 * @return 
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/saveSupplier")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.MASTER),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER) })
	public AjaxResponse saveSupplierInfo(BusSupplierBaseDTO baseDTO, BusSupplierDetailDTO detailDTO) {
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
		
		// 四、调用分佣接口，修改分佣、返点信息  TODO

		// 五、保存操作记录
		// TODO
		
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
		if (f > 0) {
			return AjaxResponse.success(null);
		} else {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR, "操作失败");
		}
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
		
		// 数据权限控制SSOLoginUser
		Set<Integer> permOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
		Set<Integer> permOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID
		Set<Integer> permOfTeam = WebSessionUtil.getCurrentLoginUser().getTeamIds(); // 普通管理员可以管理的所有车队ID
		queryDTO.setAuthOfCity(permOfCity);
		queryDTO.setAuthOfSupplier(permOfSupplier);
		queryDTO.setAuthOfTeam(permOfTeam);
		
		// 二、查询快合同快到期供应商
		Map<Object,Object> param = new HashMap<>();
		param.put("permOfSupplier", permOfSupplier);
		List<BusSupplierPageVO> contractList = busBizSupplierDetailExMapper.querySupplierContractExpireSoonList(param);
		List<Integer> contractIds = new ArrayList<>();
		contractList.forEach(e -> contractIds.add(e.getSupplierId()));
		
		// 三、所有快到期的供应商的基础信息
		queryDTO.setContractIds(contractIds);
		List<BusSupplierPageVO> contractSuppliers = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
		int offset = (Math.max(pageNum, 1) - 1) * pageSize;
		int limit = offset + pageSize;
		// 四、封装结果集
		List<BusSupplierPageVO> resultList = new ArrayList<>();
		if (offset > contractSuppliers.size()) {
			queryDTO.setContractIds(null);
			queryDTO.setExcludeContractIds(contractIds);// 其它供应商(not in 上面的供应商)
			try(Page p = PageHelper.offsetPage(offset - contractSuppliers.size(), limit)) {
				List<BusSupplierPageVO> supplierList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
				// 补充巴士供应商其它信息
				supplierList.forEach(e -> {
					completeDetailInfo(e);
				});
				resultList.addAll(supplierList);
			}
		} else {
			// 组合排序
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
			List<BusSupplierPageVO> orderedAllContractList = allContractList.stream()
					.sorted(Comparator.comparing(BusSupplierPageVO::getContractDateEnd).reversed())
					.collect(Collectors.toList());
			if (limit > contractSuppliers.size()) {
				// 合同快到期供应商
				List<BusSupplierPageVO> subList = orderedAllContractList.subList(offset, contractSuppliers.size());
				try(Page p = PageHelper.offsetPage(0, limit)) {
					// 补充巴士供应商其它信息
					queryDTO.setContractIds(null);
					queryDTO.setExcludeContractIds(contractIds);// 其它供应商(not in 上面的供应商)
					List<BusSupplierPageVO> otherList = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
					otherList.forEach(e -> {
						completeDetailInfo(e);
					});
					
					resultList.addAll(subList);
					resultList.addAll(otherList);
				}
			} else {
				// 合同快到期供应商
				List<BusSupplierPageVO> subList = orderedAllContractList.subList(offset, limit);
				resultList.addAll(subList);
			}
		}
		
		// 五、补充分佣信息(分佣比例、是否有返点)
		// TODO
		return resultList;
	}

	/**
	 * @Title: completeDetailInfo
	 * @Description: 补充巴士供应商信息
	 * @param t 
	 * @return void
	 * @throws
	 */
	private <T> void completeDetailInfo(T t) {
		if (t == null) {
			return;
		}
		BusBizSupplierDetail scope = new BusBizSupplierDetail();
		BeanUtils.copyProperties(t, scope);

		BusBizSupplierDetail detail = busBizSupplierDetailExMapper.selectBySupplierId(scope.getSupplierId());

		// 返回补充的信息
		BeanUtils.copyProperties(detail, t);
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
//		供应商,城市 ,分佣比例,加盟费,保证金,是否有返点,合同开始时间,合同到期时间,状态
		
		// 返回结果
		List<String> csvDataList = new ArrayList<>();
		if (list == null || list.isEmpty()) {
			return csvDataList;
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
			// TODO
			String rate = "";
			builder.append(StringUtils.defaultIfBlank(rate, "")).append(",");
	
			// 四、加盟费
			BigDecimal franchiseFee = detail.getFranchiseFee();
			builder.append(franchiseFee == null ? "0.00" : format.format(franchiseFee)).append(",");
	
			// 五、保证金
			BigDecimal deposit = detail.getDeposit();
			builder.append(deposit == null ? "0.00" : format.format(deposit)).append(",");
	
			// 六、是否有返点
			// TODO
			String isHas = "";
			builder.append(StringUtils.defaultIfBlank(isHas, "")).append(",");
	
			// 七、合同开始时间
			Date contractDateStart = detail.getContractDateStart();
			String contractDateStartFormatter = "";
			if (contractDateStart != null) {
				contractDateStartFormatter = FORMATTER_DATE_BY_HYPHEN.format(LocalDateTime.ofInstant(contractDateStart.toInstant(), ZoneId.systemDefault()));
			}
			builder.append(StringUtils.defaultIfBlank(contractDateStartFormatter, "")).append(",");
	
			// 八、合同到期时间
			Date contractDateEnd = detail.getContractDateEnd();
			String contractDateEndFormatter = "";
			if (contractDateEnd != null) {
				contractDateEndFormatter = FORMATTER_DATE_BY_HYPHEN.format(LocalDateTime.ofInstant(contractDateEnd.toInstant(), ZoneId.systemDefault()));
			}
			builder.append(StringUtils.defaultIfBlank(contractDateEndFormatter, "")).append(",");
	
			// 九、状态
			String status = supplier.getStatus() == 1 ? "有效" : "无效";
			builder.append(StringUtils.defaultIfBlank(status, "")).append(",");
	
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
	public BusSupplierInfoVO querySupplierById(Integer supplierId) {
		// 一、查询供应商基础信息
		BusSupplierInfoVO supplierVO = busCarBizSupplierExMapper.selectBusSupplierById(supplierId);
		// 二、查询巴士供应商其它信息
		completeDetailInfo(supplierVO);

		// 三、调用分佣接口，修改分佣、返点信息 TODO

		return null;
	}

}
