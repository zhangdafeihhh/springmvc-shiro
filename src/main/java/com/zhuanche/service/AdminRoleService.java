package com.zhuanche.service;

import com.zhuanche.entity.AdminRole;
import com.zhuanche.entity.Role;

import java.util.List;

/**
 * Created by yudanping on 2017/8/15.
 */
public interface AdminRoleService {

    /**
     * 根据用户id删除对应的角色关系
     * @param id
     * @return
     */
    Integer deleteAdminRoleByAdminId(Integer id);


    Integer saveAdminRole(AdminRole adminRole);

    List<Role> selectRoleByUid(Long id);
}
