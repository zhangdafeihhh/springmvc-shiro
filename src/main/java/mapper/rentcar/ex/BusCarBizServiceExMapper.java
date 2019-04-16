package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BusCarBizServiceExMapper {

	/**
	 * @Title: queryServices
	 * @Description: 查询巴士服务类型
	 * @return 
	 * @return List<Map<Object,Object>>
	 * @throws
	 */
	List<Map<Object, Object>> queryServices();

	List<CarBizService> queryServiceTypeByIdSet(@Param("serviceIds") Set<Integer> serviceIds);

}