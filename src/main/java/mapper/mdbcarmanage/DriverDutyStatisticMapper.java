package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverDutyStatistic;

public interface DriverDutyStatisticMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverDutyStatistic record);

    int insertSelective(DriverDutyStatistic record);

    DriverDutyStatistic selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverDutyStatistic record);

    int updateByPrimaryKey(DriverDutyStatistic record);
}