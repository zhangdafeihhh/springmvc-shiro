package mapper.driverOrderRecord;

import com.zhuanche.entity.driver.DriverActionDto;

public interface DriverActionDtoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverActionDto record);

    int insertSelective(DriverActionDto record);

    DriverActionDto selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverActionDto record);

    int updateByPrimaryKey(DriverActionDto record);
}