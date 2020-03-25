package com.zhuanche.controller.statisticalAnalysis;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndex;
import com.zhuanche.entity.bigdata.DriverOperAnalyIndexList;
import com.zhuanche.entity.bigdata.QueryTermDriverAnaly;
import com.zhuanche.serv.bigdata.BiDriverMeasureDayService;
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
public class DriverOperAnlayController {

	private static final Logger logger = LoggerFactory.getLogger(DriverOperAnlayController.class);

	private static final String SUPPLIER = "visibleAllianceIds";
	private static final String CITY = "visibleCityIds";

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
		  QueryTermDriverAnaly queryTermDriverAnaly = getQueryTermDriverAnaly(startDate, endDate,
				  allianceId, cityId, type, 1);
		  if(queryTermDriverAnaly==null){
			  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		  }
		  // 从大数据仓库获取统计数据
		  List<DriverOperAnalyIndex> query = biDriverMeasureDayService.query(queryTermDriverAnaly);
		  return AjaxResponse.success(query);
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
		QueryTermDriverAnaly queryTermDriverAnaly = getQueryTermDriverAnaly(startDate, endDate,
				allianceId, cityId, type, 2);
		if(queryTermDriverAnaly==null){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
		// 从大数据仓库获取统计数据
		DriverOperAnalyIndexList trend = biDriverMeasureDayService.trend(queryTermDriverAnaly);
		return AjaxResponse.success(trend);
    }


    public QueryTermDriverAnaly getQueryTermDriverAnaly(String startDate,  String endDate, String allianceId,
														Long cityId,  Integer type, Integer value){
		Map<String, Object> paramMap = biDriverMeasureDayService.getCurrentLoginUserParamMap(cityId, allianceId, null);
		if(paramMap==null){
			return null;
		}
		QueryTermDriverAnaly queryTermDriverAnaly = new QueryTermDriverAnaly();
		queryTermDriverAnaly.setStartDate(startDate);
		queryTermDriverAnaly.setEndDate(endDate);
		queryTermDriverAnaly.setAllianceId(allianceId);
		queryTermDriverAnaly.setCityId(cityId);
		if(paramMap.containsKey(SUPPLIER)){
			// 可见加盟商ID
			queryTermDriverAnaly.setVisibleAllianceIds((Set<String>) paramMap.get(SUPPLIER));
		}
		if(paramMap.containsKey(CITY)){
			// 可见城市ID
			queryTermDriverAnaly.setVisibleCityIds((Set<String>) paramMap.get(CITY));
		}
		queryTermDriverAnaly.setType(type);

		if(type==null || type==1){
			queryTermDriverAnaly.setDateDate(startDate);
			queryTermDriverAnaly.setStartDate(null);
			queryTermDriverAnaly.setEndDate(null);
		}

		if(value==1){
			String table = "bi_driver_disinfect_measure_day";
			if(type==2){
				table = "bi_driver_disinfect_measure_week";
			} else if(type==3){
				table = "bi_driver_disinfect_measure_month";
			}
			queryTermDriverAnaly.setTable(table);
		}
		if(value==2 && (type==2 || type==3)){
			queryTermDriverAnaly.setType(4);
		}
		return queryTermDriverAnaly;
	}
}