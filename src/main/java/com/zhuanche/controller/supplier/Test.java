package com.zhuanche.controller.supplier;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Author fanht
 * @Description
 * @Date 2020/2/18 上午11:43
 * @Version 1.0
 */
public class Test {

    public static void main(String[] args) {
        Map<Integer,String> map = Maps.newHashMap();
        map.put(1,"是的");
        map.put(2,"不死");
        System.out.println(JSONArray.toJSON(map));
    }
}
