package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;

public interface CarBizCarInfoTempMapper {
    int deleteByPrimaryKey(Integer carId);

    int insert(CarBizCarInfoTemp record);

    int insertSelective(CarBizCarInfoTemp record);

    CarBizCarInfoTemp selectByPrimaryKey(Integer carId);

    int updateByPrimaryKeySelective(CarBizCarInfoTemp record);
}