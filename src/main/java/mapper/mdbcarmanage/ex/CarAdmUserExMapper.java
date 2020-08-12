package mapper.mdbcarmanage.ex;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.CarAdmUser;

public interface CarAdmUserExMapper {
	/**根据账户查询**/
    CarAdmUser queryByAccount(@Param("account") String account);
    /**查询用户列表**/
    List<CarAdmUser> queryUsers(  @Param("userIds") List<Integer> userIds,  @Param("account") String account, @Param("userName") String userName, @Param("phone") String phone , @Param("status") Integer status );

    /**供应商查询用户列表**/
    List<CarAdmUser> supplierQueryUsers(  @Param("userIds") List<Integer> userIds,  @Param("account") String account,
                                          @Param("userName") String userName, @Param("phone") String phone ,
                                          @Param("status") Integer status,@Param("createUserId")Integer createUserId );

    /**查询所有的登录账号名称**/
    List<String> queryAccountsOfUsers( @Param("userIds") List<Integer> userIds );


    List<CarAdmUser> selectUsersByIdList(List<Integer> ids);

    /**根据等级范围查询**/
    List<CarAdmUser> selectUsersByLevel(@Param("level")Integer level);

    CarAdmUser queryUserPermissionInfo(@Param("userId")Integer userId);

    List<Integer> queryIdListByName(@Param("userName") String createUser);

    String queryNameById(@Param("userId")Integer userId);

    int updateEmail(@Param("email")String email,@Param("userId")Integer userId);


    CarAdmUser queryByPhone(@Param("phone")String phone);

    List<CarAdmUser> queryAllAccountByPhone(@Param("phone") String phone);

    /**根据创建人查询创建的所有子账号*/
    List<CarAdmUser> queryByCreateUserId(@Param("createUserId") Integer createUserId);

    /**批量更改子账号*/
    int batchUpdate(@Param("userIdList") List<Integer> userIdList);
}