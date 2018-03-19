package com.zhuanche.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhouchao on 2017/4/6.
 */
public class StringUtil {
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);
    /**
     * 首字母变成大写
     * @param str
     * @return
     */
     public static String getFirstUpperCharByStr(String str){
         return str.substring(0, 1).toUpperCase() + str.substring(1);
     }

    /**
     * 隐藏部分字符串
     * @param value
     * @param start
     * @param length
     * @return
     */
     public static String  hidePartStr(String value, int start, int length){
         return value.replaceAll("(\\d{"+(start-1)+"})\\d{"+length+"}(\\d{"+(value.length() - start - length)+"})","$1****$2");
     }

     public static String convertMessyCode(String value){
         if(value!=null){
             if(!(java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value))){
                 try {
                     value = new String(value.getBytes("ISO-8859-1"),"UTF-8");
                 } catch (UnsupportedEncodingException e) {
                     logger.error("编码转换出错误："+e.getMessage(), e);
                 }
             }
         }
         return value;
     }

     public static String[]  batchConvertMessyCode(String[] values){
        if(values == null || values.length < 1){
             return values;
        }
        for (int i = 0 ; i < values.length ; i++){
            values[i] = convertMessyCode(values[i]);
        }
         return values;
     }

}
