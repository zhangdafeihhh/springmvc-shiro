package com.zhuanche.controller.rentcar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Maps;
import com.zhuanche.bo.order.OrderEstimatedRouteBo;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.util.TimeUtils;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.rentcar.*;
import com.zhuanche.entity.driverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.order.DriverFeeDetailService;
import com.zhuanche.serv.order.OrderService;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.Common;
import com.zhuanche.util.CommonStringUtils;
import com.zhuanche.util.MobileOverlayUtil;
import com.zhuanche.util.excel.CsvUtils;
import mapper.rentcar.CarBizCustomerMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zhuanche.common.enums.MenuEnum.*;


/**
 * ClassName: OrderController 
 * date: 2018???9???10??? ??????11:19:45 
 * @author jiadongdong
 * @version
 * @since JDK 1.6 
 */
@Controller("orderController")
@RequestMapping(value = "/order")
public class OrderController{
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	// http://inside-yapi.01zhuanche.com/project/88/interface/api/21192
	public static final String COST_DETAIL_COLUMNS = "/order/cost/detail/columns";

	// http://inside-yapi.01zhuanche.com/project/88/interface/api/25744
	public static final String COST_DETAIL_EXTENDSION_COLUMNS = "/order/cost/detail/extension/columns";

	// http://inside-yapi.01zhuanche.com/project/88/interface/api/23268
	public static final String SETTLR_DETAIL_COLUMNS = "/order/settle/detail/columns";

	// http://inside-yapi.01zhuanche.com/project/88/interface/api/25748
	public static final String SETTLR_DETAIL_EXTENDSION_COLUMNS = "/order/settle/detail/extension/columns";

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

	@Autowired
	private CarBizCityExMapper cityExMapper;

	@Autowired
	private CarBizCarGroupExMapper carGroupExMapper;

	@Value("${kefu.trajectory.url}")
	private String kfDomain;

	@Value("${charge.url}")
	private String orderUrl;

	/**
	    * ???????????? ??????
	    * @return
	  */
//	?????????????????????????????????????????????????????????????????????????????????
//			????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????31??????
//			????????????????????????????????????????????????????????????????????????????????????31??????

	/**
	 * ????????????
	 *es ?????????wiki: http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047769
	 * ??????wiki:
	 * http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047320
	 * @param serviceId
	 * @param serviceCity ??????????????????
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
	 * @param beginCreateDate	??????????????????
	 * @param endCreateDate		??????????????????
	 * @param beginCostEndDate	????????????????????????
	 * @param endCostEndDate     ????????????????????????
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
			 								   Integer serviceCity,
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
											   Integer driverId,
	                                           @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                           @Verify(param = "pageSize",rule = "required") Integer pageSize,
											   @RequestParam(value = "channelSource",required = false,defaultValue = "0")String channelSource
	                                           ){
	     logger.info("???????????????-??????????????????????????? ??????:queryOrderList");
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
	     String  transId =sdf.format(new Date());
	     if(StringUtils.isEmpty(orderNo)){
	     	//????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????31??????
	     	if(StringUtils.isNotEmpty(statusStr) && ("44".equals(statusStr) || "45".equals(statusStr) || "50".equals(statusStr) || "55".equals(statusStr)))
			{
				if(StringUtils.isEmpty(beginCreateDate) && StringUtils.isEmpty(endCreateDate) && StringUtils.isEmpty(beginCostEndDate) && StringUtils.isEmpty(endCostEndDate)){
					AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					ajaxResponse.setMsg("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
					return ajaxResponse;
				}else if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
					try {
						Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
						Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

						int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
						if(intervalDays > 31){
							AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							ajaxResponse.setMsg("??????????????????????????????????????????????????????????????????31???");
							return ajaxResponse;
						}

					} catch (ParseException e) {
						logger.error("??????????????????????????????beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
						AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						ajaxResponse.setMsg("????????????????????????????????????????????????");
						return ajaxResponse;
					}

				}else if( (StringUtils.isNotEmpty(beginCostEndDate) && StringUtils.isNotEmpty(endCostEndDate))  ){
					try {
						Date beginCostEndDateD = DateUtils.parseDate(beginCostEndDate,"yyyy-MM-dd");
						Date endCostEndDateD = DateUtils.parseDate(endCostEndDate,"yyyy-MM-dd");

						int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCostEndDateD,endCostEndDateD);
						if(intervalDays > 31){
							AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							ajaxResponse.setMsg("????????????????????????????????????????????????????????????????????????31???");
							return ajaxResponse;
						}

					} catch (ParseException e) {
						logger.error("??????????????????????????????beginCostEndDate="+beginCostEndDate+";endCostEndDate="+endCostEndDate);
						AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						ajaxResponse.setMsg("???????????????????????????????????????????????????");
						return ajaxResponse;
					}

				}else {
					AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					ajaxResponse.setMsg("?????????????????????????????????????????????????????????");
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
							ajaxResponse.setMsg("??????????????????????????????????????????????????????????????????31???");
							return ajaxResponse;
						}

					} catch (ParseException e) {
						logger.error("??????????????????????????????beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
						AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						ajaxResponse.setMsg("????????????????????????????????????????????????");
						return ajaxResponse;
					}

				}else {
					AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					ajaxResponse.setMsg("????????????????????????????????????????????????");
					return ajaxResponse;
				}
			}
		 }
	     
	     Map<String, Object> paramMap = new HashMap<>();
		 paramMap.put("serviceId", serviceId);
		 paramMap.put("airportId", airportId);
		 paramMap.put("airportIdnot", airportIdnot);
	     paramMap.put("carGroupId", carGroupId);
	     paramMap.put("statusBatch", statusStr);
	     paramMap.put("cityId", cityId);
	     paramMap.put("supplierId", supplierId);
	     paramMap.put("teamId", teamId);
	     paramMap.put("teamClassId", teamClassId);
	     paramMap.put("bookingUserName", bookingUserName);
	     paramMap.put("bookingUserPhone", bookingUserPhone);
	     if(null == driverId || driverId.intValue()<1){
			 paramMap.put("driverPhone", driverPhone);
		 }else{
			 paramMap.put("driverId", driverId);
		 }
	     paramMap.put("licensePlates", licensePlates);
	     paramMap.put("orderNo", orderNo);
	     paramMap.put("type", orderType);
		 //??????????????????
		 paramMap.put("serviceCity" , serviceCity);
		 //??????????????????
		 if("1".equals(channelSource)){
			 paramMap.put("filterChannelOrder", "true");//????????????
		 }else if("2".equals(channelSource)){
			 paramMap.put("noFilterChannelOrder", "true");//???????????????
		 }
	     if(StringUtils.isNotEmpty(beginCreateDate)){
	    	 paramMap.put("beginCreateDate", beginCreateDate+" 00:00:00");
	     }
	     if(StringUtils.isNotEmpty(endCreateDate)){
	    	 paramMap.put("endCreateDate", endCreateDate+" 23:59:59");
	     }
	     if(StringUtils.isNotEmpty(beginCostEndDate)){
	    	 paramMap.put("beginCostEndDate", beginCostEndDate+" 00:00:00");
	     }
	     if(StringUtils.isNotEmpty(endCostEndDate)){
	    	 paramMap.put("endCostEndDate", endCostEndDate+" 23:59:59");
	     }
	     paramMap.put("transId", transId );
	     if(null != pageNo && pageNo > 0) {
			 //??????
			 paramMap.put("pageNo", pageNo);
		 }

	     if(null != pageSize && pageSize > 0) {
			 //???????????????
			 paramMap.put("pageSize", pageSize);
		 }

        /* String cityIdBatch,//????????????id?????? ?????????????????????
         String supplierIdBatch,//?????????id ???????????????
         String teamIdBatch,//??????ID????????????????????? ??????or??????
         */
	     paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,supplierId,teamId);

	     if(paramMap.get("visibleAllianceIds")!=null){
	    	 logger.info("visibleAllianceIdstoString"+paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			 paramMap.put("supplierIdBatch", paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // ???????????????ID
		}
		if(paramMap.get("visibleMotorcadeIds")!=null){
	    	 logger.info("visibleMotorcadeIdstoString"+paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			paramMap.put("teamIdBatch", paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // ????????????ID
		}
		 //?????????????????????cityId??????serviceCity???????????????????????????cityId??????????????????
//		if(paramMap.get("visibleCityIds")!=null){
//			paramMap.put("cityIdBatch", paramMap.get("visibleCityIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); //????????????ID
//		}

		 // ???????????????????????????
	     AjaxResponse result = carFactOrderInfoService.queryOrderDataList(paramMap);
	     return result;
	 }


	/**
	 * ???????????????
	 * es ?????????wiki: http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21047769
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
	     logger.info("???????????????-??????????????????????????? ??????:queryOrderList");
		 if(StringUtils.isEmpty(orderNo)){
			 //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????31??????
			 if(StringUtils.isNotEmpty(statusStr) && ("44".equals(statusStr) || "45".equals(statusStr) || "50".equals(statusStr) || "55".equals(statusStr)))
			 {
				 if(StringUtils.isEmpty(beginCreateDate) && StringUtils.isEmpty(endCreateDate) && StringUtils.isEmpty(beginCostEndDate) && StringUtils.isEmpty(endCostEndDate)){
					 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					 ajaxResponse.setMsg("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
					 return JSON.toJSONString(ajaxResponse);
				 }else if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
					 try {
						 Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
						 Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

						 int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
						 if(intervalDays > 31){
							 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							 ajaxResponse.setMsg("??????????????????????????????????????????????????????????????????31???");
							 return JSON.toJSONString(ajaxResponse);
						 }

					 } catch (ParseException e) {
						 logger.error("??????????????????????????????beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
						 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						 ajaxResponse.setMsg("????????????????????????????????????????????????");
						 return JSON.toJSONString(ajaxResponse);
					 }

				 }else if( (StringUtils.isNotEmpty(beginCostEndDate) && StringUtils.isNotEmpty(endCostEndDate))  ){
					 try {
						 Date beginCostEndDateD = DateUtils.parseDate(beginCostEndDate,"yyyy-MM-dd");
						 Date endCostEndDateD = DateUtils.parseDate(endCostEndDate,"yyyy-MM-dd");

						 int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCostEndDateD,endCostEndDateD);
						 if(intervalDays > 31){
							 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
							 ajaxResponse.setMsg("????????????????????????????????????????????????????????????????????????31???");
							 return JSON.toJSONString(ajaxResponse);
						 }

					 } catch (ParseException e) {
						 logger.error("??????????????????????????????beginCostEndDate="+beginCostEndDate+";endCostEndDate="+endCostEndDate);
						 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						 ajaxResponse.setMsg("???????????????????????????????????????????????????");
						 return JSON.toJSONString(ajaxResponse);
					 }

				 }else {
					 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					 ajaxResponse.setMsg("?????????????????????????????????????????????????????????");
					 return JSON.toJSONString(ajaxResponse);
				 }
			 }else  if( (StringUtils.isNotEmpty(beginCreateDate) && StringUtils.isNotEmpty(endCreateDate))  ){
				 try {
					 Date beginCreateDateD = DateUtils.parseDate(beginCreateDate,"yyyy-MM-dd");
					 Date endCreateDateD = DateUtils.parseDate(endCreateDate,"yyyy-MM-dd");

					 int intervalDays = com.zhuanche.util.DateUtils.getIntervalDays(beginCreateDateD,endCreateDateD);
					 if(intervalDays > 31){
						 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
						 ajaxResponse.setMsg("??????????????????????????????????????????????????????????????????31???");
						 return JSON.toJSONString(ajaxResponse);
					 }

				 } catch (ParseException e) {
					 logger.error("??????????????????????????????beginCreateDate="+beginCreateDate+";endCreateDate="+endCreateDate);
					 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
					 ajaxResponse.setMsg("????????????????????????????????????????????????");
					 return JSON.toJSONString(ajaxResponse);
				 }

			 }else {
				 AjaxResponse ajaxResponse = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID  );
				 ajaxResponse.setMsg("????????????????????????????????????????????????");
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
	     paramMap.put("pageNo", "1");//??????
	     paramMap.put("pageSize",CsvUtils.downPerSize);//???????????????
		 long start = System.currentTimeMillis();
	    paramMap = statisticalAnalysisService.getCurrentLoginUserParamMap(paramMap,cityId,supplierId,teamId);
	     if(paramMap.get("visibleAllianceIds")!=null){
	    	 logger.info("visibleAllianceIdstoString"+paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			 paramMap.put("supplierIdBatch", paramMap.get("visibleAllianceIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // ???????????????ID
		}
		if(paramMap.get("visibleMotorcadeIds")!=null){
	    	 logger.info("visibleMotorcadeIdstoString"+paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			paramMap.put("teamIdBatch", paramMap.get("visibleMotorcadeIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); // ????????????ID
		}
		if(paramMap.get("visibleCityIds")!=null){
			paramMap.put("cityIdBatch", paramMap.get("visibleCityIds").toString().replaceAll("\\[", "").replaceAll("\\]", "")); //????????????ID
		}
			
		// ??????ES?????????????????????????????????????????????????????????????????????

		 int code = -1;
		 AjaxResponse responseX = null;
		 int pageSize = CsvUtils.downPerSize;
		 paramMap.put("pageSize",pageSize);//???????????????
		 List<String> csvDataList = new ArrayList<>();

		try {
			List<String> headerList = new ArrayList<>();
			headerList.add("?????????,??????????????????,??????,????????????,????????????,????????????,?????????,???????????????,?????????,???????????????,??????,????????????,?????????,?????????,????????????(??????),????????????,??????,?????????????????????,????????????????????????,????????????,????????????,??????????????????,??????????????????,????????????,????????????,????????????");


			String fileName = "????????????"+ com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern)+".csv";
			String agent = request.getHeader("User-Agent").toUpperCase(); //???????????????????????????????????????
			if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO")>0 && agent.indexOf("RV:11")>0)) {  //IE????????????Edge?????????
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {  //???????????????
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
				paramMap.put("pageNo",pageNo);//??????
				// ???????????????????????????
				responseX = carFactOrderInfoService.queryOrderDataList(paramMap);
				code = responseX.getCode();
				csvDataList = new ArrayList<>();
				if(code == 0){
					  pageObj = (JSONObject) responseX.getData();
					 pageList  = (List<CarFactOrderInfoDTO>) pageObj.get("data");
					if(pageList != null && pageList.size() >=1){

						dataTrans(pageList,csvDataList);
//						logger.info("????????????????????????"+pageNo+"????????????????????????code??????"+code
//								+";???????????????"+pageObj.get("total")+"??????"+pageObj.get("totalPage")+"???????????????????????????????????????"
//								+ (pageList==null?"null":pageList.size())
//						+",pageNo="+pageNo);
					}else{
//						logger.info("????????????????????????"+pageNo+"????????????????????????code??????"+code+";???????????????"+pageObj.get("total")+"??????"+pageObj.get("totalPage")+"???????????????????????????????????????"
//								+ (pageList==null?"null":pageList.size()));
						breakTag = true;
						isLast = true;
					}
				}else{
//					logger.info("????????????????????????"+pageNo+"????????????????????????code??????"+code);
					breakTag = true;
					isLast = true;
				}

				String msg = "????????????????????????"+pageNo+"????????????isFirst="+isFirst+",isLast="+isLast+",csvDataList???????????????"+(csvDataList==null?"null":csvDataList.size())+";????????????code??????"+code;
				if(pageObj != null){
					msg += ";???????????????"+pageObj.get("total")+"??????"+pageObj.get("totalPage")+"???";
				}
				if(pageList != null){
					msg += "????????????????????????????????????"
							+ (pageList==null?"null":pageList.size());
				}
				logger.info(msg);
				if(pageNo == 1 && csvDataList.size() == 0 ){
					csvDataList.add("?????????????????????????????????");
				}
				entity.exportCsvV2(response,csvDataList,headerList,fileName,isFirst,isLast);
				if(breakTag){
					entity.exportCsvV2(response,new ArrayList<>(),headerList,fileName,isFirst,true);
					break;
				}
			}
			long end = System.currentTimeMillis();
			logger.info("??????????????????????????????"+JSON.toJSONString(paramMap)+";?????????"+(end -start)+"??????");
		} catch (Exception e) {
		 	logger.error("??????????????????????????????"+JSON.toJSONString(paramMap),e);
		}
		return null;
	 }

	private void dataTrans(List<CarFactOrderInfoDTO>  rows, List<String>  csvDataList ){
		if(null == rows){
			return;
		}
		for (CarFactOrderInfoDTO s : rows) {
			StringBuffer stringBuffer = new StringBuffer();
			// //?????????
			stringBuffer.append(s.getOrderNo() != null ? "" + s.getOrderNo() + "" : "");
			stringBuffer.append(",");

			//  ??????????????????

			stringBuffer.append(s.getPushDriverType() != null ? "" + s.getPushDriverType() + "" : "");
			stringBuffer.append(",");

			// ??????

			stringBuffer.append(s.getCityName() != null ? "" + s.getCityName() + "" : "");
			stringBuffer.append(",");

			stringBuffer.append(s.getServiceName() != null ? "" + s.getServiceName() + "" : "");
			stringBuffer.append(",");

			// ????????????
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
     * ??????????????????
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
		logger.info("*****************?????????????????? ??????id+"+orderId);
		if(StringUtil.isEmpty(orderId) && StringUtil.isEmpty(orderNo)){
			return AjaxResponse.failMsg(100,"??????????????????");
		}
		//??????orderId??????????????????
		CarFactOrderInfo order = getOrderInfo(orderId,orderNo);
		if(order==null){
			return AjaxResponse.failMsg(101,"??????????????????????????????");
		}
		if(order.getQxcancelstatus()>=10){
			order.setQxcancelstatus(10);
		}
		//????????????????????????
		order = giveOrderTime(order);
		//??????????????????????????????
		if(order.getStatus()==44){
			order.setSettleDate("??????");
		}else if(order.getPayType()!=null && (order.getPayType().equals("3000")|| order.getPayType().equals("4000"))&&order.getSettleDate()!=null && order.getSettleDate().length()>0){
			if(order.getWeixin()==null||"".equals(order.getWeixin())){
				order.setWeixin(0.0);
			}
			if(order.getZfb()==null||"".equals(order.getZfb())){
				order.setZfb(0.0);
			}
			order.setSettleDate(order.getSettleDate().substring(0,19)+"   "+"?????????"+(order.getWeixin()+order.getZfb())+"???");
		}else{
			order.setSettleDate("");
		}
		// ??????????????????
		List<CarBizOrderWaitingPeriod>  carBizOrderWaitingPeriodList = this.carFactOrderInfoService.selectWaitingPeriodListSlave(order.getOrderNo());
		order.setCarBizOrderWaitingPeriodList(carBizOrderWaitingPeriodList);
		/**???????????????????????????????????????wiki??????http://inside-yapi.01zhuanche.com/project/88/interface/api/19524*/
		try {
			order.setCouponSettleAmout(orderService.couponSettleAmout(orderNo));
		} catch (Exception e) {
			logger.info("???????????????????????????" + e);
		}
		return AjaxResponse.success(order);
	}

	/**
     * ?????????????????????
     * @return CarFactOrderInfo
     * @createdate 2018-09-11
     * Jdd
     */
	@ResponseBody
	@RequestMapping(value = "/poolMainOrderview", method = { RequestMethod.POST,RequestMethod.GET })
	@RequestFunction(menu = MAIN_ORDER_DETAIL)
	public AjaxResponse driverOutageAddView(@Verify(param = "mainOrderNo",rule = "required")  String mainOrderNo) {
		logger.info("???????????????mainOrderNo:"+mainOrderNo);
		CarPoolMainOrderDTO params = new CarPoolMainOrderDTO();
		params.setMainOrderNo(mainOrderNo);
		params = carFactOrderInfoService.queryCarpoolMainForObject(params);
		logger.info("?????????CarPoolMainOrderDTO:"+params);
		if(params==null){
			return AjaxResponse.failMsg(101,"???????????????????????????");
		}
		params = complementingInformation(params);
		logger.info("???????????????,???????????????:"+params);
        List<CarFactOrderInfo> carFactOrderInfoList = carFactOrderInfoService.getMainOrderByMainOrderNo(params.getMainOrderNo());
		params.setCarFactOrderInfoList(carFactOrderInfoList);
		logger.info("???????????????list:"+carFactOrderInfoList);
		if(params!=null){
			return AjaxResponse.success(params);
		}else{
			return AjaxResponse.failMsg(500,"????????????");
		}
	}
	
	public CarPoolMainOrderDTO complementingInformation(CarPoolMainOrderDTO params){
		//???????????????
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
		//????????????
		CarBizCity cityParams = new CarBizCity();
		cityParams.setCityId(params.getCityId());
		CarBizCity cityEntity = this.carFactOrderInfoService.queryCarBizCityById(cityParams);
		if(cityEntity!=null){
			params.setCityName(cityEntity.getCityName());
		}
		//??????????????????
        params.setServiceTypeName(carFactOrderInfoService.serviceTypeName(params.getServiceTypeId()));
        //??????????????????
		params.setGroupName(carFactOrderInfoService.getGroupNameByGroupId(params.getGroupId()));
		//????????????
		params.setModeldetail(carFactOrderInfoService.selectModelNameByLicensePlates(params.getLicensePlates()));
        return params;
    }
	
	 /**
	    * ??????LBS?????????????????????
		* @param startDate  ??????????????????
		* @param endDate    ??????????????????
		* @param driverId   ??????ID
		* @param platform   ????????????
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
	     logger.info("???????????????-?????????????????????LBS????????????????????? :queryDrivingRouteData");
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
	    * ??????????????????????????????
	    * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/queryServiceEntityData", method = { RequestMethod.POST,RequestMethod.GET })
	 public List<ServiceTypeDTO> queryServiceEntityData( ){
	     logger.info("???????????????????????? :ServiceEntityData");
	     List<ServiceTypeDTO> list = carFactOrderInfoService.selectServiceEntityList(new ServiceEntity());
	     return list;
	 }

	/**
	 * ?????????????????????
	 * @param oer
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/estimatedRoute")
	public AjaxResponse getDriverClientEstimatedRoute(OrderEstimatedRouteBo oer) {
		Map<String, String> result = new HashMap<>(1);
		String driverId = oer.getDriverId();
		String factDate = oer.getFactDate();
		String factEndDate = oer.getFactEndDate();
		//?????????????????????value=3
		String orderNo = oer.getOrderNo();
		//?????????????????????
		Double estimatedAmount = oer.getEstimatedAmount();
		//????????????(??????)
		Double travelMileage = oer.getTravelMileage();
		//????????????(??????)
		Double travelTimeShow = oer.getTravelTimeShow();
		//??????????????????
		Double actualPayAmount = oer.getActualPayAmount();
		logger.info("???????????????????????????-estimatedAmount:{" + estimatedAmount + "},travelMileage:{" + travelMileage + "},travelTimeShow:{" + travelTimeShow + "},actualPayAmount:{" + actualPayAmount + "}");
		logger.info("???????????????????????????,driverId=" + driverId + ",factDate=" + factDate + ",factEndDate=" + factEndDate  + "&orderNo=" + orderNo);
		String url = kfDomain;
		long startTime = com.zhuanche.util.DateUtils.getDate(factDate).getTime();
		long endTime = com.zhuanche.util.DateUtils.getDate(factEndDate).getTime();
		url += "/track/track-play-org-new.html?startTime=" + startTime + "&endTime=" + endTime + "&driverId=" + driverId +
				"&orgDistance=" + travelMileage + "&estimatedAmount=" + estimatedAmount + "" + "&orderNo=" + orderNo +
				"&travelTime=" + travelTimeShow + "&totalAmount=" + actualPayAmount + "&changeDestinationStation=1&isEnd=1";

		result.put("strValue", url);
		return AjaxResponse.success(result);
	}
	
	 
	/**??????????????????( ????????????????????????????????????????????????)**/
	private CarFactOrderInfo getOrderInfo(String orderId, String orderNo) {
		//TODO ?????????????????????
		//-------------------------------------------------------------------------------------------------------------------------car_fact_order????????????BEGIN
		SimpleDateFormat yyyyMMddHHmmssSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject orderInfoJson = orderService.getOrderInfo(orderId, orderNo );
		if(orderInfoJson==null) {
			return null;
		}
		orderId = ""+orderInfoJson.getLongValue("orderId");//???????????????

		CarFactOrderInfo result = new CarFactOrderInfo();
		result.setOrderId( orderInfoJson.getLongValue("orderId") );
		result.setOrderNo(  orderInfoJson.getString("orderNo") );
		result.setChannelsNum(  orderInfoJson.getString("channelsNum")  );
		if(StringUtils.isEmpty( result.getChannelsNum()  ) ) {
			result.setChannelsNum(  "0"  );
		}
		result.setEstimatedAmount(orderInfoJson.getDouble("estimatedAmount"));
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
		result.setShunLuDan(orderInfoJson.getString("shunLuDan"));
		if(orderInfoJson.containsKey("factDate") && orderInfoJson.getLongValue("factDate")>0 ) {//??????????????????
			Date   factDate = new Date( orderInfoJson.getLongValue("factDate") );
			String factDatestr = yyyyMMddHHmmssSDF.format(factDate);
			result.setFactDate( factDatestr );
			result.setFactDateStr( factDatestr );
		}
		if(orderInfoJson.containsKey("factEndDate") && orderInfoJson.getLongValue("factEndDate")>0 ) {//??????????????????
			Date   factEndDate = new Date( orderInfoJson.getLongValue("factEndDate") );
			String factEndDatestr = yyyyMMddHHmmssSDF.format(factEndDate);
			result.setFactEndDate(  factEndDatestr );
		}
		result.setBookinkGroupids( orderInfoJson.getString("bookingGroupids")  );
		result.setBookinggroupids( orderInfoJson.getString("bookingGroupids") );
		result.setBookingGroupIds( orderInfoJson.getString("bookingGroupids") );

		result.setBookingGroupName( orderInfoJson.getString("bookingGroupName") );
		result.setBookingGroupnames( orderInfoJson.getString("bookingGroupName") );
		if(orderInfoJson.containsKey("createDate") && orderInfoJson.getLongValue("createDate")>0 ) {//????????????
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
        if (StringUtils.isNotBlank(orderInfoJson.getString("memo"))
                && StringUtils.isNotBlank(JSON.parseObject(orderInfoJson.getString("memo")).getString("isDriverPassengerPriceSeparate"))) {
			result.setDriverPassengerPriceSeparate(Integer.parseInt(JSON.parseObject(orderInfoJson.getString("memo")).getString("isDriverPassengerPriceSeparate")));
		}

		try {
			// ???????????????
			JSONObject keyValueMap = orderInfoJson.getJSONObject("keyValueMap");
			if(keyValueMap!=null){
				JSONObject firstPassingPoint = keyValueMap.getJSONObject("firstPassingPoint");
				JSONObject secondPassingPoint = keyValueMap.getJSONObject("secondPassingPoint");
				if(firstPassingPoint!=null){
					String firstPassingPointAddr = firstPassingPoint.getString("bookingPassingPointAddr");
					result.setFirstPassingPointAddr(firstPassingPointAddr);
				}
				if(secondPassingPoint!=null){
					String secondPassingPointAddr = secondPassingPoint.getString("bookingPassingPointAddr");
					result.setSecondPassingPointAddr(secondPassingPointAddr);
				}
			}
		} catch (Exception e) {
			logger.info("??????{}???????????????????????????????????????{}", orderNo, e);
		}

        //????????????????????????order_cost_detail
		selectOrderCostDetailByOrderId(orderId, result);
		//????????????????????????car_biz_order_ cost_detail_extension
		selectOrderCostExtension(orderId, result);
		//????????????????????????????????????
		if( result.getDriverId()!=null && result.getDriverId().length()>0 ) {
			CarBizDriverInfo carBizDriverInfo = carBizDriverInfoMapper.selectByPrimaryKey(Integer.valueOf(result.getDriverId()));
			if(carBizDriverInfo!=null) {
				result.setDrivername(carBizDriverInfo.getName());
				result.setServiceCity(carBizDriverInfo.getServiceCity());
				String serviceCityName = cityExMapper.queryNameById(carBizDriverInfo.getServiceCity());
				result.setServiceCityName(serviceCityName);

			}
		}
		//???????????????????????????????????????
		if( result.getBookingUserId()>0 ) {
			CarBizCustomer carBizCustomer = carBizCustomerMapper.selectByPrimaryKey(result.getBookingUserId());
			if(carBizCustomer!=null) {
				result.setBookingname(carBizCustomer.getName());
				result.setBookingphone(CommonStringUtils.protectPhoneInfo(carBizCustomer.getPhone()));
				result.setBookingUserPhone(CommonStringUtils.protectPhoneInfo(carBizCustomer.getPhone()));
			}
		}
		//??????????????????????????????????????????
		if( StringUtils.isNotEmpty(result.getLicensePlates()) ) {
			CarBizCarInfoDTO carBizCarInfoDTO = carBizCarInfoExMapper.selectModelByLicensePlates( result.getLicensePlates() );
			if(carBizCarInfoDTO!=null) {
				result.setModeldetail(  carBizCarInfoDTO.getModelDetail() );
			}
		}
		//????????????????????????payment??????
		Double paymentCustomer = carFactOrderExMapper.selectPaymentCustomer(result.getOrderNo());
		if(paymentCustomer!=null) {
			result.setPaymentCustomer( paymentCustomer );
		}
		Double paymentDriver = carFactOrderExMapper.selectPaymentDriver(result.getOrderNo());
		if(paymentDriver!=null) {
			result.setPaymentDriver(paymentDriver);
		}
		//????????????????????????dissent
		CarFactOrderInfo orderDissent = carFactOrderExMapper.selectDissent( Long.valueOf(orderId) );
		if( orderDissent !=null ) {
			result.setYymemo(orderDissent.getYymemo());
			result.setYydate(orderDissent.getYydate());
			result.setYystatus(orderDissent.getYystatus());
			result.setYyperson(orderDissent.getYyperson());
		}
		//????????????????????????cancel_reason
		JSONObject orderCancelInfo = orderService.getOrderCancelInfo(""+orderId);
		//?????????{"reasonId":226286645,"orderId":457740549,"shouldDeducted":null,"factDeducted":null,"cancelReasonId":null,"updateDate":1542357165000,"createDate":1542357165000,"updateBy":null,"createBy":null,"cancelType":"551","cancelStatus":2,"memo":"????????????????????????????????????????????????"}
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
		//????????????????????????car_biz_partner_pay_detail
		Double baiDuOrCtripPrice = carFactOrderExMapper.selectPartnerPayAmount(result.getOrderNo());
		if(baiDuOrCtripPrice!=null) {
			result.setBaiDuOrCtripPrice(baiDuOrCtripPrice);
		}
		//???????????????????????????car_biz_order_ settle_detail_extension
		selectPayDetailExtension(orderInfoJson.getString("orderNo") , result);
		//-------------------------------------------------------------------------------------------------------------------------car_fact_order????????????END

		//B ??????Payperson
		Integer flag = result.getPayFlag();
		if (flag == 1) {
			result.setPayperson("?????????");
		} else if(flag == 0) {
			result.setPayperson("?????????");
		}else if(flag == 2){
			result.setPayperson("??????????????????");
		}
		//C ?????? MainOrderNo
		//2018-08-13???????????????????????????????????????????????????
		String mainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(result.getOrderNo());
		result.setMainOrderNo(mainOrderNo);
		//D ?????? ????????????????????? add by jdd
		//??????????????????
		String costDetailParamStr = "orderId="+result.getOrderId()+"&serviceId=1";
		String costDetailResult = carFactOrderInfoService.queryCostDetailData(costDetailParamStr);
		if(!"".equals(costDetailResult)){
			JSONObject costDetailJson = JSON.parseObject(costDetailResult);
			//?????????????????????????????????
//			result.setOverTimeNum(Double.valueOf(String.valueOf(costDetailJson.get("overTimeNum")==null?"0.00":costDetailJson.get("overTimeNum"))));
			//???????????????????????????*??????????????????
//			result.setOverTimePrice(Double.valueOf(String.valueOf(costDetailJson.get("overTimePrice")==null?"0.00":costDetailJson.get("overTimePrice"))));
			//??????????????????????????????
//			result.setBasePrice(Double.valueOf(String.valueOf(costDetailJson.get("basePrice")==null?"0.00":costDetailJson.get("basePrice"))));
			//?????????????????????(??????,??????)
//			result.setIncludemileage(Integer.valueOf(String.valueOf(costDetailJson.get("includeMileage")==null?"0":costDetailJson.get("includeMileage"))));
			result.setIncludemileage((String.valueOf(costDetailJson.get("includeMileage") == null ? "0" : costDetailJson.get("includeMileage"))));
			logger.info("?????????????????????(??????,??????)  :includemileage"+costDetailJson.get("includeMileage"));
			//?????????????????????(??????,??????)
			result.setIncludeminute(Integer.valueOf(String.valueOf(costDetailJson.get("includeMinute")==null?"0":costDetailJson.get("includeMinute"))));
			logger.info("?????????????????????(??????,??????) :includeminute"+costDetailJson.get("includeMinute"));
			//String orderStatus = String.valueOf(costDetailJson.get("orderStatus"));
			//String payType = String.valueOf(costDetailJson.get("payType"));
			//????????????(?????????  ???  ????????????(??????)
			Double longDistanceNum = Double.valueOf(String.valueOf(costDetailJson.get("longDistanceNum")==null?"0.00":costDetailJson.get("longDistanceNum")));
			//?????????(???) ??? ?????????(???)
			Double longDistancePrice = Double.valueOf(String.valueOf(costDetailJson.get("longDistancePrice")==null?"0.00":costDetailJson.get("longDistancePrice")));
//			Double actualPayAmount = Double.valueOf(String.valueOf(costDetailJson.get("actualPayAmount")==null?"0.00":costDetailJson.get("actualPayAmount")));
			//?????????????????????
			BigDecimal driverInfoServiceFee = costDetailJson.getBigDecimal("driverInfoServiceFee");
			result.setDriverInfoServiceFee(driverInfoServiceFee == null ? BigDecimal.ZERO.setScale(2) : driverInfoServiceFee);
			result.setDistantNum(longDistanceNum);
			result.setDistantFee(longDistancePrice);
//			result.setLongDistanceNum(longDistanceNum);
//			result.setLongdistanceprice(longDistancePrice);
//			result.setActualPayAmount(actualPayAmount);
			logger.info("order:Includemileage"+result.getIncludemileage());
			logger.info("order:Includeminute"+result.getIncludeminute());
		}
		//E ??????  ????????????????????? wiki http://inside-yapi.01zhuanche.com/project/88/interface/api/78750 fanht2020.11.12
		//List<CarFactOrderInfo> pojoList = this.carFactOrderInfoService.selectByListPrimaryKey(Long.valueOf(orderId));
		try {
			Map<String,Object> map = Maps.newHashMap();
			map.put("orderId",orderId);
			String orderResult = MpOkHttpUtil.okHttpGet(orderUrl +"/otherCost/getOtherCostByOrderId",map,0,null);
			if(StringUtils.isNotEmpty(orderResult)){
				JSONObject jsonData = JSONObject.parseObject(orderResult);
				if(jsonData != null && jsonData.get(Constants.DATA) != null){
					JSONObject jsonOrder = JSONObject.parseObject(jsonData.getString(Constants.DATA)) ;
					CarFactOrderInfo info = JSON.toJavaObject(jsonOrder,CarFactOrderInfo.class);
					if(info.getCostTypeName()!=null){
						if (info.getCostTypeName().contains("??????")) {
							result.setCostTypeNameTc(info.getCostTypeName());
							result.setCostTypeNameTcPrice(info.getCost());
						} else if (info.getCostTypeName().contains("??????")) {
							result.setCostTypeNameGs(info.getCostTypeName());
							result.setCostTypeNameGsPrice(info.getCost());
						} else if (info.getCostTypeName().contains("??????")) {
							result.setCostTypeNameJc(info.getCostTypeName());
							result.setCostTypeNameJcPrice(info.getCost());
						} else if (info.getCostTypeName().contains("??????")) {
							result.setCostTypeNameYj(info.getCostTypeName());
							result.setCostTypeNameYjPrice(info.getCost());
						}
					}
				}

            }
		} catch (Exception e) {
			logger.info("????????????????????????",e);
		}
		//F: ?????????????????????id ?????????????????????
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
		//G: ???????????????
		selectPayDetail(orderId, result);
		//????????????????????????
		OrderCostDetailInfo orderCostDetailInfo = driverFeeDetailService.getOrderCostDetailInfo(result.getOrderNo());
		orderCostDetailInfo.setCleanDeepFeeCount(orderCostDetailInfo.getCleanFee().add(orderCostDetailInfo.getDeepCleanFee()));
		getOverTimeHtml(orderCostDetailInfo);//??????????????????
		getOverMileageNumHtml(orderCostDetailInfo);//????????????????????????
        calcOverMileageTotal(orderCostDetailInfo);
		result.setDriverCostDetailVO(driverFeeDetailService.getOrderDriverCostDetailVO(result.getOrderNo(), result.getOrderId()));
		result.setDriverCostDetailVOH5(driverFeeDetailService.getDriverCostDetail(result.getOrderNo(),result.getOrderId(),result.getBuyoutFlag()));
        result.setOrderCostDetailInfo(orderCostDetailInfo);
        //???????????????????????? fanhongtao 2019-11-07
		if(result.getDriverCostDetailVOH5() != null && result.getDriverCostDetailVOH5().getGroupId() != null){
			Integer factGroupId = result.getDriverCostDetailVOH5().getGroupId();
			result.setFactBookingGroupId(factGroupId);
			String factGroupName = carGroupExMapper.getGroupNameByGroupId(factGroupId);
			result.setFactGroupName(factGroupName);
		}
		return result;
	}

    /**
     * ?????????????????????
     *
     * @param orderCostDetailInfo
     * @return
     */
    private void getOverTimeHtml(OrderCostDetailInfo orderCostDetailInfo) {
        if (null == orderCostDetailInfo)
            return;
        if (null != orderCostDetailInfo.getCostDurationDetailDTOList() && orderCostDetailInfo.getCostDurationDetailDTOList().size() > 0) {
            orderCostDetailInfo.setOverTimeFee(orderCostDetailInfo.getOverTimePrice());
            orderCostDetailInfo.setTravelTimeShow(orderCostDetailInfo.getOverTimeNum());
            for (CostTimeDetailDTO detailDTO : orderCostDetailInfo.getCostDurationDetailDTOList()){
                orderCostDetailInfo.setTravelTimeShow(orderCostDetailInfo.getTravelTimeShow().add(detailDTO.getNumber()));
                orderCostDetailInfo.setOverTimeFee(orderCostDetailInfo.getOverTimeFee().add(detailDTO.getAmount()));
            }
        } else {
            orderCostDetailInfo.setTravelTimeShow(orderCostDetailInfo.getOverTimeNum().add(new BigDecimal(orderCostDetailInfo.getHotDuration())).add(new BigDecimal(orderCostDetailInfo.getNighitDuration())));
            orderCostDetailInfo.setOverTimeFee(orderCostDetailInfo.getOverTimePrice().add(orderCostDetailInfo.getHotDurationFees()).add(orderCostDetailInfo.getNighitDurationFees()));
        }
    }

    /**
     * ?????????????????????
     *
     * @param orderCostDetailInfo
     * @return
     */
    private void getOverMileageNumHtml(OrderCostDetailInfo orderCostDetailInfo) {
        if (null == orderCostDetailInfo)
            return ;
        if (orderCostDetailInfo.getOverMileagePrice() != null)
            orderCostDetailInfo.setOverMileageFee(orderCostDetailInfo.getOverMileageFee().add(new BigDecimal(String.valueOf(orderCostDetailInfo.getOverMileagePrice()))));
        if (null != orderCostDetailInfo.getCostMileageDetailDTOList() && orderCostDetailInfo.getCostMileageDetailDTOList().size() > 0){
            for (CostTimeDetailDTO detailDTO : orderCostDetailInfo.getCostMileageDetailDTOList()){
                orderCostDetailInfo.setOverMileageFee(orderCostDetailInfo.getOverMileageFee().add(new BigDecimal(String.valueOf(detailDTO.getAmount()))));
            }
        }else {
            orderCostDetailInfo.setOverMileageFee(orderCostDetailInfo.getOverMileagePrice().add(orderCostDetailInfo.getHotMileageFees()).add(orderCostDetailInfo.getNightDistancePrice()));
        }
    }

    private void calcOverMileageTotal(OrderCostDetailInfo orderCostDetailInfo) {
    	if (Objects.isNull(orderCostDetailInfo)){
    		return ;
		}else {
			if (null != orderCostDetailInfo.getCostMileageDetailDTOList() && orderCostDetailInfo.getCostMileageDetailDTOList().size() > 0){
				orderCostDetailInfo.setOverMileageTotal(orderCostDetailInfo.getOverMileageNum());
				for (CostTimeDetailDTO dto : orderCostDetailInfo.getCostMileageDetailDTOList()){
					orderCostDetailInfo.setOverMileageTotal(orderCostDetailInfo.getOverMileageTotal().add(dto.getNumber()));
				}
			}else {
				orderCostDetailInfo.setOverMileageTotal(orderCostDetailInfo.getOverMileageNum().add(new BigDecimal(orderCostDetailInfo.getHotMileage()).add(orderCostDetailInfo.getNightDistanceNum())));
			}
		}

    }

		
	//????????????????????????
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
			//1???????????????????????????????????????
			date =sdf1.parse(order.getCreatedate());
			String tableName="car_biz_driver_record_"+sdf.format(date);
			Map<String,String>paraMap=new HashMap<String, String>();
			paraMap.put("orderNo", order.getOrderNo());
			paraMap.put("tableName", tableName.replace("-", "_") ); //driver_order_record  tableName.replace("-", "_")
			logger.info("**************????????????????????????????????????:"+paraMap.toString());
			List<OrderTimeEntity> p1 = carFactOrderInfoService.queryDriverOrderRecord(paraMap);
			logger.info("*************p1+"+JSON.toJSONString(p1));
			if(p1!=null){
				for (OrderTimeEntity item : p1) {
					if(item.getDriverBeginTime()!=null && item.getDriverBeginTime().length()>0){
						order.setDriverBeginTime(item.getDriverBeginTime());
					}
					if(item.getDriverArriveTime()!=null && item.getDriverArriveTime().length()>0){
						order.setDriverArriveTime(item.getDriverArriveTime());
						logger.info("?????????????????????????????????DriverArriveTime"+item.getDriverArriveTime());
					}
					if(item.getDriverStartServiceTime()!=null && item.getDriverStartServiceTime().length()>0){
						order.setDriverStartServiceTime(item.getDriverStartServiceTime());
						logger.info("?????????????????????????????????DriverStartServiceTime"+item.getDriverStartServiceTime());
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
			//2????????????????????????????????????????????????????????????????????????********?????????????????????????????????????????????
			logger.info("??????????????????????????????????????????????????????????????????FactDate???"+order.getFactDate());
			if(order.getFactDate()!=null){
				date =sdf1.parse(order.getFactDate());
				tableName="car_biz_driver_record_"+sdf.format(date);
				paraMap.put("tableName", tableName.replace("-", "_") ); // tableName.replace("-", "_")
				List<OrderTimeEntity> orderTimeCamcel= carFactOrderInfoService.queryDriverOrderRecord(paraMap);
				if(orderTimeCamcel!=null){
					for (OrderTimeEntity item : orderTimeCamcel) {
						logger.info("??????????????????????????????????????????????????????????????????DriverBeginTime???"+item.getDriverBeginTime());
						//????????????
						if(orderTime.getDriverBeginTime()==null && item.getDriverBeginTime()!=null && item.getDriverBeginTime().length()>0){
							order.setDriverBeginTime(item.getDriverBeginTime());
						}
						logger.info("??????????????????????????????????????????????????????????????????DriverArriveTime???"+item.getDriverArriveTime());
						//????????????
						if(orderTime.getDriverArriveTime()==null  && item.getDriverArriveTime()!=null && item.getDriverArriveTime().length()>0){
							order.setDriverArriveTime(item.getDriverArriveTime());
							logger.info("??????????????????????????????DriverArriveTime"+item.getDriverArriveTime());
						}

						//????????????
						if(orderTime.getDriverStartServiceTime()==null  && item.getDriverStartServiceTime()!=null && item.getDriverStartServiceTime().length()>0){
							order.setDriverStartServiceTime(item.getDriverStartServiceTime());
							logger.info("??????????????????????????????DriverStartServiceTime"+item.getDriverStartServiceTime());
						}

						//????????????
						if(orderTime.getDriverOrderEndTime()==null  && item.getDriverOrderEndTime()!=null && item.getDriverOrderEndTime().length()>0){
							order.setDriverOrderEndTime(item.getDriverOrderEndTime());
						}
						//??????
						if(orderTime.getDriverOrderCoformTime()==null  && item.getDriverOrderCoformTime()!=null && item.getDriverOrderCoformTime().length()>0){
							order.setDriverOrderCoformTime(item.getDriverOrderCoformTime());
						}
						//????????????
						if(orderTime.getOrderCancleTime()==null  && item.getOrderCancleTime()!=null && item.getOrderCancleTime().length()>0){
							order.setOrderCancleTime(item.getOrderCancleTime());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("?????????????????????????????????"+e.getMessage());
		}
		return order;
	}

	public static double formatDouble(double d) {
    	return (double)Math.round(d*100)/100;
    }


	/**
	 * ??????????????????order_cost_detail
	 * @param orderId
	 * @param result
	 */
	private void selectOrderCostDetailByOrderId(String orderId, CarFactOrderInfo result) {

		String method = COST_DETAIL_COLUMNS;
		Map<String, Object> params = new HashMap<>();
		params.put("orderId", orderId);
		params.put("columns", "*");
		params.put("isDriver", 0);
		params.put("bId", Common.BUSSINESSID);
		String tag = "???????????????";

		JSONObject jsonObject = carFactOrderInfoService.queryByJiFei(method, params, tag);
		if(jsonObject!=null) {
			result.setTravelTime(TimeUtils.milliToMinute(jsonObject.getInteger("travelTime") == null ? 0 : jsonObject.getInteger("travelTime")));
			result.setTravelMileage(Double.valueOf(String.valueOf(jsonObject.get("travelMileage")==null?"0.00":jsonObject.get("travelMileage"))));
			result.setNightdistancenum(Double.valueOf(String.valueOf(jsonObject.get("nightDistanceNum")==null?"0.00":jsonObject.get("nightDistanceNum"))));
			result.setNightdistanceprice(Double.valueOf(String.valueOf(jsonObject.get("nightDistancePrice")==null?"0.00":jsonObject.get("nightDistancePrice"))));
			result.setLongDistancePrice(Double.valueOf(String.valueOf(jsonObject.get("longDistancePrice")==null?"0.00":jsonObject.get("longDistancePrice"))));
			result.setReductiontotalprice(Double.valueOf(String.valueOf(jsonObject.get("reductionTotalprice")==null?"0.00":jsonObject.get("reductionTotalprice"))));
//            result.setDetailId(orderCostDetailData.getDetailId());
			result.setActualPayAmount(Double.valueOf(String.valueOf(jsonObject.get("actualPayAmount")==null?"0.00":jsonObject.get("actualPayAmount"))));
			result.setHotDurationFees(Double.valueOf(String.valueOf(jsonObject.get("hotDurationFees")==null?"0.00":jsonObject.get("hotDurationFees"))));
			result.setHotDuration(Double.valueOf(String.valueOf(jsonObject.get("hotDuration")==null?"0.00":jsonObject.get("hotDuration"))));
			result.setHotMileage(Double.valueOf(String.valueOf(jsonObject.get("hotMileage")==null?"0.00":jsonObject.get("hotMileage"))));
			result.setHotMileageFees(Double.valueOf(String.valueOf(jsonObject.get("hotMileageFees")==null?"0.00":jsonObject.get("hotMileageFees"))));
			result.setNighitDuration(Double.valueOf(String.valueOf(jsonObject.get("nighitDuration")==null?"0.00":jsonObject.get("nighitDuration"))));
			result.setNighitDurationFees(Double.valueOf(String.valueOf(jsonObject.get("nighitDurationFees")==null?"0.00":jsonObject.get("nighitDurationFees"))));
			result.setPaydriver(Double.valueOf(String.valueOf(jsonObject.get("driverPay")==null?"0.00":jsonObject.get("driverPay"))));
			result.setDecimalsFees(Double.valueOf(String.valueOf(jsonObject.get("decimalsFees")==null?"0.00":jsonObject.get("decimalsFees"))));
			result.setTotalAmount(Double.valueOf(String.valueOf(jsonObject.get("totalAmount")==null?"0.00":jsonObject.get("totalAmount"))));
			result.setOverMileagePrice(Double.valueOf(String.valueOf(jsonObject.get("overMileagePrice")==null?"0.00":jsonObject.get("overMileagePrice"))));
			result.setOverTimePrice(Double.valueOf(String.valueOf(jsonObject.get("overTimePrice")==null?"0.00":jsonObject.get("overTimePrice"))));
			result.setOverMileageNum(Double.valueOf(String.valueOf(jsonObject.get("overMileageNum")==null?"0.00":jsonObject.get("overMileageNum"))));
			result.setOverTimeNum(Double.valueOf(String.valueOf(jsonObject.get("overTimeNum")==null?"0.00":jsonObject.get("overTimeNum"))));
			result.setLongDistanceNum(Double.valueOf(String.valueOf(jsonObject.get("longDistanceNum")==null?"0.00":jsonObject.get("longDistanceNum"))));
			result.setOutServiceMileage(Double.valueOf(String.valueOf(jsonObject.get("outServiceMileage")==null?"0.00":jsonObject.get("outServiceMileage"))));
			result.setOutServicePrice(Double.valueOf(String.valueOf(jsonObject.get("outServicePrice")==null?"0.00":jsonObject.get("outServicePrice"))));
			result.setNightServiceMileage(Double.valueOf(String.valueOf(jsonObject.get("nightServiceMileage")==null?"0.00":jsonObject.get("nightServiceMileage"))));
			result.setNightServicePrice(Double.valueOf(String.valueOf(jsonObject.get("nightServicePrice")==null?"0.00":jsonObject.get("nightServicePrice"))));
			result.setForecastAmount(Double.valueOf(String.valueOf(jsonObject.get("forecastAmount")==null?"0.00":jsonObject.get("forecastAmount"))));
			result.setDesignatedDriverFee(Double.valueOf(String.valueOf(jsonObject.get("designatedDriverFee")==null?"0.00":jsonObject.get("designatedDriverFee"))));
			result.setWaitingTime(Double.valueOf(String.valueOf(jsonObject.get("waitingMinutes")==null?"0.00":jsonObject.get("waitingMinutes"))));
			result.setWaitingPrice(Double.valueOf(String.valueOf(jsonObject.get("waitingFee")==null?"0.00":jsonObject.get("waitingFee"))));
			result.setBasePrice(Double.valueOf(String.valueOf(jsonObject.get("basePrice")==null?"0.00":jsonObject.get("basePrice"))));
			// TODO ???????????????????????????
			result.setJmname(String.valueOf(jsonObject.get("reductionPerson")==null?"":jsonObject.get("reductionPerson")));
			result.setJmdate(String.valueOf(jsonObject.get("reductionDate")==null?"":jsonObject.get("reductionDate")));
			result.setJmprice(Double.valueOf(String.valueOf(jsonObject.get("reductionPrice")==null?"0.00":jsonObject.get("reductionPrice"))));
			result.setJmreason(String.valueOf(jsonObject.get("reductionReason")==null?"":jsonObject.get("reductionReason")));
		}
	}

	/**
	 * ??????????????????car_biz_order_ cost_detail_extension
	 * @param orderId
	 * @param result
	 */
	private void selectOrderCostExtension(String orderId, CarFactOrderInfo result) {
		String method = COST_DETAIL_EXTENDSION_COLUMNS;
		Map<String, Object> params = new HashMap<>();
		params.put("orderId", orderId);
		params.put("bId", Common.BUSSINESSID);
		params.put("columns", "language_service_fee, distant_detail, channel_discount_driver");
		String tag = "?????????????????????";

		JSONObject jsonObject = carFactOrderInfoService.queryByJiFei(method, params, tag);
		if(jsonObject!=null) {
			result.setLanguageServiceFee(Double.valueOf(String.valueOf(jsonObject.get("languageServiceFee")==null?"0.00":jsonObject.get("languageServiceFee"))));
			String distantDetail = jsonObject.getString("distantDetail");
			result.setDistantDetail("null".equals(distantDetail) || "[null]".equals(distantDetail) ? null : distantDetail);
			result.setChannelDiscountDriver(jsonObject.getString("channelDiscountDriver"));
		}
	}

	/**
	 * ???????????????
	 * @param orderId
	 * @param result
	 */
	private void selectPayDetail(String orderId, CarFactOrderInfo result) {

		String method = SETTLR_DETAIL_COLUMNS;
		Map<String, Object> params = new HashMap<>();
		params.put("orderId", orderId);
		params.put("bId", Common.BUSSINESSID);
		params.put("columns", "*");
		String tag = "???????????????";

		JSONObject jsonObject = carFactOrderInfoService.queryByJiFei(method, params, tag);
		if(jsonObject!=null) {
			//???????????????
			result.setCouponsType(Double.valueOf(String.valueOf(jsonObject.get("couponsType")==null?"0.00":jsonObject.get("couponsType"))));
			//???????????????
			result.setAmount(Double.valueOf(String.valueOf(jsonObject.get("couponAmount")==null?"0.00":jsonObject.get("couponAmount"))));
			//???????????????
			result.setCouponsAmount(Double.valueOf(String.valueOf(jsonObject.get("couponSettleAmount")==null?"0.00":jsonObject.get("couponSettleAmount"))));
			//?????????????????????
			result.setPaymentCustomer(Double.valueOf(String.valueOf(jsonObject.get("customerCreditcardAmount")==null?"0.00":jsonObject.get("customerCreditcardAmount"))));
			//????????????
			result.setPaydriver(Double.valueOf(String.valueOf(jsonObject.get("driverPay")==null?"0.00":jsonObject.get("driverPay"))));
			//??????????????????
			result.setDriverCashAmount(Double.valueOf(String.valueOf(jsonObject.get("deiverCashAmount")==null?"0.00":jsonObject.get("deiverCashAmount"))));
			//????????????
			result.setChangeAmount(Double.valueOf(String.valueOf(jsonObject.get("chargeSettleAmount")==null?"0.00":jsonObject.get("chargeSettleAmount"))));
			//????????????
			result.setGiftAmount(Double.valueOf(String.valueOf(jsonObject.get("giftSettleAmount")==null?"0.00":jsonObject.get("giftSettleAmount"))));
			//?????????????????????
			result.setDriverCreditcardAmount(Double.valueOf(String.valueOf(jsonObject.get("deiverCreditcardAmount")==null?"0.00":jsonObject.get("deiverCreditcardAmount"))));
			//pos?????????
			result.setPosPay(Double.valueOf(String.valueOf(jsonObject.get("posPay")==null?"0.00":jsonObject.get("posPay"))));
			//??????????????????(????????????)
			result.setCustomerRejectPay(Double.valueOf(String.valueOf(jsonObject.get("customerRejectPay")==null?"0.00":jsonObject.get("customerRejectPay"))));
		}
	}

	/**
	 * ??????????????????car_biz_order_ settle_detail_extension
	 * @param orderNo
	 * @param result
	 */
	private void selectPayDetailExtension(String orderNo, CarFactOrderInfo result) {
		String method = SETTLR_DETAIL_EXTENDSION_COLUMNS;
		Map<String, Object> params = new HashMap<>();
		params.put("orderNo", orderNo);
		params.put("bId", Common.BUSSINESSID);
		params.put("columns", "*");
		String tag = "?????????????????????";

		JSONObject jsonObject = carFactOrderInfoService.queryByJiFei(method, params, tag);
		if(jsonObject!=null) {
			result.setWeixin(Double.valueOf(String.valueOf(jsonObject.get("wxSettleAmount")==null?"0.00":jsonObject.get("wxSettleAmount"))));
			result.setZfb(Double.valueOf(String.valueOf(jsonObject.get("zfbSettleAmount")==null?"0.00":jsonObject.get("zfbSettleAmount"))));
			result.setPassengerPendingPay(Double.valueOf(String.valueOf(jsonObject.get("passengerPendingPay")==null?"0.00":jsonObject.get("passengerPendingPay"))));
			result.setSettleDate(jsonObject.getString("settleDate"));
			result.setPayType(jsonObject.getString("payType"));
		}
	}
}