package com.zhuanche.controller.authc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.common.web.Verify;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.serv.CarBizDriverInfoService;
import com.zhuanche.serv.authc.UserManagementService;

/**用户管理**/
@RestController
public class UserManagementController {
	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private CarBizDriverInfoService carBizDriverInfoService;
	
	/**一、增加一个用户**/
	@RequestMapping("/addUser")
	@RequiresPermissions(value = { "ADD_USER" } )
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
		boolean phoneExist = userManagementService.userPhoneExist(phone);
		if(phoneExist){
			return AjaxResponse.fail(RestErrorCode.PHONE_EXIST );
		}
		AjaxResponse ajaxResponse = userManagementService.addUser(user);
		if(addTelescope!=null && addTelescope.compareTo(1)==0){
			carBizDriverInfoService.addTelescopeDriver(user);
		}
		return ajaxResponse;
	}
	
	/**二、禁用一个用户**/
	@RequestMapping("/disableUser")
	@RequiresPermissions(value = { "DISABLE_USER" } )
	public AjaxResponse disableUser ( @Verify(param="userId",rule="required|min(1)") Integer userId ) {
		return userManagementService.disableUser(userId);
	}
	
	/**三、启用一个用户**/
	@RequestMapping("/enableUser")
	@RequiresPermissions(value = { "ENABLE_USER" } )
	public AjaxResponse enableUser ( @Verify(param="userId",rule="required|min(1)") Integer userId ) {
		return userManagementService.enableUser(userId);
	}
	
	/**四、修改一个用户**/
	@RequestMapping("/changeUser")
	@RequiresPermissions(value = { "CHANGE_USER" } )
	public 	AjaxResponse changeUser( 
			@Verify(param="userId",rule="required|min(1)") Integer userId, 
			@Verify(param="userName",rule="required") String userName, 
			@Verify(param="phone",rule="required|mobile") String phone, 
			@Verify(param="cityIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String cityIds, 
			@Verify(param="supplierIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String supplierIds, 
			@Verify(param="teamIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String teamIds,
			@Verify(param="groupIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String groupIds,
			Integer addTelescope) {
		CarAdmUser newUser = new CarAdmUser();
		newUser.setUserId(userId);
		newUser.setUserName(userName.trim());
		newUser.setPhone(phone);
		newUser.setCities( cityIds );
		newUser.setSuppliers( supplierIds );
		newUser.setTeamId( teamIds );
		newUser.setGroupIds(groupIds);
		if(addTelescope!=null && addTelescope.compareTo(1)==0){
			carBizDriverInfoService.addTelescopeDriver(newUser);
		}else{
			carBizDriverInfoService.disableTelescopeDriver(newUser);
		}
		return userManagementService.changeUser(newUser);
	}
	
	/**六、查询一个用户中的角色ID**/
	@RequestMapping("/getAllRoleIds")
	@RequiresPermissions(value = { "GET_ALL_ROLEIDS_OF_USER" } )
	public AjaxResponse getAllRoleIds( @Verify(param="userId",rule="required|min(1)") Integer userId ){
		List<Integer> roleIds = userManagementService.getAllRoleIds(userId);
		return AjaxResponse.success( roleIds  );
	}
	
	/**七、保存一个用户中的角色ID**/
	@RequestMapping("/saveRoleIds")
	@RequiresPermissions(value = { "SAVE_ROLEIDS_OF_USER" } )
	public AjaxResponse saveRoleIds( @Verify(param="userId",rule="required|min(1)") Integer userId,  @Verify(param="roleIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String roleIds) {
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
		return userManagementService.saveRoleIds(userId, newroleIds);
	}
	
	/**八、查询用户列表**/
	@RequestMapping("/queryUserList")
	@RequiresPermissions(value = { "UserManages_look" } )
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
	public AjaxResponse resetPassword( @Verify(param="userId",rule="required|min(1)") Integer userId ) {
		return userManagementService.resetPassword(userId);
	}

}