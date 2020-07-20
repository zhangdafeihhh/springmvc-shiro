package com.zhuanche.dto.bigdata;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/20 上午11:17
 * @Version 1.0
 */
@Data
public class BiNewcityDriverReportDayDto {


    /**司机总数*/
    private Integer driverTotal;

    /**榜单量*/
    private Integer bindOrderNumTotal;

    /**司机完单总数*/
    private Integer settleOrderNumTotal;

    /**总完单流水*/
    private BigDecimal totalAmountTotal;


}
