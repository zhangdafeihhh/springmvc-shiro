package com.zhuanche.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author fanht
 * @Description
 * @Date 2019/8/22 下午8:03
 * @Version 1.0
 */
@Component
public class EnvUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static   String ENVIMENT;

    @Value("${envirment}")
    public  void setENVIMENT(String ENVIMENT) {
        EnvUtils.ENVIMENT = ENVIMENT;
        logger.info("envirment*********:" + ENVIMENT);
    }
}
