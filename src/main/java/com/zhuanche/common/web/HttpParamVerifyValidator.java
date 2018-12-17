package com.zhuanche.common.web;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.zhuanche.constants.SaasConst;
import com.zhuanche.util.IdNumberUtil;

public class HttpParamVerifyValidator {
	private static final Predicate<String> NOT_NUMBER_PREDICATE = Pattern.compile("^\\d+$").asPredicate().negate();
	/**最小值**/
	public String min( String value , String threadHold ) {
		if(StringUtils.isEmpty(value) || StringUtils.isEmpty(threadHold)){
			return null;
		}
		if( new BigDecimal(value).compareTo(new BigDecimal(threadHold))==-1  ) {
			return "传入值"+value+"，小于最小值"+threadHold ;
		}
		return null;
	}
	/**最大值**/
	public String max( String value , String threadHold) {
		if(StringUtils.isEmpty(value) || StringUtils.isEmpty(threadHold)){
			return null;
		}
		if( new BigDecimal(value).compareTo(new BigDecimal(threadHold))==1  ) {
			return "传入值"+value+"，大于最大值"+threadHold ;
		}
		return null;
	}

	/**电子邮箱**/
	public String email( String value ) {
		if(StringUtils.isEmpty(value) ){
			return null;
		}
//		String regexp = "^[A-Za-z0-9]+([-_\\.][A-Za-z0-9]+)*@([-A-Za-z0-9]+[\\.])+[A-Za-z0-9]+$";
		String regexp = SaasConst.EMAIL_REGEX;
		if( ! value.matches(regexp) ) {
			return "传入值"+value+"，电子邮箱格式错误";
		}
		return null;
	}
	/**手机号码**/
	public String mobile( String value ) {
		if(StringUtils.isEmpty(value) ){
			return null;
		}
//		String regexp = "^1(3|4|5|6|7|8|9)[0-9]{9}$";
		String regexp = SaasConst.MOBILE_REGEX;
		if( ! value.matches(regexp) ) {
			return "传入值"+value+"，手机号码格式错误";
		}
		return null;
	}
	/**身份证号码**/
	public String idcard( String value ) {
		if(StringUtils.isEmpty(value) ){
			return null;
		}
		if(value.trim().length()==15 ) {
			return null;
		}
		String regexp = "^[1-9]{1}[0-9]{16}[0-9xX]{1}$";
		if( ! value.matches(regexp) ) {
			return "传入值"+value+"，身份证号码格式错误";
		}
		if( IdNumberUtil.check(value) ==false ) {
			return "传入值"+value+"，身份证号码不正确";
		}
		return null;
	}

	/**自定义正则表达式**/
	public String RegExp( String value ,  String threadHold ) {
		if(StringUtils.isEmpty(value) || StringUtils.isEmpty(threadHold)){
			return null;
		}
		String regexp = threadHold;
		if( ! value.matches(regexp) ) {
			return "传入值"+value+"，格式错误";
		}
		return null;
	}

	/**逗号分隔的数字**/
	public String multiNumber( String value ) {
		if(StringUtils.isEmpty(value) ){
			return null;
		}
		String[] numbers = value.split(",");
		List<String> notNumbers = Arrays.asList(numbers).parallelStream().filter( NOT_NUMBER_PREDICATE  ).collect(Collectors.toList());
		if(notNumbers!=null && notNumbers.size()>0 ) {
			return "格式错误，不是逗号分隔的数字";
		}
		return null;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		StringBuffer sb = new StringBuffer();
		for( int i=0;i<1000;i++) {
			sb.append(i).append(",");
		}
		sb.append("1a");
		
		HttpParamVerifyValidator validator = new HttpParamVerifyValidator();
//		Method validatorMethod = validator.getClass().getMethod("RegExp", String.class, String.class );
//		String invalidReason = (String) validatorMethod.invoke(validator, sb.toString() ,"^\\d+(,\\d+)*$");////校验不通过的原因
//		System.out.println( invalidReason );
		
		Method validatorMethod2 = validator.getClass().getMethod("multiNumber", String.class );
		String invalidReason2 = (String) validatorMethod2.invoke(validator, sb.toString());//校验不通过的原因
		System.out.println( invalidReason2 );
	}
	
}