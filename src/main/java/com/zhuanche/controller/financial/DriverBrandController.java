package com.zhuanche.controller.financial;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.driver.DriverBrand;
import com.zhuanche.serv.financial.DriverBrandService;

/**  
 * ClassName:DriverBrandController <br/>  
 * Date:     2019年4月25日 下午2:55:14 <br/>  
 * @author   baiyunlong
 * @version  1.0.0      
 */
@RestController
@RequestMapping("/driverBrand")
public class DriverBrandController {
	private static final Logger logger = LoggerFactory.getLogger(DriverBrandController.class);
	@Autowired
	private DriverBrandService driverBrandService;
	
	@RequestMapping(value = "/queryDriverBrandList")
	public AjaxResponse queryDriverBrandList() {
		logger.info("DriverBrandController--queryDriverBrandList--");
		List<DriverBrand> driverBrands=driverBrandService.queryDriverBrandList();
		return AjaxResponse.success(driverBrands);
	}
}
  
