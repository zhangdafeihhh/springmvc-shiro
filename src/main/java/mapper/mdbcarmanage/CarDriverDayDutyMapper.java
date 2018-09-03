package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDriverDayDuty;

public interface CarDriverDayDutyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDriverDayDuty record);

    int insertSelective(CarDriverDayDuty record);

    CarDriverDayDuty selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDriverDayDuty record);

    int updateByPrimaryKey(CarDriverDayDuty record);
}