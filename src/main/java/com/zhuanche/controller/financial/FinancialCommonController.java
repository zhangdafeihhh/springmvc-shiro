package com.zhuanche.controller.financial;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.financial.MileageEnum;
import com.zhuanche.constants.financial.VehicleAgeEnum;
import com.zhuanche.dto.financial.CarColorList;
import com.zhuanche.serv.busManage.FileUploadService;
import com.zhuanche.serv.busManage.FileUploadService.UploadResult;

/**
 * ClassName:FinancialCommonController <br/>
 * Date: 2019年4月28日 下午8:09:18 <br/>
 * 
 * @author baiyunlong
 * @version 1.0.0
 */
@RestController
@RequestMapping("/financial")
public class FinancialCommonController {
	private static final Logger logger = LoggerFactory.getLogger(FinancialCommonController.class);

	@Autowired
	private FileUploadService fileUploadService;

	/**
	 * getVehicleAgeList:(). <br/>
	 * 
	 * @author baiyunlong
	 * @return
	 */
	@RequestMapping(value = "/getVehicleAgeList")
	public AjaxResponse getVehicleAgeList() {
		String vehicleAge = VehicleAgeEnum.toJson();
		return AjaxResponse.success(vehicleAge);
	}

	/**
	 * getVehicleAgeList:(). <br/>
	 * 
	 * @author baiyunlong
	 * @return
	 */
	@RequestMapping(value = "/getMileageList")
	public AjaxResponse getMileageList() {
		String mileage = MileageEnum.toJson();
		return AjaxResponse.success(mileage);
	}

	@RequestMapping(value = "/getcolorlist")
	public AjaxResponse getColorlist() {
		logger.info("请求到达，获取颜色列表");
		try {
			return AjaxResponse.success(CarColorList.getColorList());
		} catch (Exception e) {
			logger.error("获取颜色列表异常", e);
			return AjaxResponse.fail(1);
		}
	}

	@RequestMapping(value = "/fileUpload")
	public AjaxResponse fileUpload(MultipartFile file) {
		try {
			if (file==null) {
				return AjaxResponse.fail(RestErrorCode.CORRECT_FORMAT);
			}
			 String type=null;// 文件类型
			 String fileName=file.getOriginalFilename();// 文件原名称
			 type=fileName.indexOf(".")!=-1?fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()):null;
			 
			if (!"GIF".equals(type.toUpperCase())&&!"PNG".equals(type.toUpperCase())&&!"JPG".equals(type.toUpperCase())) {
				return AjaxResponse.fail(RestErrorCode.CORRECT_FORMAT);
			}
		    UploadResult result = fileUploadService.uploadPublicStream(file.getInputStream(), fileName);
		    return AjaxResponse.success(result);
		} catch (IOException e) {
			return AjaxResponse.fail(1);
		}
	}

}
