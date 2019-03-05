package com.zhuanche.util;

import com.alibaba.fastjson.JSON;
import com.zhuanche.mongo.MenuOperationDTO;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToCSV {

    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\admin\\Desktop\\menu.json");
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String readStr;
        while ((readStr = reader.readLine()) != null) {
            builder.append(readStr);
        }
        Map<String, String> heads = new HashMap<>();
        heads.put("count", "操作次数");
        heads.put("menuName", "操作");
        heads.put("userCount", "操作人数");
        File outPutFile = new File("C:\\Users\\admin\\Desktop\\menu.csv");
        jsonArrayToCsv(builder.toString(), MenuOperationDTO.class, outPutFile, heads);
    }


    /**
     * @heads key 是json对象字段名 value是csv对应表头
     */
    public static <T> void jsonArrayToCsv(String json, Class<T> dataType, File outPutFile, Map<String, String> heads) throws IOException {
        List<T> dataList = JSON.parseArray(json, dataType);
        CSVUtil.write(new FileOutputStream(outPutFile), heads, dataType, dataList);
    }
}
