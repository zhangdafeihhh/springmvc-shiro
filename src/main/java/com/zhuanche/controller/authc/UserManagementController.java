package com.zhuanche.controller.authc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.entity.mdbcarmanage.CarAdmUser;
import com.zhuanche.serv.authc.UserManagementService;

/**用户管理**/
@RestController
public class UserManagementController {
	@Autowired
	private UserManagementService userManagementService;
	
	/**一、增加一个用户**/
	@RequestMapping("/addUser")
	public AjaxResponse addUser(  String account, String userName, String cityIds, String supplierIds, String teamIds ) {
		CarAdmUser user  = new CarAdmUser();
		user.setAccount(account);
		user.setUserName(userName);
		user.setCities( cityIds );
		user.setSuppliers( supplierIds );
		user.setTeamId( teamIds );
		return userManagementService.addUser(user);
	}
	
	/**二、禁用一个用户**/
	@RequestMapping("/disableUser")
	public AjaxResponse disableUser ( Integer userId ) {
		return userManagementService.disableUser(userId);
	}
	
	/**三、启用一个用户**/
	@RequestMapping("/enableUser")
	public AjaxResponse enableUser ( Integer userId ) {
		return userManagementService.enableUser(userId);
	}
	
	/**四、修改一个用户**/
	@RequestMapping("/changeUser")
	public 	AjaxResponse changeUser( Integer userId, String userName, String cityIds, String supplierIds, String teamIds ) {
		CarAdmUser newUser = new CarAdmUser();
		newUser.setUserId(userId);
		newUser.setUserName(userName);
		newUser.setCities( cityIds );
		newUser.setSuppliers( supplierIds );
		newUser.setTeamId( teamIds );
		return userManagementService.changeUser(newUser);
	}
	
	/**六、查询一个用户中的角色ID**/
	@RequestMapping("/getAllRoleIds")
	public AjaxResponse getAllRoleIds( Integer userId ){
		List<Integer> roleIds = userManagementService.getAllRoleIds(userId);
		return AjaxResponse.success( roleIds  );
	}
	
	/**七、保存一个用户中的角色ID**/
	@RequestMapping("/saveRoleIds")
	public AjaxResponse saveRoleIds( Integer userId, List<Integer> roleIds) {
		//TODO
		return userManagementService.saveRoleIds(userId, roleIds);
	}
	
	/**八、查询用户列表**/
	@RequestMapping("/queryUserList")
	public AjaxResponse queryUserList( Integer page, Integer pageSize,  String account , String userName, String phone , Integer status ) {
		PageDTO pageDto = userManagementService.queryUserList(page, pageSize, account, userName, phone, status);
    	return AjaxResponse.success(pageDto);
	}

	/**九、重置密码**/
	@RequestMapping("/resetPassword")
	public AjaxResponse resetPassword( Integer userId ) {
		return userManagementService.resetPassword(userId);
	}

}