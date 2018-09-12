package com.zhuanche.dao;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.http.HttpClientUtil;

/**
 * Created by huhanwei on 2018/9/3.
 */
public class HttpClientDowloadFile {
	
		 public static void main11(String[] args) {

		   	 System.out.print("==============================================");
		    	Map<String, Object> paramMap = Maps.newHashMap();
/*				paramMap.put("queryDate", "2018-09-04");
				paramMap.put("visibleAllianceIds", new String[]{"1377", "101", "1050", "71"}); // 可见加盟商ID
			    paramMap.put("visibleMotorcardIds", new String[]{"2253", "1113", "248", "1031", "2644", "248"}); // 可见车队ID
			    paramMap.put("visibleCityIds", new String[]{"91", "113"}); //可见城市ID
*/			    
				paramMap.put("queryDate", "2018-09-04");
				paramMap.put("visibleAllianceIds", new String[]{"2498", "2487", "1809", "2359", "1369"}); // 可见加盟商ID
			    paramMap.put("visibleMotorcardIds", new String[]{"2498", "2487", "1809", "2359", "1369"}); // 可见车队ID
			    paramMap.put("visibleCityIds", new String[]{"88", "67", "111", "123", "85", "91"}); //可见城市ID
			    
	/*		    "queryDate": "2018-09-04",
				"visibleCityIds": ["88", "67", "111", "123", "85", "91"],
				"visibleAllianceIds": ["97", "946", "99", "489", "1307", "65", "1349"],
				"visibleMotorcadeIds": ["2498", "2487", "1809", "2359", "1369"]*/
						
				String jsonString = JSON.toJSONString(paramMap);
				System.out.println(jsonString);
				String url = "http://test-inside-bigdata-saas-data.01zhuanche.com/cancelOrderDetail/download";
				String url02 =  "http://test-inside-bigdata-saas-data.01zhuanche.com/completeOrderDetail/download";
				HttpClient httpClient = null;
			    String fileName = "取消订单详情数据.csv";
				System.out.println(jsonString);
		        OutputStream output = null;
		        try{
		            httpClient = new DefaultHttpClient();
		            HttpPost p = new HttpPost(new URI(url));
		            String filePath = "D:\\";
		            p.addHeader("encoding", "UTF-8");
		            p.addHeader("content-type", "application/json; charset=utf-8");

		            StringEntity entity = new StringEntity(jsonString,"UTF-8");
		            p.setEntity(entity);
		            output =new FileOutputStream(filePath+ fileName);
		            HttpResponse httpResponse = httpClient.execute(p);
		            InputStream input=httpResponse.getEntity().getContent();
		            int len = -1;
		            byte[] buffer = new byte[1024];
		            while((len=input.read(buffer))!=-1){
		                output.write(buffer,0,len);
		            }
		            System.out.println("download finish...");
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }finally{
		            try {
		                output.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		}
    public static void main(String[] args) {
    	 Map<String, Object> paramMap = Maps.newHashMap();
 		paramMap.put("queryDate", "2018-09-03");
 		paramMap.put("visibleCityIds", new String[]{"91", "113"}); //可见城市ID
 		paramMap.put("visibleAllianceIds", new String[]{"1377", "101", "1050", "71"});
 		paramMap.put("visibleMotorcadeIds", new String[]{"2253", "1113", "248", "1031", "2644", "248"});
 		String jsonString = JSON.toJSONString(paramMap);
 		System.out.println(jsonString);
    	pp(jsonString);
    	
    }

    
    public static void pp(String str){

        String filePath = "D:\\";
        HttpClient httpClient = null;
        String fileName = "取消订单详情数据1.csv";
        String jsonObj = "{\n" +
                "\t\"queryDate\": \"2018-09-03\",\n" +
                "\t\"visibleCityIds\": [\"91\", \"113\"],\n" +
                "\t\"visibleAllianceIds\": [\"1377\", \"101\", \"1050\", \"71\"],\n" +
                "\t\"visibleMotorcadeIds\": [\"2253\", \"1113\", \"248\", \"1031\", \"2644\", \"248\"]\n" +
                "}"; 
        Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("queryDate", "2018-09-03");
		paramMap.put("visibleCityIds", new String[]{"91", "113"}); //可见城市ID
		paramMap.put("visibleAllianceIds", new String[]{"1377", "101", "1050", "71"});
		paramMap.put("visibleMotorcadeIds", new String[]{"2253", "1113", "248", "1031", "2644", "248"});
		String jsonString = JSON.toJSONString(paramMap);
		System.out.println(jsonString);
                OutputStream output = null;
        try{
            httpClient = new DefaultHttpClient();
            HttpPost p = new HttpPost(new URI("http://test-inside-bigdata-saas-data.01zhuanche.com/completeOrderDetail/download"));
            p.addHeader("encoding", "UTF-8");
            p.addHeader("content-type", "application/json; charset=utf-8");
            StringEntity entity = new StringEntity(str,"UTF-8");
            p.setEntity(entity);
            output =new FileOutputStream(filePath + fileName);
            HttpResponse httpResponse = httpClient.execute(p);
            InputStream input=httpResponse.getEntity().getContent();
            int len = -1;
            byte[] buffer = new byte[1024];
            while((len=input.read(buffer))!=-1){
                output.write(buffer,0,len);
            }
            System.out.println("download finish...");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
}
