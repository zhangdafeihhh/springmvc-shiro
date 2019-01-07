package com.zhuanche.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

/** JAVA对象属性拷贝工具类 **/
public final class BeanUtil {
	private static Log log = LogFactory.getLog(BeanUtil.class);
	/** 拷贝对象 **/
	public static <T> T copyObject(Object src, Class<T> destClass) {
		if (src == null) {
			return null;
		}
		try {
			T destObj = destClass.newInstance();
			BeanUtils.copyProperties(src, destObj);
			return destObj;
		} catch (Exception e) {
			log.error("convertObject ERROR", e);
			return null;
		}
	}

	/** 拷贝列表List **/
	@SuppressWarnings("rawtypes")
	public static <T> List<T> copyList(List srcList, Class<T> destClass) {
		if (srcList == null) {
			return null;
		}
		try {
			List<T> destList = new ArrayList<T>(srcList.size());
			for (int i = 0; i < srcList.size(); i++) {
				Object srcObj = srcList.get(i);
				T destObj = destClass.newInstance();
				BeanUtils.copyProperties(srcObj, destObj);
				destList.add(destObj);
			}
			return destList;
		} catch (Exception e) {
			log.error("copyList ERROR", e);
			return null;
		}
	}

	/** 拷贝集合Set **/
	@SuppressWarnings("rawtypes")
	public static <T> Set<T> copySet(Set srcSet, Class<T> destClass) {
		if (srcSet == null) {
			return null;
		}
		try {
			HashSet<T> destSet = new HashSet<T>((srcSet.size() + 1) * 2);
			Iterator iterator = srcSet.iterator();
			while (iterator.hasNext()) {
				Object srcObj = iterator.next();
				T destObj = destClass.newInstance();
				BeanUtils.copyProperties(srcObj, destObj);
				destSet.add(destObj);
			}
			return destSet;
		} catch (Exception e) {
			log.error("copySet ERROR", e);
			return null;
		}
	}

	/** Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean */
	public static void transMap2Bean(Map<String, Object> map, Object obj) {
		if (map == null || obj == null) {
			log.info("map or obj can not be null");
			return;
		}

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (map.containsKey(key)) {
					Object value = map.get(key);
					// 得到property对应的setter方法
					Method setter = property.getWriteMethod();
					setter.invoke(obj, value);
				}
			}
		} catch (Exception e) {
			System.out.println("transMap2Bean Error " + e);
		}
	}

	/** Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map */
	public static void transBean2Map(Object obj, Map<String, Object> map) {
		if (map == null || obj == null) {
			log.info("obj or map can not be null");
			return;
		}

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			System.out.println("transBean2Map Error " + e);
		}
	}

}