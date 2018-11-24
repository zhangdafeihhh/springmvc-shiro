package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverTelescopeUser;

public interface DriverTelescopeUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(DriverTelescopeUser record);

    int insertSelective(DriverTelescopeUser record);

    DriverTelescopeUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(DriverTelescopeUser record);

    int updateByPrimaryKey(DriverTelescopeUser record);
}