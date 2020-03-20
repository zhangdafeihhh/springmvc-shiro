package com.zhuanche.entity.driver;

import lombok.Data;

import java.util.Date;

/**
 * 消毒记录
 * @author admin
 */
@Data
public class DisinfectRecord {
    private Integer id;

    private Integer driverId;

    private String driverName;

    private String driverPhone;

    private Integer stationId;

    private String stationName;

    private Integer siteStaffId;

    private String siteStaffName;

    private String siteStaffPhone;

    private Integer status;

    private Integer type;

    private Integer cityId;

    private String cityName;

    private Integer stationType;

    private String imgUrl;

    private Date createTime;

    private Date updateTime;

    private Integer disinfectMethod;

    private Integer auditStatus;

    private String stationAddr;

    private String licensePlates;

    private Integer grantMask;

    private Integer grantMaskNumber;

    private Integer operateDay;

}