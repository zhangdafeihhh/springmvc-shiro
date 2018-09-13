package com.zhuanche.serv.rentcar.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDTO;
import com.zhuanche.dto.rentcar.CarPoolMainOrderDTO;
import com.zhuanche.entity.DriverOrderRecord.OrderTimeEntity;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizOrderSettleEntity;
import com.zhuanche.entity.rentcar.CarBizOrderWaitingPeriod;
import com.zhuanche.entity.rentcar.CarBizPlanEntity;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;
import com.zhuanche.entity.rentcar.CarGroupEntity;
import com.zhuanche.entity.rentcar.ServiceEntity;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.serv.rentcar.CarFactOrderInfoService;
import com.zhuanche.util.Common;

import mapper.driverOrderRecord.DriverOrderRecordMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCityExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;
import mapper.rentcar.ex.CarFactOrderExMapper;

@Service
public class CarFactOrderInfoServiceImpl implements CarFactOrderInfoService {
    private static Logger logger = LoggerFactory.getLogger(CarFactOrderInfoServiceImpl.class);

    //LBS轨迹URL
    @Value("${base.lbs.rest.url}")
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
    private  CarBizCarGroupExMapper carBizCarGroupExMapper;
    @Override
	public CarFactOrderInfo selectByPrimaryKey(Long orderId) {
		return carFactOrderExMapper.selectByPrimaryKey(orderId);
	}
   
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
			//System.out.println(url);
			String result = HttpClientUtil.buildGetRequest(url).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
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
			String url = carRestUrl+Common.GET_MAIN_ORDER_BY_ORDERNO + 
					"?isShowSubOrderList=0&mainOrderNo=" + mainOrderNo;
			// 参数：订单号 、业务线id
			StringBuffer param = new StringBuffer("");
			param.append("businessId=" + Common.BUSSINESSID + "&isShowSubOrderList=0&");
			param.append("mainOrderNo=" + mainOrderNo  + '&');
			param.append("key=" + Common.MAIN_ORDER_KEY);
			String sign = java.net.URLEncoder.encode(
					Base64.encodeBase64String(DigestUtils.md5(param.toString())), "UTF-8");;
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
	 * @param paramsStr
	 * @return
	 */
	@Override
	public String queryDrivingRouteData(String paramsStr) {
		String result = "";
		String url = baseLbsRestApiUrl+Common.LBS_DRIVING_ROUTE+"?"+paramsStr;
		//String url01 =  "http://pre-base-lbs.01zhuanche.com/driving/route?platform=15&startDate=1536139926000,1536139973000,1536139994000,1536140009000&orderNo=P20180905621360016X&batchNo=3&driverId=189290&endDate=1536139973000,1536139994000,1536140009000,1536140185000";
		try {
			result = HttpClientUtil.buildGetRequest(url).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
		} catch (HttpException e) {
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
				return job.getString("code");
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
	 * @param paramsStr
	 * @return
	 */
	@Override
	public AjaxResponse queryOrderDataList(Map<String, Object> paramMap) {
		List<CarFactOrderInfoDTO> list = null;
		String url = orderSearchUrl+Common.ORDER_ORDER_LIST_DATE;
		try {
			String result = HttpClientUtil.buildPostRequest(url).addParams(paramMap).addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED).execute();
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
		return carFactOrderExMapper.queryCarpoolMainForObject(params);
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
}
