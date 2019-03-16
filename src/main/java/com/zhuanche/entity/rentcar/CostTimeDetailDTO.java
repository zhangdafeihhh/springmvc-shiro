package com.zhuanche.entity.rentcar;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CostTimeDetailDTO {
    private Integer id;  //主键

    private String orderNo; //订单号

    private String startTime; //开始时间

    private String endTime; //结束时间

    private BigDecimal price;  //时间段单价

    private BigDecimal amount;   //时间段金额

    private BigDecimal number;  //数量   公里|分钟

    private Integer type;  //类型 （1：里程 2：时长）

    private Integer priceType;  //计价类型（1 高峰，2夜间)

    private Date createDate;

    private Date updateDate;
}
