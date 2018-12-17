package com.zhuanche.controller.busManage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.BaseController;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.busManage.BusCarDTO;
import com.zhuanche.dto.busManage.BusDriverDTO;
import com.zhuanche.dto.busManage.BusOrderDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.entity.busManage.BusCostDetail;
import com.zhuanche.entity.busManage.BusOrderDetail;
import com.zhuanche.entity.busManage.BusPayDetail;
import com.zhuanche.entity.mdbcarmanage.BusOrderOperationTime;
import com.zhuanche.entity.mdbcarmanage.CarBizOrderMessageTask;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.busManage.BusAssignmentService;
import com.zhuanche.serv.busManage.BusOrderService;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;

import mapper.mdbcarmanage.BusOrderOperationTimeMapper;
import mapper.mdbcarmanage.CarBizOrderMessageTaskMapper;
import mapper.mdbcarmanage.ex.BusOrderOperationTimeExMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import mapper.rentcar.ex.CarBizSupplierExMapper;

@Controller("busAssignmentController")
@RequestMapping(value = "/busAssignment")
public class BusAssignmentController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BusAssignmentController.class);

	@Autowired
	private CarBizDriverInfoMapper carBizDriverInfoMapper;

	@Autowired
	private CarBizCarInfoExMapper carBizCarInfoExMapper;

	@Autowired
	private CarBizSupplierExMapper carBizSupplierExMapper;

	@Autowired
	private CarBizCarGroupExMapper carBizCarGroupExMapper;

	@Autowired
	private CarBizOrderMessageTaskMapper carBizOrderMessageTaskMapper;

	@Autowired
	private BusOrderOperationTimeMapper busOrderOperationTimeMapper;

	@Autowired
	private BusOrderOperationTimeExMapper busOrderOperationTimeExMapper;

	@Autowired
	private BusAssignmentService busAssignmentService;

	@Autowired
	private BusOrderService busOrderService;

	public interface OrderOperation {
		/**
		 * 状态 1操作成功 2操作失败
		 */
		int SUCCESS_STATUS = 1;
		int FAIL_STATUS = 2;
		/**
		 * 类型 指派 1订单指派 2订单改派
		 */
		int ASSIGN_TYPE = 1;
		int REASSIGN_TYPE = 2;

	}

	/**
	 * @Title: queryData
	 * @Description: 订单列表查询
	 * @param params
	 * @return AjaxResponse
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/queryData")
	public AjaxResponse queryData(BusOrderDTO params) {
		PageDTO pageDTO = new PageDTO();
		try {
			pageDTO = busAssignmentService.selectList(params);
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-queryData ] 查询巴士订单列表出错", e);
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * 根据订单号获取当前可指派的车辆数据
	 *
	 * @param orderNo
	 * @param params
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryCarData", method = { RequestMethod.POST })
	public AjaxResponse queryCarData(@Verify(param = "orderNo", rule = "required") String orderNo,
			@Verify(param = "groupId", rule = "required") Integer groupId,
			@Verify(param = "cityId", rule = "required") Integer cityId, 
			@Verify(param = "type", rule = "required") Integer type, 
			BusCarDTO busCarDTO) {
		if (type != 1 && type != 2) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "业务类型不存在");
		}
		
		PageDTO pageDTO = new PageDTO();
		try {
			pageDTO = busAssignmentService.orderToDoListForCar(busCarDTO);
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-queryCarData ] 根据订单号获取当前可指派的车辆数据异常", e);
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * 根据订单号获取当前可指派的司机数据
	 *
	 * @param orderNo
	 * @param params
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverData", method = { RequestMethod.POST })
	public AjaxResponse queryDriverData(@Verify(param = "orderNo", rule = "required") String orderNo,
			@Verify(param = "groupId", rule = "required") Integer groupId,
			@Verify(param = "cityId", rule = "required") Integer cityId, 
			@Verify(param = "type", rule = "required") Integer type, 
			BusDriverDTO busDriverDTO) {
		if (type != 1 && type != 2) {
			return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "业务类型不存在");
		}
		
		PageDTO pageDTO = new PageDTO();
		try {
			pageDTO = busAssignmentService.orderToDoListForDriver(busDriverDTO);
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-queryDriverData ] 根据订单号获取当前可指派的车辆数据异常", e);
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * @Title: assignment
	 * @Description: 巴士指派：巴士订单与司机绑定
	 * @param orderId
	 * @param orderNo
	 * @param bookingDate
	 * @param driverId
	 * @param carId
	 * @param serviceTypeId
	 * @return AjaxResponse
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/assignment", method = { RequestMethod.POST })
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER),
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse assignment(@Verify(param = "orderId", rule = "required") Integer orderId,
			@Verify(param = "orderNo", rule = "required") String orderNo,
			@Verify(param = "bookingDate", rule = "required") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date bookingDate,
			@Verify(param = "driverId", rule = "required") Integer driverId,
			@Verify(param = "carId", rule = "required") Integer carId,
			@Verify(param = "serviceTypeId", rule = "required") Integer serviceTypeId) {

		// 操作轨迹
		BusOrderOperationTime orderOperationTime = new BusOrderOperationTime();
		// 处理结果
		AjaxResponse ajaxResponse = AjaxResponse.success(null);
		try {
			CarBizDriverInfo driverInfo = carBizDriverInfoMapper.selectByPrimaryKey(driverId);
			if (driverInfo == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机信息不存在");
			}
			String driverPhone = driverInfo.getPhone();
			String driverName = driverInfo.getName();
			// 车型id
			Integer groupId = driverInfo.getGroupId();

			CarBizCarInfoDTO carInfo = this.carBizCarInfoExMapper.selectCarCitySupplierInfoByCarId(carId);
			if (carInfo == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "车辆信息不存在");
			}
			// 城市名称
			String cityName = carInfo.getCityName();
			// 供应商ID
			Integer supplierId = carInfo.getSupplierId();
			// 车牌号
			String licensePlates = carInfo.getLicensePlates();

			if (groupId == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机未指定服务类型");
			}
			String groupName = carBizCarGroupExMapper.getGroupNameByGroupId(groupId);
			if (StringUtils.isBlank(groupName)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "服务类型名称为空");
			}
			if (StringUtils.isBlank(driverPhone)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机手机号为空");
			}
			if (StringUtils.isBlank(driverName)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机姓名为空");
			}
			if (StringUtils.isBlank(licensePlates)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "车牌号为空");
			}
			if (StringUtils.isBlank(cityName)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "城市名称为空");
			}
			if (supplierId == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "供应商为空");
			}
			String dispatcherPhone = this.carBizSupplierExMapper.queryDispatcherPhoneBySupplierId(supplierId);

			JSONObject result = busAssignmentService.busDispatcher(cityName, driverId, driverName, driverPhone,
					dispatcherPhone, groupId, groupName, licensePlates, orderId, orderNo, serviceTypeId);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");

			// 0成功1失败
			if (code != 0) {
				logger.info("[ BusAssignmentController-assignment ] 巴士指派失败, 错误码:{}, 错误原因:{}", code, msg);
				ajaxResponse = AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, msg);

				orderOperationTime.setStatus(OrderOperation.FAIL_STATUS);
				orderOperationTime.setDescription("失败原因：" + msg);
			} else {
				ajaxResponse = AjaxResponse.success(null);

				orderOperationTime.setStatus(OrderOperation.SUCCESS_STATUS);
				orderOperationTime.setDescription("订单指派成功orderNo=" + orderNo);

				// 指派时间-预约用车时间>24小时的,在截止用车前24小时的节点（误差不超过1小时），还需发送给乘客一条短信，告知司机姓名和电话
				saveMessageTask(orderNo, bookingDate);
			}
			orderOperationTime.setDirverPhone(driverPhone);
			orderOperationTime.setDriverName(driverName);
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-assignment ] 巴士指派司机出错", e);
			ajaxResponse = AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);

			orderOperationTime.setStatus(OrderOperation.FAIL_STATUS);
			orderOperationTime.setDescription("订单指派异常orderNo=" + orderNo);
			orderOperationTime.setDriverName("");
			orderOperationTime.setDirverPhone("");
		}
		orderOperationTime.setTime(new Date());
		orderOperationTime.setType(OrderOperation.ASSIGN_TYPE);
		orderOperationTime.setOrderNo(orderNo);
		orderOperationTime.setOrderId(orderId);
		orderOperationTime.setDriverId(driverId);
		// 保存操作轨迹
		saveBusOrderOperation(orderOperationTime);

		return ajaxResponse;
	}

	/**
	 * @Title: updateDriver
	 * @Description: 巴士改派：巴士订单与司机重新绑定
	 * @param orderId
	 * @param orderNo
	 * @param bookingDate
	 * @param driverId
	 * @param carId
	 * @param serviceTypeId
	 * @return Object
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/updateDriver", method = { RequestMethod.POST })
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER),
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse updateDriver(@Verify(param = "orderId", rule = "required") Integer orderId,
			@Verify(param = "orderNo", rule = "required") String orderNo,
			@Verify(param = "bookingDate", rule = "required") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date bookingDate,
			@Verify(param = "driverId", rule = "required") Integer driverId,
			@Verify(param = "carId", rule = "required") Integer carId,
			@Verify(param = "serviceTypeId", rule = "required") Integer serviceTypeId) {

		// 操作轨迹
		BusOrderOperationTime orderOperationTime = new BusOrderOperationTime();
		// 处理结果
		AjaxResponse ajaxResponse = AjaxResponse.success(null);
		try {
			CarBizDriverInfo driverInfo = carBizDriverInfoMapper.selectByPrimaryKey(driverId);
			if (driverInfo == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机信息不存在");
			}
			String driverPhone = driverInfo.getPhone();
			String driverName = driverInfo.getName();
			// 车型id
			Integer groupId = driverInfo.getGroupId();

			CarBizCarInfoDTO carInfo = this.carBizCarInfoExMapper.selectCarCitySupplierInfoByCarId(carId);
			if (carInfo == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "车辆信息不存在");
			}
			// 城市名称
			String cityName = carInfo.getCityName();
			// 车牌号
			String licensePlates = carInfo.getLicensePlates();

			if (groupId == null) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机未指定服务类型");
			}
			String groupName = carBizCarGroupExMapper.getGroupNameByGroupId(groupId);
			if (StringUtils.isBlank(groupName)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "服务类型名称为空");
			}
			if (StringUtils.isBlank(driverPhone)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机手机号为空");
			}
			if (StringUtils.isBlank(driverName)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机姓名为空");
			}
			if (StringUtils.isBlank(licensePlates)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "车牌号为空");
			}
			if (StringUtils.isBlank(cityName)) {
				return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "城市名称为空");
			}

			// 查询改派前订单信息
			BusOrderDetail beforeBusOrder = busOrderService.selectOrderDetail(orderNo);

			// 调用接口改派司机
			JSONObject result = busAssignmentService.updateDriver(cityName, driverId, driverName, driverPhone, groupId,
					groupName, licensePlates, orderId, orderNo, serviceTypeId);
			int code = result.getIntValue("code");
			String msg = result.getString("msg");
			// 0成功1失败
			if (code != 0) {
				logger.info("[ BusAssignmentController-updateDriver ] 巴士改派失败, 错误码:{}, 错误原因:{}", code, msg);
				ajaxResponse = AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, msg);

				orderOperationTime.setStatus(OrderOperation.FAIL_STATUS);
				orderOperationTime.setDescription(msg);
			} else {
				String dataStr = result.getString("data");
				Map<String, Object> resultMap = JSONObject.parseObject(dataStr, HashedMap.class);
				if (null == resultMap || !"1000".equals(resultMap.get("resultCode"))) {// "1000"-成功, 其它为失败
					String errorMsg = (String) resultMap.get("errorMsg");
					logger.info("[ BusAssignmentController-updateDriver ] 巴士改派data返回失败,错误码:{},错误原因:{}", code, errorMsg);
					ajaxResponse = AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, errorMsg);

					orderOperationTime.setStatus(OrderOperation.FAIL_STATUS);
					orderOperationTime.setDescription(errorMsg);

				} else {
					ajaxResponse = AjaxResponse.success(null);

					// 发送信息
					if (beforeBusOrder != null) {
						// 查询改派后司机信息
						BusOrderDetail afterBusOrder = busOrderService.selectOrderDetail(orderNo);
						// 发送短信
						this.sendMessage(beforeBusOrder, afterBusOrder);
					}

					orderOperationTime.setDescription("改派成功");
					orderOperationTime.setStatus(OrderOperation.SUCCESS_STATUS);
				}
			}
			orderOperationTime.setDirverPhone(driverPhone);
			orderOperationTime.setDriverName(driverName);
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-updateDriver ] 巴士改派司机出错", e);
			ajaxResponse = AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);

			orderOperationTime.setDirverPhone("");
			orderOperationTime.setDriverName("");
		}
		orderOperationTime.setTime(new Date());
		orderOperationTime.setType(OrderOperation.REASSIGN_TYPE);
		orderOperationTime.setOrderNo(orderNo);
		orderOperationTime.setOrderId(orderId);
		orderOperationTime.setDriverId(driverId);
		// 保存操作轨迹
		saveBusOrderOperation(orderOperationTime);

		return ajaxResponse;
	}

	/**
	 * @Title: saveBusOrderOperation
	 * @Description: 保存操作轨迹 
	 * @param orderOperationTime void
	 * @throws
	 */
	private void saveBusOrderOperation(BusOrderOperationTime orderOperationTime) {
		try {
			logger.info("[ BusAssignmentController-saveBusOrderOperation ] 巴士订单指派操作开始存入数据库param={}", JSON.toJSONString(orderOperationTime));
			int result = busOrderOperationTimeMapper.insertSelective(orderOperationTime);
			if (result != 0) {
				logger.info("[ BusAssignmentController-saveBusOrderOperation ] 巴士订单指派操作保存成功orderNo={}",
						orderOperationTime.getOrderNo());
			} else {
				logger.info("[ BusAssignmentController-saveBusOrderOperation ] 巴士订单指派操作保存失败orderNo={}",
						orderOperationTime.getOrderNo());
			}
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-saveBusOrderOperation ] 巴士订单指派操作保存异常", e);
		}
	}

	/**
	 * 保存发送短信的task
	 *
	 * @param orderNo
	 * @param bookDate
	 */
	private void saveMessageTask(String orderNo, Date bookingDate) {
		try {
			if (bookingDate == null) {
				logger.info( "[ BusAssignmentController-saveMessageTask ] 巴士指派司机-bookingDate为null ,本次不保存发送短信task: orderNo = {}", orderNo);
				return;
			}
			// 判断是否需要发短信
			Date currentDate = new Date();
			long difference = bookingDate.getTime() - currentDate.getTime();
			double result = difference * 1.0 / (1000 * 60 * 60);
			if (result > 24) {
				logger.info( "[ BusAssignmentController-saveMessageTask ] 巴士指派司机-保存发送短信task: orderNo = {}, bookingDate = {}", orderNo, LocalDateTime.ofInstant(bookingDate.toInstant(), ZoneId.systemDefault()));

				// 查询订单信息
				BusOrderDetail order = busOrderService.selectOrderDetail(orderNo);
				CarBizOrderMessageTask entity = new CarBizOrderMessageTask();
				entity.setOrderNo(order.getOrderNo());
				entity.setDriverName(order.getDriverName());
				entity.setDriverPhone(order.getDriverPhone());
				entity.setLicensePlates(order.getLicensePlates());
				entity.setRiderName(order.getRiderName());
				entity.setRiderPhone(order.getRiderPhone());
				entity.setBookingDate(order.getBookingDate());
				carBizOrderMessageTaskMapper.insertSelective(entity);
			} else {
				logger.info( "[ BusAssignmentController-saveMessageTask ] 巴士指派司机-小于24小时，无需保存发送短信task: orderNo = {}, bookingDate = {}", orderNo, LocalDateTime.ofInstant(bookingDate.toInstant(), ZoneId.systemDefault()));
			}
		} catch (Exception e) {
			logger.error( "[ BusAssignmentController-saveMessageTask ] 巴士指派司机-保存发送短信的task异常：orderNo = {}, bookingDate = {}", orderNo, LocalDateTime.ofInstant(bookingDate.toInstant(), ZoneId.systemDefault()), e);
		}
	}

	private Map<Object, Object> sendMessage(BusOrderDetail beforeBusOrder, BusOrderDetail afterBusOrder) {
		Map<Object, Object> result = new HashMap<Object, Object>();
		try {
			// 预订人手机
			String riderPhone = beforeBusOrder.getRiderPhone();
			// 取消的司机手机号
			String beforeDriverPhone = beforeBusOrder.getDriverPhone();
			// 改派后司机姓名
			String driverName = afterBusOrder.getDriverName();
			// 改派后司机手机号
			String afterDriverPhone = afterBusOrder.getDriverPhone();
			// 改派后车牌号
			String licensePlates = afterBusOrder.getLicensePlates();
			// 预订上车地点
			String bookingStartAddr = beforeBusOrder.getBookingStartAddr();
			// 预订下车地点
			String bookingEndAddr = beforeBusOrder.getBookingEndAddr();
			// 预订上车时间
			Date bookDate = beforeBusOrder.getBookingDate();
			String bookingDate = DateUtils.formatDate(bookDate, DateUtil.LOCAL_FORMAT);

			String driverContext = "订单，" + bookingDate + "有乘客从" + bookingStartAddr + "到" + bookingEndAddr;
			String beforeDriverContext = "尊敬的师傅您好，您的巴士指派" + driverContext + "，已被改派取消。";
			String afterDriverContext = "尊敬的师傅您好，接到巴士服务" + driverContext + "，请您按时接送。";
			String riderContext = "尊敬的用户您好，您预订的" + bookingDate + "的巴士服务订单已被改派成功，司机" + driverName + "，"
					+ afterDriverPhone + "，车牌号" + licensePlates + "，将竭诚为您服务。";

			// 乘客
			SmsSendUtil.send(riderPhone, riderContext);
			// 取消司机
			SmsSendUtil.send(beforeDriverPhone, beforeDriverContext);
			// 改派司机
			SmsSendUtil.send(afterDriverPhone, afterDriverContext);

		} catch (Exception e) {
			logger.error("巴士改派发送短信异常.", e);
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/queryOrderDetails", method = {RequestMethod.POST})
	@MasterSlaveConfigs(configs = {
			@MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE),
			@MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE) })
	public AjaxResponse queryOrderDetails(@Verify(param = "orderId", rule = "required") Integer orderId,
			@Verify(param = "orderNo", rule = "required") String orderNo) {
        if (orderId == null || StringUtils.isBlank(orderNo)) {
        	return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "订单ID或订单编号不能为空");
        }
        
        try {
			// 调用订单接口查询订单详情
			BusOrderDetail orderDetail = busOrderService.selectOrderDetail(orderNo);
			logger.info("[ BusAssignmentController-saveMessageTask ] 订单详情 = {}", JSON.toJSONString(orderDetail));
			
			// 调用计费接口
			BusCostDetail busCostDetail = busOrderService.selectOrderCostDetail(orderId);
			logger.info("[ BusAssignmentController-saveMessageTask ] 计费详情 = {}", JSON.toJSONString(busCostDetail));
			
			// 调用支付接口
			BusPayDetail busPayDetail = busOrderService.selectOrderPayDetail(orderNo);
			logger.info("[ BusAssignmentController-saveMessageTask ] 支付详情 = {}", JSON.toJSONString(busPayDetail));
			
			// 查询派单订单操作状态
			List<BusOrderOperationTime> orderOperations = busOrderOperationTimeExMapper.queryOperationByOrderId(orderId);
			logger.info("[ BusAssignmentController-saveMessageTask ] 操作详情 = {}", JSON.toJSONString(orderOperations));
			
			Map<String,Object> result = new HashMap<>();
			result.put("orderDetail", orderDetail);
			result.put("busCostDetail", busCostDetail);
			result.put("busPayDetail", busPayDetail);
			result.put("orderOperations", orderOperations);
			return AjaxResponse.success(result);
		} catch (Exception e) {
			logger.error("[ BusAssignmentController-saveMessageTask ] ", e);
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
    }

}