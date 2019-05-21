package com.zhuanche.serv.order;

import com.zhuanche.dto.DriverCostDetailVO;
import com.zhuanche.entity.rentcar.OrderCostDetailInfo;
import com.zhuanche.entity.rentcar.OrderDriverCostDetailVO;

import java.util.List;

public interface DriverFeeDetailService {

    OrderDriverCostDetailVO getOrderDriverCostDetailVO(String orderNo, long orderId);


    List<OrderDriverCostDetailVO> getOrderDriverCostDetailVOBatch(List<String> orderNos);
    List<OrderCostDetailInfo> getOrdersCostDetailInfo(String orderNos);
    OrderCostDetailInfo getOrderCostDetailInfo(String orderNo);

    DriverCostDetailVO getDriverCostDetail(String orderNo, int orderId, Integer buyoutFlag);
}
