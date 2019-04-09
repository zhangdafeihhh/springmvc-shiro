package com.zhuanche.entity.rentcar;

import java.util.Date;

/**
 * SAAS四期供应商分佣信息POJO对象
 */
public class GroupInfo {

    //服务车型名称
    private String groupName;
    //协议开始时间
    private Date activeStartDate;
    //协议结束时间
    private Date activeEndDate;
    //车型id
    private int groupId;
    //加盟商id
    private int supplierId;
    //分佣协议id
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
