package mapper.mdbcarmanage.ex;

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

}