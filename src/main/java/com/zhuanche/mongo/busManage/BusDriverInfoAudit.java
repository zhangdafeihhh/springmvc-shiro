package com.zhuanche.mongo.busManage;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 郭宏光 on 2019/3/22.
 */
@Data
@Document(collection = "bus_driver_info_audit")
public class BusDriverInfoAudit implements Serializable {
    /**物理主键*/
    private String id;
    /** 司机Id **/
    private Integer driverId;
    /** 手机号 **/
    private String phone;
    /** 服务城市 */
    private Integer serviceCity;
    /** 供应商 **/
    private Integer supplierId;
    /** 司机姓名 **/
    private String name;
    /** 服务类型car_biz_car_group **/
    private Integer groupId;
    /** 司机性别，[1.男0.女] **/
    private Integer gender;
    /** 身份证号 **/
    private String idCardNo;
    private String birthDay;
    /** 驾照类型 **/
    private String drivingLicenseType;
    /** 机动车驾驶证号 **/
    private String driverlicensenumber;
    /** 驾照领证日期 **/
    private Date issueDate;
    /** 道路运输从业资格证编号(巡游出租汽车驾驶员资格证号,巴士业务无“巡游...”业务，复用此字段) **/
    private String xyDriverNumber;
    /** 司机状态 (0:无效, 1:有效) **/
    private Integer status;

    /**
     * 0未审核，1已审核
     */
    private Integer auditStatus;

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
