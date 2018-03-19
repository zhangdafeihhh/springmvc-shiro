package com.zhuanche.service;

import com.zhuanche.entity.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> findAll();

    Permission findById(Long id);

    List<Permission> findSubmenu(Long id);
}
