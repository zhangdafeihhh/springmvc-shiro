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
    public DriverCostDetailVO getDriverCostDetail(String orderNo, int orderId, Integer buyoutFlag) {
        if (StringUtils.isBlank(orderNo) && orderId != 0)
            return null;
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
    public OrderDriverCostDetailVO getOrderDriverCostDetailVO(String orderNo){
        if (StringUtils.isBlank(orderNo)){
            return null;
        }
        try{
            Map<String, Object> params = new HashMap<>(1);
            params.put("orderNo", orderNo);
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
            return result.getJSONObject(Constants.DATA).toJavaObject(OrderDriverCostDetailVO.class);
        }catch (Exception e){
            logger.error("调用计费查询司机费用详情失败 orderNo = " + orderNo, e);
        }
        return null;
    }

    @Override
    public List<OrderDriverCostDetailVO> getOrderDriverCostDetailVOBatch(List<String> orderNos){
        if (orderNos == null || orderNos.isEmpty() || orderNos.size() > 200){
            logger.error("orderNos 参数违法");
            return Collections.EMPTY_LIST;
        }
        try{
            Map<String, Object> params = new HashMap<>(1);
            String orders = String.join(",", orderNos);
            params.put("orderNos", orders);
            String url = DRIVER_FEE_SERVICE_API_BASE_URL + "/orderCost/getOrderDriverCostDetails";
            JSONObject result = MpOkHttpUtil.okHttpGetBackJson(url, params, 1, "");
            if (result == null){
                logger.error("查询计费接口结果为空");
                return Collections.EMPTY_LIST;
            }
            Integer resultCode = result.getInteger(Constants.RESULT);
            if (resultCode == null || resultCode != Constants.SUCCESS_CODE){
                logger.error("批量查询司机费用详情无数据,result : {}", result.toJSONString());
                return Collections.EMPTY_LIST;
            }
            return result.getJSONArray(Constants.DATA).toJavaList(OrderDriverCostDetailVO.class);
        }catch (Exception e){
            logger.error("调用计费批量查询司机费用详情失败", e);
        }
        return Collections.EMPTY_LIST;
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
    public OrderCostDetailInfo getOrderCostDetailInfo(String orderNo) {
        List<OrderCostDetailInfo> list = getOrdersCostDetailInfo(orderNo);
        if (null != list && list.size() > 0)
            return list.get(0);
        return null;
    }

}
