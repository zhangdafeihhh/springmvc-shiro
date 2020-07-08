package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.SaasRole;

public interface SaasRoleExMapper{
	/**查询角色列表**/
	List<SaasRole> queryRoles( @Param("roleIds") List<Integer> roleIds, @Param("roleCode")  String  roleCode , @Param("roleName") String roleName, @Param("valid") Byte valid );
	
	/**根据用户ID，查询其拥有的所有有效的角色ID**/
	List<Integer> queryRoleIdsOfUser( @Param("userId") Integer userId );
	/**根据用户ID，查询其拥有的所有有效的角色代码**/
	List<String> queryRoleCodesOfUser( @Param("userId") Integer userId );
	/**查询供应商可见的角色列表**/
	List<SaasRole> queryVisiableRoles();
	/**更新供应商可见的角色**/
	int updateIsVisable(@Param("roleId")  Integer roleId,
						@Param("isVisiable")  Integer isVisiable );
	
}