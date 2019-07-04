package com.zhuanche.serv.mdbcarmanage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.dto.AdvancePaymentDTO.AdvancePaymentDTO;
import com.zhuanche.dto.AdvancePaymentDTO.OrderShortDTO;
import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.entity.mdbcarmanage.DriverApplyAdvanceAudit;
import com.zhuanche.entity.mdbcarmanage.DriverApplyAdvanceOperationLog;
import com.zhuanche.entity.rentcar.OrderCostDetailInfo;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.order.DriverFeeDetailService;
import com.zhuanche.util.Common;
import com.zhuanche.util.SignatureUtils;
import com.zhuanche.util.encrypt.MD5Utils;
import mapper.mdbcarmanage.ex.DriverApplyAdvanceAuditExMapper;
import mapper.mdbcarmanage.ex.DriverApplyAdvanceOperationLogExMapper;
import mapper.rentcar.ex.CarBizDriverInfoExMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ghg on 2019/7/2.
 */
@Service
public class DriverAdvancePaymentService {

    private static Logger logger = LoggerFactory.getLogger(DriverAdvancePaymentService.class);
    @Value("${prorate.new.url}")
    private String prorateNewUrl;
    @Value("${order.server.api.base.url}")
    private String ORDER_SERVICE_API_BASE_URL;

    @Autowired
    private CarBizDriverInfoExMapper carBizDriverInfoExMapper;
    @Autowired
    private DriverApplyAdvanceAuditExMapper driverApplyAdvanceAuditExMapper;
    @Autowired
    private DriverFeeDetailService driverFeeDetailService;
    @Autowired
    private DriverApplyAdvanceOperationLogExMapper driverApplyAdvanceOperationLogExMapper;

    private String ORDER_SERVICE_API_SIGNKEY = Common.MAIN_ORDER_KEY;

    public AjaxResponse queryAdvancePaymentList(Map<String, Object> params) {

        try {
            JSONObject queryResult = MpOkHttpUtil.okHttpPostBackJson(prorateNewUrl + "/platform/pay/detail/by/driverId", params, 0, "查询司机垫付记录");
            if (0 != queryResult.getInteger("code")) {
                logger.info("driverApplyAdvanceAuditService-查询司机垫付记录，param--{}，失败信息--{}", params, queryResult.toJSONString());
                return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
            }
            Map<String, Object> dataMap = JSONObject.parseObject(queryResult.get("data").toString(), Map.class);
            Integer totalSize = 0;
            List<AdvancePaymentDTO> dtoList = Lists.newArrayList();
            if (dataMap.get("totalPageSize") != null) {
                totalSize = Integer.parseInt(String.valueOf(dataMap.get("totalPageSize")));
            }
            if (totalSize == 0) {
                return AjaxResponse.success(new PageDTO());
            }

            logger.info("driverApplyAdvanceAuditService-查询司机垫付记录，param--{}", params);
            dtoList = JSONArray.parseArray(String.valueOf(dataMap.get("repDTOList")), AdvancePaymentDTO.class);
            for (AdvancePaymentDTO dto:dtoList){
                   Date createDate = new Date(dto.getCreateTime());
                   SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                   String createTimeStr = sf.format(createDate);
                   dto.setCreateTimeStr(createTimeStr);
               }
            try {
                if (CollectionUtils.isNotEmpty(dtoList)) {
                    List<String> orderNums = dtoList.stream().map(p -> p.getTradeOrderNo()).collect(Collectors.toList());
                    //封装司机信息
                    DriverVoEntity vo = new DriverVoEntity();
                    vo.setDriverId(String.valueOf(params.get("driverId")));
                    List<DriverVoEntity> driverEntity = carBizDriverInfoExMapper.selectDriverByKeyAddCooperation(vo);
                    DriverVoEntity driverVoEntity = driverEntity.get(0);
                    for (AdvancePaymentDTO dto : dtoList) {
                        dto.setDriverId(Integer.parseInt(driverVoEntity.getDriverId()));
                        dto.setDriverName(driverVoEntity.getName());
                        dto.setDriverPhone(driverVoEntity.getPhone());
                        dto.setSupplierName(driverVoEntity.getSupplierName());
                        dto.setCityName(driverVoEntity.getServiceCity());
                    }
                    //请求订单接口，封装订单完成时间
                    String orderNumStr = String.join(",", orderNums);
                    dtoList = getOrderFinishiDate(dtoList, orderNums, orderNumStr);
                    //封装手动申请信息
                    List<DriverApplyAdvanceAudit> driverApplyAdvanceAudits = driverApplyAdvanceAuditExMapper.queryListDataByOrderNum(orderNums);
                    Map<String, Date> applyDateMap = Maps.newHashMap();
                    if (CollectionUtils.isNotEmpty(driverApplyAdvanceAudits)) {
                        for (DriverApplyAdvanceAudit dto : driverApplyAdvanceAudits) {
                            applyDateMap.put(dto.getOrderNum(), dto.getCreateDate());
                        }
                        for (AdvancePaymentDTO dto : dtoList) {
                            Date applyDate = applyDateMap.get(dto.getTradeOrderNo());
                            if (applyDate != null) {
                                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                dto.setApplyAdvanceTime(sf.format(applyDate));
                            }
                        }
                    }
                    //封装订单金额信息
                    dtoList = getOrderMoney(dtoList, orderNumStr);
                    //封装垫付备注信息
                    dtoList = getAdvanceRemarkInfo(dtoList,orderNums);
                }
            } catch (Exception e) {
                logger.info("driverApplyAdvanceAuditService-封装订单信息异常", e);
            }
            PageDTO pageDto = new PageDTO((int) params.get("page"), (int) params.get("pageNo"), totalSize, dtoList);
            return AjaxResponse.success(pageDto);
        } catch (Exception e) {
            logger.error("driverApplyAdvanceAuditService-查询司机垫付记录异常", e);
            return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
        }

    }

    private List<AdvancePaymentDTO> getAdvanceRemarkInfo(List<AdvancePaymentDTO> dtoList, List<String> orderNums) {
        List<DriverApplyAdvanceOperationLog> driverApplyAdvanceOperationLogs = driverApplyAdvanceOperationLogExMapper.querySuccessApplyLogByOrderNum(orderNums);
        if(CollectionUtils.isEmpty(driverApplyAdvanceOperationLogs)){
            return dtoList;
        }
        Map<String,String> remarkMap = Maps.newHashMap();
        for (DriverApplyAdvanceOperationLog log: driverApplyAdvanceOperationLogs ) {
            remarkMap.put(log.getOrderNum(),log.getOperationRemark());
        }
        for (AdvancePaymentDTO dto: dtoList) {
            String remark = remarkMap.get(dto.getTradeOrderNo());
            if(StringUtils.isBlank(remark)){
                continue;
            }
            dto.setAdvanceRemark(remark);
        }
        return dtoList;

    }

    private List<AdvancePaymentDTO> getOrderMoney(List<AdvancePaymentDTO> dtoList, String orderNumStr) {
        List<OrderCostDetailInfo> ordersCostDetailInfo = driverFeeDetailService.getOrdersCostDetailInfo(orderNumStr);
        if (CollectionUtils.isEmpty(ordersCostDetailInfo)) {
            return dtoList;
        }
        Map<String, BigDecimal> orderMoneyMap = Maps.newHashMap();
        for (OrderCostDetailInfo costInfo : ordersCostDetailInfo) {
            orderMoneyMap.put(costInfo.getOrderNo(), costInfo.getTotalAmount());
        }
        for (AdvancePaymentDTO dto : dtoList) {
            BigDecimal orderMoney = orderMoneyMap.get(dto.getTradeOrderNo());
            if (orderMoney == null) {
                continue;
            }
            dto.setOrderMoney(orderMoney);
        }
        return dtoList;
    }

    private JSONObject getOderInfo(String orderNumStr) {
        HashMap<String, Object> orderMap = Maps.newHashMap();
        orderMap.put("orderNo", orderNumStr);
        orderMap.put("bId", "27");
        String columns = "order_no,fact_end_date";
        orderMap.put("columns", columns);
        try {
            orderMap.put("sign", MD5Utils.getMD5DigestBase64(SignatureUtils.getMD5Sign(orderMap, ORDER_SERVICE_API_SIGNKEY)));
        } catch (Exception e) {
            logger.error("签名错误");
            return null;
        }
        return MpOkHttpUtil.okHttpPostBackJson(ORDER_SERVICE_API_BASE_URL + "/orderMain/getOrdersByOrderNo", orderMap, 0, "查询订单列表");
    }

    private List<AdvancePaymentDTO> getOrderFinishiDate(List<AdvancePaymentDTO> dtoList, List<String> orderNums, String orderNumStr) {
        JSONObject queryOrderResult = getOderInfo(orderNumStr);
        List<OrderShortDTO> orderShortList = Lists.newArrayList();
        Map<String, OrderShortDTO> orderShortEndDateMap = Maps.newHashMap();
        if (0 == queryOrderResult.getInteger("code")) {
            String orderDataList = queryOrderResult.getString("data");
            if (StringUtils.isNotBlank(orderDataList)) {
                orderShortList = JSONArray.parseArray(orderDataList, OrderShortDTO.class);
            }

        } else {
            logger.info("driverApplyAdvanceAuditService-查询批量订单失败，param--{}，失败信息--{}", orderNums, queryOrderResult.getString("msg"));
        }
        if (CollectionUtils.isNotEmpty(orderShortList)) {
            for (OrderShortDTO shortDto : orderShortList) {
                orderShortEndDateMap.put(shortDto.getOrderNo(), shortDto);
            }
            for (AdvancePaymentDTO dto : dtoList) {
                OrderShortDTO shortDTO = orderShortEndDateMap.get(dto.getTradeOrderNo());
                if (null == shortDTO) {
                    continue;
                }
                Long endDateL = shortDTO.getFactEndDate();
                if (endDateL != null) {
                    Date endDate = new Date(endDateL);
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String endDateStr = sf.format(endDate);
                    dto.setOrderFinishTime(endDateStr);
                }
                dto.setOrderId(shortDTO.getOrderId());
            }
        }
        return dtoList;
    }
}
