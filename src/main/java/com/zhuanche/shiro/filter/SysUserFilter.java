//package com.zhuanche.shiro.filter;
//
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.web.filter.PathMatchingFilter;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//
//public class SysUserFilter extends PathMatchingFilter {
//
//
//
//    @Override
//    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
//
//        String username = (String) SecurityUtils.getSubject().getPrincipal();
//        //request.setAttribute(Constants.CURRENT_USER, adminService.findByUserName(username));
//        return true;
//    }
//}
