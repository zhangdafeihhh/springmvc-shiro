//package com.zhuanche.util;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.CharsetUtils;
//import org.apache.http.util.EntityUtils;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.ClientHttpRequestFactory;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MyRestTemplate extends RestTemplate {
//
//	public MyRestTemplate() {
//		super();
//	}
//	public MyRestTemplate(ClientHttpRequestFactory requestFactory) {
//		super(requestFactory);
//	}
//	private String failover = "http://localhost/";
//
//	public void setFailover(String failover) {
//		this.failover = failover;
//	}
//
//	public String getHost(){
//		String [] hosts = failover.split(",");
//		Integer random = (int) (hosts.length * Math.random());
//		return hosts[random];
//	}
//	@Override
//	public <T> ResponseEntity<T> getForEntity(String  url, Class<T> responseType, Map<String, ?> urlVariables) throws RestClientException {
//		return super.getForEntity(getHost() + url, responseType, urlVariables);
//	}
//	@Override
//	public <T> ResponseEntity<T> getForEntity(String  url, Class<T> responseType, Object... urlVariables) throws RestClientException {
//		return super.getForEntity(getHost() + url, responseType, urlVariables);
//	}
//	@Override
//	public <T> T getForObject(String  url, Class<T> responseType, Map<String, ?> urlVariables) throws RestClientException {
//		return super.getForObject(getHost() + url, responseType, urlVariables);
//	}
//	@Override
//	public <T> T getForObject(String  url, Class<T> responseType, Object... urlVariables) throws RestClientException {
//		return super.getForObject(getHost() + url, responseType, urlVariables);
//	}
//	@Override
//	public URI postForLocation(String  url, Object request, Map<String, ?> urlVariables) throws RestClientException {
//		return super.postForLocation(getHost() + url, request, urlVariables);
//	}
//	@Override
//	public URI postForLocation(String  url, Object request, Object... urlVariables) throws RestClientException {
//		return super.postForLocation(getHost() + url, request, urlVariables);
//	}
//	@Override
//	public URI postForLocation(URI  url, Object request) throws RestClientException {
//		return super.postForLocation(getHost() + url, request);
//	}
//	@Override
//	public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
//		return super.postForObject(getHost() + url, request, responseType, uriVariables);
//	}
//	@Override
//	public <T> T postForObject(String  url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
//		return super.postForObject(getHost() + url, request, responseType, uriVariables);
//	}
//
//
//	private static HttpClient httpClient = HttpClients.createDefault();
//
//	@SuppressWarnings({ "unchecked"})
//	public <T> T postForObject(String url, Object responseType, Map<String, Object> uriVariables) {
//		List<NameValuePair> pairs = null;
//		if (uriVariables != null && !uriVariables.isEmpty()) {
//			pairs = new ArrayList<NameValuePair>(uriVariables.size());
//			for (Map.Entry<String, Object> entry : uriVariables.entrySet()) {
//				Object value = entry.getValue();
//				if (value != null) {
//					pairs.add(new BasicNameValuePair(entry.getKey(), value.toString()));
//				}
//			}
//		}
//		InputStream in = null;
//		HttpPost method = new HttpPost(this.getHost() + url);
//		RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(50000)
//                .setConnectTimeout(20000)
//                .setConnectionRequestTimeout(60000).build();
//		method.setConfig(requestConfig);
//		try {
//			if (pairs != null && pairs.size() > 0) {
//				method.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
//			}
//			long begin = System.nanoTime();
//			HttpResponse response = httpClient.execute(method);
//			long end = System.nanoTime();
//			logger.info("MyRestTemplate:"+((end-begin)/1000000)+" url:"+this.getHost() + url);
//			HttpEntity entity = response.getEntity();
//			String body = EntityUtils.toString(entity);
//			logger.info("调用地址:url:"+url+"返回结果:body:"+body);
//			in = entity.getContent();
//			try {
//				if ( responseType.equals(JSONArray.class)){
//					return (T) JSONArray.fromObject(body);
//				}else if ( responseType.equals(JSONObject.class)){
//					return (T) JSONObject.fromObject(body);
//				}else{
//					return (T) body;
//				}
//			} catch (Exception e) {
//				Map<String,Object> r = new HashMap<String,Object>();
//				Map<String,Object> r1 = new HashMap<String,Object>();
//				r1.put("result", 0);
//				r1.put("exception", e.getMessage());
//				r.put("jsonStr", r1);
//				logger.error("resutful error:"+e.getMessage()+"body:"+body);
//				return (T)JSONObject.fromObject(r);
//			}finally{
//				if(method!=null){
//					method.abort();
//				}
//				if(in!=null){
//					in.close();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Map<String,Object> r = new HashMap<String,Object>();
//			Map<String,Object> r1 = new HashMap<String,Object>();
//			r1.put("result", 0);
//			r1.put("exception", e.getMessage());
//			r.put("jsonStr", r1);
//			return (T)JSONObject.fromObject(r);
//		}finally{
//			if(method!=null){
//				method.abort();
//			}
//			if(in!=null){
//				try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	/**
//	 * @param url
//	 * @param params
//	 * @return
//	 * @throws IOException
//	 * @throws ClientProtocolException
//	 */
//	@SuppressWarnings({ "unchecked", "deprecation" })
//	public <T> T postMultipartData(String url, Class<T> responseType, Map<String, Object> params) {
//
//		HttpPost post = new HttpPost(getHost() + url);
//		try {
//			MultipartEntityBuilder meb = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//			for (Map.Entry<String, Object> entry : params.entrySet()) {
//				Object value = entry.getValue();
//				if (value != null) {
//					if(value instanceof ContentBody){
//
//						meb.addPart(entry.getKey(), (ContentBody)value);// uploadFile对应服务端类的同名属性<File类型>
//					}else{
//						meb.addTextBody(entry.getKey(), value.toString(),ContentType.APPLICATION_FORM_URLENCODED);
//					}
//				}
//			}
//			meb.setCharset(CharsetUtils.get("UTF-8")).build();
//			HttpEntity reqEntity = meb.build();
//			post.setEntity(reqEntity);
//			HttpResponse response = httpClient.execute(post);
//			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
//				HttpEntity entitys = response.getEntity();
//				if (entitys != null) {
//					if(responseType.equals(String.class)){
//						return (T) EntityUtils.toString(entitys,"utf-8");
//					}
//				}
//			}
//			httpClient.getConnectionManager().shutdown();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//}
