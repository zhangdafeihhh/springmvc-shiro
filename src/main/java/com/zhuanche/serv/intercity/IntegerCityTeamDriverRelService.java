package com.zhuanche.serv.intercity;

import com.zhuanche.common.web.AjaxResponse;

import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/7/13 下午5:09
 * @Version 1.0
 */
public interface IntegerCityTeamDriverRelService {

    AjaxResponse addDriver(String driverIds, Integer teamId);

    int del(Integer driverId,Integer teamId);


}
