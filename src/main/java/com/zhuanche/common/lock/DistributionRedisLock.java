package com.zhuanche.common.lock;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import com.zhuanche.common.cache.RedisCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Redis实现的简易分布式锁
 * @author zhaoyali
 */
public final class DistributionRedisLock{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final  Set<String>  LOCKED_NAMES    = new ConcurrentSkipListSet<String>();//用于保存已经获取到的锁名称      
	private static final  Random   random              = new Random();
	/**获取锁时的超时时间(单位:毫秒)**/
    private static final  int      GETLOCK_TIMEOUT     = 60*1000*2;
    /**锁过期失效时间(单位:毫秒)**/
    private static final  int      LOCK_EXPIRE_TIMEOUT = 60*1000;
    /**锁前缀**/
    private static final  String   LOCKNAME_PREFIX     = "yc_redislock_";
    /**锁后缀(心跳)**/
    private static final  String   HEARTBEAT_SUFFIX    = "_hb";
    
    
    /**锁名称**/
    private String lockname;
    /**获取锁的客户端的唯一性标识，表示一次获取会话**/
    private String token;
    /**锁定状态**/
    private volatile boolean locked = false;
    
    private DistributionRedisLock(){}
    private DistributionRedisLock(String lockname) {
		this.lockname = LOCKNAME_PREFIX.concat(lockname);
		this.token    = UUID.randomUUID().toString().replace("-", "");
	}
    private RedisLockValue get(){
    	return RedisCacheUtil.get(lockname, RedisLockValue.class);
    }
    private boolean setNX(long expireTime){
    	return 1L == RedisCacheUtil.setnx(lockname,new RedisLockValue(token,expireTime));
    }
    private RedisLockValue getSet(long expireTime){
    	RedisLockValue oldValue =  RedisCacheUtil.getSet(lockname,new RedisLockValue(token,expireTime), RedisLockValue.class);
    	return oldValue;
    }
    
    //------当分布式程序获取同一个锁时，防止出现以下情况：当已经获取此锁的进程（物理机器）实际业务运行时间超过LOCK_EXPIRE_TIMEOUT时，此锁会再被其它进程（物理机器）获取
    static{
    	Timer timer = new Timer("JrdRedisLockHeartBeat", true);
    	timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				try{
					Set<String> lockedNames = Collections.unmodifiableSet(LOCKED_NAMES);
					for(String lockName : lockedNames){
						RedisCacheUtil.set(lockName + HEARTBEAT_SUFFIX, "Y", 40);
					}
				}catch(Exception ex){
					System.err.println("更新锁心跳异常！");
					ex.printStackTrace();
				}
			}}, 30*1000, 30*1000);
    }
    
    /**
     * 获取锁（非公平锁）, 默认获取超时为2分钟
     */
    public boolean lock(){
    	return lock(GETLOCK_TIMEOUT/1000);
    }
    /**
     * 获取锁（非公平锁）, 获取超时为timeoutSeconds秒
     */
    public boolean lock(int timeoutSeconds){
    	int timeout = timeoutSeconds*1000;
        while (timeout >= 0) {
        	//在同一个JVM内，防止出现以下情况：当已经获取此锁的线程实际业务运行时间超过LOCK_EXPIRE_TIMEOUT时，此锁会再被其它线程获取
        	if(LOCKED_NAMES.contains(lockname)){
                int sleeptime = random.nextInt(100)+100;
                try{Thread.sleep(sleeptime);} catch (InterruptedException e) {}
                timeout -= sleeptime;
                continue;
        	}
        	//当分布式程序获取同一个锁时，防止出现以下情况：当已经获取此锁的进程（物理机器）实际业务运行时间超过LOCK_EXPIRE_TIMEOUT时，此锁会再被其它进程（物理机器）获取
        	String hearbeatFlag = RedisCacheUtil.get(lockname + HEARTBEAT_SUFFIX , String.class );
        	if(hearbeatFlag!=null){
                int sleeptime = random.nextInt(100)+100;
                try{Thread.sleep(sleeptime);} catch (InterruptedException e) {}
                timeout -= sleeptime;
                continue;
        	}
        	
        	//锁到期时间
            long expires = System.currentTimeMillis() + LOCK_EXPIRE_TIMEOUT + 1;
            boolean ok = setNX(expires);
            if (ok) {
				logger.info(Thread.currentThread().getName()+":获得锁成功，通过setNX");
				LOCKED_NAMES.add(lockname);
                locked = true;
                return locked;
            }
            //获取当前锁信息
            RedisLockValue redisLockValue = get();
            if (redisLockValue != null && redisLockValue.getExpireTime() < System.currentTimeMillis()) {
            	//利用getSet的原子性操作来设置并获取到旧值
            	RedisLockValue oldRedisLockValue = getSet(expires);
                //最先设置的获取锁
            	if (oldRedisLockValue != null && oldRedisLockValue.getToken().equals(redisLockValue.getToken())) {
            		logger.info(Thread.currentThread().getName()+":获得锁成功，通过getSet");
    				LOCKED_NAMES.add(lockname);
            		locked = true;
                    return locked;
                }
            }
            //防止饥饿线程出现 采用随机休眠时间
            int sleeptime = random.nextInt(100)+100;
            try{Thread.sleep(sleeptime);} catch (InterruptedException e) {}
            timeout -= sleeptime;
        }
        
        //如果已经超时，但只是因为此时缓存还有值，因为反序列化异常导致GET取不到时，解决死锁问题
        try{Thread.sleep(300);} catch (InterruptedException e) {}
        RedisLockValue redisLockValue = get();
        if(redisLockValue==null){
        	//强制删掉即可
    		LOCKED_NAMES.remove(lockname);
    		RedisCacheUtil.delete(lockname);
    		RedisCacheUtil.delete( lockname + HEARTBEAT_SUFFIX );
        }
        return locked;
    }
    /**
     * 释放锁
     */
    public void unLock(){
    	if(locked){
    		LOCKED_NAMES.remove(lockname);
    		RedisCacheUtil.delete( lockname + HEARTBEAT_SUFFIX );
			
        	/**只有在尚未超时才进行删除**/
    		RedisLockValue redisLockValue = get();
    		if(redisLockValue!=null && System.currentTimeMillis() < redisLockValue.getExpireTime()){
    			RedisCacheUtil.delete(lockname);
    		}
    		logger.info(Thread.currentThread().getName()+":释放锁失功");
    	}
    }
    /**
     * 生成锁
     */
    public static DistributionRedisLock newLock(String lockName){
    	return new DistributionRedisLock(lockName);
    }
    

    
    
    //-----------------------------------------------------develop debug
    public static void main(String[] args) throws InterruptedException {
    	//1.主线程先占上锁
		DistributionRedisLock userLock = DistributionRedisLock.newLock("test123");
		userLock.lock();
    	
    	//2.其它线程再获得锁
		for( int i = 0; i < 30;i++){
			final int ii = (i+1);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Thread.currentThread().setName("RedisLockTester-"+ii);
					DistributionRedisLock userLock = DistributionRedisLock.newLock("test123");
					try{
						if(userLock.lock()==false){
							System.out.println(Thread.currentThread().getName()+":获得锁超时了，失败");
							return;
						}
						//DOBIZ begin
						System.out.println(Thread.currentThread().getName()+":开始运行业务");
						for(int j=0;j<20;j++){
							Thread.sleep(1000);
							System.out.println(Thread.currentThread().getName()+":正在运行业务哦，当前运行了"+(j+1)+"秒了。");
						}
						System.out.println(Thread.currentThread().getName()+":运行业务结束了");
						//DOBIZ end
					}catch(Exception e){
					}finally{
						userLock.unLock();
					}
				}
			}).start();
		}
		
		//3.主线程再释放锁
		Thread.sleep(5*1000);//------------在此调节时长，用来模拟已经获得锁的主线程运行不同的时长时，其它线程的运行况状
		System.out.println("主线程运行完毕");
		userLock.unLock();
		
		
		
		//4.主线程另一次获取	
		DistributionRedisLock userLock2 = DistributionRedisLock.newLock("test123");
		try{
			if(userLock2.lock()==false){
				System.out.println(Thread.currentThread().getName()+":获得锁超时了，失败");
				return;
			}
			Thread.sleep(3000);
		}catch(Exception ex){
		}finally{
			userLock2.unLock();
		}
		
	}
}




/**锁值对象**/
class RedisLockValue{
	private String token;   //当前获得此锁的会话ID
	private long expireTime;//当前此锁的过期时间
	
	public RedisLockValue(){}
	public RedisLockValue(String token, long expireTime){
		super();
		this.token = token;
		this.expireTime = expireTime;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
}