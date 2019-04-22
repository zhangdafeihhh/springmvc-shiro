package com.zhuanche.dto.bigdata;

/**
 * @Author fanht
 * @Description
 * @Date 2019/4/22 下午9:21
 * @Version 1.0
 */
public class SaasReportParamDTO {

    /**日报表开始日期**/
    private String startDate;

    /**日报表结束日期**/
    private String endDate;

    /**月报表**/
    private String month;

    /**表名称**/
    private String table;

    /**1 日报 2 月报 3 汇总**/
    private int type;


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SaasReportParamDTO(String startDate, String endDate, String month, String table, int type) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.month = month;
        this.table = table;
        this.type = type;
    }
}
