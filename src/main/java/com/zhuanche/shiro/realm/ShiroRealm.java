package com.zhuanche.shiro.realm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zhuanche.entity.Admin;
import com.zhuanche.entity.Permission;
import com.zhuanche.entity.Role;
import com.zhuanche.service.AdminService;
import com.zhuanche.service.RoleService;
import com.zhuanche.shiro.authc.UserAuthenticationInfo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasAuthenticationException;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasToken;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuhui on 2016/2/16.
 */
public class ShiroRealm extends CasRealm {

    private static final Logger logger = LoggerFactory.getLogger(ShiroRealm.class);


    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser)principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Admin admin = adminService.findAdminByLoginName(shiroUser.getLoginName());
        if (admin!=null) {
            Set<String> roles = Sets.newHashSet();
            Set<String> permissions = Sets.newHashSet();
            List<Role> list = admin.getRoles();
            if (list!=null&&list.size()>0) {
                for (Role r : list) {
                    roles.add(r.getRoleCode());
                    Role role = roleService.selectWithPermission(r.getId());
                    List<Permission> ps = role.getPermissions();
                    if (ps!=null&&ps.size()>0) {
                        for (Permission p : ps) {
                            permissions.add(p.getPermissionCode());
                        }
                    }
                }
            }
            authorizationInfo.setRoles(roles);
            authorizationInfo.setStringPermissions(permissions);
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        AuthenticationInfo info = super.doGetAuthenticationInfo(authenticationToken);
        if (info==null){
            return info;
        }
        String  userName =  (String)info.getPrincipals().getPrimaryPrincipal();
        String realName = null;
        Admin admin = adminService.findAdminByLoginName(userName);
        //之前没有登录过，检查LDAP服务中是否已存在
        if (admin==null ){
            admin = new Admin();
            admin.setAuthType(2);
            admin.setCreateAt(new Date());
            admin.setLoginName(userName);
            admin.setStatus(1);
            admin.setLastLoginDate(new Date());
            adminService.save(admin);
            realName = admin.getName()==null?admin.getLoginName():admin.getName();
            return new UserAuthenticationInfo(new ShiroUser(admin.getId(), admin.getLoginName(), realName),
                    admin.getAuthType(),info.getCredentials(),realName);
        }
        if (admin==null){
            throw new AccountException("登录失败，请稍后重试！");
        }
        //之前登录过
        //检查状态是否被锁定
        if (admin.getStatus()==2){
            throw new LockedAccountException("用户已被锁定，请与管理员联系");
        }
        realName = admin.getName()==null?admin.getLoginName():admin.getName();
        return new UserAuthenticationInfo(new ShiroUser(admin.getId(), admin.getLoginName(), realName),
                admin.getAuthType(),info.getCredentials(),realName);

//        if (admin.getAuthType() == 1){
//            //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
//            UserAuthenticationInfo authenticationInfo = new UserAuthenticationInfo(
//                    new ShiroUser(admin.getId(), admin.getLoginName(), admin.getName()),admin.getAuthType(),
//                    admin.getPassword(), //密码
//                    ByteSource.Util.bytes(admin.getLoginName()),//salt=username
//                    getName()  //realm name
//            );
//            admin.setLastLoginDate(new Date());
//            adminService.update(admin);
//            return authenticationInfo;
////        }else {
//            admin.setLastLoginDate(new Date());
//            adminService.update(admin);
//            AuthenticationInfo info = super.doGetAuthenticationInfo(authenticationToken);
//            return new UserAuthenticationInfo(new ShiroUser(admin.getId(), admin.getLoginName(),admin.getName()==null?admin.getLoginName():admin.getName())
//                    ,admin.getAuthType(),info.getCredentials(),admin.getName()==null?admin.getLoginName():admin.getName());
////       }
    }


    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
        clearAllCache();
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
        clearAllCache();
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
        clearAllCache();
    }

    public void clearAllCachedAuthorizationInfo() {
        if (getAuthorizationCache() != null) {
            getAuthorizationCache().clear();
        }
    }

    public void clearAllCachedAuthenticationInfo() {
        if (getAuthenticationCache() != null) {
            getAuthenticationCache().clear();
        }

    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}
