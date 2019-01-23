package com.zhuanche.entity.rentcar;

import java.util.Date;

public class GroupInfo {

    private String groupName;
    private Date activeStartDate;
    private Date activeEndDate;
    private int groupId;
    private int supplierId;
    private String prorateId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getActiveStartDate() {
        return activeStartDate;
    }

    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    public Date getActiveEndDate() {
        return activeEndDate;
    }

    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getProrateId() {
        return prorateId;
    }

    public void setProrateId(String prorateId) {
        this.prorateId = prorateId;
    }
}
