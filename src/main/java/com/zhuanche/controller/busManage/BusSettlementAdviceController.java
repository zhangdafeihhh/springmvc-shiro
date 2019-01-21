package com.zhuanche.controller.busManage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import com.zhuanche.constants.BusConst;
import com.zhuanche.constants.busManage.EnumServiceType;
import com.zhuanche.entity.busManage.BusCostDetail;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.busManage.*;
import com.zhuanche.util.BeanUtil;
import com.zhuanche.util.Common;
import com.zhuanche.util.SignUtils;
import com.zhuanche.vo.busManage.*;
import mapper.rentcar.ex.BusCarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.busManage.BusConstant;
import com.zhuanche.constants.busManage.BusConstant.SupplierMaidConstant;
import com.zhuanche.dto.busManage.BusSettleChangeDTO;
import com.zhuanche.dto.busManage.BusSettleOrderListDTO;
import com.zhuanche.dto.busManage.BusSettlementInvoiceDTO;
import com.zhuanche.dto.busManage.BusSettlementOrderChangeDTO;
import com.zhuanche.dto.busManage.BusSettlementPaymentDTO;
import com.zhuanche.dto.busManage.BusSupplierSettleListDTO;
import com.zhuanche.entity.busManage.BusOrderDetail;
import com.zhuanche.entity.rentcar.CarBizService;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.excel.CsvUtils;

import mapper.rentcar.CarBizServiceMapper;

/**
 * @ClassName: BusSettlementAdviceController
 * @Description: 巴士结算单管理
 * @author: yanyunpeng
 * @date: 2018年12月7日 上午11:28:50
 */
@RestController
@RequestMapping("/bus/settlement")
@Validated
public class BusSettlementAdviceController {

    private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceController.class);
    private static final String LOG_PRE = "【供应商分佣结算单管理】";

    @Autowired
    private BusCommonService busCommonService;

    @Autowired
    private BusSettlementAdviceService busSettlementAdviceService;

    @Autowired
    private BusSupplierService busSupplierService;
    @Autowired
    private CarBizSupplierService supplierService;
    @Autowired
    private BusOrderService busOrderService;
    @Autowired
    private CarBizServiceMapper serviceMapper;

    @Autowired
    private BusAssignmentService busAssignmentService;

    @Autowired
    private CarBizServiceMapper carBizServiceMapper;

    @Autowired
    private BusCarBizDriverInfoExMapper busCarBizDriverInfoExMapper;

    @Autowired
    private CarBizCarGroupExMapper carBizCarGroupExMapper;


    /**
     * 查询供应商账单列表
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/pageList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleDetailList(BusSupplierSettleListDTO dto) {
        logger.info(LOG_PRE + "查询账单列表参数=" + JSON.toJSONString(dto));
        getAuth(dto);
        Integer cityId = dto.getCityId();
        Integer supplierId = dto.getSupplierId();
        if (supplierId != null) {
            dto.setSupplierIds(String.valueOf(supplierId));
        } else {
            if (cityId == null) {
                Set<Integer> authOfSupplier = dto.getAuthOfSupplier();
                String join = StringUtils.join(authOfSupplier, ",");
                dto.setSupplierIds(join);
            } else {
                List<Map<Object, Object>> maps = busCommonService.querySuppliers(cityId);
                if (maps.isEmpty()) {
                    return AjaxResponse.success(new ArrayList<>());
                }
                StringBuffer sb = new StringBuffer();
                for (Map<Object, Object> map : maps) {
                    sb.append(String.valueOf(map.get("supplierId"))).append(",");
                }
                dto.setSupplierIds(sb.substring(0, sb.length() - 1));
            }
        }
        JSONObject result = busSettlementAdviceService.querySettleDetailList(dto);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }
        JSONArray data = result.getJSONArray("data");
        if (data == null || data.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        Map<Integer, BusSupplierInfoVO> supplierInfoMap = querySupplierInfo(data);
        List<BusSupplierSettleDetailVO> collect = data.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, BusSupplierSettleDetailVO.class))
                .map(o -> {
                    return buidSettleDetailVO(o, supplierInfoMap);
                }).collect(Collectors.toList());
        logger.info(LOG_PRE + "查询账单列表参数=" + JSON.toJSONString(dto) + "结果=" + JSON.toJSONString(collect));
        return AjaxResponse.success(collect);
    }

    private BusSupplierSettleDetailVO buidSettleDetailVO(BusSupplierSettleDetailVO settleDetail, Map<Integer, BusSupplierInfoVO> infoMap) {
        BusSupplierInfoVO info = infoMap.get(settleDetail.getSupplierId());
        if (info != null) {
            settleDetail.setCityName(info.getCityName());
            settleDetail.setSupplierName(info.getSupplierName());
        }
        return settleDetail;
    }

    private Map<Integer, BusSupplierInfoVO> querySupplierInfo(JSONArray array) {
        Set<Integer> queryparam = array.stream().map(O -> (JSONObject) O).map(O -> O.getInteger("supplierId")).collect(Collectors.toSet());
        //查询供应商的基本信息
        List<BusSupplierInfoVO> supplierInfo = busSupplierService.queryBasicInfoByIds(queryparam);
        Map<Integer, BusSupplierInfoVO> supplierInfoMap = new HashMap<>(16);
        supplierInfo.forEach(o -> {
            supplierInfoMap.put(o.getSupplierId(), o);
        });
        return supplierInfoMap;
    }

    private void getAuth(BusSupplierSettleListDTO dto) {
        SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
        Set<Integer> authSupper = loginUser.getSupplierIds();
        Set<Integer> authCity = loginUser.getCityIds();
        if (authSupper.isEmpty()) {
            Map<String, Set<Integer>> param = new HashMap<>(2);
            param.put("cityIds", authCity);
            List<Integer> integers = busSupplierService.querySupplierIdByCitys(param);
            authSupper = new HashSet<>(integers);
        }
        dto.setAuthOfCity(authCity);
        dto.setAuthOfSupplier(authSupper);
    }

    @RequestMapping("/exportList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public void exportSettleDetailList(BusSupplierSettleListDTO dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("巴士供应商查询账单列表参数=" + JSON.toJSONString(dto));
        //文件名称
        String fileName = BusConstant.buidFileName(request, SupplierMaidConstant.BILL_FILE_NAME);
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        headerList.add(SupplierMaidConstant.BILL_EXPORT_HEAD);
        CsvUtils utilEntity = new CsvUtils();
        getAuth(dto);
        Integer cityId = dto.getCityId();
        Integer supplierId = dto.getSupplierId();
        if (supplierId != null) {
            dto.setSupplierIds(String.valueOf(supplierId));
        } else {
            if (cityId == null) {
                Set<Integer> authOfSupplier = dto.getAuthOfSupplier();
                String join = StringUtils.join(authOfSupplier, ",");
                dto.setSupplierIds(join);
            } else {
                List<Map<Object, Object>> maps = busCommonService.querySuppliers(cityId);
                if (maps.isEmpty()) {
                    List<String> csvDataList = new ArrayList<>();
                    csvDataList.add("没有符合条件的数据");
                    utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, true, true);
                    return;
                }
                StringBuffer sb = new StringBuffer();
                for (Map<Object, Object> map : maps) {
                    sb.append(String.valueOf(map.get("supplierId"))).append(",");
                }
                dto.setSupplierIds(sb.substring(0, sb.length() - 1));
            }
        }
        Integer pageNum = 0;
        boolean isFirst = true;
        boolean isList = false;
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            dto.setPageSize(CsvUtils.downPerSize);
            JSONObject result = busSettlementAdviceService.querySettleDetailList(dto);
            Integer code = result.getInteger("code");
            if (code == null || code != 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("导出异常，请稍后重试，或者联系管理员");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            List<String> csvData;
            JSONArray array = result.getJSONArray("data");
            boolean isnull = (array == null || array.isEmpty());
            if (isnull && pageNum == 1) {
                csvData = new ArrayList<>();
                csvData.add("没有符合条件的数据");
                isList = true;
                utilEntity.exportCsvV2(response, csvData, headerList, fileName, isFirst, isList);
                break;
            }
            if (pageNum != 1) {
                isFirst = false;
            }
            if (isnull) {
                isList = true;
            }
            Map<Integer, BusSupplierInfoVO> supplierInfoMap = querySupplierInfo(array);
            csvData = array.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, BusSupplierSettleDetailVO.class)).map(o -> {
                return buidSettleDetailVO(o, supplierInfoMap);
            }).map(BusSupplierSettleDetailVO::toString).collect(Collectors.toList());
            utilEntity.exportCsvV2(response, csvData, headerList, fileName, isFirst, isList);
            // isList=true时表示时之后一页停止循环
        } while (!isList);
        logger.info(LOG_PRE + "巴士供应商查询账单列表参数=" + JSON.toJSONString(dto) + " 消耗时间=" + (System.currentTimeMillis() - start));
    }

    /**
     * 查询结算单详情
     *
     * @param supplierBillId
     * @return
     */
    @RequestMapping("/detail")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleDetail(@Verify(param = "supplierBillId", rule = "required") String supplierBillId, HttpServletRequest request) {
        JSONObject result = busSettlementAdviceService.getBillDetail(supplierBillId);
        if (result == null) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }
        BusSettlementDetailVO busSettlementDetailVO = JSONObject.toJavaObject(result, BusSettlementDetailVO.class);
        Integer supplierId = result.getInteger("supplierId");
        CarBizSupplier carBizSupplier = supplierService.selectByPrimaryKey(supplierId);
        busSettlementDetailVO.setSupplierName(carBizSupplier.getSupplierFullName());
        //暂时没有上次结算金额，如果为空，赋值0
        if (busSettlementDetailVO.getLastAmount() == null) {
            busSettlementDetailVO.setLastAmount(new BigDecimal(0));
        }
        return AjaxResponse.success(busSettlementDetailVO);
    }

    /**
     * @Description: 供应商账单修改页回显数据
     * @Param: [supplierBillId]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/20
     */
    @RequestMapping("/update/init")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleUpdateInit(@Verify(param = "supplierBillId", rule = "required") String supplierBillId) {
        JSONObject result = busSettlementAdviceService.getBillDetail(supplierBillId);
        if (result == null) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }

        BusSettlementUdateInitVO updateInitVO = JSONObject.toJavaObject(result, BusSettlementUdateInitVO.class);
        Integer supplierId = result.getInteger("supplierId");
        CarBizSupplier carBizSupplier = supplierService.selectByPrimaryKey(supplierId);
        updateInitVO.setSupplierName(carBizSupplier.getSupplierFullName());
        return AjaxResponse.success(updateInitVO);
    }

    @RequestMapping("/transactions/init")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleTransactionsInit(@Verify(param = "supplierBillId", rule = "required") String supplierBillId,
                                                    @Verify(param = "id", rule = "required") Integer id) {
        //先查询账单详情
        JSONObject billDetail = busSettlementAdviceService.getBillDetail(supplierBillId);
        //查询明细详情
        JSONObject transactionsDetail = busSettlementAdviceService.getTransactionsDetail(id);
        if (billDetail == null || transactionsDetail == null) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }
        //调用订单接口查询订单详情
        String orderNo = transactionsDetail.getString("orderNo");
        BusOrderDetail orderDetail = busOrderService.selectOrderDetail(orderNo);


        //封装结果
        BusTransactionsDetailVO resultDetail = new BusTransactionsDetailVO();
        resultDetail.setOrderNo(orderNo);
        //订单金额
        resultDetail.setOrderAmount(transactionsDetail.getBigDecimal("orderAmount"));

        resultDetail.setBookingGroupName(orderDetail.getBookingGroupName());
        resultDetail.setLicensePlates(orderDetail.getLicensePlates());
        //显示乘车人
        resultDetail.setBookingUserName(orderDetail.getRiderName() != null ? orderDetail.getRiderName() : StringUtils.EMPTY);
        //分佣比例
        resultDetail.setSettleRatio(transactionsDetail.getBigDecimal("settleRatio"));
        //账单金额
        resultDetail.setBillAmount(billDetail.getBigDecimal("billAmount"));
        //账单ID
        resultDetail.setSupplierBillId(billDetail.getString("supplierBillId"));
        Integer serviceTypeId = orderDetail.getServiceTypeId();
        if (serviceTypeId != null) {
            CarBizService carBizService = serviceMapper.selectByPrimaryKey(serviceTypeId);
            resultDetail.setServiceTypeName(carBizService.getServiceName());
        }
        return AjaxResponse.success(resultDetail);
    }

    /**
     * @Description: 查询结算单流水列表
     * @Param: [dto]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/19
     */
    @RequestMapping(value = "/transactions/List", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse queryOrderList(@Validated BusSettleOrderListDTO dto) {
        logger.info(LOG_PRE + "查询特定账单流水列表参数=" + JSON.toJSONString(dto));
        JSONObject result = busSettlementAdviceService.queryOrderList(dto);
        //接口查询失败，需要查看原因
        Integer code = result.getInteger("code");
        if (code == null || code != 0) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "查询失败，请联系管理员");
        }
        JSONObject data = result.getJSONObject("data");
        logger.info(LOG_PRE + "查询特定账单流水列表参数=" + JSON.toJSONString(dto) + " 查询结果=" + data);
        //没有查询到符合条件的数据直接返回
        if (data == null || data.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        JSONArray array = data.getJSONArray("listData");
        if (array == null || array.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        List<String> orderNos = array.stream().map(o -> (JSONObject) o).map(o -> o.getString("orderNo")).distinct().collect(Collectors.toList());
        //查询出所有的巴士车型类别
        List<Map<Object, Object>> maps = busCommonService.queryGroups();
        Map<Integer, String> groupMap = new HashMap<>(16);
        maps.forEach(o -> {
            groupMap.put(Integer.parseInt(String.valueOf(o.get("groupId"))), String.valueOf(o.get("groupName")));
        });
        List<BusOrderExVO> orderExVOS = busAssignmentService.queryOrderDetailByOrderNos(StringUtils.join(orderNos, ","));
        Map<String, BusOrderExVO> orderMap = new HashMap<>(30);
        Map<Integer,String>driverNameMap=new HashMap<>();
        if (orderExVOS != null && !orderExVOS.isEmpty()) {
            Set<Integer> driverIds = orderExVOS.stream().map(order -> order.getDriverId()).filter(Objects::nonNull).collect(Collectors.toSet());
            List<Map<String, Object>> driverList = busCarBizDriverInfoExMapper.queryDriverSimpleBatch(driverIds);
            driverList.forEach(o->{
                        int driverId = Integer.parseInt(o.get("driverId").toString());
                        String driverName = o.get("name") == null ? StringUtils.EMPTY : o.get("name").toString();
                        driverNameMap.put(driverId,driverName);
            });
            orderExVOS.forEach(order -> {
                Integer driverId = order.getDriverId();
                if (driverId != null) {
                    order.setDriverName(driverNameMap.get(driverId));
                }
                // c)预约车别类型
                String groupid = order.getBookingGroupid();
                if (StringUtils.isNotBlank(groupid)) {
                    int groupId = Integer.parseInt(groupid);
                    order.setBookingGroupName(groupMap.get(groupId));
                }
                //服务类型
                Integer serviceTypeId = order.getServiceTypeId();
                if (serviceTypeId != null && serviceTypeId > 0) {
                    order.setServiceTypeName(EnumServiceType.getI18nByValue(serviceTypeId));
                }
                orderMap.put(order.getOrderNo(), order);
            });
        }

        //处理返回结果
        List<BusSettleOrderListVO> collect = array.stream().map(o -> (JSONObject) o).map(o -> {
            BusSettleOrderListVO settleVO = JSONObject.toJavaObject(o, BusSettleOrderListVO.class);
            String orderNo = settleVO.getOrderNo();
            BusOrderExVO order = orderMap.get(orderNo);
            if (order != null) {
                settleVO.setOrderCreateDate(order.getCreateDate());
                settleVO.setBookingDate(order.getBookingDate());
                settleVO.setBookingEndAddr(order.getBookingEndAddr());
                settleVO.setBookingStartAddr(order.getBookingStartAddr());
                settleVO.setDriverName(order.getDriverName());
                settleVO.setServiceName(order.getServiceTypeName());
                settleVO.setBookingGroupName(order.getBookingGroupName());
                settleVO.setEstimatedAmountYuan(order.getEstimatedAmountYuan());
            }
            return settleVO;
        }).collect(Collectors.toList());
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), collect);
        return AjaxResponse.success(page);
    }

    @RequestMapping("/transactions/export")
    public void exportOrderList(@Validated BusSettleOrderListDTO dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(LOG_PRE + "导出特定账单流水列表参数=" + JSON.toJSONString(dto));
        //构建文件名称
        String fileName = BusConstant.buidFileName(request, SupplierMaidConstant.TRANSACTION_FLOW_FILE_NAME);
        //构建文件标题
        List<String> headerList = new ArrayList<>();
        //判断是否为巴士运营权限
        boolean roleBoolean = busCommonService.ifOperate();
        String[] settleHead = SupplierMaidConstant.TRANSACTION_FALOW_FALE_NAME.split(",");
        String[] orderHead = null;
        if (roleBoolean) {
            orderHead = BusConstant.Order.ORDER_HEAD;
        } else {
            orderHead = new String[BusConstant.Order.ORDER_HEAD.length - 3];//无权导出企业信息
            System.arraycopy(BusConstant.Order.ORDER_HEAD, 0, orderHead, 0, BusConstant.Order.ORDER_HEAD.length - 3);
        }
        String[] head = new String[settleHead.length + orderHead.length];
        System.arraycopy(settleHead, 0, head, 0, settleHead.length);
        System.arraycopy(orderHead, 0, head, settleHead.length, orderHead.length);
        headerList.add(StringUtils.join(head, ","));
        CsvUtils utilEntity = new CsvUtils();
        Integer pageNum = 0;
        boolean isFirst = true;
        boolean isList = false;
        //每次查询的最大条数,订单组只支持每次查询30个
        dto.setPageSize(30);
        //查询出所有的巴士车型类别
        List<Map<Object, Object>> maps = busCommonService.queryGroups();
        Map<Integer, String> groupMap = new HashMap<>(16);
        maps.forEach(o -> {
            groupMap.put(Integer.parseInt(String.valueOf(o.get("groupId"))), String.valueOf(o.get("groupName")));
        });
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            JSONObject result = busSettlementAdviceService.queryOrderList(dto);
            //接口查询失败，需要查看原因
            Integer code = result.getInteger("code");
            if (code == null || code != 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("导出失败，请联系管理员");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            JSONObject data = result.getJSONObject("data");
            Long total = data.getLong("total");
            Integer pages = data.getInteger("pages");
            JSONArray array = data.getJSONArray("listData");
            if (total == null || total == 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("没有查询到符合条件的数据");
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
            //遍历结果取出orderNo去调用订单组接口，补充数据
            String orderNos = array.stream().map(o -> (JSONObject) o).map(o -> o.getString("orderNo")).distinct().collect(Collectors.joining(","));
            List<BusOrderExportVO> exportVOS = busAssignmentService.buidExportData(orderNos, groupMap, roleBoolean);
            Map<String, BusOrderExportVO> orderMap = new HashMap<>();
            if (exportVOS != null) {
                exportVOS.forEach(order -> orderMap.put(order.getOrderNo(), order));
            }
            List<String> dataCsv = new ArrayList<>();
            array.forEach(o -> {
                BusSettleOrderListVO settleDetail = JSONObject.toJavaObject((JSONObject) o, BusSettleOrderListVO.class);
                BusSettleOrderExportVO settleOrderExportVO = new BusSettleOrderExportVO();
                if (orderMap.size() > 0) {
                    String orderNo = settleDetail.getOrderNo();
                    BusOrderExportVO orderExportVO = orderMap.get(orderNo);
                    if (orderExportVO != null) {
                        BeanUtils.copyProperties(orderExportVO, settleOrderExportVO);
                    }
                }
                settleOrderExportVO.setFromNum(settleDetail.getOrderNo());
                Integer accountType = settleDetail.getAccountType();
                String accountTypeName = StringUtils.EMPTY;
                if (accountType == 5) {
                    accountTypeName = "巴士分佣收入";
                } else if (accountType == 6) {
                    accountTypeName = "修正订单";
                } else if (accountType == 7) {
                    accountTypeName = "修正账单";
                } else {
                    accountTypeName = "未知类型";
                }
                settleOrderExportVO.setAccountTypeName(accountTypeName);
                settleOrderExportVO.setSettleCreateDate(settleDetail.getCreateDate());
                settleOrderExportVO.setSettleMaidAmount(settleDetail.getSettleAmount());
                String s = busAssignmentService.fieldValueToString(settleOrderExportVO);
                dataCsv.add(s);
            });
            utilEntity.exportCsvV2(response, dataCsv, headerList, fileName, isFirst, isList);
        } while (!isList);
        logger.info(LOG_PRE + "导出特定账单流水列表参数=" + JSON.toJSONString(dto) + " 消耗时间=" + (System.currentTimeMillis() - start));
    }

    /**
     * 修改账单
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/update/save", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse updateSettlement(@Validated BusSettleChangeDTO dto) {
        BusSettlementOrderChangeDTO orderChangeDTO = new BusSettlementOrderChangeDTO();
        BeanUtils.copyProperties(dto, orderChangeDTO);
        orderChangeDTO.setOrderNo(dto.getSupplierBillId() + DateUtil.creatConciseTimeString());
        //业务类型 6修正订单 7修正账单
        orderChangeDTO.setType(7);
        logger.info(LOG_PRE + "修改结算单参数=" + JSON.toJSONString(orderChangeDTO));
        JSONObject result = busSettlementAdviceService.updateSupplierBill(orderChangeDTO);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            logger.error(LOG_PRE + "修改账单失败=" + result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "修改账单失败");
        }
        return AjaxResponse.success(new ArrayList<>());
    }

    /**
     * 修改订单
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/transactions/save", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse orderRevision(@Validated BusSettlementOrderChangeDTO dto) {
        logger.info(LOG_PRE + "修改订单记录,参数=" + JSON.toJSONString(dto));
        //业务类型 6修正订单 7修正账单
        dto.setType(6);
        JSONObject result = busSettlementAdviceService.updateSupplierBill(dto);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            logger.error(LOG_PRE + "修改订单记录失败=" + result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "修改订单记录失败");
        }
        return AjaxResponse.success(new ArrayList<>());
    }


    /**
     * @param supplierBillId
     * @return AjaxResponse
     * @throws
     * @Title: confirm
     * @Description: 结算单确认
     */
    @RequestMapping(value = "/confirm")
    public AjaxResponse confirm(@NotBlank(message = "账单编号不能为空") String supplierBillId) {
        return busSettlementAdviceService.confirm(supplierBillId);
    }

    /**
     * @return AjaxResponse
     * @throws
     * @Title: invoiceInit
     * @Description: 结算单确认收票窗口查询
     */
    @RequestMapping(value = "/invoice/init")
    public AjaxResponse invoiceInit(@NotBlank(message = "账单编号不能为空") String supplierBillId) {
        BusSettlementInvoiceVO invoice = busSettlementAdviceService.queryInvoiceInfo(supplierBillId);
        return AjaxResponse.success(invoice);
    }

    /**
     * @param invoiceDTO
     * @return AjaxResponse
     * @throws IOException
     * @throws
     * @Title: invoiceSave
     * @Description: 结算单确认收票窗口保存
     */
    @RequestMapping(value = "/invoice/save")
    public AjaxResponse invoiceSave(@Validated BusSettlementInvoiceDTO invoiceDTO) throws IOException {
        return busSettlementAdviceService.saveInvoiceInfo(invoiceDTO);
    }

    /**
     * @param supplierBillId
     * @return AjaxResponse
     * @throws
     * @Title: paymentInit
     * @Description: 结算单确认收款窗口查询
     */
    @RequestMapping(value = "/payment/init")
    public AjaxResponse paymentInit(@NotBlank(message = "账单编号不能为空") String supplierBillId) {
        BusSettlementPaymentVO payment = busSettlementAdviceService.queryPaymentInfo(supplierBillId);
        return AjaxResponse.success(payment);
    }

    /**
     * @param invoiceDTO
     * @return AjaxResponse
     * @throws
     * @Title: paymentSave
     * @Description: 结算单确认收款窗口保存
     */
    @RequestMapping(value = "/payment/save")
    public AjaxResponse paymentSave(@Validated BusSettlementPaymentDTO paymentDTO) {
        return busSettlementAdviceService.savePaymentInfo(paymentDTO);
    }

}
