package com.zhuanche.shiro.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zhuanche.entity.mdbcarmanage.CarAdmUser;

import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.SaasPermissionExMapper;
import mapper.mdbcarmanage.ex.SaasRoleExMapper;

/**认证  与  权限  **/
public class UsernamePasswordRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(UsernamePasswordRealm.class);
	@Autowired
	private CarAdmUserExMapper carAdmUserExMapper;
	@Autowired
	private SaasPermissionExMapper  saasPermissionExMapper;
	@Autowired
	private SaasRoleExMapper           saasRoleExMapper;
    
    /**重写：获取用户的身份认证信息**/
	@Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException{
		UsernamePasswordToken token = (UsernamePasswordToken)authenticationToken;
		CarAdmUser adMUser = carAdmUserExMapper.queryByAccount(token.getUsername());		
		SSOLoginUser loginUser = new SSOLoginUser();  //当前登录的用户
		loginUser.setId( adMUser.getUserId() );                //用户ID
		loginUser.setLoginName( adMUser.getAccount() );//登录名
		loginUser.setMobile( adMUser.getPhone() );         //手机号码
		loginUser.setName( adMUser.getUserName() );    //真实姓名
		loginUser.setEmail(  null ); //邮箱地址
		loginUser.setType( null );   //
		loginUser.setStatus( adMUser.getStatus() );           //状态
		loginUser.setAccountType( adMUser.getAccountType() );   //自有的帐号类型：[100 普通用户]、[900 管理员]  
		//---------------------------------------------------------------------------------------------------------数据权限BEGIN
		/**此用户可以管理的城市ID**/
		if( StringUtils.isNotEmpty(adMUser.getCities()) ) {
			String[] idStrs = adMUser.getCities().trim().split(",");
			Set<Integer> ids = new HashSet<Integer>( idStrs.length *2 +2 );
			for(String id : idStrs) {
				if( StringUtils.isNotEmpty(id) ) {
					try { ids.add(  Integer.valueOf(id.trim()) ); }catch(Exception e) {}
				}
			}
			loginUser.setCityIds(ids);
		}
		/**此用户可以管理的供应商ID**/
		if( StringUtils.isNotEmpty(adMUser.getSuppliers()) ) {
			String[] idStrs = adMUser.getSuppliers().trim().split(",");
			Set<Integer> ids = new HashSet<Integer>( idStrs.length *2 +2 );
			for(String id : idStrs) {
				if( StringUtils.isNotEmpty(id) ) {
					try { ids.add(  Integer.valueOf(id.trim()) ); }catch(Exception e) {}
				}
			}
			loginUser.setSupplierIds(ids);
		}
		/**此用户可以管理的车队ID**/
		if( StringUtils.isNotEmpty(adMUser.getTeamId()) ) {
			String[] idStrs = adMUser.getTeamId().trim().split(",");
			Set<Integer> ids = new HashSet<Integer>( idStrs.length *2 +2 );
			for(String id : idStrs) {
				if( StringUtils.isNotEmpty(id) ) {
					try { ids.add(  Integer.valueOf(id.trim()) ); }catch(Exception e) {}
				}
			}
			loginUser.setTeamIds(ids);
		}
		//---------------------------------------------------------------------------------------------------------数据权限END
		logger.info( "[获取用户的身份认证信息]="+loginUser);
        return new SimpleAuthenticationInfo(loginUser, authenticationToken.getCredentials()  ,  this.getName() );
    }
    
    /**重写：获取用户授权信息**/
	@Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SSOLoginUser loginUser = (SSOLoginUser) principals.getPrimaryPrincipal();
		String account = loginUser.getLoginName(); //登录名
    	//TODO  只读库
    	//TODO  只读库
		List<String> perms_string = saasPermissionExMapper.queryPermissionCodesOfUser(  loginUser.getId() );
		List<String> roles_string   = saasRoleExMapper.queryRoleCodesOfUser( loginUser.getId() );
    	
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roles = new HashSet<String>( roles_string );
        authorizationInfo.setRoles( roles );
		logger.info( "[获取用户授权信息(角色)] "+account+"="+roles);
        
        Set<String> perms = new HashSet<String>( perms_string );
        authorizationInfo.setStringPermissions(perms);
		logger.info( "[获取用户授权信息(权限)] "+account+"="+perms);
        return authorizationInfo;
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
