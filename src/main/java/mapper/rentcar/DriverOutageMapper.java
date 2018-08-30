package mapper.rentcar;

import com.zhuanche.entity.rentcar.DriverOutage;


public interface DriverOutageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverOutage record);

    int insertSelective(DriverOutage record);

    DriverOutage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverOutage record);

    int updateByPrimaryKey(DriverOutage record);
}