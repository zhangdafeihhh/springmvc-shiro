package com.zhuanche.entity.mdbcarmanage;

import com.zhuanche.common.syslog.Column;

public class SaasRole {
    @Column(desc="角色ID")
    private Integer roleId;
    @Column(desc="角色代码")
    private String roleCode;
    @Column(desc="角色名称")
    private String roleName;

    private Boolean valid;

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
}