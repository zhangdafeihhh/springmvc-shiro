package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.DriverInfoInterCity;

public interface DriverInfoInterCityMapper {
    int insert(DriverInfoInterCity record);

    int insertSelective(DriverInfoInterCity record);

    DriverInfoInterCity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverInfoInterCity record);

    int updateByPrimaryKey(DriverInfoInterCity record);
}