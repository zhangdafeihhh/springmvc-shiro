package com.zhuanche.common.rpc;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
/**
 * 信任所有X509证书
 **/
public final class TrustAllX509TrustManager implements X509TrustManager{
	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)	throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)	throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}