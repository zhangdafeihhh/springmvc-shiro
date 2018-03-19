package com.zhuanche.service.impl;

import com.zhuanche.dao.RolePermissionDao;
import com.zhuanche.entity.RolePermission;
import com.zhuanche.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yudanping on 2017/8/14.
 */
@Service("rolePermissionService")
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Override
    public Integer insertRolePermission(List<RolePermission> permissionIds) {
        return rolePermissionDao.insertByBatch(permissionIds);
    }

    @Override
    public Integer deleteByRoleId(Long roleId) {
        return rolePermissionDao.deleteByRoleId(roleId);
    }

    @Override
    public Integer insert(RolePermission rolePermission) {
        return rolePermissionDao.insert(rolePermission);
    }
}
