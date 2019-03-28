package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BusCarBizCarGroupExMapper {
	/**
	 * @Title: queryGroups
	 * @Description: 查询巴士车型类别
	 * @return 
	 * @return List<Map<Object,Object>>
	 * @throws
	 */
	List<Map<Object, Object>> queryGroups();

	List<Map<Object,Object>> queryGroupByCityIds(@Param("cityIds")Set<Integer> cityIds);

	List<CarBizCarGroup> queryGroupByIds(@Param("groupIds")Set<Integer> groupIds);
}