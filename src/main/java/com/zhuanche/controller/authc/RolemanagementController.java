package com.zhuanche.controller.authc;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.entity.mdbcarmanage.SaasRole;
import com.zhuanche.serv.authc.RoleManagementService;

/**角色管理**/
@RestController
public class RolemanagementController {
	private RoleManagementService roleManagementService;
	
	/**一、增加一个角色**/
	@RequestMapping("/addSaasRole")
	public AjaxResponse addSaasRole( String roleCode,  String roleName) {
		SaasRole role = new SaasRole();
		role.setRoleCode(roleCode);
		role.setRoleName(roleName);
		role.setValid(true);
		return roleManagementService.addSaasRole(role);
	}

	/**二、禁用一个角色**/
	@RequestMapping("/disableSaasRole")
	public AjaxResponse disableSaasRole ( Integer roleId ) {
		return roleManagementService.disableSaasRole(roleId);
	}
	
	/**三、启用一个角色**/
	@RequestMapping("/enableSaasRole")
	public AjaxResponse enableSaasRole ( Integer roleId ) {
		return roleManagementService.enableSaasRole(roleId);
	}
	
	/**四、修改一个角色**/
	@RequestMapping("/changeRole")
	public 	AjaxResponse changeRole( Integer roleId ,String roleCode,  String roleName ) {
		SaasRole roleForupdate = new SaasRole();
		roleForupdate.setRoleId(roleId);
		roleForupdate.setRoleCode(roleCode);
		roleForupdate.setRoleName(roleName);
		return roleManagementService.changeRole(roleForupdate);
	}

	/**五、查询一个角色中的权限（返回的数据格式：列表、树形）**/
	@RequestMapping("/getAllPermissions")
	public AjaxResponse getAllPermissions( Integer roleId,  String dataFormat ){
		List<SaasPermissionDTO> permissionDtos = roleManagementService.getAllPermissions(roleId, dataFormat);
		return AjaxResponse.success(permissionDtos);
	}

	/**六、查询一个角色中的权限ID**/
	@RequestMapping("/getAllPermissionIds")
	public AjaxResponse getAllPermissionIds( Integer roleId){
		List<Integer> permissionIds = roleManagementService.getAllPermissionIds(roleId);
		return AjaxResponse.success(permissionIds);
	}
	
	/**七、保存一个角色中的权限ID**/
	@RequestMapping("/savePermissionIds")
	public AjaxResponse savePermissionIds( Integer roleId, List<Integer> permissionIds) {
		return roleManagementService.savePermissionIds(roleId, permissionIds);
	}
	
	/**八、查询角色列表**/
	@RequestMapping("/queryRoleList")
	public AjaxResponse queryRoleList( Integer page, Integer pageSize,  String roleCode , String roleName, Byte valid ) {
		PageDTO pageDto = roleManagementService.queryRoleList(page, pageSize, roleCode, roleName, valid);
		return AjaxResponse.success( pageDto );
	}
	
}