package com.zhuanche.controller.statisticalAnalysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.rentcar.CompleteOrderDTO;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.CommonStringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 
	 @Value("${bigdata.saas.data.url}")
	 String  saasBigdataApiUrl;
	 
	 @Autowired
	 private StatisticalAnalysisService statisticalAnalysisService;
	 
	 	/**
	     * 查询完成订单列表
	     * @return
	     */
		@ResponseBody
	    @RequestMapping(value = "/queryCompleteOrdeData", method = { RequestMethod.POST,RequestMethod.GET })
		@RequiresPermissions(value = { "CompleteOrderDetail_look" } )
	    public AjaxResponse queryCompleteOrderData(
	    										  @Verify(param = "queryDate",rule = "required") String queryDate,
	    										  Long cityId,
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
            paramMap.put("queryDate", queryDate);//查询日期
            if (null != cityId) {
                paramMap.put("cityId", cityId);//下单城市ID
            }
            if (StringUtil.isNotEmpty(productId)) {
                paramMap.put("productId", productId);//产品类型ID
            }
            if (StringUtil.isNotEmpty(bindVehicleTypeId)) {
                paramMap.put("bindVehicleTypeId", bindVehicleTypeId);//绑定车型ID
            }
            if (StringUtil.isNotEmpty(serviceVehicleTypeId)) {
                paramMap.put("serviceVehicleTypeId", serviceVehicleTypeId);//服务车型ID
            }
            if (StringUtil.isNotEmpty(orderTypeId)) {
                paramMap.put("orderTypeId", orderTypeId);//订单类别ID
            }
            if (StringUtil.isNotEmpty(orgnizationId)) {
                paramMap.put("orgnizationId", orgnizationId);//机构ID
            }
            if (StringUtil.isNotEmpty(channelId)) {
                paramMap.put("channelId", channelId);//渠道ID
            }
            if (StringUtil.isNotEmpty(driverTypeId)) {
                paramMap.put("driverTypeId", driverTypeId);//司机类型ID
            }
            if (StringUtil.isNotEmpty(allianceId)) {
                paramMap.put("allianceId", allianceId);//加盟商ID
            }
            if (StringUtil.isNotEmpty(motorcardId)) {
                paramMap.put("motorcardId", motorcardId);//车队ID
            }
            if (StringUtil.isNotEmpty(hotelId)) {
                paramMap.put("hotelId", hotelId);//酒店ID
            }
            if (StringUtil.isNotEmpty(driverId)) {
                paramMap.put("driverId", driverId);//司机ID
            }
            // 数据权限设置
            paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap, cityId, allianceId, motorcardId);
            if (paramMap == null) {
                return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
            }
            if (null != pageNo && pageNo > 0){
                paramMap.put("pageNo", pageNo);//页号
            }
	        if(null != pageSize && pageSize > 0) {
                paramMap.put("pageSize", pageSize);//每页记录数
            }

		    String jsonString = JSON.toJSONString(paramMap);
		    logger.info("【运营管理-统计分析】完成订单列表请求参数--"+jsonString);
		    //从大数据仓库获取统计数据
		    AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/completeOrderDetail/queryList",paramMap);
            if (result.isSuccess()){
                JSONObject data =  (JSONObject) result.getData();
                JSONArray recordList = data.getJSONArray(Constants.RECORD_LIST);
                recordList.forEach(o -> {
                    JSONObject element = (JSONObject) o;
                    element.put(Constants.BOOKING_USER_PHONE, CommonStringUtils.protectPhoneInfo(element.getString(Constants.BOOKING_USER_PHONE)));
                    element.put(Constants.RIDER_PHONE, CommonStringUtils.protectPhoneInfo(element.getString(Constants.RIDER_PHONE)));
                });
            }
		    return result;
	  }

    /**
     * 导出完成订单列表
     * @param queryDate 查询日期
     * @param cityId 下单城市ID
     * @param productId 产品类型ID
     * @param bindVehicleTypeId 绑定车型ID
     * @param serviceVehicleTypeId 服务车型ID
     * @param orderTypeId 订单类别ID
     * @param orgnizationId 机构ID
     * @param channelId 渠道ID
     * @param driverTypeId 司机类型ID
     * @param allianceId 加盟商ID
     * @param motorcardId 车队ID
     * @param hotelId 酒店ID
     * @param driverId 司机ID
     * @param response
     */
     @RequestMapping(value = "/exportCompleteOrderData", method = RequestMethod.GET)
 	 @RequiresPermissions(value = { "CompleteOrderDetail_export" } )
 	 public void exportCompleteOrderData(@Verify(param = "queryDate",rule = "required") String queryDate,
                                         @Verify(param = "cityId",rule = "required") Long cityId,
                                         String productId,
                                         String bindVehicleTypeId,
                                         String serviceVehicleTypeId,
                                         String orderTypeId,
                                         String orgnizationId,
                                         String channelId,
                                         String driverTypeId,
                                         @Verify(param = "allianceId",rule = "required")String allianceId,
                                         String motorcardId,
                                         String hotelId,
                                         String driverId,
                                         HttpServletResponse response){
    	    try{
                logger.info("【订单管理-完成订单详情】导出,完成订单详情列表数据:queryCompleteOrderData.json");
                Map<String, Object> paramMap = new HashMap<String, Object>();
                //查询日期
                paramMap.put("queryDate", queryDate);
                //下单城市ID
                paramMap.put("cityId", cityId);
                if(StringUtil.isNotEmpty(productId)){
                    paramMap.put("productId", productId);//产品类型ID
                }
                if(StringUtil.isNotEmpty(bindVehicleTypeId)){
                    paramMap.put("bindVehicleTypeId", bindVehicleTypeId);//绑定车型ID
                }
                if(StringUtil.isNotEmpty(serviceVehicleTypeId)){
                    paramMap.put("serviceVehicleTypeId", serviceVehicleTypeId);//服务车型ID
                }
                if(StringUtil.isNotEmpty(orderTypeId)){
                    paramMap.put("orderTypeId", orderTypeId);//订单类别ID
                }
                if(StringUtil.isNotEmpty(orgnizationId)){
                    paramMap.put("orgnizationId", orgnizationId);//机构ID
                }
                if(StringUtil.isNotEmpty(channelId)){
                    paramMap.put("channelId", channelId);//渠道ID
                }
                if(StringUtil.isNotEmpty(driverTypeId)){
                    paramMap.put("driverTypeId", driverTypeId);//司机类型ID
                }
                if(StringUtil.isNotEmpty(allianceId)){
                    paramMap.put("allianceId", allianceId);//加盟商ID
                }
                if(StringUtil.isNotEmpty(motorcardId)){
                    paramMap.put("motorcardId", motorcardId);//车队ID
                }
                if(StringUtil.isNotEmpty(hotelId)){
                    paramMap.put("hotelId", hotelId);//酒店ID
                }
                if(StringUtil.isNotEmpty(driverId)){
                    paramMap.put("driverId", driverId);//司机ID
                }

                //页号
                paramMap.put("pageNo", 1);
                //每页记录数
                paramMap.put("pageSize", Integer.MAX_VALUE);
                paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap, cityId, allianceId, motorcardId);
                String jsonString = JSON.toJSONString(paramMap);
                logger.info("【订单管理-统计分析】导出,完成订单详情请求参数----{}",jsonString);
                List<CompleteOrderDTO> completeOrderDTOList =  statisticalAnalysisService.queryCompleteOrderDataList(saasBigdataApiUrl+"/completeOrderDetail/queryList",paramMap);
                statisticalAnalysisService.exportExceleCompleteOrder(completeOrderDTOList,response);
            }catch (Exception e){
    	        logger.error("导出完成订单详情error:{}",e);
            }
    }
}