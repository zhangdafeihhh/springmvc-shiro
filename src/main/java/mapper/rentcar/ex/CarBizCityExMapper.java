package mapper.rentcar.ex;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zhuanche.entity.rentcar.CarBizCity;

public interface CarBizCityExMapper{
	/**根据城市ID，查询城市信息**/
    List<CarBizCity> queryByIds( @Param("cityIds")  Set<Integer> cityIds );

    /** 根据城市名称查询城市id **/
    Integer queryCityByCityName(@Param("cityName") String cityName);
}