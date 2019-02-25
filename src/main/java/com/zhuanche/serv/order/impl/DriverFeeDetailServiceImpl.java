package com.zhuanche.serv.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.constant.Constants;
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
            Integer resultCode = result.getInteger(Constants.RESULT);
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

    public List<OrderDriverCostDetailVO> getOrderDriverCostDetailVOBatch(Set<String> orderNos){
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

}
