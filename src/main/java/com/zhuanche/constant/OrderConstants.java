package com.zhuanche.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2020/4/15 下午12:30
 * @Version 1.0
 */
public class OrderConstants {


    public static final Map<String,Integer> getOrderMap(){
        Map<String,Integer> map = Maps.newHashMap();
        map.put("2018-12",12526978);
        map.put("2019-01",14840437);
        map.put("2019-02",17253826);
        map.put("2019-03",19022780);
        map.put("2019-04",21013408);
        map.put("2019-05",22486648);
        map.put("2019-06",23849050);
        map.put("2019-07",25481608);
        map.put("2019-08",27003505);
        map.put("2019-09",28390262);
        map.put("2019-10",29683866);
        map.put("2019-11",30864565);
        map.put("2019-12",31994124);
        map.put("2020-01",33037913);
        map.put("2020-02",33798684);
        map.put("2020-03",33934486);
        map.put("2020-04",34151247);
        return map;
    }
}
