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

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;


/**
 * ClassName: 取消订单分析 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 */
@Controller
@RequestMapping("/cancelOrder")
public class CancelOrderController{
	 private static final Logger logger = LoggerFactory.getLogger(CancelOrderController.class);
	 
	 /*@Value("${statistics.cancelorderdetail.queryList.url}")
	 String  cancelorderdetailQueryListApiUrl;
	 
	 @Value("${statistics.cancelorderdetail.download.url}")
	 String  cancelorderdetailDownloadApiUrl;*/
	 
	 @Value("${saas.bigdata.api.url}")
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
	    										  String driverCityId,
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
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	        Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	        Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	        Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
	        // 供应商信息
			String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
			// 车队信息
			String[] visibleMotocadeIds = statisticalAnalysisService.setToArray(teamIds);
			// 可见城市
			String[] visibleCityIds = statisticalAnalysisService.setToArray(cityIds);
			if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
	        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	        paramMap.put("visibleMotorcardIds", visibleMotocadeIds); // 可见车队ID
	        paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
	        
/*			paramMap.put("visibleAllianceIds", new String[]{"1", "3", "4"});
			paramMap.put("visibleMotorcadeIds", new String[]{"1", "4"});
			paramMap.put("visibleCityIds", new String[]{"44"}); //可见城市ID
*/			
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
   	@RequestMapping(value = "/exportCancelOrderData", method = { RequestMethod.POST,RequestMethod.GET })
	public void exportCancelOrderData( 
											String driverCityId,
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
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	        Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	        Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	        Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
	        // 供应商信息
			String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
			// 车队信息
			String[] visibleMotocadeIds = statisticalAnalysisService.setToArray(teamIds);
			// 可见城市
			String[] visibleCityIds = statisticalAnalysisService.setToArray(cityIds);
			if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
				 logger.info("【运营管理-统计分析】导出取消订单列表数据权限为空");
				 return;
			}
	        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	        paramMap.put("visibleMotorcardIds", visibleMotocadeIds); // 可见车队ID
	        paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID

	/*        paramMap.put("queryDate", "2018-09-04");
			paramMap.put("driverCityId", "44");
			paramMap.put("allianceId", "1");
			paramMap.put("motorcadeId", "1");
			paramMap.put("driverTypeId", "1");
			paramMap.put("channelId", "1");
			paramMap.put("orderVehicleTypeId", "1");
			paramMap.put("productTypeId", "1");
			paramMap.put("cancelDurationTypeId", "1");
			paramMap.put("visibleCityIds", new String[]{"44"}); //可见城市ID
			paramMap.put("visibleAllianceIds", new String[]{"1", "3", "4"});
			paramMap.put("visibleMotorcadeIds", new String[]{"1", "4"});
			paramMap.put("pageNo", "1");
			paramMap.put("pageSize", "1");
			*/
			statisticalAnalysisService.downloadCsvFromTemplet(paramMap,
					saasBigdataApiUrl+"/cancelOrderDetail/download" ,
					request.getRealPath("/")+File.separator+"template"+File.separator+"cancelOrder_info.csv");
			statisticalAnalysisService.exportCsvFromTemplet(response,
					new String("取消订单分析".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"cancelOrder_info.csv");
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
}