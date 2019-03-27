package com.zhuanche.vo.busManage;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BusAuditListVO implements Serializable {
    private String id;

    private String licensePlates;
    private String cityName;
    private String supplierName;
    private String groupName;
    private String modelDetail;
    private Integer status;
    private String color;
    private String fueltype;
    private String transportnumber;
    private String vehicleBrand;
    @JSONField(format = "yyyy-MM-dd")
    private Date createDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextInspectDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextMaintenanceDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date nextOperationDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date carPurchaseDate;

}
