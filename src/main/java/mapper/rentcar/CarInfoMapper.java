package mapper.rentcar;

import com.zhuanche.entity.rentcar.CarInfo;

public interface CarInfoMapper {
    int deleteByPrimaryKey(Integer carId);

    int insert(CarInfo record);

    int insertSelective(CarInfo record);

    CarInfo selectByPrimaryKey(Integer carId);

    int updateByPrimaryKeySelective(CarInfo record);

    int updateByPrimaryKeyWithBLOBs(CarInfo record);

    int updateByPrimaryKey(CarInfo record);
}