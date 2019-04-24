package com.zhuanche.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.syslog.Column;
import com.zhuanche.entity.driver.SysLog;

/**  
 * ClassName:CompareObjUtil <br/>  
 * Date:     2019年4月19日 上午10:48:37 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
public class CompareObjUtil {
	
	
	
    public static List<Comparison> compareObj(Object beforeObj,Object afterObj) throws Exception{
        List<Comparison> differents = new ArrayList<Comparison>();
        
        if(beforeObj == null) throw new Exception("原对象不能为空");
        if(afterObj == null) throw new Exception("新对象不能为空");
        if(!beforeObj.getClass().isAssignableFrom(afterObj.getClass())){
            throw new Exception("两个对象不相同，无法比较");
        }
        
        //取出属性
        Field[] beforeFields = beforeObj.getClass().getDeclaredFields();
        Field[] afterFields = afterObj.getClass().getDeclaredFields();
        Field.setAccessible(beforeFields, true); 
        Field.setAccessible(afterFields, true);
        
        //遍历取出差异值
        if(beforeFields != null && beforeFields.length > 0){
            for(int i=0; i<beforeFields.length; i++){
            	Column column=beforeFields[i].getAnnotation(Column.class);
            	if (column!=null) {
					System.out.println(column.desc());
				}
                Object beforeValue = beforeFields[i].get(beforeObj);
                Object afterValue = afterFields[i].get(afterObj);
                    if((beforeValue != null && !"".equals(beforeValue) && !beforeValue.equals(afterValue)) || ((beforeValue == null || "".equals(beforeValue)) && afterValue != null)){
                        Comparison comparison = new Comparison();
                        comparison.setField(beforeFields[i].getName());
                        comparison.setBefore(beforeValue);
                        comparison.setAfter(afterValue);
                        differents.add(comparison);
                    }
            }
        }
        
        return differents;
    }
    
    
    private static void main(String[] args) {
    	try {
    		String str=getRemarks(SysLog.class, "xxxx", "{\"method\":\"修改1\",\"module\":\"系统日志\",\"sysLogId\":1,\"username\":\"admin1\"}");
    		System.out.println(str);
    	} catch (Exception e) {
			e.printStackTrace();  
		}
    	
	}
    public static String getRemarks(Class clazz,String afterStr){
        if(afterStr == null){
        	return "";
        }
        StringBuilder stringbuilder=new StringBuilder();
   	    try {
			Object afterObj=JSON.parseObject(afterStr, clazz);
			//取出属性
			Field[] afterFields = afterObj.getClass().getDeclaredFields();
			Field.setAccessible(afterFields, true);
			
      
			//遍历取出差异值
			if(afterFields != null && afterFields.length > 0){
			    for(int i=0; i<afterFields.length; i++){
			    	 Column column=afterFields[i].getAnnotation(Column.class);
			        Object afterValue = afterFields[i].get(afterObj);
			            if(afterValue != null && !"".equals(afterValue)){
			             	if (column!=null) {
								stringbuilder.append(column.desc()+":");
							}
			           	     stringbuilder.append(afterValue);
			           	     stringbuilder.append(";");
			            }
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();  
		} 
		return stringbuilder.toString();
   }
    
    public static String getRemarks(Class clazz,String beforeStr,String afterStr){
    	 if (StringUtils.isBlank(beforeStr) && StringUtils.isNotBlank(afterStr)) {
    		return getRemarks(clazz, afterStr);
 		 }
    	
    	 if (StringUtils.isBlank(beforeStr)||StringUtils.isBlank(afterStr)) {
    	    return "";
		 }
    	 StringBuilder stringbuilder=new StringBuilder();
         
    	 try {
			Object beforeObj=JSON.parseObject(beforeStr, clazz);
			 Object afterObj=JSON.parseObject(afterStr, clazz);
			
			 if(!beforeObj.getClass().isAssignableFrom(afterObj.getClass())){
			     throw new Exception("两个对象不相同，无法比较");
			 }
			 //取出属性
			 Field[] beforeFields = beforeObj.getClass().getDeclaredFields();
			 Field[] afterFields = afterObj.getClass().getDeclaredFields();
			 Field.setAccessible(beforeFields, true); 
			 Field.setAccessible(afterFields, true);
      
			 //遍历取出差异值
			 if(afterFields != null && afterFields.length > 0){
			     for(int i=0; i<afterFields.length; i++){
			     	 Column column=afterFields[i].getAnnotation(Column.class);
			         Object beforeValue = beforeFields[i].get(beforeObj);
			         Object afterValue = afterFields[i].get(afterObj);
			         
			             if((afterValue != null && !"".equals(afterValue) && !afterValue.equals(beforeValue)) || ((afterValue == null || "".equals(afterValue)) && beforeValue != null)){
			              	if (column!=null) {
			 					stringbuilder.append(column.desc()+":");
			 				}
			            	 beforeFields[i].getName();
			            	 stringbuilder.append(beforeValue);
			            	 stringbuilder.append("==>");
			            	 stringbuilder.append(afterValue);
			            	 stringbuilder.append(";");
			             }
			     }
			 }
		} catch (Exception e) {
			e.printStackTrace();
			//return "";
		} 
		return stringbuilder.toString();
    }
    
    
}
  
