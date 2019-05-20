package com.zhuanche.controller.telescope;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constant.Constants;
import com.zhuanche.dto.driver.TelescopeDriver;
import com.zhuanche.dto.rentcar.CarBizDriverInfoDTO;
import com.zhuanche.entity.mdbcarmanage.DriverTelescopeUser;
import com.zhuanche.entity.rentcar.CarBizCity;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import com.zhuanche.entity.rentcar.CarBizSupplier;
import com.zhuanche.http.MpOkHttpUtil;
import com.zhuanche.mongo.DriverMongo;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Md5Util;
import com.zhuanche.vo.telescope.TelescopeUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**用户登录相关的功能**/
@Controller
@RequestMapping("/telescope")
public class TelescopeController {

	private static final Logger logger  =  LoggerFactory.getLogger(TelescopeController.class);

	@Value("${mp-manage-rest.url}")
	String mpManageRestUrl;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	/**查询千里眼用户列表**/
	@RequestMapping(value = "/queryTelescopeUserForList" )
	@ResponseBody
    public AjaxResponse queryTelescopeUserForList( Integer cityId, Integer supplierId, Integer teamId, Integer teamGroupId ,
												   Integer status, String phone,Integer driverStatus, Integer page, Integer pageSize){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cityId",cityId);
		params.put("supplierId",supplierId);
		params.put("teamId",teamId);
		params.put("groupId",teamGroupId);
		params.put("status",status);
		params.put("phone",phone);
		params.put("driverStatus",driverStatus);
		params.put("page",page);
		params.put("pageSize",pageSize);
		JSONObject result = MpOkHttpUtil.okHttpGetBackJson(mpManageRestUrl + "/telescope/queryTelescopeUserForList", params, 1, "查询千里眼用户列表");
		logger.info("【查询千里眼用户列表】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
			String errorMsg = result.getString("msg");
			logger.info("【查询千里眼用户列表】接口出错,params={},errorMsg={}", params, errorMsg);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success( result.get("data") );
	}
	
	/**修改千里眼权限状态**/
	@RequestMapping(value = "/updateTelescopeStatus" )
	@ResponseBody
    public AjaxResponse updateTelescopeStatus(Integer driverId, Integer status) throws IOException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("driverId",driverId);
		params.put("status",status);
		params.put("updateBy", WebSessionUtil.getCurrentLoginUser().getName());
		JSONObject result = MpOkHttpUtil.okHttpPostBackJson(mpManageRestUrl + "/telescope/updateTelescopeUser", params, 1, "修改千里眼权限状态");
		logger.info("【修改千里眼权限状态】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
			String errorMsg = result.getString("msg");
			logger.info("【修改千里眼权限状态】接口出错,params={},errorMsg={}", params, errorMsg);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
//		return AjaxResponse.success( result.get("data") );
		return AjaxResponse.success(null);
    }
	
	/**新增千里眼用户权限**/
	@RequestMapping("/addTelescopeUser")
	@ResponseBody
    public AjaxResponse addTelescopeUser( Integer driverId, String name, String phone, String idCardNo, Integer cityId, Integer supplierId, Integer teamId, Integer teamGroupId ,
										 String dataCityIds, String dataSupplierIds, String teamIds, String teamGrupIds ) throws Exception{
		if(null == driverId){
			/**创建司机账号**/
			TelescopeDriver telescopeDriver = new TelescopeDriver();
			telescopeDriver.setPhone(phone);
			CarBizDriverInfoDTO carBizDriverInfoDTO = carBizDriverInfoService.addTelescopeDriver(telescopeDriver);
			if(null == carBizDriverInfoDTO){
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("driverId",driverId);
		params.put("driverStatus",1);
		params.put("name",name);
		params.put("phone",phone);
		params.put("cityId",cityId);
		params.put("supplierId",supplierId);
		params.put("teamId",teamId);
		params.put("groupId",teamGroupId);
		params.put("status",1);
		params.put("dataCityIds",dataCityIds);
		params.put("dataSupplierIds",dataSupplierIds);
		params.put("teamIds",teamIds);
		params.put("teamGrupIds",teamGrupIds);
		params.put("createBy", WebSessionUtil.getCurrentLoginUser().getName());
		JSONObject result = MpOkHttpUtil.okHttpPostBackJson(mpManageRestUrl + "/telescope/addTelescopeUser", params, 1, "新增千里眼权限用户");
		logger.info("【新增千里眼权限用户】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
			String errorMsg = result.getString("msg");
			logger.info("【新增千里眼权限用户】接口出错,params={},errorMsg={}", params, errorMsg);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success(null);
	}
	
	/**修改千里眼用户权限**/
	@RequestMapping("/updateTelescopeUser")
	@ResponseBody
    public AjaxResponse updateTelescopeUser(Integer driverId, String dataCityIds, String dataSupplierIds, String teamIds, String teamGrupIds){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("driverId",driverId);
		params.put("dataCityIds",dataCityIds);
		params.put("dataSupplierIds",dataSupplierIds);
		params.put("teamIds",teamIds);
		params.put("teamGrupIds",teamGrupIds);
		params.put("updateBy", WebSessionUtil.getCurrentLoginUser().getName());
		JSONObject result = MpOkHttpUtil.okHttpPostBackJson(mpManageRestUrl + "/telescope/updateTelescopeUser", params, 1, "修改千里眼权限状态");
		logger.info("【修改千里眼用户权限】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
			String errorMsg = result.getString("msg");
			logger.info("【修改千里眼用户权限】接口出错,params={},errorMsg={}", params, errorMsg);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
//		return AjaxResponse.success( result.get("data") );
		return AjaxResponse.success(null);
	}

	@RequestMapping("/queryTelescopeUser")
	@ResponseBody
    public AjaxResponse queryTelescopeUser(){
		TelescopeUserVo telescopeUserVo = new TelescopeUserVo();
		telescopeUserVo.setDriverId(1000000);
		telescopeUserVo.setName("于师傅");
		telescopeUserVo.setPhone("13666666666");
		telescopeUserVo.setDriverStatus(1);
		telescopeUserVo.setStatus(1);
		telescopeUserVo.setCityId(44);
		telescopeUserVo.setCityName("北京");
		telescopeUserVo.setSupplierId(111);
		telescopeUserVo.setSupplierName("测试供应商");
		telescopeUserVo.setTeamId(111);
		telescopeUserVo.setTeamName("测试车队");
		telescopeUserVo.setTeamGroupId(111);
		telescopeUserVo.setTeamGroupName("测试小组");
		telescopeUserVo.setPermissionCityIds("44");
		telescopeUserVo.setPermissionSupplierIds("111");
		telescopeUserVo.setPermissionTeamIds("111");
		telescopeUserVo.setPermissionTeamGroupIds("111");
		return AjaxResponse.success(telescopeUserVo);
	}

	
}