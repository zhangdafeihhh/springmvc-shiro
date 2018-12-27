package com.zhuanche.dto.busManage;

import com.zhuanche.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @program: mp-manage
 * @description: 巴士供应商结算单流水列表查询DTO
 * @author: niuzilian
 * @create: 2018-12-19 18:05
 **/
public class BusSettleOrderListDTO extends BaseDTO{
    /** 账单编号 **/
    @NotBlank(message = "账单编号不能为空")
    private String userId;
    private String orderNo;
    private String startTime;
    private String endTime;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
