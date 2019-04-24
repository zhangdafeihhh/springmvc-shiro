package com.zhuanche.common.syslog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**  
 * ClassName:Column <br/>  
 * Date:     2019年4月19日 上午11:41:53 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@Retention(RetentionPolicy.RUNTIME) 
public @interface Column{
	  String desc();
}

