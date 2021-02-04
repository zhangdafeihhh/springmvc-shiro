package com.zhuanche.common.sms; 

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.http.MpOkHttpUtil;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** 
* SmsSendUtil Tester. 
* 
* @author <Authors name> 
* @since <pre>02/04/2021</pre> 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
@ActiveProfiles("test")
public class SmsSendUtilTest {

    private static final Logger log  = LoggerFactory.getLogger(SmsSendUtil.class);
    private static OkHttpClient httpclient = null;
    private static Properties properties; //短信URL等的配置
    static{
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        try{
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sSLSocketFactory = sslContext.getSocketFactory();
            httpclient = new OkHttpClient.Builder().
                    connectionPool(new ConnectionPool(100, 3, TimeUnit.MINUTES))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(sSLSocketFactory)
                    .hostnameVerifier(hostnameVerifier)
                    .build();
        }catch(Exception ex ) {
            log.error("短信发送工具类初始化异常！", ex);
            System.err.println("短信发送工具类初始化异常！");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: init(String configLocation) 
* 
*/ 
@Test
public void testInit() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: send(String mobile, String content) 
* 
*/ 
@Test
public void testSend() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: sendTemplate(String mobile, Integer templateId, List param) 
* 
*/ 
@Test
public void testSendTemplate() throws Exception { 
//TODO: Test goes here...
    String url            = "http://test-inside-mp.01zhuanche.com/api/v1/message/template/send";
    String appkey     = "82BFBBA6C03247A2A5BCEFB9BD3EBB1F";
    String appsecret = "3D7F14CB41144D6EA5D9C6795CE788CD";

    String mobile = "13552448009";
   // Integer templateId= 1187;
    Integer templateId= 1186;
    List param = Arrays.asList("阿三","13552448009","780425");
    try {
        String params = JSONObject.toJSONString(param);
        Long timestamp = System.currentTimeMillis();
        String sha1 = "appkey=" + appkey + "&mobile=" + mobile + "&params=" + params + "&templetId=" + templateId
                + "&timestamp=" + timestamp + "&appsecret=" + appsecret;
        String md5Hex = DigestUtils.md5Hex(sha1).toUpperCase();
        Map<String,Object> maps = new HashMap();
        maps.put("templetId",templateId);
        maps.put("appkey",appkey);
        maps.put("params",params);
        maps.put("mobile",mobile);
        maps.put("timestamp",timestamp);
        maps.put("sign",md5Hex);
        String responseBody = MpOkHttpUtil.okHttpPost(url,maps,0,mobile);
        log.info("发送短信="+mobile+", 响应："+responseBody);
    } catch (Exception e) {
        log.error("发送短信="+mobile+", 异常：{}",e);
    }

} 


} 
