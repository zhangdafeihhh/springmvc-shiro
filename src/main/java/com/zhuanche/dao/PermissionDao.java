package com.zhuanche.dao;

import com.zhuanche.entity.Permission;

import java.util.List;

public interface PermissionDao {

    List<Permission> findAll();

    Permission findById(Long id);


    List<Permission> findSubmenu(Long id);
}
