package com.zhuanche.controller.intercity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Author fanht
 * @Description
 * @Date 2020/5/26 上午10:48
 * @Version 1.0
 */
public class Test {

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","haha");
        jsonObject.put("name","dada");
        System.out.println(JSONObject.toJSONString(jsonObject));
    }
}
