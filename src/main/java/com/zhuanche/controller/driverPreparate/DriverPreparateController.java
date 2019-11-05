package com.zhuanche.controller.driverPreparate;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driverPreparate.DriverPreparate;
import com.zhuanche.serv.driverPreparate.DriverPreparateService;
import com.zhuanche.util.MobileOverlayUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.TextUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.zhuanche.common.enums.MenuEnum.*;

/**
 *   司机报备查询接口
 *   @Auther  wanghongdong
 */
@Controller()
@RequestMapping(value = "/driverPreparate")
public class DriverPreparateController {

	private static Logger log =  LoggerFactory.getLogger(DriverPreparateController.class);

	@Autowired
	private DriverPreparateService driverPreparateService;

	/**
	 * 司机报备   接收参数 ，调用接口查询数据
	 * @param orderNo 订单号
	 * @param driverPhone 司机手机号
	 * @param licensePlates 车牌号
	 * @param page 当前页
	 * @param pageSize 页面展示条数
	 * @return AjaxResponse
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverPreparateData")
	@RequiresPermissions(value = { "DriverAttendance_look" } )
	@RequestFunction(menu = ORDER_PREPARATION_LIST)
	public AjaxResponse queryDriverPreparateData(String orderNo, String driverPhone, String licensePlates,
												 @Verify(param = "page",rule = "required|min(1)") Integer page,
												 @Verify(param = "pageSize",rule = "required|min(5)") Integer pageSize) {

		if (StringUtils.isEmpty(orderNo) && StringUtils.isEmpty(driverPhone) && StringUtils.isEmpty(licensePlates)){
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		Map<String,Object> map =  driverPreparateService.selectList(orderNo, driverPhone, licensePlates, page, pageSize);
		Integer total = map.get("total") !=null ? Integer.valueOf(map.get("total").toString()) : 0;
		List<DriverPreparate> list = (List) map.get("list");
		if (list!=null && list.size() > 0 ){
			for (DriverPreparate driverPreparate:list) {
				driverPreparate.setCreateDateStr(TimeStamp2Date(driverPreparate.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
			}
		}
		PageDTO pageDTO = new PageDTO(page, pageSize, total, list);
		overLayPhone(list);
		return AjaxResponse.success(pageDTO);
	}

	private void overLayPhone(List<DriverPreparate> list) {
		if (Objects.nonNull(list)){
			for (DriverPreparate driverPreparate : list) {
				driverPreparate.setDriverPhone(MobileOverlayUtil.doOverlayPhone(driverPreparate.getDriverPhone()));
			}
		}
	}

	/**
	 * 司机报备详情
	 * @param orderNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverPreparateDetails")
	@RequestFunction(menu = ORDER_PREPARATION_DETAIL)
	public AjaxResponse queryOrderDetails(@Verify(param = "orderNo",rule = "required") String orderNo) {
		DriverPreparate entity = driverPreparateService.selectDriverPreparateDetail(orderNo);
		if(entity!=null){
			entity.setCreateDateStr(TimeStamp2Date(entity.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
		}
		return AjaxResponse.success(entity);
	}

	@ResponseBody
	@RequestMapping("/exportException")
	public AjaxResponse exportException(@Verify(param = "fileName",rule = "required") String fileName, HttpServletRequest request, HttpServletResponse response) {
		log.info("下载司机报备图片");
		String[] files = fileName.split(",");
		if (files != null && files.length > 0) {
			try {
				download(request, response, files);
				return AjaxResponse.success("下载成功");
			} catch (Exception e) {
				e.printStackTrace();
				return AjaxResponse.fail(RestErrorCode.FILE_EXCEL_REPORT_FAIL);
			}
		}
		return AjaxResponse.fail(RestErrorCode.FILE_EXCEL_REPORT_FAIL);
	}

	public void download(HttpServletRequest request, HttpServletResponse response, String[] files){
		ZipOutputStream zos = null;
		try {
			String downloadFilename = "司机报备图片.zip";//文件的名称
			downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
			response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
			response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
			zos = new ZipOutputStream(response.getOutputStream());
			for (int i=0;i<files.length;i++) {
				URL url = new URL(files[i]);
				zos.putNextEntry(new ZipEntry(UUID.randomUUID() +".jpg"));
				InputStream fis = null;
				try {
					fis = getInputStreamByGet(files[i]);
					byte[] buffer = new byte[1024];
					int r = 0;
					while ((r = fis.read(buffer)) != -1) {
						zos.write(buffer, 0, r);
					}
				} finally {
					if (null != fis) {
						fis.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(zos!=null){
					zos.flush();
					zos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 通过get请求得到读取器响应数据的数据流
	public static InputStream getInputStreamByGet(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = conn.getInputStream();
				return inputStream;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Java将Unix时间戳转换成指定格式日期字符串
	 * @param timestampString 时间戳 如："1473048265";
	 * @param formats 要格式化的格式 默认："yyyy-MM-dd HH:mm:ss";
	 *
	 * @return 返回结果 如："2016-09-05 16:06:42";
	 */
	public static String TimeStamp2Date(String timestampString, String formats) {
		if (TextUtils.isEmpty(formats))
			formats = "yyyy-MM-dd HH:mm:ss";
		Long timestamp = Long.parseLong(timestampString);
		String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
		return date;
	}
}