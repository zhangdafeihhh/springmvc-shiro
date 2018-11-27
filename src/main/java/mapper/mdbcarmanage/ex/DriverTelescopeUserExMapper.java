package mapper.mdbcarmanage.ex;

import com.zhuanche.entity.mdbcarmanage.DriverTelescopeUser;

public interface DriverTelescopeUserExMapper {

    DriverTelescopeUser selectTelescopeUserByUserId(Integer userId);

    int disableDriverTelescopeUser(Integer userId);

    int enableDriverTelescopeUser(Integer userId);
}