package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.dto.driver.CarDriverDayDutyDTO;
import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;
import com.zhuanche.request.DutyParamRequest;

import java.util.List;
import java.util.Map;

public interface CarDriverDayDutyExMapper {

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    List<CarDriverDayDutyDTO> selectForList(DutyParamRequest dutyParamRequest);

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    List<CarDriverDayDutyDTO> queryForList(DutyParamRequest dutyParamRequest);

    @SuppressWarnings("rawtypes")
    @MasterSlaveConfigs(configs={
            @MasterSlaveConfig(databaseTag="mdbcarmanage-DataSource",mode= DynamicRoutingDataSource.DataSourceMode.SLAVE )
    } )
    Integer getUnIssueCount(DutyParamRequest dutyParamRequest);


    Integer updateDriverDayDutyList(Map<String, Object> params);

    /** 保存司机排班信息--LN*/
    Integer insertDriverDayDutyList(Map<String, Object> params);

}