package com.zhuanche.serv.busManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.dto.busManage.BusCarDTO;
import com.zhuanche.dto.busManage.BusCarRicherDTO;
import com.zhuanche.dto.busManage.BusDriverDTO;
import com.zhuanche.dto.busManage.BusDriverRicherDTO;
import com.zhuanche.dto.busManage.BusOrderDTO;
import com.zhuanche.entity.rentcar.CarBizCarGroup;
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
	
	private static String SELECT_ORDER_LIST = "/busOrder/selectOrderList";
	
	private static String BUS_IN_SERVICE_LIST = "/busInService/list";

	private static String BUS_DISPATCHER = "/bus/busDispatcher";
	
	private static String UPDATE_DRIVER = "/busOrder/updateDriver";

	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;
	
	@Autowired
	private CarBizDriverInfoExMapper carBizDriverInfoExMapper;
	
	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;

	@Autowired
	@Qualifier("busAssignmentTemplate")
	private MyRestTemplate busAssignmentTemplate;

	@Autowired
	@Qualifier("carRestTemplate")
	private MyRestTemplate carRestTemplate;

	/**
	 * @Title: selectList
	 * @Description: 查询巴士订单列表
	 * @param params
	 * @return BaseEntity
	 * @throws
	 */
	public PageDTO selectList(BusOrderDTO params) {
		try {
			// 请求参数
			Map<String, Object> paramMap = new HashMap<String, Object>();
			BeanUtil.transBean2Map(params, paramMap);
			paramMap.put("businessId", Common.BUSINESSID);
			
			// 签名
			String sign = SignUtils.createMD5Sign(paramMap, Common.KEY);
			logger.info("[ BusAssignmentService-selectList ] 请求参数签名   sign={}", sign);
			paramMap.put("sign", sign);
			
			logger.info("[ BusAssignmentService-selectList ] 请求参数   paramMap={}", paramMap);
			
			// 请求接口
			String response = carRestTemplate.postForObject(SELECT_ORDER_LIST, JSONObject.class, paramMap);
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
			// 查询所有车别类型
			List<CarBizCarGroup> groupList = carBizCarGroupExMapper.queryCarGroupList(2);
			Map<String, String> groupMap = new HashMap<>();
			groupList.forEach(group -> groupMap.put(String.valueOf(group.getGroupId()), group.getGroupName()));
			// 转换车别类型
			orderList.forEach(order -> order.setBookingGroupName(groupMap.get(order.getBookingGroupid())));
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
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
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
			String urlParam = BUS_IN_SERVICE_LIST + "?" + MapUrlParamUtils.getUrlParamsByMap(param);
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
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
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
			String urlParam = BUS_IN_SERVICE_LIST + "?" + MapUrlParamUtils.getUrlParamsByMap(param);
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
			String response = busAssignmentTemplate.postForObject(BUS_DISPATCHER, JSONObject.class, paramMap);

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
			String response = carRestTemplate.postForObject(UPDATE_DRIVER, JSONObject.class, paramMap);
			return JSON.parseObject(response);
		} catch (Exception e) {
			logger.error("[ BusAssignmentService-updateDriver ] 巴士改派失败", e);
		}
		return null;
	}
	
}
