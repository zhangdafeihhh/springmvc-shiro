package com.zhuanche.serv.statisticalAnalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.http.HttpClientUtil;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.ValidateUtils;

@Service
public class  StatisticalAnalysisService {
	
	private static final Logger logger = LoggerFactory.getLogger(StatisticalAnalysisService.class);
	
	public Map<String, Object> currentLoginUserVisibleParam(Map<String, Object> paramMap,SSOLoginUser currentLoginUser){
		if(currentLoginUser == null || paramMap==null){
			return paramMap;
		}
		if(!ValidateUtils.isAdmin(currentLoginUser.getAccountType())){
			Set<Integer> suppliers = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
		    Set<Integer> teamIds = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
		    Set<Integer> cityIds = currentLoginUser.getCityIds();// 获取用户可见的城市ID
		    // 供应商信息
		    String[] visibleAllianceIds = setToArray(suppliers);
			// 车队信息
			String[] visibleMotorcadeIds = setToArray(teamIds);
			// 可见城市
			String[] visibleCityIds = setToArray(cityIds);
			if(null == visibleAllianceIds || null == visibleMotorcadeIds || visibleCityIds == null ){
				return paramMap;
			}
		    paramMap.put("visibleAllianceIds", visibleAllianceIds); // 可见加盟商ID
		    paramMap.put("visibleMotorcadeIds", visibleMotorcadeIds); // 可见车队ID
		    paramMap.put("visibleCityIds", visibleCityIds); //可见城市ID
		}
		return paramMap;
	}
	
	/**
   	 * 导出文件
   	 * @param response
   	 * @param fileName 输出文件名称
   	 * @param path 下载文件地址+文件名
   	 * @throws Exception
   	 */
   	public synchronized void exportCsvFromToPage(HttpServletResponse response,String jsonString,String uri ,String fileName , String path) throws Exception {
   		//先远程下载
   		downloadCsvFromTemplet(jsonString,uri,path);
   	    // 让servlet用UTF-8转码，默认为ISO8859
   	    response.setCharacterEncoding("UTF-8");
   	    logger.info("导出文件fileName:"+fileName+"，path:" +path);
   	    if(StringUtils.isBlank(fileName) || StringUtils.isBlank(path)){
   	    	return;
   	    }
   	    File file = new File(path);
   	    if (!file.exists()) {
   	    	logger.info("导出文件不存在");
   	        // 让浏览器用UTF-8解析数据
   	        response.setHeader("Content-type", "text/html;charset=UTF-8");
   	        response.getWriter().write("文件不存在或已过期,请重新生成");
   	        return;
   	    }
   	   // String fileName = URLEncoder.encode(path.substring(path.lastIndexOf("/") + 1), "UTF-8");
   	    response.setContentType("text/csv");
   	    response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName+".csv"));
   	    InputStream is = null;
   	    OutputStream os = null;
   	    try {
   	        is = new FileInputStream(path);
   	        byte[] buffer = new byte[1024];
   	        os = response.getOutputStream();
   	        int len;
   	        while((len = is.read(buffer)) > 0) {
   	            os.write(buffer,0, len);
   	        }
   	    }catch(Exception e) {
   	        throw new RuntimeException(e);
   	    }finally {
   	        try {
   	            if (is != null) is.close();
   	            if (os != null) os.close();
   	        } catch (Exception e) {
   	            e.printStackTrace();
   	        }

   	    }
   	}
   	
	/**
   	 * 导出文件
   	 * @param response
   	 * @param fileName 输出文件名称
   	 * @param path 下载文件地址+文件名
   	 * @throws Exception
   	 */
   	public void exportCsvFromTemplet(HttpServletResponse response,String fileName , String path) throws Exception {
   	    // 让servlet用UTF-8转码，默认为ISO8859
   	    response.setCharacterEncoding("UTF-8");
   	    logger.info("导出文件fileName:"+fileName+"，path:" +path);
   	    if(StringUtils.isBlank(fileName) || StringUtils.isBlank(path)){
   	    	return;
   	    }
   	    File file = new File(path);
   	    if (!file.exists()) {
   	    	logger.info("导出文件不存在");
   	        // 让浏览器用UTF-8解析数据
   	        response.setHeader("Content-type", "text/html;charset=UTF-8");
   	        response.getWriter().write("文件不存在或已过期,请重新生成");
   	        return;
   	    }
   	   // String fileName = URLEncoder.encode(path.substring(path.lastIndexOf("/") + 1), "UTF-8");
   	    response.setContentType("text/csv");
   	    response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName+".csv"));
   	    InputStream is = null;
   	    OutputStream os = null;
   	    try {
   	        is = new FileInputStream(path);
   	        byte[] buffer = new byte[1024];
   	        os = response.getOutputStream();
   	        int len;
   	        while((len = is.read(buffer)) > 0) {
   	            os.write(buffer,0, len);
   	        }
   	    }catch(Exception e) {
   	        throw new RuntimeException(e);
   	    }finally {
   	        try {
   	            if (is != null) is.close();
   	            if (os != null) os.close();
   	        } catch (Exception e) {
   	            e.printStackTrace();
   	        }

   	    }
   	}
   	
   	 /**
   	  * 根据uri远程调用接口下载文件
   	  * @param paramMap
   	  * @param uri 远程接口地址
   	  * @param path 保存文件路径+文件名
   	  */
	 public void downloadCsvFromTemplet(String jsonString,String uri,String path) {
	        HttpClient httpClient = null;
	        logger.info("远程下载文件uri:"+uri+"，path:" +path);
	        logger.info("远程下载文件参数:"+jsonString);
	        if(StringUtils.isBlank(uri) || StringUtils.isBlank(path) || StringUtils.isBlank(jsonString)){
	   	    	return;
	   	    }
	        OutputStream output = null;
	        try{
	            httpClient = new DefaultHttpClient();
	            HttpPost p = new HttpPost(new URI(uri));
	            p.addHeader("encoding", "UTF-8");
	            p.addHeader("content-type", "application/json; charset=utf-8");
	            StringEntity entity = new StringEntity(jsonString,"UTF-8");
	            p.setEntity(entity);
	            output =new FileOutputStream(path);
	            HttpResponse httpResponse = httpClient.execute(p);
	            InputStream input=httpResponse.getEntity().getContent();
	            int len = -1;
	            byte[] buffer = new byte[1024];
	            while((len=input.read(buffer))!=-1){
	                output.write(buffer,0,len);
	            }
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
   	
	/** 将Set<Integer>集合转成String[] **/
	public String[] setToArray(Set<Integer> set){
		if(null == set || set.size() == 0){
			return null;
		}
		Object[] array = set.toArray();
		String[] stra = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			stra[i] = array[i].toString();
		}
		return stra;
	}   
	
	/** 调用大数据接口获取数据  **/
	public  AjaxResponse parseResult(String url,Map<String, Object> paramMap) {
		try {
			logger.info("调用大数据接口，url--" + url);
			String jsonString = JSON.toJSONString(paramMap);
			logger.info("调用大数据接口，参数--" + jsonString);
			String result = HttpClientUtil.buildPostRequest(url).setBody(jsonString).addHeader("Content-Type", ContentType.APPLICATION_JSON).execute();
			logger.info("调用大数据接口，result--" + result);
			result = result.replaceAll("null", "\"\"");
			result = result.replaceAll("NULL", "\"\"");
			JSONObject job = JSON.parseObject(result);
			if (job == null) {
				logger.error("调用大数据" + url + "返回结果为null");
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			if (!job.getString("code").equals("0")) {
				return AjaxResponse.failMsg(Integer.parseInt(job.getString("code")), job.getString("message"));
			}
			try {
				JSONObject jsonResult = JSON.parseObject(job.getString("result"));
				return AjaxResponse.success(jsonResult);
			} catch (Exception e) {
				JSONArray resultArray = JSON.parseArray(job.getString("result"));
				return AjaxResponse.success(resultArray);
			}
		} catch (HttpException e) {
			logger.error("调用大数据" + url + "异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}
	
	public Map<String, Object> getCurrentLoginUserParamMap(Map<String, Object> paramMap,Long cityId,String supplier,String teamId){
		// 数据权限设置
		Set<Integer> cityIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有城市ID
		Set<Integer> supplierIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的所有供应商ID
		Set<Integer> teamIdsForAuth = new HashSet<Integer>();// 非超级管理员可以管理的可见的车队信息
		logger.info("非超级管理员:"+WebSessionUtil.isSupperAdmin());
		if (!WebSessionUtil.isSupperAdmin()) {// 非超级管理员
			// 获取当前登录用户信息
			SSOLoginUser currentLoginUser = WebSessionUtil.getCurrentLoginUser();
	   	    logger.info("获取当前登录用户信息:"+currentLoginUser);
			if(null != currentLoginUser){
				cityIdsForAuth = currentLoginUser.getCityIds();// 获取用户可见的城市ID
				supplierIdsForAuth = currentLoginUser.getSupplierIds();// 获取用户可见的供应商信息
				teamIdsForAuth = currentLoginUser.getTeamIds();// 获取用户可见的车队信息
			}else{
				return null;
			}
			if (cityIdsForAuth.size() > 0 && cityId != null && !cityIdsForAuth.contains(cityId)) {
				return null;
			}
			if (supplierIdsForAuth.size() > 0 && StringUtils.isNotBlank(supplier) && !supplierIdsForAuth.contains(supplier)) {
				return null;
			}
			if (teamIdsForAuth.size() > 0 && StringUtils.isNotBlank(teamId) && !teamIdsForAuth.contains(teamId)) {
				return null;
			}
		}
		// 查询参数设置
		Set<Long> cityIds = new HashSet<Long>();
		Set<String> supplierIds = new HashSet<String>();
		Set<String> teamIds = new HashSet<String>();
		// 城市权限
		if (null != cityId) {
			cityIds.add(cityId);
		} else {
			if(cityIdsForAuth != null && cityIdsForAuth.size() >0){
				for (Integer cityid : cityIdsForAuth) {
					cityIds.add(cityid.longValue());
				}
			}
		}
		// 供应商权限
		if (StringUtils.isNotBlank(supplier)) {
			supplierIds.add(supplier);
		} else {
			if(supplierIdsForAuth != null && supplierIdsForAuth.size() >0 ){
				for (Integer supplierId : supplierIdsForAuth) {
					supplierIds.add(String.valueOf(supplierId));
				}
			}
		}
		// 车队权限
		if (StringUtils.isNotBlank(teamId)) {
			teamIds.add(teamId);
		} else {
			if(teamIdsForAuth != null && teamIdsForAuth.size() >0 ){
				for (Integer tId : teamIdsForAuth) {
					teamIds.add(String.valueOf(tId));
				}
			}
		}
		if(!supplierIds.isEmpty()){
			 paramMap.put("visibleAllianceIds", supplierIds); // 可见加盟商ID
		}
		if(!teamIds.isEmpty()){
			paramMap.put("visibleMotorcadeIds", teamIds); // 可见车队ID
		}
		if(!cityIds.isEmpty()){
			paramMap.put("visibleCityIds", cityIds); //可见城市ID
		}
		//非管理员  没有任何可见权限返回null
		/*if(cityIds.isEmpty() && teamIds.isEmpty() && supplierIds.isEmpty() && !WebSessionUtil.isSupperAdmin()){
			return null;
		}*/
		return paramMap;
	}
}
