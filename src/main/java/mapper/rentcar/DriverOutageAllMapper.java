package mapper.rentcar;

import com.zhuanche.entity.rentcar.DriverOutageAll;

public interface DriverOutageAllMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DriverOutageAll record);

    int insertSelective(DriverOutageAll record);

    DriverOutageAll selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DriverOutageAll record);

    int updateByPrimaryKey(DriverOutageAll record);
}