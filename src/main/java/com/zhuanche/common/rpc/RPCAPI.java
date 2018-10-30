package com.zhuanche.common.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**抽象基类**/
public class RPCAPI{
	private static final Logger baselog = LoggerFactory.getLogger( RPCAPI.class );
	protected  final Logger log = LoggerFactory.getLogger(  this.getClass()  );
	private static final int MAX_RETRY_COUNT = 3;//接口重试次数

	private static final HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
	private static final X509TrustManager trustManager     = new TrustAllX509TrustManager();
	private static SSLSocketFactory       sslff            = null;   
	public enum HttpMethod{
		GET,POST;
	}
	static{
		//1.验证通过所有域名
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		//2.信任所有X509证书
		//创建SSLContext对象，并使用我们指定的信任管理器初始化
        try{
            TrustManager[] tm = { trustManager };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
	        sslff = sslContext.getSocketFactory();
		} catch(Exception e){
			baselog.error("init SSLSocketFactory failed", e);
		}
	}
	/**
	 * 请求URL,并获取响应结果
	 **/
	@SuppressWarnings("rawtypes")
	public String request( HttpMethod method, String url, Map<String,Object> httpParams, List cookies, String charset ){
		long b = System.currentTimeMillis();
		String requestId = method.toString()+"-"+System.currentTimeMillis();
		if(httpParams!=null && httpParams.containsKey("transId")) {
			requestId = method.toString()+"-"+ (String)httpParams.get("transId");
		}
		log.info("REQUEST[{}]-[{}],params={},cookies={},charset={}", requestId, url, httpParams, cookies, charset );
		try{
			//1.封装参数
            StringBuffer paramStr = new StringBuffer();
			if(httpParams!=null){
	        	Set<String> keys = httpParams.keySet();
	        	for(String key : keys){
	        		Object value = httpParams.get(key);
	        		if(value==null ) {
	        			continue;
	        		}
	        		paramStr.append(  key+ "=" +  URLEncoder.encode( value.toString() , charset ) + "&" );
	        	}
			}
			//2.如果是GET请求，则重新组装URL
			if(method.equals(HttpMethod.GET)){
				if(url.indexOf("?")>=0){
					url = url +"&"+ paramStr.toString();
				}else{
					url = url +"?"+ paramStr.toString();
				}
			}
			//3.创建连接（区分HTTP和HTTPS两种情况）
	        URL myURL = new URL(url);
	        URLConnection conn = null;
        	conn = myURL.openConnection();
	        if(url.toLowerCase().startsWith("https")){
		        HttpsURLConnection httpsConn = (HttpsURLConnection)conn;//此时是HttpsURLConnection对象，并设置其SSLSocketFactory对象
		        httpsConn.setSSLSocketFactory(sslff);
				if(method.equals(HttpMethod.GET)){
			        httpsConn.setRequestMethod("GET");
		        }
				if(method.equals(HttpMethod.POST)){
			        httpsConn.setRequestMethod("POST");
			        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");//只有POST才需要设置这个
		        }
	        }else{
		        HttpURLConnection httpConn = (HttpURLConnection)conn;
				if(method.equals(HttpMethod.GET)){
			        httpConn.setRequestMethod("GET");
		        }
				if(method.equals(HttpMethod.POST)){
			        httpConn.setRequestMethod("POST");
			        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");//只有POST才需要设置这个
		        }
	        }
	        //4.设置请求头
	        conn.setRequestProperty("Accept", "text/html,text/plain,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	        conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0)");
	        conn.setRequestProperty("Connection", "keep-alive");
	        conn.setAllowUserInteraction(true);
	        conn.setDoInput(true);
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setDefaultUseCaches(false);
	        conn.setConnectTimeout(  getConnectTimeout() );
	        conn.setReadTimeout(  getReadTimeout() );
	        //5.设置COOKIE
            if(cookies!=null && cookies.size()>0){
            	StringBuffer cookieStr = new StringBuffer();
                for(int i=0;i<cookies.size();i++){
                	String c = (String)cookies.get(i);
                	if(c==null || c.trim().equals("")){
                		continue;
                	}
                	if(i==cookies.size()-1){
                    	cookieStr.append( c.trim() );
                	}else{
                    	cookieStr.append( c.trim() +"; ");
                	}
                }
            	String cookieValue = cookieStr.toString();
            	if(cookieValue!=null && cookieValue.length()>0){
            		conn.setRequestProperty("Cookie", cookieValue);
            	}
            }
	        //6.写数据(当POST请求时)
			if(method.equals(HttpMethod.POST)){
                BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(conn.getOutputStream()));
                bw.write(paramStr.toString());
                bw.flush();
            }
	        //7.读数据
	        StringBuffer responseText = new StringBuffer();
	        InputStreamReader insr = new InputStreamReader(conn.getInputStream(),charset);
	        BufferedReader br = new BufferedReader(insr);
	        while(true){
	        	String line = br.readLine();
	        	if(line==null){
	        		break;
	        	}else{
	        		responseText.append(line).append("\r\n");
	        	}
	        }
	        //8.接收COOKIE
//            if(cookies==null){
//            	cookies = new LinkedList();
//            }else{
//            	//cookies.clear();
//            }
//            Map headers = conn.getHeaderFields();
//            if(headers.keySet().contains("Set-Cookie")){
//        		List<String> values = (List<String>)headers.get("Set-Cookie");
//        		for(String v: values){
//        			System.out.println("服务器返回的Set-Cookie: "+ v);
//    				cookies.add( v );
//        		}
//            }
	        //9.断开连接
	        if(url.toLowerCase().startsWith("https")){
		        HttpsURLConnection httpsConn = (HttpsURLConnection)conn;
		        httpsConn.disconnect();
	        }else{
		        HttpURLConnection httpConn = (HttpURLConnection)conn;
		        httpConn.disconnect();
	        }
	        String text = responseText.toString();
	        long e = System.currentTimeMillis();
			log.info("RESPONSE[{}],cost:[{}ms]=\n{}", requestId, (e-b), text );
	        return text;
		}catch(Exception ex){
			ex.printStackTrace();
	        long e = System.currentTimeMillis();
			log.error("EXCEPTION["+requestId+"],cost:["+(e-b)+"ms]= "+ex.getMessage() , ex );
			return null;
		}
	}
	
	/**
	 * 请求URL,并获取响应结果( 尝试 MAX_RETRY_COUNT 次)
	 **/
	@SuppressWarnings("rawtypes")
	public String requestWithRetry( HttpMethod method, String url, Map<String,Object> httpParams, List cookies, String charset ) {
		for(int i=0;i< MAX_RETRY_COUNT ;i++) {
			log.info("REQUEST[第"+(i+1)+"次]["+url+"]- 开始请求");
			String text = request( method, url,  httpParams, cookies, charset );
			if(text!=null) {
				log.info("RESPONSE[第"+(i+1)+"次]["+url+"]- 请求成功");
				return text;
			}else {
				log.info("RESPONSE[第"+(i+1)+"次]["+url+"]- 请求失败");
			}
			try { Thread.sleep(1000);}catch(Exception ex) { }
		}
		return null;
	}
	
	protected int getConnectTimeout() {
		return 60000;
	}
	protected int getReadTimeout() {
		return 60000;
	}
}