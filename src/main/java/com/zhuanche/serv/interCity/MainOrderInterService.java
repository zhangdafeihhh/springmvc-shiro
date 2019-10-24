package com.zhuanche.serv.interCity;

import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/24 下午6:50
 * @Version 1.0
 */
public interface MainOrderInterService {


    int updateMainTime(String mainOrderNo, String mainTime);

    int addMainOrderNo(MainOrderInterCity record);
}
