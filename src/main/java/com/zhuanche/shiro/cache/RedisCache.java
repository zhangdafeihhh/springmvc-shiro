package com.zhuanche.shiro.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 基于REDIS而实现SHIRO的Cache实现类
 * @author zhaoyali
 */
public class RedisCache<K, V> implements Cache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);
    private String cachename;              //缓存名称
    private RedisTemplate<String, Serializable>    redisTemplate;
    private int expireSeconds = 3600; //默认的有效期

	public RedisCache(String cachename, RedisTemplate<String, Serializable> redisTemplate, int expireSeconds) {
		super();
		this.cachename = cachename;
		this.redisTemplate = redisTemplate;
		this.expireSeconds = expireSeconds;
	}
	
    @SuppressWarnings("unchecked")
	public V get(K key) throws CacheException {
        try {
        	return (V) redisTemplate.opsForValue().get(  cachename+(String)key );
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Puts an object into the cache.
     *
     * @param key   the key.
     * @param value the value.
     */
    @SuppressWarnings("unchecked")
	public V put(K key, V value) throws CacheException {
        try {
       	 V previousValue = (V) redisTemplate.opsForValue().getAndSet(   cachename+(String)key, (Serializable)value   );
       	 redisTemplate.expire( cachename+(String)key , expireSeconds, TimeUnit.SECONDS);
       	 return previousValue;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Removes the element which matches the key.
     *
     * <p>If no element matches, nothing is removed and no Exception is thrown.</p>
     *
     * @param key the key of the element to remove
     */
    @SuppressWarnings("unchecked")
	public V remove(K key) throws CacheException {
        try {
        	V value =  (V) redisTemplate.opsForValue().get(  cachename+(String)key );
        	redisTemplate.delete(  cachename+(String)key );
        	return value;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Removes all elements in the cache, but leaves the cache in a useable state.
     */
    public void clear() throws CacheException {
        try {
        	Set<String> keys = redisTemplate.keys( cachename+"*" );
        	if(keys==null || keys.size()==0 ) {
                return ;
        	}
        	redisTemplate.delete(keys);
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    public int size() {
        try {
        	Set<String> keys = redisTemplate.keys( cachename+"*" );
        	if(keys!=null) {
        		return keys.size();
        	}
            return 0;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
	public Set<K> keys() {
        try {
        	return (Set<K>) redisTemplate.keys( cachename+"*" );
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
	public Collection<V> values() {
        try {
        	Set<String> keys = redisTemplate.keys( cachename+"*" );
        	if(keys==null || keys.size()==0) {
                return Collections.emptyList();
        	}
            List<V> values = new ArrayList<V>(keys.size());
        	for(String key : keys ) {
            	V value = (V) redisTemplate.opsForValue().get(key);
        		if(value!=null) {
        			values.add(value);
        		}
        	}
            return Collections.unmodifiableList(values);
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }
}