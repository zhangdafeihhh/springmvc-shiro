package com.zhuanche.controller.statisticalAnalysis;

import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndex;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndexList;
import com.zhuanche.entity.bigdata.QueryTermDriverAnaly;
import com.zhuanche.serv.bigdata.BiDriverMeasureDayService;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zhuanche.common.enums.MenuEnum.DRIVER_ANALYSIS_DATA;
import static com.zhuanche.common.enums.MenuEnum.DRIVER_ANALYSIS_GRAPH;

/**
 * 
 * ClassName: 司机运营分析 driverOperAnlayTrend
 * date: 2018年9月04日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/driverOperAnlay")
public class DriverOperAnlayController{
	private static final Logger logger = LoggerFactory.getLogger(DriverOperAnlayController.class);
	 
//	 @Value("${bigdata.saas.data.url}")
//	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;

	@Autowired
	private BiDriverMeasureDayService biDriverMeasureDayService;

	 /**
	    * 司机运营分析指标查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	allianceId	加盟商ID
	    * @return
	  */
	  @ResponseBody
	  @RequestMapping(value = "/queryDriverOperAnlayData", method = { RequestMethod.POST,RequestMethod.GET })
	  @RequiresPermissions(value = { "JoinDriverOperateAnalysis_look" } )
	  @RequestFunction(menu = DRIVER_ANALYSIS_DATA)
	  public AjaxResponse queryCarAnalysisIndexDetailData(
			  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  String allianceId, Long cityId,
			  @Verify(param = "type",rule = "required") Integer type){
		  logger.info("【运营管理-统计分析】司机运营分析指标 数据:queryDriverOperAnlayData");
		  Map<String, Object> paramMap = Maps.newHashMap();
		  paramMap.put("startDate", startDate);
		  paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap, cityId, allianceId, null);
		  if(paramMap==null){
			  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		  }
		  QueryTermDriverAnaly queryTermDriverAnaly = new QueryTermDriverAnaly();
		  queryTermDriverAnaly.setStartDate(startDate);
		  queryTermDriverAnaly.setEndDate(endDate);
		  queryTermDriverAnaly.setAllianceId(allianceId);
		  queryTermDriverAnaly.setCityId(cityId);
		  if(paramMap.containsKey("visibleAllianceIds")){
			  queryTermDriverAnaly.setVisibleAllianceIds((Set<String>) paramMap.get("visibleAllianceIds"));// 可见加盟商ID
		  }
		  if(paramMap.containsKey("visibleCityIds")){
			  queryTermDriverAnaly.setVisibleAllianceIds((Set<String>) paramMap.get("visibleCityIds"));// 可见城市ID
		  }
		  queryTermDriverAnaly.setType(type);
		  String table = "bi_driver_disinfect_measure_day";
		  if(type==null || type==1){
			  queryTermDriverAnaly.setDateDate(startDate);
			  queryTermDriverAnaly.setStartDate(null);
			  queryTermDriverAnaly.setEndDate(null);
			  table = "bi_driver_disinfect_measure_day";
		  } else if(type==2){
			  table = "bi_driver_disinfect_measure_week";
		  } else if(type==3){
			  table = "bi_driver_disinfect_measure_month";
		  }
		  queryTermDriverAnaly.setTable(table);

		  // 从大数据仓库获取统计数据
		  List<DriverOperAnalyIndex> driverOperAnalyIndexList = null;
		  try {
			  driverOperAnalyIndexList = biDriverMeasureDayService.query(queryTermDriverAnaly);
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  return AjaxResponse.success(driverOperAnalyIndexList);
	  }
	  


 	/**
	    * 司机运营分析指标趋势查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	allianceId	加盟商ID
	    * @return
	  */
	@ResponseBody
    @RequestMapping(value = "/queryDriverOperAnlayTrendData", method = { RequestMethod.POST,RequestMethod.GET })
	@RequiresPermissions(value = { "JoinDriverOperateAnalysis_look" } )
	@RequestFunction(menu = DRIVER_ANALYSIS_GRAPH)
    public AjaxResponse queryCarAnalysisIndexWayData(
    		  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  String allianceId, Long cityId,
			  @Verify(param = "type",rule = "required") Integer type){
		logger.info("【运营管理-统计分析】司机运营分析指标趋势查询 数据:queryDriverOperAnlayTrendData");
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("startDate", startDate);
		paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap, cityId, allianceId, null);
		if(paramMap==null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		QueryTermDriverAnaly queryTermDriverAnaly = new QueryTermDriverAnaly();
		queryTermDriverAnaly.setStartDate(startDate);
		queryTermDriverAnaly.setEndDate(endDate);
		queryTermDriverAnaly.setAllianceId(allianceId);
		queryTermDriverAnaly.setCityId(cityId);
		if(paramMap.containsKey("visibleAllianceIds")){
			queryTermDriverAnaly.setVisibleAllianceIds((Set<String>) paramMap.get("visibleAllianceIds"));// 可见加盟商ID
		}
		if(paramMap.containsKey("visibleCityIds")){
			queryTermDriverAnaly.setVisibleAllianceIds((Set<String>) paramMap.get("visibleCityIds"));// 可见城市ID
		}
		queryTermDriverAnaly.setType(type);
		if(type==null || type==1){
			queryTermDriverAnaly.setDateDate(startDate);
			queryTermDriverAnaly.setStartDate(null);
			queryTermDriverAnaly.setEndDate(null);
		} else if(type==2 || type==3){
			queryTermDriverAnaly.setType(4);
		}
		// 从大数据仓库获取统计数据
		DriverOperAnalyIndexList trend = null;
		try {
			trend = biDriverMeasureDayService.trend(queryTermDriverAnaly);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AjaxResponse.success(trend);
    }
}