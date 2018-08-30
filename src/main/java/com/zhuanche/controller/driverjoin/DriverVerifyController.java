package com.zhuanche.controller.driverjoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.driver.DriverVerifyDto;
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
	@ResponseBody
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
	
	/**查询司机加盟注册信息通过司机ID**/
	@RequestMapping(value = "/queryDriverVerifyById")
	@ResponseBody
	public AjaxResponse queryDriverVerifyById(@Verify(param="driverId", rule = "required")Long driverId){
		
		DriverVerifyDto driverDto = null;
		try {
			driverDto = driverVerifyService.queryDriverVerifyById(driverId);
		} catch (Exception e) {
			logger.error("查询司机加盟注册信息通过司机ID异常",e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success(driverDto);
	}
	
	/**查询司机证件照片通过司机ID和证件照片类型**/
	@RequestMapping(value = "/queryImageByDriverIdAndType")
	@ResponseBody
	public AjaxResponse queryImageByDriverIdAndType(@Verify(param="driverId", rule = "required")Long driverId,@Verify(param="type", rule = "required")Integer type){
		
		String image = "";
		try {
			image = driverVerifyService.queryImageByDriverIdAndType(driverId,type);
		} catch (Exception e) {
			logger.error("查询司机证件照片通过司机ID和证件照片类型异常,driverId="+driverId,e);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success(image);
	}
	
}
