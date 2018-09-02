package com.zhuanche.controller.homekanban;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**
 * 首页看板数据提供 ClassName: IndexController.java Date: 2018年8月31日
 * 
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
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		if(currentLoginUser == null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		visibleAllianceIds = setToArray(supplierIds);
		Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		visibleAllianceIds = setToArray(supplierIds);
		visibleMotocadeIds = setToArray(teamIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("visibleAllianceIds", visibleAllianceIds);
		paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
		return parseResult(operatingVehicleUrl, paramMap);
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
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		if(currentLoginUser == null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		visibleAllianceIds = setToArray(supplierIds);
		Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		visibleMotocadeIds = setToArray(teamIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("visibleAllianceIds", visibleAllianceIds);
		paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
		return parseResult(statisticsOrderUrl, paramMap);
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
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		if(currentLoginUser == null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		visibleAllianceIds = setToArray(supplierIds);
		Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		visibleMotocadeIds = setToArray(teamIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("visibleAllianceIds", visibleAllianceIds);
		paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
		return parseResult(statisticsEvaluationUrl, paramMap);
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
		
		// 供应商信息
		String[] visibleAllianceIds = null;
		// 车队信息
		String[] visibleMotocadeIds = null;
		// 数据权限设置
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		if(currentLoginUser == null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		visibleAllianceIds = setToArray(supplierIds);
		Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		visibleMotocadeIds = setToArray(teamIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}

		// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("visibleAllianceIds", visibleAllianceIds);
		paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
		return parseResult(onlineTimeUrl, paramMap);
	}

	/** 司机排名统计 **/
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
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		visibleAllianceIds = supplierIds.toArray(visibleAllianceIds);
		Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		visibleMotocadeIds = teamIds.toArray(visibleMotocadeIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("visibleAllianceIds", visibleAllianceIds);
		paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
		paramMap.put("orderByColumnCode", orderByColumnCode);
		paramMap.put("orderByTypeCode", orderByTypeCode);
		paramMap.put("topNum", topNum);
		return parseResult(vehicleTopUrl, paramMap);
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
		SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		if(currentLoginUser == null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		Set<Integer> supplierIds = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		visibleAllianceIds = setToArray(supplierIds);
		Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		visibleMotocadeIds = setToArray(teamIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		// 从大数据仓库获取统计数据
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("allianceId", allianceId);
		paramMap.put("motorcadeId", motorcadeId);
		paramMap.put("visibleAllianceIds", visibleAllianceIds);
		paramMap.put("visibleMotocadeIds", visibleMotocadeIds);
		return parseResult(coreIndicatorsUrl, paramMap);
	}

	/** 调用大数据接口获取数据  **/
	private AjaxResponse parseResult(String url, Map<String, Object> paramMap) {
		try {
			String jsonString = JSON.toJSONString(paramMap);
			String result = HttpClientUtil.buildPostRequest(url).setBody(jsonString).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用大数据" + url + "返回结果为null");
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			if (!job.getString("code").equals("0")) {
				return AjaxResponse.fail(Integer.parseInt(job.getString("code")), job.getString("message"));
			}
			JSONArray resultArray = JSON.parseArray(job.getString("result"));
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
	public static void main(String[] args) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", "2018-08-21");
		paramMap.put("endDate", "2018-08-28");
		paramMap.put("allianceId", "");
		paramMap.put("motorcadeId", "");
		paramMap.put("visibleAllianceIds", new String[]{"1", "3", "5"});
		paramMap.put("visibleMotocadeIds", new String[]{"1", "3", "5"});
		String jsonString = JSON.toJSONString(paramMap);
		System.out.println(jsonString);
		try {
			String result = HttpClientUtil.buildPostRequest("http://test-inside-bigdata-saas-data.01zhuanche.com/dayMotionCar/statistic").setBody(jsonString).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
			System.out.println(result);
		} catch (HttpException e) {
			e.printStackTrace();
		}
	}
}
