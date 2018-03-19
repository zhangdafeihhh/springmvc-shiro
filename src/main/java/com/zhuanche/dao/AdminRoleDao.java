package com.zhuanche.dao;

import com.zhuanche.entity.AdminRole;
import com.zhuanche.entity.Role;

import java.util.List;

public interface AdminRoleDao {

    int insert(AdminRole record);

    int deleteByAdminId(Integer adminId);

    List<Role> selectRoleByUid(Long id);
}