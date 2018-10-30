package mapper.rentcar;

import com.zhuanche.dto.driver.DriverVoEntity;
import com.zhuanche.entity.rentcar.CarBizDriverInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizDriverInfoMapper {
    int deleteByPrimaryKey(Integer driverId);

    int insert(CarBizDriverInfo record);

    int insertSelective(CarBizDriverInfo record);

    CarBizDriverInfo selectByPrimaryKey(Integer driverId);

    int updateByPrimaryKeySelective(CarBizDriverInfo record);

    int updateByPrimaryKeyWithBLOBs(CarBizDriverInfo record);

    int updateByPrimaryKey(CarBizDriverInfo record);

    CarBizDriverInfo selectByPhone(@Param("phone") String phone);

    int selectDriverByKeyCountAddCooperation(DriverVoEntity params);

    public List<DriverVoEntity> selectDriverByKeyAddCooperation(DriverVoEntity params);
}