package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.CarAdmUser;

public interface CarAdmUserExMapper {
	/**根据账户查询**/
    CarAdmUser queryByAccount(@Param("account") String account);
    /**查询用户列表**/
    List<CarAdmUser> queryUsers(  @Param("userIds") List<Integer> userIds,  @Param("account") String account, @Param("userName") String userName, @Param("phone") String phone , @Param("status") Integer status );
    
    /**查询所有的登录账号名称**/
    List<String> queryAccountsOfUsers( @Param("userIds") List<Integer> userIds );
}