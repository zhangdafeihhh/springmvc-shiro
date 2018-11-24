package mapper.mdbcarmanage.ex;

import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.DriverTeamRequest;
import com.zhuanche.request.TeamGroupRequest;
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

    List<CarRelateTeam> queryDriverTeamListByDriverIdList(List<String> driverIds);

	/**
	 * 查询权限范围内，状态不为2的小组
	 * @param driverTeamRequest
	 * @return
	 */
	List<CarDriverTeam> queryForListByStatusNotEq2(DriverTeamRequest driverTeamRequest);


	/**
	 * 根据id查找对象
	 * @param teamIdList
	 * @return
	 */
	List<CarDriverTeam> queryTeamListByTemIdList(List<Integer> teamIdList);
	
	List<CarDriverTeam> queryTeamNameByTemIds(@Param("teamIds") String teamIds);

	List<Map<String, Object>> queryForListByPids(TeamGroupRequest teamGroupRequest);

    List<Map<String, Object>> getTeamList(Set<String> teamIds);

    List<Map<String, Object>> getGroupList(Set<String> groupIds);
}