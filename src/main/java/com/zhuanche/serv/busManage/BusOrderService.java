package com.zhuanche.serv.busManage;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.zhuanche.entity.busManage.*;
import com.zhuanche.http.MpOkHttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.CarDriverInfoDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.util.Common;
import com.zhuanche.util.MyRestTemplate;
import com.zhuanche.util.SignUtils;

import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizCustomerExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;

@Service("busOrderService")
public class BusOrderService {

	private static final Logger logger = LoggerFactory.getLogger(BusOrderService.class);

	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;

	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;

	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;

	@Autowired
	private CarBizCustomerExMapper carBizCustomerExMapper;

	@Autowired
	@Qualifier("carRestTemplate")
	private MyRestTemplate carRestTemplate;

	@Autowired
	@Qualifier("busOrderCostTemplate")
	private MyRestTemplate busOrderCostTemplate;

	@Autowired
	@Qualifier("orderPayOldTemplate")
	private MyRestTemplate orderPayOldTemplate;

	@Value("${mp.restapi.url}")
	private String mpReatApiUrl;

	@Value("${business.url}")
	private String businessRestBaseUrl;

	@Value("${order.pay.old.url}")
	private String paymentBaseUrl;

	/**
	 * @Title: selectOrderDetail
	 * @Description: 查询巴士订单详情
	 * @param orderNo
	 * @return BusOrderDetail
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public BusOrderDetail selectOrderDetail(String orderNo) {

		// 组装接口参数
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("businessId", Common.BUSINESSID);
		paramMap.put("orderNo", orderNo);
		String sign = SignUtils.createMD5Sign(paramMap, Common.KEY);
		paramMap.put("sign", sign);// 签名

		BusOrderDetail entity = null;
		try {
			String response = carRestTemplate.postForObject(BusConst.Order.GET_ORDER_DETAIL, JSONObject.class, paramMap);
			logger.info("[ BusOrderService-selectOrderDetail ] 订单详情调用，订单接口返回值={}", response);

			JSONObject result = JSON.parseObject(response);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code == 1) {
				logger.info("[ BusOrderService-selectOrderDetail ] 查询订单详情出错,错误码:{},错误原因:{}", code, msg);
				return new BusOrderDetail();
			}
			JSONObject jsonObject = result.getJSONObject("data");
			entity = JSONObject.toJavaObject(jsonObject, BusOrderDetail.class);

			if (entity != null) {
				// 查询司机信息， 司机姓名 电话号 车队名 供应商名
				Integer driverId = entity.getDriverId();
				if (driverId != null) {
					CarDriverInfoDTO driverInfo = carBizDriverInfoExMapper.selectDriverInfoByDriverId(driverId);
					if (driverInfo != null) {
						entity.setDriverName(driverInfo.getName());
						entity.setDriverPhone(driverInfo.getPhone());
						entity.setSupplierId(driverInfo.getSupplierId());
						entity.setSupplierName(driverInfo.getSupplierName());
						entity.setDriverCarGroupName(driverInfo.getCarGroupName());
					}

				}
				// 查询车型信息
				String licensePlates = entity.getLicensePlates();
				if (StringUtils.isNotBlank(licensePlates)) {
					CarBizCarInfoDTO carInfo = carBizCarInfoExMapper.selectModelByLicensePlates(licensePlates);
					if (carInfo != null) {
						entity.setCarModelId(carInfo.getCarModelId());
						entity.setCarModelName(carInfo.getCarModelName());
					}
				}

				// 查询取消人姓名
				Integer cancelCreateBy = entity.getCancelCreateBy();
				if (cancelCreateBy != null) {
					String customerName = carBizCustomerExMapper.selectCustomerNameById(cancelCreateBy);
					entity.setCancelName(customerName);
				}
				// 查询预定人
				Integer bookingUserId = entity.getBookingUserId();
				if (bookingUserId != null) {
					String bookingUserName = carBizCustomerExMapper.selectCustomerNameById(bookingUserId);
					entity.setBookingUserName(bookingUserName);
				}
				// 查询预定车型名称
				String bookingGroupid = entity.getBookingGroupid();
				if (StringUtils.isNotBlank(bookingGroupid)) {
					String bookingGroupName = carBizCarGroupExMapper.getGroupNameByGroupId(Integer.valueOf(bookingGroupid));
					entity.setBookingGroupName(bookingGroupName);
				}
			}
		} catch (Exception e) {
			logger.error("[ BusOrderService-selectOrderDetail ] 巴士订单详情调用订单接口异常", e);
			return new BusOrderDetail();
		}
		return entity == null ? new BusOrderDetail() : entity;
	}

	/**
	 * @Title: selectOrderCostDetail
	 * @Description: 获取巴士订单费用详情
	 * @param orderId
	 * @return BusCostDetail
	 * @throws
	 */
	public BusCostDetail selectOrderCostDetail(Integer orderId) {
		BusCostDetail entity = null;
		try {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("orderId", orderId);
			String response = busOrderCostTemplate.postForObject(BusConst.Charge.BUSS_BACK, JSONObject.class, paramMap);
			JSONObject result = JSON.parseObject(response);
			logger.info("[ BusOrderService-selectOrderCostDetail ] 查询订单详情，调用计费接口返回值={}", result);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code == 1) {
				logger.error("[ BusOrderService-selectOrderCostDetail ] 查询订单详情出错, 错误码:{}, 错误原因:{}", code, msg);
				return new BusCostDetail();
			}
			JSONObject jsonObject = result.getJSONObject("data");
			if (jsonObject != null) {
				entity = JSONObject.toJavaObject(jsonObject, BusCostDetail.class);
			}
		} catch (Exception e) {
			logger.error("[ BusOrderService-selectOrderCostDetail ] 巴士订单详情调用计费接口异常", e);
			return new BusCostDetail();
		}
		return entity == null ? new BusCostDetail() : entity;
	}

	/**
	 * @Title: selectOrderPayDetail
	 * @Description: 巴士订单预付流水
	 * @param orderNo
	 * @return BusPayDetail
	 * @throws
	 */
	public BusPayDetail selectOrderPayDetail(String orderNo) {
		try {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("tradeOrderNo", orderNo);
			String response = orderPayOldTemplate.postForObject(BusConst.Payment.BUS_PAY_DETAIL, JSONObject.class, paramMap);
			JSONObject result = JSON.parseObject(response);
			logger.info("[ BusOrderService-selectOrderPayDetail ] 订单详情，调用支付接口返回值={}", result);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code == 1) {
				logger.error("[ BusOrderService-selectOrderPayDetail ] 订单详情查询支付接口出错, 错误码:{}, 错误原因:{}", code, msg);
				return new BusPayDetail();
			}
			JSONObject jsonObject = result.getJSONObject("data");
			BusPayDetail busPayDetail = null;
			if (jsonObject != null) {
				busPayDetail = JSONObject.toJavaObject(jsonObject, BusPayDetail.class);
			}
			return busPayDetail == null ? new BusPayDetail() : busPayDetail;
		} catch (Exception e) {
			logger.error("[ BusOrderService-selectOrderPayDetail ] 巴士订单详情调用计费接口异常", e);
			return new BusPayDetail();
		}
	}


	public Appraisal selectOrderAppraisal(String orderNo) {
		Map<String, Object> paramMap = new HashMap();
		paramMap.put("orderNos", orderNo);
		String url = mpReatApiUrl+BusConst.Payment.BUS_APPRAISAL;
		try {
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url , paramMap, 2000, "巴士订单详情查询评分信息");
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code != 0) {
				logger.error("[ BusOrderService-selectOrderAppraisal ],错误码:" + code + ",错误原因:" + msg);
				return null;
			}
			JSONArray dataArray = result.getJSONArray("data");
			if (dataArray != null && dataArray.size() > 0) {
				JSONObject jsonObject = (JSONObject) dataArray.get(0);
				Appraisal appraisal = JSONObject.toJavaObject(jsonObject, Appraisal.class);
				return appraisal;
			}
			return null;
		} catch (Exception e) {
			logger.error("[ BusOrderService-selectOrderAppraisal ]订单详情查询评价接口异常，{}", e);
			return null;
		}
	}

	/*public OrgCostInfo selectOrgInfo(String phone) {
		//查询企业信息
		Map<String, Object> paramMap = new HashMap(2);
		paramMap.put("phone", phone);
		try {
			JSONObject orgResult = MpOkHttpUtil.okHttpPostBackJson(businessRestBaseUrl+BusConst.Payment.ORG_URL , paramMap, 2000, "巴士订单详情查询企业ID");
			if (orgResult.getIntValue("code") != 0) {
				logger.error("[ BusOrderService-selectOrgInfo ] 订单详情查询机构ID 接口出错,错误码:" + orgResult.getIntValue("code") + ",错误原因:" + orgResult.getString("msg"));
				return null;
			}
			logger.error("[ BusOrderService-selectOrgInfo ] 订单详情查询机构ID 结果="+JSON.toJSONString(orgResult));
			JSONObject dataJson = orgResult.getJSONObject("data");
			OrganizationInfo orgInfo = JSONObject.toJavaObject(dataJson, OrganizationInfo.class);
			//type =1 表示新机构用户
			if(orgInfo == null || orgInfo.getType() !=1){
				logger.error("[ BusOrderService-selectOrgInfo ] 订单详情查询机构ID 该用户非企业用户不再继续查询折扣信息 phone="+phone);
				return null;
			}
			Map<String,Object> param=new HashMap(2);
			param.put("businessId",orgInfo.getCompanyId());
			//查询企业折扣信息
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(paymentBaseUrl+BusConst.Payment.ORG_COST_URL , param, 2000, "巴士订单详情查询企业折扣信息");
			if(result.getIntValue("code")!=0){
				logger.error("[ BusOrderService-selectOrgInfo ] 订单详情查询查询折扣信息 接口出错,错误码:" + orgResult.getIntValue("code") + ",错误原因:" + orgResult.getString("msg"));
				return null;
			}
			JSONObject data = result.getJSONObject("data");
			JSONObject businessJson = data.getJSONObject("businessRepDTO");
			OrgCostInfo orgCostInfo = JSONObject.toJavaObject(businessJson, OrgCostInfo.class);
			return orgCostInfo;
		} catch (Exception e) {
			logger.error("[ BusOrderService-selectOrgInfo ]订单详情查询机构id接口异常，{}", e);
			return null;
		}
	}*/

	public OrgCostInfo selectOrgInfo(Integer businessId) {
			Map<String,Object> param=new HashMap(2);
			param.put("businessId",businessId);
			//查询企业折扣信息
		try{
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(paymentBaseUrl+BusConst.Payment.ORG_COST_URL , param, 2000, "巴士订单详情查询企业折扣信息");
			if(result.getIntValue("code")!=0){
				logger.error("[ BusOrderService-selectOrgInfo ] 订单详情查询折扣信息 接口出错,错误码:" + result.getIntValue("code") + ",错误原因:" + result.getString("msg"));
				return null;
			}
			JSONObject data = result.getJSONObject("data");
			JSONObject businessJson = data.getJSONObject("businessRepDTO");
			OrgCostInfo orgCostInfo = JSONObject.toJavaObject(businessJson, OrgCostInfo.class);
			return orgCostInfo;
		} catch (Exception e) {
			logger.error("[ BusOrderService-selectOrgInfo ]订单详情查询折扣信息异常，{}", e);
			return null;
		}
	}
}
