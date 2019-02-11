package com.zhuanche.serv.rentcar.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDTO;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.dto.rentcar.ServiceTypeDTO;
import com.zhuanche.entity.DriverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.*;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.util.Common;
import com.zhuanche.util.CommonStringUtils;
import com.zhuanche.util.MyRestTemplate;
import mapper.driverOrderRecord.DriverOrderRecordMapper;
import mapper.orderPlatform.PoolMainOrderMapper;
import mapper.rentcar.ex.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CarFactOrderInfoServiceImpl implements CarFactOrderInfoService {
    private static Logger logger = LoggerFactory.getLogger(CarFactOrderInfoServiceImpl.class);

	/**链接超时时间**/
	private static final Integer CONNECT_TIMEOUT = 30000;
	/**读取超时时间**/
	private static final Integer READ_TIMEOUT = 30000;
	
    //LBS轨迹URL
    @Value("${lbs.driver.gps.rest.url}")
	String  baseLbsRestApiUrl;
    
    //订单接口URL
    @Value("${base.order.search.url}")
	String  orderSearchUrl;
    
    //计费接口URL
    @Value("${bus.order.cost.url}")
	String  orderCostUrl;
    
    //订单组提供 拼车子orderNo查主订单URL
    @Value("${car.rest.url}")
	String  carRestUrl;

	@Autowired
	@Qualifier("orderApiTemplate")
	private MyRestTemplate orderApiTemplate;
    
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private CarFactOrderExMapper carFactOrderExMapper;

    @Autowired
    private DriverOrderRecordMapper driverOrderRecordMapper;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;
    
    @Autowired
    private CarBizSupplierExMapper carBizSupplierExMapper;
    
    @Autowired
    private CarBizCityExMapper carBizCityExMapper;
    
    @Autowired
    private PoolMainOrderMapper poolMainOrderMapper;
    
    @Autowired
    private  CarBizCarGroupExMapper carBizCarGroupExMapper;

   
	/**
	 * 调用订单接口，根据子订单号查询主订单
	 * @param orderNo
	 * @return
	 */
	@Override
	public String getMainOrderBySubOrderNo(String orderNo){
		String mainOrderNo = "";
		//String url02 = "http://test-inside-order.01zhuanche.com/"+Common.GET_MAIN_ORDER_BY_ORDERNO + "?businessId=" + Common.BUSSINESSID + "&orderNo=P1521107110815579";
		String url = carRestUrl+Common.GET_MAIN_ORDER_BY_ORDERNO + 
					"?businessId=" + Common.BUSSINESSID + "&orderNo="+orderNo;
		 try {
			// 参数：订单号 、业务线id
			StringBuffer param = new StringBuffer("");
			param.append("businessId=" + Common.BUSSINESSID + '&');
			param.append("orderNo=" +  orderNo  + '&');
			param.append("key=" + Common.MAIN_ORDER_KEY);
			String sign = java.net.URLEncoder.encode(
					Base64.encodeBase64String(DigestUtils.md5(param.toString())), "UTF-8");
			url += "&sign="+sign;
			 
			System.out.println(url);
			
			String result = HttpClientUtil.buildGetRequest(url).addHeader("Content-Type", ContentType.APPLICATION_JSON).setConnectTimeOut(CONNECT_TIMEOUT)
					.setReadTimeOut(READ_TIMEOUT).execute();
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用订单接口，根据子订单号查询主订单" + url + "返回结果为null");
				return "";
			}
			if (!job.getString("code").equals("0")) {
				logger.error("调用订单接口，根据子订单号查询主订单" + url + "返回结果为code"+job.getString("code").equals("0"));
				return "";
			}
			JSONObject jsonResult = JSON.parseObject(job.getString("data"));
			if (jsonResult!=null) {
				mainOrderNo = String.valueOf(jsonResult.getString("mainOrderNo"));
				return mainOrderNo;
			}
		} catch (Exception e) {
			logger.error("调用订单接口，根据子订单号查询主订单" + url + "异常", e);
			e.printStackTrace();
		} 
		return mainOrderNo;
	}
	
	
	/**
	 * 根据主订单查询子订单信息
	 * @param mainOrderNo
	 * @return
	 */
	@Override
	public List<CarFactOrderInfo> getMainOrderByMainOrderNo(String mainOrderNo){
		List<CarFactOrderInfo> rows = new ArrayList<CarFactOrderInfo>();
	 try {
			String url = carRestUrl+Common.GET_MAIN_ORDER + 
						"?businessId=" + Common.BUSSINESSID + "&isShowSubOrderList=0&mainOrderNo=" + mainOrderNo;
			// 参数：订单号 、业务线id
			StringBuffer param = new StringBuffer("");
			param.append("businessId=" + Common.BUSSINESSID + "&isShowSubOrderList=0&");
			param.append("mainOrderNo=" + mainOrderNo  + '&');
			param.append("key=" + Common.MAIN_ORDER_KEY);
			String sign = java.net.URLEncoder.encode(
					Base64.encodeBase64String(DigestUtils.md5(param.toString())), "UTF-8");
			url += "&sign="+sign;
			 
			System.out.println(url);
			
			String result = HttpClientUtil.buildGetRequest(url).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用订单接口，根据子订单号查询主订单" + url + "返回结果为null");
				return null;
			}
			if (!job.getString("code").equals("0")) {
				logger.error("根据主订单号查询子订单出错,错误码:" + url + "返回结果为code"+job.getString("code").equals("0"));
				return null;
			}
			JSONObject jsonResult = JSON.parseObject(job.getString("data"));
			if (jsonResult!=null) {
				String subOrderList = String.valueOf(jsonResult.getString("subOrderList"));
				rows =  JSONArray.parseArray(subOrderList, CarFactOrderInfo.class);   
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}
	
	/**
	 * 查询LBS提供的轨迹坐标
	 * @param paramMap
	 * @return
	 */
	@Override
	public String queryDrivingRouteData(Map<String, Object> paramMap) {
		String result = "";
		String url = baseLbsRestApiUrl+Common.LBS_DRIVING_ROUTE;
		// String url01 =  "http://test-inside-trace-source-lbs.01zhuanche.com/grapsRoad?startDate=2018-06-20&endDate=2018-06-20&driverId=199824&output=0&platform=3";
		try {
			result = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
		} catch (Exception e) {
			logger.error("调用LBS查询LBS提供的轨迹坐标" + url + "异常", e);
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 查询计费提供的计费明细
	 * @param paramsStr
	 * @return
	 */
	@Override
	public String queryCostDetailData(String paramsStr) {
		String result = "";
		String url = orderCostUrl+Common.COST_ORDER_DETAIL+"?"+paramsStr;
		//String url01 =  "http://test-inside-charge.01zhuanche.com/orderCostdetail/getCostDetail?orderId=26985&serviceId=1";
		try {
			result = HttpClientUtil.buildGetRequest(url).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用计费接口" + url + "返回结果为null");
				return "";
			}
			if (!job.getString("code").equals("0")) {
				logger.info("调用计费接口" + url + "返回结果为result"+result);
				return "";
			}
			if (job != null) {
				if("0".equals(job.get("code").toString())){
					return job.get("data").toString();
				}
			}
		} catch (HttpException e) {
			logger.error("调用计费接口" + url + "异常", e);
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 查询 订单接口dataList
	 * @param paramMap
	 * @return
	 */
	@Override
	public AjaxResponse queryOrderDataList(Map<String, Object> paramMap) {
		List<CarFactOrderInfoDTO> list = null;
		String url = orderSearchUrl+Common.ORDER_ORDER_LIST_DATE;
		try {
			String result = orderApiTemplate.postForObject(Common.ORDER_ORDER_LIST_DATE,
					String.class, paramMap);

			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用订单接口" + url + "返回结果为null");
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			if (!job.getString("code").equals("0")) {
				logger.info("调用订单接口" + url + "返回结果为result"+result);
				return AjaxResponse.failMsg(Integer.parseInt(job.getString("code")), job.getString("message"));
			}
			if (job != null) {
				if("0".equals(job.get("code").toString())){
					JSONObject jobres = JSON.parseObject(job.get("data").toString());
					if(jobres!=null){
						list =  JSONArray.parseArray(jobres.get("data").toString(), CarFactOrderInfoDTO.class);   
						jobres.remove("data");
						jobres.put("data", list);
						list.forEach( carFactOrderInfoDTO -> {
							carFactOrderInfoDTO.setBookingUserPhone(CommonStringUtils.protectPhoneInfo(carFactOrderInfoDTO.getBookingUserPhone()));
							carFactOrderInfoDTO.setRiderPhone(CommonStringUtils.protectPhoneInfo(carFactOrderInfoDTO.getRiderPhone()));
						});
						return AjaxResponse.success(jobres);
					}
				}
			}
		} catch (Exception e) {
			logger.error("调用订单接口" + url + "异常", e);
			e.printStackTrace();
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
	}

	
	@Override
	public CarBizOrderSettleEntity selectDriverSettleByOrderId(Long orderId) {
		return carFactOrderExMapper.selectDriverSettleByOrderId(orderId);
	}

	@Override
	public CarGroupEntity selectCarGroupById(Integer id) {
		return carFactOrderExMapper.selectCarGroupById(id);
	}
	
	@Override
	public List<CarFactOrderInfo> selectByListPrimaryKey(Long orderId) {
		return this.carFactOrderExMapper.selectByListPrimaryKey(orderId);
	}

	@Override
	public CarBizPlanEntity selectByOrderNo(Map<String, Object> map) {
		return carFactOrderExMapper.selectByOrderNo(map);
	}

	@Override
	public List<OrderTimeEntity> queryDriverOrderRecord(Map<String, String> p) {
		return driverOrderRecordMapper.queryDriverOrderRecord(p);
	}

	@Override
	public List<CarBizOrderWaitingPeriod> selectWaitingPeriodListSlave(String orderNo) {
		return carFactOrderExMapper.selectWaitingPeriodListSlave(orderNo);
	}

	@Override
	public CarPoolMainOrderDTO queryCarpoolMainForObject(CarPoolMainOrderDTO params) {
		return poolMainOrderMapper.queryCarpoolMainForObject(params);
	}

	@Override
	public CarBizDriverInfoDTO querySupplierIdAndNameByDriverId(Integer params) {
		return carBizDriverInfoExMapper.querySupplierIdAndNameByDriverId(params);
	}

	@Override
	public CarBizSupplier queryCarBizSupplier(CarBizSupplier carBizSupplier) {
		return carBizSupplierExMapper.queryForObject(carBizSupplier);
	}

	@Override
	public CarBizCity queryCarBizCityById(CarBizCity params) {
		return carBizCityExMapper.queryCarBizCityById(params);
	}

	@Override
	public String serviceTypeName(Integer serviceId) {
		ServiceEntity serviceEntity = carFactOrderExMapper.selectServiceEntityById(serviceId);
		String serviceTypeName = "";
		if(serviceEntity!=null){
			serviceTypeName = serviceEntity.getServiceName();
		}
		return serviceTypeName;
	}

	@Override
	public String getGroupNameByGroupId(Integer groupId) {
		return carBizCarGroupExMapper.getGroupNameByGroupId(groupId);
	}

	@Override
	public String selectModelNameByLicensePlates(String licensePlates) {
		return carFactOrderExMapper.selectModelNameByLicensePlates(licensePlates);
	}

	@Override
	public List<ServiceTypeDTO> selectServiceEntityList(ServiceEntity serviceEntity) {
		return carFactOrderExMapper.selectServiceEntityList(serviceEntity);
	}


}
