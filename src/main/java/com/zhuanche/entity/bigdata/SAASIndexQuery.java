package com.zhuanche.entity.bigdata;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/3/29 下午5:49
 * @Version 1.0
 */
@Data
//@PeriodValidator(startDate = "startDate",endDate = "endDate",maxPeriod = 31,message = "时间区间不符合一月内查询条件！")
//@DependentValidator(child = "motorcadeId" ,parent = "allianceId",message = "查询车队时加盟商必须指定！")
//@AllianceContainValidator(candidate = "allianceId",aggregation = "visibleAllianceIds",message = "无加盟商查询权限！")
//@MotorcadeContainValidator(candidate = "motorcadeId",aggregation = "visibleMotocadeIds",message = "无车队查询权限！")
public class SAASIndexQuery {

    @NotBlank(message = "起始日期不能为空！")
    private String startDate;//起始日期
    @NotBlank(message = "结束日期不能为空！")
    private String endDate;//结束日期

    private String allianceId;//加盟商ID
    private String motorcadeId;//车队ID
    private List<String> visibleAllianceIds;//可见加盟商ID
    private List<String> visibleMotocadeIds;//可见车队ID

    private int dateDiff;//日期相差几天

}
