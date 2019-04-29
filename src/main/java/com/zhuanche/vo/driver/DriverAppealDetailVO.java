package com.zhuanche.vo.driver;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
@Data
public class DriverAppealDetailVO {
    private Integer appealId;
    private String driverName;
    private String driverPhone;
    private String licensePlates;
    private String orderNo;
    private String evaluateScore;
    private String evaluate;
    private String memo;
    private String appealContent;
    private String url;
    private Integer appealStatus;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    private String auditRemark;
}