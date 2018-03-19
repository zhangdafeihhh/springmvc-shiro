package com.zhuanche.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.entity.Admin;
import com.zhuanche.entity.Permission;
import com.zhuanche.entity.Role;
import com.zhuanche.service.PermissionService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDaoTest extends BaseFunctionalTestCase {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionService permissionService;

    @Test
    public void findById(){

    }

}
