package com.zhuanche.dto.rentcar;

import lombok.Data;

@Data
public class DriverIncomeScoreDto {
    private String incomeScore;//收入分
    private String beatRate;//击败司机比率，90代表90%
    private Integer driverId;
    private Long updateTime;//更新时间，时间戳
}
