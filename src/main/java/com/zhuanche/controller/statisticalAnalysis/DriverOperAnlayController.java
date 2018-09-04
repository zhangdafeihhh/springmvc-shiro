package com.zhuanche.controller.statisticalAnalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	 
	 /**
	    * 司机运营分析指标查询
		* @param 	startDate	起始日期
		* @param 	endDate	结束日期
		* @param 	allianceId	加盟商ID
		* @param 	visibleAllianceIds	可见加盟商ID
	    * @return
	  */
	  @RequestMapping(value = "/queryDriverOperAnlayData", method = { RequestMethod.POST,RequestMethod.GET })
	  public AjaxResponse queryCarAnalysisIndexDetailData(
			  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  @Verify(param = "allianceId",rule = "required") String allianceId){
	      logger.info("【运营管理-统计分析】司机运营分析指标 数据:queryDriverOperAnlayData");
	      Map<String, Object> paramMap = new HashMap<String, Object>();
	      paramMap.put("startDate", startDate);//订单城市ID	
	      paramMap.put("endDate", endDate);//加盟商ID
	      paramMap.put("allianceId", allianceId);//加盟商ID
	      // 数据权限设置
		  SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	      Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	      // 供应商信息
		  String[] visibleAllianceIds = setToArray(suppliers);
		  if(null == visibleAllianceIds){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		  }
	      paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	      AjaxResponse result = parseResult(saasBigdataApiUrl+"/driverEvaluateDetail/queryList",paramMap);
	  	// 从大数据仓库获取统计数据
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
    @RequestMapping(value = "/queryDriverOperAnlayTrendData", method = { RequestMethod.POST,RequestMethod.GET })
    public AjaxResponse queryCarAnalysisIndexWayData(
    		  @Verify(param = "startDate",rule = "required") String startDate,
			  @Verify(param = "endDate",rule = "required") String endDate, 
			  @Verify(param = "allianceId",rule = "required") String allianceId){
        	  logger.info("【运营管理-统计分析】司机运营分析指标趋势查询 数据:queryDriverOperAnlayTrendData");
        	  Map<String, Object> paramMap = new HashMap<String, Object>();
		      paramMap.put("startDate", startDate);//订单城市ID	
		      paramMap.put("endDate", endDate);//加盟商ID
		      paramMap.put("allianceId", allianceId);//加盟商ID
		      // 数据权限设置
			  SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
		      Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		      // 供应商信息
			  String[] visibleAllianceIds = setToArray(suppliers);
			  if(null == visibleAllianceIds){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			  }
		      paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
		      // 从大数据仓库获取统计数据
	          AjaxResponse result = parseResult(saasBigdataApiUrl+"/driverEvaluateDetail/queryList",paramMap);
	          return result;
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
	
	/** 调用大数据接口获取数据  **/
	private static AjaxResponse parseResult(String url,Map<String, Object> paramMap) {
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
			JSONObject jsonResult = JSON.parseObject(job.getString("result"));
			//JSONArray resultArray = JSON.parseArray(jsonResult.getString("recordList"));
			return AjaxResponse.success(jsonResult);
		} catch (HttpException e) {
			logger.error("调用大数据" + url + "异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}
	
	/** 调用大数据接口获取数据  **/
	private static String parseResultStr(String url, Map<String, Object> paramMap) {
		try {
			String jsonString = JSON.toJSONString(paramMap);
			String result = HttpClientUtil.buildPostRequest(url).setBody(jsonString).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用大数据" + url + "返回结果为null");
				return "";
			}
			if (!job.getString("code").equals("0")) {
				logger.error("调用大数据" + url + "返回结果code:"+job.getString("code")+",message:"+job.getString("message"));
				return  "-1";
			}
			JSONObject jsonResult = JSON.parseObject(job.getString("result"));
			return jsonResult.getString("recordList");
		} catch (HttpException e) {
			logger.error("调用大数据" + url + "异常", e);
			return null;
		}
	}

}