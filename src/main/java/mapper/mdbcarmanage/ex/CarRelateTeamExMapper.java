package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.CarRelateTeam;
import com.zhuanche.request.TeamGroupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CarRelateTeamExMapper {

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    List<CarRelateTeam> queryDriverTeamRelationList(TeamGroupRequest teamGroupRequest);

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    List<Map<String,Object>> queryDriverIdsByTeamIds(@Param("teamIds") Set<Integer> teamIds);

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    CarRelateTeam selectOneTeam(CarRelateTeam carRelateTeam);

}