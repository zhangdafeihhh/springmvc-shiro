package com.zhuanche.vo.busManage;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhuanche.constants.busManage.EnumFuel;
import com.zhuanche.util.DateUtil;
import com.zhuanche.util.DateUtils;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: mp-manage
 * @description: 巴士车辆信息需要展示到页面的封装类
 * @author: niuzilian
 * @create: 2018-11-22 17:41
 *
 **/

@Data
public class BusInfoVO implements Serializable {
    private String id;
    private Integer carId;
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
