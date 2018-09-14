package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ValidateUtils;

/**
 * 
 * ClassName: 对司机评级详情分析 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/driverEvaluate")
public class DriverEvaluateController{
	private static final Logger logger = LoggerFactory.getLogger(DriverEvaluateController.class);
	 
	 @Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
		 /**
		    * 查询对司机评级详情分析 列表
		    * @param queryDate	查询日期
			* @param orderCityId 订单城市ID	
			* @param driverTypeId 司机类型ID	
			* @param allianceId 加盟商ID	
			* @param motorcadeId 车队ID	
			* @param driverScore 司机评价分数	
			* @param appScore APP评价分数	
			* @param pageNo	页号
			* @param pageSize	每页记录数
			* @param visibleCityIds	可见城市ID
			* @param visibleAllianceIds	可见加盟商ID
			* @param visibleMotorcadeIds	可见车队ID
		    * @return
		  */
		@ResponseBody
	    @RequestMapping(value = "/queryDriverEvaluateData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryDriverEvaluateData(
	    										  Long orderCityId,
	    										  String driverTypeId,
	                                              String allianceId,
	                                              String motorcadeId,
	                                              String driverScore,
	                                              String appScore,
	                                              @Verify(param = "queryDate",rule = "required") String queryDate,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                              ){
	        logger.info("【运营管理-统计分析】对司机评级详情分析 列表数据:queryDriverEvaluateData");
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("orderCityId", orderCityId);//订单城市ID	
	        paramMap.put("allianceId", allianceId);//加盟商ID
	        paramMap.put("motorcadeId", motorcadeId);//车队ID
	        paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        paramMap.put("driverScore", driverScore);//司机评价分数	
	        paramMap.put("appScore", appScore);//APP评价分数	
			paramMap.put("queryDate", queryDate);//查询日期
			paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,orderCityId,allianceId,motorcadeId);
			if(paramMap==null){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
		   if(null != pageNo && pageNo > 0)
	        	paramMap.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	paramMap.put("pageSize", pageSize);//每页记录数
	        // 从大数据仓库获取统计数据
	        AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/driverEvaluateDetail/queryList",paramMap);
	        return result;
	    }
	    
	    /**
		    * 导出对司机评级详情分析 
		    * @param queryDate	查询日期
			* @param orderCityId 订单城市ID	
			* @param driverTypeId 司机类型ID	
			* @param allianceId 加盟商ID	
			* @param motorcadeId 车队ID	
			* @param driverScore 司机评价分数	
			* @param appScore APP评价分数	
			* @param visibleCityIds	可见城市ID
			* @param visibleAllianceIds	可见加盟商ID
			* @param visibleMotorcadeIds	可见车队ID
		    * @return
		  */
  	@RequestMapping(value = "/exportDriverEvaluateData", method = { RequestMethod.POST,RequestMethod.GET })
	public void exportDriverEvaluateData( 
										Long orderCityId,
										String driverTypeId,
							            String allianceId,
							            String motorcadeId,
							            String driverScore,
							            String appScore,
							            @Verify(param = "queryDate",rule = "required") String queryDate,
	                                    HttpServletRequest request,
	                                    HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出对司机评级详情列表数据:exportCancelOrderData");
      try {
    	  Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("orderCityId", orderCityId);//订单城市ID	
	        paramMap.put("allianceId", allianceId);//加盟商ID
	        paramMap.put("motorcadeId", motorcadeId);//车队ID
	        paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        paramMap.put("driverScore", driverScore);//司机评价分数	
	        paramMap.put("appScore", appScore);//APP评价分数	
			paramMap.put("queryDate", queryDate);//查询日期
	        // 数据权限设置
			paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,orderCityId,allianceId,motorcadeId);
			if(paramMap==null){
				return;
			}
	        String jsonString = JSON.toJSONString(paramMap);
	        
		   statisticalAnalysisService.exportCsvFromToPage(
				response,
				jsonString,
				saasBigdataApiUrl+"/driverEvaluateDetail/download" ,
				new String("对司机评级详情分析".getBytes("gb2312"), "iso8859-1"),
				request.getRealPath("/")+File.separator+"template"+File.separator+"driverEvaluate_info.csv");
				
			  /*
	        statisticalAnalysisService.downloadCsvFromTemplet(jsonString,
	        		saasBigdataApiUrl+"/driverEvaluateDetail/download" ,
					request.getRealPath("/")+File.separator+"template"+File.separator+"driverEvaluate_info.csv");
			statisticalAnalysisService.exportCsvFromTemplet(response,
					new String("对司机评级详情分析".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"driverEvaluate_info.csv");*/
       
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

}