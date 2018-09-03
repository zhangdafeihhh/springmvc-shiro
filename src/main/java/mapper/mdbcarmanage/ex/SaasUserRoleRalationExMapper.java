package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.SaasUserRoleRalation;

public interface SaasUserRoleRalationExMapper {
	/**查询一个用户的所有角色ID**/
	List<Integer> queryRoleIdsOfUser( @Param("userId")  Integer userId );
	/**删除一个用户的所有角色ID**/
	int deleteRoleIdsOfUser( @Param("userId")  Integer userId );
	/**保存一个用户的所有角色ID(批量插入)**/
	int insertBatch( @Param("records") List<SaasUserRoleRalation> records );
	
	/**查询多个角色所对应的用户ID**/
	
}