package com.zhuanche.serv.order;

import com.zhuanche.entity.rentcar.OrderDriverCostDetailVO;

import java.util.List;

public interface DriverFeeDetailService {

    OrderDriverCostDetailVO getOrderDriverCostDetailVO(String orderNo);

    List<OrderDriverCostDetailVO> getOrderDriverCostDetailVOBatch(List<String> orderNos);
}
