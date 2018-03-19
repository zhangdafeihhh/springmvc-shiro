package com.zhuanche.shiro.credentials;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.zhuanche.shiro.authc.UserAuthenticationInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * Created by wuhui on 2016/2/16.
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private RedisTemplate<String, AtomicInteger> redisTemplate;

    private static final String RETRYLIMIT_KEY = "crm_retry_limit_";

    public RetryLimitHashedCredentialsMatcher(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if (info instanceof UserAuthenticationInfo){
            UserAuthenticationInfo userAuthenticationInfo = (UserAuthenticationInfo)info;
            if (userAuthenticationInfo.getAuthType() == 2){
                return true;
            }
        }
        String username = (String)token.getPrincipal();

        //retry count + 1
        AtomicInteger retryCount = redisTemplate.opsForValue().get(RETRYLIMIT_KEY+username);
        if(retryCount == null) {
            retryCount = new AtomicInteger(0);
            redisTemplate.opsForValue().set(RETRYLIMIT_KEY+username, retryCount,4,TimeUnit.HOURS);
        }
        if(retryCount.incrementAndGet() > 4) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }
        SecurityUtils.getSubject().getSession().setAttribute("retryCount", retryCount);




        boolean matches = super.doCredentialsMatch(token, info);
        if(matches) {
            //clear retry count
            redisTemplate.delete(RETRYLIMIT_KEY+username);
        }else{
            //throw new
        }
        return matches;
    }
}
