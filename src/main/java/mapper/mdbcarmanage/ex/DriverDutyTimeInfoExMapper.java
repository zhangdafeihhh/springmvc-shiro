package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.DriverDutyTimeInfo;

import java.util.List;
import java.util.Map;

public interface DriverDutyTimeInfoExMapper {

//    List<DriverDutyTimeInfo> queryList(DriverDutyTimeInfo param);

    DriverDutyTimeInfo selectOne(DriverDutyTimeInfo param);

    Integer insertDriverDutyTimeInfoList(Map<String, Object> params);

    Integer updateDriverDutyTimeInfoList(Map<String, Object> params);

}