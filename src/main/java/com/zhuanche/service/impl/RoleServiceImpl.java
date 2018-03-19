package com.zhuanche.service.impl;

import com.zhuanche.dao.RoleDao;
import com.zhuanche.entity.Role;
import com.zhuanche.service.RoleService;
import com.zhuanche.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public Role findById(Long id) {
        return roleDao.findById(id);
    }

    @Override
    public Role findByIdAndPermission(Long id) {
        return roleDao.findByIdAndPermission(id);
    }

    @Override
    public List<Role> findByIds(List<Long> ids) {
        return roleDao.findByIds(ids);
    }

    @Override
    public Page<Role> search(Map<String, Object> params, Integer pageNo, Integer pageSize) {
        if (pageNo<=1){
            params.put("pageNo",0);
        }else{
            params.put("pageNo",(pageNo-1)*pageSize);
        }
        params.put("pageSize",pageSize);
        List<Role> content = roleDao.search(params);
        return new Page<Role>(pageNo,pageSize,content.size(),content);
    }

    @Override
    public Long insert(Role role) {
        return roleDao.insert(role);
    }

    @Override
    public void update(Role role) {
        roleDao.updateByPrimaryKeySelective(role);
    }


    @Override
    public Role findByRoleCode(String roleCode) {
        return roleDao.findByRoleCode(roleCode);
    }

    @Override
    public List<Role> findAllRole() {
        return roleDao.findAllRole();
    }

    @Override
    public Role selectWithPermission(long id) {
        return roleDao.selectWithPermission(id);
    }
}
