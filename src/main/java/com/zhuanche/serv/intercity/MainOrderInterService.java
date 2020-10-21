package com.zhuanche.serv.intercity;

import com.zhuanche.entity.mdbcarmanage.MainOrderInterCity;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2019/10/24 下午6:50
 * @Version 1.0
 */
public interface MainOrderInterService {


    int updateMainTime(String mainOrderNo, String mainTime);

    int addMainOrderNo(MainOrderInterCity record);

    MainOrderInterCity queryMainOrder(String mainOrderNo);

    int updateMainOrderState(String mainOrderNo, Integer status, String phone);

    List<MainOrderInterCity> phoneQueryByDriverId(Integer driverId);
}
