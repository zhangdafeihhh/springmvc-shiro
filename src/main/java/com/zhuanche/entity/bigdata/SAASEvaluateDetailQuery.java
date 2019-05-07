package com.zhuanche.entity.bigdata;

import java.io.Serializable;
import java.util.List;

public class SAASEvaluateDetailQuery implements Serializable {
    private String queryDate;
    private String driverTypeId;
    private String driverScore;
    private String appScore;
    private String allianceId;
    private String motorcadeId;
    private String classId;
    private String orderCityId;
    private String serviceTypeId;
    private List<String> visibleAllianceIds;
    private List<String> visibleMotocadeIds;
    private List<String> visibleCityIds;
    private String tableName;
    protected String pageNo = "1";
    protected String pageSize = "10";

    public SAASEvaluateDetailQuery() {
    }

    public String getQueryDate() {
        return this.queryDate;
    }

    public String getDriverTypeId() {
        return this.driverTypeId;
    }

    public String getDriverScore() {
        return this.driverScore;
    }

    public String getAppScore() {
        return this.appScore;
    }

    public String getAllianceId() {
        return this.allianceId;
    }

    public String getMotorcadeId() {
        return this.motorcadeId;
    }

    public String getClassId() {
        return this.classId;
    }

    public String getOrderCityId() {
        return this.orderCityId;
    }

    public String getServiceTypeId() {
        return this.serviceTypeId;
    }

    public List<String> getVisibleAllianceIds() {
        return this.visibleAllianceIds;
    }

    public List<String> getVisibleMotocadeIds() {
        return this.visibleMotocadeIds;
    }

    public List<String> getVisibleCityIds() {
        return this.visibleCityIds;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getPageNo() {
        return this.pageNo;
    }

    public String getPageSize() {
        return this.pageSize;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public void setDriverTypeId(String driverTypeId) {
        this.driverTypeId = driverTypeId;
    }

    public void setDriverScore(String driverScore) {
        this.driverScore = driverScore;
    }

    public void setAppScore(String appScore) {
        this.appScore = appScore;
    }

    public void setAllianceId(String allianceId) {
        this.allianceId = allianceId;
    }

    public void setMotorcadeId(String motorcadeId) {
        this.motorcadeId = motorcadeId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setOrderCityId(String orderCityId) {
        this.orderCityId = orderCityId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public void setVisibleAllianceIds(List<String> visibleAllianceIds) {
        this.visibleAllianceIds = visibleAllianceIds;
    }

    public void setVisibleMotocadeIds(List<String> visibleMotocadeIds) {
        this.visibleMotocadeIds = visibleMotocadeIds;
    }

    public void setVisibleCityIds(List<String> visibleCityIds) {
        this.visibleCityIds = visibleCityIds;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
