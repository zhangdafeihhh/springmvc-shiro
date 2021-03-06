package com.zhuanche.controller.busManage;

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
import com.zhuanche.constants.busManage.EnumServiceType;
import com.zhuanche.dto.busManage.*;
import com.zhuanche.entity.busManage.BusCostDetail;
import com.zhuanche.entity.busManage.BusOrderDetail;
import com.zhuanche.entity.rentcar.CarBizService;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.CarBizSupplierService;
import com.zhuanche.serv.busManage.*;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.excel.CsvUtils;
import com.zhuanche.vo.busManage.*;
import mapper.rentcar.CarBizServiceMapper;
import mapper.rentcar.ex.BusCarBizDriverInfoExMapper;
import mapper.rentcar.ex.CarBizCarGroupExMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: BusSettlementAdviceController
 * @Description: ?????????????????????
 * @author: yanyunpeng
 * @date: 2018???12???7??? ??????11:28:50
 */
@RestController
@RequestMapping("/bus/settlement")
@Validated
public class BusSettlementAdviceController {

    private static final Logger logger = LoggerFactory.getLogger(BusSettlementAdviceController.class);
    private static final String LOG_PRE = "????????????????????????????????????";

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

    @Value("${order.pay.url}")
    private String orderPayUrl;


    /**
     * ???????????????????????????
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/pageList")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleDetailList(BusSupplierSettleListDTO dto) {
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto));
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
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "?????????????????????????????????");
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
        logger.info(LOG_PRE + "????????????????????????=" + JSON.toJSONString(dto) + "??????=" + JSON.toJSONString(collect));
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
        //??????????????????????????????
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
        logger.info("???????????????????????????????????????=" + JSON.toJSONString(dto));
        //????????????
        String fileName = BusConstant.buidFileName(request, SupplierMaidConstant.BILL_FILE_NAME);
        //??????????????????
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
                    csvDataList.add("???????????????????????????");
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
                csvDataList.add("??????????????????????????????????????????????????????");
                isList = true;
                utilEntity.exportCsvV2(response, csvDataList, headerList, fileName, isFirst, isList);
                break;
            }
            List<String> csvData;
            JSONArray array = result.getJSONArray("data");
            boolean isnull = (array == null || array.isEmpty());
            if (isnull && pageNum == 1) {
                csvData = new ArrayList<>();
                csvData.add("???????????????????????????");
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
            // isList=true????????????????????????????????????
        } while (!isList);
        logger.info(LOG_PRE + "???????????????????????????????????????=" + JSON.toJSONString(dto) + " ????????????=" + (System.currentTimeMillis() - start));
    }

    /**
     * ?????????????????????
     *
     * @param supplierBillId
     * @return
     */
    @RequestMapping("/detail")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleDetail(@Verify(param = "supplierBillId", rule = "required") String supplierBillId, HttpServletRequest request) {
        JSONObject result = busSettlementAdviceService.getBillDetail(supplierBillId);
        if (result == null) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "?????????????????????????????????");
        }
        BusSettlementDetailVO busSettlementDetailVO = JSONObject.toJavaObject(result, BusSettlementDetailVO.class);
        Integer supplierId = result.getInteger("supplierId");
        CarBizSupplier carBizSupplier = supplierService.selectByPrimaryKey(supplierId);
        busSettlementDetailVO.setSupplierName(carBizSupplier.getSupplierFullName());
        //??????????????????????????????????????????????????????0
        if (busSettlementDetailVO.getLastAmount() == null) {
            busSettlementDetailVO.setLastAmount(new BigDecimal(0));
        }
        return AjaxResponse.success(busSettlementDetailVO);
    }

    /**
     * @Description: ????????????????????????????????????
     * @Param: [supplierBillId]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/20
     */
    @RequestMapping("/update/init")
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse querySettleUpdateInit(@Verify(param = "supplierBillId", rule = "required") String supplierBillId) {
        JSONObject result = busSettlementAdviceService.getBillDetail(supplierBillId);
        if (result == null) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "?????????????????????????????????");
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
        //?????????????????????
        JSONObject billDetail = busSettlementAdviceService.getBillDetail(supplierBillId);
        //??????????????????
        JSONObject transactionsDetail = busSettlementAdviceService.getTransactionsDetail(id);
        if (billDetail == null || transactionsDetail == null) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "?????????????????????????????????");
        }
        //????????????????????????????????????
        String orderNo = transactionsDetail.getString("orderNo");
        BusOrderDetail orderDetail = busOrderService.selectOrderDetail(orderNo);


        //????????????
        BusTransactionsDetailVO resultDetail = new BusTransactionsDetailVO();
        resultDetail.setOrderNo(orderNo);
        //????????????
        resultDetail.setOrderAmount(transactionsDetail.getBigDecimal("orderAmount"));

        resultDetail.setBookingGroupName(orderDetail.getBookingGroupName());
        resultDetail.setLicensePlates(orderDetail.getLicensePlates());
        //???????????????
        resultDetail.setBookingUserName(orderDetail.getRiderName() != null ? orderDetail.getRiderName() : StringUtils.EMPTY);
        //????????????
        resultDetail.setSettleRatio(transactionsDetail.getBigDecimal("settleRatio"));
        //????????????
        resultDetail.setBillAmount(billDetail.getBigDecimal("billAmount"));
        //??????ID
        resultDetail.setSupplierBillId(billDetail.getString("supplierBillId"));
        Integer serviceTypeId = orderDetail.getServiceTypeId();
        if (serviceTypeId != null) {
            CarBizService carBizService = serviceMapper.selectByPrimaryKey(serviceTypeId);
            resultDetail.setServiceTypeName(carBizService.getServiceName());
        }
        return AjaxResponse.success(resultDetail);
    }

    /**
     * @Description: ???????????????????????????
     * @Param: [dto]
     * @return: com.zhuanche.common.web.AjaxResponse
     * @Date: 2018/12/19
     */
    @RequestMapping(value = "/transactions/List", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse queryOrderList(@Validated BusSettleOrderListDTO dto) {
        logger.info(LOG_PRE + "????????????????????????????????????=" + JSON.toJSONString(dto));
        JSONObject result = busSettlementAdviceService.queryOrderList(dto);
        //???????????????????????????????????????
        Integer code = result.getInteger("code");
        if (code == null || code != 0) {
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "?????????????????????????????????");
        }
        JSONObject data = result.getJSONObject("data");
        logger.info(LOG_PRE + "????????????????????????????????????=" + JSON.toJSONString(dto) + " ????????????=" + data);
        //????????????????????????????????????????????????
        if (data == null || data.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        JSONArray array = data.getJSONArray("listData");
        if (array == null || array.isEmpty()) {
            return AjaxResponse.success(new ArrayList<>());
        }
        List<String> orderNos = array.stream().map(o -> (JSONObject) o)
                .filter(o->{
                    Integer accountType = o.getInteger("accountType");
                    return accountType!=null&&(accountType==5||accountType==6);})//5 ?????????????????? 6???????????? 7????????????, 5/6????????????????????????
                .map(o -> o.getString("orderNo"))
                .distinct().collect(Collectors.toList());
        //????????????????????????????????????
        List<Map<Object, Object>> maps = busCommonService.queryGroups();
        Map<Integer, String> groupMap = new HashMap<>(16);
        maps.forEach(o -> {
            groupMap.put(Integer.parseInt(String.valueOf(o.get("groupId"))), String.valueOf(o.get("groupName")));
        });
        //????????????????????????
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
                // c)??????????????????
                String groupid = order.getBookingGroupid();
                if (StringUtils.isNotBlank(groupid)) {
                    int groupId = Integer.parseInt(groupid);
                    order.setBookingGroupName(groupMap.get(groupId));
                }
                //????????????
                Integer serviceTypeId = order.getServiceTypeId();
                if (serviceTypeId != null && serviceTypeId > 0) {
                    order.setServiceTypeName(EnumServiceType.getI18nByValue(serviceTypeId));
                }
                orderMap.put(order.getOrderNo(), order);
            });
        }
        
        //????????????????????????
        Map<String, BusCostDetail> orderCostMap = new HashMap<>();
        JSONArray busCostList = busAssignmentService.getBusCostDetailList(StringUtils.join(orderNos, ","));
		if (busCostList != null && !busCostList.isEmpty()) {
			busCostList.stream().map(o -> JSONObject.toJavaObject((JSONObject) o, BusCostDetail.class))
					.forEach(cost -> {
						orderCostMap.put(cost.getOrderNo(), cost);
					});
		}

        //??????????????????
        List<BusSettleOrderListVO> collect = array.stream().map(o -> (JSONObject) o).map(o -> {
            BusSettleOrderListVO settleVO = JSONObject.toJavaObject(o, BusSettleOrderListVO.class);
            String orderNo = settleVO.getOrderNo();
            // ????????????
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
			// ????????????
			BusCostDetail cost = orderCostMap.get(orderNo);
			if (cost != null) {
				settleVO.setAmount(cost.getAmount());
			}
            return settleVO;
        }).collect(Collectors.toList());
        PageDTO page = new PageDTO(data.getInteger("pageNo"), data.getInteger("pageSize"), data.getLong("total"), collect);
        return AjaxResponse.success(page);
    }

    @RequestMapping("/transactions/export")
    public void exportOrderList(@Validated BusSettleOrderListDTO dto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        long start = System.currentTimeMillis();
        logger.info(LOG_PRE + "????????????????????????????????????=" + JSON.toJSONString(dto));
        //??????????????????
        String fileName = BusConstant.buidFileName(request, SupplierMaidConstant.TRANSACTION_FLOW_FILE_NAME);
        //??????????????????
        List<String> headerList = new ArrayList<>();
        //?????????????????????????????????
        boolean roleBoolean = busCommonService.ifOperate();
        String[] settleHead = SupplierMaidConstant.TRANSACTION_FALOW_FALE_NAME.split(",");
        String[] orderHead = null;
        if (roleBoolean) {
            orderHead = BusConstant.Order.ORDER_HEAD;
        } else {
            orderHead = new String[BusConstant.Order.ORDER_HEAD.length - 3];//????????????????????????
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
        //???????????????????????????,??????????????????????????????30???
        dto.setPageSize(30);
        do {
            pageNum++;
            dto.setPageNum(pageNum);
            JSONObject result = busSettlementAdviceService.queryOrderList(dto);
            //???????????????????????????????????????
            Integer code = result.getInteger("code");
            if (code == null || code != 0) {
                List<String> csvDataList = new ArrayList<>();
                csvDataList.add("?????????????????????????????????");
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
                csvDataList.add("????????????????????????????????????");
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
            //??????????????????orderNo???????????????????????????????????????
            String orderNos = array.stream().map(o -> (JSONObject) o)
                    .filter(o->{
                        Integer accountType = o.getInteger("accountType");
                        return accountType!=null&&(accountType==5||accountType==6);})//5 ?????????????????? 6???????????? 7????????????, 5/6????????????????????????
                         .map(o -> o.getString("orderNo")).distinct().collect(Collectors.joining(","));
            List<BusOrderExportVO> exportVOS = busAssignmentService.buidExportData(orderNos,roleBoolean);
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
                    accountTypeName = "??????????????????";
                } else if (accountType == 6) {
                    accountTypeName = "????????????";
                } else if (accountType == 7) {
                    accountTypeName = "????????????";
                } else {
                    accountTypeName = "????????????";
                }
                settleOrderExportVO.setAccountTypeName(accountTypeName);
                settleOrderExportVO.setSettleCreateDate(settleDetail.getCreateDate());
                settleOrderExportVO.setSettleMaidAmount(settleDetail.getSettleAmount());
                String s = busAssignmentService.fieldValueToString(settleOrderExportVO);
                dataCsv.add(s);
            });
            utilEntity.exportCsvV2(response, dataCsv, headerList, fileName, isFirst, isList);
        } while (!isList);
        logger.info(LOG_PRE + "????????????????????????????????????=" + JSON.toJSONString(dto) + " ????????????=" + (System.currentTimeMillis() - start));
    }

    /**
     * ????????????
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
        //???????????? 6???????????? 7????????????
        orderChangeDTO.setType(7);
        logger.info(LOG_PRE + "?????????????????????=" + JSON.toJSONString(orderChangeDTO));
        JSONObject result = busSettlementAdviceService.updateSupplierBill(orderChangeDTO);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            logger.error(LOG_PRE + "??????????????????=" + result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "??????????????????");
        }
        return AjaxResponse.success(new ArrayList<>());
    }

    /**
     * ????????????
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/transactions/save", method = RequestMethod.POST)
    @MasterSlaveConfigs(configs = @MasterSlaveConfig(databaseTag = "rentcar-DataSource", mode = DataSourceMode.SLAVE))
    public AjaxResponse orderRevision(@Validated BusSettlementOrderChangeDTO dto) {
        logger.info(LOG_PRE + "??????????????????,??????=" + JSON.toJSONString(dto));
        //???????????? 6???????????? 7????????????
        dto.setType(6);
        JSONObject result = busSettlementAdviceService.updateSupplierBill(dto);
        Integer code = result.getInteger("code");
        if (code == null || 0 != code) {
            logger.error(LOG_PRE + "????????????????????????=" + result.getString("msg"));
            return AjaxResponse.failMsg(RestErrorCode.UNKNOWN_ERROR, "????????????????????????");
        }
        return AjaxResponse.success(new ArrayList<>());
    }


    /**
     * @param supplierBillId
     * @return AjaxResponse
     * @throws
     * @Title: confirm
     * @Description: ???????????????
     */
    @RequestMapping(value = "/confirm")
    public AjaxResponse confirm(@NotBlank(message = "????????????????????????") String supplierBillId) {
        return busSettlementAdviceService.confirm(supplierBillId);
    }

    /**
     * @return AjaxResponse
     * @throws
     * @Title: invoiceInit
     * @Description: ?????????????????????????????????
     */
    @RequestMapping(value = "/invoice/init")
    public AjaxResponse invoiceInit(@NotBlank(message = "????????????????????????") String supplierBillId) {
        BusSettlementInvoiceVO invoice = busSettlementAdviceService.queryInvoiceInfo(supplierBillId);
        return AjaxResponse.success(invoice);
    }

    /**
     * @param invoiceDTO
     * @return AjaxResponse
     * @throws IOException
     * @throws
     * @Title: invoiceSave
     * @Description: ?????????????????????????????????
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
     * @Description: ?????????????????????????????????
     */
    @RequestMapping(value = "/payment/init")
    public AjaxResponse paymentInit(@NotBlank(message = "????????????????????????") String supplierBillId) {
        BusSettlementPaymentVO payment = busSettlementAdviceService.queryPaymentInfo(supplierBillId);
        return AjaxResponse.success(payment);
    }

    /**
     * @param
     * @return AjaxResponse
     * @throws
     * @Title: paymentSave
     * @Description: ?????????????????????????????????
     */
    @RequestMapping(value = "/payment/save")
    public AjaxResponse paymentSave(@Validated BusSettlementPaymentDTO paymentDTO) {
        return busSettlementAdviceService.savePaymentInfo(paymentDTO);
    }

    @RequestMapping(value = "/exportBillsSummary")
    public void exportBillsSummary(@Verify(param = "supplierBillId",rule = "required") String supplierBillId,
                                           HttpServletRequest request, HttpServletResponse response)throws Exception{
        HashMap<String,Object> params=new HashMap<>();
        params.put("supplierBillId",supplierBillId);
        String fileName = "????????????" + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), com.zhuanche.util.dateUtil.DateUtil.intTimestampPattern);
        //???????????????????????????????????????
        String agent = request.getHeader("User-Agent").toUpperCase();
        //IE????????????Edge?????????
        if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {  //???????????????
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
        }
        //???????????????
        XSSFWorkbook wb=new XSSFWorkbook();


        //????????????
        XSSFSheet sheet = wb.createSheet("????????????");
        //????????????
        sheet.setHorizontallyCenter(true);
        //????????????
        sheet.setFitToPage(true);


        //??????????????????
        XSSFCellStyle titleStyle = wb.createCellStyle();
        //????????????
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setWrapText(true);
        //??????????????????
        titleStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //??????????????????
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //??????
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight((short)12);//????????????
        titleFont.setFontHeightInPoints((short)12);
        titleStyle.setFont(titleFont);



        //??????head??????
        XSSFCellStyle head_style = wb.createCellStyle();
        head_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        head_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        head_style.setBorderTop(BorderStyle.THIN);
        head_style.setBorderBottom(BorderStyle.THIN);
        head_style.setBorderLeft(BorderStyle.THIN);
        head_style.setBorderRight(BorderStyle.THIN);
        head_style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        head_style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        head_style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        head_style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        head_style.setWrapText(true);
        //????????????????????????
        head_style.setAlignment(HorizontalAlignment.CENTER);
        //????????????????????????
        head_style.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont head_font = wb.createFont();
        head_font.setBold(true);
        head_style.setFont(head_font);

        //????????????
        XSSFCellStyle style = wb.createCellStyle();
        //????????????????????????
        style.setAlignment(HorizontalAlignment.CENTER);
        //????????????????????????
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //??????????????????
        style.setWrapText(true);

        //??????Excel???????????????????????????????????????????????????
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //set border color
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());





        XSSFRow title_row = sheet.createRow(0);
        title_row.setHeightInPoints(50);
        for(int i=0;i<6;i++){
            sheet.setColumnWidth(i,8000);
            XSSFCell title_cell = title_row.createCell(i);
            title_cell.setCellStyle(titleStyle);
        }
        title_row.getCell(0).setCellValue("????????????");
        //???????????????
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,5));


        ArrayList<String>head=new ArrayList<>(6);
        head.add("????????????????????????");
        head.add("??????????????????????????????");
        head.add("???????????????");
        head.add("???????????????");
        head.add("????????????");
        head.add("??????????????????");
        XSSFRow head_row = sheet.createRow(1);
        XSSFRow context_row = sheet.createRow(2);
        head_row.setHeightInPoints(20);
        for(int i=0;i<head.size();i++){
            XSSFCell cell = head_row.createCell(i);
            cell.setCellValue(head.get(i));
            cell.setCellStyle(head_style);
            XSSFCell context_cell = context_row.createCell(i);
            context_cell.setCellStyle(style);
        }


        XSSFCellStyle remarks_Style = wb.createCellStyle();
        remarks_Style.setAlignment(HorizontalAlignment.LEFT);
        remarks_Style.setVerticalAlignment(VerticalAlignment.CENTER);
        remarks_Style.setBorderTop(BorderStyle.NONE);
        remarks_Style.setBorderBottom(BorderStyle.NONE);
        remarks_Style.setBorderLeft(BorderStyle.NONE);
        remarks_Style.setBorderRight(BorderStyle.NONE);
        //??????????????????
        remarks_Style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //??????????????????
        remarks_Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont remarks_font = wb.createFont();
        remarks_font.setFontHeight((short)9);
        remarks_font.setFontHeightInPoints((short)9);
        remarks_Style.setFont(remarks_font);

        ArrayList<String>remarks = new ArrayList<>();
        remarks.add("??????1???   ????????????=???????????????????????????-?????????????????????????????????*???1-??????????????????+??????????????????????????????+????????????");
        remarks.add("??????2:    ????????????????????????????????????");
        remarks.add("          ?????????????????????????????????????????????");
        remarks.add("          ?????????????????????91120118328676979R");
        remarks.add("          ???????????????????????????????????????????????????3???2???6016??? 010-65206159");
        remarks.add("          ??????????????????????????????????????????????????? 110060635018800021058");
        for(int i=0;i<remarks.size();i++){
            int rowIdx =3+i;
            sheet.addMergedRegion(new CellRangeAddress(rowIdx,rowIdx,0,5));
            XSSFRow row = sheet.createRow(rowIdx);
            if(rowIdx==3){
                row.setHeightInPoints(70);
            }else{
                row.setHeightInPoints(20);
            }
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(remarks.get(i));
            cell.setCellStyle(remarks_Style);
        }

        String url=orderPayUrl+"/settle/supplier/findOrderFlow?supplierBillId="+supplierBillId;
        try {
            String rpc_response = MpOkHttpUtil.okHttpGet(url, 3, "??????????????????");
            JSONObject reponse_json = JSONObject.parseObject(rpc_response);
            Integer code = reponse_json.getInteger("code");
            JSONObject data = reponse_json.getJSONObject("data");
            if(code==0&&data!=null){
                DecimalFormat decimalFormat = new DecimalFormat("#########0.##");
                BigDecimal orderAmount = data.getBigDecimal("orderAmount");
                context_row.getCell(0).setCellValue(decimalFormat.format(orderAmount));
                BigDecimal driverPaymentAmount = data.getBigDecimal("driverPaymentAmount");
                context_row.getCell(1).setCellValue(decimalFormat.format(driverPaymentAmount));
                BigDecimal settleRatio = data.getBigDecimal("settleRatio");
                context_row.getCell(2).setCellValue(decimalFormat.format(settleRatio));
                BigDecimal factCollectAmount = data.getBigDecimal("factCollectAmount");
                context_row.getCell(3).setCellValue(decimalFormat.format(factCollectAmount));
                BigDecimal otherAmount = data.getBigDecimal("otherAmount");
                context_row.getCell(4).setCellValue(decimalFormat.format(otherAmount));
                BigDecimal shouldSettleAmount = data.getBigDecimal("shouldSettleAmount");
                context_row.getCell(5).setCellValue(decimalFormat.format(shouldSettleAmount));

                Integer supplierId = data.getInteger("supplierId");
                CarBizSupplier carBizSupplier = supplierService.selectByPrimaryKey(supplierId);
                String supplierName=carBizSupplier!=null?carBizSupplier.getSupplierFullName():"";
                String startTime =data.getString("startTime");
                String endTime = data.getString("endTime");
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String title=supplierName+" "+df.format(LocalDateTime.parse(startTime,df2))+"-"+df.format(LocalDateTime.parse(endTime,df2))+"????????????";
                sheet.getRow(0).getCell(0).setCellValue(title);
            }else{
                logger.error("??????????????????????????????????????????code={},msg={}",code,reponse_json.getString("msg"));
                context_row.getCell(0).setCellValue("???????????????????????????");
            }
        } catch (Exception e) {
            logger.error("????????????????????????????????????????????????{}",e);
            context_row.getCell(0).setCellValue("???????????????????????????");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename="+fileName + ".xlsx");
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        ServletOutputStream out = response.getOutputStream();
        wb.write(out);
        out.flush();
        out.close();
    }

}
