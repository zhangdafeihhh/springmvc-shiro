package com.zhuanche.controller.statisticalAnalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

/**
 * 
 * ClassName: 车辆分析 carAnalysisIndex 
 * date: 2018年9月04日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/carAnalysisIndex")
public class CarAnalysisIndexController{
	private static final Logger logger = LoggerFactory.getLogger(CarAnalysisIndexController.class);
	
	/*//指标
	 @Value("${statistics.caranalysisindex.carDetail.url}")
	 String  caranalysisindexCarDetailApiUrl;
	 
	 //趋势
	 @Value("${statistics.caranalysisindex.carIndex.url}")
	 String  caranalysisindexCarIndexApiUrl;*/
	 
	 @Value("${saas.bigdata.api.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 /**
	    * 车辆分析指标明细查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	groupByColumnCode	汇总维度代码
		* @param 	visibleAllianceIds	可见加盟商ID
		* @param 	visibleMotocadeIds	可见车队ID
	    * @return
	  */
	  @RequestMapping(value = "/queryCarAnalysisIndexDetailData", method = { RequestMethod.POST,RequestMethod.GET })
	  public AjaxResponse queryCarAnalysisIndexDetailData(
			  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  @Verify(param = "groupByColumnCode",rule = "required") String groupByColumnCode){
	      logger.info("【运营管理-统计分析】车辆分析指标明细 数据:queryCarAnalysisIndexDetailData");
	      Map<String, Object> paramMap = new HashMap<String, Object>();
	      paramMap.put("startDate", startDate);//订单城市ID	
	      paramMap.put("endDate", endDate);//加盟商ID
	      paramMap.put("groupByColumnCode", groupByColumnCode);//汇总维度代码
	      // 数据权限设置
		  SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	      Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	      Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	      // 供应商信息
		  String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
		  // 车队信息
		  String[] visibleMotocadeIds = statisticalAnalysisService.setToArray(teamIds);
		  if(null == visibleAllianceIds || null == visibleMotocadeIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		  }
	      paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	      paramMap.put("visibleMotocadeIds", visibleMotocadeIds); // 可见车队ID
	      AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/carAnalysisDetail/carDetail",paramMap);
	  	// 从大数据仓库获取统计数据
	      return result;
	  }
	  
  
  
	 	/**
		    * 车辆分析指标趋势查询
			* @param 	startDate	起始日期
			* @param 	endDate	结束日期
			* @param 	allianceId	加盟商ID
			* @param 	vehicleTypeId	车辆类型ID
			* @param 	visibleAllianceIds	可见加盟商ID
			* @param 	allVehicleTypeIds	全部车辆类型ID
		    * @return
		  */
	    @RequestMapping(value = "/queryCarAnalysisIndexWayData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryCarAnalysisIndexWayData(
	    			@Verify(param = "startDate",rule = "required") String startDate,
	    			@Verify(param = "endDate",rule = "required") String endDate, 
	                                              String allianceId,
	                                              String vehicleTypeId,
	                                              String allVehicleTypeIds
	                                              //String visibleAllianceIds,
	                                              //String allVehicleTypeIds
	                                              ){
	        logger.info("【运营管理-统计分析】车辆分析指标趋势 数据:queryCarAnalysisIndexWayData");
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("startDate", startDate);//订单城市ID	
	        paramMap.put("endDate", endDate);//加盟商ID
	        paramMap.put("allianceId", allianceId);//车队ID
	        // 数据权限设置
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	        Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	        Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	        // 供应商信息
			String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
			// 全部车辆类型  ??
			String[] allVehicleTypeIdsArray = statisticalAnalysisService.setToArray(teamIds);
			if(null == visibleAllianceIds || null == allVehicleTypeIdsArray){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
	        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	        paramMap.put("allVehicleTypeIds", allVehicleTypeIds); // 全部车辆类型
	        AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/carAnalysisIndex/carIndex",paramMap);
	    	// 从大数据仓库获取统计数据
	        return result;
	    }

}