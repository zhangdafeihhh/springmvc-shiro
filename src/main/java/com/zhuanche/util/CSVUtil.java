package com.zhuanche.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 单车 on 18/11/1.
 */
public class CSVUtil {

    private static Logger logger = LoggerFactory.getLogger(CSVUtil.class);

    /**
     * 往表格中插入记录
     * @param fields       表头
     * @param clz          导出实体字节码
     * @param data         导出数据集合
     *
     */
    public static void write(FileOutputStream outputStream, Map<String,String> fields, Class<?> clz, List data) {
        if(fields == null || clz == null) {
            logger.info("导出数据时入参为空");
            return;
        }
        try {
            // 数据源model所有字段map
            Map<String, Field> fieldMap = new HashMap<>();
            getFieldMap(clz,fieldMap);
            //写表头，生成指定名字的文件，返回客户端
            StringBuilder hb = new StringBuilder();
            for(Map.Entry<String, String> e : fields.entrySet())
                hb.append(e.getValue()).append(",");
            outputStream.write(hb.substring(0, hb.length() - 1).getBytes("GBK"));
            outputStream.flush();
            //遍历处理数据导出
            for(Object o : data){
                StringBuilder sb = new StringBuilder();
                sb.append("\n");
                for(String field : fields.keySet()){
                    Field f = fieldMap.get(field);
                    f.setAccessible(true);
                    Object value = f.get(o);
                    if(value == null || StringUtils.isBlank(value.toString())){
                        sb.append(",");
                    } else if (f.getType() == Date.class) {
                        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)).append(",");
                    } else if (f.getType() == DateTime.class) {
                        sb.append(((DateTime) value).toString("yyyy-MM-dd HH:mm:ss")).append(",");
                    } else {
                        String tmp = value.toString();
                        if(tmp.contains(",")) {
                            tmp = tmp.replace(",", "\",\"");
                        }
                        sb.append(tmp).append(",");
                    }
                }
                outputStream.write(sb.substring(0, sb.length() - 1).getBytes("GBK"));
                outputStream.flush();
            }
        }catch (Exception e) {
            logger.error("导出csv数据时异常：" , e);
        }finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("导出csv数据时关流异常：", e);
                }
            }
        }
    }

    private static <T> void getFieldMap(Class<T> clz, Map<String, Field> result) {
        for (Field field : clz.getDeclaredFields()) {
            result.put(field.getName(), field);
        }
        if (clz.getSuperclass() != null) {
            getFieldMap(clz.getSuperclass(), result);
        }
    }
}
