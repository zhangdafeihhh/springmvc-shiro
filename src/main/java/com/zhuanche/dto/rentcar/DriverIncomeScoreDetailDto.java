package com.zhuanche.dto.rentcar;

import lombok.Data;

@Data
public class DriverIncomeScoreDetailDto {
    private String incomeType;
    private String type;
    private String score;
    private String orderNo;
    private Long updateTime;
    private Long createTime;
}
