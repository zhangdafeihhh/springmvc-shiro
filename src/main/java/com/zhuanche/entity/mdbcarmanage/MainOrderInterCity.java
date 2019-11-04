package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class MainOrderInterCity {
    private Integer id;

    private Integer driverId;

    private String mainOrderNo;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private String mainName;

    private String mainTime;

    private String opePhone;


    public enum orderState{

         NOTSETOUT(1,"未出发"),
         SETOUT(2,"已出发");


        private int code;

        private String msg;

        orderState(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

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

    public String getMainOrderNo() {
        return mainOrderNo;
    }

    public void setMainOrderNo(String mainOrderNo) {
        this.mainOrderNo = mainOrderNo == null ? null : mainOrderNo.trim();
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
        this.mainName = mainName == null ? null : mainName.trim();
    }

    public String getMainTime() {
        return mainTime;
    }

    public void setMainTime(String mainTime) {
        this.mainTime = mainTime == null ? null : mainTime.trim();
    }

    public String getOpePhone() {
        return opePhone;
    }

    public void setOpePhone(String opePhone) {
        this.opePhone = opePhone == null ? null : opePhone.trim();
    }
}