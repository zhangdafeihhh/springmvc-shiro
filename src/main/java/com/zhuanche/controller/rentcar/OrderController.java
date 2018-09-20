package com.zhuanche.controller.rentcar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.controller.driver.Componment;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDTO;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDetailDTO;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.DriverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarGroupEntity;
import com.zhuanche.entity.rentcar.ServiceEntity;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.serv.statisticalAnalysis.StatisticalAnalysisService;
import com.zhuanche.util.CopyBeanUtil;

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
	/**
	    * 查询订单 列表
	    * @param queryDate	查询日期
	    * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/queryOrderList", method = { RequestMethod.POST,RequestMethod.GET })
	 public AjaxResponse queryOrderList(
			 								   String serviceId,
			 								   String airportIdnot,
			 								   String airportId,
	 										   String carGroupId,
	 										   String status,
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
	                                           @Verify(param = "beginCreateDate",rule = "required") String beginCreateDate,
	                                           @Verify(param = "endCreateDate",rule = "required") String endCreateDate,
	                                           @Verify(param = "beginCostEndDate",rule = "required") String beginCostEndDate,
	                                           @Verify(param = "endCostEndDate",rule = "required") String endCostEndDate,
	                                           @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                           @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                           ){
	     logger.info("【运营管理-统计分析】查询订单 列表:queryOrderList");
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
	     String  transId =sdf.format(new Date());
	     
	     Map<String, Object> paramMap = new HashMap<String, Object>();
		 paramMap.put("serviceId", serviceId);// 
		 paramMap.put("airportId", airportId);//
		 paramMap.put("airportIdnot", airportIdnot);//
	     paramMap.put("carGroupId", carGroupId);// 
	     paramMap.put("status", status);//
	     paramMap.put("cityId", cityId);//
	     paramMap.put("supplierId", supplierId);//
	     paramMap.put("teamId", teamId);// 
	     paramMap.put("teamClassId", teamClassId);//
	     paramMap.put("bookingUserName", bookingUserName);//
	     paramMap.put("bookingUserPhone", bookingUserPhone);//
	     paramMap.put("driverPhone", driverPhone);//
	     paramMap.put("licensePlates", licensePlates);// 
	     paramMap.put("orderNo", orderNo);//
	     paramMap.put("orderType", orderType);//
	     paramMap.put("beginCreateDate", beginCreateDate+" 00:00:00");//
	     paramMap.put("endCreateDate", endCreateDate+" 23:59:59");//
	     paramMap.put("beginCostEndDate", beginCostEndDate+" 00:00:00");//
	     paramMap.put("endCostEndDate", endCostEndDate+" 23:59:59");//
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
	 
	 public String arrayToStr(String v[]){
		 String temp = "";
		 for(String str : v){
			 temp+=str+",";
		 }
		 if(!"".equals(temp)){
			 temp=temp.substring(0, temp.length()-1);
		 }
		 return temp;
	 }
	 
		/**
	    * 订单 列表导出
	    * @param queryDate	查询日期
	    * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/exportOrderList", method = { RequestMethod.POST,RequestMethod.GET })
	 public void exportOrderList(
											   String serviceId,
											   String airportIdnot,
											   String airportId,
	 										   String carGroupId,
	 										   String status,
	 										   String cityId,
	 										   String supplierId,
	                                           String teamId,
	                                           String teamClassId,
	                                           String bookingUserName, 
	                                           String bookingUserPhone,
	                                           String driverPhone,
	                                           String licensePlates, 
	                                           String orderNo, 
	                                           String orderType,
	                                           @Verify(param = "beginCreateDate",rule = "required") String beginCreateDate,
	                                           @Verify(param = "endCreateDate",rule = "required") String endCreateDate,
	                                           @Verify(param = "beginCostEndDate",rule = "required") String beginCostEndDate,
	                                           @Verify(param = "endCostEndDate",rule = "required") String endCostEndDate,
	                                           HttpServletRequest request,HttpServletResponse response
	                                           ){
	     logger.info("【运营管理-统计分析】查询订单 列表:queryOrderList");
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
	     String  transId =sdf.format(new Date());
	     
	     Map<String, Object> paramMap = new HashMap<String, Object>();
	     paramMap.put("serviceId", serviceId);// 
		 paramMap.put("airportId", airportId);//
		 paramMap.put("airportIdnot", airportIdnot);//
	     paramMap.put("carGroupId", carGroupId);// 
	     paramMap.put("status", status);//
	     paramMap.put("cityId", cityId);//
	     paramMap.put("supplierId", supplierId);//
	     paramMap.put("teamId", teamId);// 
	     paramMap.put("teamClassId", teamClassId);//
	     paramMap.put("bookingUserName", bookingUserName);//
	     paramMap.put("bookingUserPhone", bookingUserPhone);//
	     paramMap.put("driverPhone", driverPhone);//
	     paramMap.put("licensePlates", licensePlates);// 
	     paramMap.put("orderNo", orderNo);//
	     paramMap.put("orderType", orderType);//
	     paramMap.put("beginCreateDate", beginCreateDate+" 00:00:00");//
	     paramMap.put("endCreateDate", endCreateDate+" 23:59:59");//
	     paramMap.put("beginCostEndDate", beginCostEndDate+" 00:00:00");//
	     paramMap.put("endCostEndDate", endCostEndDate+" 23:59:59");//
	     paramMap.put("transId", transId );//
	     paramMap.put("pageNo", "1");//页号
	     paramMap.put("pageSize", "10000");//每页记录数
		 // 从订单组取统计数据
	    List<CarFactOrderInfoDTO> dtoList = carFactOrderInfoService.queryAllOrderDataList(paramMap);
		@SuppressWarnings("deprecation")
		Workbook wb;
		try {
			wb = carFactOrderInfoService.exportExceleOrderList(dtoList,request.getServletContext().getRealPath("/")+ "template" + File.separator +"orderList_info.xlsx");
			Componment.fileDownload(response, wb, new String("订单列表".getBytes("gb2312"), "iso8859-1"));
			
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
	 
	/**
     * 查询订单详情
     * @param orderId
     * @return CarFactOrderInfo
     * @createdate 2018-09-11
     * Jdd
     */
	@ResponseBody
	@RequestMapping(value = "/orderView", method = { RequestMethod.POST,RequestMethod.GET })
	public AjaxResponse selectUser(String orderId,String orderNo){
		logger.info("*****************查询订单详情 订单id+"+orderId);
		CarFactOrderInfoDetailDTO orderDTO = new CarFactOrderInfoDetailDTO();
		long startTime=System.currentTimeMillis();
		if(StringUtil.isEmpty(orderId) && StringUtil.isEmpty(orderNo)){
			return AjaxResponse.failMsg(100,"参数不能是空");
		}
		//根据orderId获取订单明细
		CarFactOrderInfo order = getOrderInfo(orderId,orderNo);
		if(order==null){
			return AjaxResponse.failMsg(101,"根据条件没有返回结果");
		}
		long endTime=System.currentTimeMillis();
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
		
		CopyBeanUtil.copyByIgnoreCase(orderDTO,order,true);
		float excTime=(float)(endTime-startTime)/1000;
		logger.info("*****************查询订单详情 耗时+"+excTime);
		if(orderDTO!=null){
			return AjaxResponse.success(orderDTO);
		}else{
			return AjaxResponse.failMsg(500,"内部错误");
		}
	}
	
	
	/**
     * 查询主订单详情
     * @param orderId
     * @return CarFactOrderInfo
     * @createdate 2018-09-11
     * Jdd
     */
	@ResponseBody
	@RequestMapping(value = "/poolMainOrderview", method = { RequestMethod.POST,RequestMethod.GET })
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
	    * @param orderNo	订单号
		* @param batchNo	批次号 
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
	
	 
	 /**
		 * 根据orderId获取订单明细
		 * @param orderId
		 * @return
		 */
		public CarFactOrderInfo getOrderInfo(String orderId,String orderNo){
			CarFactOrderInfo carFactOrderInfo = new CarFactOrderInfo();
			if(StringUtil.isNotEmpty(orderId)){
				carFactOrderInfo.setOrderId(Long.valueOf(orderId));
			}
			if(StringUtil.isNotEmpty(orderNo)){
				carFactOrderInfo.setOrderNo(orderNo);
			}
			CarFactOrderInfo order = this.carFactOrderInfoService.selectByPrimaryKey(carFactOrderInfo);
			if(order==null){
				return null;
			}
			order.setOrderId(order.getOrderId());
			Integer flag = order.getPayFlag();
			if(flag!=null){
				if (flag == 1) {
					order.setPayperson("乘车人");
				} else if(flag == 0) {
					order.setPayperson("预订人");
				}else if(flag == 2){
					order.setPayperson("乘车人付现金");
				}
			}
			
			//调订单接口，查询拼车子订单号查主单号
			String mainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(order.getOrderNo());
			order.setMainOrderNo(mainOrderNo);
			
			//调用计费接口
			String costDetailParamStr = "orderId="+order.getOrderId()+"&serviceId=1";
			String result = carFactOrderInfoService.queryCostDetailData(costDetailParamStr);
			if(!"".equals(result)){
				JSONObject costDetailJson = JSON.parseObject(result);
				 //超时长数（单位：分钟）
				order.setOverTimeNum(Double.valueOf(String.valueOf(costDetailJson.get("overTimeNum")==null?"0.00":costDetailJson.get("overTimeNum"))));
				//超时长费（超时长数*超时长单价）
				order.setOverTimePrice(Double.valueOf(String.valueOf(costDetailJson.get("overTimePrice")==null?"0.00":costDetailJson.get("overTimePrice"))));
				//基础资费（套餐费用）
				order.setBasePrice(Double.valueOf(String.valueOf(costDetailJson.get("basePrice")==null?"0.00":costDetailJson.get("basePrice"))));
				//基础价包含公里(单位,公里) 
				order.setIncludemileage(Integer.valueOf(String.valueOf(costDetailJson.get("includemileage")==null?"0":costDetailJson.get("includemileage"))));
				//基础价包含时长(单位,分钟)
				order.setIncludeminute(Integer.valueOf(String.valueOf(costDetailJson.get("includeminute")==null?"0":costDetailJson.get("includeminute"))));
				//查找车型的名字
				order.setBookingGroupnames(String.valueOf(costDetailJson.get("carGroupName")==null?"":costDetailJson.get("carGroupName")));
				//String orderStatus = String.valueOf(costDetailJson.get("orderStatus"));
				//String payType = String.valueOf(costDetailJson.get("payType"));
				//长途里程(公里）  ，  空驶里程(公里)
				Double longDistanceNum = Double.valueOf(String.valueOf(costDetailJson.get("longDistanceNum")==null?"0.00":costDetailJson.get("longDistanceNum")));
				//长途费(元) ， 空驶费(元)
				Double longDistancePrice = Double.valueOf(String.valueOf(costDetailJson.get("longDistancePrice")==null?"0.00":costDetailJson.get("longDistancePrice")));
				order.setDistantNum(longDistanceNum);
				order.setDistantFee(longDistancePrice);
				order.setLongDistanceNum(longDistanceNum);
				order.setLongdistanceprice(longDistancePrice);
				
			}
			 //end
			List<CarFactOrderInfo> pojoList = this.carFactOrderInfoService.selectByListPrimaryKey(order.getOrderId());
			if (pojoList != null) {
				for (int i = 0; i < pojoList.size(); i++) {
					CarFactOrderInfo info = pojoList.get(i);
					if (info.getCostTypeName().contains("停车")) {
						order.setCostTypeNameTc(info.getCostTypeName());
						order.setCostTypeNameTcPrice(info.getCost());
					} else if (info.getCostTypeName().contains("高速")) {
						order.setCostTypeNameGs(info.getCostTypeName());
						order.setCostTypeNameGsPrice(info.getCost());
					} else if (info.getCostTypeName().contains("机场")) {
						order.setCostTypeNameJc(info.getCostTypeName());
						order.setCostTypeNameJcPrice(info.getCost());
					} else if (info.getCostTypeName().contains("食宿")) {
						order.setCostTypeNameYj(info.getCostTypeName());
						order.setCostTypeNameYjPrice(info.getCost());
					}
				}
			}
			//根据预约的车型id 查找车型的名字
			String bookinggroupids=order.getBookinkGroupids();
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
					order.setBookingGroupnames(groupStr);
			}
			
			CarBizOrderSettleEntity carBizOrderSettle= carFactOrderInfoService.selectDriverSettleByOrderId(order.getOrderId());
			if(carBizOrderSettle!=null){
				//优惠券面值
				//优惠券类型
				order.setCouponsType(carBizOrderSettle.getCouponsType());
				order.setAmount(carBizOrderSettle.getCouponAmount());
				//优惠券抵扣
				order.setCouponsAmount(carBizOrderSettle.getCouponSettleAmount());
				//乘客信用卡支付
				order.setPaymentCustomer(carBizOrderSettle.getCustomerCreditcardAmount());
				//司机代收
				order.setPaydriver(carBizOrderSettle.getDriverPay());
				//司机代收现金
				order.setDriverCashAmount(carBizOrderSettle.getDriverCashAmount());
				//支付账户
				order.setChangeAmount(carBizOrderSettle.getChargeSettleAmount());
				//赠送账户
				order.setGiftAmount(carBizOrderSettle.getGiftSettleAmount());
				//司机信用卡支付
				order.setDriverCreditcardAmount(carBizOrderSettle.getDriverCreditcardAmount());
				//pos机支付
				order.setPosPay(carBizOrderSettle.getPosPay());
			}
			return order;
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
				List<OrderTimeEntity> p1 = carFactOrderInfoService.queryDriverOrderRecord(paraMap);
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
							//司机出发	
							if(orderTime.getDriverBeginTime()==null && item.getDriverBeginTime()!=null && item.getDriverBeginTime().length()>0){
								order.setDriverBeginTime(item.getDriverBeginTime());
							}
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
	}