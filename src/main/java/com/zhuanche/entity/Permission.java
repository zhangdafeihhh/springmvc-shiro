package com.zhuanche.entity;

import java.io.Serializable;

public class Permission implements Serializable {

    private static final long serialVersionUID = 2544403412093470883L;

    private Long id; //权限表

    private String permissionName;//权限名称

    private String permissionCode;//权限码标识

    private Permission parent; //上级

    private Integer parentId; //上级id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Permission getParent() {
        return parent;
    }

    public void setParent(Permission parent) {
        this.parent = parent;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
