package com.zhuanche.dto.disinfect;

import lombok.Data;

import java.util.Date;

/**
 * 消毒列表DTO数据
 *
 * @author admin
 */
@Data
public class DisinfectResultDTO implements java.io.Serializable{

    private Integer driverId;

    private String phone;

    private String name;

    private Integer cityId;

    private String cityName;

    private Integer supplierId;

    private String supplierName;

    private String licensePlates;
    /**
     * 消毒状态 1:已消毒 2:消毒无效 3:未消毒
     */
    private Integer disinfectStatus;
    /**
     * 消毒时间
     */
    private Date disinfectTime;
    /**
     * 消毒照片
     */
    private String disinfectImgUrl;

}
