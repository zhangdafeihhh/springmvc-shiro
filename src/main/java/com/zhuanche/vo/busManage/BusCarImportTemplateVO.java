package com.zhuanche.vo.busManage;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-01-29 15:03
 **/

@Data
public class BusCarImportTemplateVO {
    private String cityName;
    private String supplierName;
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
