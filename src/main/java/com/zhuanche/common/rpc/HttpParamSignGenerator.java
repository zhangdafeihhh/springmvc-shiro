package com.zhuanche.common.rpc;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**TP 参数的签名**/
public final class HttpParamSignGenerator {
	private static final Logger log  = LoggerFactory.getLogger(HttpParamSignGenerator.class);
	
	/**订单接口：生成签名**/
	public static String genSignForOrderAPI ( Map<String,Object> params , String signKey ) {
		//步骤1 ：所有参数过滤掉参数值为null 和空串""的参数，过滤掉参数sign ，然后按照参数名升序排序，最后拼接参数key
		Map<String,Object> treeMap = new TreeMap<String,Object>();
		for( String key :  params.keySet()  ) {
			if(StringUtils.isEmpty(key)) {
				continue;
			}
			if(key!=null && "sign".equalsIgnoreCase(key.trim())) {
				continue;
			}
			Object value  = params.get(key);
			if(value==null ) {
				continue;
			}
			String valueString = value.toString().trim();
			if(StringUtils.isEmpty(valueString) ) {
				continue;
			}
			treeMap.put(key, value);
		}
		if (treeMap.size() == 0) {
            // 没有参数
            return "";
        }
		
		StringBuffer rawText = new StringBuffer();
		for(String key : treeMap.keySet()){
			Object value  = treeMap.get(key);
			rawText.append(key).append("=").append( value.toString() ).append("&");
		}
		rawText.append("key=").append( signKey );
		String rawTextStr = rawText.toString();
		//步骤2： 计算MD5散列值，返回16位 byte[]
		byte[]  md5 = DigestUtils.md5(rawTextStr);
		//步骤3：计算步骤2的结果对应的Base64编码格式字符串	
		String sign = new String(Base64.encodeBase64(md5));
		log.info("raw: "+rawTextStr);
		log.info("sign: "+sign);
		return sign;
	}
}