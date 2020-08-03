package com.zhuanche.dto.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.InterCityEchelon;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/27 下午1:43
 * @Version 1.0
 */
@Data
public class InterEchelonDto {

    private String cityName;
    private String supplierName;
    private String teamName;
    private Integer teamId;
    private Integer cityId;
    private Integer supplierId;

    private Integer createId;

    private String createName;

    private Date updateTime;

    private String echelonMonth;

    private List<InterCityEchelon> echelonList;
}
