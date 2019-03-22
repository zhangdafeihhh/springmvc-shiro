package com.zhuanche.mongo.busManage;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士车辆审核
 * @author: niuzilian
 * @create: 2019-03-18 15:15
 **/
@Data
@Document(collection = "bus_info_audit")
public class BusInfoAudit implements Serializable {
    /**
     * 主键
     */
    private String id;
    /**
     * 车辆ID
     */
    private Integer carId;
    /**
     * 城市ID
     */
    private Integer cityId;
    /**
     * 供应商ID
     */
    private Integer supplierId;
    /**
     * 车牌号
     */
    private String licensePlates;
    /**
     * 车型类别ID
     */
    private Integer groupId;
    /**
     * 车辆厂牌
     */
    private String vehicleBrand;
    /**
     * 具体型号
     */
    private String modelDetail;
    /**
     * 车辆颜色
     */
    private String color;
    /**
     * 燃料类型
     */
    private String fueltype;
    /**
     * 运输证字号
     */
    private String transportnumber;

    /**
     * 下次检车时间
     */
    private Date nextInspectDate;
    /**
     *下次维保时间
     */
    private Date nextMaintenanceDate;
    /**
     *下次运营证检验时间
     */
    private Date nextOperationDate;
    /**
     * 购买日期
     */
    private Date carPurchaseDate;

    /**
     * 0审核，1已审核
     */
    private Integer status;

    /**
     * 0 创建 1修改
     */
    private Integer stemFrom;

    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 创建人
     */
    private Integer createBy;
    /**
     * 修改人
     */
    private Integer updateBy;
    /**
     * 审核时间
     */
    private Date auditDate;
    /**
     * 审核人
     */
    private Integer auditor;

}
