package com.zhuanche.common.cache;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import redis.clients.jedis.*;

import java.util.*;

/**
 * 基于Redis sentinel（哨兵容灾部署）架构的分布式缓存实现
 * @author zhaoyali
 **/
public final class RedisCacheDriverUtil {
	private static final Logger log = LoggerFactory.getLogger(RedisCacheDriverUtil.class);
	private static final int MAX_EXPIRE_SECONDS = 7 * 24 * 60 * 60; //最多存储7天
	private static Properties properties;
	/**连接池配置**/
	private static JedisPoolConfig   jedisPoolConfig = null;
	/**连接池**/
	private static JedisSentinelPool pool = null;
	
	/**初始化   配置文件位置configLocation **/
	public static synchronized void init( String configLocation) {
		try{
			RedisCacheDriverUtil.properties = PropertiesLoaderUtils.loadAllProperties(configLocation);
			log.info("***************initializing RedisCacheDriverUtil["+configLocation+"]");
			log.info( RedisCacheDriverUtil.properties.toString()  );
			//创建连接池配置
			if(jedisPoolConfig==null) {
				jedisPoolConfig = new JedisPoolConfig();
				jedisPoolConfig.setMaxTotal( Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.maxActive", "60")) );
				jedisPoolConfig.setMaxIdle( Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.maxIdle", "10")) );
				jedisPoolConfig.setMinIdle( Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.minIdle", "5")) );
				jedisPoolConfig.setMaxWaitMillis( Long.parseLong(RedisCacheDriverUtil.properties.getProperty("redis.maxWait", "3000")) );
				jedisPoolConfig.setTestOnBorrow("true".equalsIgnoreCase(RedisCacheDriverUtil.properties.getProperty("redis.testOnBorrow", "false")));
				jedisPoolConfig.setTestOnReturn("true".equalsIgnoreCase(RedisCacheDriverUtil.properties.getProperty("redis.testOnReturn", "false")));
				jedisPoolConfig.setTestWhileIdle("true".equalsIgnoreCase(RedisCacheDriverUtil.properties.getProperty("redis.testWhileIdle", "false")));
				jedisPoolConfig.setMinEvictableIdleTimeMillis(Long.parseLong(RedisCacheDriverUtil.properties.getProperty("redis.minEvictableIdleTimeMillis", "600000")));
				jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(Long.parseLong(RedisCacheDriverUtil.properties.getProperty("redis.softMinEvictableIdleTimeMillis", "600000")));
				jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Long.parseLong(RedisCacheDriverUtil.properties.getProperty("redis.timeBetweenEvictionRunsMillis", "300000")));
				jedisPoolConfig.setNumTestsPerEvictionRun(Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.numTestsPerEvictionRun", "3")));
				log.info("***************JedisPoolConfig initialized successful");
			}
			//创建连接池
			if(pool==null) {
				String masterName = RedisCacheDriverUtil.properties.getProperty("redis.sentinel.name");
				Set<String> sentinels = new HashSet<String>( Arrays.asList(RedisCacheDriverUtil.properties.getProperty("redis.sentinel.connectString").split(",")) );
				String password = RedisCacheDriverUtil.properties.getProperty("redis.password");
				int connectionTimeout = Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.connectionTimeout", "5000"));
				int soTimeout = Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.soTimeout", "5000"));
				int database = Integer.parseInt(RedisCacheDriverUtil.properties.getProperty("redis.database", "0"));
				String clientName = RedisCacheDriverUtil.properties.getProperty("redis.clientName", null);
				if(password!=null && password.trim().length()==0) {
					password = null;
				}
				pool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig, connectionTimeout, soTimeout, password, database, clientName );
				log.info("***************JedisSentinelPool initialized successful");
			}
			log.info("***************initialized RedisCacheDriverUtil["+configLocation+"]\n");
		} catch (Exception e) {
			System.err.println("初始化RedisCacheDriverUtil发生异常！配置文件："+ configLocation );
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	//---------------------------------------------------------------------------------------------------------------------在下面封装常用的操作begin
	/**保存对象到Redis**/
	public static void set(String key, Object value ) {
		if(key==null||value==null) {
			return;
		}
		Jedis jedis = pool.getResource();
        try{
            jedis.set(key, JSON.toJSONString(value) );
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	
	/**保存对象到Redis, 设置过期时间**/
	public static void set(String key, Object value, int seconds) {
		if(key==null||value==null) {
			return;
		}
		if( seconds<=0) {
			return;
		}
		if( seconds>MAX_EXPIRE_SECONDS ) {
			seconds = MAX_EXPIRE_SECONDS;
		}
		Jedis jedis = pool.getResource();
        try{
            jedis.setex(key, seconds, JSON.toJSONString(value) );
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}

	/**保存对象到Redis , 只有当不存在时才保存成功**/
	public static Long setnx(String key, Object value) {
		if(key==null||value==null) {
			return 0L;
		}
		Jedis jedis = pool.getResource();
        try{
            return jedis.setnx(key, JSON.toJSONString(value) );
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        	return 0L;
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	
	/**获取对象**/
	public static <T> T get( String key , Class<T> clazz) {
		if(key==null) {
			return null;
		}
		Jedis jedis = pool.getResource();
        try{
        	String rawValue = jedis.get(key);
        	if(rawValue!=null ) {
        		return JSON.parseObject(rawValue, clazz);
        	}
        	return null;
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        	return null;
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	
	/**获取对象（支持大量KEY的获取，性能比单个获取要提升100倍左右）**/
	public static <T> Map<String,T> batchGet( Set<String> keys , Class<T> clazz) {
		if(keys==null||keys.size()==0) {
			return null;
		}
		Jedis jedis = pool.getResource();
        try{
        	Pipeline pipeline =  jedis.pipelined();
        	Map<String, Response<String>> responseMap = new HashMap<String, Response<String>>( keys.size() * 2 );
        	//批量发送读数据请求
        	for (String key : keys) {
            	Response<String> response = pipeline.get(key);
        	    responseMap.put(key, response);
        	}
        	//等待全部传输完毕
        	pipeline.sync();
        	//批量获取结果
        	Map<String, T> result = new HashMap<String, T>( keys.size() * 2 );
        	for(String key :  responseMap.keySet() ) {
        		String rawValue = responseMap.get(key).get() ;
        		if(rawValue!=null) {
        			T t = JSON.parseObject(rawValue, clazz);
        			result.put(key, t);
        		}
        	}
        	return result;
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        	return null;
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	
	/**保存新值，并返回原值**/
	public static <T> T getSet( String key , Object value, Class<T> clazz) {
		if(key==null||value==null) {
			return null;
		}
		Jedis jedis = pool.getResource();
        try{
        	String rawValue = jedis.getSet(key, JSON.toJSONString(value));
        	if(rawValue!=null ) {
        		return JSON.parseObject(rawValue, clazz);
        	}
        	return null;
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        	return null;
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	/**自增**/
	public static Long incr( String key ) {
		if(key==null) {
			return 0L;
		}
		Jedis jedis = pool.getResource();
        try{
        	return jedis.incr(key);
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        	return 0L;
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	/**自减**/
	public static Long decr( String key ) {
		if(key==null) {
			return 0L;
		}
		Jedis jedis = pool.getResource();
        try{
        	return jedis.decr(key);
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        	return 0L;
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	
	/**删除KEY**/
	public static void delete(String... keys) {
		if(keys==null) {
			return;
		}
		Jedis jedis = pool.getResource();
        try{
        	jedis.del(keys);
        } catch (Exception e) {
        	log.error("RedisCacheDriverUtil have Exception", e);
        }finally {
            if(jedis!=null){
            	jedis.close();
            }
        }
	}
	//---------------------------------------------------------------------------------------------------------------------在上面封装常用的操作end
}