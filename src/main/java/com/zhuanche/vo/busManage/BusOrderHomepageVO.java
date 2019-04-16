package com.zhuanche.vo.busManage;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @program: mp-manage
 * @description:巴士saas首页列表
 * @author: niuzilian
 * @create: 2019-03-15 14:33
 **/
@Data
public class BusOrderHomepageVO {
    private Integer orderId;
    private String orderNo;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date bookingDate;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    private String cityName;
    private Integer serviceTypeId;
    private String serviceName;
    private Integer driverId;
    private String driverName;
    private String driverPhone;
    private String licensePlates;
    private Integer bookingGroupid;
    private String bookingGroupName;
}
