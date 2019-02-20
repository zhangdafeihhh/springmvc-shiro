package com.zhuanche.entity.driver;

import lombok.Data;

import java.util.Date;

@Data
public class DriverActionVO {

    private String driverName;
    private String driverPhone;
    private String driverLicense;
    private Integer driverId;
    private String time;
    private Date actionTime;
    private String actionName;
    private Integer actionId;
    private String orderNo;

}
