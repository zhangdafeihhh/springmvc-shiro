package com.zhuanche.serv.busManage;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.MyRestTemplate;


@Service
public class FileUploadService {

	private static Logger logger = LoggerFactory.getLogger(FileUploadService.class);

	@Autowired
	@Qualifier("wwwApiTemplate")
	private MyRestTemplate wwwApiTemplate;

	// ===============上传接口===================
	private final String publicUrl = "/upload/public";
	private final String privateUrl = "/upload/private";

	/**
	 * @param content
	 * @param url
	 * @param currentUser
	 * @return
	 */
	private UploadResult uploadFile(ContentBody content, String url) {
		// 处理结果
		UploadResult uploadResult = new UploadResult();
		if (content == null) {
			logger.info("[ FileUploadService-uploadFile ] 文件为空，无需要上传，参数：url {}", url);
			return uploadResult;
		}
		logger.info("[ FileUploadService-uploadFile ] 上传文件参数：file {}, url {}", content.getFilename(), url);

		SSOLoginUser currentUser = WebSessionUtil.getCurrentLoginUser();
		try {
			// 上传操作人
			Integer userId = currentUser.getId();
			String username = currentUser.getName();

			// 上传图片参数
			Map<String, Object> params = new HashMap<String, Object>();
			String filename = content.getFilename();
			params.put(filename, content);
			params.put("create_uid", userId);
			
			logger.info( "[ FileUploadService-uploadFile ] 调用 www-api 上传文件接口，参数：file {}, url {}, userId{}, username {}", filename, url, userId, username);
			long start = System.currentTimeMillis();
			String result = wwwApiTemplate.postMultipartData(url, String.class, params);
			long end = System.currentTimeMillis();
			logger.info("[ FileUploadService-uploadFile ] 调用 www-api 上传文件接口,花费时间:file={},cost={} ms", filename, end - start);

			logger.info("[ FileUploadService-uploadFile ] 调用 www-api 上传文件接口结果：result={}", result);
			JSONObject jsonResult = JSONObject.parseObject(result);
			if (jsonResult == null) {
				logger.info( "[ FileUploadService-uploadFile ] 调用 www-api 上传文件接口返回结果为空，参数：file {}, url {}, userId{}, username {}", filename, url, userId, username);
				uploadResult.setSuccess(false);
				uploadResult.setMsg("上传文件接口出错");
				return uploadResult;
			}
			if (1 == jsonResult.getIntValue("code")) {
				String filePath = jsonResult.getJSONArray("data").getJSONObject(0).getString("path");
				logger.info("[ FileUploadService-uploadFile ] 上传文件接口返回路径:filePath={}", filePath);

				// 返回 filePath
				uploadResult.setSuccess(true);
				uploadResult.setFilePath(filePath);
				return uploadResult;
			} else {
				uploadResult.setSuccess(false);
				uploadResult.setMsg(jsonResult.getString("msg"));
				return uploadResult;
			}
		} catch (Exception e) {
			logger.error("[ FileUploadService-uploadFile ] 上传文件接口处理异常", e);
			uploadResult.setSuccess(false);
			uploadResult.setMsg(e.getMessage());
			return uploadResult;
		}
	}
	
	static class UploadResult {
		public boolean success;
		public String msg;
		public String filePath;
		
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getFilePath() {
			return filePath;
		}
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
	}
	
	public UploadResult uploadPublicFile(File file) {
		return uploadFile(new FileBody(file), publicUrl);// 如果文件为临时文件则应该在finally块中删除file.delete(); 
	}

	public UploadResult uploadPrivateFile(File file) {
		return uploadFile(new FileBody(file), privateUrl);// 如果文件为临时文件则应该在finally块中删除file.delete(); 
	}
	
	public UploadResult uploadPublicStream(InputStream in, String filename) {
		return uploadFile(new InputStreamBody(in, filename), publicUrl);
	}
	
	public UploadResult uploadPrivateStream(InputStream in, String filename) {
		return uploadFile(new InputStreamBody(in, filename), privateUrl);
	}


}
