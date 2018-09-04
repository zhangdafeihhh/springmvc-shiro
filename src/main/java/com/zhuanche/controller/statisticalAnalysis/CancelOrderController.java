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
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CancelOrderDTO;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.http.PostRequest;
import com.zhuanche.serv.statisticalAnalysis.CancelOrderService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;


/**
 * 
 * ClassName: 取消订单分析 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 * allianceAssessment
 */
@Controller
@RequestMapping("/cancelOrder")
public class CancelOrderController{
	 private static final Logger logger = LoggerFactory.getLogger(CancelOrderController.class);
	 
	 @Value("${saas.bigdata.api.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private CancelOrderService cancelOrderService;
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
	        
	        AjaxResponse result = parseResult(saasBigdataApiUrl+"/cancelOrderDetail/queryList",paramMap);
	    	// 从大数据仓库获取统计数据
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
			String[] visibleAllianceIds = setToArray(suppliers);
			// 车队信息
			String[] visibleMotocadeIds = setToArray(teamIds);
			// 可见城市
			String[] visibleCityIds = setToArray(cityIds);
			if(null == visibleAllianceIds || null == visibleMotocadeIds || visibleCityIds == null ){
				//return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
			}
	        paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
	        paramMap.put("visibleMotorcardIds", visibleMotocadeIds); // 可见车队ID
	        paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
	        String resultStr = parseResultStr(saasBigdataApiUrl+"/cancelOrderDetail/queryList",paramMap);
	    	
	        //调用接口
	        List<CancelOrderDTO> list = JSONObject.parseArray(resultStr, CancelOrderDTO.class);
	        
           @SuppressWarnings("deprecation")
           Workbook wb = cancelOrderService.exportExcelCancelOrder(list,request.getRealPath("/")+File.separator+"template"+File.separator+"cancelOrder_info.xlsx");
           exportExcelFromTemplet(request, response, wb, new String("完成订单详情".getBytes("gb2312"), "iso8859-1"));
       } catch (IOException e) {
           e.printStackTrace();
       } catch (Exception e) {
           e.printStackTrace();
       }
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
			logger.info("excel--" + job.getString("result"));
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
	
	public static void main(String[] args) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("queryDate", "2018-08-28");
		paramMap.put("endDate", "2018-08-28");
		paramMap.put("allianceId", "1");
		paramMap.put("motorcadeId", "2");
		paramMap.put("driverTypeId", "3");
		paramMap.put("channelId", "2");
		paramMap.put("orderVehicleTypeId", "1");
		paramMap.put("productTypeId", "2");
		paramMap.put("cancelDurationTypeId", "1");
		paramMap.put("visibleCityIds", new String[]{"1", "3", "5"}); //可见城市ID
		paramMap.put("visibleAllianceIds", new String[]{"1", "3", "4"});
		paramMap.put("visibleMotorcadeIds", new String[]{"2", "4"});
        
        
		String jsonString = JSON.toJSONString(paramMap);
		System.out.println(jsonString);
		try {
			// AjaxResponse result = parseResult("http://test-inside-bigdata-saas-data.01zhuanche.com/cancelOrderDetail/download",paramMap);
			
			//String result = HttpClientUtil.buildPostRequest("http://test-inside-bigdata-saas-data.01zhuanche.com/cancelOrderDetail/download").execute();
			String result =  HttpClientUtil.buildPostRequest("http://test-inside-bigdata-saas-data.01zhuanche.com/cancelOrderDetail/download")
					.setBody(jsonString).addHeader("encoding", "UTF-8").addHeader("content-type", ContentType.APPLICATION_JSON).execute();
			//JSONObject job = JSON.parseObject(result);
			
			String[] a  = result.split(",");
			System.out.println("---"+a.length);
			//AjaxResponse result = parseResult("http://test-inside-bigdata-saas-data.01zhuanche.com/cancelOrderDetail/queryList",paramMap);
			//.setBody(jsonString).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute()
			System.out.println("---"+result);
			//System.out.println("---"+result.getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
}