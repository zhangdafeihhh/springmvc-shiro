package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class MainOrderInterCity {
    private Integer id;

    private Integer driverId;

    private Integer mainOrderNo;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private String mainName;

    private String mainTime;

    private String opePhone;

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

    public Integer getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(Integer mainOrderNo) {
        this.mainOrderNo = mainOrderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public String getMainTime() {
        return mainTime;
    }

    public void setMainTime(String mainTime) {
        this.mainTime = mainTime;
    }

    public String getOpePhone() {
        return opePhone;
    }

    public void setOpePhone(String opePhone) {
        this.opePhone = opePhone;
    }
}