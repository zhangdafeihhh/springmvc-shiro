package com.zhuanche.dto.driver;

import java.math.BigDecimal;

/**
 * @Author fanht
 * @Description  计费返回的数据
 * @Date 2018/12/20 下午6:49
 * @Version 1.0
 */
public class DriverIncome {

    /**当段日期完成订单量**/
    private Integer orderCounts;

    /**当段日期营业额**/
    private BigDecimal incomeAmount;

    public Integer getOrderCounts() {
        return orderCounts;
    }

    public void setOrderCounts(Integer orderCounts) {
        this.orderCounts = orderCounts;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public DriverIncome(Integer orderCounts, BigDecimal incomeAmount) {
        this.orderCounts = orderCounts;
        this.incomeAmount = incomeAmount;
    }
}
