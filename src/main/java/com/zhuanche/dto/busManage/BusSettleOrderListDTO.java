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

    private String startDate;
    private String endDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
