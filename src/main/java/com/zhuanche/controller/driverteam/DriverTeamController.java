package com.zhuanche.controller.driverteam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**车队管理**/
@Controller
@RequestMapping("/driverteam")
public class DriverTeamController{
	
	/**车队列表**/
	@RequestMapping("/list")
	public String list(){
		return "driverteam/driverlist";
	}

}