package com.zhuanche.controller.statisticalAnalysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.rentcar.CancelOrderDTO;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.CommonStringUtils;
import com.zhuanche.util.MobileOverlayUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhuanche.common.enums.MenuEnum.CANCEL_ORDER_LIST;
import static com.zhuanche.common.enums.MenuEnum.CANCEL_ORDER_LIST_EXPORT;


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
		    * @param queryDate	查询日期
			* @param driverCityId	司机所属城市ID
			* @param allianceId	加盟商ID
			* @param motorcadeId	车队ID
			* @param driverTypeId	司机类型ID
			* @param channelId	下单渠道ID
			* @param orderVehicleTypeId	预约车型ID
			* @param productTypeId	产品类型ID
			* @param cancelDurationTypeId	取消时长分类ID
			* @param cancelTypeId	取消类型ID
			* @param pageNo	页号
			* @param pageSize	每页记录数
		    * @return
		  */
	    @ResponseBody
	    @RequestMapping(value = "/queryCancelOrderData", method = { RequestMethod.POST,RequestMethod.GET })
		@RequiresPermissions(value = { "CancelOrderDetail_look" } )
		@RequestFunction(menu = CANCEL_ORDER_LIST)
	    public AjaxResponse queryCancelOrderData(
	    										  Long driverCityId,
	    										  String allianceId,
	                                              String motorcadeId,
	                                              String driverTypeId,
	                                              String channelId,
	                                              String orderVehicleTypeId,
	                                              String productTypeId,
	                                              String cancelDurationTypeId,
	                                              String cancelTypeId,
	                                              @Verify(param = "queryDate",rule = "required") String queryDate,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                              ){
	        logger.info("【运营管理-统计分析】取消订单列表数据:queryCancelOrderData");
	        Map<String, Object> paramMap = new HashMap<String, Object>();
	        if(null!=driverCityId){
	        	 paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	        }
	        if(StringUtil.isNotEmpty(allianceId)){
	        	paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(StringUtil.isNotEmpty(motorcadeId)){
	        	paramMap.put("motorcadeId", motorcadeId);//车队ID
	        }
	        if(StringUtil.isNotEmpty(driverTypeId)){
	        	paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        }
	        if(StringUtil.isNotEmpty(channelId)){
	        	paramMap.put("channelId", channelId);//下单渠道ID
	        }
	        if(StringUtil.isNotEmpty(orderVehicleTypeId)){
	        	paramMap.put("orderVehicleTypeId", orderVehicleTypeId);//预约车型ID
	        }
	        if(StringUtil.isNotEmpty(productTypeId)){
	        	paramMap.put("productTypeId", productTypeId);//产品类型ID
	        }
	        if(StringUtil.isNotEmpty(cancelDurationTypeId)){
	        	paramMap.put("cancelDurationTypeId", cancelDurationTypeId);//取消时长分类ID
	        }
	        if(StringUtil.isNotEmpty(cancelTypeId)){
	        	paramMap.put("cancelTypeId", cancelTypeId);//取消类型
	        }
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
			if (result.isSuccess()){
				JSONObject data =  (JSONObject) result.getData();
				JSONArray recordList = data.getJSONArray(Constants.RECORD_LIST);
				recordList.forEach(o -> {
					JSONObject element = (JSONObject) o;
					element.put(Constants.BOOKING_USER_PHONE, CommonStringUtils.protectPhoneInfo(element.getString(Constants.BOOKING_USER_PHONE)));
					element.put(Constants.RIDER_PHONE, CommonStringUtils.protectPhoneInfo(element.getString(Constants.RIDER_PHONE)));
					element.put(Constants.DRIVER_PHONE, MobileOverlayUtil.doOverlayPhone(element.getString(Constants.DRIVER_PHONE)));
				});
			}
	        return result;
	    }
	    
	    /**
		    * 导出取消订单列表
		    * @param queryDate	查询日期
			* @param driverCityId	司机所属城市ID
			* @param allianceId	加盟商ID
			* @param motorcadeId	车队ID
			* @param driverTypeId	司机类型ID
			* @param channelId	下单渠道ID
			* @param orderVehicleTypeId	预约车型ID
			* @param productTypeId	产品类型ID
			* @param cancelDurationTypeId	取消时长分类ID
		    * @return
		  */
	@ResponseBody
   	@RequestMapping(value = "/exportCancelOrderDataBak", method = { RequestMethod.POST,RequestMethod.GET })
	public AjaxResponse exportCancelOrderDataBak(
											Long driverCityId,
											String allianceId,
								            String motorcadeId,
								            String driverTypeId,
								            String channelId,
								            String orderVehicleTypeId,
								            String productTypeId,
								            String cancelDurationTypeId,
								            String cancelTypeId,
								            @Verify(param = "queryDate",rule = "required") String queryDate,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
	        logger.info("【运营管理-统计分析】导出,导出取消订单列表数据:exportCancelOrderData");
       try {
    	    Map<String, Object> paramMap = new HashMap<String, Object>();
	        if(null!=driverCityId){
	        	 paramMap.put("driverCityId", driverCityId);//司机所属城市ID
	        }
	        if(StringUtil.isNotEmpty(allianceId)){
	        	paramMap.put("allianceId", allianceId);//加盟商ID
	        }
	        if(StringUtil.isNotEmpty(motorcadeId)){
	        	paramMap.put("motorcadeId", motorcadeId);//车队ID
	        }
	        if(StringUtil.isNotEmpty(driverTypeId)){
	        	paramMap.put("driverTypeId", driverTypeId);//司机类型ID
	        }
	        if(StringUtil.isNotEmpty(channelId)){
	        	paramMap.put("channelId", channelId);//下单渠道ID
	        }
	        if(StringUtil.isNotEmpty(orderVehicleTypeId)){
	        	paramMap.put("orderVehicleTypeId", orderVehicleTypeId);//预约车型ID
	        }
	        if(StringUtil.isNotEmpty(productTypeId)){
	        	paramMap.put("productTypeId", productTypeId);//产品类型ID
	        }
	        if(StringUtil.isNotEmpty(cancelDurationTypeId)){
	        	paramMap.put("cancelDurationTypeId", cancelDurationTypeId);//取消时长分类ID
	        }
	        if(StringUtil.isNotEmpty(cancelTypeId)){
	        	paramMap.put("cancelTypeId", cancelTypeId);//取消类型
	        }
	        paramMap.put("queryDate", queryDate);//查询日期
	        
	       // 数据权限设置
	        paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,driverCityId,allianceId,motorcadeId);
		      if(paramMap==null){
		    	  return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
		      }

           //页号
           paramMap.put("pageNo", 1);
           //每页记录数
           paramMap.put("pageSize", Integer.MAX_VALUE);

           String jsonString = JSON.toJSONString(paramMap);
           logger.info("【运营管理-取消订单】导出,取消订单详情请求参数----{}",jsonString);

           List<CancelOrderDTO> completeOrderDTOList =  statisticalAnalysisService.queryCancelOrderDataList(saasBigdataApiUrl+"/cancelOrderDetail/queryList",paramMap, CancelOrderDTO.class);
           String[] headers = new String[]{"下单城市", "创建时间", "预计乘车时间", "司机出发时间", "取消时间",
                   "取消时长", "订单号", "司机ID", "司机类型", "加盟商名称", "司机姓名", "订车人ID", "预估金额",
                   "下单渠道名称", "产品类型名称", "预定车型名称"};
           String fileName = "取消订单详情Excel";
           statisticalAnalysisService.exportExceleCancelOrder(completeOrderDTOList,response,headers,fileName);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return AjaxResponse.success("成功");
   }


    /**
     * 导出取消订单列表
     * @param queryDate	查询日期
     * @param driverCityId	司机所属城市ID
     * @param allianceId	加盟商ID
     * @param motorcadeId	车队ID
     * @param driverTypeId	司机类型ID
     * @param channelId	下单渠道ID
     * @param orderVehicleTypeId	预约车型ID
     * @param productTypeId	产品类型ID
     * @param cancelDurationTypeId	取消时长分类ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportCancelOrderData", method = { RequestMethod.POST,RequestMethod.GET })
	@RequiresPermissions(value = { "CancelOrderDetail_export" } )
	@RequestFunction(menu = CANCEL_ORDER_LIST_EXPORT)
    public AjaxResponse exportCancelOrderData(
            Long driverCityId,
            String allianceId,
            String motorcadeId,
            String driverTypeId,
            String channelId,
            String orderVehicleTypeId,
            String productTypeId,
            String cancelDurationTypeId,
            String cancelTypeId,
            @Verify(param = "queryDate",rule = "required") String queryDate,
            HttpServletRequest request,
            HttpServletResponse response){
        logger.info("【运营管理-统计分析】导出,导出取消订单列表数据:exportCancelOrderData");
        try {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            if(null!=driverCityId){
                paramMap.put("driverCityId", driverCityId);//司机所属城市ID
            }
            if(StringUtil.isNotEmpty(allianceId)){
                paramMap.put("allianceId", allianceId);//加盟商ID
            }
            if(StringUtil.isNotEmpty(motorcadeId)){
                paramMap.put("motorcadeId", motorcadeId);//车队ID
            }
            if(StringUtil.isNotEmpty(driverTypeId)){
                paramMap.put("driverTypeId", driverTypeId);//司机类型ID
            }
            if(StringUtil.isNotEmpty(channelId)){
                paramMap.put("channelId", channelId);//下单渠道ID
            }
            if(StringUtil.isNotEmpty(orderVehicleTypeId)){
                paramMap.put("orderVehicleTypeId", orderVehicleTypeId);//预约车型ID
            }
            if(StringUtil.isNotEmpty(productTypeId)){
                paramMap.put("productTypeId", productTypeId);//产品类型ID
            }
            if(StringUtil.isNotEmpty(cancelDurationTypeId)){
                paramMap.put("cancelDurationTypeId", cancelDurationTypeId);//取消时长分类ID
            }
            if(StringUtil.isNotEmpty(cancelTypeId)){
                paramMap.put("cancelTypeId", cancelTypeId);//取消类型
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResponse.success("成功");
    }
	
	
	
}