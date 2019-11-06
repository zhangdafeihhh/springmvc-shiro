package com.zhuanche.controller.authc;

import com.alibaba.fastjson.JSONObject;
import com.zhuanche.common.enums.PermissionLevelEnum;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.syslog.SysLogAnn;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.common.web.Verify;
import com.zhuanche.dto.financial.FinancialBasicsVehiclesDTO;
import com.zhuanche.dto.financial.FinancialGoodsInfoDTO;
import com.zhuanche.dto.mdbcarmanage.CarAdmUserDto;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.mongo.SysSaveOrUpdateLog;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.authc.UserManagementService;
import com.zhuanche.shiro.realm.SSOLoginUser;
import com.zhuanche.shiro.session.WebSessionUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zhuanche.common.enums.MenuEnum.*;

/**用户管理**/
@RestController
public class UserManagementController {
	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;

	@Resource(name = "userOperationLogMongoTemplate")
	private MongoTemplate mongoTemplate;
	
	/**一、增加一个用户**/
	@RequestMapping("/addUser")
	@RequiresPermissions(value = { "ADD_USER" } )
	@RequestFunction(menu = USER_ADD)
	@SysLogAnn(module="CarAdmUser",methods="addUser",parameterType="Integer",parameterKey="userId",objClass= CarAdmUserDto.class )
	public AjaxResponse addUser( 
			@Verify(param="account",rule="required|RegExp(^[a-zA-Z0-9_\\-]{3,30}$)") String account, 
			@Verify(param="userName",rule="required") String userName, 
			@Verify(param="phone",rule="required|mobile") String phone, 
			@Verify(param="cityIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String cityIds, 
			@Verify(param="supplierIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String supplierIds, 
			@Verify(param="teamIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String teamIds,
			@Verify(param="groupIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String groupIds,
			Integer addTelescope) {
		CarAdmUser user  = new CarAdmUser();
		user.setAccount(account.trim());
		user.setUserName(userName.trim());
		user.setPhone(phone);
		user.setCities( cityIds );
		user.setSuppliers( supplierIds );
		user.setTeamId( teamIds );
		user.setGroupIds(groupIds);
		if (StringUtils.isNotBlank(groupIds)){
			user.setLevel(PermissionLevelEnum.GROUP.getCode());
		}
		else if (StringUtils.isNotBlank(teamIds)){
			user.setLevel(PermissionLevelEnum.TEAM.getCode());
		}
		else if (StringUtils.isNotBlank(supplierIds)){
			user.setLevel(PermissionLevelEnum.SUPPLIER.getCode());
		}
		else if (StringUtils.isNotBlank(cityIds)){
			user.setLevel(PermissionLevelEnum.CITY.getCode());
		}
		else {
			user.setLevel(PermissionLevelEnum.ALL.getCode());
		}
		// 暂时不用
//		boolean phoneExist = userManagementService.userPhoneExist(phone);
//		if(phoneExist){
//			return AjaxResponse.fail(RestErrorCode.PHONE_EXIST );
//		}
		AjaxResponse ajaxResponse = userManagementService.addUser(user);


		return ajaxResponse;
	}
	
	/**二、禁用一个用户**/
	@RequestMapping("/disableUser")
	@RequiresPermissions(value = { "DISABLE_USER" } )
	@RequestFunction(menu = USER_DISABLE)
	@SysLogAnn(module="CarAdmUser",methods="enableUser",
			serviceClass="userManagementService",queryMethod="findByPrimaryKey",parameterType="Integer",parameterKey="userId",objClass=CarAdmUser.class ,extendParam="{status:100}")
	public AjaxResponse disableUser ( @Verify(param="userId",rule="required|min(1)") Integer userId ) {
		return userManagementService.disableUser(userId);
	}
	
	/**三、启用一个用户**/
	@RequestMapping("/enableUser")
	@RequiresPermissions(value = { "ENABLE_USER" } )
	@RequestFunction(menu = USER_ENABLE)
	@SysLogAnn(module="CarAdmUser",methods="enableUser",
			serviceClass="userManagementService",queryMethod="findByPrimaryKey",parameterType="Integer",parameterKey="userId",objClass=CarAdmUser.class ,extendParam="{status:200}")
	public AjaxResponse enableUser ( @Verify(param="userId",rule="required|min(1)") Integer userId ) {
		return userManagementService.enableUser(userId);
	}
	
	/**四、修改一个用户**/

	@RequestMapping("/changeUser")
	@RequiresPermissions(value = { "CHANGE_USER" } )
	@RequestFunction(menu = USER_UPDATE)
	@SysLogAnn(module="CarAdmUser",methods="changeUser",parameterType="Integer",parameterKey="userId",objClass= CarAdmUserDto.class ,serviceClass="userManagementService",queryMethod="findByPrimaryKeyV2")
	public 	AjaxResponse changeUser( 
			@Verify(param="userId",rule="required|min(1)") Integer userId, 
			@Verify(param="userName",rule="required") String userName, 
			@Verify(param="phone",rule="required|mobile") String phone,
			@RequestParam("cityIds") String cityIds,
			@RequestParam("supplierIds") String supplierIds,
			@RequestParam("teamIds") String teamIds,
			@RequestParam("groupIds") String groupIds) {//去掉了未使用的参数addTelescope

		CarAdmUser newUser = new CarAdmUser();
		newUser.setUserId(userId);
		newUser.setUserName(userName.trim());
		newUser.setPhone(phone);
		newUser.setCities( cityIds );
		newUser.setSuppliers( supplierIds );
		newUser.setTeamId( teamIds );
		newUser.setGroupIds(groupIds);
		return userManagementService.changeUser(newUser);
	}
	
	/**六、查询一个用户中的角色ID**/
	@RequestMapping("/getAllRoleIds")
	@RequiresPermissions(value = { "GET_ALL_ROLEIDS_OF_USER" } )
	@RequestFunction(menu = USER_ROLE_LIST)
	public AjaxResponse getAllRoleIds( @Verify(param="userId",rule="required|min(1)") Integer userId ){
		List<Integer> roleIds = userManagementService.getAllRoleIds(userId);
		return AjaxResponse.success( roleIds  );
	}
	
	/**七、保存一个用户中的角色ID**/
	@RequestMapping("/saveRoleIds")
	@RequiresPermissions(value = { "SAVE_ROLEIDS_OF_USER" } )
	@RequestFunction(menu = USER_ROLE_SAVE)
	public AjaxResponse saveRoleIds( @Verify(param="userId",rule="required|min(1)") Integer userId,  @Verify(param="roleIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String roleIds) {
		SysSaveOrUpdateLog sysLog = new SysSaveOrUpdateLog();
		sysLog.setStartTime(new Date());

		List<Integer> newroleIds = new ArrayList<Integer>();
		if(roleIds!=null) {
			String[]  ids = roleIds.split(",");
			if(ids.length>0) {
				for(String id : ids ) {
					if(StringUtils.isNotEmpty(id)) {
						try {
							newroleIds.add(Integer.valueOf(id));
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
		AjaxResponse rep = userManagementService.saveRoleIds(userId, newroleIds);


		if(rep.getCode() == 0){
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("userId",userId);
			jsonParam.put("roleIds",roleIds);

			SSOLoginUser user = WebSessionUtil.getCurrentLoginUser();
			sysLog.setUsername(user.getName());
			sysLog.setModule("CarAdmUser");
			sysLog.setMethod("saveRoleIds");
			sysLog.setOperateParams(jsonParam.toJSONString());
			sysLog.setLogKey(userId+"");
			sysLog.setEndTime(new Date());
			sysLog.setResultMsg("成功");
			sysLog.setResultStatus(1);


			mongoTemplate.insert(sysLog);
		}



		return rep;
	}
	
	/**八、查询用户列表**/
	@RequestMapping("/queryUserList")
	@RequiresPermissions(value = { "UserManages_look" } )
	@RequestFunction(menu = USER_LIST)
	public AjaxResponse queryUserList( 
			@Verify(param="page",rule="required|min(1)") Integer page, 
			@Verify(param="pageSize",rule="required|min(10)") Integer pageSize,  
			String account , 
			String userName, 
			@Verify(param="phone",rule="mobile") String phone , 
			Integer status ,
			Integer roleId 
			) {
		PageDTO pageDto = userManagementService.queryUserList(page, pageSize, roleId, account, userName, phone, status);
    	return AjaxResponse.success(pageDto);
	}

	/**九、重置用户密码**/
	@RequestMapping("/resetPassword")
	@RequiresPermissions(value = { "RESET_USER_PASSWORD" } )
	@RequestFunction(menu = USER_RESET_PASSWORD)
	public AjaxResponse resetPassword( @Verify(param="userId",rule="required|min(1)") Integer userId ) {
		return userManagementService.resetPassword(userId);
	}

}