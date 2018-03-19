package com.zhuanche.service;

import com.zhuanche.entity.Admin;
import com.zhuanche.util.Page;

import java.util.List;
import java.util.Map;

public interface AdminService {

    Admin findByLoginName(String loginName);

    Admin findAdminByLoginName(String loginName);

    Admin findById(Long id);

    Admin findAdminById(Long id);

    Long save(Admin admin);

    void update(Admin admin);

    Page<Admin> search(Map<String,Object> params,Integer pageNo, Integer pageSize);

    List<Admin> findAll();

    List<Admin> findByName(String name);

    void sendModifyPwdEmail(String email,String name,String loginName,String password);

    void sendResetPwdEmail(String email, String name, String loginName, String password);


    /**
     * 查询员工，关联员工角色
     * @param id
     * @return
     */
    Admin selectRoleByAdminPrimaryKey(Long id);
}
