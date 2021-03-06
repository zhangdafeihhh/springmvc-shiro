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

	// ===========================??????????????????mapper==================================

	// ===========================??????????????????mapper==================================
	@Autowired
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;

	// ===========================??????????????????service==================================

	// ===========================??????????????????service==================================
	@Autowired
	private BusSupplierService busSupplierService;

	@Autowired
	private BusBizChangeLogService busBizChangeLogService;

	// ============================??????????????????service==================================

	@Autowired
	@Qualifier("hibernateValidator")
	private Validator validator;

	/**
	 * @Title: saveSupplier
	 * @Description: ??????/???????????????
	 * @param baseDTO
	 * @param detailDTO
	 * @return
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/saveSupplier")
	public AjaxResponse saveSupplier(@Validated BusSupplierBaseDTO baseDTO, @Validated BusSupplierDetailDTO detailDTO,
			@Validated BusSupplierCommissionInfoDTO commissionDTO, String prorateList, String rebateList) {
		// ??????
		JSONArray prorateArray = null;
		try {
			prorateArray = JSONArray.parseArray(prorateList);
		} catch (Exception e) {
			logger.error("[ BusSupplierController-saveSupplier ] ???????????????????????????", e.getMessage(), e);
			return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "???????????????????????????");
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
		
		// ??????
		JSONArray rebateArray = null;
		try {
			rebateArray = JSONArray.parseArray(rebateList);
		} catch (Exception e) {
			logger.error("[ BusSupplierController-saveSupplier ] ???????????????????????????", e.getMessage(), e);
			return AjaxResponse.failMsg(RestErrorCode.PARAMS_ERROR, "???????????????????????????");
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
		
		// ????????????
		Integer supplierId = baseDTO.getSupplierId();
		boolean isAdd = true;
		List<Object> old = null;
		if (supplierId != null && supplierId != 0) {
			isAdd = false;
			old = busSupplierService.getContents(supplierId);
		}
		
		// ??????????????????
		AjaxResponse response = busSupplierService.saveSupplierInfo(baseDTO, detailDTO, commissionDTO, prorates, rebates);
		
		// ????????????????????????
		if (response.isSuccess()) {
			if (isAdd) {
				busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(baseDTO.getSupplierId()), "???????????????", new Date());
			} else {
				List<Object> fresh = busSupplierService.getContents(supplierId);;
				busSupplierService.saveChangeLog(supplierId, old, fresh);
			}
		}
		
		return response;
	}
	
	/**
	 * @Title: deleteProrate
	 * @Description: ??????ID???????????????????????????
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/deleteProrate")
	public AjaxResponse deleteProrate(@NotNull(message = "id????????????") Long id,
			@NotNull(message = "?????????ID????????????") Integer supplierId, String supplierRate, String startTime, String endTime,
			String createName, String createTime) {
		// ????????????????????????
		AjaxResponse response = busSupplierService.deleteProrate(id);
		// ????????????????????????
		StringBuilder builder = new StringBuilder("?????????????????????????????????   ");
		if (StringUtils.isNotBlank(supplierRate)) {
			builder.append("????????????:").append(supplierRate).append(";");
		}
		if (StringUtils.isNotBlank(startTime)) {
			builder.append("????????????????????????:").append(startTime).append(";");
		}
		if (StringUtils.isNotBlank(endTime)) {
			builder.append("????????????????????????:").append(endTime).append(";");
		}
		if (StringUtils.isNotBlank(createName)) {
			builder.append("?????????:").append(createName).append(";");
		}
		if (StringUtils.isNotBlank(createTime)) {
			builder.append("????????????:").append(createTime).append(";");
		}
		busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), builder.toString(), new Date());
		return response;
	}

	/**
	 * @Title: deleteRebate
	 * @Description: ??????ID???????????????????????????
	 * @param id
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/deleteRebate")
	public AjaxResponse deleteRebate(@NotNull(message = "id????????????") Integer id,
			@NotNull(message = "?????????ID????????????") Integer supplierId, String rebateRate, String maxMoney, String startTime,
			String endTime, String createName, String createTime) {
		// ????????????????????????
		AjaxResponse response = busSupplierService.deleteRebate(id);
		// ????????????????????????
		StringBuilder builder = new StringBuilder("?????????????????????????????????   ");
		if (StringUtils.isNotBlank(rebateRate)) {
			builder.append("????????????:").append(rebateRate).append(";");
		}
		if (StringUtils.isNotBlank(maxMoney)) {
			builder.append("??????:").append(maxMoney).append(";");
		}
		if (StringUtils.isNotBlank(startTime)) {
			builder.append("????????????????????????:").append(startTime).append(";");
		}
		if (StringUtils.isNotBlank(endTime)) {
			builder.append("????????????????????????:").append(endTime).append(";");
		}
		if (StringUtils.isNotBlank(createName)) {
			builder.append("?????????:").append(createName).append(";");
		}
		if (StringUtils.isNotBlank(createTime)) {
			builder.append("????????????:").append(createTime).append(";");
		}
		busBizChangeLogService.insertLog(BusinessType.SUPPLIER, String.valueOf(supplierId), builder.toString(), new Date());
		return response;
	}

	/**
	 * @Title: querySupplierPageList
	 * @Description: ???????????????????????????
	 * @param queryDTO
	 * @return
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/querySupplierPageList")
	@MasterSlaveConfigs(configs = { @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse queryList(@Validated BusSupplierQueryDTO queryDTO,Integer pageNum,Integer pageSize) {
			//????????? BusSupplierQueryDTO ?????????????????? pageNum???pageSize??????????????????????????? supportMethodsArguments=true
			pageNum=(pageNum==null||pageNum<=0)?1:pageNum;
			pageSize=(pageSize==null||pageSize<=0)?30:pageSize;
		Double supplierRate = queryDTO.getSupplierRate();
        List<Integer> supplierIds=new ArrayList<>();
        if (supplierRate != null) {
            JSONArray supplierIdsByRate = busSupplierService.getSupplierByProrateRate(supplierRate);
            if (supplierIdsByRate == null) {
                logger.error("[?????????????????????]???????????????????????????????????????????????????????????????ID??????????????????" + JSON.toJSONString(queryDTO));
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
            if (supplierIdsByRate.isEmpty()) {
                return AjaxResponse.success(new PageDTO(pageNum, pageSize, 0, supplierIds));
            }
            supplierIds=supplierIdsByRate.stream().map(o->(JSONObject)o).map(o->o.getInteger("supplierId")).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if(queryDTO.getSupplierId()!=null){
        	if(supplierIds.size()>0){
				boolean contains = supplierIds.contains(queryDTO.getSupplierId());
				if(!contains){
					return AjaxResponse.success(new PageDTO(pageNum,pageSize,0,new ArrayList()));
				}
				supplierIds.clear();
			}
            supplierIds.add(queryDTO.getSupplierId());
        }
        queryDTO.setSupplierIds(supplierIds);
        //????????????
		List<BusSupplierPageVO> busSupplierPageVOS = busCarBizSupplierExMapper.querySupplierPageListByMaster(queryDTO);
		if(busSupplierPageVOS==null||busSupplierPageVOS.isEmpty()) {
            return AjaxResponse.success(new PageDTO(pageNum, pageSize, 0, new ArrayList()));
        }
        //????????????
        Set<Integer> supplierIdSet = busSupplierPageVOS.stream().map(BusSupplierPageVO::getSupplierId).collect(Collectors.toSet());
        List<BusBizSupplierDetail> supplierDetails = busSupplierService.querySettleInfoByIds(supplierIdSet);
        //????????????
        String supplierIdsStr = busSupplierPageVOS.stream().map(BusSupplierPageVO::getSupplierId).map(String::valueOf).collect(Collectors.joining(","));
        JSONArray settleDetails = busSupplierService.getProrateList(supplierIdsStr);
        if(settleDetails==null){settleDetails=new JSONArray();}
        Map<Integer, BusBizSupplierDetail> supplierDetailMap = supplierDetails.stream().collect(Collectors.toMap(s -> s.getSupplierId(), s -> s));

        Map<Integer, JSONObject> settleDetailMap = settleDetails.stream().map(o -> (JSONObject) o).collect(Collectors.toMap(o -> o.getInteger("supplierId"), o -> o));

        //????????????
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
        //???????????? 0
        Map<Integer, List<BusSupplierPageVO>> groupByStatusMap = busSupplierPageVOS.stream().collect(Collectors.groupingBy(o -> o.getStatus()));

        //????????????
        List<BusSupplierPageVO> validSup = Optional.ofNullable(groupByStatusMap.get(1)).orElse(new ArrayList<>());
        //????????????
        List<BusSupplierPageVO> invalidSup = Optional.ofNullable(groupByStatusMap.get(0)).orElse(new ArrayList<>());
        //??????  contractDateEnd < now -1 ???  now< contractDateEnd<now +3month   0 ; now+3month <contractDateEnd 1  ; contractDate ==null -2
        long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long afterThirdMonth = LocalDateTime.now().plusMonths(3).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Map<Integer, List<BusSupplierPageVO>> groupByTime = validSup.stream().collect(Collectors.groupingBy(o -> {
            Date date = o.getContractDateEnd();
            if(date==null){return -2;}
			long time = date.getTime();
			return time < now ? -1 : time < afterThirdMonth ? 0 : 1;
        }));

        //?????????????????????????????????????????????????????????????????????????????????????????????
        List<BusSupplierPageVO> expiredSup =Optional.ofNullable(groupByTime.get(-1)).orElse(new ArrayList<>());
        expiredSup = expiredSup.stream().map(o->{o.setIsExpireSoon(1);return o;})
                .sorted(Comparator.comparing(BusSupplierPageVO::getContractDateEnd).
                        thenComparing(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()))
                .collect(Collectors.toList());
         //3???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<BusSupplierPageVO> expiringSup = Optional.ofNullable(groupByTime.get(0)).orElse(new ArrayList<>());
        expiringSup=expiringSup.stream().map(o->{o.setIsExpireSoon(1);return o;}).sorted(Comparator.comparing(BusSupplierPageVO::getContractDateEnd).
                thenComparing(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()))
                .collect(Collectors.toList());
        //????????????3??????????????????????????????????????????
        List<BusSupplierPageVO> notExpiredSup = Optional.ofNullable(groupByTime.get(1)).orElse(new ArrayList());
        notExpiredSup = notExpiredSup.stream().sorted(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()).collect(Collectors.toList());

        //???????????????????????????????????????
		invalidSup = invalidSup.stream().sorted(Comparator.comparing(BusSupplierPageVO::getCreateDate).reversed()).collect(Collectors.toList());

		//?????????????????????????????????
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
	 * @Description: ?????????????????????
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
        long start = System.currentTimeMillis(); // ??????????????????
		try {
            // ?????????
            LocalDateTime now = LocalDateTime.now();
            String suffix = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(now);
            String fileName = "???????????????" + suffix + ".csv";
            String agent = request.getHeader("User-Agent").toUpperCase(); // ???????????????????????????????????????
            if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) { // IE????????????Edge?????????
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else { // ???????????????
                fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
            CsvUtils utilEntity = new CsvUtils();
			// ??????????????????????????????
            List<Integer> supplierIds = new ArrayList();
            Double supplierRate = queryDTO.getSupplierRate();
            if (supplierRate != null) {
                JSONArray supplierIdsByRate = busSupplierService.getSupplierByProrateRate(queryDTO.getSupplierRate());
                if(supplierIdsByRate==null){
                    logger.error("[ BusSupplierController-exportSupplierList ] ,????????????????????????", JSON.toJSONString(queryDTO));
                    List<String> csvDataList = new ArrayList<>();
                    csvDataList.add("?????????????????????????????????");
                    utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);
                    return;
                }
                if(supplierIdsByRate.isEmpty()){
                    List<String> csvDataList = new ArrayList<>();
                    csvDataList.add("?????????????????????????????????");
                    utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);
                    return;
                }
                supplierIds=supplierIdsByRate.stream().map(o->(JSONObject)o).map(o->o.getInteger("supplierId")).filter(Objects::nonNull).collect(Collectors.toList());
            }

            if(queryDTO.getSupplierId()!=null){
                supplierIds.add(queryDTO.getSupplierId());
            }
			/**????????????*/
			// ????????????
			List<BusSupplierExportVO> list = busSupplierService.querySupplierExportList(queryDTO);
			// ?????????
			// ????????????????????????
			if (list == null || list.isEmpty()) {
				logger.info("[ BusSupplierController-exportSupplierList ] ????????????params={}?????????????????????????????????????????????", JSON.toJSONString(queryDTO));
				List<String> csvDataList = new ArrayList<>();
				csvDataList.add("?????????????????????????????????");
				utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);
			}

			// ??????????????????
			List<String> csvDataList = busSupplierService.completeSupplierExportList(list);// ??????????????????
			utilEntity.exportCsvV2(response, csvDataList, null, fileName, true, true);

			// ??????????????????
			long end = System.currentTimeMillis();
			logger.info("??????????????????????????????????????????" + JSON.toJSONString(queryDTO) + ",??????=" + (end - start) + "ms");
		} catch (Exception e) {
			// ??????????????????
			long end = System.currentTimeMillis();
			logger.error("??????????????????????????????????????????" + JSON.toJSONString(queryDTO) + ",??????=" + (end - start) + "ms", e);
		}
	}

	/**
	 * @Title: querySupplierById
	 * @Description: ?????????????????????
	 * @param supplierId
	 * @param prorateStatus
	 * @return AjaxResponse
	 * @throws
	 */
	@RequestMapping(value = "/querySupplierById")
	public AjaxResponse querySupplierById(@NotNull(message = "?????????ID????????????") Integer supplierId, Integer prorateStatus) {
		BusSupplierInfoVO supplierVO = busSupplierService.querySupplierById(supplierId, prorateStatus);
		return AjaxResponse.success(supplierVO);
	}

}
