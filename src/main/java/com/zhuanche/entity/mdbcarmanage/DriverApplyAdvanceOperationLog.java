package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class DriverApplyAdvanceOperationLog {
    private Integer id;

    private Integer driverApplyAdvanceId;

    private String orderNum;

    private Integer operationId;

    private String operationName;

    private String operationAction;

    private String operationRemark;

    private String enclosureUrl;

    private Date operationDate;

    private Date createDate;

    private Date updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverApplyAdvanceId() {
        return driverApplyAdvanceId;
    }

    public void setDriverApplyAdvanceId(Integer driverApplyAdvanceId) {
        this.driverApplyAdvanceId = driverApplyAdvanceId;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum == null ? null : orderNum.trim();
    }

    public Integer getOperationId() {
        return operationId;
    }

    public void setOperationId(Integer operationId) {
        this.operationId = operationId;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName == null ? null : operationName.trim();
    }

    public String getOperationAction() {
        return operationAction;
    }

    public void setOperationAction(String operationAction) {
        this.operationAction = operationAction == null ? null : operationAction.trim();
    }

    public String getOperationRemark() {
        return operationRemark;
    }

    public void setOperationRemark(String operationRemark) {
        this.operationRemark = operationRemark == null ? null : operationRemark.trim();
    }

    public String getEnclosureUrl() {
        return enclosureUrl;
    }

    public void setEnclosureUrl(String enclosureUrl) {
        this.enclosureUrl = enclosureUrl == null ? null : enclosureUrl.trim();
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}