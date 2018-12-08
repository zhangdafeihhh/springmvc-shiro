package com.zhuanche.controller.busManage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.entity.busManage.MaidListEntity;
import com.zhuanche.util.dateUtil.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: mp-manage
 * @description: 导出巴士相关信息的共有方法
 * @author: niuzilian
 * @create: 2018-12-08 15:04
 **/
public class BusCSVDataUtil {

    /**
     * 构建文件成防止乱码
     * @param request
     * @param name
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String buidFileName (HttpServletRequest request,String name) throws UnsupportedEncodingException{
        String fileName = name + com.zhuanche.util.dateUtil.DateUtil.dateFormat(new Date(), DateUtil.intTimestampPattern) + ".csv";
        //获得浏览器信息并转换为大写
        String agent = request.getHeader("User-Agent").toUpperCase();
        //IE浏览器和Edge浏览器
        if (agent.indexOf("MSIE") > 0 || (agent.indexOf("GECKO") > 0 && agent.indexOf("RV:11") > 0)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {  //其他浏览器
            fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
        }
        return fileName;
    }
    /**
    * 构建csv导出的数据，传入的type必须实现符合业务导出格式的toString方法
    * @Param: [array, type]
    * @return: java.util.List<java.lang.String>
    * @Date: 2018/12/8
    */
    public static List<String> buidCsvData(JSONArray array,Class<MaidListEntity> type){
        List<String> collect = array.stream().map(o -> (JSONObject) o).map(o -> JSONObject.toJavaObject(o, type)).map(o ->o.toString()).collect(Collectors.toList());
        return null;
    }
}
