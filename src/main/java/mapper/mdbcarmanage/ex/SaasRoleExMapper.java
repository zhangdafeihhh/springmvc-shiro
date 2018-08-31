package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.SaasRole;

public interface SaasRoleExMapper{
	/**查询角色列表**/
	List<SaasRole> queryRoles( @Param("roleIds") List<Integer> roleIds, @Param("roleCode")  String  roleCode , @Param("roleName") String roleName, @Param("valid") Byte valid );
}