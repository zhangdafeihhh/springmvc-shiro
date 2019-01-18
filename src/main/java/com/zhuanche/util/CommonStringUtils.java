package com.zhuanche.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cos on 2019/1/18.
 */
public class CommonStringUtils {
    private static Logger logger = LoggerFactory.getLogger(CommonStringUtils.class);

     public static String protectPhoneInfo(String phone){
         StringBuilder builder = new StringBuilder(phone);
         builder.replace(phone.length() - 4,phone.length(),"****");
         return builder.toString();
     }

}
