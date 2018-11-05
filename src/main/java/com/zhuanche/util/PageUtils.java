package com.zhuanche.util;

import org.apache.commons.lang3.StringUtils;

public class PageUtils {

    public static int getTotalPage(long totalCount,int pageSize) {
        long p = totalCount / pageSize;
        if (totalCount % pageSize == 0)
            return Integer.parseInt(p+"");
        else
            return Integer.parseInt(p+"") + 1;
    }

    public static String replaceComma (String msg){
        if(StringUtils.isEmpty(msg)){
            return  msg;
        }
        return msg.replaceAll(",","，");
    }
}
