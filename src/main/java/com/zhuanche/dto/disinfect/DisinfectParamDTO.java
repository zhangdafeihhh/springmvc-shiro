package com.zhuanche.dto.disinfect;

import com.zhuanche.entity.common.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 消毒入参dto
 *
 * @author admin
 */
@Data
public class DisinfectParamDTO extends BaseEntity {

    private Integer driverId;

    private String phone;

    private String name;

    private Integer cityId;

    private Integer supplierId;

    private String licensePlates;
    /**
     * 消毒状态 1:已消毒 2:消毒无效 3:未消毒
     */
    private Integer disinfectStatus;
    /**
     * 消毒时间
     */
    private String disinfectTimeStart;

    private String disinfectTimeEnd;

}
