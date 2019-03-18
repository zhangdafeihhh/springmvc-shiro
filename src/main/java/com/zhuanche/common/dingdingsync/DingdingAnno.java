package com.zhuanche.common.dingdingsync;

import java.lang.annotation.*;

/**
 * @Author fanht
 * @Description 含有该注解的controller方法存储到mq
 * @Date 2019/2/28 上午11:26
 * @Version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface DingdingAnno {
    String cityId() default "";  //城市id
    String supplierId() default ""; //供应商id
    String teamId() default ""; //teamId
    String method() default ""; //添加修改删除方法
    String level() default "";  //级别 0 城市 1 供应商 2 车队班组
}
