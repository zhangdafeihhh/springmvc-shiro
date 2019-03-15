package com.zhuanche.controller.rentcar;

import static com.zhuanche.common.enums.MenuEnum.MAIN_ORDER_DETAIL;
import static com.zhuanche.common.enums.MenuEnum.ORDER_DETAIL;
import static com.zhuanche.common.enums.MenuEnum.ORDER_LIST;
import static com.zhuanche.common.enums.MenuEnum.ORDER_LIST_EXPORT;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mapper.rentcar.CarBizCustomerMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarFactOrderExMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.common.util.TimeUtils;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.DriverCostDetailVO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDTO;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.driverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizCustomer;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarGroupEntity;
import com.zhuanche.entity.rentcar.ServiceEntity;
import com.zhuanche.serv.order.DriverFeeDetailService;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.CommonStringUtils;
import com.zhuanche.util.excel.CsvUtils;


/**
 * ClassName: OrderController 
 * date: 2018年9月10日 上午11:19:45 
 * @author jiadongdong
 * @version
 * @since JDK 1.6 
 */
@Controller("orderController")
@RequestMapping(value = "/order")
public class OrderController{
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	 
	@Autowired
	private CarFactOrderInfoService carFactOrderInfoService;
	 
	@Autowired
	private StatisticalAnalysisService statisticalAnalysisService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private CarFactOrderExMapper carFactOrderExMapper;
	@Autowired
	private CarBizDriverInfoMapper carBizDriverInfoMapper;
	@Autowired
	private CarBizCustomerMapper carBizCustomerMapper;
	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;

	@Autowired
	private DriverFeeDetailService driverFeeDetailService;


	/**
	    * 查询订单 列表
	    * @return
	  */
//	输入【订单号】参数，无需搭配其他限定条件，可直接查询；
//			【订单状态】选择“已完成”，必须限定【下单日期】范围或【完成日期】范围，支持跨度31天；
//			【订单状态】选择其他，必须限定【下单日期】范围，支持跨度31天；

	/**
	 * 订单查询
	 *es 输出的wiki: http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047769
	 * 输出wiki:
	 * http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047320
	 * @param serviceId
	 * @param airportIdnot
	 * @param airportId
	 * @param carGroupId
	 * @param statusStr
	 * @param cityId
	 * @param supplierId
	 * @param teamId
	 * @param teamClassId
	 * @param bookingUserName
	 * @param bookingUserPhone
	 * @param driverPhone
	 * @param licensePlates
	 * @param orderNo
	 * @param orderType
	 * @param beginCreateDate	下单开始日期
	 * @param endCreateDate		下单结束日期
	 * @param beginCostEndDate	订单完成开始时间
	 * @param endCostEndDate     订单完成结束时间
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping(value = "/queryOrderList", method = { RequestMethod.POST,RequestMethod.GET })
	 @RequiresPermissions(value = { "OrderList_look" } )
	 @RequestFunction(menu = ORDER_LIST)
	 public AjaxResponse queryOrderList(
			 								   String serviceId,
			 								   String airportIdnot,
			 								   String airportId,
	 										   String carGroupId,
	 										   String statusStr,
	 										   Long cityId,
	 										   String supplierId,
	                                           String teamId,
	                                           String teamClassId,
	                                           String bookingUserName, 
	                                           String bookingUserPhone,
	                                           String driverPhone,
	                                           String licensePlates, 
	                                           String orderNo, 
	                                           String orderType,
	                                          String beginCreateDate,
	                                           String endCreateDate,
	                                            String beginCostEndDate,
	                                      		String endCostEndDate,
	                                           @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                           @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                           ){
	     logger.info("【运营管理-统计分析】查询订单 列表:queryOrderList");
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
	     String  transId =sdf.format(new Date());
	     if(StringUtils.isEmpty(orderNo)){
	     	//【订单状态】选择“已完成”，必须限定【下单日期】范围或【完成日期】范围，支持跨度31天；
	     	if(StringUtils.isNotEmpty(statusStr) && "50".equals(statusStr))
			{
				if(StringUtils.isEmpty(beginCreateDate) && StringUtils.isEmpty(endCreateDate) && StringUtils.isEmpty(beginCostEndDate) && StringUtils.isEmpty(endCostEndDate)){
					AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					ajaxResponse.setMsg("下单日期开始时间和结束时间或者是完成日期的开始时间和结束时间二者之一不能为空");
					return ajaxResponse;
				}else if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
					try {
						Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
						Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

						int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
						if(intervalDays > 31){
							AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							ajaxResponse.setMsg("下单日期开始时间和结束时间之间的跨度不能超过31天");
							return ajaxResponse;
						}

					} catch (ParseException e) {
						logger.error("订单查询，参数错误。beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
						AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						ajaxResponse.setMsg("下单开始时间或者结束时间参数错误");
						return ajaxResponse;
					}

				}else if( (StringUtils.isNotEmpty(beginCostEndDate) && StringUtils.isNotEmpty(endCostEndDate))  ){
					try {
						Date beginCostEndDateD = DateUtils.parseDate(beginCostEndDate,"yyyy-MM-dd");
						Date endCostEndDateD = DateUtils.parseDate(endCostEndDate,"yyyy-MM-dd");

						int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCostEndDateD,endCostEndDateD);
						if(intervalDays > 31){
							AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							ajaxResponse.setMsg("订单完成日期开始时间和结束时间之间的跨度不能超过31天");
							return ajaxResponse;
						}

					} catch (ParseException e) {
						logger.error("订单查询，参数错误。beginCostEndDate="+beginCostEndDate+";endCostEndDate="+endCostEndDate);
						AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						ajaxResponse.setMsg("订单完成日期开始时间和结束时间错误");
						return ajaxResponse;
					}

				}else {
					AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					ajaxResponse.setMsg("订单完成时间必填或者是订单下单时间必填");
					return ajaxResponse;
				}
			}else{
				if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
					try {
						Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
						Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

						int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
						if(intervalDays > 31){
							AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							ajaxResponse.setMsg("下单日期开始时间和结束时间之间的跨度不能超过31天");
							return ajaxResponse;
						}

					} catch (ParseException e) {
						logger.error("订单查询，参数错误。beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
						AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						ajaxResponse.setMsg("下单开始时间或者结束时间参数错误");
						return ajaxResponse;
					}

				}else {
					AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					ajaxResponse.setMsg("下单开始时间或者结束时间参数错误");
					return ajaxResponse;
				}
			}
		 }
	     
	     Map<String, Object> paramMap = new HashMap<String, Object>();
		 paramMap.put("serviceId", serviceId);// 
		 paramMap.put("airportId", airportId);//
		 paramMap.put("airportIdnot", airportIdnot);//
	     paramMap.put("carGroupId", carGroupId);// 
	     paramMap.put("statusBatch", statusStr);//
	     paramMap.put("cityId", cityId);//
	     paramMap.put("supplierId", supplierId);//
	     paramMap.put("teamId", teamId);// 
	     paramMap.put("teamClassId", teamClassId);//
	     paramMap.put("bookingUserName", bookingUserName);//
	     paramMap.put("bookingUserPhone", bookingUserPhone);//
	     paramMap.put("driverPhone", driverPhone);//
	     paramMap.put("licensePlates", licensePlates);// 
	     paramMap.put("orderNo", orderNo);//
	     paramMap.put("type", orderType);//
	     if(StringUtils.isNotEmpty(beginCreateDate)){
	    	 paramMap.put("beginCreateDate", beginCreateDate+" 00:00:00");// 
	     }
	     if(StringUtils.isNotEmpty(endCreateDate)){
	    	 paramMap.put("endCreateDate", endCreateDate+" 23:59:59");//
	     }
	     if(StringUtils.isNotEmpty(beginCostEndDate)){
	    	 paramMap.put("beginCostEndDate", beginCostEndDate+" 00:00:00");//
	     }
	     if(StringUtils.isNotEmpty(endCostEndDate)){
	    	 paramMap.put("endCostEndDate", endCostEndDate+" 23:59:59");//
	     }
	     paramMap.put("transId", transId );//
	     if(null != pageNo && pageNo > 0)
	     paramMap.put("pageNo", pageNo);//页号
	     if(null != pageSize && pageSize > 0)
	     paramMap.put("pageSize", pageSize);//每页记录数
        /* String cityIdBatch,//下单城市id批量 多个用逗号分割
         String supplierIdBatch,//供应商id 多个用逗号
         String teamIdBatch,//车队ID多个用逗号分割 类似or操作
         */
	     paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,supplierId,teamId);

	     if(paramMap.get("visibleAllianceIds")!=null){
	    	 logger.info("visibleAllianceIdstoString"+paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			 paramMap.put("supplierIdBatch", paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // 可见加盟商ID
		}
		if(paramMap.get("visibleMotorcadeIds")!=null){
	    	 logger.info("visibleMotorcadeIdstoString"+paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			paramMap.put("teamIdBatch", paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // 可见车队ID
		}
		if(paramMap.get("visibleCityIds")!=null){
			paramMap.put("cityIdBatch", paramMap.get("visibleCityIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); //可见城市ID
		}
		 // 从订单组取统计数据
	     AjaxResponse result = carFactOrderInfoService.queryOrderDataList(paramMap);
	     return result;
	 }


	/**
	 * 订单导出：
	 * es 输出的wiki: http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047769
	 * @param serviceId
	 * @param airportIdnot
	 * @param airportId
	 * @param carGroupId
	 * @param statusStr
	 * @param cityId
	 * @param supplierId
	 * @param teamId
	 * @param teamClassId
	 * @param bookingUserName
	 * @param bookingUserPhone
	 * @param driverPhone
	 * @param licensePlates
	 * @param orderNo
	 * @param orderType
	 * @param beginCreateDate
	 * @param endCreateDate
	 * @param beginCostEndDate
	 * @param endCostEndDate
	 * @param request
	 * @param response
	 * @return
	 */
	 @ResponseBody
	 @RequestMapping(value = "/exportOrderList", method = { RequestMethod.POST,RequestMethod.GET })
 	 @RequiresPermissions(value = { "OrderList_export" } )
	 @RequestFunction(menu = ORDER_LIST_EXPORT)
	 public String exportOrderList(
											   String serviceId,
											   String airportIdnot,
											   String airportId,
	 										   String carGroupId,
	 										   String statusStr,
	 										   Long cityId,
	 										   String supplierId,
	                                           String teamId,
	                                           String teamClassId,
	                                           String bookingUserName, 
	                                           String bookingUserPhone,
	                                           String driverPhone,
	                                           String licensePlates, 
	                                           String orderNo, 
	                                           String orderType,
	                                           String beginCreateDate,
	                                            String endCreateDate,
	                                          String beginCostEndDate,
	                                           String endCostEndDate,
	                                           HttpServletRequest request,HttpServletResponse response
	                                           ){
	     logger.info("【运营管理-统计分析】查询订单 列表:queryOrderList");
		 if(StringUtils.isEmpty(orderNo)){
			 //【订单状态】选择“已完成”，必须限定【下单日期】范围或【完成日期】范围，支持跨度31天；
			 if(StringUtils.isNotEmpty(statusStr) && "50".equals(statusStr))
			 {
				 if(StringUtils.isEmpty(beginCreateDate) && StringUtils.isEmpty(endCreateDate) && StringUtils.isEmpty(beginCostEndDate) && StringUtils.isEmpty(endCostEndDate)){
					 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					 ajaxResponse.setMsg("下单日期开始时间和结束时间或者是完成日期的开始时间和结束时间二者之一不能为空");
					 return JSON.toJSONString(ajaxResponse);
				 }else if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
					 try {
						 Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
						 Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

						 int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
						 if(intervalDays > 31){
							 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							 ajaxResponse.setMsg("下单日期开始时间和结束时间之间的跨度不能超过31天");
							 return JSON.toJSONString(ajaxResponse);
						 }

					 } catch (ParseException e) {
						 logger.error("订单查询，参数错误。beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
						 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						 ajaxResponse.setMsg("下单开始时间或者结束时间参数错误");
						 return JSON.toJSONString(ajaxResponse);
					 }

				 }else if( (StringUtils.isNotEmpty(beginCostEndDate) && StringUtils.isNotEmpty(endCostEndDate))  ){
					 try {
						 Date beginCostEndDateD = DateUtils.parseDate(beginCostEndDate,"yyyy-MM-dd");
						 Date endCostEndDateD = DateUtils.parseDate(endCostEndDate,"yyyy-MM-dd");

						 int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCostEndDateD,endCostEndDateD);
						 if(intervalDays > 31){
							 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							 ajaxResponse.setMsg("订单完成日期开始时间和结束时间之间的跨度不能超过31天");
							 return JSON.toJSONString(ajaxResponse);
						 }

					 } catch (ParseException e) {
						 logger.error("订单查询，参数错误。beginCostEndDate="+beginCostEndDate+";endCostEndDate="+endCostEndDate);
						 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						 ajaxResponse.setMsg("订单完成日期开始时间和结束时间错误");
						 return JSON.toJSONString(ajaxResponse);
					 }

				 }else {
					 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					 ajaxResponse.setMsg("订单完成时间必填或者是订单下单时间必填");
					 return JSON.toJSONString(ajaxResponse);
				 }
			 }else  if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
				 try {
					 Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
					 Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

					 int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
					 if(intervalDays > 31){
						 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						 ajaxResponse.setMsg("下单日期开始时间和结束时间之间的跨度不能超过31天");
						 return JSON.toJSONString(ajaxResponse);
					 }

				 } catch (ParseException e) {
					 logger.error("订单查询，参数错误。beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
					 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					 ajaxResponse.setMsg("下单开始时间或者结束时间参数错误");
					 return JSON.toJSONString(ajaxResponse);
				 }

			 }else {
				 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
				 ajaxResponse.setMsg("下单开始时间或者结束时间参数错误");
				 return JSON.toJSONString(ajaxResponse);
			 }
		 }
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
	     String  transId =sdf.format(new Date());
	     
	     Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("serviceId", serviceId);// 
		 paramMap.put("airportId", airportId);//
		 paramMap.put("airportIdnot", airportIdnot);//
	     paramMap.put("carGroupId", carGroupId);// 
	     paramMap.put("statusBatch", statusStr);//
	     paramMap.put("cityId", cityId);//
	     paramMap.put("supplierId", supplierId);//
	     paramMap.put("teamId", teamId);// 
	     paramMap.put("teamClassId", teamClassId);//
	     paramMap.put("bookingUserName", bookingUserName);//
	     paramMap.put("bookingUserPhone", bookingUserPhone);//
	     paramMap.put("driverPhone", driverPhone);//
	     paramMap.put("licensePlates", licensePlates);// 
	     paramMap.put("orderNo", orderNo);//
	     paramMap.put("type", orderType);//
	     if(StringUtils.isNotEmpty(beginCreateDate)){
	    	 paramMap.put("beginCreateDate", beginCreateDate+" 00:00:00");// 
	     }
	     if(StringUtils.isNotEmpty(endCreateDate)){
	    	 paramMap.put("endCreateDate", endCreateDate+" 23:59:59");//
	     }
	     if(StringUtils.isNotEmpty(beginCostEndDate)){
	    	 paramMap.put("beginCostEndDate", beginCostEndDate+" 00:00:00");//
	     }
	     if(StringUtils.isNotEmpty(endCostEndDate)){
	    	 paramMap.put("endCostEndDate", endCostEndDate+" 23:59:59");//
	     }
	     
	     paramMap.put("transId", transId );//
	     paramMap.put("pageNo", "1");//页号
	     paramMap.put("pageSize",CsvUtils.downPerSize);//每页记录数
		 long start = System.currentTimeMillis();
	    paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,supplierId,teamId);
	     if(paramMap.get("visibleAllianceIds")!=null){
	    	 logger.info("visibleAllianceIdstoString"+paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			 paramMap.put("supplierIdBatch", paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // 可见加盟商ID
		}
		if(paramMap.get("visibleMotorcadeIds")!=null){
	    	 logger.info("visibleMotorcadeIdstoString"+paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			paramMap.put("teamIdBatch", paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // 可见车队ID
		}
		if(paramMap.get("visibleCityIds")!=null){
			paramMap.put("cityIdBatch", paramMap.get("visibleCityIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); //可见城市ID
		}
			
		// 查询ES（性能优化：采用分页的方式进行检索并获取数据）

		 int code = -1;
		 AjaxResponse responseX = null;
		 int pageSize = CsvUtils.downPerSize;
		 paramMap.put("pageSize",pageSize);//每页记录数
		 List<String> csvDataList = new ArrayList<>();

		try {
			List<String> headerList = new ArrayList<>();
			headerList.add("订单号,订单指派方式,城市,服务类别,车型类别,订单类别,预订人,预订人手机,乘车人,乘车人手机,司机,司机手机,车牌号,供应商,乘车时长(分钟),乘车里程,金额,是否使用优惠券,优惠券支付（元）,下单时间,完成时间,实际上车地址,实际下车地址,订单状态,是否拼车,主订单号");


			String fileName = "订单列表"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //获得浏览器信息并转换为大写
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE浏览器和Edge浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //其他浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}
			CsvUtils entity = new CsvUtils();
			boolean isFirst = true;
			boolean isLast = false;
			boolean breakTag = false;
			responseX = carFactOrderInfoService.queryOrderDataList(paramMap);
			code = responseX.getCode();
			int total = 0;
			int totalPage = 0;
			if(code == 0) {
				JSONObject pageObj = (JSONObject) responseX.getData();
				if(pageObj != null){
					total = pageObj.getInteger("total");

					totalPage = pageObj.getInteger("totalPage");
				}

			}
			List<CarFactOrderInfoDTO> pageList = null;
			JSONObject pageObj = null;
			for(int pageNo=1; pageNo <=totalPage; pageNo++  ) {
				if(pageNo == 1){
					isFirst = true;
				}else {
					isFirst = false;
				}

				if(pageNo == totalPage){
					isLast = true;
				}
				paramMap.put("pageNo",pageNo);//页号
				// 从订单组取统计数据
				responseX = carFactOrderInfoService.queryOrderDataList(paramMap);
				code = responseX.getCode();
				csvDataList = new ArrayList<>();
				if(code == 0){
					  pageObj = (JSONObject) responseX.getData();
					 pageList  = (List<CarFactOrderInfoDTO>) pageObj.get("data");
					if(pageList != null && pageList.size() >=1){

						dataTrans(pageList,csvDataList);
//						logger.info("订单下载，下载第"+pageNo+"页数据，返回结果code为："+code
//								+";总条数为："+pageObj.get("total")+"，共"+pageObj.get("totalPage")+"页，当前页返回结果条数为："
//								+ (pageList==null?"null":pageList.size())
//						+",pageNo="+pageNo);
					}else{
//						logger.info("订单下载，下载第"+pageNo+"页数据，返回结果code为："+code+";总条数为："+pageObj.get("total")+"，共"+pageObj.get("totalPage")+"页，当前页返回结果条数为："
//								+ (pageList==null?"null":pageList.size()));
						breakTag = true;
						isLast = true;
					}
				}else{
//					logger.info("订单下载，下载第"+pageNo+"页数据，返回结果code为："+code);
					breakTag = true;
					isLast = true;
				}

				String msg = "订单下载，下载第"+pageNo+"页数据，isFirst="+isFirst+",isLast="+isLast+",csvDataList的长度为："+(csvDataList==null?"null":csvDataList.size())+";返回结果code为："+code;
				if(pageObj != null){
					msg += ";总条数为："+pageObj.get("total")+"，共"+pageObj.get("totalPage")+"页";
				}
				if(pageList != null){
					msg += "，当前页返回结果条数为："
							+ (pageList==null?"null":pageList.size());
				}
				logger.info(msg);
				if(pageNo == 1 && csvDataList.size() == 0 ){
					csvDataList.add("没有查到符合条件的数据");
				}
				entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
				if(breakTag){
					entity.exportCsvV2(response,new ArrayList<>(),headerList,fileName,isFirst,true);
					break;
				}
			}
			long end = System.currentTimeMillis();
			logger.info("订单导出成功，参数为"+JSON.toJSONString(paramMap)+";耗时："+(end -start)+"毫秒");
		} catch (Exception e) {
		 	logger.error("订单导出异常，参数为"+JSON.toJSONString(paramMap),e);
		}
		return null;
	 }

	private void dataTrans(List<CarFactOrderInfoDTO>  rows, List<String>  csvDataList ){
		if(null == rows){
			return;
		}
		for (CarFactOrderInfoDTO s : rows) {
			StringBuffer stringBuffer = new StringBuffer();
			// //订单号
			stringBuffer.append(s.getOrderNo() != null ? "" + s.getOrderNo() + "" : "");
			stringBuffer.append(",");

			//  订单指派方式

			stringBuffer.append(s.getPushDriverType() != null ? "" + s.getPushDriverType() + "" : "");
			stringBuffer.append(",");

			// 城市

			stringBuffer.append(s.getCityName() != null ? "" + s.getCityName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getServiceName() != null ? "" + s.getServiceName() + "" : "");
			stringBuffer.append(",");

			// 车型类别
			stringBuffer.append(s.getGroupName() != null ? "" + s.getGroupName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getType() != null ? "" + s.getType() + "" : "");
			stringBuffer.append(",");
			//
			stringBuffer.append(s.getBookingUserName() != null ? "" + s.getBookingUserName() + "" : "");
			stringBuffer.append(",");
			//
			stringBuffer.append(s.getBookingUserPhone() != null ? "\t" + CommonStringUtils.protectPhoneInfo(s.getBookingUserPhone()) : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getRiderName() != null ? "" + s.getRiderName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getRiderPhone() != null ? "\t" + CommonStringUtils.protectPhoneInfo(s.getRiderPhone()) : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getDriverName() != null ? "" + s.getDriverName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getDriverPhone() != null ? "\t" + s.getDriverPhone() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getLicensePlates() != null ? "" + s.getLicensePlates() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getSupplierFullName() != null ? "" + s.getSupplierFullName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelTime() != null ? "" + formatDouble(Double.valueOf(s.getTravelTime())/60/1000)  + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getTravelMileage() != null ? "" + s.getTravelMileage() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getActualPayAmount() != null ? "" + s.getActualPayAmount() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getCouponId() != null ? "" + s.getCouponId() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getCouponAmount() != null ? "" + s.getCouponAmount() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getCreateDate() != null ? "\t" + s.getCreateDate() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getCostEndDate() != null ? "\t" + s.getCostEndDate() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getFactStartAddr() != null ? "\t" + s.getFactStartAddr() + "" : "");
			stringBuffer.append(",");


			stringBuffer.append(s.getFactEndAddr() != null ? "" + s.getFactEndAddr() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getDicName() != null ? "" + s.getDicName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getAirportId() != null ? "" + s.getAirportId() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getMainOrderNo() != null ? "" + s.getMainOrderNo() + "" : "");

			csvDataList.add(stringBuffer.toString());
		}

	}

	 
	/**
     * 查询订单详情
     * @param orderId
     * @return CarFactOrderInfo
     * @createdate 2018-09-11
     * Jdd
     */
	@ResponseBody
	@RequestMapping(value = "/orderView", method = { RequestMethod.POST,RequestMethod.GET })
    @MasterSlaveConfigs(configs={ 
		  @MasterSlaveConfig(databaseTag="driver-DataSource",mode=DataSourceMode.SLAVE ),
		  @MasterSlaveConfig(databaseTag="rentcar-DataSource",mode=DataSourceMode.SLAVE )
	} )
	@RequestFunction(menu = ORDER_DETAIL)
	public AjaxResponse selectUser(String orderId,String orderNo){
		logger.info("*****************查询订单详情 订单id+"+orderId);
		if(StringUtil.isEmpty(orderId) && StringUtil.isEmpty(orderNo)){
			return AjaxResponse.failMsg(100,"参数不能是空");
		}
		//根据orderId获取订单明细
		CarFactOrderInfo order = getOrderInfo(orderId,orderNo);
		if(order==null){
			return AjaxResponse.failMsg(101,"根据条件没有返回结果");
		}
		if(order.getQxcancelstatus()>=10){
			order.setQxcancelstatus(10);
		}
		//订单时间流程赋值
		order = giveOrderTime(order);
		//添加订单待付金额信息
		if(order.getStatus()==44){
			order.setSettleDate("待付");
		}else if(order.getPayType()!=null && (order.getPayType().equals("3000")|| order.getPayType().equals("4000"))&&order.getSettleDate()!=null && order.getSettleDate().length()>0){
			if(order.getWeixin()==null||"".equals(order.getWeixin())){
				order.setWeixin(0.0);
			}
			if(order.getZfb()==null||"".equals(order.getZfb())){
				order.setZfb(0.0);
			}
			order.setSettleDate(order.getSettleDate().substring(0,19)+"   "+"后支付"+(order.getWeixin()+order.getZfb())+"元");
		}else{
			order.setSettleDate("");
		}
		// 等待时间明细
		List<CarBizOrderWaitingPeriod>  carBizOrderWaitingPeriodList = this.carFactOrderInfoService.selectWaitingPeriodListSlave(order.getOrderNo());
		order.setCarBizOrderWaitingPeriodList(carBizOrderWaitingPeriodList);
		return AjaxResponse.success(order);
	}

	/**
     * 查询主订单详情
     * @return CarFactOrderInfo
     * @createdate 2018-09-11
     * Jdd
     */
	@ResponseBody
	@RequestMapping(value = "/poolMainOrderview", method = { RequestMethod.POST,RequestMethod.GET })
	@RequestFunction(menu = MAIN_ORDER_DETAIL)
	public AjaxResponse driverOutageAddView(@Verify(param = "mainOrderNo",rule = "required")  String mainOrderNo) {
		logger.info("主订单页面mainOrderNo:"+mainOrderNo);
		CarPoolMainOrderDTO params = new CarPoolMainOrderDTO();
		params.setMainOrderNo(mainOrderNo);
		params = carFactOrderInfoService.queryCarpoolMainForObject(params);
		logger.info("主订单CarPoolMainOrderDTO:"+params);
		if(params==null){
			return AjaxResponse.failMsg(101,"没有此主订单号数据");
		}
		params = complementingInformation(params);
		logger.info("查询供应商,城市，车型:"+params);
        List<CarFactOrderInfo> carFactOrderInfoList = carFactOrderInfoService.getMainOrderByMainOrderNo(params.getMainOrderNo());
		params.setCarFactOrderInfoList(carFactOrderInfoList);
		logger.info("查询子订单list:"+carFactOrderInfoList);
		if(params!=null){
			return AjaxResponse.success(params);
		}else{
			return AjaxResponse.failMsg(500,"内部错误");
		}
	}
	
	public CarPoolMainOrderDTO complementingInformation(CarPoolMainOrderDTO params){
		//查询供应商
		if(params.getDriverId()!=null){
			CarBizDriverInfoDTO driverEntity = carFactOrderInfoService.querySupplierIdAndNameByDriverId(params.getDriverId());
			if(driverEntity!=null){
				params.setDriverName(driverEntity.getName());
				params.setDriverPhone(driverEntity.getPhone());
				CarBizSupplier supplierEntityParams = new CarBizSupplier();
				supplierEntityParams.setSupplierId(driverEntity.getSupplierId());
				CarBizSupplier supplierEntity = carFactOrderInfoService.queryCarBizSupplier(supplierEntityParams);
				if(supplierEntity!=null){
					params.setSupplierName(supplierEntity.getSupplierFullName());
				}
			}
		}
		//查询城市
		CarBizCity cityParams = new CarBizCity();
		cityParams.setCityId(params.getCityId());
		CarBizCity cityEntity = this.carFactOrderInfoService.queryCarBizCityById(cityParams);
		if(cityEntity!=null){
			params.setCityName(cityEntity.getCityName());
		}
		//查询服务类型
        params.setServiceTypeName(carFactOrderInfoService.serviceTypeName(params.getServiceTypeId()));
        //查询车行类别
		params.setGroupName(carFactOrderInfoService.getGroupNameByGroupId(params.getGroupId()));
		//查询车型
		params.setModeldetail(carFactOrderInfoService.selectModelNameByLicensePlates(params.getLicensePlates()));
        return params;
    }
	
	 /**
	    * 查询LBS提供的轨迹坐标
		* @param startDate  订单开始时间
		* @param endDate    订单结束时间
		* @param driverId   司机ID
		* @param platform   渠道来源
	    * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/queryDrivingRouteData", method = { RequestMethod.POST,RequestMethod.GET })
	 public String queryDrivingRouteData(
	 										   @Verify(param = "startDate",rule = "required") String startDate,
	                                           @Verify(param = "endDate",rule = "required")   String endDate,
	                                           @Verify(param = "driverId",rule = "required")  String driverId,
	                                           String output,
	                                           @Verify(param = "platform",rule = "required")  String platform
	                                           ){
	     logger.info("【运营管理-统计分析】查询LBS提供的轨迹坐标 :queryDrivingRouteData");
	     Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("startDate", startDate);// 
	     paramMap.put("endDate", endDate);//
	     paramMap.put("driverId", driverId);//
	     
	     if(StringUtil.isEmpty(output)){
	    	 paramMap.put("output", "1");//
	     }else{
	    	 paramMap.put("output", output);//
	     }
	     paramMap.put("platform", platform);// 
		 String result = carFactOrderInfoService.queryDrivingRouteData(paramMap);
	     return result;
	 }
	
	 /**
	    * 查询订单服务类型字典
	    * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/queryServiceEntityData", method = { RequestMethod.POST,RequestMethod.GET })
	 public List<ServiceTypeDTO> queryServiceEntityData( ){
	     logger.info("订单服务类型字典 :ServiceEntityData");
	     List<ServiceTypeDTO> list = carFactOrderInfoService.selectServiceEntityList(new ServiceEntity());
	     return list;
	 }
	
	 
	/**查询订单详情( 首先调用订单接口，然后再补全数据)**/
	private CarFactOrderInfo getOrderInfo(String orderId, String orderNo) {
		//TODO 司乘分离修改处
		//-------------------------------------------------------------------------------------------------------------------------car_fact_order拆表开始BEGIN
		SimpleDateFormat yyyyMMddHHmmssSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject orderInfoJson = orderService.getOrderInfo(orderId, orderNo );
		if(orderInfoJson==null) {
			return null;
		}
		orderId = ""+orderInfoJson.getIntValue("orderId");//重新赋下值

		CarFactOrderInfo result = new CarFactOrderInfo();
		result.setOrderId( orderInfoJson.getIntValue("orderId") );
		result.setOrderNo(  orderInfoJson.getString("orderNo") );
		result.setChannelsNum(  orderInfoJson.getString("channelsNum")  );
		if(StringUtils.isEmpty( result.getChannelsNum()  ) ) {
			result.setChannelsNum(  "0"  );
		}
		result.setPayFlag(  orderInfoJson.getIntValue("payFlag")  );
		result.setPushDriverType( orderInfoJson.getIntValue("pushDriverType") );
		result.setRiderName( orderInfoJson.getString("riderName") );
		result.setRiderPhone( CommonStringUtils.protectPhoneInfo(orderInfoJson.getString("riderPhone")) );
		result.setCityId( orderInfoJson.getIntValue("cityId")  );
		result.setCityName(  orderInfoJson.getString("cityName") );
		result.setBookingStartAddr( orderInfoJson.getString("bookingStartAddr") );
		result.setBookingEndAddr(  orderInfoJson.getString("bookingEndAddr") );
		result.setBookingStartPoint(  orderInfoJson.getString("bookingStartPoint") );
		result.setBookingEndPoint(  orderInfoJson.getString("bookingEndPoint") );
		result.setFactStartAddr( orderInfoJson.getString("factStartAddr") );
		result.setFactEndAddr(  orderInfoJson.getString("factEndAddr") );
		if(orderInfoJson.containsKey("factDate") && orderInfoJson.getLongValue("factDate")>0 ) {//实际上车时间
			Date   factDate = new Date( orderInfoJson.getLongValue("factDate") );
			String factDatestr = yyyyMMddHHmmssSDF.format(factDate);
			result.setFactDate( factDatestr );
			result.setFactDateStr( factDatestr );
		}
		if(orderInfoJson.containsKey("factEndDate") && orderInfoJson.getLongValue("factEndDate")>0 ) {//实际下车时间
			Date   factEndDate = new Date( orderInfoJson.getLongValue("factEndDate") );
			String factEndDatestr = yyyyMMddHHmmssSDF.format(factEndDate);
			result.setFactEndDate(  factEndDatestr );
		}
		result.setBookinkGroupids( orderInfoJson.getString("bookingGroupids")  );
		result.setBookinggroupids( orderInfoJson.getString("bookingGroupids") );
		result.setBookingGroupIds( orderInfoJson.getString("bookingGroupids") );

		result.setBookingGroupName( orderInfoJson.getString("bookingGroupName") );
		result.setBookingGroupnames( orderInfoJson.getString("bookingGroupName") );
		if(orderInfoJson.containsKey("createDate") && orderInfoJson.getLongValue("createDate")>0 ) {//创建时间
			Date   createDate = new Date( orderInfoJson.getLongValue("createDate") );
			String createDatestr = yyyyMMddHHmmssSDF.format(createDate);
			result.setCreatedate(  createDatestr );
		}
		result.setBookingDateStr( orderInfoJson.getString("bookingDateStr") );
		result.setLicensePlates(  orderInfoJson.getString("licensePlates") );
		result.setMemo(  orderInfoJson.getString("memo") );
		result.setStatus(  orderInfoJson.getIntValue("status") );
		result.setBookingUserId( orderInfoJson.getIntValue("bookingUserId")  );
		result.setServiceTypeId(  orderInfoJson.getIntValue("serviceTypeId") );
		result.setServiceName( orderInfoJson.getString("serviceTypeName") );
		if( orderInfoJson.containsKey("driverId") && orderInfoJson.getLongValue("driverId")>0) {
			result.setDriverId( ""+orderInfoJson.getIntValue("driverId")   );
		}
		result.setAirportId( orderInfoJson.getIntValue("airportId") );
		result.setAirlineNo( orderInfoJson.getString("airlineNo")  );
		result.setAirlineDepCode( orderInfoJson.getString("airlineDepCode") );
		if( StringUtils.isNotEmpty(orderInfoJson.getString("airlineStatus"))) {
			result.setAirlineStatus(  Integer.valueOf( orderInfoJson.getString("airlineStatus")  )  ) ;
		}
		result.setAirlineArrCode( orderInfoJson.getString("airlineArrCode") );
		if(orderInfoJson.containsKey("airlineArrDate") && orderInfoJson.getLongValue("airlineArrDate")>0 ) {
			Date   airlineArrDate = new Date( orderInfoJson.getLongValue("airlineArrDate") );
			String airlineArrDatestr = yyyyMMddHHmmssSDF.format(airlineArrDate);
			result.setAirlineArrDate(airlineArrDatestr);
		}
		if(orderInfoJson.containsKey("airlinePlanDate") && orderInfoJson.getLongValue("airlinePlanDate")>0 ) {
			Date   airlinePlanDate = new Date( orderInfoJson.getLongValue("airlinePlanDate") );
			result.setAirlinePlanDate(airlinePlanDate);
		}
		result.setDriverPassengerPriceSeparate(orderInfoJson.getIntValue("isDriverPassengerPriceSeparate"));
		//二、补全此订单的order_cost_detail
		CarFactOrderInfo  orderCostDetailData =  carFactOrderExMapper.selectOrderCostDetailByOrderId( Long.valueOf(orderId) );
		if(orderCostDetailData!=null) {
			result.setTravelTime(TimeUtils.milliToMinute(orderCostDetailData.getTravelTime() == null ? 0 : orderCostDetailData.getTravelTime()));
			result.setTravelMileage(orderCostDetailData.getTravelMileage());
			result.setNightdistancenum(orderCostDetailData.getNightdistancenum());
			result.setNightdistanceprice(orderCostDetailData.getNightdistanceprice());
			result.setLongDistancePrice(orderCostDetailData.getLongDistancePrice());
			result.setReductiontotalprice(orderCostDetailData.getReductiontotalprice());
			result.setDetailId(orderCostDetailData.getDetailId());
			result.setActualPayAmount(orderCostDetailData.getActualPayAmount());
			result.setHotDurationFees(orderCostDetailData.getHotDurationFees());
			result.setHotDuration(orderCostDetailData.getHotDuration());
			result.setHotMileage(orderCostDetailData.getHotMileage());
			result.setHotMileageFees(orderCostDetailData.getHotMileageFees());
			result.setNighitDuration(orderCostDetailData.getNighitDuration());
			result.setNighitDurationFees(orderCostDetailData.getNighitDurationFees());
			result.setPaydriver(orderCostDetailData.getPaydriver());
			result.setDecimalsFees(orderCostDetailData.getDecimalsFees());
			result.setTotalAmount(orderCostDetailData.getTotalAmount());
			result.setOverMileagePrice(orderCostDetailData.getOverMileagePrice());
			result.setOverTimePrice(orderCostDetailData.getOverTimePrice());
			result.setOverMileageNum(orderCostDetailData.getOverMileageNum());
			result.setOverTimeNum(orderCostDetailData.getOverTimeNum());
			result.setLongDistanceNum(orderCostDetailData.getLongDistanceNum());
			result.setOutServiceMileage(orderCostDetailData.getOutServiceMileage());
			result.setOutServicePrice(orderCostDetailData.getOutServicePrice());
			result.setNightServiceMileage(orderCostDetailData.getNightServiceMileage());
			result.setNightServicePrice(orderCostDetailData.getNightServicePrice());
			result.setForecastAmount(orderCostDetailData.getForecastAmount());
			result.setDesignatedDriverFee(orderCostDetailData.getDesignatedDriverFee());
			result.setWaitingTime(orderCostDetailData.getWaitingTime());
			result.setWaitingPrice(orderCostDetailData.getWaitingPrice());
			result.setBasePrice(orderCostDetailData.getBasePrice());
			result.setJmname(orderCostDetailData.getJmname());
			result.setJmdate(orderCostDetailData.getJmdate());
			result.setJmprice(orderCostDetailData.getJmprice());
			result.setJmreason(orderCostDetailData.getJmreason());
		}
		//三、补全此订单的car_biz_order_cost_detail_extension
		CarFactOrderInfo orderCostExtension = carFactOrderExMapper.selectOrderCostExtension( Long.valueOf(orderId) );
		if(orderCostExtension!=null) {
			result.setLanguageServiceFee(orderCostExtension.getLanguageServiceFee());
			result.setDistantDetail(orderCostExtension.getDistantDetail());
			result.setChannelDiscountDriver(orderCostExtension.getChannelDiscountDriver());
		}
		//四、补全此订单的司机信息
		if( result.getDriverId()!=null && result.getDriverId().length()>0 ) {
			CarBizDriverInfo carBizDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(Integer.valueOf(result.getDriverId()));
			if(carBizDriverInfo!=null) {
				result.setDrivername(carBizDriverInfo.getName());
				result.setDriverphone(carBizDriverInfo.getPhone());
			}
		}
		//五、补全此订单的预订人信息
		if( result.getBookingUserId()>0 ) {
			CarBizCustomer carBizCustomer = carBizCustomerMapper.selectByPrimaryKey(result.getBookingUserId());
			if(carBizCustomer!=null) {
				result.setBookingname(carBizCustomer.getName());
				result.setBookingphone(CommonStringUtils.protectPhoneInfo(carBizCustomer.getPhone()));
				result.setBookingUserPhone(CommonStringUtils.protectPhoneInfo(carBizCustomer.getPhone()));
			}
		}
		//六、补全此订单的车辆详情信息
		if( StringUtils.isNotEmpty(result.getLicensePlates()) ) {
			CarBizCarInfoDTO carBizCarInfoDTO = carBizCarInfoExMapper.selectModelByLicensePlates( result.getLicensePlates() );
			if(carBizCarInfoDTO!=null) {
				result.setModeldetail(  carBizCarInfoDTO.getModelDetail() );
			}
		}
		//七、补全此订单的payment信息
		Double paymentCustomer = carFactOrderExMapper.selectPaymentCustomer(result.getOrderNo());
		if(paymentCustomer!=null) {
			result.setPaymentCustomer( paymentCustomer );
		}
		Double paymentDriver = carFactOrderExMapper.selectPaymentDriver(result.getOrderNo());
		if(paymentDriver!=null) {
			result.setPaymentDriver(paymentDriver);
		}
		//八、补全此订单的dissent
		CarFactOrderInfo orderDissent = carFactOrderExMapper.selectDissent( Long.valueOf(orderId) );
		if( orderDissent !=null ) {
			result.setYymemo(orderDissent.getYymemo());
			result.setYydate(orderDissent.getYydate());
			result.setYystatus(orderDissent.getYystatus());
			result.setYyperson(orderDissent.getYyperson());
		}
		//九、补全此订单的cancel_reason
		JSONObject orderCancelInfo = orderService.getOrderCancelInfo(""+orderId);
		//示例：{"reasonId":226286645,"orderId":457740549,"shouldDeducted":null,"factDeducted":null,"cancelReasonId":null,"updateDate":1542357165000,"createDate":1542357165000,"updateBy":null,"createBy":null,"cancelType":"551","cancelStatus":2,"memo":"混派出租车派单成功，专车订单取消"}
		if( orderCancelInfo!=null ) {
			result.setQxmemo( orderCancelInfo.getString("memo") );
			long qxCreateDate = orderCancelInfo.getLongValue("createDate");
			if(qxCreateDate>0) {
				result.setQxdate( yyyyMMddHHmmssSDF.format(new Date(qxCreateDate)) );
			}
			result.setQxperson("");
			result.setQxreasonname(orderCancelInfo.getString("cancelType") );
			result.setQxcancelstatus( orderCancelInfo.getIntValue("cancelStatus") );
		}
		//十、补全此订单的car_biz_partner_pay_detail
		Double baiDuOrCtripPrice = carFactOrderExMapper.selectPartnerPayAmount(result.getOrderNo());
		if(baiDuOrCtripPrice!=null) {
			result.setBaiDuOrCtripPrice(baiDuOrCtripPrice);
		}
		//十一、补全此订单的car_biz_order_settle_detail_extension
		CarFactOrderInfo orderSettleDetail = carFactOrderExMapper.selectOrderSettleDetail(Long.valueOf(orderId));
		if( orderSettleDetail!=null ) {
			result.setWeixin(orderSettleDetail.getWeixin());
			result.setZfb(orderSettleDetail.getZfb());
			result.setPassengerPendingPay(orderSettleDetail.getPassengerPendingPay());
			result.setSettleDate(orderSettleDetail.getSettleDate());
			result.setPayType(orderSettleDetail.getPayType());
			result.setCustomerRejectPay(orderSettleDetail.getCustomerRejectPay());
		}
		//-------------------------------------------------------------------------------------------------------------------------car_fact_order拆表完成END

		//B 设置Payperson
		Integer flag = result.getPayFlag();
		if (flag == 1) {
			result.setPayperson("乘车人");
		} else if(flag == 0) {
			result.setPayperson("预订人");
		}else if(flag == 2){
			result.setPayperson("乘车人付现金");
		}
		//C 设置 MainOrderNo
		//2018-08-13日添加拼车单业务，调接口查询主单号
		String mainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(result.getOrderNo());
		result.setMainOrderNo(mainOrderNo);
		//D 计算 超时长费和时间 add by jdd
		//调用计费接口
		String costDetailParamStr = "orderId="+result.getOrderId()+"&serviceId=1";
		String costDetailResult = carFactOrderInfoService.queryCostDetailData(costDetailParamStr);
		if(!"".equals(costDetailResult)){
			JSONObject costDetailJson = JSON.parseObject(costDetailResult);
			//超时长数（单位：分钟）
			result.setOverTimeNum(Double.valueOf(String.valueOf(costDetailJson.get("overTimeNum")==null?"0.00":costDetailJson.get("overTimeNum"))));
			//超时长费（超时长数*超时长单价）
			result.setOverTimePrice(Double.valueOf(String.valueOf(costDetailJson.get("overTimePrice")==null?"0.00":costDetailJson.get("overTimePrice"))));
			//基础资费（套餐费用）
			result.setBasePrice(Double.valueOf(String.valueOf(costDetailJson.get("basePrice")==null?"0.00":costDetailJson.get("basePrice"))));
			//基础价包含公里(单位,公里)
			result.setIncludemileage(Integer.valueOf(String.valueOf(costDetailJson.get("includeMileage")==null?"0":costDetailJson.get("includeMileage"))));
			logger.info("基础价包含公里(单位,公里)  :includemileage"+costDetailJson.get("includeMileage"));
			//基础价包含时长(单位,分钟)
			result.setIncludeminute(Integer.valueOf(String.valueOf(costDetailJson.get("includeMinute")==null?"0":costDetailJson.get("includeMinute"))));
			logger.info("基础价包含时长(单位,分钟) :includeminute"+costDetailJson.get("includeMinute"));
			//String orderStatus = String.valueOf(costDetailJson.get("orderStatus"));
			//String payType = String.valueOf(costDetailJson.get("payType"));
			//长途里程(公里）  ，  空驶里程(公里)
			Double longDistanceNum = Double.valueOf(String.valueOf(costDetailJson.get("longDistanceNum")==null?"0.00":costDetailJson.get("longDistanceNum")));
			//长途费(元) ， 空驶费(元)
			Double longDistancePrice = Double.valueOf(String.valueOf(costDetailJson.get("longDistancePrice")==null?"0.00":costDetailJson.get("longDistancePrice")));
			Double actualPayAmount = Double.valueOf(String.valueOf(costDetailJson.get("actualPayAmount")==null?"0.00":costDetailJson.get("actualPayAmount")));
			result.setDistantNum(longDistanceNum);
			result.setDistantFee(longDistancePrice);
			result.setLongDistanceNum(longDistanceNum);
			result.setLongdistanceprice(longDistancePrice);
			result.setActualPayAmount(actualPayAmount);
			logger.info("order:Includemileage"+result.getIncludemileage());
			logger.info("order:Includeminute"+result.getIncludeminute());
		}
		//E 设置
		List<CarFactOrderInfo> pojoList = this.carFactOrderInfoService.selectByListPrimaryKey(Long.valueOf(orderId));
		if (pojoList != null) {
			for (int i = 0; i < pojoList.size(); i++) {
				CarFactOrderInfo info = pojoList.get(i);
				if (info.getCostTypeName().contains("停车")) {
					result.setCostTypeNameTc(info.getCostTypeName());
					result.setCostTypeNameTcPrice(info.getCost());
				} else if (info.getCostTypeName().contains("高速")) {
					result.setCostTypeNameGs(info.getCostTypeName());
					result.setCostTypeNameGsPrice(info.getCost());
				} else if (info.getCostTypeName().contains("机场")) {
					result.setCostTypeNameJc(info.getCostTypeName());
					result.setCostTypeNameJcPrice(info.getCost());
				} else if (info.getCostTypeName().contains("食宿")) {
					result.setCostTypeNameYj(info.getCostTypeName());
					result.setCostTypeNameYjPrice(info.getCost());
				}
			}
		}
		//F: 根据预约的车型id 设置车型的名字
		String bookinggroupids=result.getBookinkGroupids();
		if(!StringUtils.isEmpty(bookinggroupids)){
			String[] ids = bookinggroupids.split(",");
			String groupStr = "";
			for (int i = 0; i < ids.length; i++) {
				CarGroupEntity carBizGroup = carFactOrderInfoService.selectCarGroupById(Integer.parseInt(ids[i]));
				if (carBizGroup != null) {
					if (!groupStr.contains(carBizGroup.getGroupName())) {
						groupStr += carBizGroup.getGroupName()+ ",";
					}
				}
			}
			if (groupStr.length() > 0) {
				groupStr = groupStr.substring(0,groupStr.length() - 1);
			}
			result.setBookingGroupnames(groupStr);
		}
		//G: 设置优惠券
		CarBizOrderSettleEntity carBizOrderSettle= carFactOrderInfoService.selectDriverSettleByOrderId( Long.valueOf(orderId) );
		if(carBizOrderSettle!=null){
			//优惠券类型
			result.setCouponsType(carBizOrderSettle.getCouponsType());
			//优惠券面值
			result.setAmount(carBizOrderSettle.getCouponAmount());
			//优惠券抵扣
			result.setCouponsAmount(carBizOrderSettle.getCouponSettleAmount());
			//乘客信用卡支付
			result.setPaymentCustomer(carBizOrderSettle.getCustomerCreditcardAmount());
			//司机代收
			result.setPaydriver(carBizOrderSettle.getDriverPay());
			//司机代收现金
			result.setDriverCashAmount(carBizOrderSettle.getDriverCashAmount());
			//支付账户
			result.setChangeAmount(carBizOrderSettle.getChargeSettleAmount());
			//赠送账户
			result.setGiftAmount(carBizOrderSettle.getGiftSettleAmount());
			//司机信用卡支付
			result.setDriverCreditcardAmount(carBizOrderSettle.getDriverCreditcardAmount());
			//pos机支付
			result.setPosPay(carBizOrderSettle.getPosPay());
		}
		//设置司乘分离对象
		result.setDriverCostDetailVO(driverFeeDetailService.getOrderDriverCostDetailVO(result.getOrderNo()));
		result.setDriverCostDetailVOH5(driverFeeDetailService.getDriverCostDetail(result.getOrderNo(),Integer.parseInt(Long.toString(result.getOrderId())),result.getBuyoutFlag()));
		return result;
	}

		
		//订单时间流程赋值
		public CarFactOrderInfo giveOrderTime(CarFactOrderInfo order){
			if(order==null){
				order = new CarFactOrderInfo();
				return  order;
			}
			OrderTimeEntity orderTime=new OrderTimeEntity();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				//1、根据订单创建时间确定表名
				date =sdf1.parse(order.getCreatedate()); 
				String tableName="car_biz_driver_record_"+sdf.format(date);
				Map<String,String>paraMap=new HashMap<String, String>();
				paraMap.put("orderNo", order.getOrderNo());
				paraMap.put("tableName", tableName.replace("-", "_") ); //driver_order_record  tableName.replace("-", "_")
				logger.info("**************根据订单创建时间确定表名:"+paraMap.toString());
				List<OrderTimeEntity> p1 = carFactOrderInfoService.queryDriverOrderRecord(paraMap);
				logger.info("*************p1+"+JSON.toJSONString(p1));
				if(p1!=null){
					for (OrderTimeEntity item : p1) {
						if(item.getDriverBeginTime()!=null && item.getDriverBeginTime().length()>0){
							order.setDriverBeginTime(item.getDriverBeginTime());
						}
						if(item.getDriverArriveTime()!=null && item.getDriverArriveTime().length()>0){
							order.setDriverArriveTime(item.getDriverArriveTime());
							logger.info("可能订单为不跨天订单，DriverArriveTime"+item.getDriverArriveTime());
						}
						if(item.getDriverStartServiceTime()!=null && item.getDriverStartServiceTime().length()>0){
							order.setDriverStartServiceTime(item.getDriverStartServiceTime());
							logger.info("可能订单为不跨天订单，DriverStartServiceTime"+item.getDriverStartServiceTime());
						}
						if(item.getDriverOrderEndTime()!=null && item.getDriverOrderEndTime().length()>0){
							order.setDriverOrderEndTime(item.getDriverOrderEndTime());
						}
						if(item.getDriverOrderCoformTime()!=null && item.getDriverOrderCoformTime().length()>0){
							order.setDriverOrderCoformTime(item.getDriverOrderCoformTime());
						}
						if(item.getOrderCancleTime()!=null && item.getOrderCancleTime().length()>0){
							order.setOrderCancleTime(item.getOrderCancleTime());
						}
					}
				}
				//2、可能订单为跨天订单，需根据订单结束时间再次查询********如果中间状态也跨天怎么办？？？
				logger.info("可能订单为跨天订单，需根据订单结束时间再次查FactDate："+order.getFactDate());
				if(order.getFactDate()!=null){
					date =sdf1.parse(order.getFactDate()); 
					tableName="car_biz_driver_record_"+sdf.format(date);
					paraMap.put("tableName", tableName.replace("-", "_") ); // tableName.replace("-", "_")
					List<OrderTimeEntity> orderTimeCamcel= carFactOrderInfoService.queryDriverOrderRecord(paraMap);
					if(orderTimeCamcel!=null){
						for (OrderTimeEntity item : orderTimeCamcel) {
							logger.info("可能订单为跨天订单，需根据订单结束时间再次查DriverBeginTime："+item.getDriverBeginTime());
							//司机出发	
							if(orderTime.getDriverBeginTime()==null && item.getDriverBeginTime()!=null && item.getDriverBeginTime().length()>0){
								order.setDriverBeginTime(item.getDriverBeginTime());
							}
							logger.info("可能订单为跨天订单，需根据订单结束时间再次查DriverArriveTime："+item.getDriverArriveTime());
							//司机到达
							if(orderTime.getDriverArriveTime()==null  && item.getDriverArriveTime()!=null && item.getDriverArriveTime().length()>0){
								order.setDriverArriveTime(item.getDriverArriveTime());
								logger.info("可能订单为跨天订单，DriverArriveTime"+item.getDriverArriveTime());
							}
							
							//开始服务
							if(orderTime.getDriverStartServiceTime()==null  && item.getDriverStartServiceTime()!=null && item.getDriverStartServiceTime().length()>0){
								order.setDriverStartServiceTime(item.getDriverStartServiceTime());
								logger.info("可能订单为跨天订单，DriverStartServiceTime"+item.getDriverStartServiceTime());
							}
							
							//服务完成
							if(orderTime.getDriverOrderEndTime()==null  && item.getDriverOrderEndTime()!=null && item.getDriverOrderEndTime().length()>0){
								order.setDriverOrderEndTime(item.getDriverOrderEndTime());
							}
							//结算
							if(orderTime.getDriverOrderCoformTime()==null  && item.getDriverOrderCoformTime()!=null && item.getDriverOrderCoformTime().length()>0){
								order.setDriverOrderCoformTime(item.getDriverOrderCoformTime());
							}
							//取消订单
							if(orderTime.getOrderCancleTime()==null  && item.getOrderCancleTime()!=null && item.getOrderCancleTime().length()>0){
								order.setOrderCancleTime(item.getOrderCancleTime());
							}
						}
					}
				}
			} catch (Exception e) {
				logger.info("获取订单服务时间错误："+e.getMessage());
			}
			return order;
		}
		 public static double formatDouble(double d) {
		        return (double)Math.round(d*100)/100;
		 }
	}