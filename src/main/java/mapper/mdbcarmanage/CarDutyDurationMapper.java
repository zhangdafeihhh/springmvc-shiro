package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDutyDuration;

public interface CarDutyDurationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDutyDuration record);

    int insertSelective(CarDutyDuration record);

    CarDutyDuration selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDutyDuration record);

    int updateByPrimaryKey(CarDutyDuration record);
}