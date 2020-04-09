package mapper.driver;

import com.zhuanche.entity.driver.DriverPunish;

public interface DriverPunishMapper {
    int deleteByPrimaryKey(Integer punishId);

    int insert(DriverPunish record);

    int insertSelective(DriverPunish record);

    DriverPunish selectByPrimaryKey(Integer punishId);

    int updateByPrimaryKeySelective(DriverPunish record);

    int updateByPrimaryKey(DriverPunish record);
}