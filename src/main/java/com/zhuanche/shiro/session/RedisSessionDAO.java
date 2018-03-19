package com.zhuanche.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class RedisSessionDAO extends CachingSessionDAO {

    private RedisTemplate<Serializable, Session> redisTemplate;

    private static final String SESSION_KEY = "crm_session_key_";

    @Override
    protected void doUpdate(Session session) {
        redisTemplate.opsForValue().set(SESSION_KEY+session.getId(), session,4,TimeUnit.HOURS);
    }

    @Override
    protected void doDelete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        redisTemplate.delete(SESSION_KEY+session.getId());
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        redisTemplate.opsForValue().set(SESSION_KEY+sessionId, session,4,TimeUnit.HOURS);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return redisTemplate.opsForValue().get(SESSION_KEY+sessionId);
    }

    public void setRedisTemplate(RedisTemplate<Serializable, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
