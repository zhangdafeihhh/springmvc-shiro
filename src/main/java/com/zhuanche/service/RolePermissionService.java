package com.zhuanche.service;

import com.zhuanche.entity.RolePermission;

import java.util.List;

/**
 * Created by yudanping on 2017/8/14.
 */
public interface RolePermissionService {

    Integer insertRolePermission(List<RolePermission> permissionIds);

    Integer deleteByRoleId(Long roleId);

    Integer insert(RolePermission rolePermission);
}
