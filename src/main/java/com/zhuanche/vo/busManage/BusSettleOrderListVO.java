package com.zhuanche.vo.busManage;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: mp-manage
 * @description: 巴士供应商 分佣账单流水 列表返回的VO
 * @author: niuzilian
 * @create: 2018-12-19 18:26
 **/
@Data
public class BusSettleOrderListVO {
    private Integer id;
    private String orderNo;
    private BigDecimal settleAmount;
    private String createDate;
    private Integer accountType;
    private String orderDetail;

}
