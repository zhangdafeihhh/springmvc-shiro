package mapper.mdblog;

import com.zhuanche.entity.mdblog.StatisticDutyHalf;

public interface StatisticDutyHalfMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StatisticDutyHalf record);

    int insertSelective(StatisticDutyHalf record);

    StatisticDutyHalf selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StatisticDutyHalf record);

    int updateByPrimaryKey(StatisticDutyHalf record);
}