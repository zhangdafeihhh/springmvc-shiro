package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCity;

public interface CarBizCityMapper {
    int deleteByPrimaryKey(Integer cityId);

    int insert(CarBizCity record);

    int insertSelective(CarBizCity record);

    CarBizCity selectByPrimaryKey(Integer cityId);

    int updateByPrimaryKeySelective(CarBizCity record);

    int updateByPrimaryKeyWithBLOBs(CarBizCity record);

    int updateByPrimaryKey(CarBizCity record);
}