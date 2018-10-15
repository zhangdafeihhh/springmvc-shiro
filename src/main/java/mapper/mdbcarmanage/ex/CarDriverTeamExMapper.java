package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.DriverTeamRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CarDriverTeamExMapper{

	/**查询车队列表(根据城市ID，供应商ID、车队ID  )**/
	List<CarDriverTeam> queryDriverTeam(@Param("cityIds") Set<String> cityIds, @Param("supplierIds") Set<String> supplierIds ,  @Param("teamIds") Set<Integer>  teamIds );

	List<CarDriverTeamDTO> queryDriverTeamAndGroup(@Param("cityIds") Set<String> cityIds, @Param("supplierIds") Set<String> supplierIds , @Param("teamIds") Set<Integer>  teamIds );

	CarDriverTeam selectByCondition(DriverTeamRequest driverTeamRequest);

	List<CarDriverTeam> queryForListByPid(DriverTeamRequest driverTeamRequest);

	public Map<String, Object> queryTeamIdByDriverId(@Param("driverId") Integer driverId);

	/** 车队设置中给车队排班制*/
	int updateTeamDuty(CarDriverTeam record);

	/**
	 * 根据司机ID查询司机的车队小组信息
	 * @param driverId
	 * @return
	 */
	Map<String, Object> queryTeamNameAndGroupNameByDriverId(@Param("driverId") Integer driverId);

	List<CarDriverTeam> queryDriverTeamList(@Param("cityId") Integer cityId, @Param("supplierId") Integer supplierId);

	List<CarRelateTeam> queryDriverTeamListByDriverId(@Param("driverIds") String driverIds);
}