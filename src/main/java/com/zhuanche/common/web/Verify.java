package com.zhuanche.common.web;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 基于Spring MVC拦截器的参数合法性验证，用于Handler中的方法参数<br>
 * @author zhaoyali
 * 
 * param ：HTTP请求的参数名称<br>
 * rule     ：校验规则，支持如下常见的、已经预定义好的校验规则<br>
 *                   1、required  必须有值<br>
 *                   2、min(N)     最小值为N（支持整数、小数）<br>
 *                   3、max(N)    最大值为N（支持整数、小数）<br>
 *                   4、mobile     手机号码<br>
 *                   5、idcard      身份证号码<br>
 *                   6、email       电子邮箱地址<br>
 *                   7、RegExp(N)  正则表达式（可自定义正则式N）<br>
 *                   
 *  多项规则： 多个规则之间用"|"分隔开来。<br>
 *  关于扩展： 如需增加更多的校验规则，只需继承并扩展com.zhuanche.common.web.HttpParamVerifyValidator类即可。 <br>
 *  示          例：<br>
 *                   @Verify(param="money",rule="required|min(100)|max(300)")  <br>
 *                   @Verify(param="phone",rule="mobile")  <br>
 *                   @Verify(param="mailAddr",rule="required|email")  <br>
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Verify {
	public String param();
	public String rule();
}