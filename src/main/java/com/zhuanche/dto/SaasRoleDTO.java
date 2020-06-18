package com.zhuanche.dto;

public class SaasRoleDTO {
    private Integer roleId;

    private String roleCode;

    private String roleName;

    private Boolean valid;

    private Integer isVisiable;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode == null ? null : roleCode.trim();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Integer getIsVisiable() {
        return isVisiable;
    }

    public void setIsVisiable(Integer isVisiable) {
        this.isVisiable = isVisiable;
    }
}