package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDriverMonthDuty;

public interface CarDriverMonthDutyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDriverMonthDuty record);

    int insertSelective(CarDriverMonthDuty record);

    CarDriverMonthDuty selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDriverMonthDuty record);

    int updateByPrimaryKey(CarDriverMonthDuty record);
}