package com.zhuanche.controller.driverteam;

import com.alibaba.fastjson.JSON;
import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.dutyEnum.ServiceReturnCodeEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.serv.driverteam.CarDriverTeamService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import com.zhuanche.util.Check;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

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
@RequestMapping("/saas/driverTeam")
public class DriverTeamController{

    private static final Logger logger = LoggerFactory.getLogger(DriverTeamController.class);
	
	@Autowired
	private CarDriverTeamService carDriverTeamService;



	/**
	 * @Desc: 查询车队列表
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/queryDriverTeamList")
	public AjaxResponse queryDriverTeamList(DriverTeamRequest param){
		logger.info("查询车队列表入参:"+ JSON.toJSONString(param));
		SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
		if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
			return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
		}
		PageDTO pageDTO = carDriverTeamService.queryDriverTeamPage(param);
		return AjaxResponse.success(pageDTO);
	}

	/**
	 * @Desc: 新增车队
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/saveOneDriverTeam")
	public AjaxResponse saveOneDriverTeam(CarDriverTeamDTO param){
		logger.info("新增车队入参:"+ JSON.toJSONString(param));
		int result = carDriverTeamService.saveOneDriverTeam(param);
		if(result >0){
			return AjaxResponse.success(result);
		}else{
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
	}

	/**
	 * @Desc: 修改车队
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/updateOneDriverTeam")
	public AjaxResponse updateOneDriverTeam(CarDriverTeamDTO param){
		logger.info("修改车队入参:"+ JSON.toJSONString(param));
		if(Check.NuNObj(param) || Check.NuNObj(param.getId())){
			return AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
		}
		int result = carDriverTeamService.updateOneDriverTeam(param);
		if(result >0){
			return AjaxResponse.success(result);
		}else{
			return AjaxResponse.fail(RestErrorCode.UNKNOWN_ERROR);
		}
	}

	/**
	 * @Desc: 查询车队详情
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/driverTeamDetail")
	public AjaxResponse driverTeamDetail(DriverTeamRequest param){
		logger.info("查询车队详情入参:"+ JSON.toJSONString(param));
		CarDriverTeamDTO detail = carDriverTeamService.selectOneDriverTeam(param);
		return AjaxResponse.success(detail);
	}

	/**
	 * @Desc: 查询车队/小组已存在司机列表
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/queryTeamExistsDriverList")
	public AjaxResponse queryTeamExistsDriverList(DriverTeamRequest param){
		logger.info("查询车队/小组已存在司机列表入参:"+ JSON.toJSONString(param));
		SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
		if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
			return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
		}
		PageDTO pageDTO = carDriverTeamService.selectTeamExistsDriverList(param);
		return AjaxResponse.success(pageDTO);
	}

	/** 
	* @Desc: 车队/小组移除司机 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/10 
	*/ 
	@ResponseBody
	@RequestMapping(value = "/removeDriverToTeam")
	public AjaxResponse removeDriverToTeam(@Verify(param = "id", rule = "required") Integer id,@Verify(param = "driverId", rule = "required") Integer driverId){
		logger.info("车队/小组移除司机 入参:"+ "车队/小组id："+id+"要移除的司机id："+driverId);
		if(Check.NuNObj(id) || Check.NuNObj(driverId)){
			return AjaxResponse.fail(RestErrorCode.PARAMS_ERROR);
		}
		int result = carDriverTeamService.removeDriverToTeam(id, driverId);
		ServiceReturnCodeEnum typeByCode = ServiceReturnCodeEnum.getTypeByCode(result);
		if(result < 0 ){
			AjaxResponse fail = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
			Map<String,String> map = new HashedMap();
			map.put("errorMsg",typeByCode.getName());
			fail.setData(map);
			return fail;
		}else if(result == 1){
			return AjaxResponse.success(result);
		}
		return AjaxResponse.success(result);
	}

	/**
	 * @Desc: 添加司机到车队/小组
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/addDriverToTeam")
	public AjaxResponse addDriverToTeam(DriverTeamRequest param){
		logger.info("添加司机到车队/小组入参:"+ JSON.toJSONString(param));
		int result = carDriverTeamService.addDriverToTeam(param);
		ServiceReturnCodeEnum typeByCode = ServiceReturnCodeEnum.getTypeByCode(result);
		if(result < 0 ){
			AjaxResponse fail = AjaxResponse.fail(RestErrorCode.HTTP_PARAM_INVALID);
			Map<String,String> map = new HashedMap();
			map.put("errorMsg",typeByCode.getName());
			fail.setData(map);
			return fail;
		}else if(result == 1){
			return AjaxResponse.success(result);
		}else if(result == 2){
			AjaxResponse success = AjaxResponse.success(result);
			Map<String,String> map = new HashedMap();
			map.put("successMsg",typeByCode.getName());
			success.setData(map);
			return success;
		}
		return AjaxResponse.success(result);
	}

	/**
	 * @Desc: 查询可添加司机列表
	 * @param:
	 * @return:
	 * @Author: lunan
	 * @Date: 2018/9/4
	 */
	@ResponseBody
	@RequestMapping(value = "/queryAddDriverList")
	public AjaxResponse queryAddDriverList(DriverTeamRequest param){
		logger.info("查询可添加司机列表入参:"+ JSON.toJSONString(param));
		SSOLoginUser loginUser = WebSessionUtil.getCurrentLoginUser();
		if(Check.NuNObj(loginUser) || Check.NuNObj(loginUser.getId())){
			return AjaxResponse.fail(RestErrorCode.HTTP_FORBIDDEN);
		}
		PageDTO pageDTO = carDriverTeamService.selectAddDriverList(param);
		return AjaxResponse.success(pageDTO);
	}

	/** 
	* @Desc: 车队设置车队班制排班 
	* @param:
	* @return:  
	* @Author: lunan
	* @Date: 2018/9/5 
	*/ 
	@RequestMapping("/saveTeamDuty")
	@ResponseBody
	public AjaxResponse saveTeamDuty(@Verify(param = "dutyStartDate   ", rule = "required") String dutyStartDate   ,
									 @Verify(param = "dutyEndDate", rule = "required") String dutyEndDate,
									 @Verify(param = "paramId", rule = "required") Integer paramId) {
		logger.info("车队设置车队班制排班入参:"+dutyStartDate+"--"+dutyEndDate+"--"+paramId);
		CarDriverTeam record = new CarDriverTeam();
		record.setId(paramId);
		record.setDutyStartDate(dutyStartDate);
		record.setDutyEndDate(dutyEndDate);
		int result = carDriverTeamService.updateTeamDuty(record);
		if(result > 0 ){
			return AjaxResponse.success(result);
		}else{
			return AjaxResponse.fail(RestErrorCode.RECORD_DEAL_FAILURE);
		}
	}

}