package com.zhuanche.shiro.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于REDIS而实现SHIRO的Cache实现类
 * @author zhaoyali
 */
@Slf4j
public class RedisCache<K, V> implements Cache<K, V> {
    private static final int MAX_LENGTH = 1000000;
    /**
     *  缓存名称
     */
    private String cachename;
    private RedisTemplate<String, Serializable> redisTemplate;
    private int expireSeconds;
    private String keySetKey;

	public RedisCache(String cachename, RedisTemplate<String, Serializable> redisTemplate, int expireSeconds) {
		super();
		this.cachename = cachename;
		this.redisTemplate = redisTemplate;
		this.expireSeconds = expireSeconds;
        this.keySetKey = "shiro.set.key" + cachename;
	}

	@Override
    @SuppressWarnings("unchecked")
	public V get(K key) throws CacheException {
        try {
            return (V) redisTemplate.opsForValue().get(cachename + key);
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
    @Override
    @SuppressWarnings("unchecked")
	public V put(K key, V value) throws CacheException {
        try {
            V previousValue = (V) redisTemplate.opsForValue().getAndSet(cachename + key, (Serializable) value);
            redisTemplate.expire(cachename + key, expireSeconds, TimeUnit.SECONDS);
            redisTemplate.opsForZSet().add(keySetKey, cachename + key, System.currentTimeMillis());
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
    @Override
    @SuppressWarnings("unchecked")
	public V remove(K key) throws CacheException {
        log.info("shiro redis cache remove {}", key);
        try {
            checkCorrect();
            V value = (V) redisTemplate.opsForValue().get(cachename + key);
            redisTemplate.delete(cachename + key);
            redisTemplate.opsForZSet().remove(keySetKey, cachename + key);
        	return value;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Removes all elements in the cache, but leaves the cache in a useable state.
     */
    @Override
    public void clear() throws CacheException {
        try {
            log.info("shiro redis cache clear");
            Set<K> keys = this.keys();
        	if(keys==null || keys.size()==0 ) {
                return ;
        	}
            keys.forEach(key-> redisTemplate.delete((String)key));
            redisTemplate.delete(keySetKey);
            log.info("shiro redis clear success size:{}", keys.size());
            keys.clear();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public int size() {
        try {
            int size = Math.toIntExact(redisTemplate.opsForZSet().size(keySetKey));
            log.info("shiro redis cache size:{}", size);
            return size;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
	public Set<K> keys() {
        try {
            int size = this.size();
            if (size == 0) {
                return null;
            }
            return redisTemplate.opsForZSet().range(keySetKey, 0, size - 1)
                    .stream().map(e -> (K) e).collect(Collectors.toSet());
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
	public Collection<V> values() {
        try {
        	Set<K> keys = this.keys();
        	if(keys==null || keys.size()==0) {
                return Collections.emptyList();
        	}
            List<V> values = new ArrayList<V>(keys.size());
        	for(K key : keys ) {
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

    private void checkCorrect(){
        int size = Math.toIntExact(redisTemplate.opsForZSet().size(keySetKey));
        if (size > MAX_LENGTH) {
            redisTemplate.delete(keySetKey);
        }

        //清理已过期的集合元素
        Set<Serializable> expiredElement = redisTemplate.opsForZSet().rangeByScore(keySetKey, 0, System.currentTimeMillis() - expireSeconds * 1000);
        Optional.ofNullable(expiredElement).ifPresent(keys->{
            keys.forEach(key->{
                log.info("shiro redis cache clear expired key:{}", key);
                redisTemplate.opsForZSet().remove(keySetKey, key);
            });
        });
    }

}