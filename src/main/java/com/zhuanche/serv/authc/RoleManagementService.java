package com.zhuanche.serv.authc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zhuanche.common.paging.PageDTO;
import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.dto.SaasRoleDTO;
import com.zhuanche.entity.mdbcarmanage.SaasPermission;
import com.zhuanche.entity.mdbcarmanage.SaasRole;
import com.zhuanche.entity.mdbcarmanage.SaasRolePermissionRalation;
import com.zhuanche.shiro.session.RedisSessionDAO;
import com.zhuanche.util.BeanUtil;

import mapper.mdbcarmanage.SaasRoleMapper;
import mapper.mdbcarmanage.ex.SaasPermissionExMapper;
import mapper.mdbcarmanage.ex.SaasRoleExMapper;
import mapper.mdbcarmanage.ex.SaasRolePermissionRalationExMapper;

/**角色管理功能**/
@Service
public class RoleManagementService{
	@Autowired
	private SaasRoleMapper     saasRoleMapper;
	@Autowired
	private SaasRoleExMapper saasRoleExMapper;
	@Autowired
	private SaasRolePermissionRalationExMapper saasRolePermissionRalationExMapper;
	@Autowired
	private SaasPermissionExMapper  saasPermissionExMapper;
	@Autowired
	private RedisSessionDAO              redisSessionDAO;
	
	/**一、增加一个角色**/
	public AjaxResponse addSaasRole( SaasRole role ) {
		//角色代码已经存在
		List<SaasRole> roles = saasRoleExMapper.queryRoles(null, role.getRoleCode(), null, null);
		if(roles!=null && roles.size()>0) {
			return AjaxResponse.fail(RestErrorCode.ROLE_CODE_EXIST );
		}
		//保存
		role.setValid(true);
		saasRoleMapper.insertSelective(role);
		return AjaxResponse.success( null );
	}

	/**二、禁用一个角色**/
	public AjaxResponse disableSaasRole ( Integer roleId ) {
		//角色不存在
		SaasRole role = saasRoleMapper.selectByPrimaryKey(roleId);
		if( role==null ) {
			return AjaxResponse.fail(RestErrorCode.ROLE_NOT_EXIST );
		}
		//系统预置角色，不能被禁用
		if( SaasConst.SYSTEM_ROLE.equalsIgnoreCase(role.getRoleCode()) ) {
			return AjaxResponse.fail(RestErrorCode.SYSTEM_ROLE_CANOT_CHANGE , role.getRoleCode() );
		}
		//执行
		SaasRole roleForUpdate = new SaasRole();
		roleForUpdate.setRoleId(roleId);
		roleForUpdate.setValid(false);
		saasRoleMapper.updateByPrimaryKeySelective(roleForUpdate);
		redisSessionDAO.clearRelativeSession(null, roleId, null);//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**三、启用一个角色**/
	public AjaxResponse enableSaasRole ( Integer roleId ) {
		//角色不存在
		SaasRole role = saasRoleMapper.selectByPrimaryKey(roleId);
		if( role==null ) {
			return AjaxResponse.fail(RestErrorCode.ROLE_NOT_EXIST );
		}
		//执行
		SaasRole roleForUpdate = new SaasRole();
		roleForUpdate.setRoleId(roleId);
		roleForUpdate.setValid(true);
		saasRoleMapper.updateByPrimaryKeySelective(roleForUpdate);
		redisSessionDAO.clearRelativeSession(null, roleId, null);//自动清理用户会话
		return AjaxResponse.success( null );
	}

	/**四、修改一个角色**/
	public 	AjaxResponse changeRole( SaasRole newrole ) {
		//角色不存在
		SaasRole rawrole = saasRoleMapper.selectByPrimaryKey(newrole.getRoleId());
		if( rawrole==null ) {
			return AjaxResponse.fail(RestErrorCode.ROLE_NOT_EXIST );
		}
		//系统预置角色，不能被禁用
		if( SaasConst.SYSTEM_ROLE.equalsIgnoreCase(rawrole.getRoleCode()) ) {
			return AjaxResponse.fail(RestErrorCode.SYSTEM_ROLE_CANOT_CHANGE , rawrole.getRoleCode() );
		}
		//角色代码已经存在   (如果发生变化时 )
		if( newrole.getRoleCode()!=null && newrole.getRoleCode().length()>0 && !newrole.getRoleCode().equalsIgnoreCase(rawrole.getRoleCode())   ) {
			List<SaasRole> roles = saasRoleExMapper.queryRoles(null, newrole.getRoleCode(), null, null);
			if(roles!=null && roles.size()>0) {
				return AjaxResponse.fail(RestErrorCode.ROLE_CODE_EXIST );
			}
		}
		//执行
		saasRoleMapper.updateByPrimaryKeySelective(newrole);
		redisSessionDAO.clearRelativeSession(null, newrole.getRoleId(), null);//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**五、查询一个角色中的权限（返回的数据格式：列表、树形）**/
	public List<SaasPermissionDTO> getAllPermissions( Integer roleId,  String dataFormat ){
		//查询一个角色的所有权限ID（作为是否选中的依据）
		List<Integer> checkedPermissionIds = saasRolePermissionRalationExMapper.queryPermissionIdsOfRole(roleId);
		if(checkedPermissionIds==null) {
			checkedPermissionIds = new ArrayList<Integer>();
		}
		
		if(  SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(dataFormat) ) {
			return this.getAllPermissions_list( checkedPermissionIds  );
		}else if( SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(dataFormat) ) {
			return this.getAllPermissions_tree( checkedPermissionIds );
		}
		return null;
	}
	/**返回的数据格式：列表**/
	private List<SaasPermissionDTO> getAllPermissions_list( List<Integer> checkedPermissionIds ){
		List<SaasPermission> allPos = saasPermissionExMapper.queryPermissions(null, null, null, null, null, null);
		List<SaasPermissionDTO> allDtos = BeanUtil.copyList(allPos, SaasPermissionDTO.class);
		for(SaasPermissionDTO dto : allDtos) {
			if( checkedPermissionIds.contains(dto.getPermissionId()) ) {
				dto.setChecked(true);//置为选中
			}
			if(SaasConst.SYSTEM_PERMISSIONS.contains(dto.getPermissionCode())) {
				dto.setPresetPerm(true);//设置是否为系统预置权限
			}
		}
		return allDtos;
	}
	/**返回的数据格式：树形**/
	private List<SaasPermissionDTO> getAllPermissions_tree( List<Integer> checkedPermissionIds ){
		return this.getChildren(checkedPermissionIds,  0 );
	}
	private List<SaasPermissionDTO> getChildren( List<Integer> checkedPermissionIds, Integer parentPermissionId ){
		List<SaasPermission> childrenPos = saasPermissionExMapper.queryPermissions(null, parentPermissionId, null, null, null, null);
		if(childrenPos==null || childrenPos.size()==0) {
			return null;
		}
		//递归
		List<SaasPermissionDTO> childrenDtos = BeanUtil.copyList(childrenPos, SaasPermissionDTO.class);
		for( SaasPermissionDTO childrenDto : childrenDtos ) {
			if( checkedPermissionIds.contains(childrenDto.getPermissionId()) ) {
				childrenDto.setChecked(true);//置为选中
			}
			if(SaasConst.SYSTEM_PERMISSIONS.contains(childrenDto.getPermissionCode())) {
				childrenDto.setPresetPerm(true);//设置是否为系统预置权限
			}
			
			List<SaasPermissionDTO> childs = this.getChildren( checkedPermissionIds, childrenDto.getPermissionId() );
 			childrenDto.setChildPermissions(childs);
		}
		return childrenDtos;
	}
	
	/**六、查询一个角色中的权限ID**/
	public List<Integer> getAllPermissionIds( Integer roleId){
		List<Integer> checkedPermissionIds = saasRolePermissionRalationExMapper.queryPermissionIdsOfRole(roleId);
		return checkedPermissionIds;
	}
	
	/**七、保存一个角色中的权限ID**/
	public AjaxResponse savePermissionIds( Integer roleId, List<Integer> permissionIds) {
		//角色不存在
		SaasRole rawrole = saasRoleMapper.selectByPrimaryKey( roleId );
		if( rawrole==null ) {
			return AjaxResponse.fail(RestErrorCode.ROLE_NOT_EXIST );
		}
		//系统预置角色，不能被禁用
		if( SaasConst.SYSTEM_ROLE.equalsIgnoreCase(rawrole.getRoleCode()) ) {
			return AjaxResponse.fail(RestErrorCode.SYSTEM_ROLE_CANOT_CHANGE , rawrole.getRoleCode() );
		}
		
		//先删除
		saasRolePermissionRalationExMapper.deletePermissionIdsOfRole(roleId);
		//再插入
		if(permissionIds!=null && permissionIds.size()>0) {
			List<SaasRolePermissionRalation> records = new ArrayList<SaasRolePermissionRalation>(  permissionIds.size() );
			for(Integer permissionId : permissionIds ) {
				SaasRolePermissionRalation ralation = new SaasRolePermissionRalation();
				ralation.setRoleId(roleId);
				ralation.setPermissionId(permissionId);
				records.add(ralation);
			}
			saasRolePermissionRalationExMapper.insertBatch(records);
		}
		redisSessionDAO.clearRelativeSession(null, roleId , null);//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**八、查询角色列表**/
	@SuppressWarnings("rawtypes")
	public PageDTO queryRoleList( Integer page, Integer pageSize,  String roleCode , String roleName, Byte valid ) {
    	//一、参数修正
		if(page==null || page.intValue()<=0) {
			page = new Integer(1);
		}
		if(pageSize==null || pageSize.intValue()<=0) {
			pageSize = new Integer(20);
		}
		if( StringUtils.isEmpty(roleCode) ) {
			roleCode = null;
		}
		if( StringUtils.isEmpty(roleName) ) {
			roleName = null;
		}
		if( StringUtils.isNotEmpty(roleName) ) {
			roleName = roleName.replace("/", "//").replace("%", "/%").replace("_", "/_");
			roleName = "%"+roleName+"%";
		}
		if( valid!=null && valid.byteValue()!=0 && valid.byteValue()!=1 ) {
			valid = null;
		}
    	//二、开始查询DB
    	int total = 0;
    	List<SaasRole> roles = null;
    	Page p = PageHelper.startPage( page, pageSize, true );
    	try{
    		roles = saasRoleExMapper.queryRoles(null, roleCode, roleName, valid);
        	total    = (int)p.getTotal();
    	}finally {
        	PageHelper.clearPage();
    	}
    	//三、判断返回结果
    	if(roles==null|| roles.size()==0) {
        	PageDTO pageDto = new PageDTO( page, pageSize, total , new ArrayList()  );
        	return pageDto;
    	}
    	List<SaasRoleDTO> roledtos = BeanUtil.copyList(roles, SaasRoleDTO.class);
    	PageDTO pageDto = new PageDTO( page, pageSize, total , roledtos);
    	return pageDto;
	}
	
	/**九、删除一个角色**/
	public AjaxResponse deleteSaasRole ( Integer roleId ) {
		//角色不存在
		SaasRole role = saasRoleMapper.selectByPrimaryKey(roleId);
		if( role==null ) {
			return AjaxResponse.fail(RestErrorCode.ROLE_NOT_EXIST );
		}
		//系统预置角色，不能被禁用
		if( SaasConst.SYSTEM_ROLE.equalsIgnoreCase(role.getRoleCode()) ) {
			return AjaxResponse.fail(RestErrorCode.SYSTEM_ROLE_CANOT_CHANGE , role.getRoleCode() );
		}
		//执行
		redisSessionDAO.clearRelativeSession(null, roleId, null); //自动清理用户会话
		try {Thread.sleep(3000);} catch (InterruptedException e) {	}//目的是等待一会儿，因会话清理也要查表的
		saasRolePermissionRalationExMapper.deletePermissionIdsOfRole(roleId);
		saasRoleMapper.deleteByPrimaryKey(roleId);
		return AjaxResponse.success( null );
	}

	public List<String> getAllRoleName(Integer userId) {
		return saasRolePermissionRalationExMapper.queryRoleNameList(userId);
	}
}