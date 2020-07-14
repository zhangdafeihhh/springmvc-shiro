package com.zhuanche.util.collectionutil;


import com.alibaba.fastjson.JSONObject;
import com.zhuanche.constant.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Author fanht
 * @Description 常用的集合转换util
 * @Date 2020/5/28 下午4:53
 * @Version 1.0
 */
public class TransportUtils {

    /**
     * 获取数组里面的最小值
     * @param strArr
     * @return
     */
    public static Double getDoubleMinValue(String[] strArr){
        List<Double>  doubleList = new ArrayList<>();

        for(String str : strArr){
            doubleList.add(Double.valueOf(str.trim()));
        }

        Double[] doubleArr = new  Double[doubleList.size()];

        doubleArr = doubleList.toArray(doubleArr);

        Double minPrice = Collections.min(Arrays.asList(doubleArr));

        return minPrice;

    }

    /**
     * 获取数组里面的最大值
     * @param strArr
     * @return
     */
    public static Double getDoubleMaxValue(String[] strArr){
        List<Double>  doubleList = new ArrayList<>();

        for(String str : strArr){
            doubleList.add(Double.valueOf(str.trim()));
        }

        Double[] doubleArr = new  Double[doubleList.size()];

        doubleArr = doubleList.toArray(doubleArr);

        Double maxPrice = Collections.max(Arrays.asList(doubleArr));

        return maxPrice;

    }


    /**
     * 获取数组里面的最小值
     * @param strArr
     * @return
     */
    public static Integer getMinIntegerValu(String[] strArr){
        List<Integer>  integerList = new ArrayList<>();

        for(String str : strArr){
            integerList.add(Integer.valueOf(str.trim()));
        }

        Integer[] integerArr = new  Integer[integerList.size()];

        integerArr = integerList.toArray(integerArr);

        Integer minValue = Collections.min(Arrays.asList(integerArr));

        return minValue;

    }


    /**
     * 获取数组里面的最大值
     * @param strArr
     * @return
     */
    public static Integer getMaxIntegerValu(String[] strArr){
        List<Integer>  integerList = new ArrayList<>();

        for(String str : strArr){
            integerList.add(Integer.valueOf(str.trim()));
        }

        Integer[] integerArr = new  Integer[integerList.size()];

        integerArr = integerList.toArray(integerArr);

        Integer minValue = Collections.max(Arrays.asList(integerArr));

        return minValue;

    }


    public static List<Integer> listInteger(String params){
     String[] strArr =   params.split(Constants.SEPERATER);
     List<Integer> listInt = new ArrayList<>();
     for(int i = 0;i<strArr.length;i++){
         listInt.add(Integer.valueOf(strArr[i]));
     }
      return listInt;
    }

    public static void main(String[] args) {

        String[] strArr = new String[]{"34","244","4","22"};
        System.out.println(getDoubleMinValue(strArr));
        System.out.println(getDoubleMaxValue(strArr));
        System.out.println(getMinIntegerValu(strArr));
        System.out.println(getMaxIntegerValu(strArr));
        System.out.println(JSONObject.toJSONString(listInteger("55,333,54554,2223,2233")));
    }
}
