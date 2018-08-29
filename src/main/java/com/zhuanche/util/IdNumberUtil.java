package com.zhuanche.util;

public final class IdNumberUtil {
	/**身份证号码是否合法**/
	public static boolean check( String no ) {
		  // 1-17位相乘因子数组
	      int[] factor = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	      // 18位随机码数组
	      char[] random = "10X98765432".toCharArray();
	      // 计算1-17位与相应因子乘积之和
	      int total = 0;
	      for (int i = 0; i < 17; i++)
	      {
	         total += Character.getNumericValue(no.charAt(i)) * factor[i];
	      }
	      // 判断随机码是否相等
	      return random[total % 11] == no.charAt(17);
	}
	
//	public static void main( String[] a ) {
//		 // 正确
//	      System.out.println(IdNumberUtil.check("432831196411150810"));
//	      System.out.println(IdNumberUtil.check("130229198408081414"));
//	      // 错误
//	      System.out.println(IdNumberUtil.check("432831196411150813"));
//	}
}