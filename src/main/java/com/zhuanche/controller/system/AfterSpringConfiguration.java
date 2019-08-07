package com.zhuanche.controller.system;

import com.sq.startup.config.manager.ConfigManagerInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @program: mp-manage
 * @description: spring启动后加载类
 * @author: zjw
 * @create: 2019-07-25 17:58
 **/
@Component("afterSpringConfiguration")
public class AfterSpringConfiguration implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(AfterSpringConfiguration.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent()==null){
            try{
                logger.info("开始加载配置中心");
                String classPath = AfterSpringConfiguration.class.getClassLoader().getResource("").getPath();
                ConfigManagerInit.initManagerFromClasspath();
            }catch(Exception e){
                logger.error("configmanagerconfigfail", e);
                throw new RuntimeException("config manager config fail");
            }
        }
    }
}