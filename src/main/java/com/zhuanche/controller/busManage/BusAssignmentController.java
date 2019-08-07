package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.dto.BaseDTO;
import com.zhuanche.dto.busManage.BusCarDTO;
import com.zhuanche.dto.busManage.BusDriverDTO;
import com.zhuanche.dto.busManage.BusOrderDTO;
import com.zhuanche.dto.rentcar.CarBizCarInfoDTO;
import com.zhuanche.entity.busManage.*;
import com.zhuanche.entity.mdbcarmanage.BusOrderMessageTask;
import com.zhuanche.entity.mdbcarmanage.BusOrderOperationTime;
import com.zhuanche.entity.mdbcarmanage.CarBizOrderMessageTask;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.serv.busManage.BusAssignmentService;
import com.zhuanche.serv.busManage.BusCommonService;
import com.zhuanche.serv.busManage.BusOrderService;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.BusOrderExportCutVO;
import com.zhuanche.vo.busManage.OrderOperationProcessVO;
import mapper.mdbcarmanage.BusOrderMessageTaskMapper;
import mapper.mdbcarmanage.BusOrderOperationTimeMapper;
import mapper.mdbcarmanage.CarBizOrderMessageTaskMapper;
import mapper.mdbcarmanage.ex.BusOrderMessageTaskExMapper;
import mapper.mdbcarmanage.ex.BusOrderOperationTimeExMapper;
import mapper.mdbcarmanage.ex.SaasRolePermissionRalationExMapper;
import mapper.rentcar.CarBizDriverInfoMapper;
import mapper.rentcar.ex.CarBizCarInfoExMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller("busAssignmentController")
@RequestMapping(value = "/busAssignment")
public class BusAssignmentController {

    private static final Logger logger = LoggerFactory.getLogger(BusAssignmentController.class);

    @Autowired
    private CarBizDriverInfoMapper carBizDriverInfoMapper;

    @Autowired
    private CarBizCarInfoExMapper carBizCarInfoExMapper;

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
    @Autowired
    private BusCommonService commonService;

    @Autowired
    private SaasRolePermissionRalationExMapper saasRolePermissionRalationExMapper;
    @Autowired
    private BusOrderMessageTaskMapper busOrderMessageTaskMapper;
    @Autowired
    private BusOrderMessageTaskExMapper busOrderMessageTaskExMapper;

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
     * @param params
     * @return AjaxResponse
     * @throws
     * @Title: queryData
     * @Description: 订单列表查询
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "/exportOrder")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public void exportExcel(BusOrderDTO params, HttpServletRequest request, HttpServletResponse response) throws Exception {

        long start = System.currentTimeMillis();
        logger.info("[ BusAssignmentController-exportExcel ]" + "导出订单列表参数=" + JSON.toJSONString(params));
        //下单时间，完成时间必须有一项不为空，且间隔小于92天
        boolean createFlag=false;
        boolean factEndFlag=false;
        if (params.getCreateDateBegin() != null && params.getCreateDateEnd() != null) {
            Date begin = DateUtils.getDate(params.getCreateDateBegin(), "yyyy-MM-dd");
            Date end = DateUtils.getDate(params.getCreateDateEnd(), "yyyy-MM-dd");
            Integer createGap = DateUtils.getIntervalDays(begin, end);
            if(createGap<=92) createFlag=true;
        }
        if (params.getFactEndDateBegin() != null && params.getFactEndDateEnd() != null) {
            Date begin = DateUtils.getDate(params.getFactEndDateBegin(), "yyyy-MM-dd");
            Date end = DateUtils.getDate(params.getFactEndDateEnd(), "yyyy-MM-dd");
            Integer createGap = DateUtils.getIntervalDays(begin, end);
            if(createGap<=92) factEndFlag=true;
        }
        //如果两个时间区间都不符合条件，不可以导出
        CsvUtils utilEntity = new CsvUtils();
        //构建文件名称
        String fileName = BusConstant.buidFileName(request, "订单明细");
        if(!createFlag&&!factEndFlag){
            ArrayList csvData = new ArrayList();
            csvData.add("下单时间、完成时间需要任选其一且不能于三个月");
            utilEntity.exportCsvV2(response, csvData, new ArrayList<>(), fileName, true, true);
            return;
        }
        boolean roleBoolean = commonService.ifOperate();
        String[] headArray = null;
        if (roleBoolean) {
            headArray = BusConstant.Order.ORDER_HEAD;
        } else {
            //无权导出企业信息（精简信息）
            headArray = BusConstant.Order.CUT_ORDER_HEAD;
        }
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        String head = StringUtils.join(headArray, ",");
        headerList.add(head);

        Integer pageNum = 0;
        params.setPageSize(500);
        boolean isFirst = true;
        boolean isList = false;
        do {
            pageNum++;
            params.setPageNum(pageNum);
            PageDTO pageDTO = busAssignmentService.buidExportData(params, roleBoolean);
            long total = pageDTO.getTotal();
            List result = pageDTO.getResult();
            int pages = pageDTO.getPages();
            if (total == 0 || result == null || result.isEmpty()) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("没有符合条件的数据");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            if (pageNum != 1) {
                isFirst = false;
            }
            if (pageNum.equals(pages)) {
                isList = true;
            }
            List<String> csvData = new ArrayList<>();
            result.forEach(o -> {
                Object resultObj;
                if(!roleBoolean){
                    //导出精简版信息
                    BusOrderExportCutVO busOrderExportVO = new BusOrderExportCutVO();
                    BeanUtils.copyProperties(o,busOrderExportVO);
                    resultObj=busOrderExportVO;
                }else{
                    resultObj=o;
                }
                String s = busAssignmentService.fieldValueToString(resultObj);
                csvData.add(s);
            });
            utilEntity.exportCsvV2(response, csvData, headerList, fileName, isFirst, isList);
            // isList=true时表示时之后一页停止循环
        } while (!isList);
        logger.info("[ BusAssignmentController-exportExcel ]" + "导出订单数据=" + JSON.toJSONString(params) + " 消耗时间=" + (System.currentTimeMillis() - start));
    }


    /**
     * 根据订单号获取当前可指派的车辆数据
     *
     * @param orderNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryCarData", method = {RequestMethod.POST})
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryDriverData", method = {RequestMethod.POST})
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
     * @param orderId
     * @param orderNo
     * @param bookingDate
     * @param driverId
     * @param carId
     * @param serviceTypeId
     * @return AjaxResponse
     * @throws
     * @Title: assignment
     * @Description: 巴士指派：巴士订单与司机绑定
     */
    @ResponseBody
    @RequestMapping(value = "/assignment", method = {RequestMethod.POST})
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER),
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)})
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
            CarBizCarInfoDTO carInfo = this.carBizCarInfoExMapper.selectBasicCarInfo(carId);
            if (driverInfo == null || carInfo == null) {
                return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机或者车辆信息不存在");
            }
            if(!driverInfo.getSupplierId().equals(carInfo.getSupplierId())){
                return AjaxResponse.fail(RestErrorCode.BUS_COMMON_ERROR_CODE,"车辆和司机不是同一家供应商");
            }
            String driverPhone = driverInfo.getPhone();
            String driverName = driverInfo.getName();
            // 车型类别ID
            Integer groupId = carInfo.getGroupId();
            //车型类别名称
            String groupName = carInfo.getGroupName();
            // 车型类别名称
            String cityName = carInfo.getCityName();
            //供应商手机号
            String dispatcherPhone = carInfo.getDispatcherPhone();
            // 车牌号
            String licensePlates = carInfo.getLicensePlates();
            if (groupId == null) {
                return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "车辆未指定车型类别");
            }
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
            // 一、通知分佣账户
            busAssignmentService.orderNoToMaid(orderNo);
            
            // 二、指派订单
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
            //修改延迟短信任务状态（不管有没有发送，改状态为已发送）
            BusOrderMessageTask busOrderMessageTask = busOrderMessageTaskExMapper.selectByOrderNum(orderNo);
            if(busOrderMessageTask != null){
                busOrderMessageTask.setStatus(1);
                busOrderMessageTask.setUpdateDate(new Date());
                busOrderMessageTaskMapper.updateByPrimaryKeySelective(busOrderMessageTask);
                logger.info("BusAssignmentController-巴士订单指派，修改短信任务状态成功，taskId--{}",busOrderMessageTask.getId());
            }
            orderOperationTime.setDirverPhone(driverPhone);
            orderOperationTime.setDriverName(driverName);
        } catch (Exception e) {
            logger.error("[ BusAssignmentController-assignment ] 巴士指派司机出错", e);
            ajaxResponse = AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "巴士指派司机出错");

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
     * @param orderId
     * @param orderNo
     * @param bookingDate
     * @param driverId
     * @param carId
     * @param serviceTypeId
     * @return Object
     * @throws
     * @Title: updateDriver
     * @Description: 巴士改派：巴士订单与司机重新绑定
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/updateDriver", method = {RequestMethod.POST})
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.MASTER),
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)})
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
            CarBizCarInfoDTO carInfo = this.carBizCarInfoExMapper.selectBasicCarInfo(carId);
            if (driverInfo == null || carInfo == null) {
                return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "司机或者车辆信息不存在");
            }
            if(!driverInfo.getSupplierId().equals(carInfo.getSupplierId())){
                return AjaxResponse.fail(RestErrorCode.BUS_COMMON_ERROR_CODE,"车辆和司机不是同一家供应商");
            }
            String driverPhone = driverInfo.getPhone();
            String driverName = driverInfo.getName();
            // 车型类别ID
            Integer groupId = carInfo.getGroupId();
            //车型类别名称
            String groupName = carInfo.getGroupName();
            // 车型类别名称
            String cityName = carInfo.getCityName();
            // 车牌号
            String licensePlates = carInfo.getLicensePlates();

            if (groupId == null) {
                return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "车辆未指定车型类别");
            }
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

            // 一、通知分佣账户
            busAssignmentService.orderNoToMaid(orderNo);
            
            // 查询改派前订单信息
            BusOrderDetail beforeBusOrder = busOrderService.selectOrderDetail(orderNo);
            // 二、调用接口改派司机
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
					String errorMsg = resultMap == null ? "改派失败" : (String) resultMap.get("errorMsg");

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
     * @param orderOperationTime void
     * @throws
     * @Title: saveBusOrderOperation
     * @Description: 保存操作轨迹
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
     * @param bookingDate
     */
    private void saveMessageTask(String orderNo, Date bookingDate) {
        try {
            if (bookingDate == null) {
                logger.info("[ BusAssignmentController-saveMessageTask ] 巴士指派司机-bookingDate为null ,本次不保存发送短信task: orderNo = {}", orderNo);
                return;
            }
            // 判断是否需要发短信
            Date currentDate = new Date();
            long difference = bookingDate.getTime() - currentDate.getTime();
            double result = difference * 1.0 / (1000 * 60 * 60);
            if (result > 24) {
                logger.info("[ BusAssignmentController-saveMessageTask ] 巴士指派司机-保存发送短信task: orderNo = {}, bookingDate = {}", orderNo, LocalDateTime.ofInstant(bookingDate.toInstant(), ZoneId.systemDefault()));

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
                logger.info("[ BusAssignmentController-saveMessageTask ] 巴士指派司机-小于24小时，无需保存发送短信task: orderNo = {}, bookingDate = {}", orderNo, LocalDateTime.ofInstant(bookingDate.toInstant(), ZoneId.systemDefault()));
            }
        } catch (Exception e) {
        	LocalDateTime bookingLocalDate = null;
			if (bookingDate != null) {
				bookingLocalDate = LocalDateTime.ofInstant(bookingDate.toInstant(), ZoneId.systemDefault());
			}
            logger.error("[ BusAssignmentController-saveMessageTask ] 巴士指派司机-保存发送短信的task异常：orderNo = {}, bookingDate = {}", orderNo, bookingLocalDate, e);
        }
    }

    private Map<Object, Object> sendMessage(BusOrderDetail beforeBusOrder, BusOrderDetail afterBusOrder) {
        Map<Object, Object> result = new HashMap<Object, Object>();
        try {
        	// 预订上车时间
        	Date bookDate = beforeBusOrder.getBookingDate();
        	String bookingDate = DateUtils.formatDate(bookDate, DateUtil.LOCAL_FORMAT);
        	
			Date date = new Date();
			long difference = bookDate.getTime() - date.getTime();
			double subResult = difference * 1.0 / (1000 * 60 * 60);
			if (subResult <= 24) {
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
	
	            String driverContext = "订单，" + bookingDate + "有乘客从" + bookingStartAddr + "到" + bookingEndAddr;
	            String beforeDriverContext = "尊敬的师傅您好，您的巴士指派" + driverContext + "，已被改派取消。";
	            String afterDriverContext = "尊敬的师傅您好，接到巴士服务" + driverContext + "，请您按时接送。";
	            String riderContext = "尊敬的用户您好，您预订的" + bookingDate + "的巴士服务订单已被改派成功，司机" + driverName + "，" + afterDriverPhone + "，车牌号" + licensePlates + "，将竭诚为您服务。";
	
	            // 乘客
	            SmsSendUtil.send(riderPhone, riderContext);
	            // 取消司机
	            SmsSendUtil.send(beforeDriverPhone, beforeDriverContext);
	            // 改派司机
	            SmsSendUtil.send(afterDriverPhone, afterDriverContext);
			} else {
				logger.info("巴士改派司机-大于等于24小时，无需发送短信: orderNo = " + beforeBusOrder.getOrderNo() + ", bookingDate = " + bookingDate);
			}
        } catch (Exception e) {
            logger.error("巴士改派发送短信异常.", e);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/queryOrderDetails", method = {RequestMethod.POST})
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "mdbcarmanage-DataSource", mode = DataSourceMode.SLAVE),
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)})
    public AjaxResponse queryOrderDetails(@Verify(param = "orderNo", rule = "required") String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return AjaxResponse.failMsg(RestErrorCode.HTTP_PARAM_INVALID, "订单编号不能为空");
        }
        try {
            // 调用订单接口查询订单详情
            BusOrderDetail orderDetail = busOrderService.selectOrderDetail(orderNo);
            
            Integer status = orderDetail.getStatus();
            //是否可以被继续操作
            //该订单未被指派，可以继续查看详情
            if (status < 10105) {
            	return AjaxResponse.fail(RestErrorCode.BUS_COMMON_ERROR_CODE, "该订单已经被指派,禁止查看详情");
            }
            
            Integer orderId = orderDetail.getOrderId();
            // 调用计费接口
            BusCostDetail busCostDetail = busOrderService.selectOrderCostDetail(orderId);
            //计费返回新字段settleOriginalAmount  代替 待结算金额 settleAmount
            busCostDetail.setSettleAmount(busCostDetail.getSettleOriginalAmount());

            // 调用支付接口
            BusPayDetail busPayDetail = busOrderService.selectOrderPayDetail(orderNo);

            // 查询派单订单操作状态
            List<BusOrderOperationTime> orderOperations = busOrderOperationTimeExMapper.queryOperationByOrderId(orderId);
            logger.info("[ BusAssignmentController-saveMessageTask ] 操作详情 = {}", JSON.toJSONString(orderOperations));

            //查询司机评分
            Appraisal appraisal = busOrderService.selectOrderAppraisal(orderNo);
            logger.info("[ BusAssignmentController-saveMessageTask ] 评分信息 = {}", JSON.toJSONString(appraisal));

            //查询企业信息
            OrgCostInfo orgCostInfo =null;
            //type=2代表企业订单
            if(orderDetail.getType()==2&&orderDetail.getBusinessId()!=null){
                orgCostInfo = busOrderService.selectOrgInfo(orderDetail.getBusinessId());
                logger.info("[ BusAssignmentController-saveMessageTask ] 企业折扣信息 = {}", JSON.toJSONString(orgCostInfo));
            }else{
                logger.info("[ BusAssignmentController-saveMessageTask ] 不是企业订单无企业信息 orderNo={}",orderDetail.getOrderNo());
            }
            //封装各个订单节点操作时间
            List<OrderOperationProcessVO> operationProcess = buidOperationProcess(orderDetail, busCostDetail, busPayDetail, orderOperations);

            Map<String, Object> result = new HashMap<>(16);
            result.put("orderDetail", orderDetail);
            result.put("busCostDetail", busCostDetail);
            result.put("busPayDetail", busPayDetail);
            result.put("orderOperations", operationProcess);
            result.put("appraisal", appraisal);
            result.put("orgInfo", orgCostInfo);
            return AjaxResponse.success(result);
        } catch (Exception e) {
            logger.error("[ BusAssignmentController-saveMessageTask ] ", e);
            return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
        }
    }

    private List<OrderOperationProcessVO> buidOperationProcess(BusOrderDetail order, BusCostDetail cost, BusPayDetail pay, List<BusOrderOperationTime> orderOperations) {
        List<OrderOperationProcessVO> operList = new ArrayList<>();
        if (order != null && order.getCreateDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("创建订单", order.getCreateDate(), "创建订单成功");
            operList.add(process);
        }
        OrderOperationProcessVO payProcess = new OrderOperationProcessVO("支付");
        operList.add(payProcess);
        if (pay != null && pay.getFinishDate() != null) {
            payProcess.setOperationTime(pay.getFinishDate());
            payProcess.setDesc("订单支付成功");
        } else {
            payProcess.setDesc("该订单未支付，请查看订单状态");
        }
        for (BusOrderOperationTime o : orderOperations) {
            OrderOperationProcessVO process = new OrderOperationProcessVO();
            operList.add(process);
            if (o.getType() == 1) {
                process.setEventName("指派");
            } else {
                process.setEventName("改派");
            }
            if (o.getStatus() == 1) {
                process.setDesc("操作成功 司机姓名:" + o.getDriverName() + "手机号:" + o.getDirverPhone());
            } else {
                process.setDesc("操作失败 原因:" + o.getDescription());
            }
            process.setOperationTime(o.getTime());
        }
        if (order != null && order.getCancelCreateDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("订单取消", order.getCancelCreateDate(), "取消原因：" + order.getMemo());
            operList.add(process);
        }
        if (order != null && order.getStartOffDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("司机出发时间", order.getStartOffDate(), "司机已出发");
            operList.add(process);
        }
        if (order != null && order.getArriveDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("司机到达时间", order.getArriveDate(), "司机已到达");
            operList.add(process);
        }
        if (order != null && order.getFactDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("开始服务时间", order.getFactDate(), "开始服务");
            operList.add(process);
        }
        if (order != null && order.getEndServiceDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("结束服务时间", order.getEndServiceDate(), "服务结束");
            operList.add(process);
        }
        if (cost != null && cost.getSettleDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("尾款生成时间", cost.getSettleDate(), "尾款已经生成");
            operList.add(process);
        }
        if (order != null && order.getFactEndDate() != null) {
            OrderOperationProcessVO process = new OrderOperationProcessVO("订单完成时间", order.getFactEndDate(), "订单已经完成");
            operList.add(process);
        }
        return operList;
    }

    /**
     *  供应商角色：显示本供应商下未来三天内带服务的订单
     *
     *  运营角色：显示所有供应商下未来三天内带服务的订单
     * @return
     */
    @RequestMapping("/queryUpcomingOrder")
    @ResponseBody
    @MasterSlaveConfigs(configs = {
            @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE)})
    public AjaxResponse queryUpcomingOrder(BaseDTO pageDTO) {
        AjaxResponse response = busOrderService.queryUpcomingOrder(pageDTO);
        return response;
    }

    @RequestMapping("/checkOrderStatus")
    @ResponseBody
    public AjaxResponse checkOrderStatus(@Verify(param="orderNo",rule="required") String orderNo){
        Map<String, Object> result = new HashMap<>(2);
        boolean isContinue = false;
        BusOrderDetail orderDetail = busOrderService.selectOrderDetail(orderNo);
        if (orderDetail != null) {
            Integer status = orderDetail.getStatus();
            //是否可以被继续操作
            //该订单未被指派，可以继续查看详情
            if (status < 10105) {
                isContinue = true;
            }
        } else {
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }
        result.put("isContinue", isContinue);
        return AjaxResponse.success(result);
    }
}