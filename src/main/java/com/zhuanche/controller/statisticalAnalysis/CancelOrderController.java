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
 * ClassName: 取消订单分析 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 */
@Controller
@RequestMapping("/cancelOrder")
public class CancelOrderController{
	 private static final Logger logger = LoggerFactory.getLogger(CancelOrderController.class);
	 
	 @Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
		 /**
		    * 查询取消订单列表
		 	* @param 查询日期	queryDate
		    * @param queryDate	查询日期
			* @param driverCityId	司机所属城市ID
			* @param allianceId	加盟商ID
			* @param motorcadeId	车队ID
			* @param driverTypeId	司机类型ID
			* @param channelId	下单渠道ID
			* @param orderVehicleTypeId	预约车型ID
			* @param productTypeId	产品类型ID
			* @param cancelDurationTypeId	取消时长分类ID
			* @param pageNo	页号
			* @param pageSize	每页记录数
			* @param visibleCityIds	可见城市ID
			* @param visibleAllianceIds	可见加盟商ID
			* @param visibleMotorcadeIds	可见车队ID
		    * @return
		  */
	    @ResponseBody
	    @RequestMapping(value = "/queryCancelOrderData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryCancelOrderData(
	    										  Long driverCityId,
	    										  String allianceId,
	                                              String motorcadeId,
	                                              String driverTypeId,
	                                              String channelId,
	                                              String orderVehicleTypeId,
	                                              String productTypeId,
	                                              String cancelDurationTypeId,
	                                              @Verify(param = "queryDate",rule = "required") String queryDate,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                              ){
	        logger.info("【运营管理-统计分析】取消订单列表数据:queryCancelOrderData");
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	        paramMap.put("allianceId", allianceId);//加盟商ID
	        paramMap.put("motorcadeId", motorcadeId);//车队ID
	        paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        paramMap.put("channelId", channelId);//下单渠道ID
	        paramMap.put("orderVehicleTypeId", orderVehicleTypeId);//预约车型ID
	        paramMap.put("productTypeId", productTypeId);//产品类型ID
	        paramMap.put("cancelDurationTypeId", cancelDurationTypeId);//取消时长分类ID
	        paramMap.put("queryDate", queryDate);//查询日期
			
	        // 数据权限设置
	        paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,driverCityId,allianceId,motorcadeId);
		      if(paramMap==null){
		    	  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		      }
			//paramMap.put("visibleAllianceIds", new String[]{"97", "946", "99", "489", "1307", "65", "1349"});
			//paramMap.put("visibleMotorcadeIds", new String[]{"2498", "2487", "1809", "2359", "1369"});
			//paramMap.put("visibleCityIds", new String[]{"88", "67", "111", "123", "85", "91"}); //可见城市ID
	        
			if(null != pageNo && pageNo > 0)
	        	paramMap.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	paramMap.put("pageSize", pageSize);//每页记录数
   
			// 从大数据仓库获取统计数据
	        AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/cancelOrderDetail/queryList",paramMap);
	        return result;
	    }
	    
	    /**
		    * 导出取消订单列表
		 	* @param 查询日期	queryDate
		    * @param queryDate	查询日期
			* @param driverCityId	司机所属城市ID
			* @param allianceId	加盟商ID
			* @param motorcadeId	车队ID
			* @param driverTypeId	司机类型ID
			* @param channelId	下单渠道ID
			* @param orderVehicleTypeId	预约车型ID
			* @param productTypeId	产品类型ID
			* @param cancelDurationTypeId	取消时长分类ID
			* @param visibleCityIds	可见城市ID
			* @param visibleAllianceIds	可见加盟商ID
			* @param visibleMotorcadeIds	可见车队ID
		    * @return
		  */
	@ResponseBody
   	@RequestMapping(value = "/exportCancelOrderData", method = { RequestMethod.POST,RequestMethod.GET })
	public AjaxResponse exportCancelOrderData( 
											Long driverCityId,
											String allianceId,
								            String motorcadeId,
								            String driverTypeId,
								            String channelId,
								            String orderVehicleTypeId,
								            String productTypeId,
								            String cancelDurationTypeId,
								            @Verify(param = "queryDate",rule = "required") String queryDate,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出取消订单列表数据:exportCancelOrderData");
       try {
    	    Map<String, Object> paramMap = new HashMap<String, Object>();
	        paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	        paramMap.put("allianceId", allianceId);//加盟商ID
	        paramMap.put("motorcadeId", motorcadeId);//车队ID
	        paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        paramMap.put("channelId", channelId);//下单渠道ID
	        paramMap.put("orderVehicleTypeId", orderVehicleTypeId);//预约车型ID
	        paramMap.put("productTypeId", productTypeId);//产品类型ID
	        paramMap.put("cancelDurationTypeId", cancelDurationTypeId);//取消时长分类ID
	        paramMap.put("queryDate", queryDate);//查询日期
	        
	       // 数据权限设置
	        paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,driverCityId,allianceId,motorcadeId);
		      if(paramMap==null){
		    	  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		      }
			String jsonString = JSON.toJSONString(paramMap);
			 
			statisticalAnalysisService.exportCsvFromToPage(
					response,
					jsonString,
					saasBigdataApiUrl+"/cancelOrderDetail/download",
					new String("取消订单分析".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"cancelOrder_info.csv");
			
		/*	statisticalAnalysisService.downloadCsvFromTemplet(jsonString,
					saasBigdataApiUrl+"/cancelOrderDetail/download" ,
					request.getRealPath("/")+File.separator+"template"+File.separator+"cancelOrder_info.csv");
			statisticalAnalysisService.exportCsvFromTemplet(response,
					new String("取消订单分析".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"cancelOrder_info.csv");*/
       } catch (Exception e) {
           e.printStackTrace();
       }
       return AjaxResponse.success("成功");
   }
	
	
	
}