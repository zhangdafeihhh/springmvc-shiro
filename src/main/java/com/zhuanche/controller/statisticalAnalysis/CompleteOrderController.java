package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.util.MobileOverlayUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.CommonStringUtils;

import static com.zhuanche.common.enums.MenuEnum.COMPLETE_ORDER_LIST;
import static com.zhuanche.common.enums.MenuEnum.COMPLETE_ORDER_LIST_EXPORT;

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

	 @Autowired
     private OrderService orderService;
	 
	 	/**
	     * 查询完成订单列表
	     * @return
	     */
		@ResponseBody
	    @RequestMapping(value = "/queryCompleteOrdeData", method = { RequestMethod.POST,RequestMethod.GET })
		@RequiresPermissions(value = { "CompleteOrderDetail_look" } )
        @RequestFunction(menu = COMPLETE_ORDER_LIST)
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
	                                              Integer supplierId,
	                                              Integer distributorId,
	                                              Integer isReductDiscount,
                                                  Integer isPlatformAmount,
	                                              Integer couponSettleAmout,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize){
	        logger.info("【运营管理-统计分析】完成订单列表数据:queryCompleteOrderData");
            Map<String, Object> paramMap = this.queryMap(queryDate,cityId,productId,bindVehicleTypeId,serviceVehicleTypeId,orderTypeId,orgnizationId,
                    channelId,driverTypeId,allianceId,motorcardId,hotelId,driverId,supplierId,distributorId,isReductDiscount,isPlatformAmount,couponSettleAmout);

            /** 数据权限设置*/
            paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap, cityId, allianceId, motorcardId);


            /**如果选择了城际拼车并且选择了供应商*/
            if(StringUtils.isNotEmpty(productId) && Constants.INTEGER_SERVICE_TYPE.toString().equals(productId)){
                if(supplierId != null && supplierId > 0){
                    paramMap.remove("visibleCityIds");
                }
            }
            if (paramMap == null) {
                return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
            }
            if (null != pageNo && pageNo > 0){
                /**页号*/
                paramMap.put("pageNo", pageNo);
            }
	        if(null != pageSize && pageSize > 0) {
                /**每页记录数*/
                paramMap.put("pageSize", pageSize);
            }

		    String jsonString = JSON.toJSONString(paramMap);
		    logger.info("【运营管理-统计分析】完成订单列表请求参数--"+jsonString);
		    /**从大数据仓库获取统计数据*/
            logger.info("调用请求域名:" + (saasBigdataApiUrl+"/completeOrderDetail/queryList"));
		    AjaxResponse result = statisticalAnalysisService.parseResult(saasBigdataApiUrl+"/completeOrderDetail/queryList",paramMap);
            if (result.isSuccess()){
                JSONObject data =  (JSONObject) result.getData();
                JSONArray recordList = data.getJSONArray(Constants.RECORD_LIST);
                recordList.forEach(o -> {
                    JSONObject element = (JSONObject) o;
                    element.put(Constants.BOOKING_USER_PHONE, CommonStringUtils.protectPhoneInfo(element.getString(Constants.BOOKING_USER_PHONE)));
                    element.put(Constants.RIDER_PHONE, CommonStringUtils.protectPhoneInfo(element.getString(Constants.RIDER_PHONE)));
                    element.put(Constants.DRIVER_PHONE, MobileOverlayUtil.doOverlayPhone(element.getString(Constants.DRIVER_PHONE)));
                    element.put(Constants.COUPON_SETTLE_AMOUNT,orderService.couponSettleAmout(element.getString(Constants.ORDERNO)));
                });
            }
		    return result;
	  }


	  private Map<String, Object> queryMap(String queryDate,
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
                                           Integer supplierId,
                                           Integer distributorId,
                                           Integer isReductDiscount,
                                           Integer isPlatformAmount,
                                           Integer couponSettleAmout){
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
          if(distributorId != null){
              paramMap.put("distributorId",distributorId);
          }

          if(supplierId != null){
              paramMap.put("allianceId",supplierId);
          }

          if(isReductDiscount != null){
              if(Constants.IS_REDUCT_DISCOUNT_FALSE.equals(isReductDiscount) || Constants.IS_REDUCT_DISCOUNT_TRUE.equals(isReductDiscount) ){
                  paramMap.put("isReductDiscount",isReductDiscount);
              }
          }

          if(isPlatformAmount != null){
              if(Constants.IS_REDUCT_DISCOUNT_FALSE.equals(isPlatformAmount) || Constants.IS_REDUCT_DISCOUNT_TRUE.equals(isPlatformAmount) ){
                  paramMap.put("isPlatformAmount",isPlatformAmount);
              }
          }

          if(couponSettleAmout != null){
              if(Constants.IS_COUPLE_SETTLE_AMOUNT.equals(couponSettleAmout)){
                  paramMap.put("couponSettleAmout",couponSettleAmout);
              }
          }

          return paramMap;
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
     @RequestFunction(menu = COMPLETE_ORDER_LIST_EXPORT)
 	 public void exportCompleteOrderData(@Verify(param = "queryDate",rule = "required") String queryDate,
                                         @Verify(param = "cityId",rule = "required") Long cityId,
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
                                         Integer supplierId,
                                         Integer distributorId,
                                         Integer isReductDiscount,
                                         Integer isPlatformAmount,
                                         Integer couponSettleAmout,
                                         HttpServletRequest request,
                                         HttpServletResponse response){
    	    try{
                logger.info("【订单管理-完成订单详情】导出,完成订单详情列表数据:queryCompleteOrderData.json");
                Map<String, Object> paramMap = exportMap(queryDate,cityId,productId,bindVehicleTypeId,serviceVehicleTypeId,orderTypeId,
                        orgnizationId,channelId,driverTypeId,allianceId,motorcardId,hotelId,driverId,supplierId,distributorId,isReductDiscount,
                        isPlatformAmount,couponSettleAmout);
		  		paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,allianceId,motorcardId);
				if(paramMap==null){
					return;
				}
                /**如果选择了城际拼车并且选择了供应商*/
                if(StringUtils.isNotEmpty(productId) && Constants.INTEGER_SERVICE_TYPE.toString().equals(productId)){
                    if(supplierId != null && supplierId > 0){
                        paramMap.remove("visibleCityIds");
                    }
                }
	          String jsonString = JSON.toJSONString(paramMap);
	          
			  statisticalAnalysisService.exportCsvFromToPage(
					response,
					jsonString,
					saasBigdataApiUrl+"/completeOrderDetail/download",
					new String("完成订单详情".getBytes("gb2312"), "iso8859-1"),
					request.getRealPath("/")+File.separator+"template"+File.separator+"completeOrderDetail_info.csv");
			  
            }catch (Exception e){
    	        logger.error("导出完成订单详情error:{}",e);
            }
    }


    public Map<String,Object> exportMap(String queryDate,
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
                                     Integer supplierId,
                                     Integer distributorId,
                                     Integer isReductDiscount,
                                     Integer isPlatformAmount,
                                     Integer couponSettleAmout){

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
        if(distributorId != null){
            paramMap.put("distributorId",distributorId);
        }
        if(supplierId != null){
            //匹配大数据那边的字段
            paramMap.put("allianceId",supplierId);
        }

        if(isReductDiscount != null){
            if(Constants.IS_REDUCT_DISCOUNT_FALSE.equals(isReductDiscount) || Constants.IS_REDUCT_DISCOUNT_TRUE.equals(isReductDiscount) ){
                paramMap.put("isReductDiscount",isReductDiscount);
            }
        }

        if(isPlatformAmount != null){
            if(Constants.IS_REDUCT_DISCOUNT_FALSE.equals(isPlatformAmount) || Constants.IS_REDUCT_DISCOUNT_TRUE.equals(isPlatformAmount) ){
                paramMap.put("isPlatformAmount",isPlatformAmount);
            }
        }

        if(couponSettleAmout != null){
            if(Constants.IS_COUPLE_SETTLE_AMOUNT.equals(couponSettleAmout)){
                paramMap.put("couponSettleAmout",couponSettleAmout);
            }
        }

        return paramMap;
    }
}