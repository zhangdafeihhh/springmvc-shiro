package com.zhuanche.common.securityLog;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.zhuanche.common.elasticsearch.ElasticSearchInit;
import org.apache.http.HttpHost;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.elasticsearch.ElasticsearchGenerationException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.IpAddr;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** 
 * ClassName: SensitiveDataOprationLogAspect.java 
 * Date: 2018年12月12日 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 * AOP切面：应用于注解@SensitiveDataOperationLog
 * 后置通知： 记录用户操作敏感数据成功日志
 * 日志输出格式：[时间][IP地址][来源系统][操作账户][一级数据类型][二级数据类型][说明]
 * demo:
 *	{
 *	'datetime': '2018-11-11 21:11:111',
 *	'ip': '172.11.1.1',
 *	'system': '车管后台',
 *	'user': 'wulicheng',
 *	'fdatatype': '用户数据',
 *	'sdatatype': '司机的个人信息',
 *	'content': '司机个人信息读取, 查询: 李师傅 个人信息.'
 *	}
 */
@Component
@Aspect
public class SensitiveDataOprationLogAspect {
	
    private static final Logger log = LoggerFactory.getLogger(SensitiveDataOprationLogAspect.class);

    private static final String SYSTEM_NAME = "SAAS管理后台";
    
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");





	@Autowired
	HttpServletRequest httpServletRequest;

	@AfterReturning("within(com.zhuanche.controller..*) && @annotation(sdol)")
    public void addLogSuccess(JoinPoint jp , SensitiveDataOperationLog sdol){
        try {
            LocalDateTime localDateTime = LocalDateTime.now();
            String dateTime = DF.format(localDateTime);
            SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();// 获取当前登录用户信息
            String user = currentLoginUser.getName();

            String ip = IpAddr.getIpAddr(httpServletRequest);// 用户IP
            log.info("[{}][{}][{}][{}][{}][{}][{}]",dateTime,ip,SYSTEM_NAME,user,sdol.primaryDataType(),sdol.secondaryDataType(),sdol.desc());

            log.info(MessageFormat.format("es地址：{0},port:{1}",ElasticSearchInit.serviceIp,ElasticSearchInit.port));
            RestHighLevelClient highLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(ElasticSearchInit.serviceIp, Integer.parseInt(ElasticSearchInit.port), "http")));
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("dateTime",dateTime);
            map.put("ip",ip);
            map.put("systemName",SYSTEM_NAME);
            map.put("user",user);
            map.put("primaryDataType",sdol.primaryDataType());
            map.put("secondaryDataType",sdol.secondaryDataType());
            map.put("desc",sdol.desc());

            IndexRequest indexRequest = new IndexRequest(ElasticSearchInit.index, "senseLog", "").source(map);

            try {
                IndexResponse response = highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                log.info("response:" + response);
            } catch (IOException e) {
                log.info(e.getMessage());
            }

            try {
                highLevelClient.close();
            } catch (IOException e) {
                log.info("关闭错误"+e.getMessage());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ElasticsearchGenerationException e) {
            e.printStackTrace();
        }


    }
	
	/**   
     * 解析方法参数   
     * @param parames 方法参数   
     * @return 解析后的方法参数   
     */    
    private String parseParames(Object[] parames) {     
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<parames.length; i++){     
            if(parames[i] instanceof Object[] || parames[i] instanceof Collection){     
                JSONArray json = JSONArray.fromObject(parames[i]);     
                if(i==parames.length-1){     
                    sb.append(json.toString());     
                }else{     
                    sb.append(json.toString() + ",");     
                }     
            }else{     
                JSONObject json = JSONObject.fromObject(parames[i]);     
                if(i==parames.length-1){     
                    sb.append(json.toString());     
                }else{     
                    sb.append(json.toString() + ",");     
                }     
            }     
        }     
        String params = sb.toString();     
        params = params.replaceAll("(\"\\w+\":\"\",)", "");     
        params = params.replaceAll("(,\"\\w+\":\"\")", "");     
        return params;     
    }     
    
}
