package com.zhuanche.controller.statisticalAnalysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.CAR_DETAIL_ANALYSIS;
import static com.zhuanche.common.enums.MenuEnum.CAR_TABLE_ANALYSIS;

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
		* @param 	groupByColumnCode	可见加盟商ID
		* @param 	visibleVehicleTypeIds	可见车辆类型ID
	    * @return
	  */
	  @ResponseBody
	  @RequestMapping(value = "/queryCarAnalysisIndexDetailData", method = { RequestMethod.POST,RequestMethod.GET })
  	  @RequiresPermissions(value = { "CarOperateAnalysis_look" } )
	  @RequestFunction(menu = CAR_TABLE_ANALYSIS)
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
			* @param 	carGroupId	车辆类型ID
			* @param 	motorcadeId	车队ID
		    * @return
		  */
		@ResponseBody
	    @RequestMapping(value = "/queryCarAnalysisIndexWayData", method = { RequestMethod.POST,RequestMethod.GET })
		@RequiresPermissions(value = { "CarOperateAnalysis_look" } )
		@RequestFunction(menu = CAR_DETAIL_ANALYSIS)
	    public AjaxResponse queryCarAnalysisIndexWayData(
	    			@Verify(param = "startDate",rule = "required") String startDate,
	    			@Verify(param = "endDate",rule = "required") String endDate, 
	                 String allianceId,  // 加盟商ID
					String motorcadeId,// 车队ID
					String carGroupId// 车辆类型ID
					){

	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("startDate", startDate);//
	        paramMap.put("endDate", endDate);//
	        if(StringUtil.isNotEmpty(allianceId)){
	        	paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(StringUtil.isNotEmpty(motorcadeId)){
	        	paramMap.put("motorcadeId", motorcadeId);//车队ID
	        }
			if(StringUtil.isNotEmpty(carGroupId)){
				paramMap.put("carGroupId", carGroupId); // 车辆类型  ??
			}
	        String httpUrl = saasBigdataApiUrl+"/carAnalysisIndex/carIndex";
			logger.info("【运营管理-统计分析】车辆分析指标趋势 数据:"+JSON.toJSONString(paramMap));
			JSONObject responseObject = null;
			try {
				// 数据权限设置
				paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,null,allianceId,null);
				if(paramMap==null){
					return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
				}
				// 从大数据仓库获取统计数据
				HttpHeaders headers = new HttpHeaders();
				MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
				headers.setContentType(type);
				headers.add("Accept", MediaType.APPLICATION_JSON.toString());
				HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(paramMap), headers);
				RestTemplate restTemplate = new RestTemplate();
				responseObject = restTemplate.postForObject(httpUrl, formEntity, JSONObject.class);
				logger.info("【运营管理-统计分析】车辆分析指标趋势-请求参数" + JSON.toJSONString(paramMap)+",请求url："+httpUrl+",返回结果为："+(responseObject == null?"null":responseObject.toJSONString()));

				if (responseObject != null) {
					Integer code = responseObject.getInteger("code");
					if (code == 0) {
						//gps有返回
						JSONObject gpgData = responseObject.getJSONObject("result");
						return AjaxResponse.success(gpgData);
					}else {
						logger.error("【运营管理-统计分析】车辆分析指标趋势异常-请求参数" + JSON.toJSONString(paramMap)+",请求url："+httpUrl+",返回结果为："+(responseObject == null?"null":responseObject.toJSONString()));
					}
				}
				return AjaxResponse.success(null);
			}  catch (Exception e) {
				logger.error("【运营管理-统计分析】车辆分析指标趋势异常-请求参数" + JSON.toJSONString(paramMap)+",请求url："+httpUrl+",返回结果为："+(responseObject == null?"null":responseObject.toJSONString()),e);
				return AjaxResponse.fail(RestErrorCode.MONITOR_CARINDEX_FAIL,null);

			}
	    }
}