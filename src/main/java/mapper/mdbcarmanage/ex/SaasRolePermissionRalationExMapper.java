package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.SaasRolePermissionRalation;

public interface SaasRolePermissionRalationExMapper {
	/**查询一个角色的所有权限ID**/
	List<Integer> queryPermissionIdsOfRole( @Param("roleId")  Integer roleId );
	/**删除一个角色的所有权限ID**/
	int deletePermissionIdsOfRole( @Param("roleId")  Integer roleId );
	/**删除一个权限的所有角色ID**/
	int deleteRoleIdsOfPermission( @Param("permissionId")  Integer permissionId );
	/**保存一个角色的所有权限ID(批量插入)**/
	int insertBatch( @Param("records") List<SaasRolePermissionRalation> records );
	
	/**查询一个权限的所有角色ID**/
	List<Integer> queryRoleIdsOfPermission( @Param("permissionId")  Integer permissionId );

    List<String> queryRoleNameList(@Param("userId") Integer userId);
    /**查询某用户的所有角色ID */
    List<String> queryRoleCodeList(@Param("userId") Integer userId);
}