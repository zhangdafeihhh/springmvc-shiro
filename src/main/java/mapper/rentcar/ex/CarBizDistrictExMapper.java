package mapper.rentcar.ex;

import java.util.List;

import com.zhuanche.entity.rentcar.CarBizDistrict;

public interface CarBizDistrictExMapper {
	/**查询一个城市的默认商圈**/
    List<CarBizDistrict> queryCarBizDistrict(Integer cityId);
}