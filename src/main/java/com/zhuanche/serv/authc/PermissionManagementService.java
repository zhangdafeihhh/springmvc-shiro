package com.zhuanche.serv.authc;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhuanche.common.web.AjaxResponse;
import com.zhuanche.common.web.RestErrorCode;
import com.zhuanche.constants.SaasConst;
import com.zhuanche.dto.SaasPermissionDTO;
import com.zhuanche.entity.mdbcarmanage.SaasPermission;
import com.zhuanche.shiro.session.RedisSessionDAO;
import com.zhuanche.util.BeanUtil;

import mapper.mdbcarmanage.SaasPermissionMapper;
import mapper.mdbcarmanage.ex.SaasPermissionExMapper;

/**权限管理功能**/
@Service
public class PermissionManagementService{
	@Autowired
	private SaasPermissionMapper     saasPermissionMapper;
	@Autowired
	private SaasPermissionExMapper  saasPermissionExMapper;
	@Autowired
	private RedisSessionDAO              redisSessionDAO;
	
	/**一、增加一个权限**/
	public AjaxResponse addSaasPermission( SaasPermission pemission ) {
		//父权限不存在
		if( pemission.getParentPermissionId()!=null && pemission.getParentPermissionId().intValue()>0 ) {
			SaasPermission parentPermission = saasPermissionMapper.selectByPrimaryKey( pemission.getParentPermissionId() );
			if(parentPermission==null) {
				return AjaxResponse.fail(RestErrorCode.PARENT_PERMISSION_NOT_EXIST );
			}
		}
		//权限代码已经存在
		List<SaasPermission> pos= saasPermissionExMapper.queryPermissions(null, null, pemission.getPermissionCode(), null, null, null);
		if( pos!=null && pos.size()>0 ) {
			return AjaxResponse.fail(RestErrorCode.PERMISSION_CODE_EXIST );
		}
		//权限类型不合法
		List<Byte> permTypes = Arrays.asList( new Byte[] { SaasConst.PermissionType.MENU,SaasConst.PermissionType.BUTTON,SaasConst.PermissionType.DATA_AREA }  );
		if(!permTypes.contains(pemission.getPermissionType())) {
			return AjaxResponse.fail(RestErrorCode.PERMISSION_TYPE_WRONG );
		}
		
		int sortSeq = saasPermissionExMapper.selectMaxSortSeq(pemission.getParentPermissionId()).intValue()+1;//自动生成排序的序号
		pemission.setValid(true);
		pemission.setSortSeq(sortSeq);
	    saasPermissionMapper.insertSelective(pemission);
		return AjaxResponse.success( null );
	}

	/**二、禁用一个权限**/
	public AjaxResponse disableSaasPermission ( Integer permissionId ) {
		//权限不存在
		SaasPermission thisPermission = saasPermissionMapper.selectByPrimaryKey( permissionId );
		if(thisPermission == null ) {
			return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST );
		}
		//系统预置权限，不能禁用、修改
		if( SaasConst.SYSTEM_PERMISSIONS.contains( thisPermission.getPermissionCode() ) ) {
			return AjaxResponse.fail(RestErrorCode.SYSTEM_PERMISSION_CANOT_CHANGE , thisPermission.getPermissionCode() );
		}
		//存在已经生效的子权限，请先禁用子权限
		List<SaasPermission> validChildren = saasPermissionExMapper.queryPermissions(null, permissionId, null, null, null, (byte)1 );
		if( validChildren!=null && validChildren.size()>0) {
			return AjaxResponse.fail(RestErrorCode.PERMISSION_DISABLE_CANT );
		}
		//执行禁用此权限
		SaasPermission pemissionForUpdate = new SaasPermission();
		pemissionForUpdate.setPermissionId(permissionId);
		pemissionForUpdate.setValid(false);
		saasPermissionMapper.updateByPrimaryKeySelective(pemissionForUpdate);
		redisSessionDAO.clearRelativeSession(permissionId, null, null);//自动清理用户会话
		return AjaxResponse.success( null );
	}
	
	/**三、启用一个权限**/
	public AjaxResponse enableSaasPermission ( Integer permissionId ) {
		//权限不存在
		SaasPermission thisPermission = saasPermissionMapper.selectByPrimaryKey( permissionId );
		if(thisPermission == null ) {
			return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST );
		}
		//查询其父权限，判断父权限状态
		//父权限已经被禁用，请先启用父权限
		if( thisPermission.getParentPermissionId()!=null && thisPermission.getParentPermissionId().intValue()>0 ) {
			SaasPermission parentPermission = saasPermissionMapper.selectByPrimaryKey( thisPermission.getParentPermissionId() );
			if(parentPermission!=null && parentPermission.getValid()==false) {
				return AjaxResponse.fail(RestErrorCode.PERMISSION_ENABLE_CANT );
			}
		}
		//执行启用此权限
		SaasPermission pemissionForUpdate = new SaasPermission();
		pemissionForUpdate.setPermissionId(permissionId);
		pemissionForUpdate.setValid( true );
		saasPermissionMapper.updateByPrimaryKeySelective(pemissionForUpdate);
		redisSessionDAO.clearRelativeSession(permissionId, null, null);//自动清理用户会话
		return AjaxResponse.success( null );
	}

	/**四、修改一个权限**/
	public 	AjaxResponse changeSaasPermission( SaasPermission pemission ) {
		//权限不存在
		SaasPermission thisPermission = saasPermissionMapper.selectByPrimaryKey( pemission.getPermissionId() );
		if(thisPermission == null ) {
			return AjaxResponse.fail(RestErrorCode.PERMISSION_NOT_EXIST );
		}
		//系统预置权限，不能禁用、修改
		if( SaasConst.SYSTEM_PERMISSIONS.contains( thisPermission.getPermissionCode() ) ) {
			return AjaxResponse.fail(RestErrorCode.SYSTEM_PERMISSION_CANOT_CHANGE , thisPermission.getPermissionCode() );
		}
		//权限代码已经存在   (如果发生变化时 )
		if( pemission.getPermissionCode()!=null && pemission.getPermissionCode().length()>0  && !pemission.getPermissionCode().equalsIgnoreCase(thisPermission.getPermissionCode()) ) {
			List<SaasPermission> pos= saasPermissionExMapper.queryPermissions(null, null, pemission.getPermissionCode(), null, null, null);
			if( pos!=null && pos.size()>0 ) {
				return AjaxResponse.fail(RestErrorCode.PERMISSION_CODE_EXIST );
			}
		}
		//权限类型不合法
		if( pemission.getPermissionType()!=null ) {
			List<Byte> permTypes = Arrays.asList( new Byte[] { SaasConst.PermissionType.MENU,SaasConst.PermissionType.BUTTON,SaasConst.PermissionType.DATA_AREA }  );
			if(!permTypes.contains(pemission.getPermissionType())) {
				return AjaxResponse.fail(RestErrorCode.PERMISSION_TYPE_WRONG );
			}
		}
		pemission.setParentPermissionId( null );
	    saasPermissionMapper.updateByPrimaryKeySelective( pemission );
		redisSessionDAO.clearRelativeSession(pemission.getPermissionId(), null, null);//自动清理用户会话
		return AjaxResponse.success( null );
	}
	

	/**五、查询所有的权限信息（返回的数据格式：列表、树形）**/
	public List<SaasPermissionDTO> getAllPermissions( String dataFormat ){
		if(  SaasConst.PermissionDataFormat.LIST.equalsIgnoreCase(dataFormat) ) {
			return this.getAllPermissions_list();
		}else if( SaasConst.PermissionDataFormat.TREE.equalsIgnoreCase(dataFormat) ) {
			return this.getAllPermissions_tree();
		}
		return null;
	}
	/**返回的数据格式：列表**/
	private List<SaasPermissionDTO> getAllPermissions_list(){
		List<SaasPermission> allPos = saasPermissionExMapper.queryPermissions(null, null, null, null, null, null);
		return BeanUtil.copyList(allPos, SaasPermissionDTO.class);
	}
	/**返回的数据格式：树形**/
	private List<SaasPermissionDTO> getAllPermissions_tree(){
		return this.getChildren( 0 );
	}
	private List<SaasPermissionDTO> getChildren( Integer parentPermissionId ){
		List<SaasPermission> childrenPos = saasPermissionExMapper.queryPermissions(null, parentPermissionId, null, null, null, null);
		if(childrenPos==null || childrenPos.size()==0) {
			return null;
		}
		//递归
		List<SaasPermissionDTO> childrenDtos = BeanUtil.copyList(childrenPos, SaasPermissionDTO.class);
		for( SaasPermissionDTO childrenDto : childrenDtos ) {
			List<SaasPermissionDTO> childs = this.getChildren( childrenDto.getPermissionId() );
 			childrenDto.setChildPermissions(childs);
		}
		return childrenDtos;
	}

}