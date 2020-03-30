package com.zhuanche.entity.bigdata;

import lombok.Data;

import java.util.Set;

/**
 * 前台查询条件实体类
 * @author admin
 */
@Data
public class QueryTermDriverAnaly {
    /**
     * 起始日期
     */
    private String startDate;
    /**
     * 结束日期
     */
    private String endDate;
    /**
     * 加盟商ID
     */
    private String allianceId;
    /**
     * 可见加盟商ID
     */
    private Set<String> visibleAllianceIds;
    /**
     * 查询类型 1-天 2-周 3-月
     */
    private Integer type;
    /**
     * 如果是 天， 用该字段
     */
    private String dateDate;
    /**
     * 表名
     */
    private String table;
    /**
     * 城市ID
     */
    private Long cityId;
    /**
     * 可见城市ID
     */
    private Set<String> visibleCityIds;
}
