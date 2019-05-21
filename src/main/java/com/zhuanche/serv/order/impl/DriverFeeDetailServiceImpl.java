package com.zhuanche.serv.order.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.rpc.RPCAPI;
import com.zhuanche.common.rpc.RPCResponse;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.DriverCostDetailVO;
import com.zhuanche.entity.rentcar.OrderCostDetailInfo;
import com.zhuanche.entity.rentcar.OrderDriverCostDetailVO;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.serv.order.DriverFeeDetailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DriverFeeDetailServiceImpl implements DriverFeeDetailService {

    @Value("${driver.fee.server.api.base.url}")
    private String DRIVER_FEE_SERVICE_API_BASE_URL;

    private static final Logger logger = LoggerFactory.getLogger(DriverFeeDetailServiceImpl.class);

    @Value("${ordercost.server.api.base.url}")
    private String ORDERCOST_SERVICE_API_BASE_URL;

    /**
     * http://cowiki.01zhuanche.com/pages/viewpage.action?pageId=21053623
     * 费用明细 For H5 司机端费用详情
     * <p>
     * orderNo/orderId传一个即可
     *
     * @param orderNo    订单号
     * @param orderId    订单id
     * @param buyoutFlag 0-非一口价 1-一口价
     */
    @Override
    public DriverCostDetailVO getDriverCostDetail(String orderNo, int orderId, Integer buyoutFlag) {
        if (StringUtils.isBlank(orderNo) && orderId != 0) {
            return null;
        }
        if(null == buyoutFlag){
            OrderCostDetailInfo info = getOrderCostDetailInfo(orderNo);
            if (Objects.nonNull(info)) {
                buyoutFlag = info.getBuyOutFlag();
            } else {
                return null;
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderNo", orderNo);
        params.put("orderId", orderId);
        params.put("isFix", buyoutFlag);
        params.put("isDriver", 1);
        String detail = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, ORDERCOST_SERVICE_API_BASE_URL + "/orderCostdetailDriver/getCostDetailForH5", params, null, "UTF-8");
        if (detail == null) {
            logger.error("查询/orderCostdetailDriver/getCostDetailForH5返回空，入参为：orderNo:" + orderNo + "  orderId:" + orderId + "  buyoutFlag:" + buyoutFlag);
            return null;
        }
        logger.info("调用计费查询司机费用明细接口返回：" + detail);
        RPCResponse detailResponse = RPCResponse.parse(detail);
        if (null == detailResponse || detailResponse.getCode() != 0 || detailResponse.getData() == null) {
            logger.error("查询/orderCostdetailDriver/getCostDetailForH5返回空，入参为：orderNo:" + orderNo + "  orderId:" + orderId + "  buyoutFlag:" + buyoutFlag);
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(detailResponse.getData()), DriverCostDetailVO.class);
    }

    @Override
    public OrderDriverCostDetailVO getOrderDriverCostDetailVO(String orderNo, long orderId){
        if (StringUtils.isBlank(orderNo)){
            return null;
        }
        try{
            Map<String, Object> params = new HashMap<>(1);
            params.put("orderNo", orderNo);
            params.put("orderId", orderId);
            String url = DRIVER_FEE_SERVICE_API_BASE_URL + "/orderCost/getOrderDriverCostDetail";
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(url, params, 1, "");
            if (result == null){
                logger.error("查询计费接口返回为空");
                return null;
            }
            Integer resultCode = result.getInteger(Constants.CODE);
            if (resultCode == null || resultCode != Constants.SUCCESS_CODE){
                logger.info("查询计费司机详情无数据 响应结果: {}", result.toJSONString());
                return null;
            }
            JSONObject data = result.getJSONObject(Constants.DATA);
            if (Objects.nonNull(data)) {
                return data.toJavaObject(OrderDriverCostDetailVO.class);
            }
            return null;
        }catch (Exception e){
            logger.error("调用计费查询司机费用详情失败 orderNo = " + orderNo, e);
        }
        return null;
    }

    /**
     * 根据订单号获取司机费用明细（批量）
     *
     * @param orderIds
     * @return
     */
    public List<OrderDriverCostDetailVO> getOrderDriverCostDetailVOBatch(List<String> orderIds) {
        if (null == orderIds || orderIds.size() == 0) {
            logger.info("接口调用入参订单号为空");
            return new ArrayList<>();
        }
        try {
            Map<String, Object> httpParams = new HashMap<>();
            httpParams.put("orderIds", String.join(",", orderIds));
            String orderInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, DRIVER_FEE_SERVICE_API_BASE_URL + "/orderCost/findOrderDriverCostDetails", httpParams, null, "UTF-8");
            if (orderInfo == null) {
                logger.error("查询/orderCost/findOrderDriverCostDetails返回空，入参为：" + String.join(",", orderIds));
                return new ArrayList<>();
            }
            logger.info("调用计费查询司机费用明细接口返回：" + orderInfo);
            RPCResponse orderResponse = RPCResponse.parse(orderInfo);
            if (null == orderResponse || orderResponse.getCode() != 0 || orderResponse.getData() == null) {
                logger.info("相关司机费用信息不存在，入参订单号：" + String.join(",", orderIds));
                return new ArrayList<>();
            }
            return JSON.parseArray(JSON.toJSONString(orderResponse.getData()), OrderDriverCostDetailVO.class);
        } catch (Exception e) {
            logger.error("查询/orderCost/getOrderDriverCostDetails异常:", e);
            return new ArrayList<>();
        }
    }



    /**
     * 查询订单明细(批量)
     *
     * @param orderNos “P1439635871018071”，“P1439635928276329”
     * @return
     */
    public List<OrderCostDetailInfo> getOrdersCostDetailInfo(String orderNos) {
        if (StringUtils.isBlank(orderNos))
            return null;
        String orderInfo = new RPCAPI().requestWithRetry(RPCAPI.HttpMethod.GET, String.format(ORDERCOST_SERVICE_API_BASE_URL + "/orderCostdetail/%s", orderNos), null, null, "UTF-8");
        if (StringUtils.isBlank(orderInfo) || orderInfo.equals("true\r\n")) {
            logger.error("查询/getOrderCostDetailInfo，入参为：" + orderNos + "，返回空");
            return null;
        }
        RPCResponse orderResponse = RPCResponse.parse(orderInfo);
        if (null == orderResponse || orderResponse.getCode() != 0 || orderResponse.getData() == null) {
            logger.info("查询订单明细，入参订单号：" + orderNos);
            return null;
        }
        return JSON.parseArray(JSON.toJSONString(orderResponse.getData()), OrderCostDetailInfo.class);
    }

    /**
     * 查询订单明细(单个)
     *
     * @param orderNo P1439635871018071
     * @return
     */
    @Override
    public OrderCostDetailInfo getOrderCostDetailInfo(String orderNo) {
        List<OrderCostDetailInfo> list = getOrdersCostDetailInfo(orderNo);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

}
