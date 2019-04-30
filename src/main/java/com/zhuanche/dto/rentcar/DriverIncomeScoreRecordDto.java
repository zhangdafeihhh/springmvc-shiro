package com.zhuanche.dto.rentcar;

import lombok.Data;

/**
 * 司机收入分更新记录
 */
@Data
public class DriverIncomeScoreRecordDto {
    private String name;
    private String phone;
    private String incomeScore;//收入分
    private String tripScore;//出行分
    private String serviceScore;//服务分
    private String appendScore;//附加分
    private Long updateTime;//更新时间, 时间戳
    private String driverId;//driverId
    private String changeScore;//与前一天对比分值

}
