package com.zhuanche.vo.busManage;

import lombok.Data;

import java.util.Date;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2019-01-30 10:23
 **/
@Data
public class BusDriverBaseImportTemplateVO {
    private String name;
    /** 值：男  女*/
    private String gender;
    private String groupName;
    private String idCardNo;
    private String phone;
    private Date birthDay;
    /**驾照类型*/
    private String drivingLicenseType;
    private String driverLicenseNumber;
    /** 驾照领证日期(必填 驾龄≥3)*/
    private Date issueDate;
     /**从业资格证编号*/
     private String xyDriverNumber;
}
