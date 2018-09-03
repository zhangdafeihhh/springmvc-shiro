package com.zhuanche.controller.driverteam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: 车队设置
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author lunan
 * @version 1.0
 * @since 1.0
 * @create: 2018-08-29 16:54
 *
 */
@Controller
@RequestMapping("/driverteam")
public class DriverTeamController{

    private static final Logger logger = LoggerFactory.getLogger(DriverTeamController.class);
	
	/**车队列表**/
	@RequestMapping("/list")
	public String list(){
		return "driverteam/driverlist";
	}



}