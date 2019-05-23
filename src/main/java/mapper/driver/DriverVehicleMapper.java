package mapper.driver;

import com.zhuanche.entity.driver.DriverVehicle;

public interface DriverVehicleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverVehicle record);

    int insertSelective(DriverVehicle record);

    DriverVehicle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DriverVehicle record);

    int updateByPrimaryKey(DriverVehicle record);
}