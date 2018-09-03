package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Set;

import com.zhuanche.request.DriverTeamRequest;
import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;

public interface CarDriverTeamExMapper{
	/**查询车队列表(根据城市ID，供应商ID、车队ID  )**/
	List<CarDriverTeam> queryDriverTeam(@Param("cityIds") Set<String> cityIds, @Param("supplierIds") Set<String> supplierIds ,  @Param("teamIds") Set<Integer>  teamIds );

	CarDriverTeam selectByCondition(DriverTeamRequest driverTeamRequest);

	List<CarDriverTeam> queryForListByPid(DriverTeamRequest driverTeamRequest);
}