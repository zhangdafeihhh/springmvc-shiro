package com.zhuanche.controller.driverjoin;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.serv.DriverVerifyService;

/**
 * 司机加盟注册 ClassName: DriverVerifyController.java Date: 2018年8月29日
 * 
 * @author xinchun
 * @version 1.0
 * @since JDK 1.8.0_161
 */

@Controller
@RequestMapping("/driverVerify")
public class DriverVerifyController {

	private static final Logger logger = LoggerFactory.getLogger(DriverVerifyController.class);

	@Autowired
	DriverVerifyService driverVerifyService;

	/**
	 * 查询加盟司机审核列表数据
	 * @param page
	 * @param pageSize
	 * @param cityId
	 * @param supplier
	 * @param mobile
	 * @param verifyStatus
	 * @param createDateBegin
	 * @param createDateEnd
	 * @return
	 */
	@RequestMapping("/queryDriverVerifyData")
	public AjaxResponse queryDriverVerifyData(int page, int pageSize, Long cityId, String supplier, String mobile,
			Integer verifyStatus, String createDateBegin, String createDateEnd) {

		// 增加操作日志 TODO
		PageDTO pageDto = new PageDTO();
		try {
			// 分页查询司机加盟注册信息
			pageDto = driverVerifyService.queryDriverVerifyList(page, pageSize, cityId, supplier, mobile, verifyStatus,
					createDateBegin, createDateEnd);
		} catch (Exception e) {
			logger.error("查询司机加盟注册信息异常", e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success(pageDto);
	}
}
