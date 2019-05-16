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
    private String driverId;
    private String name;
    private String phone;
    private String cityId;
    private String itemScore;
    private String itemKey;
    private String calFormula;
    private String status;
    private String day;
    private String hour;
    private String incomeTypeName;//出行分类型
    private String typeName;//时长分类型
    private String updateTimeStr;
}
