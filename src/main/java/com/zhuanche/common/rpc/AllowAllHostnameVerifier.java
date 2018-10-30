package com.zhuanche.common.rpc;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 验证通过所有域名
 */
public class AllowAllHostnameVerifier implements HostnameVerifier{
	@Override
	public boolean verify(String hostname, SSLSession session){
		return true;
	}
}