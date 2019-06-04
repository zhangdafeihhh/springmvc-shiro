package com.zhuanche.dto.driver;

import lombok.Data;

/**
 * 司机当期积分和当日积分
 */
@Data
public class DriverIntegralDto {
    private String currentCycleIntegral;//当期积分
    private String currentDayIntegral;//当日积分
    private String driverId;//司机id
    private Integer calcuateCycle;//等级计算周期，1按周计算，2按月计算
}