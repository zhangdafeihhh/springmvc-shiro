package com.zhuanche.dto.mdbcarmanage;

import lombok.Data;

import java.util.Date;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/16 下午5:25
 * @Version 1.0
 */
@Data
public class InterCityEchelonDto {

    private Integer id;

    private Integer teamId;

    private Integer sort;

    private String echelonDate;

    private Date createTime;

    private Date updateTime;

    private String echelonName;

    private String echelonMonth;

    private Integer cityId;

    private Integer supplierId;
}
