package com.zhuanche.common.syslog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import java.util.Map;

import javax.servlet.ServletRequest;

import javax.servlet.ServletResponse;

import org.apache.ibatis.javassist.ClassClassPath;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.NotFoundException;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;

/**
 * 
 * 获取AOP代理的方法的参数名称和参数值工具类

 */

public class LogAopUtil {

	private static String[] types = { "java.lang.Integer", "java.lang.Double",

			"java.lang.Float", "java.lang.Long", "java.lang.Short",

			"java.lang.Byte", "java.lang.Boolean", "java.lang.Char",

			"java.lang.String", "int", "double", "long", "short", "byte",

			"boolean", "char", "float" ,"java.math.BigDecimal" };
	
	public static StringBuffer getNameAndArgs(Class<?> cls, String clazzName, String methodName, Object[] args)

			throws NotFoundException {

		Map<String, Object> nameAndArgs = new HashMap<String, Object>();

		ClassPool pool = ClassPool.getDefault();

		ClassClassPath classPath = new ClassClassPath(cls);

		pool.insertClassPath(classPath);

		CtClass cc = pool.get(clazzName);

		CtMethod cm = cc.getDeclaredMethod(methodName);

		MethodInfo methodInfo = cm.getMethodInfo();

		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

		if (attr == null) {

			// exception

		}

		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

		for (int i = 0; i < cm.getParameterTypes().length; i++) {

			nameAndArgs.put(attr.variableName(i + pos), args[i]);// paramNames即参数名

		}

		// nameAndArgs的两种类型，用实体类接收的类似这样：

		// reqParams=com.whoareyou.fido.rest.User@616b9c0e

		// 用Map<String,Object>接收的是这样：menuNo=56473283，遍历这个map区分两种不同，使用不同的取值方式。

		// 根据获取到的值所属的不同类型通过两种不同的方法获取参数

		boolean flag = false;

		if (nameAndArgs != null && nameAndArgs.size() > 0) {

			for (Map.Entry<String, Object> entry : nameAndArgs.entrySet()) {

				if (entry.getValue() instanceof String) {

					flag = true;

					break;

				}

			}

		}

		StringBuffer sb = new StringBuffer();

		if (flag) {

			// 从Map中获取

			sb.append(JSON.toJSONString(nameAndArgs));

		} else {

			if (args != null) {

				for (Object object : args) {

					if (object != null) {

						if (object instanceof MultipartFile || object instanceof ServletRequest

								|| object instanceof ServletResponse) {

							continue;

						}

						sb.append(JSON.toJSONString(object));

					}

				}

			}

		}

		return sb;

	}

	public static Map<String, Object> getNameAndArgsMap(Class<?> cls, String clazzName, String methodName, Object[] args)
			throws NotFoundException {

		Map<String, Object> nameAndArgs = new HashMap<String, Object>();

		ClassPool pool = ClassPool.getDefault();

		ClassClassPath classPath = new ClassClassPath(cls);

		pool.insertClassPath(classPath);

		CtClass cc = pool.get(clazzName);

		CtMethod cm = cc.getDeclaredMethod(methodName);

		MethodInfo methodInfo = cm.getMethodInfo();

		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

		if (attr == null) {
			// exception
		}

		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

		for (int i = 0; i < cm.getParameterTypes().length; i++) {

			nameAndArgs.put(attr.variableName(i + pos), args[i]);// paramNames即参数名

		}

		return nameAndArgs;

	}
	
	
	/**

	 * 得到方法参数的名称

	 * @param cls

	 * @param clazzName

	 * @param methodName

	 * @return

	 * @throws NotFoundException

	 */

	public static String[] getFieldsName(Class cls, String clazzName, String methodName) throws NotFoundException{

		ClassPool pool = ClassPool.getDefault();

		ClassClassPath classPath = new ClassClassPath(cls);

		pool.insertClassPath(classPath);

		CtClass cc = pool.get(clazzName);

		CtMethod cm = cc.getDeclaredMethod(methodName);

		MethodInfo methodInfo = cm.getMethodInfo();

		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

		if (attr == null) {

			// exception
		}

		String[] paramNames = new String[cm.getParameterTypes().length];

		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

		for (int i = 0; i < paramNames.length; i++){

			paramNames[i] = attr.variableName(i + pos);	//paramNames即参数名

		}

		return paramNames;

	}
	
	/**

	 * 得到参数的值

	 * @param obj

	 */

	public static String getFieldsValue(Object obj) {

		Field[] fields = obj.getClass().getDeclaredFields();

		String typeName = obj.getClass().getTypeName();

		for (String t : types) {

			if(t.equals(typeName))

				return "";

		}

		StringBuilder sb = new StringBuilder();

		sb.append("【");

		for (Field f : fields) {

			f.setAccessible(true);

			try {

				for (String str : types) {

					if (f.getType().getName().equals(str)){

						sb.append(f.getName() + " = " + f.get(obj)+"; ");

					}

				}

			} catch (IllegalArgumentException e) {

				e.printStackTrace();

			} catch (IllegalAccessException e) {

				e.printStackTrace();

			}

		}

		sb.append("】");

		return sb.toString();

	}
	
	

	private static String writeLogInfo(String[] paramNames, JoinPoint joinPoint){

		Object[] args = joinPoint.getArgs();

		StringBuilder sb = new StringBuilder();

		boolean clazzFlag = true;

		for(int k=0; k<args.length; k++){

			Object arg = args[k];

			sb.append(paramNames[k]+" ");

			// 获取对象类型

			String typeName = arg.getClass().getTypeName();

			

			for (String t : types) {

				if (t.equals(typeName)) {

					sb.append("=" + arg+"; ");

				}

			}

			if (clazzFlag) {

				sb.append(getFieldsValue(arg));

			}

		}

		return sb.toString();

	}
	
}
