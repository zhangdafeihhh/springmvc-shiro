package com.zhuanche.serv.sync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fanht
 * @description 异步线程池
 * @date 2021/1/5 上午9:48
 * @version 1.0
 */
@Configuration
@EnableAsync
public class SyncThreadPool {

    /**核心线程数*/
    private static final Integer CORE_POOL_SIZE = 20;

    /**最大线程数*/
    private static final Integer MAX_POOL_SIZE = 100;

    /**允许线程空闲时间 （单位 秒）*/
    private static final Integer KEEP_ALIVE_TIME  = 10;

    /**缓冲队列大小*/
    private static final Integer QUEUE_CAPACITY  = 200;

    /**线程名前缀  主要是在service层使用*/
    private static final  String THREADNAME_PREFIX  = "Async-service-";


    @Bean("syncTaskExecutor")
    public ThreadPoolTaskExecutor  syncTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_TIME);
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
        taskExecutor.setThreadNamePrefix(THREADNAME_PREFIX);
        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：由调用线程（提交任务的线程）处理该任务
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

}
