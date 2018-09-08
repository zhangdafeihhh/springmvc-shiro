package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.CarRelateGroup;
import com.zhuanche.request.TeamGroupRequest;

import java.util.List;

public interface CarRelateGroupExMapper {

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    List<CarRelateGroup> queryDriverGroupRelationList(TeamGroupRequest teamGroupRequest);

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    CarRelateGroup selectOneGroup(CarRelateGroup group);

}