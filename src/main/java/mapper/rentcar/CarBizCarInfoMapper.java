package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarBizCarInfo;

public interface CarBizCarInfoMapper {
    int deleteByPrimaryKey(Integer carId);

    int insert(CarBizCarInfo record);

    int insertSelective(CarBizCarInfo record);

    CarBizCarInfo selectByPrimaryKey(Integer carId);

    int updateByPrimaryKeySelective(CarBizCarInfo record);

    int updateByPrimaryKeyWithBLOBs(CarBizCarInfo record);

    int updateByPrimaryKey(CarBizCarInfo record);
}