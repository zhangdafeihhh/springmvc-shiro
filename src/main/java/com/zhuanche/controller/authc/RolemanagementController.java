package com.zhuanche.controller.authc;

import java.util.ArrayList;
import java.util.List;

import com.zhuanche.common.syslog.SysLogAnn;
import com.zhuanche.common.web.RequestFunction;
import com.zhuanche.dto.mdbcarmanage.CarAdmUserDto;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.entity.mdbcarmanage.SaasRole;
import com.zhuanche.serv.authc.RoleManagementService;

import static com.zhuanche.common.enums.MenuEnum.*;

/**角色管理**/
@RestController
public class RolemanagementController{
	@Autowired
	private RoleManagementService roleManagementService;
	
	/**一、增加一个角色**/
	@RequestMapping("/addSaasRole")
	@RequiresPermissions(value = { "ADD_SAAS_ROLE" } )
	@RequestFunction(menu = ROLE_ADD)
	@SysLogAnn(module="RoleManage",methods="addSaasRole",parameterType="Integer",parameterKey="roleId",objClass= SaasRole.class )
	public AjaxResponse addSaasRole( @Verify(param="roleCode",rule="required") String roleCode,  @Verify(param="roleName",rule="required") String roleName) {
		SaasRole role = new SaasRole();
		role.setRoleCode(roleCode.trim());
		role.setRoleName(roleName.trim());
		role.setValid(true);
		return roleManagementService.addSaasRole(role);
	}

	/**二、禁用一个角色**/
	@RequestMapping("/disableSaasRole")
	@RequiresPermissions(value = { "DISABLE_SAAS_ROLE" } )
	@RequestFunction(menu = ROLE_DISABLE)
	@SysLogAnn(module="RoleManage",methods="disableSaasRole",parameterType="Integer",parameterKey="roleId",objClass= SaasRole.class )
	public AjaxResponse disableSaasRole ( @Verify(param="roleId",rule="required|min(1)") Integer roleId ) {
		return roleManagementService.disableSaasRole(roleId);
	}
	
	/**三、启用一个角色**/
	@RequestMapping("/enableSaasRole")
	@RequiresPermissions(value = { "ENABLE_SAAS_ROLE" } )
	@RequestFunction(menu = ROLE_ENABLE)
	@SysLogAnn(module="RoleManage",methods="enableSaasRole",parameterType="Integer",parameterKey="roleId",objClass= SaasRole.class )
	public AjaxResponse enableSaasRole ( @Verify(param="roleId",rule="required|min(1)") Integer roleId ) {
		return roleManagementService.enableSaasRole(roleId);
	}
	
	/**四、修改一个角色**/
	@RequestMapping("/changeRole")
	@RequiresPermissions(value = { "CHANGE_SAAS_ROLE" } )
	@RequestFunction(menu = ROLE_UPDATE)
	@SysLogAnn(module="RoleManage",methods="changeSaasRole",parameterType="Integer",parameterKey="roleId",objClass= SaasRole.class )
	public 	AjaxResponse changeRole( @Verify(param="roleId",rule="required|min(1)") Integer roleId , @Verify(param="roleCode",rule="required")  String roleCode,  @Verify(param="roleName",rule="required") String roleName ) {
		SaasRole roleForupdate = new SaasRole();
		roleForupdate.setRoleId(roleId);
		roleForupdate.setRoleCode(roleCode.trim());
		roleForupdate.setRoleName(roleName.trim());
		return roleManagementService.changeRole(roleForupdate);
	}

	/**五、查询一个角色中的权限（返回的数据格式：列表、树形）**/
	@RequestMapping("/getAllPermissions")
	@RequiresPermissions(value = { "GET_ALL_ROLE_PERMISSIONS" } )
	@RequestFunction(menu = ROLE_PERMISSION_LIST)
	public AjaxResponse getAllPermissions( @Verify(param="roleId",rule="required|min(1)") Integer roleId,  String dataFormat ){
		if( !SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(dataFormat) && !SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(dataFormat) ) {
			dataFormat = SaasConst.PermissionDataFormat.TREE;//默认为树形
		}
		List<SaasPermissionDTO> permissionDtos = roleManagementService.getAllPermissions(roleId, dataFormat);
		return AjaxResponse.success(permissionDtos);
	}

	/**六、查询一个角色中的权限ID**/
	@RequestMapping("/getAllPermissionIds")
	@RequiresPermissions(value = { "GET_PERMISSIONIDS_OF_ROLE" } )
	@RequestFunction(menu = ROLE_PERMISSION_IDS)
	public AjaxResponse getAllPermissionIds( @Verify(param="roleId",rule="required|min(1)") Integer roleId){
		List<Integer> permissionIds = roleManagementService.getAllPermissionIds(roleId);
		return AjaxResponse.success(permissionIds);
	}
	
	/**七、保存一个角色中的权限ID**/
	@RequestMapping("/savePermissionIds")
	@RequiresPermissions(value = { "SAVE_ROLE_PERMISSIONIDS" } )
	@RequestFunction(menu = ROLE_PERMISSION_SAVE)
	@SysLogAnn(module="RoleManage",methods="savePermissionIds",parameterType="Integer",parameterKey="roleId",objClass= SaasRole.class )
	public AjaxResponse savePermissionIds(@Verify(param="roleId",rule="required|min(1)") Integer roleId, @Verify(param="permissionIds",rule="RegExp(^([0-9]+,)*[0-9]+$)") String permissionIds) {
		List<Integer> newPermissionIds = new ArrayList<Integer>();
		if(StringUtils.isNotEmpty(permissionIds) ) {
			String[]  ids = permissionIds.split(",");
			if(ids.length>0) {
				for(String id : ids ) {
					if(StringUtils.isNotEmpty(id)) {
						try {
							newPermissionIds.add(Integer.valueOf(id));
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
		return roleManagementService.savePermissionIds(roleId, newPermissionIds);
	}
	
	/**八、查询角色列表**/
	@RequestMapping("/queryRoleList")
	@RequiresPermissions(value = { "RoleManages_look" } )
	@RequestFunction(menu = ROLE_LIST)
	public AjaxResponse queryRoleList( 
			@Verify(param="page",rule="required|min(1)") Integer page, 
			@Verify(param="pageSize",rule="required|min(10)") Integer pageSize,  
			String roleCode , 
			String roleName, 
			Byte valid ) {
		PageDTO pageDto = roleManagementService.queryRoleList(page, pageSize, roleCode, roleName, valid);
		return AjaxResponse.success( pageDto );
	}
	
	/**九、删除一个角色（只能开发人员用）**/
	@RequestMapping("/deleteSaasRole")
	@RequestFunction(menu = ROLE_DELETE)
	public AjaxResponse deleteSaasRole ( @Verify(param="roleId",rule="required|min(1)") Integer roleId ) {
		return roleManagementService.deleteSaasRole(roleId);
	}
}