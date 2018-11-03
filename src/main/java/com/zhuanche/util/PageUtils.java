package com.zhuanche.util;

public class PageUtils {

    public static int getTotalPage(long totalCount,int pageSize) {
        long p = totalCount / pageSize;
        if (totalCount % pageSize == 0)
            return Integer.parseInt(p+"");
        else
            return Integer.parseInt(p+"") + 1;
    }
}
