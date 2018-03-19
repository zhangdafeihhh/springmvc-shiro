package com.zhuanche.dao;

import com.zhuanche.entity.Role;

import java.util.List;
import java.util.Map;

public interface RoleDao {

    Role findByIdAndPermission(Long id);

    Role findById(Long id);

    List<Role> findByIds(List<Long> ids);

    List<Role> search(Map<String,Object> params);

    Long insert(Role admin);

    void updateByPrimaryKeySelective(Role admin);

    void updateByPrimaryKey(Role admin);

    Role findByRoleCode(String roleCode);

    List<Role> findAllRole();

    Role selectWithPermission(long id);
}
