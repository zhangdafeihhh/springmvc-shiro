package com.zhuanche.serv.busManage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.BaseDTO;
import com.zhuanche.entity.busManage.*;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizService;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.vo.busManage.BusOrderHomepageVO;
import mapper.rentcar.ex.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
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
	private BusCarBizSupplierExMapper busCarBizSupplierExMapper;
	@Autowired
	private BusCarBizDriverInfoExMapper busCarBizDriverInfoExMapper;
	@Autowired
	private BusCarBizServiceExMapper busCarBizServiceExMapper;

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
				// 查询预定车型类别名称
				String bookingGroupid = entity.getBookingGroupid();
				if (StringUtils.isNotBlank(bookingGroupid)) {
					String bookingGroupName = carBizCarGroupExMapper.getGroupNameByGroupId(Integer.valueOf(bookingGroupid));
					entity.setBookingGroupName(bookingGroupName);
				}
				//查询实际指派车辆车型类别名
				Integer carGroupId=entity.getCarGroupId();
				if(carGroupId!=null){
					String carGroupName = carBizCarGroupExMapper.getGroupNameByGroupId(carGroupId);
					entity.setCarGroupName(carGroupName);
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

	/**  return new PageDTO(params.getPageNum(), params.getPageSize(), total, orderList);
	 * 根据用户数据权限等级查询订单的基本信息列表
	 */
	public AjaxResponse queryUpcomingOrder(BaseDTO pageDTO) {
		try {
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
			//权限级别
			Integer levelCode = currentLoginUser.getLevel();
			PermissionLevelEnum enumByCode = PermissionLevelEnum.getEnumByCode(levelCode);
			Map<String, Object> param = new TreeMap<>();
			//首页只显示待服务的订单
			param.put("businessId",Common.BUSINESSID);
			param.put("status", 10105);
			param.put("pageNum",pageDTO.getPageNum());
			param.put("pageSize",pageDTO.getPageSize());
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			param.put("bookingDateBeginStr", now.format(dateTimeFormatter));
			LocalDateTime localDateTime = now.plusHours(72);
			param.put("bookingDateEndStr", localDateTime.format(dateTimeFormatter));
			switch (enumByCode) {
                case ALL:
                    break;
                case CITY:
                    //查询权限城市下的所有供应商ID
                    Set<Integer> cityIds = currentLoginUser.getCityIds();
                    Map<String, Set<Integer>> cityIdsMap = new HashMap<>();
                    cityIdsMap.put("cityIds", cityIds);
                    List<Integer> supplierIds = busCarBizSupplierExMapper.querySupplierIdByCitys(cityIdsMap);
                    String supplierStr = supplierIds.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(","));
                    param.put("supplierIds", supplierStr);
                    break;
                case SUPPLIER:
                    Set<Integer> userSupplierSet = currentLoginUser.getSupplierIds();
                    String collect = userSupplierSet.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(","));
                    param.put("supplierIds", collect);
                    break;
                default:
                	logger.error("[巴士saas首页查询列表] 权限错误 user="+JSON.toJSONString(currentLoginUser));
                    return AjaxResponse.fail(RestErrorCode.HTTP_UNAUTHORIZED);
            }
			String sign = SignUtils.createMD5Sign(param, Common.KEY);
			// 签名
			param.put("sign", sign);
			String response_txt = carRestTemplate.postForObject(BusConst.Order.SELECT_ORDER_LIST, JSONObject.class, param);
			if (response_txt == null) {
				logger.error("[巴士saas首页查询列表],订单接口没有响应值");
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
			JSONObject response = JSONObject.parseObject(response_txt);
			if (response == null || response.getInteger("code") != 0 || response.get("data") == null) {
				logger.error("[巴士saas首页查询列表],订单接口返回错误："+response_txt);
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
			JSONObject data = response.getJSONObject("data");
			String dataList = data.getString("dataList");
			long total = data.getLongValue("totalCount");
			List<BusOrderHomepageVO> busOrderHomepages = JSONArray.parseArray(dataList, BusOrderHomepageVO.class);
			if(busOrderHomepages.size()==0){
                return AjaxResponse.success(new PageDTO(pageDTO.getPageNum(),pageDTO.getPageSize(),0,busOrderHomepages));
            }
			Set<Integer> serviceTypeIds = new HashSet<>();
			Set<Integer> driverIds = new HashSet<>();
			Set<Integer> bookingGroupIds = new HashSet();
			busOrderHomepages.forEach(order -> {
                if (order.getBookingGroupid() != null) {
                    bookingGroupIds.add(order.getBookingGroupid());
                }
                if (order.getServiceTypeId() != null) {
                    serviceTypeIds.add(order.getServiceTypeId());
                }
                if (order.getDriverId() != null) {
                    driverIds.add(order.getDriverId());
                }
            });

			List<CarBizCarGroup> groups = carBizCarGroupExMapper.queryCarGroupByIdSet(bookingGroupIds);
			List<Map<String, Object>> drivers = busCarBizDriverInfoExMapper.queryDriverSimpleBatch(driverIds);
			List<CarBizService> carBizServices = busCarBizServiceExMapper.queryServiceTypeByIdSet(serviceTypeIds);
			Map<Integer, CarBizCarGroup> groupMap = new HashMap<>(groups.size());
			Map<Integer, CarBizDriverInfo> driverMap = new HashMap<>(driverIds.size());
			Map<Integer, CarBizService> serviceMap = new HashMap<>(carBizServices.size());

			groups.forEach(o -> {
                groupMap.put(o.getGroupId(), o);
            });
			drivers.forEach(o -> {
                CarBizDriverInfo driverInfo = new CarBizDriverInfo();
                driverInfo.setDriverId((Integer) o.get("driverId"));
                driverInfo.setName((String) o.get("name"));
                driverInfo.setPhone((String) o.get("phone"));
                driverMap.put(driverInfo.getDriverId(), driverInfo);
            });
			carBizServices.forEach(o -> {
                serviceMap.put(o.getServiceId(), o);
            });

			List<BusOrderHomepageVO> collect = busOrderHomepages.stream().map(order -> {
				CarBizCarGroup group = groupMap.get(order.getBookingGroupid());
				CarBizService servcice = serviceMap.get(order.getServiceTypeId());
				CarBizDriverInfo driver = driverMap.get(order.getDriverId());
				if (group != null) {
					order.setBookingGroupName(group.getGroupName());
				}
				if (servcice != null) {
					order.setServiceName(servcice.getServiceName());
				}
				if (driver != null) {
					order.setDriverName(driver.getName());
					order.setDriverPhone(driver.getPhone());
				}
				return order;
			}).sorted(Comparator.comparing(BusOrderHomepageVO::getBookingDate)
					.thenComparing(Comparator.comparing(BusOrderHomepageVO::getCreateDate)))
					.collect(Collectors.toList());
			return AjaxResponse.success(new PageDTO(pageDTO.getPageNum(),pageDTO.getPageSize(),total,collect));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[巴士saas首页查询列表]，异常 e:{}",e);
			return AjaxResponse.failMsg(RestErrorCode.HTTP_SYSTEM_ERROR,"查询异常");

		}
	}
}

