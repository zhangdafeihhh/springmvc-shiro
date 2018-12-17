package com.zhuanche.controller.busManage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: BaseController
 * @Description: 基础Controller
 * @author: yanyunpeng
 * @date: 2018年12月10日 上午11:17:35
 * 
 */
public class BusBaseController {

	private static final Logger logger = LoggerFactory.getLogger(BusBaseController.class);

	/*
	 * 下载
	 */
	protected void fileDownload(HttpServletRequest request, HttpServletResponse response, String path) {

		File file = new File(path);// path是根据日志路径和文件名拼接出来的
		String filename = file.getName();// 获取日志文件名称
		try (OutputStream os = new BufferedOutputStream(response.getOutputStream());
				InputStream is = new BufferedInputStream(new FileInputStream(path));) {
			// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
			response.reset();
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			response.setContentType("application/octet-stream");
			// 读取文件
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			// 输出文件
			os.write(buffer);
			os.flush();
		} catch (Exception e) {
			logger.error("[ BusFileDownload-fileDownload ] 下载本地文件出错, filePath={}, error={}", path, e.getMessage(), e);
		}
	}
}
