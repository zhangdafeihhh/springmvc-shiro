package com.zhuanche.common.util;

import com.zhuanche.constant.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author fanht
 * @Description
 * @Date 2020/6/3 下午2:57
 * @Version 1.0
 */
public class TransportUtils {


    /**
     * 把字符串已指定符号分隔并转化为List
     * @param str
     * @return
     */
    public static List<String> strToList(String str,String split){

        if(StringUtils.isEmpty(str)){
            return new ArrayList<>();
        }
        String[] arrStr = str.split(split);

        List<String> listStr = new ArrayList<>();

        if(arrStr != null && arrStr.length > 0){
            for(int i = 0;i<arrStr.length;i++){
                listStr.add(arrStr[i]);
            }
        }
        return listStr;
    }

}
