package com.zhuanche.controller.homekanban;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.cache.RedisCacheUtil;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.util.CurrentSystemUtils;
import com.zhuanche.common.util.RedisKeyUtils;
import com.zhuanche.common.util.TransportUtils;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.entity.bigdata.*;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.bigdata.AllianceIndexService;
import com.zhuanche.serv.common.CitySupplierTeamService;
import com.zhuanche.serv.driverMeasureDay.DriverMeasureDayService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.dateUtil.DateUtil;
import mapper.bigdata.ex.CarMeasureDayExMapper;
import mapper.bigdata.ex.DriverRankDetaiExlMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDate;
import java.util.*;

/**
 * 首页看板数据提供 
 * ClassName: HomeKanBanController.java 
 * Date: 2018年8月31日
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */
@Controller
@RequestMapping("/home")
public class HomeKanBanController {

	private static final Logger logger = LoggerFactory.getLogger(HomeKanBanController.class);

	@Value("${statistics.operating.vehicle.url}")
	String operatingVehicleUrl;

	@Value("${statistics.order.url}")
	String statisticsOrderUrl;

	@Value("${statistics.evaluation.url}")
	String statisticsEvaluationUrl;

	@Value("${statistics.online.time.url}")
	String onlineTimeUrl;

	@Value("${statistics.vehicle.top.url}")
	String vehicleTopUrl;

	@Value("${statistics.core.indicators.url}")
	String coreIndicatorsUrl;
	
	/**链接超时时间**/
	private static final Integer CONNECT_TIMEOUT = 30000;
	/**读取超时时间**/
	private static final Integer READ_TIMEOUT = 30000;
	
	@Autowired
	CitySupplierTeamService citySupplierTeamService;

	@Autowired
	private CarMeasureDayExMapper measureDayExMapper;

	@Autowired
	private DriverRankDetaiExlMapper driverRankDetaiExlMapper;

	@Autowired
	AllianceIndexService allianceIndexService;

	@Autowired
	private DriverMeasureDayService driverMeasureDayService;

	@Autowired
	private CurrentSystemUtils systemUtils;

	/** 日均运营车辆统计查询接口 **/
	@RequestMapping("/operatingVehicleStatistics")
	@ResponseBody
	public AjaxResponse operatingVehicleStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}

		String key = null;

		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();

			key = "";
			StringBuilder stringBuffer = new StringBuilder();


			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
                key = RedisKeyUtils.VEHICLE_STATISTICS + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
                List<Map> resultList = RedisCacheUtil.get(key,List.class);
                if(RedisCacheUtil.exist(key) && resultList != null){
                    return  AjaxResponse.success(resultList);
                }
            }else {
				String keyParam = this.keyParam(cityId,allianceId);

                key = RedisKeyUtils.VEHICLE_STATISTICS + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
                        .append(allianceId).append(motorcadeId).toString().replaceAll("null","");
                List<Map> resultList = RedisCacheUtil.get(key,List.class);
                if(RedisCacheUtil.exist(key) && resultList != null){
                    return AjaxResponse.success(resultList);
                }
            }
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
		}

		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			saas.setVisibleAllianceIds(systemUtils.supplierIds(cityId,allianceId));
			List<Map> resultList = allianceIndexService.getCarOperateStatistics(saas);
			if(CollectionUtils.isNotEmpty(resultList)){

				RedisCacheUtil.set(key,resultList,3600*24);

				return AjaxResponse.success(resultList);
			}else {
				return AjaxResponse.success(new ArrayList<>());
			}
		}catch (Exception e){
			logger.error("查询首页日均运营车辆统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}

	}

	private String keyParam(Integer cityId,String allianceId){
		String keyParam = "";
		if(cityId == null){
			List<String> strList =	systemUtils.supplierIds(cityId,allianceId);
			if(!CollectionUtils.isEmpty(strList)){
				keyParam = StringUtils.join(strList,Constants.SEPERATER);
			}
		}
		return keyParam;
	}

	/** 订单数量统计 **/
	@RequestMapping("/orderStatistics")
	@ResponseBody
	public AjaxResponse orderStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate, Integer cityId,String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}

		String key = null;

		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();

			key = "";
			StringBuffer stringBuffer = new StringBuffer();


			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				//
				key = RedisKeyUtils.ORDER_STATISTICS + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				List<Map> resultList = RedisCacheUtil.get(key,List.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return  AjaxResponse.success(resultList);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.ORDER_STATISTICS + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				List<Map> resultList = RedisCacheUtil.get(key,List.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return AjaxResponse.success(resultList);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
		}



		try{
 			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			saas.setVisibleAllianceIds(systemUtils.supplierIds(cityId,allianceId));
			List<Map> resultList = allianceIndexService.getOrderNumStatistic(saas);
			if(CollectionUtils.isNotEmpty(resultList)){

				RedisCacheUtil.set(key,resultList,3600*24);

				return AjaxResponse.success(resultList);
			}else {
				return AjaxResponse.success(new ArrayList<>());
			}
		}catch (Exception e){
			logger.error("查询首页订单数量统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** ci安装预测和订单数量统计 **/
	@RequestMapping("/orderAndCiStatistics")
	@ResponseBody
	public AjaxResponse orderAndCiStatistics(@Verify(param = "startDate", rule = "required") String startDate,
										@Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}

		String key = null;

		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息

			key = "";
			StringBuffer stringBuffer = new StringBuffer();

			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				key = RedisKeyUtils.ORDER_STATISTICS_CI + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				Map<String,Object> resultList = RedisCacheUtil.get(key,Map.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return  AjaxResponse.success(resultList);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.ORDER_STATISTICS_CI + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				Map<String,Object> resultList = RedisCacheUtil.get(key,Map.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return AjaxResponse.success(resultList);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
		}


		List<Map> orderList = new ArrayList<>();
		List<Map> ciOrderList = new ArrayList<>();
		Map<String,Object> map = new HashMap<>();
		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			saas.setVisibleAllianceIds(systemUtils.supplierIds(cityId,allianceId));
			orderList = allianceIndexService.getOrderNumStatistic(saas);
			ciOrderList = allianceIndexService.getCiOrderNumStatistic(saas);
			map.put("orderList",orderList);
			map.put("ciOrderList",ciOrderList);
			RedisCacheUtil.set(key,map,3600*24);

			return AjaxResponse.success(map);

		}catch (Exception e){
			logger.error("查询首页订单数量统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** ci预测增长和下降幅度集合 **/
	@RequestMapping("/ciInstallStatisticsPercent")
	@ResponseBody
	public AjaxResponse ciInstallStatisticsPercent(@Verify(param = "startDate", rule = "required") String startDate,
											 @Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		String key = null;

		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息

			key = "";
			StringBuffer stringBuffer = new StringBuffer();


			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				//
				key = RedisKeyUtils.CORE_STATISTICS_CI + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				Map<String,Object> resultList = RedisCacheUtil.get(key,Map.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return  AjaxResponse.success(resultList);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.CORE_STATISTICS_CI + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				Map<String,Object> resultList = RedisCacheUtil.get(key,Map.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return AjaxResponse.success(resultList);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
		}
		Map<String,Object> map = new HashMap<>();

		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);

			// 供应商信息
			String[] visibleAllianceIds = null;
			// 车队信息
			String[] visibleMotocadeIds = null;
			// 数据权限设置
			if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
				SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
				Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取当前登录用户可见城市ID
				// 如果城市id为空，代表可查全国所有数据
				if(cityIds != null && cityIds.size() > 0){
					Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
					if(supplierIds == null || supplierIds.size() <= 0){
						List<CarBizSupplier> querySupplierList = citySupplierTeamService.querySupplierList();// 获取用户可见的供应商信息
						if(querySupplierList != null && querySupplierList.size() > 0){
							for (CarBizSupplier carBizSupplier : querySupplierList) {
								supplierIds.add(carBizSupplier.getSupplierId());
							}
						}
					}
					visibleAllianceIds = setToArray(supplierIds);
					Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
					visibleMotocadeIds = setToArray(teamIds);
				}
			}
			List<String>  visibleList = systemUtils.supplierIds(cityId,allianceId);
			List<String>  visibleMotoIdsList = null;
			if(visibleMotocadeIds != null){
				visibleMotoIdsList = Arrays.asList(visibleMotocadeIds);
			}

			long dateDiff = DateUtil.calDateDiff(startDate, endDate);
			List<SAASCoreIndexPercentDto> list = allianceIndexService.getCiCoreIndexStatistic(saas,startDate,endDate,allianceId,motorcadeId,visibleList,visibleMotoIdsList,dateDiff);
			if(list==null || list.size()==0){
				map.put("completeOrderAmountPercent","1%");
				map.put("incomeAmountPercent","1%");
				map.put("orderPerVehiclePercent","1%");
				map.put("incomePerVehiclePercent","1%");
				map.put("badEvaluateAllNumPercent","-1%");
				map.put("badEvaluateNumPercent","-1%");
				map.put("criticismRatePercent","-1%");
				return AjaxResponse.success(map);
			}
			map.put("completeOrderAmountPercent",list.get(0).getCompleteOrderAmountPerecnt());
			map.put("incomeAmountPercent",list.get(0).getIncomeAmountPercent());
			map.put("orderPerVehiclePercent",list.get(0).getOrderPerVehiclePercent());
			map.put("incomePerVehiclePercent",list.get(0).getIncomePerVehiclePercent());
			map.put("badEvaluateAllNumPercent",list.get(0).getBadEvaluateAllNumPercent());
			map.put("badEvaluateNumPercent",list.get(0).getBadEvaluateNumPercent());
			map.put("criticismRatePercent",list.get(0).getCriticismRatePercent());
			RedisCacheUtil.set(key,map,3600*24);

			return AjaxResponse.success(map);

		}catch (Exception e){
			logger.error("查询首页订单数量统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 服务差评率统计 **/
	@RequestMapping("/serviceEvaluationRateStatistics")
	@ResponseBody
	public AjaxResponse serviceEvaluationRateStatistics(
			@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}


		String key = null;

		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息

			key = "";
			StringBuffer stringBuffer = new StringBuffer();


			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				//
				key = RedisKeyUtils.SERVICE_RATE_STATISTIS + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				List<Map> resultList = RedisCacheUtil.get(key,List.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return  AjaxResponse.success(resultList);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.SERVICE_RATE_STATISTIS + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				List<Map> resultList = RedisCacheUtil.get(key,List.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return AjaxResponse.success(resultList);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
		}



		try{

			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			saas.setVisibleAllianceIds(systemUtils.supplierIds(cityId,allianceId));
			List<Map> resultList = allianceIndexService.getServiceNegativeRate(saas);
			if(CollectionUtils.isNotEmpty(resultList)){

				RedisCacheUtil.set(key,resultList,3600*24);

				return AjaxResponse.success(resultList);
			}else {
				return AjaxResponse.success(new ArrayList<>());
			}
		}catch (Exception e){
			logger.error("查询首页订单数量统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 服务差评率统计(含ci预测) **/
	@RequestMapping("/serviceAndCiEvaluationRateStatistics")
	@ResponseBody
	public AjaxResponse serviceAndCiEvaluationRateStatistics(
			@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}


		String key = null;

		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息

			key = "";
			StringBuffer stringBuffer = new StringBuffer();


			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				//
				key = RedisKeyUtils.SERVICE_RATE_STATISTIS_CI + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				Map<String,Object>  resultList = RedisCacheUtil.get(key,Map.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return  AjaxResponse.success(resultList);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.SERVICE_RATE_STATISTIS_CI + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				Map<String,Object>  resultList = RedisCacheUtil.get(key,Map.class);
				if(RedisCacheUtil.exist(key) && resultList != null){
					return AjaxResponse.success(resultList);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
		}

		List<Map> rateList = new ArrayList<>();
		List<Map> ciRateList = new ArrayList<>();
		Map<String,Object> map = new HashMap<>();

		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			saas.setVisibleAllianceIds(systemUtils.supplierIds(cityId,allianceId));
			rateList = allianceIndexService.getServiceNegativeRate(saas);
			ciRateList = allianceIndexService.getCiServiceBadEvaNumStatistic(saas);
			map.put("rateList",rateList);
			map.put("ciRateList",ciRateList);
			RedisCacheUtil.set(key,map,3600*24);

			return AjaxResponse.success(map);
		}catch (Exception e){
			logger.error("查询首页订单数量统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 在线时长统计 **/
	@RequestMapping("/onlineTimeStatistics")
	@ResponseBody
	public AjaxResponse onlineTimeStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		SAASIndexQuery saas = setVisibleData();
		saas.setStartDate(startDate);
		saas.setEndDate(endDate);
		saas.setAllianceId(allianceId);
		saas.setMotorcadeId(motorcadeId);
		saas.setVisibleAllianceIds(systemUtils.supplierIds(cityId,allianceId));
		if(startDate!=null && endDate!=null){
			try{
				Date searchStartDate = DateUtils.getDate1(startDate);
				Date searchEndDate = DateUtils.getDate1(endDate);
				Integer createGap = DateUtils.getIntervalDays(searchStartDate, searchEndDate);
				if(createGap>14){
					logger.info("查询日子区间大于14天："+createGap);
					Date middleDate = DateUtils.addDays(searchStartDate,14);
					try{
						//第一次查询
						saas.setStartDate(startDate);
						saas.setEndDate(DateUtils.formatDateTime(middleDate));
						List<Map> middleMap = allianceIndexService.getCarOnlineDuration(saas);
						//第二次查询
						saas.setStartDate(DateUtils.formatDateTime(middleDate));
						saas.setEndDate(endDate);
						List<Map> resultList = allianceIndexService.getCarOnlineDuration(saas);
						middleMap.addAll(resultList);
						if(CollectionUtils.isNotEmpty(middleMap)){
							return AjaxResponse.success(middleMap);
						}else {
							return AjaxResponse.success(new ArrayList<>());
						}
					}catch (Exception e){
						logger.error("查询首页日均运营车辆统计错误异常", e);
						return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
					}
				}
			}catch (Exception e){
				logger.error("按区间查询车辆在线时长异常",e);
			}

		}
		logger.info("查询日子区间小于14天");
		try{
			List<Map> resultList = allianceIndexService.getCarOnlineDuration(saas);
			if(CollectionUtils.isNotEmpty(resultList)){
				return AjaxResponse.success(resultList);
			}else {
				return AjaxResponse.success(new ArrayList<>());
			}
		}catch (Exception e){
			logger.error("查询首页日均运营车辆统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}

	}

	/**
	 * 司机排名统计
	 * @param allianceId 加盟商ID
	 * @param motorcadeId 车队ID
	 * @param orderByColumnCode 排序字段编号 1：订单数量;2：平均得分;3：在线时长;4：流水合计
	 * @param orderByTypeCode 排序方式 1：升序;2：降序
	 * @param topNum 取前几名 最多不允许超过50
	 * @return
	 */
	@RequestMapping("/vehicleTopStatistics")
	@ResponseBody
	public AjaxResponse vehicleTopStatistics(Integer cityId,String allianceId, String motorcadeId,
			@Verify(param = "orderByColumnCode", rule = "required") String orderByColumnCode,
			@Verify(param = "orderByTypeCode", rule = "required") String orderByTypeCode,
			@Verify(param = "topNum", rule = "required|max(50)") String topNum) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
			Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取当前登录用户可见城市ID
			// 如果城市id为空，代表可查全国所有数据
			if(cityIds != null && cityIds.size() > 0){
				Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
				if(supplierIds == null || supplierIds.size() <= 0){
					List<CarBizSupplier> querySupplierList = citySupplierTeamService.querySupplierList();// 获取用户可见的供应商信息
					if(querySupplierList != null && querySupplierList.size() > 0){
						for (CarBizSupplier carBizSupplier : querySupplierList) {
							supplierIds.add(carBizSupplier.getSupplierId());
						}
					}
				}
				visibleAllianceIds = setToArray(supplierIds);
				Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
				visibleMotocadeIds = setToArray(teamIds);
			}
		}

		try{

			List<String>  visibleMotoIdsList = null;
			List<String>  visibleList = systemUtils.supplierIds(cityId,allianceId);
			if(visibleMotocadeIds != null){
				visibleMotoIdsList = Arrays.asList(visibleMotocadeIds);
			}
			String date = LocalDate.now().minusMonths(1).toString();
			List<SAASDriverRankingDto> driverRankingSections = driverRankDetaiExlMapper.getDriverRanking(allianceId,motorcadeId,orderByColumnCode,orderByTypeCode,Integer.parseInt(topNum),visibleList,visibleMotoIdsList,date);
			logger.info("司机排名统计查询数据库");
			return AjaxResponse.success(driverRankingSections);
		}catch (Exception e){
			logger.error("查询首页日均运营车辆统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 核心指标统计 **/
	@RequestMapping("/coreIndicatorsStatistics")
	@ResponseBody
	public AjaxResponse coreIndicatorsStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate, Integer cityId,String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		String key = null;
		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId+motorcadeId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
			key = "";
			StringBuffer stringBuffer = new StringBuffer();
			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				//
				key = RedisKeyUtils.CORE_STATISTICS + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				SAASCoreIndexDto saasCoreIndexDto = RedisCacheUtil.get(key,SAASCoreIndexDto.class);
				if(RedisCacheUtil.exist(key) && saasCoreIndexDto != null){
					return  AjaxResponse.success(saasCoreIndexDto);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.CORE_STATISTICS + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).append(motorcadeId).toString().replaceAll("null","");
				SAASCoreIndexDto saasCoreIndexDto = RedisCacheUtil.get(key,SAASCoreIndexDto.class);
				if(RedisCacheUtil.exist(key) && saasCoreIndexDto != null){
					return AjaxResponse.success(saasCoreIndexDto);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
			RedisCacheUtil.delete(key);
		}

		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		if(WebSessionUtil.isSupperAdmin() == false){
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
			Set<Integer> cityIds = currentLoginUser.getCityIds();
			// 如果城市id为空，代表可查全国所有数据
			if(cityIds != null && cityIds.size() > 0){
				Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
				if(supplierIds == null || supplierIds.size() <= 0){
					List<CarBizSupplier> querySupplierList = citySupplierTeamService.querySupplierList();// 获取用户可见的供应商信息
					if(querySupplierList != null && querySupplierList.size() > 0){
						for (CarBizSupplier carBizSupplier : querySupplierList) {
							supplierIds.add(carBizSupplier.getSupplierId());
						}
					}
				}
				visibleAllianceIds = setToArray(supplierIds);
				Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
				visibleMotocadeIds = setToArray(teamIds);
			}
		}
 		try {
			List<String>  visibleList = systemUtils.supplierIds(cityId,allianceId);
			List<String>  visibleMotoIdsList = null;

			if(visibleMotocadeIds != null){
				visibleMotoIdsList = Arrays.asList(visibleMotocadeIds);
			}

			long dateDiff = DateUtil.calDateDiff(startDate, endDate);

			MaxAndMinId scoreMaxAndMinId = measureDayExMapper.queryMaxAndMinId(startDate,endDate);
 			List<SAASCoreIndexDto> saasCoreIndexDtoList = measureDayExMapper.getCoreIndexStatistic(startDate,endDate,allianceId,motorcadeId,visibleList,visibleMotoIdsList,dateDiff,
					scoreMaxAndMinId== null?null:scoreMaxAndMinId.getMinId(),scoreMaxAndMinId== null?null:scoreMaxAndMinId.getMaxId());

			//获取有责投诉率

			if(CollectionUtils.isNotEmpty(saasCoreIndexDtoList)){
				RedisCacheUtil.set(key,saasCoreIndexDtoList.get(0),3600*24);
				return AjaxResponse.success(saasCoreIndexDtoList.get(0));
			}else {
				return AjaxResponse.success(null);
			}
		} catch (Exception e) {
			logger.error("查询首页统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 有责统计 **/
	@RequestMapping("/countResponsibleComplaintRate")
	@ResponseBody
	public AjaxResponse countResponsibleComplaintRate(@Verify(param = "startDate", rule = "required") String startDate,
												 @Verify(param = "endDate", rule = "required") String endDate,Integer cityId, String allianceId) {

		String key = null;
		try {
			//如果城市权限为空（说明是全国的权限），且数据权限为全国 则缓存一天数据。如果不是，缓存key值为当前登录用户+时间+allianceId
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
			key = "";
			StringBuffer stringBuffer = new StringBuffer();
			if(CollectionUtils.isEmpty(currentLoginUser.getCityIds()) && currentLoginUser.getLevel().equals(PermissionLevelEnum.ALL.getCode())){
				key = RedisKeyUtils.RESPONSIBLE_RATE_STATISTICS + stringBuffer.append(startDate).append(endDate).append(cityId).append(allianceId).toString().replaceAll("null","");
				String  responsibleComplaintRate = RedisCacheUtil.get(key,String.class);
				if(RedisCacheUtil.exist(key) && responsibleComplaintRate != null && !("").equals(responsibleComplaintRate)){
					return  AjaxResponse.success(responsibleComplaintRate);
				}
			}else {
				String keyParam = this.keyParam(cityId,allianceId);

				key = RedisKeyUtils.RESPONSIBLE_RATE_STATISTICS + stringBuffer.append(keyParam).append(currentLoginUser.getId()).append(startDate).append(endDate).append(cityId)
						.append(allianceId).toString().replaceAll("null","");
				String  responsibleComplaintRate = RedisCacheUtil.get(key,String.class);
				if(RedisCacheUtil.exist(key) && responsibleComplaintRate != null && !("").equals(responsibleComplaintRate)){
					return AjaxResponse.success(responsibleComplaintRate);
				}
			}
		} catch (Exception e) {
			logger.error("缓存查询错误",e);
			RedisCacheUtil.delete(key);
		}

 		try {
			String responsibleComplaintRate= driverMeasureDayService.getResponsibleComplaintRate(startDate,endDate,cityId, allianceId);
			//获取有责投诉率
			if(responsibleComplaintRate != null && !("").equals(responsibleComplaintRate)){
				RedisCacheUtil.set(key,responsibleComplaintRate,3600*24);
				return AjaxResponse.success(responsibleComplaintRate);
			}else {
				return AjaxResponse.success(null);
			}
		} catch (Exception e) {
			logger.error("查询有责统计异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 调用大数据接口获取数据  **/
	private AjaxResponse parseResult(String url, Map<String, Object> paramMap) {
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
			Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
			Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取当前登录用户可见城市ID
			// 如果城市id为空，代表可查全国所有数据
			if(cityIds != null && cityIds.size() > 0){
				if(supplierIds == null || supplierIds.size() <= 0){
					List<CarBizSupplier> querySupplierList = citySupplierTeamService.querySupplierList();// 获取用户可见的供应商信息
					if(querySupplierList != null && querySupplierList.size() > 0){
						for (CarBizSupplier carBizSupplier : querySupplierList) {
							supplierIds.add(carBizSupplier.getSupplierId());
						}
					}
				}
				visibleAllianceIds = setToArray(supplierIds);
				Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
				visibleMotocadeIds = setToArray(teamIds);
				paramMap.put("visibleAllianceIds", visibleAllianceIds);
				paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
			}
		}
		try {
			String jsonString = JSON.toJSONString(paramMap);
			String result = HttpClientUtil.buildPostRequest(url)
					.setConnectTimeOut(CONNECT_TIMEOUT)
					.setReadTimeOut(READ_TIMEOUT)
					.setBody(jsonString)
					.addHeader("Content-Type", ContentType.APPLICATION_JSON)
					.execute();
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.info("调用大数据" + url + "返回结果为null");
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			if (!job.getString(Constants.CODE).equals("0")) {
				logger.info("调用大数据接口错误" + url + "返回code" + job.getString(Constants.CODE) + "返回错误信息: " + job.getString("message") );
				return AjaxResponse.fail(Integer.parseInt(job.getString(Constants.CODE)), job.getString("message"));
			}
			JSONArray resultArray = JSON.parseArray(job.getString(Constants.DATA));
			return AjaxResponse.success(resultArray);
		} catch (HttpException e) {
			logger.error("调用大数据" + url + "异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}
	
	/** 将Set<Integer>集合转成String[] **/
	private String[] setToArray(Set<Integer> set){
		if(null == set || set.size() == 0){
			return null;
		}
		Object[] array = set.toArray();
		String[] stra = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			stra[i] = array[i].toString();
		}
		return stra;
	}

	private SAASIndexQuery setVisibleData(){
		logger.info("大数据接口迁移，组装数据");
		SAASIndexQuery saas = new SAASIndexQuery();
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		if(WebSessionUtil.isSupperAdmin() == false){// 如果是普通管理员
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
			Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
			Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取当前登录用户可见城市ID
			// 如果城市id为空，代表可查全国所有数据
			if(cityIds != null && cityIds.size() > 0){
				if(supplierIds == null || supplierIds.size() <= 0){
					List<CarBizSupplier> querySupplierList = citySupplierTeamService.querySupplierList();// 获取用户可见的供应商信息
					if(querySupplierList != null && querySupplierList.size() > 0){
						for (CarBizSupplier carBizSupplier : querySupplierList) {
							supplierIds.add(carBizSupplier.getSupplierId());
						}
					}
				}
				visibleAllianceIds = setToArray(supplierIds);
				Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
				visibleMotocadeIds = setToArray(teamIds);
				if(visibleAllianceIds!=null){
					saas.setVisibleAllianceIds(Arrays.asList(visibleAllianceIds));
				}
				if(visibleMotocadeIds!=null){
					saas.setVisibleMotocadeIds(Arrays.asList(visibleMotocadeIds));
				}
			}
		}
		return saas;
	}


}
