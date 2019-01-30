package com.zhuanche.vo.busManage;

import lombok.Data;

import java.util.Date;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-01-29 20:14
 **/
@Data
public class BusCarBaseImportTemplateVO {
    private String licensePlates;
    private String groupName;
    private String color;
    private String fuelName;
    private String  transportnumber;
    private String vehicleBrand;
    private String modelDetail;
    private Date nextInspectDate;
    private Date nextMaintenanceDate;
    private Date nextOperationDate;
    private Date carPurchaseDate;
}
