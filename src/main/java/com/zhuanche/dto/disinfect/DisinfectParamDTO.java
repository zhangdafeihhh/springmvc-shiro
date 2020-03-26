package com.zhuanche.dto.disinfect;

import com.zhuanche.entity.common.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;

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
    @NotNull(message = "城市必选")
    private Integer cityId;
    @NotNull(message = "供应商必选")
    private Integer supplierId;

    private String licensePlates;
    /**
     * 消毒状态 1:已消毒 2:消毒无效 3:未消毒
     */
    private Integer disinfectStatus;
    /**
     * 消毒时间
     */
    @NotNull(message = "消毒时间必选")
    private String disinfectTimeStart;
    @NotNull(message = "消毒时间必选")
    private String disinfectTimeEnd;

}
