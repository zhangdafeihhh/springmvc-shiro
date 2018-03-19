package com.zhuanche.service;

import com.zhuanche.entity.Role;
import com.zhuanche.util.Page;

import java.util.List;
import java.util.Map;

public interface RoleService {

    Role findById(Long id);

    Role findByIdAndPermission(Long id);

    List<Role> findByIds(List<Long> ids);

    Page<Role> search(Map<String,Object> params, Integer pageNo, Integer pageSize);

    Long insert(Role role);

    void update(Role role);

    Role findByRoleCode(String roleCode);

    List<Role> findAllRole();

    Role selectWithPermission(long id);
}
