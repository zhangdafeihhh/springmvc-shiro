package mapper.mdbcarmanage.ex;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.CarAdmUser;

public interface CarAdmUserExMapper {
	/**根据账户查询**/
    CarAdmUser queryByAccount(@Param("account") String account);
}