package com.zhuanche.shiro.session;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

/**实现shiro分布式会话持久化 （基于REDIS），实现会话数据的CRUD操作
 * 
 * 注意：保存在REDIS中的会话过期时间只要略大于shiro会话管理器中的globalSessionTimeout即可，设置太长的时效没有意义
 * @author zhaoyali
 **/
public class RedisSessionDAO extends CachingSessionDAO{

    private RedisTemplate<Serializable, Session> redisTemplate;

    private static final String SESSION_KEY = "mp_manage_sessionKey_";

    @Override
    protected void doUpdate(Session session) {
        redisTemplate.opsForValue().set(SESSION_KEY+session.getId(), session,1,TimeUnit.HOURS);
//        RedisCacheUtil.set(SESSION_KEY+session.getId(), session, 1*60*60 );
    }

    @Override
    protected void doDelete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        redisTemplate.delete(SESSION_KEY+session.getId());
//        RedisCacheUtil.delete(SESSION_KEY+session.getId());
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(SESSION_KEY+sessionId, session,1,TimeUnit.HOURS);
//        RedisCacheUtil.set(SESSION_KEY+session.getId(), session, 1*60*60 );
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return redisTemplate.opsForValue().get(SESSION_KEY+sessionId);
//    	return (Session) RedisCacheUtil.get(SESSION_KEY+sessionId, SimpleSession.class );
    }

    public void setRedisTemplate(RedisTemplate<Serializable, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}