package com.zhuanche.service.impl;

import com.zhuanche.dao.PermissionDao;
import com.zhuanche.entity.Permission;
import com.zhuanche.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService{

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public List<Permission> findAll() {
        return permissionDao.findAll();
    }

    @Override
    public Permission findById(Long id) {
        return permissionDao.findById(id);
    }

    @Override
    public List<Permission> findSubmenu(Long id) {
        return permissionDao.findSubmenu(id);
    }
}
