package mapper.rentcar.ex;

import com.zhuanche.entity.rentcar.CarBizCarGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarBizCarGroupExMapper {

    CarBizCarGroup queryGroupByGroupName (@Param("groupName") String groupName);
    /**
     * 根据groupId查询
     * @param carBizCarGroup
     * @return
     */
    CarBizCarGroup queryForObject(CarBizCarGroup carBizCarGroup);

    CarBizCarGroup queryForObjectByGroupName(CarBizCarGroup carBizCarGroup);

    /**
     * 查询列表
     * @param carBizCarGroup
     * @return
     */
    List<CarBizCarGroup> queryForListObject(CarBizCarGroup carBizCarGroup);
}