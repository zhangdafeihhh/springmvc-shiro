package com.zhuanche.dto.financial;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CarColorList implements Serializable {

    private static final long serialVersionUID = -1190702358581612263L;

    private static List<JSONObject> colorList = new ArrayList<>();

    static {
        JSONObject c1 = new JSONObject();
        c1.put("name","白色");

        JSONObject c2 = new JSONObject();
        c2.put("name","黑色");

        JSONObject c3 = new JSONObject();
        c3.put("name","银色");

        JSONObject c4 = new JSONObject();
        c4.put("name","金色");

        JSONObject c5 = new JSONObject();
        c5.put("name","灰色");

        JSONObject c6 = new JSONObject();
        c6.put("name","蓝色");

        JSONObject c7 = new JSONObject();
        c7.put("name","棕色");


        JSONObject c8 = new JSONObject();
        c8.put("name","红色");


        JSONObject c9 = new JSONObject();
        c9.put("name","紫色");

        JSONObject c10 = new JSONObject();
        c10.put("name","绿色");

        JSONObject c11 = new JSONObject();
        c11.put("name","粉色");

        JSONObject c12 = new JSONObject();
        c12.put("name","黄色");

        colorList.add(c1);
        colorList.add(c2);
        colorList.add(c3);
        colorList.add(c4);
        colorList.add(c5);
        colorList.add(c6);
        colorList.add(c7);
        colorList.add(c8);
        colorList.add(c9);
        colorList.add(c10);
        colorList.add(c11);
        colorList.add(c12);
    }

    public static List<JSONObject> getColorList() {
        return colorList;
    }


}
