package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.DriverDutyStatistic;
import com.zhuanche.entity.rentcar.DriverDutyStatisticParams;

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