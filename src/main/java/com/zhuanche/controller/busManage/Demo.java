package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: mp-manage
 * @description:
 * @author: niuzilian
 * @create: 2018-12-08 15:21
 **/
public class Demo {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static void main(String[] args) {
       Demo demo = new Demo();
       demo.setId(1);
       demo.setName("niuzilian");
        String s = JSON.toJSONString(demo);
        zhuabbian(s, Demo.class);

    }

    public static String  zhuabbian(String demo,Class type){
        Object o = JSONObject.parseObject(demo, type);
        System.out.println(o.toString());
        return null;
    }

}
