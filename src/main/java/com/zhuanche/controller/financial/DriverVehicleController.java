package com.zhuanche.controller.financial;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.driver.DriverBrand;
import com.zhuanche.entity.driver.DriverVehicle;
import com.zhuanche.serv.financial.DriverVehicleService;

/**  
 * ClassName:DriverVehicleController <br/>  
 * Date:     2019年4月25日 下午2:58:13 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@RestController
@RequestMapping("/driverVehicle")
public class DriverVehicleController {
	private static final Logger logger = LoggerFactory.getLogger(DriverVehicleController.class);
	@Autowired
	private DriverVehicleService driverVehicleService;
	
	@RequestMapping(value = "/queryDriverVehicleList")
	public AjaxResponse queryDriverVehicleList(Integer brandId) {
		logger.info("请求--DriverVehicleController--方法queryDriverVehicleList--参数--");
		List<DriverVehicle> driverVehicles=driverVehicleService.queryDriverVehicleList(brandId);
		return AjaxResponse.success(driverVehicles);
	}
}
  
