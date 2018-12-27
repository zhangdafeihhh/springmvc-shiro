package com.zhuanche.entity.busManage;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @program: car-manage
 * @description: 企业机构信息
 * @author: niuzilian
 * @create: 2018-11-07 16:10
 **/

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationInfo implements Serializable {
    /**
     * 企业id
     */
    private Integer companyId;
    /**
     * 1、需要第三方认证，0、不需要第三方认证
     */
    private Integer isThirdAuth;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 部门id
     */
    private Integer departmentId;
    /**
     * 1,是新机构,2,不是新机构
     */
    private Integer type;
    /**
     * 用户id
     */
    private Integer userId;




    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getIsThirdAuth() {
        return isThirdAuth;
    }

    public void setIsThirdAuth(Integer isThirdAuth) {
        this.isThirdAuth = isThirdAuth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
