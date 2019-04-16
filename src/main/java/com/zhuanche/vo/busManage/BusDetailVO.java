package com.zhuanche.vo.busManage;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士saas详情页展示
 * @author: niuzilian
 * @create: 2018-11-23 13:27
 **/
@Data
public class BusDetailVO implements Serializable{
    private String id;
    private Integer carId;
    private Integer cityId;
    private String cityName;
    private Integer supplierId;
    private String supplierName;
    private String licensePlates;
    private Integer groupId;
    private String groupName;
    private String vehicleBrand;
    private String modelDetail;
    private String color;
    private String fueltype;
    private String fuelName;
    private String transportnumber;
    private Integer status;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextInspectDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextMaintenanceDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextOperationDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date carPurchaseDate;
}
