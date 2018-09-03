package mapper.driver;

import com.zhuanche.entity.driver.DriverVerify;

public interface DriverVerifyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DriverVerify record);

    int insertSelective(DriverVerify record);

    DriverVerify selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DriverVerify record);

    int updateByPrimaryKey(DriverVerify record);
}