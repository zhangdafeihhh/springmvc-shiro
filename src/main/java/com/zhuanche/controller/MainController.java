package com.zhuanche.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @Value("${sso.server.url}")
    private String ssoServerUrl;

    @RequestMapping("/index")
    public String index(Model model){
//        ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
//        if (shiroUser!=null){
//            Admin admin = adminService.findAdminById(shiroUser.getUserId());
//            if (admin!=null&&admin.getIsInitialized()==1){
//                redirectAttributes.addFlashAttribute("msg","您的密码不安全，请修改！");
//                redirectAttributes.addFlashAttribute("result","alert");
//                return "redirect:/admin/pwd.html";
//            }
//        }
        model.addAttribute("ssoServerUrl",ssoServerUrl);
        return "index";
    }

    @RequestMapping("/main")
    public String main(){
        return "main";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/nginx")
    public String nginx(){
        return "common/nginx";
    }

    @RequestMapping("/demo")
    public String demo(){
        return "common/demo";
    }
}
