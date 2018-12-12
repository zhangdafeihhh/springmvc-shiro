package com.zhuanche.serv.busManage;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.zhuanche.entity.rentcar.CarBizCarGroup;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.mongo.BusDriverMongoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.MapUrlParamUtils;
import com.zhuanche.util.MyRestTemplate;
import com.zhuanche.util.SignUtils;
import com.zhuanche.vo.busManage.BusOrderVO;

import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;

@Service("busAssignmentService")
public class BusAssignmentService {
	
	private static final Logger logger = LoggerFactory.getLogger(BusAssignmentService.class);
	
	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;
	
	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;
	
	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;
	
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
			if (supplierId == null && authOfSupplier != null) {
				int size = authOfSupplier.size();
				if (size == 1) {
					authOfSupplier.stream().findFirst().ifPresent(params::setSupplierId);
				} else {
					supplierIds = StringUtils.join(authOfSupplier, ",");
				}
			}
			
			// 请求参数
			Map<String, Object> paramMap = new HashMap<String, Object>();
			BeanUtil.transBean2Map(params, paramMap);
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
				paramMap.put("driverIds", StringUtils.join(driverIds, ","));// TODO 确定字段key
			}
			// 补充权限控制
			paramMap.put("cityIds", cityIds);// TODO 确定字段key
			paramMap.put("supplierIds", supplierIds);// TODO 确定字段key
			
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
			// a)司机姓名/司机手机号/本单司机评分 TODO
			
			// b)预约车别类型
			List<CarBizCarGroup> groupList = carBizCarGroupExMapper.queryCarGroupList(2);
			Map<String, String> groupMap = new HashMap<>();
			groupList.forEach(group -> groupMap.put(String.valueOf(group.getGroupId()), group.getGroupName()));
			orderList.forEach(order -> order.setBookingGroupName(groupMap.get(order.getBookingGroupid())));
			// c)预估里程 TODO
//			String url = chargeBaseUrl + BusConst.Charge.BUSS_GETBUSCOSTDETAILLIST;
			// d)企业名称/企业折扣/付款类型 TODO 
//			String url = businessRestBaseUrl + BusConst.BusinessRest.COMPANY_QUERYCOMPANYBYPHONE;
//			String url = paymentBaseUrl + BusConst.Payment.BUSINESS_QUERYBUSINESSINFOBATCH;
			
			
			return new PageDTO(params.getPageNum(), params.getPageSize(), total, orderList);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-selectList ] 查询巴士订单列表出错 {}", e);
		}
		return new PageDTO();
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
					String score = busCarBizCustomerAppraisalStatisticsService.getScore((Integer) driver.get("driverId"), LocalDate.now());
					driver.put("monthlyScore", score);
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
