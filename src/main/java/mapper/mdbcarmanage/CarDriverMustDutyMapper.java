package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarDriverMustDuty;

public interface CarDriverMustDutyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CarDriverMustDuty record);

    int insertSelective(CarDriverMustDuty record);

    CarDriverMustDuty selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CarDriverMustDuty record);

    int updateByPrimaryKey(CarDriverMustDuty record);
}