package com.zhuanche.service.impl;

import com.zhuanche.dao.AdminRoleDao;
import com.zhuanche.entity.AdminRole;
import com.zhuanche.entity.Role;
import com.zhuanche.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by yudanping on 2017/8/15.
 */
@Service("adminRoleService")
public class AdminRoleServiceImpl implements AdminRoleService{

    @Autowired
    private AdminRoleDao adminRoleDao;

    /**
     * 根据用户id删除对应的角色关系
     * @param id
     * @return
     */
    @Override
    public Integer deleteAdminRoleByAdminId(Integer id) {
        return adminRoleDao.deleteByAdminId(id);
    }

    @Override
    public Integer saveAdminRole(AdminRole adminRole) {
        return adminRoleDao.insert(adminRole);
    }
    @Override
    public List<Role> selectRoleByUid(Long id){
        return adminRoleDao.selectRoleByUid(id);
    }
}
