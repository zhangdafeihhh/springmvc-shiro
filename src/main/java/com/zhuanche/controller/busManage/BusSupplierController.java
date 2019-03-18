package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.busManage.*;
import com.zhuanche.entity.mdbcarmanage.BusBizSupplierDetail;
import com.zhuanche.serv.busManage.BusBizChangeLogService;
import com.zhuanche.serv.busManage.BusSupplierService;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusSupplierExportVO;
import com.zhuanche.vo.busManage.BusSupplierInfoVO;
import com.zhuanche.vo.busManage.BusSupplierPageVO;
import mapper.mdbcarmanage.ex.BusBizChangeLogExMapper.BusinessType;
import mapper.rentcar.ex.BusCarBizSupplierExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
		if (response.isSuccess()) {
			if (isAdd) {
				busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(baseDTO.getSupplierId()), "创建供应商", new Date());
			} else {
				List<Object> fresh = busSupplierService.getContents(supplierId);;
				busSupplierService.saveChangeLog(supplierId, old, fresh);
			}
		}
		
		return response;
	}
	
	/**
	 * @Title: deleteProrate
	 * @Description: 根据ID删除供应商结算比例
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/deleteProrate")
	public AjaxResponse deleteProrate(@NotNull(message = "id不能为空") Long id,
			@NotNull(message = "供应商ID不能为空") Integer supplierId, String supplierRate, String startTime, String endTime,
			String createName, String createTime) {
		// 一、删除分佣协议
		AjaxResponse response = busSupplierService.deleteProrate(id);
		// 二、保存操作日志
		StringBuilder builder = new StringBuilder("删除供应商结算比例配置   ");
		if (StringUtils.isNotBlank(supplierRate)) {
			builder.append("结算比例:").append(supplierRate).append(";");
		}
		if (StringUtils.isNotBlank(startTime)) {
			builder.append("生效日期开始时间:").append(startTime).append(";");
		}
		if (StringUtils.isNotBlank(endTime)) {
			builder.append("生效日期结束日期:").append(endTime).append(";");
		}
		if (StringUtils.isNotBlank(createName)) {
			builder.append("创建人:").append(createName).append(";");
		}
		if (StringUtils.isNotBlank(createTime)) {
			builder.append("创建时间:").append(createTime).append(";");
		}
		busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), builder.toString(), new Date());
		return response;
	}

	/**
	 * @Title: deleteRebate
	 * @Description: 根据ID删除供应商返点协议
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/deleteRebate")
	public AjaxResponse deleteRebate(@NotNull(message = "id不能为空") Integer id,
			@NotNull(message = "供应商ID不能为空") Integer supplierId, String rebateRate, String maxMoney, String startTime,
			String endTime, String createName, String createTime) {
		// 一、删除分佣协议
		AjaxResponse response = busSupplierService.deleteRebate(id);
		// 二、保存操作日志
		StringBuilder builder = new StringBuilder("删除供应商返点比例配置   ");
		if (StringUtils.isNotBlank(rebateRate)) {
			builder.append("返点比例:").append(rebateRate).append(";");
		}
		if (StringUtils.isNotBlank(maxMoney)) {
			builder.append("金额:").append(maxMoney).append(";");
		}
		if (StringUtils.isNotBlank(startTime)) {
			builder.append("生效日期开始时间:").append(startTime).append(";");
		}
		if (StringUtils.isNotBlank(endTime)) {
			builder.append("生效日期结束日期:").append(endTime).append(";");
		}
		if (StringUtils.isNotBlank(createName)) {
			builder.append("创建人:").append(createName).append(";");
		}
		if (StringUtils.isNotBlank(createTime)) {
			builder.append("创建时间:").append(createTime).append(";");
		}
		busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), builder.toString(), new Date());
		return response;
	}

	/**
	 * @Title: querySupplierPageList
	 * @Description: 查询供应商分页列表
	 * @param queryDTO
	 * @return
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/querySupplierPageList")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse queryList(@Validated BusSupplierQueryDTO queryDTO,Integer pageNum,Integer pageSize) {
			//不可使 BusSupplierQueryDTO 的属性中包含 pageNum，pageSize，否则会被默认分页 supportMethodsArguments=true
			pageNum=(pageNum==null||pageNum<=0)?1:pageNum;
			pageSize=(pageSize==null||pageSize<=0)?30:pageSize;
		Double supplierRate = queryDTO.getSupplierRate();
        List<Integer> supplierIds=new ArrayList<>();
        if (supplierRate != null) {
            JSONArray supplierIdsByRate = busSupplierService.getSupplierByProrateRate(supplierRate);
            if (supplierIdsByRate == null) {
                logger.error("[查询供应商列表]，根据结算比例调用接口查询符合条件的供应商ID出错，参数：" + JSON.toJSONString(queryDTO));
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
            if (supplierIdsByRate.isEmpty()) {
                return AjaxResponse.success(new PageDTO(pageNum, pageSize, 0, supplierIds));
            }
            supplierIds=supplierIdsByRate.stream().map(o->(JSONObject)o).map(o->o.getInteger("supplierId")).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if(queryDTO.getSupplierId()!=null){
            supplierIds.add(queryDTO.getSupplierId());
        }
        //主表信息
        List<BusSupplierPageVO> busSupplierPageVOS = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
        if(busSupplierPageVOS==null||busSupplierPageVOS.isEmpty()) {
            return AjaxResponse.success(new PageDTO(pageNum, pageSize, 0, new ArrayList()));
        }
        //附表信息
        Set<Integer> supplierIdSet = busSupplierPageVOS.stream().map(BusSupplierPageVO::getSupplierId).collect(Collectors.toSet());
        List<BusBizSupplierDetail> supplierDetails = busSupplierService.querySettleInfoByIds(supplierIdSet);
        //计费信息
        String supplierIdsStr = busSupplierPageVOS.stream().map(BusSupplierPageVO::getSupplierId).map(String::valueOf).collect(Collectors.joining(","));
        JSONArray settleDetails = busSupplierService.getProrateList(supplierIdsStr);
        if(settleDetails==null){settleDetails=new JSONArray();}
        Map<Integer, BusBizSupplierDetail> supplierDetailMap = supplierDetails.stream().collect(Collectors.toMap(s -> s.getSupplierId(), s -> s));

        Map<Integer, JSONObject> settleDetailMap = settleDetails.stream().map(o -> (JSONObject) o).collect(Collectors.toMap(o -> o.getInteger("supplierId"), o -> o));

        //拼装信息
        busSupplierPageVOS.stream().forEach(sup->{
            Integer supplierId = sup.getSupplierId();
            BusBizSupplierDetail supplierDetail = supplierDetailMap.get(supplierId);
            JSONObject settleDetail = settleDetailMap.get(supplierId);
            if(supplierDetail!=null){BeanUtils.copyProperties(supplierDetail,sup);}
            if(settleDetail!=null){
              sup.setSupplierRate(settleDetail.getDouble("supplierRate"));
              sup.setIsRebate(settleDetail.getInteger("isRebate"));
            }
        });
        //分割数据 0
        Map<Integer, List<BusSupplierPageVO>> groupByStatusMap = busSupplierPageVOS.stream().collect(Collectors.groupingBy(o -> o.getStatus()));

        //有效数据
        List<BusSupplierPageVO> validSup = Optional.ofNullable(groupByStatusMap.get(1)).orElse(new ArrayList<>());
        //无效数据
        List<BusSupplierPageVO> invalidSup = Optional.ofNullable(groupByStatusMap.get(0)).orElse(new ArrayList<>());
        //分隔  contractDateEnd < now -1 ；  now< contractDateEnd<now +3month   0 ; now+3month <contractDateEnd 1  ; contractDate ==null -2
        long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long afterThirdMonth = LocalDateTime.now().plusMonths(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Map<Integer, List<BusSupplierPageVO>> groupByTime = validSup.stream().collect(Collectors.groupingBy(o -> {
            Date date = o.getContractDateEnd();
            if(date==null){return -2;}
			long time = date.getTime();
			return time < now ? -1 : time < afterThirdMonth ? 0 : 1;
        }));

        //已过期，按合同到期时间正序排列。到期相同的，按照创建时间倒叙；
        List<BusSupplierPageVO> expiredSup =Optional.ofNullable(groupByTime.get(-1)).orElse(new ArrayList<>());
        expiredSup = expiredSup.stream().map(o->{o.setIsExpireSoon(1);return o;})
                .sorted(Comparator.comparing(BusSupplierPageVO::getContractDateEnd).
                        thenComparing(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()))
                .collect(Collectors.toList());
         //3个月内即将到期，按合同到期时间正序排列。到期时间相同的，按照创建时间倒叙；
        List<BusSupplierPageVO> expiringSup = Optional.ofNullable(groupByTime.get(0)).orElse(new ArrayList<>());
        expiringSup=expiringSup.stream().map(o->{o.setIsExpireSoon(1);return o;}).sorted(Comparator.comparing(BusSupplierPageVO::getContractDateEnd).
                thenComparing(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()))
                .collect(Collectors.toList());
        //其他大于3个月，按照创建时间倒叙排列。
        List<BusSupplierPageVO> notExpiredSup = Optional.ofNullable(groupByTime.get(1)).orElse(new ArrayList());
        notExpiredSup = notExpiredSup.stream().sorted(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()).collect(Collectors.toList());

        //合同到期时间为空的情况
		List<BusSupplierPageVO> nullContractDateSup = Optional.ofNullable( groupByTime.get(-2)).orElse(new ArrayList<>());

		List<BusSupplierPageVO> result=new ArrayList<>(busSupplierPageVOS.size());
        result.addAll(expiringSup);
        result.addAll(expiredSup);
        result.addAll(notExpiredSup);
        result.addAll(invalidSup);
        result.addAll(nullContractDateSup);
        int total = result.size();
        int start=(pageNum-1)*pageSize;
        int end = start+pageSize>=total?total:start+pageSize;
        return AjaxResponse.success(new PageDTO(pageNum,pageSize,total,result.subList(start,end)));
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
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public void exportSupplierList(@Validated BusSupplierQueryDTO queryDTO, HttpServletRequest request,
			HttpServletResponse response) {
        long start = System.currentTimeMillis(); // 获取开始时间
		try {
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
			// 按结算比例筛选供应商
            List<Integer> supplierIds = new ArrayList();
            Double supplierRate = queryDTO.getSupplierRate();
            if (supplierRate != null) {
                JSONArray supplierIdsByRate = busSupplierService.getSupplierByProrateRate(queryDTO.getSupplierRate());
                if(supplierIdsByRate==null){
                    logger.error("[ BusSupplierController-exportSupplierList ] ,查询结算比例异常", JSON.toJSONString(queryDTO));
                    List<String> csvDataList = new ArrayList<>();
                    csvDataList.add("没有查到符合条件的数据");
                    utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);
                    return;
                }
                if(supplierIdsByRate.isEmpty()){
                    List<String> csvDataList = new ArrayList<>();
                    csvDataList.add("没有查到符合条件的数据");
                    utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);
                    return;
                }
                supplierIds=supplierIdsByRate.stream().map(o->(JSONObject)o).map(o->o.getInteger("supplierId")).filter(Objects::nonNull).collect(Collectors.toList());
            }

            if(queryDTO.getSupplierId()!=null){
                supplierIds.add(queryDTO.getSupplierId());
            }
			/**导出逻辑*/
			// 查询数据
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
