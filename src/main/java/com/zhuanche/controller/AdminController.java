package com.zhuanche.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.entity.Admin;
import com.zhuanche.entity.AdminRole;
import com.zhuanche.entity.Role;
import com.zhuanche.service.AdminRoleService;
import com.zhuanche.service.AdminService;
import com.zhuanche.service.RoleService;
import com.zhuanche.util.Page;
import com.zhuanche.util.Servlets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    AdminRoleService adminRoleService;


    @RequestMapping("/index")
    public String index(@RequestParam(value = "pageNo", defaultValue = "1")Integer pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize,
                        HttpServletRequest request,
                        Model model){
        try {
            Map<String,Object> params = Servlets.getSearchParams(request);
            Page<Admin> page = adminService.search(params,pageNo,pageSize);
            model.addAttribute("page",page);
            model.addAttribute("pageNo",pageNo);
            model.addAttribute("pageSize",pageSize);
            model.addAttribute("param", params);
            model.addAttribute("name",params.get("name"));
            model.addAttribute("loginName",params.get("loginName"));
            model.addAttribute("mobile",params.get("mobile"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/admin/index";
    }

    @ResponseBody
    @RequestMapping(value = "/operate",method = RequestMethod.POST)
    public String operate(Long id,Integer status){
        if (id==null||status==null){
            return "请求参数异常！";
        }
        Admin admin = adminService.findAdminById(id);
        if (admin==null){
            return "用户不存在！";
        }
        String title = status==1?admin.getLoginName()+",用户已被锁定":admin.getLoginName()+",解锁成功";
        if (status==1){
            admin.setStatus(2);
        }else{
            admin.setStatus(1);
        }
        adminService.update(admin);

        return title;
    }

    @RequestMapping("/edit")
    public String edit(Long id,Model model){

        if (id!=null){
            Admin admin = adminService.findAdminById(id);
            model.addAttribute("entity",admin);
            List<Role> adminRole = adminRoleService.selectRoleByUid(id);
            model.addAttribute("adminRole", adminRole);

        }
        //查询所有角色
        List<Role> allRoles = roleService.findAllRole();
        if(allRoles.size() !=0){
            JSONArray allRolesArr = new JSONArray();
            for (Role role:allRoles) {
                JSONObject obj = new JSONObject();
                obj.put("id",role.getId());
                obj.put("text",role.getRoleName());
                allRolesArr.add(obj);
            }
            logger.info("allRolesArr={}",allRolesArr);
            model.addAttribute("allRoles", allRolesArr);
        }
        return "/admin/single";
    }

    @ResponseBody
    @RequestMapping("/checkLoginName")
    public String checkLoginName(Long uid,String loginName,Model model){

        if(uid!=null){
            Admin admin = adminService.findAdminById(uid);
            if(admin != null && loginName.equals(admin.getLoginName())){
                return "true";
            }
        }
        if (StringUtils.isBlank(loginName)){
            return "true";
        }
        Admin admin = adminService.findAdminByLoginName(loginName);
        if (admin==null){
            return "true";
        }
        return "false";
    }

    @RequestMapping("/save")
    public String save(Long id,String name,String email,String mobile,String role,RedirectAttributes attr){
        Admin admin = new Admin();
        try {
            admin.setId(id);
            admin.setEmail(email);
            admin.setName(name);
            admin.setMobile(mobile);
            adminService.update(admin);
            String[] roles = role.split(",");
            adminRoleService.deleteAdminRoleByAdminId(Integer.parseInt(String.valueOf(id)));
            for(int i=0;i<roles.length;i++){
                AdminRole adminRole = new AdminRole();
                adminRole.setUid(Integer.parseInt(String.valueOf(id)));
                adminRole.setRid(Integer.parseInt(roles[i]));
                adminRoleService.saveAdminRole(adminRole);
            }

            attr.addFlashAttribute("result",true);
            attr.addFlashAttribute("msg","修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            attr.addFlashAttribute("result",false);
            attr.addFlashAttribute("msg","添加失败，请稍后重试");
        }

        return "redirect:index.html";
    }
}
