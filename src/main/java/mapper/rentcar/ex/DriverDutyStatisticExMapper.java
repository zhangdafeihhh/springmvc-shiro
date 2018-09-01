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

}