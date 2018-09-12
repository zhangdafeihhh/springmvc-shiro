package com.zhuanche.controller.rentcar;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
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
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDetailDTO;
import com.zhuanche.entity.DriverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
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
	
	@Value("${bigdata.saas.data.url}")
	String  saasBigdataApiUrl;
	 
	@Autowired
	private CarFactOrderInfoService carFactOrderInfoService;
	 
	 
	/**
	    * 查询订单 列表
	    * @param queryDate	查询日期
	    * @return
	  */
	 @ResponseBody
	 @RequestMapping(value = "/queryOrderList", method = { RequestMethod.POST,RequestMethod.GET })
	 public AjaxResponse queryOrderList(
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
	                                           @Verify(param = "pageNo",rule = "required") Integer pageNo,
	                                           @Verify(param = "pageSize",rule = "required") Integer pageSize
	                                           ){
	     logger.info("【运营管理-统计分析】查询订单 列表:queryOrderList");
	     SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
	     String  transId =sdf.format(new Date());
	     
	     Map<String, Object> paramMap = new HashMap<String, Object>();
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
		 // 从订单组取统计数据
	     AjaxResponse result = carFactOrderInfoService.queryOrderDataList(paramMap);
	     return result;
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
	public CarFactOrderInfoDetailDTO selectUser(@Verify(param = "orderId",rule = "required") Long orderId){
		logger.info("*****************查询订单详情 订单id+"+orderId);
		CarFactOrderInfoDetailDTO orderDTO = new CarFactOrderInfoDetailDTO();
		long startTime=System.currentTimeMillis();
		//根据orderId获取订单明细
		CarFactOrderInfo order = getOrderInfo(orderId);
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
		List<CarBizOrderWaitingPeriod>  carBizOrderWaitingPeriodList = this.carFactOrderInfoService.selectWaitingPeriodListSlave(order.getOrderno());
		order.setCarBizOrderWaitingPeriodList(carBizOrderWaitingPeriodList);
		
		CopyBeanUtil.copyByIgnoreCase(orderDTO,order,true);
		float excTime=(float)(endTime-startTime)/1000;
		logger.info("*****************查询订单详情 耗时+"+excTime);
		return orderDTO;
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
	 										   @Verify(param = "orderNo",rule = "required") String orderNo,
	                                           String batchNo,
	                                           @Verify(param = "startDate",rule = "required") String startDate,
	                                           @Verify(param = "endDate",rule = "required")   String endDate,
	                                           @Verify(param = "driverId",rule = "required")  String driverId,
	                                           @Verify(param = "platform",rule = "required")  String platform
	                                           ){
	     logger.info("【运营管理-统计分析】查询LBS提供的轨迹坐标 :queryDrivingRouteData");
	     StringBuffer param = new StringBuffer("");
		 param.append("orderNo=" + orderNo + '&');
		 if(StringUtil.isNotEmpty(batchNo)){
			 param.append("batchNo=" + batchNo+ '&');
		 }
		 param.append("startDate=" + startDate + '&');
		 param.append("endDate=" + endDate+ '&');
		 param.append("driverId=" + driverId+ '&');
		 param.append("platform=" + platform);
		 String result = carFactOrderInfoService.queryDrivingRouteData(param.toString());
	     return result;
	 }
	
	 /**
		 * 根据orderId获取订单明细
		 * @param orderId
		 * @return
		 */
		public CarFactOrderInfo getOrderInfo(long orderId){
			CarFactOrderInfo order = this.carFactOrderInfoService.selectByPrimaryKey(orderId);
			order.setOrderId((int)orderId);
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
			String mainOrderNo = carFactOrderInfoService.getMainOrderBySubOrderNo(order.getOrderno());
			order.setMainOrderNo(mainOrderNo);
			//调用计费接口
			String costDetailParamStr = "orderId="+orderId+"&serviceId=1";
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
			List<CarFactOrderInfo> pojoList = this.carFactOrderInfoService.selectByListPrimaryKey(orderId);
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
			CarBizOrderSettleEntity carBizOrderSettle= carFactOrderInfoService.selectDriverSettleByOrderId(orderId);
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
				paraMap.put("orderNo", order.getOrderno());
				paraMap.put("tableName", tableName.replace("-", "_") ); //driver_order_record  tableName.replace("-", "_")
				List<OrderTimeEntity> p1 = carFactOrderInfoService.queryDriverOrderRecord(paraMap);
				if(p1!=null){
					for (OrderTimeEntity item : p1) {
						if(item.getDriverBeginTime()!=null && item.getDriverBeginTime().length()>0){
							order.setDriverBeginTime(item.getDriverBeginTime());
						}
						if(item.getDriverArriveTime()!=null && item.getDriverArriveTime().length()>0){
							order.setDriverArriveTime(item.getDriverArriveTime());
						}
						if(item.getDriverStartServiceTime()!=null && item.getDriverStartServiceTime().length()>0){
							order.setDriverStartServiceTime(item.getDriverStartServiceTime());
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
							}
							
							//开始服务
							if(orderTime.getDriverStartServiceTime()==null  && item.getDriverStartServiceTime()!=null && item.getDriverStartServiceTime().length()>0){
								order.setDriverStartServiceTime(item.getDriverStartServiceTime());
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