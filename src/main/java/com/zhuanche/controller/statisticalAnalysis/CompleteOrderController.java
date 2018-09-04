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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CompleteOrderDTO;
import com.zhuanche.dto.rentcar.CompleteOrderDetailDTO;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.statisticalAnalysis.CompleteOrderService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;

/**
 * 
 * ClassName: 完成订单详情分析 
 * date: 2018年9月01日 下午7:19:45 
 * @author jiadongdong
 *
 */
@Controller
@RequestMapping("/completeOrder")
public class CompleteOrderController{
	 private static final Logger logger = LoggerFactory.getLogger(CompleteOrderController.class);
	 
	 @Value("${saas.bigdata.api.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private CompleteOrderService completeOrderService;
	 
	 	/**
	     * 查询完成订单列表
	     * @return
	     */
	    @RequestMapping(value = "/queryCompleteOrdeData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryCompleteOrderData(
	    										  @Verify(param = "queryDate",rule = "required") String queryDate,
	    										  String cityId,
	    										  String productId,
	                                              String bindVehicleTypeId,
	                                              String serviceVehicleTypeId,
	                                              String orderTypeId,
	                                              String orgnizationId,
	                                              String channelId,
	                                              String driverTypeId,
	                                              String allianceId,
	                                              String motorcardId,
	                                              String hotelId,
	                                              String driverId,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize){
	        logger.info("【运营管理-统计分析】完成订单列表数据:queryCompleteOrderData");
	        
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("queryDate", queryDate);//查询日期
	        params.put("cityId", cityId);//下单城市ID
	        params.put("productId", productId);//产品类型ID
	        params.put("bindVehicleTypeId", bindVehicleTypeId);//绑定车型ID
	        params.put("serviceVehicleTypeId", serviceVehicleTypeId);//服务车型ID
	        params.put("orderTypeId", orderTypeId);//订单类别ID
	        params.put("orgnizationId", orgnizationId);//机构ID
	        params.put("channelId", channelId);//渠道ID
	        params.put("driverTypeId", driverTypeId);//司机类型ID
	        params.put("allianceId", allianceId);//加盟商ID
	        params.put("motorcardId", motorcardId);//车队ID
	        params.put("hotelId", hotelId);//酒店ID
	        params.put("driverId", driverId);//司机ID
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
	        	params.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	params.put("pageSize", pageSize);//每页记录数
		     //从大数据仓库获取统计数据
		     AjaxResponse result = parseResult(saasBigdataApiUrl+"/cancelOrderDetail/queryList",paramMap);
		     return result;
	  }
	 

	    /**
	     *导出完成订单列表
	     * @return
	     */
    	@RequestMapping(value = "/exportCompleteOrderData", method = { RequestMethod.POST,RequestMethod.GET })
 	    public void exportCompleteOrderData(
 	    										  @Verify(param = "queryDate",rule = "required") String queryDate,
 	    										  String cityId,
 	    										  String productId,
 	                                              String bindVehicleTypeId,
 	                                              String serviceVehicleTypeId,
 	                                              String orderTypeId,
 	                                              String orgnizationId,
 	                                              String channelId,
 	                                              String driverTypeId,
 	                                              String allianceId,
 	                                              String motorcardId,
 	                                              String hotelId,
 	                                              String driverId,
 	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
 	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize,
 	                                              HttpServletRequest request,
 	                                              HttpServletResponse response){
 	        logger.info("【运营管理-统计分析】导出,完成订单详情列表数据:queryCompleteOrderData.json");
        try {
        	 Map<String, Object> paramMap = new HashMap<String, Object>();
        	 paramMap.put("queryDate", queryDate);//查询日期
        	 paramMap.put("cityId", cityId);//下单城市ID
        	 paramMap.put("productId", productId);//产品类型ID
        	 paramMap.put("bindVehicleTypeId", bindVehicleTypeId);//绑定车型ID
        	 paramMap.put("serviceVehicleTypeId", serviceVehicleTypeId);//服务车型ID
        	 paramMap.put("orderTypeId", orderTypeId);//订单类别ID
        	 paramMap.put("orgnizationId", orgnizationId);//机构ID
        	 paramMap.put("channelId", channelId);//渠道ID
        	 paramMap.put("driverTypeId", driverTypeId);//司机类型ID
        	 paramMap.put("allianceId", allianceId);//加盟商ID
        	 paramMap.put("motorcardId", motorcardId);//车队ID
        	 paramMap.put("hotelId", hotelId);//酒店ID
        	 paramMap.put("driverId", driverId);//司机ID
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
 	        
 	       //调用接口
	       // Map<String,Object>  pageMap = completeOrderService.queryForPageObject(paramMap);
	      //  List<CompleteOrderDTO> rows = (List<CompleteOrderDTO>)pageMap.get("list");
	        
            @SuppressWarnings("deprecation")
            Workbook wb =  null ; //completeOrderService.exportExcelCompleteOrder(rows,request.getRealPath("/")+File.separator+"template"+File.separator+"completeOrder_info.xlsx");
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