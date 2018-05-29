package com.zhuanche.common.cache;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
/**基于内存的一个简易轻量缓存 
 * @author zhaoyali
 **/
public final class MemoryCacheUtil{
	private static final int              MAX_CACHE_SIZE = 4096;
	private static boolean                isClearing     = false;
	private static Map<String,CacheValue> valueMappings  = new ConcurrentHashMap<String,CacheValue>(MAX_CACHE_SIZE);//用户保存缓存的对象
	static{
		TimerTask clearMemoryCacheTask = new TimerTask(){
			@Override
			public void run(){
				int removed = 0;
				long b = 0L, e = 0L;
				try{
					if(isClearing){
						return;
					}
					isClearing = true;
					
					b = System.currentTimeMillis();
					for(String key : valueMappings.keySet() ){
						CacheValue cacheValue = valueMappings.get(key);
						if(cacheValue==null){
							valueMappings.remove(key);
							removed++;
							continue;
						}
						if(cacheValue.getExpiretime().longValue()<b){
							valueMappings.remove(key);
							removed++;
						}
					}
					e = System.currentTimeMillis();
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					isClearing = false;
					System.out.println("ClearMemoryCache Cost:"+(e-b)+"ms, Removed:"+removed+", Current:"+valueMappings.size());
				}
			}
		};
		Timer timer = new Timer("ClearMemoryCacheTimer", true);
		timer.scheduleAtFixedRate(clearMemoryCacheTask, 5000L, 30*1000);
	}
	public static void set(String key, Object value, int seconds){
		if(key==null||key.trim().length()==0||value==null||seconds<=0){
			return;
		}
		if(seconds>86400){//最多存24小时
			seconds = 86400;
		}
		valueMappings.remove(key);
		valueMappings.put(key, new CacheValue(value,System.currentTimeMillis()+(seconds*1000)) );
	}
	public static Object get(String key){
		CacheValue cacheValue = valueMappings.get(key);
		if(cacheValue==null){
			return null;
		}
		if(cacheValue.getExpiretime().longValue()<System.currentTimeMillis()){
			return null;//超时了返回NULL
		}
		return cacheValue.getData();
	}
	public static void del(String key){
		valueMappings.remove(key);
	}
	public static boolean containsKey(String key){
		return valueMappings.containsKey(key);
	}
	
	/**缓存值封装对象**/
	static class CacheValue{
		private Object data;           //缓存值
		private Long   expiretime = 0L;//缓存过期时间（单位：毫秒）
		public CacheValue(Object data, Long expiretime) {
			super();
			this.data = data;
			this.expiretime = expiretime;
		}
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
		public Long getExpiretime() {
			return expiretime;
		}
		public void setExpiretime(Long expiretime) {
			this.expiretime = expiretime;
		}
	}

	//-----------------------------debug
	public static void main(String[] s) throws InterruptedException{
		System.out.println("begin..." );
		for(int i=0;i<300000;i++){
			String k = UUID.randomUUID().toString();
			String v = UUID.randomUUID().toString();
			MemoryCacheUtil.set(k, v, i+60);
		}
		System.out.println("testing...");
		
		Thread.sleep(24*60*60*1000);
	}
}