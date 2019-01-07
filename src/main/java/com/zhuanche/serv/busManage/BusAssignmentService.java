package com.zhuanche.serv.busManage;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.constants.BusConst;
import com.zhuanche.dto.busManage.BusCarDTO;
import com.zhuanche.dto.busManage.BusCarRicherDTO;
import com.zhuanche.dto.busManage.BusDriverDTO;
import com.zhuanche.dto.busManage.BusDriverRicherDTO;
import com.zhuanche.dto.busManage.BusOrderDTO;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisal;
import com.zhuanche.entity.rentcar.CarBizCustomerAppraisalStatistics;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizService;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.mongo.BusDriverMongoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MapUrlParamUtils;
import com.zhuanche.util.MyRestTemplate;
import com.zhuanche.util.SignUtils;
import com.zhuanche.vo.busManage.BusOrderVO;

import mapper.rentcar.CarBizServiceMapper;
import mapper.rentcar.ex.BusCarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;

@Service("busAssignmentService")
public class BusAssignmentService {
	
	private static final Logger logger = LoggerFactory.getLogger(BusAssignmentService.class);
	
	@Autowired
	private CarBizServiceMapper carBizServiceMapper;
	
	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;
	
	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;
	
	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;
	
	@Autowired
	private BusCarBizDriverInfoExMapper busCarBizDriverInfoExMapper;
	
	@Autowired
	private BusCarBizCustomerAppraisalService busCarBizCustomerAppraisalService;
	
	@Autowired
	private BusCarBizCustomerAppraisalStatisticsService busCarBizCustomerAppraisalStatisticsService;
	
	@Autowired
	private BusDriverMongoService busDriverMongoService;
	
	@Autowired
	@Qualifier("busAssignmentTemplate")
	private MyRestTemplate busAssignmentTemplate;
	
	@Autowired
	@Qualifier("carRestTemplate")
	private MyRestTemplate carRestTemplate;
	
	@Value("${bus.order.cost.url}")
	private String chargeBaseUrl;
	
	@Value("${business.url}")
	private String businessRestBaseUrl;
	
	@Value("${order.pay.old.url}")
	private String paymentBaseUrl;

	/**
	 * @Title: selectList
	 * @Description: 查询巴士订单列表
	 * @param params
	 * @return BaseEntity
	 * @throws
	 */
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public PageDTO selectList(BusOrderDTO params) {
		try {
			// 数据权限控制SSOLoginUser
			String cityIds = null;
			String supplierIds = null;
			Integer cityId = params.getCityId();
			Integer supplierId = params.getSupplierId();
			Set<Integer> authOfCity = WebSessionUtil.getCurrentLoginUser().getCityIds(); // 普通管理员可以管理的所有城市ID
			Set<Integer> authOfSupplier = WebSessionUtil.getCurrentLoginUser().getSupplierIds(); // 普通管理员可以管理的所有供应商ID			
			if (cityId == null && authOfCity != null) {
				int size = authOfCity.size();
				if (size == 1) {
					authOfCity.stream().findFirst().ifPresent(params::setCityId);
				} else {
					cityIds = StringUtils.join(authOfCity, ",");
				}
			}
			if (Integer.valueOf(10103).equals(params.getStatus())) {// 指派列表不限制供应商
				params.setSupplierId(null);
				supplierIds = null;
			} else if (supplierId == null && authOfSupplier != null) {
				int size = authOfSupplier.size();
				if (size == 1) {
					authOfSupplier.stream().findFirst().ifPresent(params::setSupplierId);
				} else {
					supplierIds = StringUtils.join(authOfSupplier, ",");
				}
			}
			
			// 请求参数
			String jsonString = JSON.toJSONStringWithDateFormat(params, JSON.DEFFAULT_DATE_FORMAT);
			JSONObject json = (JSONObject) JSONObject.parse(jsonString);
			Map<String, Object> paramMap = json.getInnerMap();
			
			// 补充司机信息
			Set<Integer> driverIds = new HashSet<>();
			if (StringUtils.isNotBlank(params.getDriverName())) {
				List<DriverMongo> driversByName = busDriverMongoService.queryDriverByName(params.getDriverName());
				if (driversByName != null) {
					driverIds.addAll(driversByName.stream().map(driver -> driver.getDriverId()).collect(Collectors.toList()));
				} 
			}
			if (StringUtils.isNotBlank(params.getDriverPhone())) {
				List<DriverMongo> driversByPhone = busDriverMongoService.queryDriverByPhone(params.getDriverPhone());
				if (driversByPhone != null) {
					driverIds.addAll(driversByPhone.stream().map(driver -> driver.getDriverId()).collect(Collectors.toList()));
				} 
			}
			if (driverIds.size() == 1) {
				driverIds.stream().findFirst().ifPresent(driverId -> paramMap.put("driverId", driverId));
			} else {
				String driverIdsStr = StringUtils.join(driverIds, ",");
				paramMap.put("driverIds", StringUtils.isBlank(driverIdsStr) ? null : driverIdsStr);
			}
			// 补充权限控制
			paramMap.put("cityIds", StringUtils.isBlank(cityIds) ? null : cityIds);
			paramMap.put("supplierIds", StringUtils.isBlank(supplierIds) ? null : supplierIds);
			
			// 签名
			paramMap.put("businessId", Common.BUSINESSID);
			String sign = SignUtils.createMD5Sign(paramMap, Common.KEY);
			logger.info("[ BusAssignmentService-selectList ] 请求参数签名   sign={}", sign);
			paramMap.put("sign", sign);
			
			logger.info("[ BusAssignmentService-selectList ] 请求参数   paramMap={}", paramMap);
			
			// 请求接口
			String response = carRestTemplate.postForObject(BusConst.Order.SELECT_ORDER_LIST, JSONObject.class, paramMap);
			JSONObject result = JSON.parseObject(response);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			
			// 接口错误
			if (code == 1) {
				logger.info("[ BusAssignmentService-selectList ] 查询巴士订单列表出错, 错误码:{}, 错误原因:{}", code, msg);
				return new PageDTO();
			}
			
			// 返回数据
			JSONObject data = result.getJSONObject("data");
			JSONArray dataList = data.getJSONArray("dataList");
			int total = data.getIntValue("totalCount");
			List<BusOrderVO> orderList = JSON.parseArray(dataList.toString(), BusOrderVO.class);
			if (orderList != null && orderList.size() > 0) {
				orderList.forEach(order -> {
					// a)司机姓名/司机手机号
					if (order.getDriverId() != null) {
						CarBizDriverInfo driver = busCarBizDriverInfoExMapper.queryDriverSimpleInfoById(order.getDriverId());
						if (driver != null) {
							order.setDriverName(driver.getName());
							order.setDriverPhone(driver.getPhone());
						}
					}
					// b)本单司机评分
					if (StringUtils.isNotBlank(order.getOrderNo())) {
						CarBizCustomerAppraisal appraisal = busCarBizCustomerAppraisalService.queryAppraisal(order.getOrderNo());
						if (appraisal != null) {
							order.setDriverScore(appraisal.getEvaluateScore());
						}
					}
					// c)预约车别类型
					if (StringUtils.isNotBlank(order.getBookingGroupid())) {
						order.setBookingGroupName(carBizCarGroupExMapper.getGroupNameByGroupId(Integer.valueOf(order.getBookingGroupid())));
					}
					
					// d)订单类型名称
					if (order.getServiceTypeId() != null) {
						CarBizService service = carBizServiceMapper.selectByPrimaryKey(order.getServiceTypeId());
						if (service != null) {
							order.setServiceTypeName(service.getServiceName());
						}
					}
				});
				// d)预估里程
				String orderNos = orderList.stream().map(order -> order.getOrderNo()).collect(Collectors.joining(","));
				JSONArray busCostDetailList = getBusCostDetailList(orderNos);
				if (busCostDetailList != null) {
					orderList.forEach(order -> {
						String orderNo = order.getOrderNo();
						if (StringUtils.isNotBlank(orderNo)) {
							busCostDetailList.stream().filter(cost -> {
								JSONObject jsonObject = (JSONObject) JSON.toJSON(cost);
								return orderNo.equals(jsonObject.getString("orderNo"));
							}).findFirst().ifPresent(cost -> {
								JSONObject jsonObject = (JSONObject) JSON.toJSON(cost);
								order.setEstimateDistance(jsonObject.getDouble("estimateDistance"));
							});
						}
					});
				}
				// e)企业名称/企业折扣/付款类型
				List<Object> phoneList = new ArrayList<>();
				orderList.forEach(order -> {
					String bookingUserPhone = order.getBookingUserPhone();
					if (StringUtils.isNotBlank(bookingUserPhone)) {
						phoneList.add(bookingUserPhone);
					}
				});
				String phones = StringUtils.join(phoneList, ",");
				JSONArray queryBatchOrgInfo = queryCompanyByPhone(phones);
				if (queryBatchOrgInfo != null) {
					// 匹配企业ID/企业名称
					orderList.forEach(order -> {
						String bookingUserPhone = order.getBookingUserPhone();
						if (StringUtils.isNotBlank(bookingUserPhone)) {
							queryBatchOrgInfo.stream().filter(company -> {
								JSONObject jsonObject = (JSONObject) JSON.toJSON(company);
								return bookingUserPhone.equals(jsonObject.getString("phone"));
							}).findFirst().ifPresent(company -> {
								JSONObject jsonObject = (JSONObject) JSON.toJSON(company);
								order.setCompanyId(jsonObject.getInteger("companyId"));
								order.setCompanyName(jsonObject.getString("companyName"));
							});
						}
					});
					
					// 付款类型/企业折扣
					List<String> companyIdList = new ArrayList<>();
					orderList.forEach(order -> {
						Integer companyId = order.getCompanyId();
						if (companyId != null) {
							companyIdList.add(companyId.toString());
						}
					});
					String companyIds = StringUtils.join(companyIdList, ",");
					JSONArray queryBusinessInfoBatch = queryBusinessInfoBatch(companyIds);
					if (queryBusinessInfoBatch != null) {
						orderList.forEach(order -> {
							Integer companyId = order.getCompanyId();
							if (companyId != null) {
								queryBusinessInfoBatch.stream().filter(business -> {
									JSONObject jsonObject = (JSONObject) JSON.toJSON(business);
									return companyId.toString().equals(jsonObject.getString("businessId"));
								}).findFirst().ifPresent(business -> {
									JSONObject jsonObject = (JSONObject) JSON.toJSON(business);
									order.setCompanyType(jsonObject.getInteger("type"));
									order.setPercent(jsonObject.getInteger("percent"));
								});
							}
						});
					}
				}
			}
			
			// 返回结果
			return new PageDTO(params.getPageNum(), params.getPageSize(), total, orderList);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-selectList ] 查询巴士订单列表出错 {}", e);
		}
		return new PageDTO();
	}
	
	/**
	 * @Title: getBusCostDetailList
	 * @Description: 批量获取费用明细
	 * @param orderNos
	 * @return 
	 * @return JSONArray
	 * @throws
	 */
	public JSONArray getBusCostDetailList(String orderNos) {
		Map<String,Object> params = new HashMap<>();
		params.put("orderNos", orderNos);
		logger.info("[ BusAssignmentService-getBusCostDetailList ] 大巴车-批量获取费用明细,请求参数,params={}", params);
		try {
			String url = chargeBaseUrl + BusConst.Charge.BUSS_GETBUSCOSTDETAILLIST;
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 3000, "大巴车-批量获取费用明细");
			if (result.getIntValue("code") != 0) {
				logger.info("[ BusAssignmentService-getBusCostDetailList ] 大巴车-批量获取费用明细,调用接口出错 code:{},错误原因:{}",
						result.getIntValue("code"), result.getString("msg"));
				return null;
			}
			JSONArray jsonArray = result.getJSONArray("data");
			return jsonArray;
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-getBusCostDetailList ] 大巴车-批量获取费用明细,调用接口异常errorMsg={}", e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * @Title: queryBatchOrgInfo
	 * @Description: 根据手机号查询企业信息 
	 * @param phones
	 * @return 
	 * @return JSONArray
	 * @throws
	 */
	public JSONArray queryCompanyByPhone(String phones) {
        try {
        	Map<String,Object> params = new HashMap<>();
    		params.put("phone", phones);
    		logger.info("[ BusAssignmentService-queryCompanyByPhone ] 根据手机号查询企业信息,请求参数,params={}", params);
    		
        	String url = businessRestBaseUrl + BusConst.BusinessRest.COMPANY_QUERYCOMPANYBYPHONE;
        	JSONObject result = MpOkHttpUtil.okHttpGetBackJson(url, params, 3000, "根据手机号查询企业信息");
            int code = result.getIntValue("code");
            String msg = result.getString("msg");
			if (code != 0) {
                logger.error("[ BusAssignmentService-queryCompanyByPhone ] 根据手机号查询企业信息,调用接口错误,code:{},错误原因:{}", code, msg);
                return null;
            }
            JSONArray jsonArray = result.getJSONArray("data");
            return jsonArray;
        } catch (Exception e) {
			logger.error("[ BusAssignmentService-queryCompanyByPhone ] 根据手机号查询企业信息,订单接口异常,{}", e.getMessage(), e);
            return null;
        }
    }
	
	/**
	 * @Title: queryBusinessInfoBatch
	 * @Description: 批量查询企业信息
	 * @param companyIds
	 * @return 
	 * @return JSONArray
	 * @throws
	 */
	public JSONArray queryBusinessInfoBatch(String companyIds) {
		Map<String, Object> params = new HashMap<>();
		params.put("businessIdStr", companyIds);
		logger.info("[ BusAssignmentService-queryBusinessInfoBatch ] 批量查询企业信息,请求参数,params={}", params);
		try {
			String url = paymentBaseUrl + BusConst.Payment.BUSINESS_QUERYBUSINESSINFOBATCH;
			JSONObject result = MpOkHttpUtil.okHttpPostBackJson(url, params, 3000, "批量查询企业信息");
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			if (code != 0) {
				logger.error("[ BusAssignmentService-queryBusinessInfoBatch ] 批量查询企业信息,错误码:{},错误原因:{}", code, msg);
				return null;
			}
			JSONArray jsonArray = result.getJSONArray("data");
			return jsonArray;
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-queryBusinessInfoBatch ] 批量查询企业信息,调用接口异常errorMsg={}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @Title: orderToDoListForCar
	 * @Description: 根据订单编号调取订单服务获取当前可派单车辆
	 * @param busCarDTO
	 * @return BaseEntity
	 * @throws
	 */
	@SuppressWarnings({ "resource" })
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public PageDTO orderToDoListForCar(BusCarDTO busCarDTO) {
		
		// 参数
		Map<String, Object> param = new HashMap<>();
		param.put("orderNo", busCarDTO.getOrderNo());
		param.put("businessId", Common.BUSINESSID);
		param.put("type", busCarDTO.getType());
		param.put("sign", SignUtils.createMD5Sign(param, Common.KEY));// 签名
		logger.info("[ BusAssignmentService-orderToDoListForCar ] 请求参数   param={}", param);
		
		try {
			// 请求接口
			String urlParam = BusConst.Order.BUS_IN_SERVICE_LIST + "?" + MapUrlParamUtils.getUrlParamsByMap(param);
			logger.info("[ BusAssignmentService-orderToDoListForCar ] urlParam:{}", urlParam);
			
			JSONObject result = carRestTemplate.getForObject(urlParam, JSONObject.class);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			
			// 接口异常
			if (code == 1) {
				logger.info("[ BusAssignmentService-orderToDoListForCar ] 查询不可派单车辆列表出错, 错误码:{}, 错误原因:{}", code, msg);
				return new PageDTO();
			}

			// 返回数据
			JSONArray data = result.getJSONArray("data");
			List<String> invalidLicensePlatesList = new ArrayList<>();
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					JSONObject bean = data.getJSONObject(i);
					invalidLicensePlatesList.add((String) bean.get("licensePlates"));
				}
			}
			logger.info("[ BusAssignmentService-orderToDoListForCar ] 当前不可以指派车辆车牌号:{}", invalidLicensePlatesList);
			
			// 封装查询参数, 查询可用车辆
			BusCarRicherDTO richerDTO = BeanUtil.copyObject(busCarDTO, BusCarRicherDTO.class);
			// 扩大车型查询范围
			if (busCarDTO.getGroupId() != null) {
				int seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(busCarDTO.getGroupId());
				richerDTO.setSeatNum(seatNum);
			}
			// 不可用车辆
			if (!invalidLicensePlatesList.isEmpty()) {
				richerDTO.setInvalidLicensePlatesList(invalidLicensePlatesList);
			}
			// 当然用户的供应商权限
			richerDTO.setSupplierIds(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
			List<Map<String, Object>> busCarList = carBizCarInfoExMapper.queryBusCarList(richerDTO);
			Page<Map<String, Object>> page = (Page<Map<String, Object>>) busCarList;
			return new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), busCarList);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-orderToDoListForCar ] 获取不可指派司机错误", e);
		}
		return new PageDTO();
	}
	
	/**
	 * @Title: orderToDoListForDriver
	 * @Description: 根据订单编号调取订单服务获取当前可派单司机
	 * @param busDriverDTO
	 * @return BaseEntity
	 * @throws
	 */
	@SuppressWarnings("resource")
	@MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
	public PageDTO orderToDoListForDriver(BusDriverDTO busDriverDTO) {

		// 参数
		Map<String, Object> param = new HashMap<>();
		param.put("orderNo", busDriverDTO.getOrderNo());
		param.put("businessId", Common.BUSINESSID);
		param.put("type", busDriverDTO.getType());
		param.put("sign", SignUtils.createMD5Sign(param, Common.KEY));// 签名
		logger.info("[ BusAssignmentService-orderToDoListForDriver ] 请求参数   param={}", param);

		try {
			// 请求接口
			String urlParam = BusConst.Order.BUS_IN_SERVICE_LIST + "?" + MapUrlParamUtils.getUrlParamsByMap(param);
			logger.info("[ BusAssignmentService-orderToDoListForDriver ] urlParam:{}", urlParam);

			JSONObject result = carRestTemplate.getForObject(urlParam, JSONObject.class);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");

			// 接口异常
			if (code == 1) {
				logger.info("[ BusAssignmentService-orderToDoListForDriver ] 查询不可派单司机列表出错, 错误码:{}, 错误原因:{}", code, msg);
				return new PageDTO();
			}

			// 返回数据
			JSONArray data = result.getJSONArray("data");
			List<Integer> invalidDriverIds = new ArrayList<>();
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					JSONObject bean = data.getJSONObject(i);
					invalidDriverIds.add((Integer) bean.get("driverId"));
				}
			}
			logger.info("[ BusAssignmentService-orderToDoListForDriver ] 当前不可以指派司机ID:{}", invalidDriverIds);

			// 封装查询参数, 查询可用司机
			BusDriverRicherDTO richerDTO = BeanUtil.copyObject(busDriverDTO, BusDriverRicherDTO.class);
			// 扩大车型查询范围
			if (busDriverDTO.getGroupId() != null) {
				int seatNum = carBizCarGroupExMapper.getSeatNumByGroupId(busDriverDTO.getGroupId());
				richerDTO.setSeatNum(seatNum);
			}
			// 不可用司机
			if (!invalidDriverIds.isEmpty()) {
				richerDTO.setInvalidDriverIds(invalidDriverIds);
			}
			// 当然用户的供应商权限
			richerDTO.setSupplierIds(WebSessionUtil.getCurrentLoginUser().getSupplierIds());
			List<Map<String, Object>> busDriverList = carBizDriverInfoExMapper.queryBusDriverList(richerDTO);
			// 补充司机评分
			if (busDriverList != null && busDriverList.size() > 0) {
				busDriverList.forEach(driver -> {
					CarBizCustomerAppraisalStatistics appraisal = busCarBizCustomerAppraisalStatisticsService
							.queryAppraisal((Integer) driver.get("driverId"), LocalDate.now());
					if (appraisal != null) {
						driver.put("monthlyScore", appraisal.getEvaluateScore());
					}
				});
			}
			Page<Map<String, Object>> page = (Page<Map<String, Object>>) busDriverList;
			return new PageDTO(page.getPageNum(), page.getPageSize(), page.getTotal(), busDriverList);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-orderToDoListForDriver ] 获取不可指派司机错误", e);
		}
		return new PageDTO();
	}
	
	
	
	/**
	 * @Title: busDispatcher
	 * @Description: 指派巴士订单
	 * @param cityName
	 * @param driverId
	 * @param driverName
	 * @param driverPhone
	 * @param dispatcherPhone
	 * @param groupId
	 * @param groupName
	 * @param licensePlates
	 * @param orderId
	 * @param orderNo
	 * @param serviceTypeId
	 * @return JSONObject
	 * @throws
	 */
	public JSONObject busDispatcher(String cityName, Integer driverId, String driverName, String driverPhone,
			String dispatcherPhone, Integer groupId, String groupName, String licensePlates,
			Integer orderId, String orderNo, Integer serviceTypeId) {

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderNo", orderNo);
			paramMap.put("orderId", orderId);
			paramMap.put("driverId", driverId);
			paramMap.put("driverPhone", driverPhone);
			paramMap.put("driverName", driverName);
			paramMap.put("licensePlates", licensePlates);
			paramMap.put("groupId", groupId);
			paramMap.put("groupName", groupName);
			paramMap.put("serviceTypeId", serviceTypeId);
			paramMap.put("cityName", cityName);
			paramMap.put("yardmanPhone", dispatcherPhone);
			logger.info("[ BusAssignmentService-busDispatcher ] 接口参数：{}", paramMap);
			String response = busAssignmentTemplate.postForObject(BusConst.Dispatcher.BUS_DISPATCHER, JSONObject.class, paramMap);

			return JSON.parseObject(response);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-busDispatcher ] 巴士派单失败.", e);
		}
		return null;
	}
	
	/**
	 * @Title: updateDriver
	 * @Description: 指派巴士订单
	 * @param cityName
	 * @param driverId
	 * @param driverName
	 * @param driverPhone
	 * @param groupId
	 * @param groupName
	 * @param licensePlates
	 * @param orderId
	 * @param orderNo
	 * @param serviceTypeId
	 * @return JSONObject
	 * @throws
	 */
	public JSONObject updateDriver(String cityName, Integer driverId, String driverName, String driverPhone,
			Integer groupId, String groupName, String licensePlates, Integer orderId, String orderNo,
			Integer serviceTypeId) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String key = Common.MAIN_ORDER_KEY; // 请求key
			paramMap.put("orderNo", orderNo);
			paramMap.put("businessId", Common.BUSSINESSID);
			paramMap.put("driverId", driverId);
			paramMap.put("licensePlates", licensePlates);
			paramMap.put("carGroupId", groupId);
			paramMap.put("carGroupName", groupName);
			paramMap.put("sign", SignUtils.createMD5Sign(paramMap, key));
			logger.info("[ BusAssignmentService-updateDriver ] 接口参数：{}", paramMap);
			String response = carRestTemplate.postForObject(BusConst.Order.UPDATE_DRIVER, JSONObject.class, paramMap);
			return JSON.parseObject(response);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-updateDriver ] 巴士改派失败", e);
		}
		return null;
	}
	
}
