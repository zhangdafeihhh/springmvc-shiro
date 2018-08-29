package com.zhuanche.controller.driverjoin;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.zhuanche.entity.driver.DriverVerify;
import com.zhuanche.serv.DriverVerifyService;

/** 
 * 司机加盟注册
 * ClassName: DriverVerifyController.java 
 * Date: 2018年8月29日 
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
	 * @param params
	 * @return
	 */
	@RequestMapping("/queryDriverVerifyData")
	public Object queryDriverVerifyData(Long cityId,String supplier,String mobile,Integer verifyStatus,String createDateBegin,String createDateEnd){
		
		Map<String, Object> result = Maps.newHashMap();
		// 增加操作日志
		// 数据权限
		// 查询
		List<DriverVerify> driverVerifyList = driverVerifyService.queryDriverVerifyList(cityId, supplier, mobile, verifyStatus, createDateBegin, createDateEnd);
		result.put("result", driverVerifyList);
		result.put("isSuccess", true);
		
		return result;
		
	}
}
