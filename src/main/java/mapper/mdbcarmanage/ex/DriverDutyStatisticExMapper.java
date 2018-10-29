package mapper.mdbcarmanage.ex;

import com.zhuanche.common.database.DynamicRoutingDataSource;
import com.zhuanche.common.database.MasterSlaveConfig;
import com.zhuanche.common.database.MasterSlaveConfigs;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatistic;
import com.zhuanche.entity.mdbcarmanage.DriverDutyStatisticParams;

import java.util.List;

public interface DriverDutyStatisticExMapper {

    /**
     * 查询
     * @param params
     * @return
     */
    List<DriverDutyStatistic> queryForListObject(DriverDutyStatisticParams params);

    /**
     * 按月度统计
     * @param params
     * @return
     */
    List<DriverDutyStatistic> queryDriverMonthDutyList(DriverDutyStatisticParams params);

    /**
     * 司机详情查询
     * @param params
     * @return
     */
    List<DriverDutyStatistic> queryDriverDutyHalfByDriverId(DriverDutyStatisticParams params);
}