package com.zhuanche.controller.authc;

import java.util.List;

import com.zhuanche.common.web.RequestFunction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.Verify;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.entity.mdbcarmanage.SaasPermission;
import com.zhuanche.serv.authc.PermissionManagementService;

import static com.zhuanche.common.enums.MenuEnum.*;

/**权限管理**/
@RestController
public class PermissionManageController {
	@Autowired
	private PermissionManagementService permissionManagementService;
	
	/**一、增加一个权限**/
	@RequestMapping("addSaasPermission")
	@RequiresPermissions(value = { "ADD_SAAS_PERMISSION" } )
	@RequestFunction(menu = PERMISSION_ADD)
	public AjaxResponse addSaasPermission( 
			@Verify(param="parentPermissionId",rule="required|min(0)")  Integer parentPermissionId, 
			@Verify(param="permissionName",rule="required") String permissionName, 
			@Verify(param="permissionCode",rule="required") String permissionCode, 
			@Verify(param="permissionType",rule="required") Byte permissionType, 
			String menuUrl, 
			Byte menuOpenMode ) {
		SaasPermission pemission =  new SaasPermission();
		pemission.setParentPermissionId(parentPermissionId);
		pemission.setPermissionName(permissionName);
		pemission.setPermissionCode(permissionCode.trim());
		pemission.setPermissionType(permissionType);
		pemission.setMenuUrl(menuUrl==null? "": menuUrl.trim());
		pemission.setMenuOpenMode(menuOpenMode);
		return permissionManagementService.addSaasPermission(pemission);
	}
	
	/**二、禁用一个权限**/
	@RequestMapping("disableSaasPermission")
	@RequiresPermissions(value = { "DISABLE_SAAS_PERMISSION" } )
	@RequestFunction(menu = PERMISSION_DISABLE)
	public AjaxResponse disableSaasPermission (@Verify(param="permissionId",rule="required|min(1)") Integer permissionId ) {
		return permissionManagementService.disableSaasPermission(permissionId);
	}
	
	/**三、启用一个权限**/
	@RequestMapping("enableSaasPermission")
	@RequiresPermissions(value = { "ENABLE_SAAS_PERMISSION" } )
	@RequestFunction(menu = PERMISSION_ENABLE)
	public AjaxResponse enableSaasPermission (@Verify(param="permissionId",rule="required|min(1)")  Integer permissionId ) {
		return permissionManagementService.enableSaasPermission(permissionId);
	}
	
	/**四、修改一个权限**/
	@RequestMapping("changeSaasPermission")
	@RequiresPermissions(value = { "CHANGE_SAAS_PERMISSION" } )
	@RequestFunction(menu = PERMISSION_UPDATE)
	public 	AjaxResponse changeSaasPermission(  
			@Verify(param="permissionId",rule="required|min(1)")  Integer permissionId, 
			@Verify(param="permissionName",rule="required") String permissionName, 
			@Verify(param="permissionCode",rule="required") String permissionCode, 
			@Verify(param="permissionType",rule="required") Byte permissionType, 
			String menuUrl, 
			Byte menuOpenMode ) {
		SaasPermission pemissionForupdate = new SaasPermission();
		pemissionForupdate.setPermissionId(permissionId);
		pemissionForupdate.setPermissionName(permissionName);
		pemissionForupdate.setPermissionCode(permissionCode.trim());
		pemissionForupdate.setPermissionType(permissionType);
		pemissionForupdate.setMenuUrl(menuUrl==null? "": menuUrl.trim());
		pemissionForupdate.setMenuOpenMode(menuOpenMode);
		return permissionManagementService.changeSaasPermission(pemissionForupdate);
	}

	/**五、查询所有的权限信息（返回的数据格式：列表、树形）**/
	@RequestMapping("getAllSaasPermissionsInfo")
	@RequiresPermissions(value = { "CammelCase_look" } )
	@RequestFunction(menu = PERMISSION_LIST)
	public AjaxResponse getAllSaasPermissionsInfo( String dataFormat ){
		if( !SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(dataFormat) && !SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(dataFormat) ) {
			dataFormat = SaasConst.PermissionDataFormat.TREE;//默认为树形
		}
		List<SaasPermissionDTO> allDtos = permissionManagementService.getAllPermissions(dataFormat);
		return AjaxResponse.success(allDtos);
	}
	
	/**六、删除一个权限（只能开发人员用）**/
	@RequestMapping("deleteSaasPermission")
	@RequestFunction(menu = PERMISSION_DELETE)
	public AjaxResponse deleteSaasPermission( @Verify(param="permissionId",rule="required|min(1)")  Integer permissionId ){
		return permissionManagementService.deleteSaasPermission(permissionId);
	}
}