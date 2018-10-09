package mapper.mdblog.ex;

import com.zhuanche.entity.mdblog.StatisticDutyHalf;
import com.zhuanche.entity.mdblog.StatisticDutyHalfParams;

import java.util.List;

public interface StatisticDutyHalfExMapper {

    /**
     * 查询
     * @param params
     * @return
     */
    public List<StatisticDutyHalf> queryDriverDutyHalfByDriverId(StatisticDutyHalfParams params);

}