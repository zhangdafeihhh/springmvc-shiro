package com.zhuanche.dao;

import com.zhuanche.entity.Admin;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AdminDao {

    Admin findById(@Param("id") Long id);

    Admin findAdminById(@Param("id") Long id);

    Admin findByLoginName(@Param("loginName")String loginName);

    Admin findAdminByLoginName(@Param("loginName")String loginName);

    Long save(Admin admin);

    void update(Admin admin);

    List<Admin> search(Map<String,Object> params);

    List<Admin> findAll();

    Integer count(Map<String,Object> params);


    List<Admin> findByName(@Param("name") String name);



    Admin selectRoleByAdminPrimaryKey(Long id);
}
