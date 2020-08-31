package mapper.mdbcarmanage;

import com.zhuanche.entity.mdbcarmanage.CarBizCarInfoTemp;
import org.apache.ibatis.annotations.Param;

public interface CarBizCarInfoTempMapper {
    int deleteByPrimaryKey(Integer carId);

    int insert(CarBizCarInfoTemp record);

    int insertSelective(CarBizCarInfoTemp record);

    CarBizCarInfoTemp selectByPrimaryKey(Integer carId);

    int updateByPrimaryKeySelective(CarBizCarInfoTemp record);

    int updateImageInfo(@Param("carId") Integer carId, @Param("type")  String type, @Param("value")  String value);
}