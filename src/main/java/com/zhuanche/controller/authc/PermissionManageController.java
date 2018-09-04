package com.zhuanche.controller.authc;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.entity.mdbcarmanage.SaasPermission;
import com.zhuanche.serv.authc.PermissionManagementService;

/**权限管理**/
@RestController
public class PermissionManageController {
	private PermissionManagementService permissionManagementService;
	
	/**一、增加一个权限**/
	@RequestMapping("addSaasPermission")
//	@RequiresPermissions(value = { "ADD_SAAS_PERMISSION" } )
	public AjaxResponse addSaasPermission( 
			@Verify(param="parentPermissionId",rule="required|min(0)")  Integer parentPermissionId, 
			@Verify(param="permissionCode",rule="required") String permissionCode, 
			@Verify(param="permissionType",rule="required") Byte permissionType, 
			String menuUrl, 
			Byte menuOpenMode ) {
		SaasPermission pemission =  new SaasPermission();
		pemission.setParentPermissionId(parentPermissionId);
		pemission.setPermissionCode(permissionCode.trim());
		pemission.setPermissionType(permissionType);
		pemission.setMenuUrl(menuUrl==null? "": menuUrl.trim());
		pemission.setMenuOpenMode(menuOpenMode);
		return permissionManagementService.addSaasPermission(pemission);
	}
	
	/**二、禁用一个权限**/
	@RequestMapping("disableSaasPermission")
//	@RequiresPermissions(value = { "DISABLE_SAAS_PERMISSION" } )
	public AjaxResponse disableSaasPermission (@Verify(param="permissionId",rule="required|min(1)") Integer permissionId ) {
		return permissionManagementService.disableSaasPermission(permissionId);
	}
	
	/**三、启用一个权限**/
	@RequestMapping("enableSaasPermission")
//	@RequiresPermissions(value = { "ENABLE_SAAS_PERMISSION" } )
	public AjaxResponse enableSaasPermission (@Verify(param="permissionId",rule="required|min(1)")  Integer permissionId ) {
		return permissionManagementService.enableSaasPermission(permissionId);
	}
	
	/**四、修改一个权限**/
	@RequestMapping("changeSaasPermission")
//	@RequiresPermissions(value = { "CHANGE_SAAS_PERMISSION" } )
	public 	AjaxResponse changeSaasPermission(  
			@Verify(param="permissionId",rule="required|min(1)")  Integer permissionId, 
			@Verify(param="permissionCode",rule="required") String permissionCode, 
			@Verify(param="permissionType",rule="required") Byte permissionType, 
			String menuUrl, 
			Byte menuOpenMode ) {
		SaasPermission pemissionForupdate = new SaasPermission();
		pemissionForupdate.setPermissionId(permissionId);
		pemissionForupdate.setPermissionCode(permissionCode.trim());
		pemissionForupdate.setPermissionType(permissionType);
		pemissionForupdate.setMenuUrl(menuUrl==null? "": menuUrl.trim());
		pemissionForupdate.setMenuOpenMode(menuOpenMode);
		return permissionManagementService.changeSaasPermission(pemissionForupdate);
	}

	/**五、查询所有的权限信息（返回的数据格式：列表、树形）**/
	@RequestMapping("getAllSaasPermissionsInfo")
//	@RequiresPermissions(value = { "GET_ALL_SAAS_PERMISSIONS_INFO" } )
	public AjaxResponse getAllSaasPermissionsInfo( String dataFormat ){
		if( !SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(dataFormat) && !SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(dataFormat) ) {
			dataFormat = SaasConst.PermissionDataFormat.TREE;//默认为树形
		}
		List<SaasPermissionDTO> allDtos = permissionManagementService.getAllPermissions(dataFormat);
		return AjaxResponse.success(allDtos);
	}
	
}