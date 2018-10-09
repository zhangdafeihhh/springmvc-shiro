package com.zhuanche.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.JSONUtils;
import net.sf.json.util.PropertyFilter;

public class JsonUtil {
    /**
     * 转成jsonOjbect对象
     * @param obj
     * @return
     */
    public static JSONObject toJsonOjbect(Object obj){
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);//可防止hibernate模式下的关联关系子对象中包含父对象造成死循环
        JSONObject jsonObject = JSONObject.fromObject(obj,jsonConfig);
        return jsonObject;
    }
    
    /**
     * 转成jsonArray对象
     * @param obj
     * @return
     */
    public static JSONArray toJsonArray(Object obj){
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);//可防止hibernate模式下的关联关系子对象中包含父对象造成死循环
        JSONArray jsonArray = JSONArray.fromObject(obj,jsonConfig);
        return jsonArray;
    }
    
    /**
     * json串或jsonobject对象转成Map
     * @param obj
     * @return
     */
    public static Map<?, ?> jsonToMap(Object obj){
        JSONObject jsonObject = null;
        if(obj instanceof JSONObject){
            jsonObject = (JSONObject)obj;
        }else{
            jsonObject = JSONObject.fromObject(obj);
        }

        Map<Object, Object> map = new HashMap<Object, Object>(jsonObject.size());
        Iterator<?> it = jsonObject.keys();
        while(it.hasNext()){
            Object key = it.next();
            Object value = jsonObject.get(key);
            if(value instanceof JSONObject){
                map.put(key, jsonToMap(value));
            }else if(value instanceof JSONArray){
                map.put(key, jsonArrToList(value));
            }else{
                map.put(key, value);
            }
        }
        
        return map;
    }
    
    /**
     * list串或jsonArray对象转成list
     * @param obj
     * @return
     */
    public static List<?> jsonArrToList(Object obj){
        
        JSONArray jsonArray = null;
        if(obj instanceof JSONArray){
            jsonArray = (JSONArray)obj;
        }else{
            jsonArray = JSONArray.fromObject(obj);
        }
        
        List list = new ArrayList(jsonArray.size());
        Iterator<?> it = jsonArray.iterator();
        while(it.hasNext()){
            Object next = it.next();
            if(next instanceof JSONObject){
                list.add(jsonToMap(next));
            }else if(next instanceof JSONArray){
                list.add(jsonArrToList(next));
            }else{
                list.add(next);
            }
        }
        
        return list;
    }
    
    /**
     * json串转java对象
     * @param jsonStr json串
     * @param rootClass 要转成的主对象
     * @param subClassMap 主对象中包含的list类型的属性Map 格式：subClassMap.put("iordersegments", IorderSegment.class); key是子对象在主对象中的属性名， value是子对象类型
     * @return
     */
    public static <T>T jsonToBean(String jsonStr,Class<T> rootClass,Map<String, Class> subClassMap){
        JsonConfig filterNullConfig = new JsonConfig();
        //过滤掉参数值为null的参数，防止后边的时间转换出错
        filterNullConfig.setJsonPropertyFilter(new PropertyFilter() {
            @Override
            public boolean apply(Object clazz, String name, Object value) {
                boolean isFilter = false;
                if(value==null||"".equals(value)){
                    isFilter = true;
                }
                return isFilter;
            }
        });
        
        JSONObject jsonObject = JSONObject.fromObject(jsonStr,filterNullConfig);
        
        String[] dateFormats = new String[] {"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd","yyyy-MM-dd HH:mm"};//不过好像只有   yyyy-MM-dd HH:mm:ss 格式有效
        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(dateFormats));
        
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setRootClass(rootClass);
        if(subClassMap!=null&&subClassMap.size()>0){
            jsonConfig.setClassMap(subClassMap);
        }

        return (T)JSONSerializer.toJava(jsonObject, jsonConfig);
    }
    
}