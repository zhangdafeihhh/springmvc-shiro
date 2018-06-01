package com.zhuanche.shiro.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.http.HttpClientUtil;

/**
 * Created by zhaoyali on 2018/5/30.
 */
@SuppressWarnings("deprecation")
public class ShiroRealm extends CasRealm {
    private static final Logger logger = LoggerFactory.getLogger(ShiroRealm.class);
    @Value("${auth.permission.url}")
    private String AUTH_PERMISSION_URL;
    @Value("${auth.finduser.url}")
    private String AUTH_FINDUSER_URL;
    @Value("${auth.appid}")
    private String APP_ID;

    /**重写：获取用户授权信息**/
    @SuppressWarnings("unchecked")
	@Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    	SSOLoginUser loginUser = (SSOLoginUser) principals.getPrimaryPrincipal();
        if (null == loginUser) {
            return null;
        }
        //一、通过RPC向CAS获取当前登录账号的授权信息
        try{
            String url = AUTH_PERMISSION_URL + "?appId=" + APP_ID + "&loginName=" + loginUser.getLoginName();
            String result = HttpClientUtil.buildGetRequest(url).setLimitResult(1).execute();
            logger.info("[RPC向CAS获取登录账号的授权信息]=" + result );
            
            Map<String, Object> map = JSON.parseObject(result, Map.class);
            List<String> roles = JSONObject.parseArray(map.get("roles").toString() , String.class);
            List<String> permission =JSONObject.parseArray(map.get("permission").toString() , String.class);

            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            authorizationInfo.setRoles(new HashSet<String>(roles));
            authorizationInfo.setStringPermissions(new HashSet<String>(permission));
            return authorizationInfo;
        } catch (HttpException e) {
            logger.info("[RPC向CAS获取登录账号的授权信息]异常" + e.getMessage(), e );
            return null;
        }
    }
    
    /**重写：获取身份认证信息**/
    @SuppressWarnings("deprecation")
	@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException{
        AuthenticationInfo info = super.doGetAuthenticationInfo(authenticationToken);
        if (info == null) {
            return null;
        }
        String userName = (String) info.getPrincipals().getPrimaryPrincipal();//当前登录的账号
        if(StringUtils.isEmpty(userName)) {
            logger.error( "CAS没有返回用户登录名称");
            throw new AccountException("用户账号异常，请与管理员联系");
        }
        //一、通过RPC向CAS获取当前登录账号的属性信息
        SSOLoginUser loginUser = null;
        try{
            String url = AUTH_FINDUSER_URL + "?loginName=" + userName;
            String result = HttpClientUtil.buildGetRequest(url).setLimitResult(1).execute();
            logger.info("[RPC向CAS获取登录账号的属性信息]=" + result );
            //示例：result = {"loginName":"wangjiaqi","mobile":"17600606250","name":"王佳琪","id":287,"type":2,"email":"wangjiaqi@01zhuanche.com","status":1}
            if(StringUtils.isEmpty(result)) {
                logger.error("[RPC向CAS获取登录账号的属性信息]获取信息返回为空! ");
                throw new AccountException("获取用户账号信息异常，请重新登录");
            }
            loginUser = JSON.parseObject(result, SSOLoginUser.class);
        }catch (HttpException e) {
            logger.error("[RPC向CAS获取登录账号的属性信息]获取失败! "+ e.getMessage(), e);
            throw new AccountException("登录失败，请稍后重试！");
        }
        //二、校验
        if(loginUser==null || loginUser.getId()<=0) {
            logger.error("登录账号的ID<=0");
            throw new AccountException("用户账号异常，请与管理员联系");
        }
        if(loginUser.getStatus()==2) {
            logger.error("登录账号已被锁定");
            throw new LockedAccountException("用户账号已被锁定，请与管理员联系");
        }
        return new SimpleAuthenticationInfo(loginUser, info.getCredentials() ,  this.getName());
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
