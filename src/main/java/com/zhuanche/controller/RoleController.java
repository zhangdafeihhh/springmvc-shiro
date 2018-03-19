package com.zhuanche.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.entity.Permission;
import com.zhuanche.entity.Role;
import com.zhuanche.entity.RolePermission;
import com.zhuanche.service.AdminService;
import com.zhuanche.service.PermissionService;
import com.zhuanche.service.RolePermissionService;
import com.zhuanche.service.RoleService;
import com.zhuanche.util.Page;
import com.zhuanche.util.Servlets;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by yudanping on 2016/8/31.
 **/
@Controller
@RequestMapping(value="/role")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);


    @Autowired
    RoleService roleService;
    
    @Autowired
    AdminService adminService;

    @Autowired
    PermissionService permissionService;


    @Autowired
    private RolePermissionService rolePermissionService;

    @RequiresPermissions("role")
    @RequestMapping(value="/index")
    public String index(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
                        @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
                        Model model, HttpServletRequest request){
        Map<String, Object> searchParams = Servlets.getParametersStartingWith(request, "search_");
        Page<Role> page = roleService.search(searchParams, pageNumber, pageSize);
        model.addAttribute("page", page);
        model.addAttribute("pageNo",pageNumber);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("roleName",searchParams.get("roleName"));
        return "role/index";
    }

    /**
     * 跳转到新增或修改页面
     * @param id
     * @param modelAndView
     * @return
     */
    @RequestMapping(value="/single")
    public ModelAndView single(@RequestParam(value="id",required = false) Long id,
                               ModelAndView modelAndView){
        if(id!=null){
            Role role = roleService.findById(id);
            modelAndView.addObject("role",role);
        }
        modelAndView.setViewName("role/single");
        return modelAndView;

    }

    /**
     * 新增或修改角色
     * @param request
     * @param attr
     * @return
     */
    @RequestMapping(value="/edit")
    public String create(Role role, HttpServletRequest request, RedirectAttributes attr){
        try {
            if (role.getId()==null) {
                Long roleResult = roleService.insert(role);
                if (roleResult > 0) {
                    attr.addFlashAttribute("msg","添加成功");
                } else {
                    attr.addFlashAttribute("msg","添加失败");
                    attr.addFlashAttribute("result", false);
                }
            } else {
                role.setRoleName(role.getRoleName());
                try{
                    roleService.update(role);
                    attr.addFlashAttribute("msg","修改成功");
                }catch (Exception e){
                    attr.addFlashAttribute("msg","修改失败");
                    attr.addFlashAttribute("result", false);
                }
            }
        }catch (Exception e){
            logger.error("/edit"+e.getMessage(),e);
            attr.addFlashAttribute("msg","修改失败");
            attr.addFlashAttribute("result", false);
        }
        return "redirect:index.html";
    }


    /**
     * 跳转到角色赋权限页面
     * @param roleId
     * @param model
     * @return
     */
    @RequestMapping(value="/permission")
    public String permission(@RequestParam(value="roleId") Long roleId,
                                   Model model) {
        //获取所有权限
        JSONArray array = new JSONArray();
        Role role = roleService.findByIdAndPermission(roleId);
        Role currRole = roleService.findById(roleId);
        List<Long> pids = new ArrayList<Long>();
        if(role!=null){
            if(role.getPermissions().size()>0){
                for(int i=0;i<role.getPermissions().size();i++){
                    Long pid = role.getPermissions().get(i).getId();
                    pids.add(pid);
                }
            }
        }

        //封装成tree需要的数据
        List<Permission> ziRoles = permissionService.findSubmenu(0L);
        if(ziRoles!=null&& !ziRoles.isEmpty()){
            for(Permission permission:ziRoles){
                JSONObject jsonDate = submenu(buildPermission(permission,pids), permission.getId(),pids);
                array.add(jsonDate);
            }
        }
        model.addAttribute("permissions", JSON.toJSONString(array));
        model.addAttribute("role", role);
        model.addAttribute("currRole", currRole);
        model.addAttribute("roleId", roleId);
        return "/role/permission";
    }

    /**
     * 修改角色权限
     * @param roleId
     * @param permissionIds
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/modify")
    public String modify(@RequestParam(value="roleId") Long roleId,
                         @RequestParam(value="permissionIds") String permissionIds){
        try {
            logger.info("修改角色权限，参数roleId={},permissionIds={}",roleId,permissionIds);
            if(roleId!=null){
                Role role = roleService.findByIdAndPermission(roleId);
                if (role!=null) {
                    rolePermissionService.deleteByRoleId(roleId);
                }
                List<String> newPermissionIds = Arrays.asList(permissionIds.split(","));
                for(String permissionId:newPermissionIds){
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setPid(Long.valueOf(permissionId));
                    rolePermission.setRid(roleId);
                    rolePermissionService.insert(rolePermission);
                }
                return "true";
            }
        }catch (Exception e){
            logger.error("修改权限报错："+e.getMessage(),e);
            return "false";
        }

        return "false";

    }

    public JSONObject submenu(JSONObject parent,Long id,List<Long> pids){
        if (id==null){
            id = 0L;
        }
        List<Permission> permissions = permissionService.findSubmenu(id);
        if(permissions!=null&& !permissions.isEmpty()){
            JSONArray nodes = new JSONArray();
            for(Permission p : permissions){
                JSONObject permission = buildPermission(p,pids);
                permission = submenu(permission,p.getId(),pids);
                nodes.add(permission);
            }
            parent.put("nodes",nodes);
        }
        return parent;
    }

    private JSONObject buildPermission(Permission permission,List<Long> pids){
        JSONObject obj = new JSONObject();
        obj.put("id",permission.getId());
        obj.put("text",permission.getPermissionName());
        JSONObject state = new JSONObject();
        if(pids.contains(permission.getId())){
            state.put("checked",pids.contains(permission.getId()));
            state.put("expanded",true);
        }
        obj.put("state",state);
        return obj;
    }


}
