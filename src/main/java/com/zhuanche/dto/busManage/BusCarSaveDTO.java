package com.zhuanche.dto.busManage;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 保存车辆接收参数类
 * @author: niuzilian
 * @create: 2019-01-25 21:13
 **/
@Data
public class BusCarSaveDTO implements Serializable {

    private Integer carId;
    private Integer cityId;
    private Integer supplierId;
    @NotBlank(message = "车牌号不能为空")
    private String licensePlates;
    @NotNull(message = "车型类别不能为空")
    private Integer groupId;
    @NotBlank(message = "车辆厂牌不能为空")
    private String vehicleBrand;
    private String modelDetail;
    @NotBlank(message = "车辆颜色不能为空")
    private String color;
    @NotBlank(message = "燃料类型不能为空")
    private String fueltype;
    @NotBlank(message = "运输证字号不能为空")
    private String transportnumber;
    @NotNull(message = "状态不能为空")
    private Integer status;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nextInspectDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nextMaintenanceDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date nextOperationDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date carPurchaseDate;
}
