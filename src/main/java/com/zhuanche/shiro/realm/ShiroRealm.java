package com.zhuanche.shiro.realm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.shiro.authc.UserAuthenticationInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhui on 2016/2/16.
 */
public class ShiroRealm extends CasRealm {

    private static final Logger logger = LoggerFactory.getLogger(ShiroRealm.class);

    private static String USER_ID = "userId";
    private static String LOGIN_NAME = "loginName";
    private static String REAL_NAME = "realName";

    @Value("${auth.permission.url}")
    private String AUTH_PERMISSION_URL;

    @Value("${auth.finduser.url}")
    private String AUTH_FINDUSER_URL;

    @Value("${auth.appid}")
    private String APP_ID;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        JSONObject jsonObject = (JSONObject) principals.getPrimaryPrincipal();
        if (null == jsonObject) {
            return null;
        }
        String loginName = jsonObject.getString(LOGIN_NAME);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        try {
            String url = AUTH_PERMISSION_URL + "?appId=" + APP_ID + "&loginName=" + loginName;
            String result = HttpClientUtil.buildGetRequest(url).setLimitResult(1).execute();
            Map<String, Object> map = JSON.parseObject(result, Map.class);
            List<String> roles = JSONObject.parseArray(map.get("roles").toString() , String.class);
            List<String> permission =JSONObject.parseArray(map.get("permission").toString() , String.class);

            authorizationInfo.setRoles(new HashSet<String>(roles));
            authorizationInfo.setStringPermissions(new HashSet<String>(permission));
        } catch (HttpException e) {
            e.printStackTrace();
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {

        AuthenticationInfo info = super.doGetAuthenticationInfo(authenticationToken);
        if (info == null) {
            return info;
        }
        String userName = (String) info.getPrincipals().getPrimaryPrincipal();
        String realName = null;


        JSONObject jsonObject = null;
        String loginName=null;
        String name=null;
        Integer status=null;
        Integer type=null;
        String mobile=null;
        try {
            String url = AUTH_FINDUSER_URL + "?loginName=" + userName;
            String result = HttpClientUtil.buildGetRequest(url).setLimitResult(1).execute();
            jsonObject = JSON.parseObject(result);
            if(jsonObject!=null) {
                loginName=jsonObject.getString("loginName");
                name=jsonObject.getString("name");
                status=jsonObject.getInteger("status");
                type=jsonObject.getInteger("type");

                mobile=jsonObject.getString("mobile");
            }
        } catch (HttpException e) {
            logger.error("权限获取失败"+ e.getMessage(), e);
            throw new AccountException("登录失败，请稍后重试！");
        }

        // 检查状态是否被锁定
        if (status==2) {
            throw new LockedAccountException("用户已被锁定，请与管理员联系");
        }
        realName = StringUtils.defaultString(name, loginName);

        return new UserAuthenticationInfo(jsonObject, type, info.getCredentials(), realName);
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
