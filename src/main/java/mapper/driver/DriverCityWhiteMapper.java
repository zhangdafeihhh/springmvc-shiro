package mapper.driver;

import com.zhuanche.entity.driver.DriverCityWhite;

public interface DriverCityWhiteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverCityWhite record);

    int insertSelective(DriverCityWhite record);

    DriverCityWhite selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverCityWhite record);

    int updateByPrimaryKey(DriverCityWhite record);
}