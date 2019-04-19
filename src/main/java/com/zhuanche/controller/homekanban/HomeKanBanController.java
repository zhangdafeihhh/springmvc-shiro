package com.zhuanche.controller.homekanban;

import java.time.LocalDate;
import java.util.*;

import com.zhuanche.constant.Constants;
import com.zhuanche.entity.bigdata.SAASCoreIndexDto;
import com.zhuanche.entity.bigdata.SAASDriverRankingDto;
import com.zhuanche.entity.bigdata.SAASIndexQuery;
import com.zhuanche.entity.bigdata.StatisticSection;
import com.zhuanche.serv.bigdata.AllianceIndexService;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.common.CitySupplierTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

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

	/** 日均运营车辆统计查询接口 **/
	@RequestMapping("/operatingVehicleStatistics")
	@ResponseBody
	public AjaxResponse operatingVehicleStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		// 从大数据仓库获取统计数据
		/*Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);*/

		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			List<Map> resultList = allianceIndexService.getCarOperateStatistics(saas);
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

	/** 订单数量统计 **/
	@RequestMapping("/orderStatistics")
	@ResponseBody
	public AjaxResponse orderStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		/*// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);*/
		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			List<Map> resultList = allianceIndexService.getOrderNumStatistic(saas);
			if(CollectionUtils.isNotEmpty(resultList)){
				return AjaxResponse.success(resultList);
			}else {
				return AjaxResponse.success(new ArrayList<>());
			}
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
			@Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		/*// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		return parseResult(statisticsEvaluationUrl, paramMap);*/
		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
			List<Map> resultList = allianceIndexService.getServiceNegativeRate(saas);
			if(CollectionUtils.isNotEmpty(resultList)){
				return AjaxResponse.success(resultList);
			}else {
				return AjaxResponse.success(new ArrayList<>());
			}
		}catch (Exception e){
			logger.error("查询首页订单数量统计错误异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}

	/** 在线时长统计 **/
	@RequestMapping("/onlineTimeStatistics")
	@ResponseBody
	public AjaxResponse onlineTimeStatistics(@Verify(param = "startDate", rule = "required") String startDate,
			@Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId) {

		// 如果加盟商ID为空，不允许传入车队ID
		if (StringUtils.isNotBlank(motorcadeId) && StringUtils.isBlank(allianceId)) {
			logger.warn("如果加盟商ID为空，不允许传入车队ID");
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		
		/*// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		return parseResult(onlineTimeUrl, paramMap);*/
		try{
			SAASIndexQuery saas = setVisibleData();
			saas.setStartDate(startDate);
			saas.setEndDate(endDate);
			saas.setAllianceId(allianceId);
			saas.setMotorcadeId(motorcadeId);
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
	public AjaxResponse vehicleTopStatistics(String allianceId, String motorcadeId,
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
		// 从大数据仓库获取统计数据
		/*Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("orderByColumnCode", orderByColumnCode);
		paramMap.put("orderByTypeCode", orderByTypeCode);
		paramMap.put("topNum", topNum);
		return parseResult(vehicleTopUrl, paramMap);*/
		try{

			List<String>  visibleList = null;
			List<String>  visibleMotoIdsList = null;
			if(visibleAllianceIds != null){
				visibleList = Arrays.asList(visibleAllianceIds);
			}
			if(visibleMotocadeIds != null){
				visibleMotoIdsList = Arrays.asList(visibleMotocadeIds);
			}
			String date = LocalDate.now().minusMonths(1).toString();
			List<SAASDriverRankingDto> driverRankingSections = driverRankDetaiExlMapper.getDriverRanking(allianceId,motorcadeId,orderByColumnCode,orderByTypeCode,Integer.parseInt(topNum),visibleList,visibleMotoIdsList,date);
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
			@Verify(param = "endDate", rule = "required") String endDate, String allianceId, String motorcadeId) {

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
		// 从大数据仓库获取统计数据
		try {
			List<String>  visibleList = null;
			List<String>  visibleMotoIdsList = null;
			if(visibleAllianceIds != null){
				visibleList = Arrays.asList(visibleAllianceIds);
			}
			if(visibleMotocadeIds != null){
				visibleMotoIdsList = Arrays.asList(visibleMotocadeIds);
			}

			long dateDiff = DateUtil.calDateDiff(startDate, endDate);

			List<SAASCoreIndexDto> saasCoreIndexDtoList = measureDayExMapper.getCoreIndexStatistic(startDate,endDate,allianceId,motorcadeId,visibleList,visibleMotoIdsList,dateDiff);

			if(CollectionUtils.isNotEmpty(saasCoreIndexDtoList)){
				return AjaxResponse.success(saasCoreIndexDtoList.get(0));
			}else {
				return AjaxResponse.success(null);
			}
		} catch (Exception e) {
			logger.error("查询首页统计错误异常", e);
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
				logger.error("调用大数据" + url + "返回结果为null");
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			if (!job.getString(Constants.CODE).equals("0")) {
				logger.error("调用大数据接口错误" + url + "返回code" + job.getString(Constants.CODE) + "返回错误信息: " + job.getString("message") );
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

				saas.setVisibleAllianceIds(Arrays.asList(visibleAllianceIds));
				saas.setVisibleMotocadeIds(Arrays.asList(visibleMotocadeIds));

			}
		}
		return saas;
	}
	
}
