package com.zhuanche.controller.statisticalAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CompleteOrderDTO;
import com.zhuanche.dto.rentcar.CompleteOrderDetailsDTO;
import com.zhuanche.serv.statisticalAnalysis.CompleteOrderService;
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
	 
	 @Autowired
	 private CompleteOrderService completeOrderService;
	 
	 /**
	     * 查询完成订单列表
	     * @param cityId    城市
	     * @param supplierId    供应商
	     * @param carGroupId    服务类型
	     * @param outageSource  停运来源
	     * @param driverName    司机姓名
	     * @param driverPhone   手机号
	     * @param startDateBegin    停运开始日期
	     * @param startDateEnd      停运结束日期
	     * @param removeStatus      解除状态
	     * @return
	     */
	    @RequestMapping(value = "/queryCompleteOrdeData", method = { RequestMethod.POST,RequestMethod.GET })
	    public AjaxResponse queryCompleteOrderData(
	    										  @Verify(param = "queryDate",rule = "required") String queryDate,
	    										  Integer cityId,
	    										  String productId,
	                                              String bindVehicleTypeId,
	                                              String serviceVehicleTypeId,
	                                              String orderTypeId,
	                                              String orgnizationId,
	                                              String channelId,
	                                              Integer driverTypeId,
	                                              String allianceId,
	                                              String motorcardId,
	                                              String hotelId,
	                                              String driverId,
	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize,
	                                              @Verify(param = "visibleAllianceIds",rule = "required") String visibleAllianceIds,
	                                              @Verify(param = "visibleMotorcardIds",rule = "required") String visibleMotorcardIds,
	                                              @Verify(param = "visibleCityIds",rule = "required") String visibleCityIds){
	        logger.info("【运营管理-统计分析】完成订单列表数据:queryCompleteOrderData.json");
	        
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
	        //权限
	        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
	        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
	        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(),",");
	        params.put("visibleAllianceIds", suppliers); // 可见加盟商ID
	        params.put("visibleMotorcardIds", teamIds); // 可见车队ID
	        params.put("visibleCityIds", cities); //可见城市ID
	        if(null != pageNo && pageNo > 0)
	        	params.put("pageNo", pageNo);//页号
	        if(null != pageSize && pageSize > 0)
	        	params.put("pageSize", pageSize);//每页记录数
	        
	        
	        List<CompleteOrderDTO> rows = Lists.newArrayList();
	        //调用接口
	        Map<String,Object>  pageMap = completeOrderService.queryForPageObject(params);
	        //查数量
	        Integer total = (Integer)pageMap.get("total");
	        if(total==0){
	            PageDTO result = new PageDTO(pageNo, pageSize, 0, rows);
	            return AjaxResponse.success(result);
	        }
	        rows = (List<CompleteOrderDTO>)pageMap.get("list");
	        return AjaxResponse.success(new PageDTO(pageNo, pageSize, total, BeanUtil.copyList(rows, CompleteOrderDTO.class)));
	    }
	 

	    /**
	     *
	     *导出司机停运操作
	     * @return
	     */
    	@RequestMapping(value = "/exportCompleteOrderData", method = { RequestMethod.POST,RequestMethod.GET })
 	    public void exportCompleteOrderData(
 	    										  @Verify(param = "queryDate",rule = "required") String queryDate,
 	    										  Integer cityId,
 	    										  String productId,
 	                                              String bindVehicleTypeId,
 	                                              String serviceVehicleTypeId,
 	                                              String orderTypeId,
 	                                              String orgnizationId,
 	                                              String channelId,
 	                                              Integer driverTypeId,
 	                                              String allianceId,
 	                                              String motorcardId,
 	                                              String hotelId,
 	                                              String driverId,
 	                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
 	                                              @Verify(param = "pageSize",rule = "required") Integer pageSize,
 	                                              @Verify(param = "visibleAllianceIds",rule = "required") String visibleAllianceIds,
 	                                              @Verify(param = "visibleMotorcardIds",rule = "required") String visibleMotorcardIds,
 	                                              @Verify(param = "visibleCityIds",rule = "required") String visibleCityIds,
 	                                              HttpServletRequest request,
 	                                              HttpServletResponse response){
 	        logger.info("【运营管理-统计分析】导出,完成订单详情列表数据:queryCompleteOrderData.json");
        try {
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
 	        //权限
 	        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
 	        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
 	        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(),",");
 	        params.put("visibleAllianceIds", suppliers); // 可见加盟商ID
 	        params.put("visibleMotorcardIds", teamIds); // 可见车队ID
 	        params.put("visibleCityIds", cities); //可见城市ID
 	        
 	       //调用接口
	        Map<String,Object>  pageMap = completeOrderService.queryForPageObject(params);
	        List<CompleteOrderDTO> rows = (List<CompleteOrderDTO>)pageMap.get("list");
	        
            @SuppressWarnings("deprecation")
            Workbook wb = completeOrderService.exportExcelCompleteOrder(rows,request.getRealPath("/")+File.separator+"template"+File.separator+"completeOrder_info.xlsx");
            exportExcelFromTemplet(request, response, wb, new String("完成订单详情".getBytes("gb2312"), "iso8859-1"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	    
	    	 /**
		     * 查询完成订单详情
		     * @param cityId    城市
		     * @param supplierId    供应商
		     * @param carGroupId    服务类型
		     * @param outageSource  停运来源
		     * @param driverName    司机姓名
		     * @param driverPhone   手机号
		     * @param startDateBegin    停运开始日期
		     * @param startDateEnd      停运结束日期
		     * @param removeStatus      解除状态
		     * @return
		     */
		    @RequestMapping(value = "/queryCompleteOrderDetailsData", method = { RequestMethod.POST,RequestMethod.GET })
		    @ResponseBody
		    public CompleteOrderDetailsDTO queryCompleteOrderDetailsData(
		    										  @Verify(param = "orderNo",rule = "required") String orderNo,
		    										  @Verify(param = "queryDate",rule = "required") String queryDate,
		    										  Integer cityId,
		    										  String productId,
		                                              String bindVehicleTypeId,
		                                              String serviceVehicleTypeId,
		                                              String orderTypeId,
		                                              String orgnizationId,
		                                              String channelId,
		                                              Integer driverTypeId,
		                                              String allianceId,
		                                              String motorcardId,
		                                              String hotelId,
		                                              String driverId,
		                                              @Verify(param = "pageNo",rule = "required") Integer pageNo,
		                                              @Verify(param = "pageSize",rule = "required") Integer pageSize,
		                                              @Verify(param = "visibleAllianceIds",rule = "required") String visibleAllianceIds,
		                                              @Verify(param = "visibleMotorcardIds",rule = "required") String visibleMotorcardIds,
		                                              @Verify(param = "visibleCityIds",rule = "required") String visibleCityIds){
		        logger.info("【运营管理-统计分析】完成订单详情数据:queryCompleteOrderDetailsData.json");
		        
		        Map<String, Object> params = new HashMap<String, Object>();
		        params.put("orderNo", orderNo);//订单号
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
		        //权限
		        String cities = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getCityIds(), ",");
		        String suppliers = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getSupplierIds(),",");
		        String teamIds = StringUtils.join(WebSessionUtil.getCurrentLoginUser().getTeamIds(),",");
		        params.put("visibleAllianceIds", suppliers); // 可见加盟商ID
		        params.put("visibleMotorcardIds", teamIds); // 可见车队ID
		        params.put("visibleCityIds", cities); //可见城市ID
		        if(null != pageNo && pageNo > 0)
		        	params.put("pageNo", pageNo);//页号
		        if(null != pageSize && pageSize > 0)
		        	params.put("pageSize", pageSize);//每页记录数
		        
		        CompleteOrderDetailsDTO completeOrderDetails = new CompleteOrderDetailsDTO();
		        List<CompleteOrderDetailsDTO> rows = Lists.newArrayList();
		        //调用接口
		        Map<String,Object>  pageMap = completeOrderService.queryForPageObject(params);
		        rows = (List<CompleteOrderDetailsDTO>)pageMap.get("list");
		        if(rows!=null && rows.size()>0){
		        	 completeOrderDetails = rows.get(0);
		        }
		        return completeOrderDetails;
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
	    
	    
}