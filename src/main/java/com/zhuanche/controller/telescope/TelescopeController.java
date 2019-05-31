package com.zhuanche.controller.telescope;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.rocketmq.CommonRocketProducer;
import com.zhuanche.common.sms.SmsSendUtil;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public AjaxResponse queryTelescopeUserForList(Integer cityId, Integer supplierId, Integer teamId, Integer teamGroupId ,
												  Integer status, String phone, Integer driverStatus,
												  @RequestParam(value="page", defaultValue="0")Integer page,
												  @Verify(param = "pageSize",rule = "max(50)")@RequestParam(value="pageSize", defaultValue="20")Integer pageSize){
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
										 String dataCityIds, String dataSupplierIds, String dataTeamIds, String dataGrupIds ) throws Exception{
		Map<String, Object> params = new HashMap<String, Object>();
		if(null == driverId){
			/**创建司机账号**/
			TelescopeDriver telescopeDriver = new TelescopeDriver();
			telescopeDriver.setPhone(phone);
			telescopeDriver.setName(name);
			telescopeDriver.setDataCityIds(dataCityIds);
			CarBizDriverInfoDTO carBizDriverInfoDTO = carBizDriverInfoService.addTelescopeDriver(telescopeDriver);
			if(null == carBizDriverInfoDTO){
				return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
			}
			params.put("cityId",carBizDriverInfoDTO.getServiceCity());
			params.put("supplierId",carBizDriverInfoDTO.getSupplierId());
			params.put("driverId",carBizDriverInfoDTO.getDriverId());
		}else{
			params.put("cityId",cityId);
			params.put("supplierId",supplierId);
			params.put("driverId",driverId);
		}
		params.put("driverStatus",1);
		params.put("name",name);
		params.put("phone",phone);
		params.put("teamId",teamId);
		params.put("groupId",teamGroupId);
		params.put("status",1);
		params.put("dataCityIds",dataCityIds);
		params.put("dataSupplierIds",dataSupplierIds);
		params.put("dataTeamIds",dataTeamIds);
		params.put("dataGrupIds",dataGrupIds);
		params.put("createBy", WebSessionUtil.getCurrentLoginUser().getName());
		if (StringUtils.isNotBlank(dataGrupIds)){
			params.put("level",PermissionLevelEnum.GROUP.getCode());
		}else if (StringUtils.isNotBlank(dataTeamIds)){
			params.put("level",PermissionLevelEnum.TEAM.getCode());
		}else if(StringUtils.isNotBlank(dataSupplierIds)){
			params.put("level",PermissionLevelEnum.SUPPLIER.getCode());
		}else if(StringUtils.isNotBlank(dataCityIds)){
			params.put("level",PermissionLevelEnum.CITY.getCode());
		}else {
			params.put("level",PermissionLevelEnum.ALL.getCode());
		}
		JSONObject result = MpOkHttpUtil.okHttpPostBackJson(mpManageRestUrl + "/telescope/addTelescopeUser", params, 1, "新增千里眼权限用户");
		logger.info("【新增千里眼权限用户】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") == Constants.SUCCESS_CODE) {
			return AjaxResponse.success(null);
		}
		String errorMsg = result.getString("msg");
		logger.info("【新增千里眼权限用户】接口出错,params={},errorMsg={}", params, errorMsg);
		if(result.getIntValue("code") == 1){
			return AjaxResponse.fail(RestErrorCode.DRIVER_ACCOUNT_APPLY_EXIST,errorMsg);
		}else{
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
	}
	
	/**修改千里眼用户权限**/
	@RequestMapping("/updateTelescopeUser")
	@ResponseBody
    public AjaxResponse updateTelescopeUser(Integer driverId, String dataCityIds, String dataSupplierIds, String dataTeamIds, String dataGroupIds, Integer level){
		Map<String, Object> params = new HashMap<String, Object>();
		if(driverId == null){
			return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
		}
		params.put("driverId",driverId);
		params.put("dataCityIds",dataCityIds);
		params.put("dataSupplierIds",dataSupplierIds);
		params.put("dataTeamIds",dataTeamIds);
		params.put("level",level);
		params.put("dataGroupIds",dataGroupIds);
		params.put("updateBy", WebSessionUtil.getCurrentLoginUser().getName());
		JSONObject result = MpOkHttpUtil.okHttpPostBackJson(mpManageRestUrl + "/telescope/updateTelescopeUser", params, 1, "修改千里眼权限状态");
		logger.info("【修改千里眼用户权限】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
			String errorMsg = result.getString("msg");
			logger.info("【修改千里眼用户权限】接口出错,params={},errorMsg={}", params, errorMsg);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success(null);
	}

	@RequestMapping("/queryTelescopeUser")
	@ResponseBody
    public AjaxResponse queryTelescopeUser(Integer driverId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("driverId",driverId);
		JSONObject result = MpOkHttpUtil.okHttpGetBackJson(mpManageRestUrl + "/telescope/queryTelescopeUser", params, 1, "查询千里眼用户信息");
		logger.info("【查询千里眼用户信息】接口返回结果：{}",result.toJSONString());
		if (result.getIntValue("code") != Constants.SUCCESS_CODE) {
			String errorMsg = result.getString("msg");
			logger.info("【查询千里眼用户信息】接口出错,params={},errorMsg={}", params, errorMsg);
			return AjaxResponse.fail(RestErrorCode.HTTP_SYSTEM_ERROR);
		}
		return AjaxResponse.success( result.get("data") );
	}

	
}