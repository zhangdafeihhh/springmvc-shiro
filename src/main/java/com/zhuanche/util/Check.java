package com.zhuanche.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检测工具.
 * 
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 * 
 * @author lunan
 * @since 1.0
 * @version 1.0
 */
public class Check {

	/**
	 * 检测Java对象是否为空.
	 * 
	 * @param obj
	 * 待检测的对象
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNObj(final Object obj) {
		if(obj == null) {
			return true;
		}
		
		if (obj instanceof String) {
			return NuNStr((String) obj);
		}
		
		return false;
	}

	/**
	 * 检测Java对象是否为空. 同时检测多个指定的对象, 如果存在一个为空, 则全部为空.
	 * 
	 * @param objs
	 * 待检测的对象
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNObjs(final Object... objs) {
		for (final Object obj : objs) {
			final boolean nun = NuNObj(obj);
			if (nun) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测Java对象是否为空. 同时检测多个指定的对象, 如果存在一个为空, 则全部为空.
	 * 
	 * @param objs
	 * 待检测的对象
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNObject(final Object[] objs) {
		if ((objs == null) || (objs.length == 0)) {
			return true;
		}
		for (final Object obj : objs) {
			final boolean nun = NuNObj(obj);
			if (nun) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测字符串是否为空.
	 * <p>
	 * 1. 未分配内存
	 * </p>
	 * <p>
	 * 2. 字符串剔除掉前后空格后的长度为0
	 * </p>
	 * 
	 * @param str
	 * 待检测的字符串
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNStr(final String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	/**
	 * 严格的检测字符串是否为空.
	 * <p>
	 * 1. 未分配内存
	 * </p>
	 * <p>
	 * 2. 字符串剔除掉前后空格后的长度为0
	 * </p>
	 * <p>
	 * 3. 字符串为'null'字串.
	 * </p>
	 * 
	 * @param str
	 * 待检测的字符串
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNStrStrict(final String str) {
		return NuNStr(str) || "null".equalsIgnoreCase(str);
	}

	/**
	 * 判断集合是否为空
	 * 
	 * @param colls
	 * 待检测的集合
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNCollection(final Collection<?> colls) {
		return (colls == null) || colls.isEmpty();
	}

	/**
	 * 判断Map集合是否为空
	 * 
	 * @param map
	 * 待检测的集合
	 * @return true: 空; false:非空.
	 */
	public static boolean NuNMap(final Map<?, ?> map) {
		return (map == null) || map.isEmpty();
	}

	/**
	 * 
	 * 编码URL参数. 提供默认的URT-8编码格式
	 * 
	 * @author YRJ
	 * @created 2014年12月21日 下午5:26:57
	 *
	 * @param params
	 * @return
	 */
	public static String encodeUrl(final String params) {
		return encodeUrl(params, "UTF-8");
	}

	/**
	 * 
	 * 编码URL参数
	 * 
	 * @author YRJ
	 * @created 2014年12月21日 下午5:26:39
	 *
	 * @param params	待编码的值
	 * @param encode	编码格式
	 * @return
	 */
	public static String encodeUrl(final String params, final String encode) {
		try {
			return URLEncoder.encode(params, encode);
		} catch (final UnsupportedEncodingException e) {
			return encodeUrl(params, "UTF-8");//如果你再错, 就是混蛋王八蛋了.
		}
	}

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("").toUpperCase();
        }
        return dest.replaceAll("\\s*", "");
    }

	private Check() {
		throw new AssertionError("Uninstantiable class");
	};

    public static void main(String[] args) {
        Long n1 = 6553699777L;
        DecimalFormat df1 = new DecimalFormat("##,###.##");
        System.out.println(df1.format(n1));//1,234.527

        DecimalFormat a = new DecimalFormat(".##");
        String s= a.format(33333);
        System.err.println(s);
    }
}
