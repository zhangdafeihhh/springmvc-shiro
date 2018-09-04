package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.zhuanche.dto.rentcar.DriverEvaluateDTO;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.statisticalAnalysis.DriverEvaluateService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;

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
	 
	 @Value("${saas.bigdata.api.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private DriverEvaluateService driverEvaluateService;
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
	    @RequestMapping(value = "/queryDriverEvaluateData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryDriverEvaluateData(
	    										  String orderCityId,
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
	        // 数据权限设置
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	        Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	        Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	        Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
	        // 供应商信息
			String[] visibleAllianceIds = setToArray(suppliers);
			// 车队信息
			String[] visibleMotocadeIds = setToArray(teamIds);
			// 可见城市
			String[] visibleCityIds = setToArray(cityIds);
			if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
				return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
	        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	        paramMap.put("visibleMotorcardIds", visibleMotocadeIds); // 可见车队ID
	        paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
	        if(null != pageNo && pageNo > 0)
	        	paramMap.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	paramMap.put("pageSize", pageSize);//每页记录数
	        
	        AjaxResponse result = parseResult(saasBigdataApiUrl+"/driverEvaluateDetail/queryList",paramMap);
	    	// 从大数据仓库获取统计数据
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
										String orderCityId,
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
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
	        Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
	        Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
	        Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
	        // 供应商信息
			String[] visibleAllianceIds = setToArray(suppliers);
			// 车队信息
			String[] visibleMotocadeIds = setToArray(teamIds);
			// 可见城市
			String[] visibleCityIds = setToArray(cityIds);
			if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
				 // return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
	        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	        paramMap.put("visibleMotorcardIds", visibleMotocadeIds); // 可见车队ID
	        paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
	        String resultStr = parseResultStr(saasBigdataApiUrl+"/driverEvaluateDetail/download",paramMap);
	        //调用接口
	        List<DriverEvaluateDTO> list = JSONObject.parseArray(resultStr, DriverEvaluateDTO.class);
	        
          @SuppressWarnings("deprecation")
          Workbook wb = driverEvaluateService.exportExcelDriverEvaluate(list,request.getRealPath("/")+File.separator+"template"+File.separator+"driverEvaluate_info.xlsx");
          exportExcelFromTemplet(request, response, wb, new String("司机评级详情分析".getBytes("gb2312"), "iso8859-1"));
      } catch (IOException e) {
          e.printStackTrace();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  
	 /**
	    * 查询对司机评级详情分析 明细
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
 @RequestMapping(value = "/queryDriverEvaluateDetailData", method = { RequestMethod.POST,RequestMethod.GET })
 public AjaxResponse queryDriverEvaluateDetailData(
 										   String orderCityId,
 										   String driverTypeId,
                                           String allianceId,
                                           String motorcadeId,
                                           String driverScore,
                                           String appScore,
                                           @Verify(param = "queryDate",rule = "required") String queryDate){
     logger.info("【运营管理-统计分析】对司机评级详情分析 明细数据:queryDriverEvaluateDetailData");
     Map<String, Object> paramMap = new HashMap<String, Object>();
     paramMap.put("orderCityId", orderCityId);//订单城市ID	
     paramMap.put("allianceId", allianceId);//加盟商ID
     paramMap.put("motorcadeId", motorcadeId);//车队ID
     paramMap.put("driverTypeId", driverTypeId);//司机类型ID
     paramMap.put("driverScore", driverScore);//司机评价分数	
     paramMap.put("appScore", appScore);//APP评价分数	
	 paramMap.put("queryDate", queryDate);//查询日期
     // 数据权限设置
	 SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
     Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
     Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
     Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
     // 供应商信息
		String[] visibleAllianceIds = setToArray(suppliers);
		// 车队信息
		String[] visibleMotocadeIds = setToArray(teamIds);
		// 可见城市
		String[] visibleCityIds = setToArray(cityIds);
		if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
			return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		}
     paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
     paramMap.put("visibleMotorcardIds", visibleMotocadeIds); // 可见车队ID
     paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
 	 paramMap.put("pageNo", "1");//页号
 	 paramMap.put("pageSize", "1");//每页记录数
     
     AjaxResponse result = parseResult(saasBigdataApiUrl+"/driverEvaluateDetail/queryList",paramMap);
 	// 从大数据仓库获取统计数据
     return result;
 }
 

  	
  	
   public void exportExcelFromTemplet(HttpServletRequest request, HttpServletResponse response, Workbook wb, String fileName) throws IOException {
       if(StringUtils.isEmpty(fileName)) {
           fileName = "exportExcel";
       }
       response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");//指定下载的文件名
       response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
       ServletOutputStream os =  response.getOutputStream();
       wb.write(os);
       os.close();
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