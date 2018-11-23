package com.zhuanche.vo.rentcar;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: mp-manage
 * @description: 巴士车辆信息需要展示到页面的封装类
 * @author: niuzilian
 * @create: 2018-11-22 17:41
 **/
@Data
public class BusInfoVO implements Serializable{
    private Integer carId;
    private String licensePlates;
    private String cityName;
    private String supplierName;
    private String groupName;
    private String modelDetail;
    private Integer status;
    private String createDate;
}
