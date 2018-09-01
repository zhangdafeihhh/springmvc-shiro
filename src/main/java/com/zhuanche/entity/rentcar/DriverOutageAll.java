package com.zhuanche.entity.rentcar;

import java.util.Date;

public class DriverOutageAll {
    private Integer id;

    private Integer driverId;

    private Date outStartDate;

    private Date outEndDate;

    private Integer outageSource;

    private String outageReason;

    private Integer createBy;

    private String createName;

    private Date createDate;

    private Integer removeBy;

    private String removeName;

    private Date removeDate;

    private String removeReason;

    private Integer removeStatus;

    private Date updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public Date getOutStartDate() {
        return outStartDate;
    }

    public void setOutStartDate(Date outStartDate) {
        this.outStartDate = outStartDate;
    }

    public Date getOutEndDate() {
        return outEndDate;
    }

    public void setOutEndDate(Date outEndDate) {
        this.outEndDate = outEndDate;
    }

    public Integer getOutageSource() {
        return outageSource;
    }

    public void setOutageSource(Integer outageSource) {
        this.outageSource = outageSource;
    }

    public String getOutageReason() {
        return outageReason;
    }

    public void setOutageReason(String outageReason) {
        this.outageReason = outageReason == null ? null : outageReason.trim();
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getRemoveBy() {
        return removeBy;
    }

    public void setRemoveBy(Integer removeBy) {
        this.removeBy = removeBy;
    }

    public String getRemoveName() {
        return removeName;
    }

    public void setRemoveName(String removeName) {
        this.removeName = removeName == null ? null : removeName.trim();
    }

    public Date getRemoveDate() {
        return removeDate;
    }

    public void setRemoveDate(Date removeDate) {
        this.removeDate = removeDate;
    }

    public String getRemoveReason() {
        return removeReason;
    }

    public void setRemoveReason(String removeReason) {
        this.removeReason = removeReason == null ? null : removeReason.trim();
    }

    public Integer getRemoveStatus() {
        return removeStatus;
    }

    public void setRemoveStatus(Integer removeStatus) {
        this.removeStatus = removeStatus;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}