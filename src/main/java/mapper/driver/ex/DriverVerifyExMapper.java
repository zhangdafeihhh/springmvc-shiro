package mapper.driver.ex;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.driver.DriverVerify;

import mapper.driver.DriverVerifyMapper;

public interface DriverVerifyExMapper extends DriverVerifyMapper {

	/** 查询司机加盟注册信息 **/
	List<DriverVerify> queryDriverVerifyList(@Param("cityId") Long cityId, @Param("supplier") String supplier,
			@Param("mobile") String mobile, @Param("verifyStatus") Integer verifyStatus,
			@Param("createDateBegin") String createDateBegin, @Param("createDateEnd") String createDateEnd);

}
