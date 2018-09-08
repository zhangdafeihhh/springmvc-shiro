package mapper.mdbcarmanage.ex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.CarDriverTeamDTO;
import com.zhuanche.dto.driver.DriverTeamGroupDTO;
import com.zhuanche.request.DriverTeamRequest;
import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.mdbcarmanage.CarDriverTeam;

public interface CarDriverTeamExMapper{

	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	/**查询车队列表(根据城市ID，供应商ID、车队ID  )**/
	List<CarDriverTeam> queryDriverTeam(@Param("cityIds") Set<String> cityIds, @Param("supplierIds") Set<String> supplierIds ,  @Param("teamIds") Set<Integer>  teamIds );

	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	List<CarDriverTeamDTO> queryDriverTeamAndGroup(@Param("cityIds") Set<String> cityIds, @Param("supplierIds") Set<String> supplierIds , @Param("teamIds") Set<Integer>  teamIds );

	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	CarDriverTeam selectByCondition(DriverTeamRequest driverTeamRequest);

	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	List<CarDriverTeam> queryForListByPid(DriverTeamRequest driverTeamRequest);

	@SuppressWarnings("rawtypes")
	@MasterSlaveConfigs(configs={
			@MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
	} )
	public Map<String, Object> queryTeamIdByDriverId(@Param("driverId") Integer driverId);

	/** 车队设置中给车队排班制*/
	int updateTeamDuty(CarDriverTeam record);
}