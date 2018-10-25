package com.zhuanche.shiro.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.DynamicRoutingDataSource.DataSourceMode;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.realm.UsernamePasswordRealm;

import mapper.mdbcarmanage.ex.CarAdmUserExMapper;
import mapper.mdbcarmanage.ex.SaasRolePermissionRalationExMapper;
import mapper.mdbcarmanage.ex.SaasUserRoleRalationExMapper;

/**实现shiro分布式会话持久化 （基于REDIS），实现会话数据的CRUD操作
 * 
 * 注意：保存在REDIS中的会话过期时间只要略大于shiro会话管理器中的globalSessionTimeout即可，设置太长的时效没有意义。
 * 此外，当权限信息、角色信息、用户信息发生变化时，可以同时自动清理与之相关联的会话
 * @author zhaoyali
 **/
public class RedisSessionDAO extends CachingSessionDAO{
    private static final String KEY_PREFIX_OF_SESSION      = "mp_manage_sessionId_";        //KEY：会话ID，VALUE：shiro Session对象
    private static final String KEY_PREFIX_OF_SESSIONID   = "mp_manage_sessionIds_Of_"; //KEY：登录账户的KEY，VALUE：此账户的所有会话ID（以Set形式存储） 

    @Autowired
    private SaasRolePermissionRalationExMapper saasRolePermissionRalationExMapper;
    @Autowired
    private SaasUserRoleRalationExMapper          saasUserRoleRalationExMapper;
    @Autowired
    private CarAdmUserExMapper                       carAdmUserExMapper;
    private RedisTemplate<String, Serializable>    redisTemplate;
    public void setRedisTemplate(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    private AuthorizingRealm authorizingRealm;
    public void setAuthorizingRealm(AuthorizingRealm authorizingRealm) {
		this.authorizingRealm = authorizingRealm;
	}

	@Override
    protected void doUpdate(Session session) {
        redisTemplate.opsForValue().set(KEY_PREFIX_OF_SESSION+session.getId(), (Serializable)session,30,TimeUnit.MINUTES);
//        RedisCacheUtil.set(SESSION_KEY+session.getId(), session, 1*60*60 );
    }

    @Override
    protected void doDelete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        redisTemplate.delete(KEY_PREFIX_OF_SESSION+session.getId());
//        RedisCacheUtil.delete(SESSION_KEY+session.getId());
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(KEY_PREFIX_OF_SESSION+sessionId, (Serializable)session,30,TimeUnit.MINUTES);
//        RedisCacheUtil.set(SESSION_KEY+session.getId(), session, 1*60*60 );
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = (Session)redisTemplate.opsForValue().get(KEY_PREFIX_OF_SESSION+sessionId);
        return session;
//    	return (Session) RedisCacheUtil.get(SESSION_KEY+sessionId, SimpleSession.class );
    }
    
    /********************************************自动清理会话BEGIN****************************************************************************************************************/
    /**一、保存当前登录用户的会话ID**/
    @SuppressWarnings("unchecked")
	public void saveSessionIdOfLoginUser( String loginAccount, String sessionId ){
    	Set<String> allSessionIds =  (Set<String>) redisTemplate.opsForValue().get(KEY_PREFIX_OF_SESSIONID+loginAccount);
    	if(allSessionIds == null ) {
    		allSessionIds =  new HashSet<String>(4);
    	}
    	allSessionIds.add(sessionId);
    	redisTemplate.opsForValue().set(KEY_PREFIX_OF_SESSIONID+loginAccount, (Serializable)allSessionIds, 24, TimeUnit.HOURS);
    }
    /**二、当权限信息、角色信息、用户信息发生变化时，同时清理与之相关联的会话**/
    public void clearRelativeSession( final Integer permissionId, final  Integer roleId, final  Integer userId ) {
    	final Cache<Serializable, Session> cache = super.getActiveSessionsCache();
    	new Thread(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try{
					DynamicRoutingDataSource.setMasterSlave("mdbcarmanage-DataSource", DataSourceMode.SLAVE );
			    	//A：如果当权限发生变化时，查询所关联的全部角色ID
			    	List<Integer> roleIds = new ArrayList<Integer>();
			    	if( permissionId!=null ) {
			    		roleIds = saasRolePermissionRalationExMapper.queryRoleIdsOfPermission( permissionId );
			    	}
			    	//B：如果当角色发生变化时，查询所关联的用户ID
			    	if( roleId !=null ) {
			    		roleIds.add(roleId);
			    	}
			    	List<Integer> userIds = new ArrayList<Integer>();
			    	if( roleIds.size()>0 ) {
			    		userIds = saasUserRoleRalationExMapper.queryUserIdsOfRole( roleIds );
			    	}
			    	//C：如果当用户发生变化时，查询出这些用户的登录账户名称
			    	if( userId != null ) {
			    		userIds.add(userId);
			    	}
			    	List<String> accounts = new ArrayList<String>();
			    	if(userIds.size()>0) {
			    		accounts = carAdmUserExMapper.queryAccountsOfUsers(userIds);
			    	}
			    	//D：汇总需要清理的REDIS KEY 和 sessionId
			    	if(accounts.size() ==0) {
			    		return;
			    	}
			    	Set<String> redisKeysNeedDelete = new HashSet<String>();//这是需要清除的所有REDIS KEY
			    	Set<String> allSessionIds              = new HashSet<String>();//这是需要清除的所有的sessionId
			    	for( String account : accounts) {
			    		redisKeysNeedDelete.add( KEY_PREFIX_OF_SESSIONID + account );
			        	Set<String> sessionIds  =  (Set<String>) redisTemplate.opsForValue().get(KEY_PREFIX_OF_SESSIONID+account);
			        	if(sessionIds!=null && sessionIds.size()>0) {
			        		allSessionIds.addAll(sessionIds);
			        	}
			    	}
			    	
			    	//E1：执行清除执久化的会话(这里是保存在REDIS中的)
			    	for( String sessionId : allSessionIds) {
	            		redisKeysNeedDelete.add( KEY_PREFIX_OF_SESSION + sessionId );
			    	}
			    	redisTemplate.delete(redisKeysNeedDelete);
			    	//E2：执行清理shiro会话缓存
			    	if(cache!=null) {
		        		for(String sessionId : allSessionIds ){
		        			SimpleSession session = (SimpleSession)cache.get(sessionId);
		        			if(session!=null) {
			        			session.setExpired(true);
		        			}
		        			cache.remove(sessionId);
		        		}
			    	}
	        		//E3：执行清理shiro 认证与授权缓存
			    	for( String account : accounts) {
		        		SSOLoginUser principal  = new SSOLoginUser();
		        		principal.setLoginName(  account );
		        		SimplePrincipalCollection simplePrincipalCollection = new SimplePrincipalCollection( );
		        		simplePrincipalCollection.add(principal, authorizingRealm.getName() );
		        		((UsernamePasswordRealm)authorizingRealm).clearCache( simplePrincipalCollection );
			    	}
				}catch(Exception ex) {
				}finally {
					DynamicRoutingDataSource.setDefault("mdbcarmanage-DataSource");
				}
			}
		}).start();
    }
    /********************************************自动清理会话END******************************************************************************************************************/

}