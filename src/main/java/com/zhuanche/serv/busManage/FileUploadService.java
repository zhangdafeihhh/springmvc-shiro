package com.zhuanche.serv.busManage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.mime.content.FileBody;
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

	// 返回处理结果-key
	public String CODE = "code";
	public String MSG = "msg";
	public String FILE_PATH = "filePath";

	// 返回处理结果-value
	public String SUCCESS = "success";
	public String ERROR = "error";

	/**
	 * 不清楚该www-api接口是否支持上传图片之外的其它图片，慎用
	 * 
	 * @param file
	 * @param url
	 * @param currentUser
	 * @return
	 */
	private Map<String, Object> uploadFile(File file, String url, SSOLoginUser currentUser) {
		logger.info("[ FileUploadService-uploadCarFile ] 上传车辆相关图片，参数：file {}, url {}", file, url);

		// 处理结果
		Map<String, Object> resultMap = new HashMap<String, Object>();

		if (file == null) {
			logger.info("[ FileUploadService-uploadCarFile ] 文件为空，无需要上传，参数：file {}, url {}", file, url);
			return resultMap;
		}

		if (currentUser == null) {
			currentUser = WebSessionUtil.getCurrentLoginUser();
		}
		try {
			// 上传操作人
			Integer userId = currentUser.getId();
			String username = currentUser.getName();

			// 上传图片参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("carFile", new FileBody(file));
			params.put("create_uid", userId);
			String result = null;
			JSONObject jsonResult = null;
			try {
				logger.info( "[ FileUploadService-uploadCarFile ] 调用 www-api 上传车辆相关图片接口，参数：file {}, url {}, userId{}, username {}", file, url, userId, username);

				long start = System.currentTimeMillis();
				result = wwwApiTemplate.postMultipartData(url, String.class, params);
				long end = System.currentTimeMillis();
				logger.info("[ FileUploadService-uploadCarFile ] 生成二维码  {} ,花费时间:cost {} ms", file.getName(), end - start);

				jsonResult = JSONObject.parseObject(result);
			} catch (Exception e) {
				logger.error( "[ FileUploadService-uploadCarFile ] 调用 www-api 上传车辆相关图片接口  异常，参数：file {}, url {}, userId{}, username {}", file, url, userId, username, e);

				resultMap.put(CODE, ERROR);
				resultMap.put(MSG, e.getMessage());

				return resultMap;
			}

			if (1 == jsonResult.getIntValue("code")) {
				String filePath = jsonResult.getJSONArray("data").getJSONObject(0).getString("path");
				logger.info("[ FileUploadService-uploadCarFile ] 上传车辆相关图片：" + result);
				logger.info("[ FileUploadService-uploadCarFile ] 上传车辆相关图片接口返回路径:" + filePath);

				// 返回 filePath
				resultMap.put(CODE, SUCCESS);
				resultMap.put(FILE_PATH, filePath);
				return resultMap;
			} else {
				logger.info("[ FileUploadService-uploadCarFile ] 上传车辆相关图片接口处理失败，返回信息" + result);

				resultMap.put(CODE, ERROR);
				resultMap.put(MSG, result);
				return resultMap;
			}
		} catch (NullPointerException nullPointEx) {
			logger.error("[ FileUploadService-uploadCarFile ] 上传车辆相关图片处理出现空指针异常", nullPointEx);

			resultMap.put(CODE, ERROR);
			resultMap.put(MSG, "上传车辆相关图片处理出现异常");
			return resultMap;
		} catch (Exception ex) {
			logger.error("[ FileUploadService-uploadCarFile ] 上传车辆相关图片处理异常", ex);

			resultMap.put(CODE, ERROR);
			resultMap.put(MSG, ex.getMessage());
			return resultMap;
		} finally {
			file.delete();// 删除上传的临时文件
		}
	}

	public Map<String, Object> uploadPublicFile(File file) {
		String url = "/upload/public";
		return uploadFile(file, url, null);
	}

	public Map<String, Object> uploadPublicFile(File file, SSOLoginUser currentUser) {
		String url = "/upload/public";
		return uploadFile(file, url, currentUser);
	}

	public Map<String, Object> uploadPrivateFile(File file) {
		String url = "/upload/private";
		return uploadFile(file, url, null);
	}

	public Map<String, Object> uploadPrivateFile(File file, SSOLoginUser currentUser) {
		String url = "/upload/private";
		return uploadFile(file, url, currentUser);
	}

}
