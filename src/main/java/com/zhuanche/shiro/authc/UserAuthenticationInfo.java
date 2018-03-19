package com.zhuanche.shiro.authc;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class UserAuthenticationInfo extends SimpleAuthenticationInfo {

    //1:本地，即帐号密码，2:LDAP
    private Integer authType;

    public UserAuthenticationInfo(){
        super();
    }

    public UserAuthenticationInfo(Object principal, Object hashedCredentials, ByteSource credentialsSalt, String realmName){
        super(principal,hashedCredentials,credentialsSalt,realmName);
    }

    public UserAuthenticationInfo(Object principal,Integer authType, Object hashedCredentials, ByteSource credentialsSalt, String realmName){
        super(principal,hashedCredentials,credentialsSalt,realmName);
        this.authType = authType;
    }

    public UserAuthenticationInfo(Object principal,Integer authType, Object hashedCredentials, String realmName){
        this.principals = new SimplePrincipalCollection(principal, realmName);
        this.credentials = hashedCredentials;
        this.authType = authType;
    }

    public Integer getAuthType() {
        return authType;
    }
}
