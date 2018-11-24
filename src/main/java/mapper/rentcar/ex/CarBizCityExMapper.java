package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CarBizCityExMapper{
	/**根据城市ID，查询城市信息**/
    List<CarBizCity> queryByIds( @Param("cityIds")  Set<Integer> cityIds );

    /** 根据城市名称查询城市id **/
    Integer queryCityByCityName(@Param("cityName") String cityName);
    
    CarBizCity queryCarBizCityById(CarBizCity params);

    /**
     * 根据城市ID，查询城市名称**/
    List<CarBizCity> queryNameByIds( @Param("cityIds")  Set<Integer> cityIds );

    List<CarBizCity> queryNameByCityIds(@Param("cityIds")  String cityIds );
	
	List<String> getCityList(@Param("cityIds") Set<String> cityIds);

}