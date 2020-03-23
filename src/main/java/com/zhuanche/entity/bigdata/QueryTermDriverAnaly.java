package com.zhuanche.entity.bigdata;

import lombok.Data;

import java.util.Set;

/**
 * 前台查询条件实体类
 */
@Data
public class QueryTermDriverAnaly {
    private String startDate;//起始日期
    private String endDate;//结束日期

    private String allianceId;//加盟商ID
    private Set<String> visibleAllianceIds;//可见加盟商ID

    private Integer type;// 查询类型 1-天 2-周 3-月
    private String dateDate;// 如果是 天， 用该字段

    private String table;// 表名
    private Long cityId;//城市ID
    private Set<String> visibleCityIds;//可见城市ID
}
