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

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

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
	 
	 @Value("${saas.bigdata.api.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 /**
	    * 司机运营分析指标查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	allianceId	加盟商ID
		* @param 	visibleAllianceIds	可见加盟商ID
	    * @return
	  */
	  @ResponseBody
	  @RequestMapping(value = "/queryDriverOperAnlayData", method = { RequestMethod.POST,RequestMethod.GET })
	  public AjaxResponse queryCarAnalysisIndexDetailData(
			  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  String allianceId){
	      logger.info("【运营管理-统计分析】司机运营分析指标 数据:queryDriverOperAnlayData");
	      Map<String, Object> paramMap = new HashMap<String, Object>();
	      paramMap.put("startDate", startDate);//订单城市ID	
	      paramMap.put("endDate", endDate);//加盟商ID
	      paramMap.put("allianceId", allianceId);//加盟商ID
	      // 数据权限设置
		  SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		  if(currentLoginUser == null){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
		  Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	      // 供应商信息
		  String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
		  if(null == visibleAllianceIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		  }
	      paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	      // 从大数据仓库获取统计数据
	      AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/driverOperAnaly/query",paramMap);
	      return result;
	  }
	  


 	/**
	    * 司机运营分析指标趋势查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	allianceId	加盟商ID
		* @param 	visibleAllianceIds	可见加盟商ID
	    * @return
	  */
	@ResponseBody
    @RequestMapping(value = "/queryDriverOperAnlayTrendData", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse queryCarAnalysisIndexWayData(
    		  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  String allianceId){
        	  logger.info("【运营管理-统计分析】司机运营分析指标趋势查询 数据:queryDriverOperAnlayTrendData");
        	  Map<String, Object> paramMap = new HashMap<String, Object>();
		      paramMap.put("startDate", startDate);//订单城市ID	
		      paramMap.put("endDate", endDate);//加盟商ID
		      paramMap.put("allianceId", allianceId);//加盟商ID
		      // 数据权限设置
			  SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
			  if(currentLoginUser == null){
					return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
				}
			  Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		      // 供应商信息
			  String[] visibleAllianceIds = statisticalAnalysisService.setToArray(suppliers);
			  if(null == visibleAllianceIds){
				logger.info("【运营管理-统计分析】司机运营分析指标趋势查询 数据:授权不足");
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			  }
		      paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
		      // 从大数据仓库获取统计数据
	          AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/driverOperAnlayTrend/trend",paramMap);
	          return result;
    }
}