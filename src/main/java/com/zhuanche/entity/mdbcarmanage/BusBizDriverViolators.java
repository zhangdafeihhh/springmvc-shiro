package com.zhuanche.entity.mdbcarmanage;

import lombok.Data;

import java.util.Date;

@Data
public class BusBizDriverViolators {
    private Integer id;

    private Integer busDriverId;

    private String busDriverName;

    private String busDriverPhone;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private Integer groupId;

    private String groupName;

    private String evaluateScore;

    private String idNumber;

    private Short punishType;

    private String punishReason;

    private Double punishDuration;

    private Date punishStartTime;

    private Date punishEndTime;

    private Date createTime;

    private Date updateTime;

    private Short punishStatus;
}