package com.zhuanche.util;

public class PageUtils {

    public static int getTotalPage(int totalCount,int pageSize) {
        int p = totalCount / pageSize;
        if (totalCount % pageSize == 0)
            return p;
        else
            return p + 1;
    }
}
