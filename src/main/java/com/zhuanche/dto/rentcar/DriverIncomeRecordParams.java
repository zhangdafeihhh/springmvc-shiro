package com.zhuanche.dto.rentcar;

import com.zhuanche.entity.common.BaseEntity;
import lombok.Data;

@Data
public class DriverIncomeRecordParams extends BaseEntity {
    private String driverId;
    private String incomeType;
    private String type;
    private String orderNo;
    private String startDate;
    private String endDate;

}
