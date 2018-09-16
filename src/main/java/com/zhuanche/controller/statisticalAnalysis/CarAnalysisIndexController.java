package com.zhuanche.controller.statisticalAnalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ValidateUtils;

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

	@Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 /**
	    * 车辆分析指标明细查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	groupByColumnCode	汇总维度代码
		* @param 	visibleAllianceIds	可见加盟商ID
		* @param 	visibleVehicleTypeIds	可见车辆类型ID
	    * @return
	  */
	  @ResponseBody
	  @RequestMapping(value = "/queryCarAnalysisIndexDetailData", method = { RequestMethod.POST,RequestMethod.GET })
	  public AjaxResponse queryCarAnalysisIndexDetailData(
			  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  @Verify(param = "groupByColumnCode",rule = "required") String groupByColumnCode,
              @Verify(param = "visibleVehicleTypeIds",rule = "required") String visibleVehicleTypeIds){
	      logger.info("【运营管理-统计分析】车辆分析指标明细 数据:queryCarAnalysisIndexDetailData");
	      Map<String, Object> paramMap = new HashMap<String, Object>();
	      paramMap.put("startDate", startDate);//订单城市ID	
	      paramMap.put("endDate", endDate);//加盟商ID
	      paramMap.put("groupByColumnCode", groupByColumnCode);//汇总维度代码
	      // 数据权限设置
	      paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,null,null,null);
	      if(paramMap==null){
	    	  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
	      }
		  if(null == visibleVehicleTypeIds){
				return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID,"可见车辆类型ID参数不能为空");
		  }
		  // 全部车辆类型  ??
		  String[] visibleVehicleTypeIdsStr = visibleVehicleTypeIds.split(",");
		  paramMap.put("visibleVehicleTypeIds", visibleVehicleTypeIdsStr); // 可见车辆类型ID
	      // 从大数据仓库获取统计数据
	      AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/carAnalysisDetail/carDetail",paramMap);
	      return result;
	  }
	  
  
  
	 	/**
		    * 车辆分析指标趋势查询
			* @param 	startDate	起始日期
			* @param 	endDate	结束日期
			* @param 	allianceId	加盟商ID
			* @param 	vehicleTypeId	车辆类型ID
			* @param 	visibleAllianceIds	可见加盟商ID
			* @param 	visibleVehicleTypeIds	可见车辆类型ID
		    * @return
		  */
		@ResponseBody
	    @RequestMapping(value = "/queryCarAnalysisIndexWayData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryCarAnalysisIndexWayData(
	    			@Verify(param = "startDate",rule = "required") String startDate,
	    			@Verify(param = "endDate",rule = "required") String endDate, 
	                                              String allianceId,
	                                              String vehicleTypeId,
	                @Verify(param = "visibleVehicleTypeIds",rule = "required") String visibleVehicleTypeIds
	                                              ){
	        logger.info("【运营管理-统计分析】车辆分析指标趋势 数据:queryCarAnalysisIndexWayData");
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("startDate", startDate);//
	        paramMap.put("endDate", endDate);//
	        if(StringUtil.isNotEmpty(allianceId)){
	        	paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(StringUtil.isNotEmpty(vehicleTypeId)){
	        	paramMap.put("vehicleTypeId", vehicleTypeId);//车辆类型ID
	        }
	        // 数据权限设置
		      paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,null,null,allianceId);
		      if(paramMap==null){
		    	  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		      }
			  // 车辆类型  ??
			  String[] visibleVehicleTypeIdsStr = visibleVehicleTypeIds.split(",");
		      paramMap.put("visibleVehicleTypeIds", visibleVehicleTypeIdsStr); // 可见车辆类型ID
		      // 从大数据仓库获取统计数据
	          AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/carAnalysisIndex/carIndex",paramMap);
	        return result;
	    }

}