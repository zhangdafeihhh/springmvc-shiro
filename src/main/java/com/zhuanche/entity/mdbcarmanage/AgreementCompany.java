package com.zhuanche.entity.mdbcarmanage;

import java.util.Date;

public class AgreementCompany{

	/**
     * 主键
     */
    private Integer id;

    /**
     * 协议公司
     */
    private String name;

    /**
     * 状态：1启用，2禁止
     */
    private int status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改人
     */
    private String updateName;

    /**
     * 修改时间
     */
    private Date updateDate;
    
    private String createDateStr;

    private String updateDateStr;
    
    public String getCreateDateStr() {
		return createDateStr;
	}

	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}

	public String getUpdateDateStr() {
		return updateDateStr;
	}

	public void setUpdateDateStr(String updateDateStr) {
		this.updateDateStr = updateDateStr;
	}

	/**
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     * @return
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @return
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     * @return
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     * @return
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * @return
     */
    public String getCreateName() {
        return createName;
    }

    /**
     * @param createName
     * @return
     */
    public void setCreateName(String createName) {
        this.createName = createName == null ? null : createName.trim();
    }

    /**
     * @return
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     * @return
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return
     */
    public String getUpdateName() {
        return updateName;
    }

    /**
     * @param updateName
     * @return
     */
    public void setUpdateName(String updateName) {
        this.updateName = updateName == null ? null : updateName.trim();
    }

    /**
     * @return
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate
     * @return
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
  