package com.zhuanche.util;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.zhuanche.dto.rentcar.CarFactOrderInfoDetailDTO;
import com.zhuanche.entity.rentcar.CarFactOrderInfo;

/**
 * 实体类字段值相同的复制
 *
 * @author wangshuai01@e6yun.com 2017年8月18日
 */
public class CopyBeanUtil {
    static Logger log = LoggerFactory.getLogger(CopyBeanUtil.class);

    /**
     * 复制sour里属性不为空的值到obje为空的属性
     *
     * @param obje    目标实体类
     * @param sour    源实体类
     * @param isCover 是否保留obje类里不为null的属性值(true为保留源值，属性为null则赋值)
     * @return obje
     */
    public static Object Copy(Object obje, Object sour, boolean isCover) {
        Field[] fields = sour.getClass().getDeclaredFields();
        for (int i = 0, j = fields.length; i < j; i++) {
            String propertyName = fields[i].getName();
            Object propertyValue = getProperty(sour, propertyName);
            if (isCover) {
                if (getProperty(obje, propertyName) == null && propertyValue != null) {
                    Object setProperty = setProperty(obje, propertyName, propertyValue);
                }
            } else {
                Object setProperty = setProperty(obje, propertyName, propertyValue);
            }

        }
        return obje;
    }

    /**
     * 复制sour里属性不为空的值到obj里并相加
     *
     * @param obj     目标实体类
     * @param sour    源实体类
     * @param isCover
     * @return obj
     */
    public static Object CopyAndAdd(Object obj, Object sour, boolean isCover) {
        Field[] fields = sour.getClass().getDeclaredFields();
        for (int i = 0, j = fields.length; i < j; i++) {
            String propertyName = fields[i].getName();
            Object sourPropertyValue = getProperty(sour, propertyName);
            Object objPropertyValue = getProperty(obj, propertyName);
            if (isCover) {
                if (objPropertyValue == null && sourPropertyValue != null) {
                    Object setProperty = setProperty(obj, propertyName, sourPropertyValue);
                } else if (objPropertyValue != null && sourPropertyValue == null) {
                    Object setProperty = setProperty(obj, propertyName, objPropertyValue);
                } else if (objPropertyValue != null && sourPropertyValue != null) {
                    Object setProperty = setProperty(obj, propertyName, ((int) sourPropertyValue) + (int) objPropertyValue);
                }
            }

        }
        return obj;
    }


    /**
     * 得到值
     *
     * @param bean
     * @param propertyName
     * @return
     */
    private static Object getProperty(Object bean, String propertyName) {
        Class clazz = bean.getClass();
        try {
            Field field = clazz.getDeclaredField(propertyName);
            Method method = clazz.getDeclaredMethod(getGetterName(field.getName(),field.getType()), new Class[]{});
            return method.invoke(bean, new Object[]{});
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 给bean赋值
     *
     * @param bean
     * @param propertyName
     * @param value
     * @return
     */
    private static Object setProperty(Object bean, String propertyName, Object value) {
        Class clazz = bean.getClass();
        try {
            Field field = clazz.getDeclaredField(propertyName);
            Method method = clazz.getDeclaredMethod(getSetterName(field.getName()), new Class[]{field.getType()});
            return method.invoke(bean, new Object[]{value});
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 根据变量名得到get方法
     *
     * @param propertyName
     * @return
     */
    private static String getGetterName(String propertyName) {
        String method ;
        if( propertyName.length()>1&& Character.isUpperCase(propertyName.charAt(1))){
             method = "get" +propertyName;
        }else{
            method = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }
        return method;
    }

    /**
     * 根据变量名和类型获取getter方法
     * @param propertyName
     * @param type
     * @return
     */
    private static String getGetterName(String propertyName, Class<?> type) {
        String method ;
        if(type==Boolean.class|| type==boolean.class){
            if("is".equalsIgnoreCase(propertyName.substring(0, 2))){
                return propertyName;
            }else{
                return "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            }

        }
        if( propertyName.length()>1&& Character.isUpperCase(propertyName.charAt(1))){
            method = "get" +propertyName;
        }else{
            method = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }
        return method;
    }

    /**
     * 得到setter方法
     *
     * @param propertyName 变量名
     * @return
     */
    private static String getSetterName(String propertyName) {
//        String method = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String method ;
        if( propertyName.length()>1&& Character.isUpperCase(propertyName.charAt(1))){
            method = "set" +propertyName;
        }else{
            method = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }
        return method;
    }


    /**
     * 父类集合转成子类集合集合通用方法(子类集合接收父类集合)
     *
     * @param list 父类集合
     * @param <T>  子类
     * @param <E>  父类
     * @return
     */
    public static <T, E> List<T> chang2ChildClassList(List<E> list) {
        List<T> alist = new ArrayList<>();
        for (E o : list) {
            alist.add((T) o);
        }
        return alist;

    }

    /**
     * 属性copy  复制sour里属性和obje里属性值忽略大小写相同的 ，不为空的值赋值到obje里
     * 如果存在属性复杂类型并为有效值慎用或改进
     *
     * @param obje
     * @param sour
     * @param isCover 是否保留obje里面属性值不为空的字段值
     * @return
     */
    public static Object copyByIgnoreCase(Object obje, Object sour, boolean isCover) {

        try {
            Field[] objFields = obje.getClass().getDeclaredFields();

            Field[] sourFields = sour.getClass().getDeclaredFields();
            for (int i = 0; i < sourFields.length; i++) {
                String sourPropertyName = sourFields[i].getName();
                //获取来源对象的属性值
                Object propertyValue = getSourPropertyValue(sour, sourPropertyName);
                for (int j = 0; j < objFields.length; j++) {

                    try {
                        String objPropertyName = objFields[j].getName();
                        if (objPropertyName.equalsIgnoreCase(sourPropertyName)) {
                            if (isCover) {
                                if (getProperty(obje, objPropertyName) == null && propertyValue != null) {
                                    setObjProperty(obje, objPropertyName, propertyValue);
                                }
                            } else {
                                setObjProperty(obje, objPropertyName, propertyValue);
                            }
                            break;
                        }
                    } catch (Exception e) {
                        log.error("给目标bean赋值出错,objPropertyName:{},value:{}",sourPropertyName,propertyValue,e);
                        e.printStackTrace();
                    }
                }

            }
        } catch (SecurityException e) {
            e.printStackTrace();
            log.error("给目标bean赋值出错,obje:{},sour:{}", JSON.toJSONString(obje), JSON.toJSONString(sour),e);
        }
        return obje;
    }

    /**
     * 根据属性名获取的值
     *
     * @param sourceBean
     * @param sourcePropertyName
     * @return
     */
    private static Object getSourPropertyValue(Object sourceBean, String sourcePropertyName) {
        Class clazz = sourceBean.getClass();
        try {
            Field field = clazz.getDeclaredField(sourcePropertyName);
            Method method = clazz.getDeclaredMethod(getGetterName(field.getName(),field.getType()), new Class[]{});
            return method.invoke(sourceBean, new Object[]{});
        } catch (Exception e) {
            log.error("获取属性名（不区分大小写）相似的值赋值出差", e);
        }
        return null;
    }



    /**
     * 给目标bean赋值
     *
     * @param objBean
     * @param sourcePropertyName
     * @param value
     * @return
     */
    private static Object setObjPropertyBySourceProperty(Object objBean, String sourcePropertyName, Object value) {
        Class clazz = objBean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (int i = 0, j = fields.length; i < j; i++) {
                String propertyName = fields[i].getName();
                if (sourcePropertyName.equalsIgnoreCase(propertyName)) {
                    Field field = clazz.getDeclaredField(propertyName);
                    if (field.getType() == BigDecimal.class) {
                        if (value instanceof String) {
                            value = new BigDecimal(String.valueOf(value));
                        } else if (value instanceof Integer || value instanceof Double) {
//                          传double直接new BigDecimal，数会变大
                            value = BigDecimal.valueOf(Double.parseDouble(String.valueOf(value)));
                        }
                    }
                    if (field.getType() == Double.class || field.getType() == double.class) {
                        if (value instanceof BigDecimal) {
                            DecimalFormat df = new DecimalFormat("#.000000");
                            Double v = Double.parseDouble(String.valueOf(value));
                            value = df.format(v);
                        }
                    }

                    Method method = clazz.getDeclaredMethod(getSetterName(field.getName()), new Class[]{field.getType()});
                    return method.invoke(objBean, new Object[]{value});
                }
            }

        } catch (Exception e) {
        }
        return null;
    }


    /**
     * 给目标bean赋值
     *
     * @param objBean
     * @param propertyName
     * @param value
     * @return
     */
    private static Object setObjProperty(Object objBean, String propertyName, Object value) {
        Class clazz = objBean.getClass();
        try {
            Field field = clazz.getDeclaredField(propertyName);
            if (field.getType() == BigDecimal.class) {
                if (value instanceof String) {
                    value = new BigDecimal(String.valueOf(value));
                } else if (value instanceof Integer || value instanceof Double) {
//                          传double直接new BigDecimal，数会变大
                    value = BigDecimal.valueOf(Double.parseDouble(String.valueOf(value)));
                }
            }
            if (field.getType() == Double.class || field.getType() == double.class) {
                if (value instanceof BigDecimal) {
                    DecimalFormat df = new DecimalFormat("#.000000");
                    Double v = Double.parseDouble(String.valueOf(value));
                    value =new BigDecimal(df.format(v));
                }
            }
            if (field.getType() == Integer.class || field.getType() == int.class) {
                if (value instanceof Float) {
                     value = Math.round(Float.parseFloat(String.valueOf(value)));
                }
            }
            Method method = clazz.getDeclaredMethod(getSetterName(field.getName()), new Class[]{field.getType()});
            log.info("给目标bean赋值,propertyName:{},value:{}",propertyName,value);
            Object obj = method.invoke(objBean, new Object[]{value});
            return obj;

        } catch (Exception e) {
            log.error("给目标bean赋值出错,propertyName:{},value:{}",propertyName,value,e);
        }
        return null;
    }
/*    public static void main(String[] args) {
    	CarFactOrderInfoDetailDTO orderDTO = new CarFactOrderInfoDetailDTO();
    	CarFactOrderInfo ci = new CarFactOrderInfo();
    	ci.setCreatedate("2019-09-10");
    	ci.setAmount(132.1);
    	copyByIgnoreCase(orderDTO,ci,true);
    	System.out.println(orderDTO.getCreatedate());
    	System.out.println(orderDTO.getAmount());
    }*/

}
